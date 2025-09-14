ALTER TABLE template_versions
  ALTER COLUMN variables_schema TYPE TEXT USING variables_schema::text;