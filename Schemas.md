#Schemas for POCM Analytics Service

This document lists the schemas used by the Analytics backend: shared types, event envelopes and types, individual event payloads, response shapes, and database tables for both OLTP (PostgreSQL) and OLAP (ClickHouse).

1. Shared Types
1.1 Money
- amount: number (>=0)
- currency: string (ISO-4217, e.g., "INR")

1.2 Geo
- country: string (ISO-2)
- region: string
- city: string

1.3 Device
- platform: enum [web, mobile_app, tablet, desktop]
- user_agent: string
- screen_resolution: string (e.g., 1920x1080)

2. Envelope and Enumerations
2.1 EventType
- cart, checkout, payment, notification, logistics, user, inventory, feedback, search, recommendation, promotion, affiliate, subscription, support, review, wishlist, comparison, abandonment, referral, loyalty, device_activation, patch_application, sensor_reading, dosage_delivery, adverse_event, efficacy_measurement, patient_outcome, clinical_data, regulatory_report, batch_production, quality_control, sterilization, packaging, distribution, recall, provider_onboarding, bulk_order, training_completion, certification, audit_trail, ml_prediction, anomaly_detection, cohort_analysis, a_b_test, personalization

2.2 IngestEnvelope
- type: EventType
- data: object (one of event schemas below)
- occurred_at: datetime (ISO 8601)
- producer: string
- source_service: string
- user_id?: string
- order_id?: string
- session_id?: string
- trace_id?: string
- idempotency_key?: string (safe characters)
- tenant_id: string (server-assigned based on auth)

3. Event Schemas (payloads)
3.1 CartEvent
- cart_id: string
- action: enum [item_added, item_removed, item_updated, cart_viewed, cart_cleared]
- items_added: [{product_id:string, quantity:int>=1, price:number>=0, category:string}] (optional unless action=item_added)
- cart_total: Money
- cart_abandonment_duration: duration (ISO 8601)
- coupon_code?: string
- device?: Device
- geo?: Geo

3.2 CheckoutEvent
- cart_id: string
- checkout_status: enum [started, completed, abandoned, failed]
- total_order_value: Money
- number_of_items: int>=1
- applied_discounts: number>=0
- payment_method: enum [credit_card, debit_card, upi, paytm, google_pay, bank_transfer, cash_on_delivery]
- shipping_region: string
- checkout_duration: duration
- session_id: string

3.3 PaymentEvent
- transaction_id: string
- payment_gateway: enum [razorpay, payu, ccavenue, instamojo, cashfree]
- payment_method: enum [credit_card, debit_card, upi, paytm, google_pay, bank_transfer, cash_on_delivery]
- payment_status: enum [pending, authorized, captured, failed, refunded, partially_refunded, cancelled]
- failure_reason?: string
- amount: number>=0
- currency: string (ISO-4217)
- refund_amount?: number>=0
- confirmation_duration: duration
- risk_score: number 0..100

3.4 NotificationEvent
- notification_type: enum [email, sms, push, in_app, webhook]
- notification_event: enum [order_confirmation, shipping_update, payment_receipt, promotion, reminder, welcome]
- delivery_status: enum [sent, delivered, opened, clicked, bounced, failed, unsubscribed]
- template_id: string
- vendor: enum [msg91, netcore, pepipost, firebase, custom]
- language: string (e.g., en-US)
- trigger_source: string
- open_rate: number 0..1
- click_through_rate: number 0..1

3.5 LogisticsEvent
- warehouse_id: string
- delivery_partner: enum [bluedart, dtdc, delhivery, ecom_express, xpressbees, local_courier]
- tracking_id: string
- delivery_status: enum [pending, picked_up, in_transit, out_for_delivery, delivered, failed, returned]
- estimated_delivery: datetime
- actual_delivery?: datetime
- delivery_region: string
- return_status: enum [none, requested, approved, in_transit, completed]
- delivery_delay_duration?: duration
- delivery_updates: [{status:string, timestamp:datetime, location:string}]

