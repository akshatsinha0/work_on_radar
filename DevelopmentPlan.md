# POCM Analytics Service — Development Plan

1. Frontend Analytics Dashboard (what it will show)
1.1 Global filters
- Date range, tenant/provider, region/city, device type, product category/brand, gateway/method, warehouse, batch/lot, channel, cohort.

1.2 Live overview (top cards)
- Gross merchandise value (GMV)
- Orders, items sold, unique purchasers, unique sessions
- Conversion rate (session→view→cart→checkout→payment)
- Average order value (AOV)
- Payment success rate by gateway and method
- Refund/return rate, RTO rate
- Active devices, device adherence, adverse event count

1.3 Funnels and journeys
- Search→Product view→Add-to-cart→Checkout→Payment
- Product-specific funnels for key microfluidic/nanofluidic goods (patches)
- Drop-off at each step and time-to-convert

1.4 Inventory and supply chain panels
- Stock on hand by product and warehouse
- Days of cover, stockout incidents, reorder threshold alerts
- Inventory turnover, aging inventory, shrinkage rate, batch performance/recall flags

1.5 Logistics panels
- On-time delivery rate, average delivery time, first-attempt delivery rate
- Region heatmap of delivery performance and returns

1.6 Payments panels
- Success/decline by gateway/method, latency percentiles, risk score distribution
- Refunds, partial refunds, chargebacks

1.7 Marketing and growth panels
- Recommendation CTR, promotion conversion, affiliate revenue
- Subscription churn and revenue, loyalty activity
- Search analytics: zero-result rate, top queries, click position impact

1.8 Device analytics panels
- Activation trend, patch application adherence, dosage delivery success
- Sensor reading stability, anomalies, firmware mix
- Adverse events by severity and batch/lot linkage

1.9 Cohorts and retention
- Cohort retention curves, DAU/WAU/MAU, LTV model outputs

1.10 Compliance and operations
- Audit access timeline, GDPR export status
- Ingestion error rate, DLQ size, consumer lag

1.11 Real-time stream
- Last N validated events with filters for quick ops triage

1.12 Key calculations (examples)
- Conversion rate = payments_captured / sessions
- Cart abandonment = carts_not_converted / carts_created
- Payment success = captured / (captured + failed)
- AOV = GMV / orders
- ARPU = GMV / unique_purchasers
- On-time delivery = delivered_on_or_before_eta / delivered
- Inventory turnover = COGS / average_inventory
- Days of cover = stock_on_hand / daily_units_sold
- Device adherence = valid_patch_days / prescribed_days
- Adverse event rate = adverse_events / active_devices
- Zero-result rate = zero_result_searches / total_searches

2. System Design and Platform Workflow
2.1 Components
- Producer services: products, inventory, checkout, payment, logistics, notifications, user, device telemetry.
- Messaging: Kafka topics per event type; REST fallback for partners and backfills.
- Analytics service (Spring Boot WebFlux): ingestion, validation, dedupe, storage, streams, query APIs.
- Datastores: PostgreSQL (system-of-record), ClickHouse (OLAP analytics), Redis (cache and rate-limits), S3/MinIO (archive).
- Observability: Prometheus, Grafana; exporters for Postgres, Kafka, and ClickHouse.

2.2 End-to-end flow
- Ingest: Event arrives via HTTP or Kafka. Service validates against schema, checks idempotency, enriches with tenant and trace. Writes to PostgreSQL and produces to Kafka.
- Process: Stream processors aggregate funnels, payment metrics, inventory and device metrics. Bad messages go to a DLQ. Replay tools support backfills.
- Store: PostgreSQL keeps the ledger (JSONB + indexes + time partitions). ClickHouse stores columnar copies and rollups via Kafka sink and materialized views. S3/MinIO stores raw and parquet snapshots.
- Query: Read APIs and Grafana panels query ClickHouse for aggregates; single-event lookups and compliance reads go to PostgreSQL. WebSocket/SSE gives a live filtered feed.

2.3 API surface (from Analytics.yaml)
- Ingestion: POST /analytics/ingest, POST /analytics/ingest/batch
- Query: GET /analytics/events, GET /analytics/events/{id}
- Streaming: GET /analytics/stream
- ML gateway: POST /analytics/ml/predict (optional external model server)
- Compliance: GET /analytics/compliance/audit, POST /analytics/compliance/gdpr/export
- Health: GET /health, GET /ready

