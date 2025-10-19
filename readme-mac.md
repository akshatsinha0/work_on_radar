# Run demos (macOS/Linux - Bash/Zsh)

Run these commands from the repository root (the folder that contains for example `8CommandCenter`).

## Prerequisites
- Docker Desktop running
- Bash or Zsh shell (default on macOS)

## Quick Start

Templates (build → start → logs → health → rebuild+smoke):

```bash
# Note: Requires PowerShell Core (pwsh) installed on Mac
# Install via: brew install --cask powershell
pwsh -NoProfile -Command ". ./8CommandCenter/templates.ps1; Demo-Templates"
```

Analytics (build image → start stack → logs → DB checks → unit + integration tests):

```bash
# Note: Requires PowerShell Core (pwsh) installed on Mac
# Install via: brew install --cask powershell
pwsh -NoProfile -Command ". ./8CommandCenter/analytics.ps1; Demo-Analytics"
```

Seed Analytics with dummy events (HTTP ingest to the app on :8085):

```bash
pwsh -NoProfile -File ./8CommandCenter/seed_analytics.ps1
```

## Demo Proof Points

After seeding, show proof points for the demo:

### Health Check
Note: Redis may be DOWN locally; Postgres/disk/ping should be UP

```bash
curl -s 'http://localhost:8085/actuator/health' | python3 -m json.tool
```

### Ingest Endpoint Metrics
Successful POSTs count:

```bash
curl -s 'http://localhost:8085/actuator/metrics/http.server.requests?tag=uri:/analytics/ingest&tag=method:POST' | python3 -m json.tool
```

### Database Queries

Latest 5 events with payload snippet:

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select type,occurred_at,left(data_json::text,120) as data_snippet from events order by occurred_at desc limit 5;"
```

Events per minute over last 10 minutes:

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select to_char(date_trunc('minute',received_at),'HH24:MI') as minute,count(*) from events where received_at>now()-interval '10 minutes' group by 1 order by 1;"
```

Total by type (optional closing slide):

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select type,count(*) from events group by 1 order by 2 desc;"
```

## Detail Views by Event Label

Each command below shows the last 10 rows for that label and picks key fields from data_json.

### Cart Events
id, time, user/order, cart action, cart_total amount+currency, coupon

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,order_id,data_json->>'action' as action,(data_json->'cart_total'->>'amount')::numeric as total_amount,data_json->'cart_total'->>'currency' as currency,data_json->>'coupon_code' as coupon from events where type='cart' order by occurred_at desc limit 10;"
```

### Payment Events
transaction_id, gateway, method, status, amount, currency

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,order_id,data_json->>'transaction_id' as transaction_id,data_json->>'payment_gateway' as gateway,data_json->>'payment_method' as method,data_json->>'payment_status' as status,(data_json->>'amount')::numeric as amount,data_json->>'currency' as currency from events where type='payment' order by occurred_at desc limit 10;"
```

### Notification Events
channel, event, delivery status, template, vendor

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,order_id,data_json->>'notification_type' as type,data_json->>'notification_event' as event,data_json->>'delivery_status' as delivery_status,data_json->>'template_id' as template_id,data_json->>'vendor' as vendor from events where type='notification' order by occurred_at desc limit 10;"
```

### Inventory Events
product, warehouse, stock_level, action, quantity change

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'product_id' as product_id,data_json->>'warehouse_id' as warehouse_id,(data_json->>'stock_level')::int as stock_level,data_json->>'action' as action,(data_json->>'quantity_changed')::int as qty_delta from events where type='inventory' order by occurred_at desc limit 10;"
```

### Logistics Events
tracking id, partner, delivery status, ETA and actual delivery

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'tracking_id' as tracking_id,data_json->>'delivery_partner' as partner,data_json->>'delivery_status' as status,data_json->>'estimated_delivery' as eta,data_json->>'actual_delivery' as delivered_at from events where type='logistics' order by occurred_at desc limit 10;"
```

### User Events
action, user_type, email/mobile verified, login_method, avg_session_duration

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,data_json->>'action' as action,data_json->>'user_type' as user_type,data_json->>'email_verified' as email_verified,data_json->>'mobile_verified' as mobile_verified,data_json->>'login_method' as login_method,data_json->>'avg_session_duration' as avg_session_duration from events where type='user' order by occurred_at desc limit 10;"
```

### Search Events
query text, results count, zero-result flag, search duration

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,order_id,data_json->>'query' as query,(data_json->>'results_returned')::int as results,data_json->>'zero_result_flag' as zero_result,data_json->>'search_duration' as duration from events where type='search' order by occurred_at desc limit 10;"
```

