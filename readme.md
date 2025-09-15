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
Docker desktop engine should be running, assumed
*/