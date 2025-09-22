@echo off
REM Simplified Gradle wrapper bootstrapper that delegates to a locally installed Gradle.
setlocal
set DIR=%~dp0
gradle %*