### Sensor Reading Events
device, sensor_type, reading_value+uom, quality

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'device_id' as device_id,data_json->>'sensor_type' as sensor_type,(data_json->>'reading_value')::numeric as reading_value,data_json->>'unit_of_measurement' as uom,data_json->>'reading_quality' as quality from events where type='sensor_reading' order by occurred_at desc limit 10;"
```

### Checkout Events
status, total order value, items, payment_method, checkout duration

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,order_id,data_json->>'checkout_status' as status,(data_json->'total_order_value'->>'amount')::numeric as total_amount,data_json->'total_order_value'->>'currency' as currency,(data_json->>'number_of_items')::int as items,data_json->>'payment_method' as payment_method,data_json->>'checkout_duration' as duration from events where type='checkout' order by occurred_at desc limit 10;"
```

### Recall Events
batch_number, reason, severity

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'batch_number' as batch_number,data_json->>'reason' as reason,data_json->>'severity' as severity from events where type='recall' order by occurred_at desc limit 10;"
```

### Adverse Event Events
device, event_type, severity, reporter, medical_intervention_required

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'device_id' as device_id,data_json->>'event_type' as event_type,data_json->>'severity' as severity,data_json->>'reporter_type' as reporter,data_json->>'medical_intervention_required' as med_intervention from events where type='adverse_event' order by occurred_at desc limit 10;"
```

### Promotion Events
promotion_id, type, action, discount, channel

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'promotion_id' as promotion_id,data_json->>'promotion_type' as type,data_json->>'action' as action,(data_json->>'discount_percentage')::numeric as discount,data_json->>'promotion_channel' as channel from events where type='promotion' order by occurred_at desc limit 10;"
```

### Referral Events
referrer/referee, action, referral_code, reward_type

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,order_id,data_json->>'referrer_id' as referrer_id,data_json->>'referee_id' as referee_id,data_json->>'action' as action,data_json->>'referral_code' as referral_code,data_json->>'reward_type' as reward_type from events where type='referral' order by occurred_at desc limit 10;"
```

### Feedback Events
feedback_id, rating, category, sentiment, resolved flag

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,data_json->>'feedback_id' as feedback_id,(data_json->>'rating')::int as rating,data_json->>'category' as category,data_json->>'sentiment' as sentiment,data_json->>'resolved' as resolved from events where type='feedback' order by occurred_at desc limit 10;"
```

### Affiliate Events
affiliate_id, action, commission_rate, commission_amount+currency

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'affiliate_id' as affiliate_id,data_json->>'action' as action,(data_json->>'commission_rate')::numeric as commission_rate,(data_json->'commission_amount'->>'amount')::numeric as commission_amount,data_json->'commission_amount'->>'currency' as currency from events where type='affiliate' order by occurred_at desc limit 10;"
```

### Loyalty Events
program_id, action, points_earned/redeemed, tier level

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,data_json->>'loyalty_program_id' as program_id,data_json->>'action' as action,(data_json->>'points_earned')::int as points_earned,(data_json->>'points_redeemed')::int as points_redeemed,data_json->>'tier_level' as tier from events where type='loyalty' order by occurred_at desc limit 10;"
```

### Batch Production Events
batch_number, status, device_type, manufacturing_date

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'batch_number' as batch_number,data_json->>'status' as status,data_json->>'device_type' as device_type,data_json->>'manufacturing_date' as manufacturing_date from events where type='batch_production' order by occurred_at desc limit 10;"
```

### Dosage Delivery Events
device, substance, dosage_amount+uom, method, success flag

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'device_id' as device_id,data_json->>'substance_delivered' as substance,(data_json->>'dosage_amount')::numeric as dosage_amount,data_json->>'unit_of_measurement' as uom,data_json->>'delivery_method' as method,data_json->>'delivery_success' as success from events where type='dosage_delivery' order by occurred_at desc limit 10;"
```

### Patch Application Events
device, application_site, adhesion_quality, comfort score

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'device_id' as device_id,data_json->>'application_site' as site,data_json->>'adhesion_quality' as adhesion_quality,(data_json->>'patient_comfort_score')::int as comfort from events where type='patch_application' order by occurred_at desc limit 10;"
```