2.4 Topic naming (example)
- pocm.analytics.cart.v1, pocm.analytics.checkout.v1, pocm.analytics.payment.v1, ... (one topic per EventType)

2.5 Back-pressure and reliability
- Reactor back-pressure on REST ingest
- Retries with jitter for transient errors
- Circuit breakers for downstreams
- DLQ with replay tooling; idempotency prevents double writes

2.6 Data governance and privacy
- Schema registry and versioning; backward-compatible changes on hot topics
- PII and medical data masking; field-level encryption for sensitive fields
- Tenant isolation and RBAC scopes (analytics:ingest, analytics:read, compliance:export)
- Retention and purge jobs; audit logs for all data access

2.7 Observability
- Prometheus scrapes app /actuator/prometheus
- postgres-exporter, kafka-exporter, clickhouse metrics
- Grafana dashboards for ops and for business KPIs
- Alerts: high 5xx, dedupe spikes, consumer lag, ingest p95>100ms, OLAP sink delay>5m

3. Chosen Stack and Integration
- Spring Boot 3 WebFlux
- PostgreSQL via R2DBC for writes/targeted reads
- Kafka + Reactor Kafka
- ClickHouse via JDBC (read-only analytics queries)
- Redis for short TTL dedupe hints and cache
- Flyway migrations; Micrometer Prometheus; OpenTelemetry tracing
- Docker Compose for local; Kubernetes for higher envs

4. Database Design and Lifecycle
4.1 PostgreSQL (system-of-record)
- Table events(id,type,occurred_at,received_at,producer,source_service,user_id,order_id,session_id,trace_id,idempotency_key,tenant_id,data_json)
- Unique index on idempotency_key (nullable)
- Indexes: occurred_at desc; (type,occurred_at); (user_id,occurred_at); (order_id,occurred_at); JSONB path indexes for hot fields
- Monthly partitions by occurred_at; 60–90 day hot window
- Support tables: batch_operations, compliance_audit_logs, gdpr_export_requests, api_keys, rate_limits, data_retention_policies, ml_models

4.2 ClickHouse (analytics store)
- events_raw partitioned by month, ordered by (type,occurred_at,user_id,order_id)
- Materialized views build kpi_daily, funnel_step, inventory_daily, device_metrics
- Rollups at hourly/daily levels for fast panels

4.3 Redis (support)
- Rate limits, dedupe cache, hot panel cache with short TTL

4.4 S3/MinIO (archive)
- Raw JSON and parquet snapshots for ML and long retention

5. Processing and Aggregation
- Stream jobs compute: funnels, conversion, AOV, payment success/failure, risk bands, inventory days-of-cover and stockouts, delivery SLAs, device adherence and adverse metrics, marketing CTR and conversions
- Aggregates persisted to ClickHouse (and optionally a small Postgres kpi table for quick reads)

6. Security and Compliance
- OAuth2/JWT for users; API keys for services
- Tenant claim enforced at query and write layers
- Mask sensitive fields; encrypt where needed (pgcrypto)
- Full audit trail; GDPR export async workflow with signed artifacts

7. Testing and Quality
- Unit tests for validators, idempotency, masking
- Contract tests against Analytics.yaml
- Integration tests with Testcontainers (Postgres, Kafka, Redis, ClickHouse)
- Load tests (k6/Gatling) for ingest and dashboard queries
- Fault tests: broker loss, DB slow, DLQ replay

8. Deployment and Operations
- Local: Docker Compose with Postgres, Redis, Kafka, ClickHouse, Prometheus, Grafana
- Higher envs: Kubernetes with HPA, TLS, secrets manager, rolling deploys
- Migrations via Flyway on startup; blue/green or canary for safe releases
- Backups: Postgres WAL archiving; ClickHouse backups; S3 lifecycle policies

9. Risks and Mitigation
- OLTP/OLAP drift → reconcile jobs, lineage checks
- PII leakage → strict masking and reviews
- Schema evolution → versioned topics and dark-launch validation
- Hot partitions → balanced keys, proper table partitioning
- High-cardinality labels in Prometheus → limit and aggregate labels

10. Extensibility
- Add an event by: define schema → add topic → map to ClickHouse columns → add aggregate view and panels → extend validation and RBAC

