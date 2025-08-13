# POCM Database Tables Documentation

## Analytics Service Tables

### Core Event Storage Tables
- `events` - Main events table with envelope data
- `event_data` - JSONB payload storage for all event types

### E-commerce Event Tables
- `cart_events`
- `checkout_events`
- `payment_events`
- `notification_events`
- `logistics_events`
- `user_events`
- `inventory_events`
- `feedback_events`
- `search_events`
- `order_events`
- `product_view_events`
- `category_browse_events`
- `price_change_events`
- `discount_events`
- `refund_events`
- `return_events`
- `shipping_events`
- `delivery_attempt_events`
- `customer_service_events`
- `session_events`
- `page_view_events`
- `click_events`
- `form_submission_events`
- `error_events`
- `performance_events`

### Marketing & Growth Event Tables
- `recommendation_events`
- `promotion_events`
- `affiliate_events`
- `subscription_events`
- `support_events`
- `review_events`
- `wishlist_events`
- `comparison_events`
- `abandonment_events`
- `referral_events`
- `loyalty_events`

### Medical Device Event Tables
- `device_activation_events`
- `patch_application_events`
- `sensor_reading_events`
- `dosage_delivery_events`
- `device_removal_events`
- `calibration_events`

### Medical & Compliance Event Tables
- `adverse_event_reports`
- `efficacy_measurement_events`
- `patient_outcome_events`
- `clinical_data_events`
- `regulatory_report_events`

### Supply Chain & Manufacturing Event Tables
- `batch_production_events`
- `quality_control_events`
- `sterilization_events`
- `packaging_events`
- `distribution_events`
- `recall_events`

### B2B & Healthcare Provider Event Tables
- `provider_onboarding_events`
- `bulk_order_events`
- `training_completion_events`
- `certification_events`
- `audit_trail_events`

### Analytics & ML Event Tables
- `ml_prediction_events`
- `anomaly_detection_events`
- `cohort_analysis_events`
- `a_b_test_events`
- `personalization_events`

### Analytics Service Support Tables
- `batch_operations` - Batch processing tracking
- `ml_models` - Machine learning model metadata
- `compliance_audit_logs` - HIPAA/GDPR compliance tracking
- `gdpr_export_requests` - Data export request tracking
- `event_schemas` - Schema versioning and validation
- `api_keys` - API key management
- `rate_limits` - Rate limiting configuration
- `data_retention_policies` - Data lifecycle management

---

## Templates Service Tables

### Core Template Tables
- `templates` - Main template storage
- `template_versions` - Template version history
- `template_variables` - Template variable definitions
- `template_variants` - A/B testing variants

### Template Category Tables
- `transactional_templates`
- `marketing_templates`
- `support_templates`
- `system_templates`
- `lifecycle_templates`
- `promotional_templates`

### Medical Device Template Tables
- `patient_care_templates`
- `device_instruction_templates`
- `adverse_event_templates`
- `clinical_alert_templates`

### Healthcare Provider Template Tables
- `provider_notification_templates`
- `training_material_templates`
- `compliance_report_templates`

### Regulatory & Compliance Template Tables
- `regulatory_filing_templates`
- `safety_alert_templates`
- `recall_notification_templates`
- `audit_communication_templates`

### B2B Healthcare Template Tables
- `bulk_order_templates`
- `certification_update_templates`
- `partnership_communication_templates`

### Message Delivery Tables
- `message_queue` - Outbound message queue
- `message_delivery_status` - Delivery tracking
- `message_analytics` - Performance metrics
- `bulk_operations` - Bulk messaging operations
- `delivery_providers` - Provider configuration (MSG91, Kaleyra, etc.)

### Template Service Support Tables
- `template_analytics` - Usage and performance metrics
- `delivery_configurations` - Channel-specific settings
- `compliance_validations` - HIPAA/FDA compliance checks
- `template_approvals` - Regulatory approval tracking
- `localization_data` - Multi-language content
- `media_assets` - Images, videos, attachments
- `template_tags` - Tagging and categorization
- `workflow_definitions` - Automated communication workflows
- `escalation_rules` - Emergency communication protocols
- `provider_segments` - Healthcare provider groupings
- `patient_consent_records` - Communication consent tracking
- `template_performance_metrics` - Open rates, click rates, conversions