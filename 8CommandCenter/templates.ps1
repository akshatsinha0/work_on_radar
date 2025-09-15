<#
.SYNOPSIS
Template Microservice runbook (PowerShell).
.DESCRIPTION
Build, run, logs, health, and rebuild+smoke for 3TemplatesBackend.
.REQUIREMENTS
PowerShell 7+, Docker Desktop.
#>

# Paths (auto-detected from this script's folder)
$RepoRoot=Split-Path -Path $PSScriptRoot -Parent
$TemplatesRoot=Join-Path $RepoRoot "3TemplatesBackend"
$TemplatesCompose=Join-Path $TemplatesRoot "docker-compose.yml"
$SmokeScript=Join-Path $TemplatesRoot "scripts\smoke.ps1"

function Build-Templates{
<#
.SYNOPSIS Build with Maven in Docker (skip tests).
.DESCRIPTION Uses maven:3.9-eclipse-temurin-17 to package the app.
.EXPECTEDOUTPUT Lines ending with "[INFO] BUILD SUCCESS"; jar in target/.
#>
Write-Host "Build Template service (skip tests)..."
docker run --rm --name templates-mvn -v "${TemplatesRoot}:/workspace" -w /workspace maven:3.9-eclipse-temurin-17 mvn -q -DskipTests package
}

function Start-Templates{
<#
.SYNOPSIS Start stack and show status.
.DESCRIPTION Brings up services in background and lists their status.
.EXPECTEDOUTPUT Services show Up/healthy; repeat shows is up-to-date.
#>
Write-Host "Start Template stack and show status..."
docker compose -f "$TemplatesCompose" up -d
docker compose -f "$TemplatesCompose" ps
}

function Get-TemplatesLogs{
<#
.SYNOPSIS Tail recent logs for templates-app.
.DESCRIPTION Prints last 200 lines without prefixes.
.EXPECTEDOUTPUT Spring Boot startup; "Started ..."; listening on 8086.
#>
Write-Host "Tail last 200 logs for templates-app..."
docker compose -f "$TemplatesCompose" logs templates-app --tail=200 --no-log-prefix
}

function Test-TemplatesHealth{
<#
.SYNOPSIS Call /health endpoint.
.DESCRIPTION Waits briefly then fetches http://localhost:8086/health.
.EXPECTEDOUTPUT JSON like {"status":"UP"}.
#>
Write-Host "Health check on http://localhost:8086/health ..."
Start-Sleep -Seconds 3
Invoke-WebRequest -UseBasicParsing http://localhost:8086/health | Select-Object -ExpandProperty Content
}

function Up-Templates{
<#
.SYNOPSIS Idempotent compose up.
.DESCRIPTION Ensures services are running; safe to re-run.
.EXPECTEDOUTPUT Up-to-date on repeats.
#>
Write-Host "Compose up (idempotent)..."
docker compose -f "$TemplatesCompose" up -d
}

function RebuildAndSmoke-Templates{
<#
.SYNOPSIS Rebuild images and run smoke tests.
.DESCRIPTION Builds images if needed, starts services, runs smoke script.
.EXPECTEDOUTPUT Build ok, services Up, smoke script success.
#>
Write-Host "Rebuild images and run smoke tests..."
docker compose -f "$TemplatesCompose" up -d --build
& "$SmokeScript"
}

function Demo-Templates{
<#
.SYNOPSIS One-shot demo runner for Templates.
.DESCRIPTION Runs build, start, logs, health, then rebuild+smoke in order.
#>
Write-Host "Demo: build"
Build-Templates
Write-Host "Demo: start"
Start-Templates
Write-Host "Demo: logs"
Get-TemplatesLogs
Write-Host "Demo: health"
Test-TemplatesHealth
Write-Host "Demo: rebuild+smoke"
RebuildAndSmoke-Templates
}

Write-Host "Loaded Templates runbook. Functions:" -ForegroundColor Cyan
' - Build-Templates',' - Start-Templates',' - Get-TemplatesLogs',' - Test-TemplatesHealth',' - Up-Templates',' - RebuildAndSmoke-Templates',' - Demo-Templates'|ForEach-Object{Write-Host $_}
