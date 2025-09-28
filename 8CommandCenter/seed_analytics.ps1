<#
.SYNOPSIS
Seed Analytics with comprehensive dummy events for demo.
.DESCRIPTION
Seeds all event types from all microservices with realistic data.
.USAGE
From repo root: . .\8CommandCenter\seed_analytics.ps1
#>

$ErrorActionPreference='Stop'
$BASE="http://localhost:8085"
$ApiKey="dev-key-1"
$Headers=@{"X-API-Key"=$ApiKey}

$User="user_demo_1"; $Order="ORDER_DEMO_1001"; $Session="sess_demo_1"; $Device="NFLX_A1B2C3D4"

function Send-Event {
  param([string]$Type,[hashtable]$Data)
  $body=[ordered]@{
    type=$Type
    occurred_at=(Get-Date).ToUniversalTime().ToString("o")
    producer="demo"; source_service="demo-script"
    user_id=$User; order_id=$Order; session_id=$Session
    trace_id=[guid]::NewGuid().ToString("N")
    idempotency_key=[guid]::NewGuid().ToString("N")
    data=$Data
  }
  $json=$body|ConvertTo-Json -Depth 10
  Invoke-RestMethod -Method Post -Uri "$BASE/analytics/ingest" -Headers $Headers -ContentType "application/json" -Body $json | Out-Null
}

Write-Host "Starting comprehensive event seeding..." -ForegroundColor Cyan

# Search Events
Send-Event "search" @{ query="nanofluidic injection patch"; filters_applied=@(@{filter_type="price_range";filter_value="50-100"}); results_returned=25; top_clicked_product="NFLX_PATCH_DIABETES_001"; zero_result_flag=$false; search_duration="PT45S"; device=@{platform="web"}; geo=@{region="Maharashtra"; city="Mumbai"} }
Send-Event "search" @{ query="insulin delivery system"; results_returned=15; top_clicked_product="NFLX_PATCH_001"; zero_result_flag=$false; search_duration="PT30S"; device=@{platform="mobile_app"} }
Send-Event "search" @{ query="glucose monitoring"; results_returned=0; zero_result_flag=$true; search_duration="PT10S" }

# Cart Events  
Send-Event "cart" @{ action="item_added"; items_added=@(@{product_id="NFLX_PATCH_001"; quantity=2; price=1249.50; category="microfluidics"}); cart_total=@{amount=2499.00; currency="INR"}; coupon_code="SAVE20"; device=@{platform="web"}; geo=@{region="Maharashtra"; city="Mumbai"} }
Send-Event "cart" @{ action="item_removed"; items_added=@(@{product_id="NFLX_SENSOR_002"; quantity=1}); cart_total=@{amount=1249.50; currency="INR"} }
Send-Event "cart" @{ action="item_updated"; items_added=@(@{product_id="NFLX_PATCH_001"; quantity=3}); cart_total=@{amount=3748.50; currency="INR"} }
Send-Event "cart" @{ action="cart_viewed"; cart_total=@{amount=3748.50; currency="INR"}; cart_abandonment_duration="PT10M23S" }

# Checkout Events
Send-Event "checkout" @{ checkout_status="completed"; total_order_value=@{amount=2499.00; currency="INR"}; number_of_items=2; payment_method="upi"; applied_discounts=499.80; shipping_region="Western India"; checkout_duration="PT3M12S" }
Send-Event "checkout" @{ checkout_status="started"; total_order_value=@{amount=5000; currency="INR"}; number_of_items=4 }
Send-Event "checkout" @{ checkout_status="abandoned"; total_order_value=@{amount=1500; currency="INR"}; checkout_duration="PT15M" }

# Payment Events
Send-Event "payment" @{ transaction_id="TXN_DEMO_1"; payment_gateway="razorpay"; payment_method="upi"; payment_status="captured"; amount=2499.00; currency="INR"; confirmation_duration="PT2S"; risk_score=9.5 }
Send-Event "payment" @{ transaction_id="TXN_DEMO_2"; payment_gateway="payu"; payment_method="credit_card"; payment_status="authorized"; amount=5000; currency="INR"; risk_score=15 }
Send-Event "payment" @{ transaction_id="TXN_DEMO_3"; payment_gateway="razorpay"; payment_method="debit_card"; payment_status="failed"; failure_reason="insufficient_funds"; amount=1500; currency="INR" }
Send-Event "payment" @{ transaction_id="TXN_DEMO_4"; payment_status="refunded"; refund_amount=500; amount=2000; currency="INR" }

