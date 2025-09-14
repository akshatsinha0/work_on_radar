param(
  [switch]$NoStart,
  [switch]$TestClickHouse
)

$ErrorActionPreference = "Stop"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$root = Resolve-Path (Join-Path $scriptDir ".")
$dotenv = Join-Path $root ".env"

function Set-EnvFromDotEnv($path){
  if(!(Test-Path $path)){ throw ".env not found at $path" }
  Get-Content -Path $path | ForEach-Object {
    $line = $_.Trim()
    if([string]::IsNullOrWhiteSpace($line)){ return }
    if($line.StartsWith('#')){ return }
    if($line -match '^(?<k>[A-Za-z_][A-Za-z0-9_\.\-]*)\s*=\s*(?<v>.*)$'){
      $k=$Matches['k']
      $v=$Matches['v']
      if(($v.StartsWith('"') -and $v.EndsWith('"')) -or ($v.StartsWith("'") -and $v.EndsWith("'"))){ $v=$v.Substring(1,$v.Length-2) }
      $v=[regex]::Replace($v,'\${([^}]+)}',{ param($m) [Environment]::GetEnvironmentVariable($m.Groups[1].Value,'Process') })
      [Environment]::SetEnvironmentVariable($k,$v,'Process')
    }
  }
}

function Print-EnvSummary(){
  $safeKeys = @('CH_HOST','CH_PORT','CH_USER','CLICKHOUSE_URL','SPRING_R2DBC_URL','SPRING_KAFKA_BOOTSTRAP_SERVERS')
  foreach($k in $safeKeys){
    $val = [Environment]::GetEnvironmentVariable($k,'Process')
    if($val){ Write-Host "$k=$val" }
  }
}

function Test-ClickHouse(){
  if([string]::IsNullOrWhiteSpace($env:CH_HOST) -or [string]::IsNullOrWhiteSpace($env:CH_PORT)){
    throw "CH_HOST/CH_PORT not set"
  }
  if([string]::IsNullOrWhiteSpace($env:CH_USER) -or [string]::IsNullOrWhiteSpace($env:CH_PASSWORD)){
    throw "CH_USER/CH_PASSWORD not set. Edit .env and set CH_PASSWORD first."
  }
  $pair = "$($env:CH_USER):$($env:CH_PASSWORD)"
  $basic = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes($pair))
  $uri = "https://$($env:CH_HOST):$($env:CH_PORT)/"
  $resp = Invoke-RestMethod -Method Post -Uri $uri -Headers @{ Authorization = "Basic $basic" } -Body "SELECT 1"
  Write-Host "ClickHouse SELECT 1 => $resp"
}

# 1) Load .env
Set-EnvFromDotEnv $dotenv
Print-EnvSummary

# 2) Optionally test ClickHouse
if($TestClickHouse){ Test-ClickHouse }

# 3) Start app unless --NoStart
if(-not $NoStart){
  # Use Maven to run app
  if(Get-Command mvn -ErrorAction SilentlyContinue){
    mvn -q spring-boot:run
  } else {
    Write-Warning "mvn not found on PATH; skipping app start."
  }
}

