CREATE TABLE IF NOT EXISTS templates (
  id UUID PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  type VARCHAR(32) NOT NULL,
  category VARCHAR(64) NOT NULL,
  language VARCHAR(16) NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'draft',
  tags TEXT[],
  tenant_id VARCHAR(64) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS template_versions (
  id UUID PRIMARY KEY,
  template_id UUID NOT NULL REFERENCES templates(id) ON DELETE CASCADE,
  version INT NOT NULL,
  subject TEXT,
  content_raw TEXT NOT NULL,
  content_compiled TEXT,
  engine VARCHAR(16) NOT NULL,
  variables_schema JSONB NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT false,
  created_by VARCHAR(64),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(template_id, version)
);

CREATE TABLE IF NOT EXISTS template_variants (
  id UUID PRIMARY KEY,
  template_id UUID NOT NULL REFERENCES templates(id) ON DELETE CASCADE,
  key VARCHAR(64) NOT NULL,
  weight INT NOT NULL DEFAULT 50,
  diff JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS messages (
  id UUID PRIMARY KEY,
  template_id UUID NOT NULL REFERENCES templates(id),
  version INT NOT NULL,
  channel VARCHAR(16) NOT NULL,
  recipient JSONB NOT NULL,
  variables JSONB NOT NULL,
  provider VARCHAR(32),
  provider_msg_id VARCHAR(128),
  status VARCHAR(32) NOT NULL DEFAULT 'queued',
  error_code VARCHAR(64),
  sent_at TIMESTAMPTZ,
  delivered_at TIMESTAMPTZ,
  opened_at TIMESTAMPTZ,
  clicked_at TIMESTAMPTZ,
  tenant_id VARCHAR(64) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_templates_name ON templates(name);
CREATE INDEX IF NOT EXISTS idx_messages_status ON messages(status);