# Notification Events
Send-Event "notification" @{ notification_type="email"; notification_event="order_confirmation"; delivery_status="delivered"; template_id="order_confirm_v2"; vendor="msg91"; language="en-IN"; trigger_source="order_service"; open_rate=0.85 }
Send-Event "notification" @{ notification_type="sms"; notification_event="shipping_update"; delivery_status="sent"; vendor="kaleyra"; language="hi-IN" }
Send-Event "notification" @{ notification_type="push"; notification_event="promotion"; delivery_status="opened"; click_through_rate=0.12 }
Send-Event "notification" @{ notification_type="whatsapp"; delivery_status="delivered"; vendor="netcore" }

# Logistics Events
Send-Event "logistics" @{ warehouse_id="WH_MUM_001"; delivery_partner="bluedart"; tracking_id="TRK_DEMO_1001"; delivery_status="delivered"; estimated_delivery="2025-09-15T18:00:00Z"; actual_delivery=(Get-Date).ToUniversalTime().ToString("o"); delivery_region="Western Region"; return_status="none" }
Send-Event "logistics" @{ warehouse_id="WH_DEL_002"; delivery_partner="delhivery"; tracking_id="TRK_DEMO_1002"; delivery_status="in_transit"; estimated_delivery="2025-09-16T15:00:00Z" }
Send-Event "logistics" @{ delivery_status="out_for_delivery"; tracking_id="TRK_DEMO_1003"; delivery_partner="ecom_express" }
Send-Event "logistics" @{ delivery_status="returned"; return_status="completed"; tracking_id="TRK_DEMO_1004" }

# Inventory Events
Send-Event "inventory" @{ product_id="NFLX_PATCH_001"; warehouse_id="WH_MUM_001"; stock_level=148; action="sale"; quantity_changed=-2; last_restock="2025-09-10T10:00:00Z"; product_category="microfluidics"; reorder_threshold=20; avg_turnover_days=30 }
Send-Event "inventory" @{ product_id="NFLX_SENSOR_002"; warehouse_id="WH_DEL_002"; stock_level=250; action="restock"; quantity_changed=100; supplier_id="SUPP_TECH_001" }
Send-Event "inventory" @{ product_id="NFLX_CHIP_003"; stock_level=15; action="transfer"; quantity_changed=-10; warehouse_id="WH_BLR_003" }
Send-Event "inventory" @{ product_id="NFLX_PATCH_001"; stock_level=5; action="reservation"; daily_units_sold=12; shrinkage_rate=0.02 }

# User Events
Send-Event "user" @{ action="signup"; user_type="registered"; email_verified=$true; mobile_verified=$false; device=@{platform="web"}; geo=@{region="Maharashtra"; city="Mumbai"} }
Send-Event "user" @{ action="login"; user_type="premium"; login_method="email_password"; login_count=15; last_active=(Get-Date).ToUniversalTime().ToString("o"); avg_session_duration="PT25M" }
Send-Event "user" @{ action="profile_update"; email_verified=$true; mobile_verified=$true }
Send-Event "user" @{ action="logout"; user_type="registered" }

# Review & Feedback Events
Send-Event "review" @{ review_id="REV_12345"; product_id="NFLX_PATCH_001"; rating=5; review_text="Excellent patch quality!"; verified_purchase=$true; helpful_votes=12; total_votes=15; review_status="approved" }
Send-Event "review" @{ review_id="REV_12346"; product_id="NFLX_SENSOR_002"; rating=4; review_text="Good sensor accuracy"; verified_purchase=$true }
Send-Event "feedback" @{ feedback_id="FB_12345"; rating=5; category="product"; sentiment="positive"; comment="Great product"; resolved=$true; resolution_duration="PT2H15M"; channel="website" }
Send-Event "feedback" @{ feedback_id="FB_12346"; rating=3; category="delivery"; sentiment="neutral"; comment="Delayed delivery" }

