<#
.SYNOPSIS
Analytics service runbook (PowerShell).
.DESCRIPTION
Commands to start, build, observe, and test. Each function prints what it does and the expected output.
.REQUIREMENTS
PowerShell 7+, Docker Desktop.
#>

# Paths (auto-detected from this script's folder)
$RepoRoot = Split-Path -Path $PSScriptRoot -Parent
$AnalyticsRoot = Join-Path $RepoRoot "2AnalyticsBackend"
$AnalyticsCompose = Join-Path $AnalyticsRoot "docker-compose.yml"
$ComposeProjectName = "analyticsbackend"

function Start-AnalyticsStack {
    <#
.SYNOPSIS Start the full stack.
.DESCRIPTION Starts all services in background using docker compose.
.EXPECTEDOUTPUT First run: network/images/containers then Up. Repeat: "is up-to-date".
#>
    Write-Host "Start stack... expected: Up or is up-to-date"
docker compose -p "$ComposeProjectName" -f "$AnalyticsCompose" --project-directory "$AnalyticsRoot" up -d
}

function Start-AnalyticsApp {
    <#
.SYNOPSIS Start only the app service.
.DESCRIPTION Starts the app container (deps should already be running).
.EXPECTEDOUTPUT app created/started; repeat shows "is up-to-date".
#>
    Write-Host "Start app... expected: created/started or is up-to-date"
docker compose -p "$ComposeProjectName" -f "$AnalyticsCompose" --project-directory "$AnalyticsRoot" up -d app
}

function Build-AnalyticsImage {
    <#
.SYNOPSIS Build/rebuild the app image.
.DESCRIPTION Builds the image for the app service.
.EXPECTEDOUTPUT Build steps or cache hits; finish without error.
#>
    Write-Host "Build app image..."
docker compose -p "$ComposeProjectName" -f "$AnalyticsCompose" --project-directory "$AnalyticsRoot" build app
}

function Get-AnalyticsLogs {
    <#
.SYNOPSIS Show recent app logs.
.DESCRIPTION Prints logs for the last window.
.PARAMETER Since Time window like 30s,5m,1h.
.EXPECTEDOUTPUT Startup lines, ready/health messages if present.
#>
    param([string]$Since = "30s")
    Write-Host "Logs since $Since..."
(docker compose -p "$ComposeProjectName" -f "$AnalyticsCompose" --project-directory "$AnalyticsRoot" logs --no-log-prefix app --since $Since) | Out-Host
}

function Get-AnalyticsDbTables {
    <#
.SYNOPSIS List DB tables.
.DESCRIPTION Runs psql \dt inside the Postgres container.
.EXPECTEDOUTPUT Table list.
#>
    Write-Host "DB tables (\\dt)..."
    docker exec -i analyticsbackend-postgres-1 psql -U analytics -d analytics -c "\dt"
}

function Describe-AnalyticsTable {
    <#
.SYNOPSIS Describe a DB table.
.DESCRIPTION Shows columns, types, indexes for a table.
.PARAMETER Name Table name like events or batch_operations.
.EXPECTEDOUTPUT Table definition with details.
#>
    param([Parameter(Mandatory = $true)][string]$Name)
    Write-Host "Describe table $Name..."
    docker exec -i analyticsbackend-postgres-1 psql -U analytics -d analytics -c "\d+ $Name"
}

function Test-AnalyticsUnit {
    <#
.SYNOPSIS Run unit tests in Maven container (test profile).
.DESCRIPTION Uses a clean Maven image to run tests.
.EXPECTEDOUTPUT Surefire reports; "[INFO] BUILD SUCCESS" if tests pass.
#>
    Write-Host "Run unit tests (test profile)..."
docker run --rm -e SPRING_PROFILES_ACTIVE=test -v "${AnalyticsRoot}:/workspace" -w /workspace maven:3.9.6-eclipse-temurin-17 mvn -q -DskipTests=false test
}

function Test-AnalyticsIntegration {
    <#
.SYNOPSIS Run integration tests on compose network.
.DESCRIPTION Runs Maven tests with env vars pointing to kafka/redis/postgres/clickhouse.
.EXPECTEDOUTPUT Tests connect via service DNS; "[INFO] BUILD SUCCESS" if pass.
#>
    Write-Host "Run integration tests on compose network..."
docker run --rm --name maven-tests --network analyticsbackend_default -v "/var/run/docker.sock:/var/run/docker.sock" -v "${AnalyticsRoot}:/workspace" -w /workspace -e SPRING_PROFILES_ACTIVE=dev -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 -e SPRING_REDIS_HOST=redis -e SPRING_R2DBC_URL="r2dbc:postgresql://postgres:5432/analytics" -e SPRING_R2DBC_USERNAME=analytics -e SPRING_R2DBC_PASSWORD=analytics -e SPRING_DATASOURCE_URL="jdbc:postgresql://postgres:5432/analytics" -e SPRING_DATASOURCE_USERNAME=analytics -e SPRING_DATASOURCE_PASSWORD=analytics -e SPRING_FLYWAY_URL="jdbc:postgresql://postgres:5432/analytics" -e SPRING_FLYWAY_USER=analytics -e SPRING_FLYWAY_PASSWORD=analytics -e CLICKHOUSE_URL="jdbc:clickhouse://clickhouse:8123/analytics" -e CLICKHOUSE_USERNAME=default -e CLICKHOUSE_PASSWORD= -e KAFKA_TOPIC_PREFIX="pocm.analytics" maven:3.9-eclipse-temurin-17 mvn -q test
}

function Demo-Analytics{
<#
.SYNOPSIS One-shot demo runner for Analytics.
.DESCRIPTION Runs build, stack start, logs, DB checks, unit and integration tests in order.
#>
Write-Host "Demo: build image"
Build-AnalyticsImage
Write-Host "Demo: start stack"
Start-AnalyticsStack
Write-Host "Demo: recent logs"
Get-AnalyticsLogs -Since 60s
Write-Host "Demo: DB schema"
Get-AnalyticsDbTables
Describe-AnalyticsTable -Name events
Describe-AnalyticsTable -Name batch_operations
Write-Host "Demo: unit tests"
Test-AnalyticsUnit
Write-Host "Demo: integration tests"
Test-AnalyticsIntegration
}

Write-Host "Loaded Analytics runbook. Functions:" -ForegroundColor Cyan
' - Start-AnalyticsStack',' - Start-AnalyticsApp',' - Build-AnalyticsImage',' - Get-AnalyticsLogs [-Since 30s]',' - Get-AnalyticsDbTables',' - Describe-AnalyticsTable -Name events',' - Test-AnalyticsUnit',' - Test-AnalyticsIntegration',' - Demo-Analytics'|ForEach-Object{Write-Host $_}
