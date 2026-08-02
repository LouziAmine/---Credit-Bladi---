#!/usr/bin/env bash
# Runs the OWASP ZAP baseline/active scan defined in zap-automation.yaml against a
# running instance of Credit Bladi. Start the app first:
#   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

docker run --rm \
  --add-host=host.docker.internal:host-gateway \
  -v "${SCRIPT_DIR}:/zap/wrk/:rw" \
  -t ghcr.io/zaproxy/zaproxy:stable \
  zap.sh -cmd -autorun /zap/wrk/zap-automation.yaml