3.6 UserEvent
- action: enum [signup, login, logout, profile_update, password_change, email_verification, phone_verification, account_deletion]
- user_type: enum [guest, registered, premium, business]
- login_method: enum [email_password, social_google, social_facebook, phone_otp, aadhaar_otp, sso]
- login_count: int>=0
- last_active: datetime
- email_verified: bool
- mobile_verified: bool
- referral_code?: string
- avg_session_duration: duration
- device?: Device
- geo?: Geo

3.7 InventoryEvent
- product_id: string
- warehouse_id: string
- stock_level: int>=0
- action: enum [restock, sale, return, adjustment, transfer, reservation]
- quantity_changed: int (negative for sales)
- last_restock: datetime
- out_of_stock_frequency: int>=0
- product_category: string
- avg_turnover_days: int>=0
- returned_units: int>=0
- daily_units_sold: int>=0
- reorder_threshold: int>=0
- shrinkage_rate: number 0..1
- supplier_id: string

3.8 FeedbackEvent
- feedback_id: string
- rating: int 1..5
- comment?: string
- category: enum [product, service, delivery, website, support, general]
- sentiment: enum [positive, negative, neutral]
- channel: enum [website, email, phone, chat, social_media, app]
- resolved: bool
- resolution_duration?: duration
- tags?: [string]
- helpful_votes: int>=0

3.9 SearchEvent
- query: string
- filters_applied: [{filter_type:string, filter_value:string}]
- results_returned: int>=0
- top_clicked_product?: string
- click_position?: int>=1
- abandoned_search: bool
- category_clicked?: string
- zero_result_flag: bool
- search_duration: duration
- suggestion_clicked: bool
- converted_to_order: bool
- search_type: enum [text, voice, image, barcode]

3.10 RecommendationEvent
- recommendation_type: enum [product, category, brand, similar_users, trending, personalized]
- algorithm: enum [collaborative_filtering, content_based, hybrid, ml_model, rule_based]
- recommended_items: [{item_id:string, item_type:string, score:number, position:int}]
- context: enum [homepage, product_page, cart, checkout, email, search_results]
- clicked: bool
- clicked_item_id?: string
- converted: bool
- model_version: string

3.11 PromotionEvent
- promotion_id: string
- promotion_type: enum [discount, bogo, free_delivery, cashback, loyalty_points]
- action: enum [viewed, clicked, applied, removed, expired]
- discount_amount: Money
- discount_percentage?: number 0..100
- minimum_order_value: Money
- promotion_channel: enum [email, website_banner, app_notification, social_media, affiliate]
- campaign_id: string

3.12 AffiliateEvent
- affiliate_id: string
- action: enum [click, conversion, commission_earned, payout]
- commission_rate: number 0..1
- commission_amount: Money
- referral_url: uri
- conversion_value: Money
- tracking_id: string

3.13 SubscriptionEvent
- subscription_id: string
- action: enum [created, renewed, cancelled, paused, resumed, upgraded, downgraded]
- plan_id: string
- billing_cycle: enum [monthly, quarterly, yearly]
- subscription_value: Money
- trial_period: bool
- auto_renewal: bool
- cancellation_reason?: enum [price, features, competition, service_quality, other]

3.14 SupportEvent
- ticket_id: string
- action: enum [created, updated, resolved, closed, escalated, reopened]
- category: enum [technical, billing, product, delivery, account, general]
- priority: enum [low, medium, high, urgent]
- channel: enum [email, chat, phone, ticket_system, social_media]
- agent_id?: string
- resolution_duration?: duration
- satisfaction_rating?: int 1..5

3.15 ReviewEvent
- review_id: string
- product_id: string
- rating: int 1..5
- review_text?: string
- verified_purchase: bool
- helpful_votes: int>=0
- total_votes: int>=0
- review_status: enum [pending, approved, rejected, flagged]
- moderation_reason?: string

3.16 WishlistEvent
- wishlist_id: string
- action: enum [item_added, item_removed, wishlist_shared, item_moved_to_cart]
- product_id: string
- wishlist_size: int>=0
- shared_with?: [string]
- privacy_setting: enum [private, public, friends_only]

3.17 ComparisonEvent
- comparison_id: string
- products_compared: [string] (min 2)
- comparison_criteria: [string]
- selected_product?: string
- comparison_duration: duration