### Abandonment Events
type, stage, duration, cart_value, items_count, exit_page

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'abandonment_type' as type,data_json->>'stage' as stage,data_json->>'abandonment_duration' as duration,(data_json->'cart_value'->>'amount')::numeric as cart_value,(data_json->>'items_count')::int as items_count,data_json->>'exit_page' as exit_page from events where type='abandonment' order by occurred_at desc limit 10;"
```

### Device Activation Events
device_id, device_type, activation_method, batch_number

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,order_id,data_json->>'device_id' as device_id,data_json->>'device_type' as device_type,data_json->>'activation_method' as activation_method,data_json->>'batch_number' as batch from events where type='device_activation' order by occurred_at desc limit 10;"
```

### Wishlist Events
wishlist_id, action, product_id, wishlist_size

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,data_json->>'wishlist_id' as wishlist_id,data_json->>'action' as action,data_json->>'product_id' as product_id,(data_json->>'wishlist_size')::int as wishlist_size from events where type='wishlist' order by occurred_at desc limit 10;"
```

### Review Events
review_id, product_id, rating, status, short review text

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,order_id,data_json->>'review_id' as review_id,data_json->>'product_id' as product_id,(data_json->>'rating')::int as rating,data_json->>'review_status' as status,left(data_json->>'review_text',60) as review_text from events where type='review' order by occurred_at desc limit 10;"
```

### Support Events
ticket_id, action, category, priority, channel, agent_id

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,data_json->>'ticket_id' as ticket_id,data_json->>'action' as action,data_json->>'category' as category,data_json->>'priority' as priority,data_json->>'channel' as channel,data_json->>'agent_id' as agent_id from events where type='support' order by occurred_at desc limit 10;"
```

### Quality Control Events
batch_number, status, tests_count

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'batch_number' as batch_number,data_json->>'status' as status,coalesce(jsonb_array_length(data_json->'tests_run'),0) as tests_count from events where type='quality_control' order by occurred_at desc limit 10;"
```

### Recommendation Events
type, algorithm, model_version, clicked flag, clicked_item_id

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,user_id,data_json->>'recommendation_type' as rec_type,data_json->>'algorithm' as algorithm,data_json->>'model_version' as model_version,data_json->>'clicked' as clicked,data_json->>'clicked_item_id' as clicked_item_id from events where type='recommendation' order by occurred_at desc limit 10;"
```

### Subscription Events
subscription_id, action, plan_id, billing_cycle, amount+currency, auto_renewal

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'subscription_id' as subscription_id,data_json->>'action' as action,data_json->>'plan_id' as plan_id,data_json->>'billing_cycle' as billing_cycle,(data_json->'subscription_value'->>'amount')::numeric as amount,data_json->'subscription_value'->>'currency' as currency,data_json->>'auto_renewal' as auto_renewal from events where type='subscription' order by occurred_at desc limit 10;"
```

### Packaging Events
batch_number, packaging_type, units_packaged

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'batch_number' as batch_number,data_json->>'packaging_type' as packaging_type,(data_json->>'units_packaged')::int as units_packaged from events where type='packaging' order by occurred_at desc limit 10;"
```

### Comparison Events
comparison_id, selected_product, comparison duration

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'comparison_id' as comparison_id,data_json->>'selected_product' as selected_product,data_json->>'comparison_duration' as duration from events where type='comparison' order by occurred_at desc limit 10;"
```

### Distribution Events
batch_number, destination, units_shipped

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'batch_number' as batch_number,data_json->>'destination' as destination,(data_json->>'units_shipped')::int as units_shipped from events where type='distribution' order by occurred_at desc limit 10;"
```

### Sterilization Events
batch_number, method, status

```bash
docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select id,to_char(occurred_at,'YYYY-MM-DD HH24:MI:SS') as occurred,data_json->>'batch_number' as batch_number,data_json->>'method' as method,data_json->>'status' as status from events where type='sterilization' order by occurred_at desc limit 10;"
```

---

## Notes

- Docker commands are identical across platforms
- For JSON formatting, using `python3 -m json.tool` (built-in on macOS)
- Alternative: install `jq` via `brew install jq` and use `| jq` instead
- PowerShell scripts still require PowerShell Core on Mac: `brew install --cask powershell`
