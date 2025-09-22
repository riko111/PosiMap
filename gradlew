#!/usr/bin/env sh
# Simplified Gradle wrapper bootstrapper that delegates to a locally installed Gradle.
# This placeholder exists because network access is unavailable when generating the
# standard Gradle wrapper distribution.
DIR="$(cd "$(dirname "$0")" && pwd)"
exec gradle "$@"