3.18 AbandonmentEvent
- abandonment_type: enum [cart, checkout, search, product_view, registration]
- stage: string
- abandonment_duration: duration
- cart_value: Money
- items_count: int>=0
- exit_page: uri
- recovery_email_sent: bool

3.19 ReferralEvent
- referrer_id: string
- referee_id?: string
- referral_code: string
- action: enum [code_generated, code_shared, signup_via_referral, first_purchase, reward_earned]
- reward_type?: enum [discount, cashback, points, free_product]
- reward_value: Money
- sharing_channel?: enum [email, social_media, direct_link, sms]

3.20 LoyaltyEvent
- loyalty_program_id: string
- action: enum [points_earned, points_redeemed, tier_upgraded, tier_downgraded, reward_claimed]
- points_earned?: int>=0
- points_redeemed?: int>=0
- current_points_balance: int>=0
- tier_level: enum [bronze, silver, gold, platinum, diamond]
- reward_id?: string
- expiry_date?: datetime

3.21 DeviceActivationEvent
- device_id: string (format NFLX_XXXXXXXX)
- batch_number: string (format BATCH_YYYYMMDD_XXX)
- activation_method: enum [qr_scan, nfc_tap, manual_entry, app_pairing]
- patient_id?: string (anonymized)
- healthcare_provider?: string
- prescription_id?: string
- device_type: enum [insulin_patch, pain_relief_patch, hormone_patch_microsizefibers, vaccine_patch, diagnostic_sensor]
- activation_location?: Geo
- device_metadata?: {firmware_version:string, manufacturing_date:date, expiry_date:date, storage_conditions:{temperature_range:string, humidity_exposure:number}}

3.22 PatchApplicationEvent
- device_id: string
- application_site: enum [upper_arm, abdomen, thigh, back, chest]
- application_timestamp: datetime
- skin_preparation?: {cleaned:bool, hair_removed:bool, skin_condition:enum [normal, dry, oily, sensitive, irritated]}
- application_pressure?: number
- adhesion_quality?: enum [excellent, good, fair, poor]
- patient_comfort_score?: int 1..10
- environmental_conditions?: {temperature:number, humidity:number, altitude:number}

3.23 SensorReadingEvent
- device_id: string
- sensor_type: enum [glucose, insulin_level, ph, temperature, pressure, flow_rate, biomarker]
- reading_value: number
- unit_of_measurement: string
- timestamp: datetime
- reading_quality: enum [high, medium, low, error]
- calibration_status: enum [calibrated, needs_calibration, calibration_expired]
- baseline_deviation?: number
- trend_direction?: enum [rising, falling, stable, fluctuating]
- alert_triggered?: bool
- clinical_significance?: enum [normal, attention_needed, critical, emergency]

3.24 DosageDeliveryEvent
- device_id: string
- substance_delivered: string
- dosage_amount: number
- unit_of_measurement: string
- delivery_timestamp: datetime
- delivery_method: enum [micro_needle, transdermal, iontophoresis, ultrasonic]
- delivery_rate?: number
- delivery_pressure?: number
- delivery_success: bool
- patient_response?: {immediate_reaction:enum [none, mild_irritation, moderate_reaction, severe_reaction], pain_level:int 0..10, absorption_rate:enum [fast, normal, slow, impaired]}
- reservoir_level_remaining?: number (percentage)

3.25 AdverseEventReport
- device_id: string
- event_type: enum [skin_irritation, allergic_reaction, device_malfunction, dosage_error, infection, other]
- severity: enum [mild, moderate, severe, life_threatening]
- reporter_type: enum [patient, healthcare_provider, caregiver, manufacturer]
- event_description?: string
- onset_time: datetime
- resolution_time?: datetime
- medical_intervention_required: bool
- regulatory_reporting_required: bool
- batch_investigation_triggered: bool
- patient_demographics?: {age_group:enum [pediatric, adult, geriatric], gender:enum [male, female, other, not_specified], medical_conditions:[string]}

4. Response Schemas
4.1 IngestResponse
- event_id: string
- status: enum [ingested, queued, deduped]
- deduped: bool
- received_at: datetime

