# Contributing to Credit Bladi

Thanks for taking the time to contribute. This document explains how to propose a change, what's
expected before opening a pull request, and the coding standards enforced in this repository.

## Code of Conduct

This project and everyone participating in it is governed by the
[Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold it.

## Getting set up

Follow the [Quick Start](README.md#quick-start) of the README to install the prerequisites and run the
application locally.

## Workflow

1. Fork the repository and create your branch from `master`.
2. Name your branch after what it does, e.g. `feature/mourabaha-early-repayment` or
   `fix/taeg-rounding`.
3. Make your change, keeping it focused on a single concern.
4. Add or update tests that cover the change.
5. Run the full check suite locally before opening a pull request (see below).
6. Open a pull request against `master` with a clear description of the change and why it's
   needed.

## Before opening a pull request

Run these from the repository root; `./run-tests.sh` wraps all of them in an interactive menu:

```bash
# Code style: must pass with zero violations
./mvnw checkstyle:check

# Unit tests (H2, no Docker needed)
./mvnw test -Dtest='!IntegrationTest'

# Karate integration tests (H2, needs a free port 8080 or -Dserver.port=...)
./mvnw test -Dtest=IntegrationTest
```

A pull request that fails Checkstyle or the test suite will not be merged until it's fixed.

## Code style

- Java code follows **Google Java Style**, enforced by Checkstyle (`checkstyle.xml`); run
  `./mvnw checkstyle:check` to verify before pushing.
- Keep the layered architecture documented in [Architecture](docs/architecture.md#layers-and-dependency-rule):
  `model/` imports no framework; `service/` never imports `infrastructure/`, `datasource/` or
  `controller/` (external systems go behind a port in `service/`, implemented in `infrastructure/`);
  controllers never touch repositories.
- New business rules belong on the domain model (`model/`) as validating constructors/builders or
  methods, not re-implemented against raw getters/setters in a service, see
  [Domain-Driven Design](docs/architecture.md#domain-driven-design).
- No comments in the source code (`src/main` and `src/test`): names carry the meaning, and the business
  rationale (loi 31-08, TMIC, four-eyes...) lives in `docs/`. Configuration files (`.properties`, YAML, Docker,
  CI) may explain their settings. Commit messages are in English.
- No literal business values in tests: they come from `src/test/java/com/credit/bladi/unittest/mock/data/`.
- Log messages are in English. Every message a client or manager can read (API error `description`,
  notifications, emails, SMS) goes through an i18n key with its `.properties` / `_fr` / `_en` / `_ar` files, 
  never a hard-coded sentence. API responses follow the request's `Accept-Language`; notifications follow the
  recipient's stored language (French when unknown).

## Commit messages

Write commit messages that explain **why** a change was made, not just what changed. Keep the
first line under ~70 characters; add a body if the reasoning needs more space.

## Reporting bugs / requesting features

Open a GitHub issue with:
- A clear description of the problem or the feature request
- Steps to reproduce (for bugs), including request payloads if relevant to the simulation engine
  or BAM parameter endpoints
- What you expected to happen vs. what actually happened

## Questions

If anything here is unclear, open an issue or write to the contact in the [README](README.md#license).
