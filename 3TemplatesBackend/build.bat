@echo off
REM Build the templates-backend using Dockerized Maven (no local Maven needed, load of installing on windows loll)
setlocal enabledelayedexpansion
set PWD=%~dp0
docker run --rm --name templates-mvn -v "%PWD%":/workspace -w /workspace maven:3.9-eclipse-temurin-17 mvn -q -DskipTests package