# Marketing/Growth Events
Send-Event "recommendation" @{ recommendation_type="product"; algorithm="collaborative_filtering"; recommended_items=@(@{item_id="NFLX_PATCH_002"; score=0.92; position=1}); context="product_page"; clicked=$true; clicked_item_id="NFLX_PATCH_002"; converted=$false; model_version="v2.1.0" }
Send-Event "recommendation" @{ recommendation_type="personalized"; algorithm="ml_model"; recommended_items=@(@{item_id="NFLX_SENSOR_003"; score=0.88}) }
Send-Event "promotion" @{ promotion_id="PROMO_SUMMER_2025"; promotion_type="discount"; action="applied"; discount_percentage=20.0; promotion_channel="website_banner"; campaign_id="CAMP_SUMMER_001"; minimum_order_value=@{amount=1000; currency="INR"} }
Send-Event "promotion" @{ promotion_id="PROMO_DIWALI"; promotion_type="bogo"; action="viewed"; promotion_channel="email" }
Send-Event "affiliate" @{ affiliate_id="AFF_PARTNER_001"; action="conversion"; commission_rate=0.05; commission_amount=@{amount=50; currency="INR"}; referral_url="https://our_website_link.com/ref/partner001"; conversion_value=@{amount=2499; currency="INR"}; tracking_id="TRACK_AFF_12345" }
Send-Event "affiliate" @{ affiliate_id="AFF_PARTNER_002"; action="click" }
Send-Event "subscription" @{ subscription_id="SUB_PREMIUM_001"; action="renewed"; plan_id="PLAN_PREMIUM_MONTHLY"; billing_cycle="monthly"; subscription_value=@{amount=499; currency="INR"}; auto_renewal=$true; trial_period=$false }
Send-Event "subscription" @{ subscription_id="SUB_BASIC_002"; action="cancelled"; cancellation_reason="price" }
Send-Event "support" @{ ticket_id="TICKET_12345"; action="resolved"; category="technical"; priority="medium"; channel="chat"; agent_id="AGENT_001"; resolution_duration="PT2H30M"; satisfaction_rating=4 }
Send-Event "support" @{ ticket_id="TICKET_12346"; action="created"; category="billing"; priority="high" }
Send-Event "wishlist" @{ wishlist_id="WISH_USER_001"; action="item_added"; product_id="NFLX_PATCH_001"; wishlist_size=5; privacy_setting="private" }
Send-Event "wishlist" @{ action="item_moved_to_cart"; product_id="NFLX_SENSOR_002" }
Send-Event "comparison" @{ comparison_id="COMP_12345"; products_compared=@("NFLX_PATCH_001","MFLX_CHIP_002","NFLX_SENSOR_003"); comparison_criteria=@("price","features","ratings"); selected_product="NFLX_PATCH_001"; comparison_duration="PT3M45S" }
Send-Event "abandonment" @{ abandonment_type="cart"; stage="payment_info"; abandonment_duration="PT15M"; cart_value=@{amount=1999; currency="INR"}; items_count=3; exit_page="/checkout/payment"; recovery_email_sent=$true }
Send-Event "abandonment" @{ abandonment_type="search"; stage="results" }
Send-Event "referral" @{ referrer_id="USER_REFERRER_001"; referee_id="USER_REFEREE_001"; referral_code="REF_ABC123"; action="first_purchase"; reward_type="discount"; reward_value=@{amount=100; currency="INR"}; sharing_channel="social_media" }
Send-Event "referral" @{ referrer_id="USER_B"; action="code_generated"; referral_code="REF_XYZ789" }
Send-Event "loyalty" @{ loyalty_program_id="LOYALTY_PREMIUM"; action="points_earned"; points_earned=100; current_points_balance=1250; tier_level="gold" }
Send-Event "loyalty" @{ action="points_redeemed"; points_redeemed=500; reward_id="REWARD_FREE_DELIVERY" }

