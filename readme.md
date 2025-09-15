# Run demos (PowerShell 7+)

Run these commands from the repository root (the folder that contains for example `8CommandCenter`).

Templates (build → start → logs → health → rebuild+smoke):

```
pwsh -NoProfile -Command ". .\8CommandCenter\templates.ps1; Demo-Templates"
```

Analytics (build image → start stack → logs → DB checks → unit + integration tests):

```
pwsh -NoProfile -Command ". .\8CommandCenter\analytics.ps1; Demo-Analytics"
```
/*
Docker desktop engine should be running, assumed; & dockerized mvn.....
*/

Seed Analytics with dummy events (HTTP ingest to the app on :8085):

```
pwsh -NoProfile -File .\\8CommandCenter\\seed_analytics.ps1
```

After seeding, show proof points for the demo:

- Health (note: Redis may be DOWN locally; Postgres/disk/ping should be UP)
```
Invoke-RestMethod -SkipHttpErrorCheck 'http://localhost:8085/actuator/health' | ConvertTo-Json -Depth 5
```

- Ingest endpoint metrics (successful POSTs count)
```
Invoke-RestMethod 'http://localhost:8085/actuator/metrics/http.server.requests?tag=uri:/analytics/ingest&tag=method:POST' | ConvertTo-Json -Depth 6
```

- Latest 5 events with payload snippet
```
docker exec -i analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select type,occurred_at,left(data_json::text,120) as data_snippet from events order by occurred_at desc limit 5;"
```

- Events per minute over last 10 minutes
```
docker exec -i analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select to_char(date_trunc('minute',received_at),'HH24:MI') as minute,count(*) from events where received_at>now()-interval '10 minutes' group by 1 order by 1;"
```

- Total by type (optional closing slide)
```
docker exec -i analyticsbackend-postgres-1 psql -U analytics -d analytics -c "select type,count(*) from events group by 1 order by 2 desc;"
```
