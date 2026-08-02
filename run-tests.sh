#!/usr/bin/env bash
set -euo pipefail

MVN="./mvnw"
APP_PORT=8080
APP_PID=""

# ── Colours ──────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

info()    { echo -e "${BLUE}[INFO]${NC}  $*"; }
success() { echo -e "${GREEN}[OK]${NC}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*"; }

# ── Menu ─────────────────────────────────────────────────────────────────────
print_menu() {
    echo ""
    echo -e "${BOLD}╔══════════════════════════════════════════╗${NC}"
    echo -e "${BOLD}║        Credit Bladi — Dev Launcher       ║${NC}"
    echo -e "${BOLD}╚══════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "  ${CYAN}1)${NC} Unit tests  — JUnit / Mockito        ${GREEN}(no app needed)${NC}"
    echo -e "  ${CYAN}2)${NC} Karate      — Integration tests       ${YELLOW}(app starts automatically)${NC}"
    echo -e "  ${CYAN}3)${NC} Gatling     — Performance tests       ${RED}(requires app on :${APP_PORT})${NC}"
    echo -e "  ${CYAN}4)${NC} K + G       — Karate then Gatling     ${YELLOW}(app auto-managed)${NC}"
    echo -e "  ${CYAN}5)${NC} Checkstyle  — Architecture & style    ${BLUE}(no app needed)${NC}"
    echo -e "  ${CYAN}6)${NC} Start app   — DEV mode                ${GREEN}(H2 · Swagger · DEBUG logs)${NC}"
    echo -e "  ${CYAN}7)${NC} Start app   — DEV + Remote Debug      ${YELLOW}(JDWP :5005 · suspend=n)${NC}"
    echo -e "  ${CYAN}8)${NC} Dependency-Check — OWASP SCA scan     ${BLUE}(no app needed · downloads NVD data)${NC}"
    echo -e "  ${CYAN}9)${NC} ZAP         — OWASP DAST scan         ${RED}(requires app on :${APP_PORT} · Docker)${NC}"
    echo -e "  ${CYAN}q)${NC} Quit"
    echo ""
}

# ── App helpers ───────────────────────────────────────────────────────────────

app_is_running() {
    nc -z localhost "$APP_PORT" 2>/dev/null
}

start_app_background() {
    if app_is_running; then
        success "App already running on :${APP_PORT} — skipping start."
        return 0
    fi

    info "Starting Credit-Bladi on :${APP_PORT} (profile=dev) ..."

    # DevTools restart disabled to avoid port conflicts when Gatling compiles
    $MVN spring-boot:run \
        -Dspring-boot.run.profiles=dev \
        -Dspring-boot.run.jvmArguments="--enable-preview" \
        -Dspring.devtools.restart.enabled=false \
        -q &
    APP_PID=$!

    local max_wait=90
    local elapsed=0
    while ! app_is_running; do
        if ! kill -0 "$APP_PID" 2>/dev/null; then
            error "App process died unexpectedly — check the logs."
            exit 1
        fi
        if [ $elapsed -ge $max_wait ]; then
            error "App did not start within ${max_wait}s — aborting."
            stop_app
            exit 1
        fi
        sleep 2
        elapsed=$((elapsed + 2))
        echo -ne "  Waiting for app... ${elapsed}s / ${max_wait}s\r"
    done
    echo ""
    success "App is UP on :${APP_PORT}"
}

stop_app() {
    if [ -n "$APP_PID" ]; then
        info "Stopping app (PID $APP_PID)..."
        kill "$APP_PID" 2>/dev/null || true
        wait "$APP_PID" 2>/dev/null || true
        APP_PID=""
        success "App stopped."
    fi
}

# ── Test runners ──────────────────────────────────────────────────────────────