# Device Telemetry Events
Send-Event "device_activation" @{ device_id=$Device; batch_number="BATCH_20250910_MUM"; activation_method="qr_scan"; device_type="insulin_patch"; patient_id="PT_HASH_ABC123"; healthcare_provider="Apollo_Hospital_Mumbai"; prescription_id="RX_2025_001234"; activation_location=@{region="Maharashtra"; city="Mumbai"}; device_metadata=@{firmware_version="v2.1.3"; manufacturing_date="2025-08-05"; expiry_date="2025-11-05"} }
Send-Event "device_activation" @{ device_id="NFLX_B2C3D4E5"; batch_number="BATCH_20250911_DEL"; activation_method="nfc_tap"; device_type="hormone_patch" }
Send-Event "patch_application" @{ device_id=$Device; application_site="upper_arm"; application_timestamp=(Get-Date).ToUniversalTime().ToString("o"); skin_preparation=@{cleaned=$true; hair_removed=$true; skin_condition="normal"}; application_pressure=1500.0; adhesion_quality="excellent"; patient_comfort_score=8; environmental_conditions=@{temperature=24.5; humidity=60.2} }
Send-Event "patch_application" @{ device_id="NFLX_B2C3D4E5"; application_site="abdomen"; application_timestamp=(Get-Date).AddHours(-2).ToUniversalTime().ToString("o") }
Send-Event "sensor_reading" @{ device_id=$Device; sensor_type="glucose"; reading_value=118.5; unit_of_measurement="mg/dL"; timestamp=(Get-Date).ToUniversalTime().ToString("o"); reading_quality="high"; calibration_status="calibrated"; baseline_deviation=15.2; trend_direction="stable"; alert_triggered=$false; clinical_significance="normal" }
Send-Event "sensor_reading" @{ device_id=$Device; sensor_type="insulin_level"; reading_value=25.0; unit_of_measurement="mU/L"; timestamp=(Get-Date).AddMinutes(-30).ToUniversalTime().ToString("o") }
Send-Event "sensor_reading" @{ device_id=$Device; sensor_type="temperature"; reading_value=37.2; unit_of_measurement="C"; timestamp=(Get-Date).AddMinutes(-15).ToUniversalTime().ToString("o") }
Send-Event "dosage_delivery" @{ device_id=$Device; substance_delivered="insulin_rapid_acting"; dosage_amount=2.0; unit_of_measurement="units"; delivery_timestamp=(Get-Date).ToUniversalTime().ToString("o"); delivery_method="micro_needle"; delivery_rate=0.5; delivery_pressure=2000.0; delivery_success=$true; patient_response=@{immediate_reaction="none"; pain_level=1; absorption_rate="normal"}; reservoir_level_remaining=75.5 }
Send-Event "dosage_delivery" @{ device_id="NFLX_B2C3D4E5"; substance_delivered="hormone_estrogen"; dosage_amount=0.05; unit_of_measurement="mg"; delivery_timestamp=(Get-Date).AddHours(-1).ToUniversalTime().ToString("o") }
Send-Event "adverse_event" @{ device_id=$Device; event_type="skin_irritation"; severity="mild"; reporter_type="patient"; event_description="Mild redness around patch"; onset_time=(Get-Date).AddMinutes(-30).ToUniversalTime().ToString("o"); resolution_time=(Get-Date).ToUniversalTime().ToString("o"); medical_intervention_required=$false; regulatory_reporting_required=$false; patient_demographics=@{age_group="adult"; gender="female"; medical_conditions=@("diabetes_type_1","hypertension")} }
Send-Event "adverse_event" @{ device_id="NFLX_B2C3D4E5"; event_type="allergic_reaction"; severity="moderate"; reporter_type="healthcare_provider" }

# Manufacturing/Quality Events
Send-Event "batch_production" @{ batch_number="BATCH_20250910_MUM"; status="completed"; manufacturing_date="2025-09-10"; device_type="insulin_patch" }
Send-Event "batch_production" @{ batch_number="BATCH_20250911_DEL"; status="in_progress"; device_type="hormone_patch" }
Send-Event "quality_control" @{ batch_number="BATCH_20250910_MUM"; status="passed"; tests_run=@("adhesion","delivery_rate","sterility") }
Send-Event "quality_control" @{ batch_number="BATCH_20250909_BLR"; status="failed"; tests_run=@("sterility") }
Send-Event "sterilization" @{ batch_number="BATCH_20250910_MUM"; method="gamma_radiation"; status="completed" }
Send-Event "packaging" @{ batch_number="BATCH_20250910_MUM"; packaging_type="blister"; units_packaged=1000 }
Send-Event "distribution" @{ batch_number="BATCH_20250910_MUM"; destination="WH_MUM_001"; units_shipped=500 }
Send-Event "recall" @{ batch_number="BATCH_20250810_MUM"; reason="packaging_issue"; severity="low" }
Send-Event "recall" @{ batch_number="BATCH_20250808_DEL"; reason="contamination"; severity="high" }

Write-Host "Seeding complete - added 70+ events across all categories" -ForegroundColor Green
Write-Host "Query the database to verify: docker exec -i 2analyticsbackend-postgres-1 psql -U analytics -d analytics -c 'select type,count(*) from events group by 1 order by 2 desc;'" -ForegroundColor Yellow
