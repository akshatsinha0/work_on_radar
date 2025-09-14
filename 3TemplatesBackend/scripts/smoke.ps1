param(
  [string]$Base = "http://localhost:8086",
  [string]$ApiKey = "dev-key-1"
)

$Headers = @{ "X-API-Key" = $ApiKey }

Write-Host "Waiting for health at $Base/health..."
$max=30
$ok=$false
for($i=1;$i -le $max;$i++){
  try {
    $health = Invoke-WebRequest -UseBasicParsing -Uri "$Base/health" -TimeoutSec 3 | Select-Object -ExpandProperty Content
    if($health){ Write-Host $health; $ok=$true; break }
  } catch {}
  Start-Sleep -Seconds 1
}
if(-not $ok){ Write-Error "Health check did not become ready in time"; exit 1 }

Write-Host "Creating template..."
$tBody = @{ name='Order Confirmation - Premium'; type='email'; category='transactional'; language='en-IN'; status='draft'; tenantId='demo' } | ConvertTo-Json -Compress
$t = Invoke-RestMethod -Method Post -Uri "$Base/templates" -Headers $Headers -ContentType 'application/json' -Body $tBody
$id = $t.id
Write-Host "Template ID: $id"

Write-Host "Adding version..."
$verBody = @{ 
  version=1
  subject='Your order {{order_id}} is confirmed!'
  contentRaw='<mjml><mj-body><mj-section><mj-column><mj-text><h2>Thanks {{customer_name}}</h2><p>Order {{order_id}} total ₹{{total_amount}}</p></mj-text></mj-column></mj-section></mj-body></mjml>'
  engine='handlebars'
  variablesSchema='{"required":["customer_name","order_id","total_amount"]}'
  isActive=$true 
} | ConvertTo-Json -Compress
$v = Invoke-RestMethod -Method Post -Uri "$Base/templates/$id/versions" -Headers $Headers -ContentType 'application/json' -Body $verBody
Write-Host "Version created: $($v.id)"

Write-Host "Rendering preview..."
$renderBody = @{ 
  engine='handlebars'
  content='Hello {{customer_name}}, order {{order_id}} total ₹{{total_amount}}'
  variables=@{ customer_name='Rajesh'; order_id='POCM-2025-001234'; total_amount=2499 } 
} | ConvertTo-Json -Compress
$r = Invoke-RestMethod -Method Post -Uri "$Base/templates/$id/render" -Headers $Headers -ContentType 'application/json' -Body $renderBody
Write-Host "Rendered:"
$r | ConvertTo-Json -Compress