4.2 Event
- id: string
- type: EventType
- occurred_at: datetime
- received_at: datetime
- producer: string
- source_service: string
- user_id?: string
- order_id?: string
- session_id?: string
- trace_id?: string
- data: object (event-specific)

4.3 EventsResponse
- items: [Event]
- total: int
- has_more: bool
- next_cursor?: string

4.4 ErrorResponse
- type: uri
- title: string
- status: int
- detail?: string
- instance?: uri
- errors?: [{field:string, message:string}]

5. Relational Tables (PostgreSQL)
5.1 events
- id BIGSERIAL PRIMARY KEY
- type VARCHAR(64) NOT NULL
- occurred_at TIMESTAMPTZ NOT NULL
- received_at TIMESTAMPTZ NOT NULL DEFAULT now()
- producer VARCHAR(255)
- source_service VARCHAR(255)
- user_id VARCHAR(255)
- order_id VARCHAR(255)
- session_id VARCHAR(255)
- trace_id VARCHAR(255)
- idempotency_key VARCHAR(255)
- tenant_id VARCHAR(64)
- data_json JSONB NOT NULL
- Indexes: unique on idempotency_key (nullable), occurred_at desc, (type,occurred_at), (user_id,occurred_at), (order_id,occurred_at), JSONB path indexes as needed
- Partitions: monthly by occurred_at

5.2 batch_operations
- id BIGSERIAL PRIMARY KEY
- batch_id VARCHAR(255) UNIQUE NOT NULL
- status VARCHAR(32) NOT NULL
- total_events INT
- created_at TIMESTAMPTZ DEFAULT now()
- updated_at TIMESTAMPTZ DEFAULT now()
- tracking_url TEXT

5.3 compliance_audit_logs
- id BIGSERIAL PRIMARY KEY
- timestamp TIMESTAMPTZ NOT NULL DEFAULT now()
- user_id VARCHAR(255)
- action VARCHAR(64)
- resource TEXT
- ip_address VARCHAR(64)
- user_agent TEXT
- data_classification VARCHAR(64)
- flags TEXT[]

5.4 gdpr_export_requests
- id BIGSERIAL PRIMARY KEY
- request_id VARCHAR(255) UNIQUE NOT NULL
- user_id VARCHAR(255) NOT NULL
- status VARCHAR(32) NOT NULL
- created_at TIMESTAMPTZ DEFAULT now()
- completed_at TIMESTAMPTZ
- download_url TEXT

5.5 Support tables
- api_keys(id, key_hash, service, scopes[], created_at, rotated_at)
- rate_limits(id, key_id, limit, window, created_at)
- data_retention_policies(id, category, ttl_days)
- ml_models(id, name, version, uri, created_at)

6. Columnar Tables (ClickHouse)
6.1 events_raw (MergeTree)
- tenant_id String
- type LowCardinality(String)
- occurred_at DateTime64(3, 'UTC')
- user_id String
- order_id String
- session_id String
- producer String
- source_service String
- idempotency_key String
- data_json JSON
- PARTITION BY toYYYYMM(occurred_at)
- ORDER BY (type, occurred_at, user_id, order_id)

6.2 Aggregates (examples)
- kpi_daily(day Date, tenant_id String, metric String, value AggregateFunction(sum, Float64)) ENGINE=AggregatingMergeTree PARTITION BY toYYYYMM(day) ORDER BY (tenant_id, metric, day)
- funnel_step(day Date, tenant_id String, step LowCardinality(String), count AggregateFunction(sum, UInt64)) ENGINE=AggregatingMergeTree
- inventory_daily(day Date, product_id String, warehouse_id String, stock_level Int64, sold Int64, returns Int64, stockouts UInt64) ENGINE=MergeTree
- device_metrics(day Date, device_type LowCardinality(String), activation UInt64, adherence Float64, dosage_success Float64, adverse_count UInt64) ENGINE=MergeTree

7. Security and Policy Fields
- Every record carries tenant_id for isolation.
- Sensitive medical fields are masked/encrypted as per policy.
- Audit entries recorded for read/export operations.