run_unit_tests() {
    echo ""
    info "Running unit tests (JUnit 5 / Mockito) with coverage..."
    echo -e "  ${YELLOW}(package: com.credit.bladi.unittest — no Spring context)${NC}"
    echo ""
    $MVN test -Dtest="com.credit.bladi.unittest.**" -DfailIfNoTests=false

    REPORT="$(pwd)/target/site/jacoco/index.html"
    echo ""
    success "Unit tests done."

    if [ -f "$REPORT" ]; then
        echo ""
        echo -e "  ${CYAN}Coverage report:${NC} file://${REPORT}"
        echo -ne "  Open in browser? [y/N] "
        read -r open_answer
        if [[ "$open_answer" =~ ^[Yy]$ ]]; then
            xdg-open "$REPORT" 2>/dev/null || open "$REPORT" 2>/dev/null || \
                warn "Cannot open browser — navigate to: file://${REPORT}"
        fi
    else
        warn "Coverage report not found — JaCoCo may not be configured."
    fi
}

run_karate() {
    echo ""
    info "Running Karate integration tests..."
    echo -e "  ${YELLOW}(Spring Boot embedded server starts automatically via @SpringBootTest)${NC}"
    echo ""
    $MVN test
    success "Karate tests done. Reports: target/karate-reports/"
}

run_gatling() {
    echo ""
    if app_is_running; then
        success "App is running on :${APP_PORT}"
    else
        warn "App is NOT running on :${APP_PORT}"
        echo -ne "  Start it automatically? [y/N] "
        read -r answer
        if [[ "$answer" =~ ^[Yy]$ ]]; then
            start_app_background
            trap stop_app EXIT
        else
            error "Please start the app first:  ./run-tests.sh  →  option 6"
            return 1
        fi
    fi
    info "Running Gatling performance tests..."
    echo ""
    $MVN gatling:test
    success "Gatling done. Report: target/gatling/"
}

run_both() {
    run_karate
    echo ""
    start_app_background
    trap stop_app EXIT
    run_gatling
}

run_checkstyle() {
    echo ""
    info "Running Checkstyle — verifying Google Java Style compliance..."
    echo -e "  ${YELLOW}(config: checkstyle.xml — naming, indentation, imports, Javadoc, braces)${NC}"
    echo ""
    $MVN checkstyle:check
    success "Checkstyle passed — code style is clean."
}

start_app_dev() {
    echo ""
    info "Starting Credit-Bladi in DEV mode..."
    echo ""
    echo -e "  ${CYAN}Profile  :${NC} dev (H2 in-memory · Liquibase contexts=dev)"
    echo -e "  ${CYAN}Port     :${NC} http://localhost:${APP_PORT}"
    echo -e "  ${CYAN}Swagger  :${NC} http://localhost:${APP_PORT}/swagger-ui.html"
    echo -e "  ${CYAN}H2 console:${NC} http://localhost:${APP_PORT}/h2-console"
    echo -e "  ${CYAN}Logs     :${NC} DEBUG (com.credit.bladi)"
    echo ""
    echo -e "  ${YELLOW}Press Ctrl+C to stop the app.${NC}"
    echo ""

    $MVN spring-boot:run \
        -Dspring-boot.run.profiles=dev \
        -Dspring-boot.run.jvmArguments="--enable-preview"
}

run_dependency_check() {
    echo ""
    info "Running OWASP Dependency-Check — scanning dependencies for known CVEs..."

    local nvd_args=()
    if [ -n "${NVD_API_KEY:-}" ]; then
        success "Using NVD_API_KEY from environment — updates will be much faster."
        nvd_args=(-DnvdApiKey="${NVD_API_KEY}")
    else
        warn "No NVD_API_KEY set — the first run can take a VERY long time (hours)."
        echo -e "  ${YELLOW}Get a free key: https://nvd.nist.gov/developers/request-an-api-key${NC}"
        echo -e "  ${YELLOW}Then export it:  export NVD_API_KEY=xxxx${NC}"
    fi
    echo ""
    $MVN org.owasp:dependency-check-maven:check "${nvd_args[@]}"
    success "Dependency-Check done. Report: target/dependency-check-report.html"
}

check_docker_permissions() {
    if ! command -v docker &>/dev/null; then
        error "Docker is required to run ZAP. Install Docker and retry."
        return 1
    fi

    if docker info &>/dev/null; then
        return 0
    fi

    if getent group docker &>/dev/null && id -nG "$USER" 2>/dev/null | grep -qw docker; then
        error "Docker is installed but this shell session cannot reach the daemon."
        echo -e "  ${YELLOW}Your user is in the 'docker' group but this session hasn't picked it up yet.${NC}"
        echo -e "  ${CYAN}Fix (this terminal only):${NC} run 'newgrp docker' then retry."
        echo -e "  ${CYAN}Fix (permanent):${NC} log out and back in (or reboot)."
    elif getent group docker &>/dev/null; then
        error "Your user is not in the 'docker' group."
        echo -e "  ${CYAN}Fix:${NC} sudo usermod -aG docker \$USER   (then log out and back in)"
    else
        error "Cannot connect to Docker daemon — is it installed and running?"
        echo -e "  ${YELLOW}Try:${NC} sudo systemctl status docker"
    fi
    return 1
}

run_zap() {
    echo ""
    if ! check_docker_permissions; then
        return 1
    fi
    if app_is_running; then
        success "App is running on :${APP_PORT}"
    else
        warn "App is NOT running on :${APP_PORT}"
        echo -ne "  Start it automatically? [y/N] "
        read -r answer
        if [[ "$answer" =~ ^[Yy]$ ]]; then
            start_app_background
            trap stop_app EXIT
        else
            error "Please start the app first:  ./run-tests.sh  →  option 6"
            return 1
        fi
    fi
    info "Running OWASP ZAP DAST scan (config: security/zap-automation.yaml)..."
    echo -e "  ${YELLOW}(active scan is intrusive — only run against this local/dev instance)${NC}"
    echo ""
    local zap_status=0
    ./security/run-zap-scan.sh || zap_status=$?
    if [[ $zap_status -eq 0 ]]; then
        success "ZAP scan done. Report: security/zap-report.html"
    else
        warn "ZAP scan finished with warnings/findings (exit code ${zap_status}). Report: security/zap-report.html"
    fi
    echo -e "  ${CYAN}file://$(cd "$(dirname "$0")" && pwd)/security/zap-report.html${NC}"
}

start_app_debug() {
    echo ""
    info "Starting Credit-Bladi in DEV + DEBUG mode..."
    echo ""
    echo -e "  ${CYAN}Profile  :${NC} dev (H2 in-memory · Liquibase contexts=dev)"
    echo -e "  ${CYAN}Port     :${NC} http://localhost:${APP_PORT}"
    echo -e "  ${CYAN}Debug    :${NC} JDWP listening on :5005 (suspend=n)"
    echo -e "  ${CYAN}Swagger  :${NC} http://localhost:${APP_PORT}/swagger-ui.html"
    echo -e "  ${CYAN}H2 console:${NC} http://localhost:${APP_PORT}/h2-console"
    echo -e "  ${CYAN}Logs     :${NC} DEBUG (com.credit.bladi)"
    echo ""
    echo -e "  ${YELLOW}Attach your debugger on port 5005, then press Ctrl+C to stop.${NC}"
    echo ""

    $MVN spring-boot:run \
        -Dspring-boot.run.profiles=dev \
        -Dspring-boot.run.jvmArguments="--enable-preview -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
}

# ── Main ──────────────────────────────────────────────────────────────────────
print_menu
echo -ne "  Choose [1/2/3/4/5/6/7/8/9/q]: "
read -r choice

case "$choice" in
    1) run_unit_tests        ;;
    2) run_karate            ;;
    3) run_gatling           ;;
    4) run_both              ;;
    5) run_checkstyle        ;;
    6) start_app_dev         ;;
    7) start_app_debug       ;;
    8) run_dependency_check  ;;
    9) run_zap               ;;
    q|Q) info "Bye."; exit 0 ;;
    *) error "Invalid choice: '$choice'"; exit 1 ;;
esac