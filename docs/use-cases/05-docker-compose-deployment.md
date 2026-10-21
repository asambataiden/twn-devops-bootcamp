# Use case 05 — AWS EC2 deployment with Docker Compose

- **Historical branch:** [`deployToAWSDokerServer_With_DockerCompose`](https://github.com/asambataiden/twn-devops-bootcamp/tree/deployToAWSDokerServer_With_DockerCompose)
- **Reviewed commit:** [`00ca507b9caf`](https://github.com/asambataiden/twn-devops-bootcamp/tree/00ca507b9cafc6e60b37c56ba33ec0b2eb72be4d)
- **Status:** Compose and pipeline code reviewed; runtime evidence pending.

## Why this use case exists

Explore a declarative multi-service alternative to the earlier single-container `docker run` deployment. The branch retains Maven build, image publishing and remote SSH but switches the EC2 rollout to a Docker Compose project.

## Implemented sequence

The `Deploy to Ec2 with Docker Compose` stage (branch-gated to this exact branch):

1. Creates a dedicated deployment directory on EC2 via SSH.
2. Copies the local `docker-compose.yaml` via `scp`.
3. Creates a remote `.env` containing the application image repository, image tag and active Spring profile.
4. Runs `docker compose --env-file .env -f docker-compose.yaml config --quiet` to validate the resolved Compose configuration.
5. Executes `docker compose pull`, then `docker compose up --detach --remove-orphans`.
6. Displays status using `docker compose ps`.

The Compose manifest defines `java-maven-app` and a PostgreSQL 15.3 service with a named volume. Image reference uses variables from `.env`.

**Do not overclaim:** `depends_on: condition: service_started` does not prove database readiness. Nor does the inspected Java code prove a Spring data-source connection to PostgreSQL; treat the second service as a Compose orchestration exercise unless runtime evidence establishes otherwise.

## Security review

The historical Compose file contains a **hardcoded PostgreSQL password**. The pipelines also bypass SSH/SCP host key checking. Do **not** copy the literal credential into documentation or deploy it beyond a disposable lab. If the password was reused on any real host, rotate it; Git history retains the disclosed value.

## Safe reproduction and evidence

```bash
git switch deployToAWSDokerServer_With_DockerCompose
git rev-parse HEAD
# Inspect filenames and changes before reading any credential-bearing content:
git diff --name-status main...HEAD
```

For a clean-room validation, replace embedded credentials using an approved secrets mechanism, provision verified SSH host keys, and use a disposable EC2 host. Collect redacted Compose config validation, `compose ps`, meaningful application and database checks, deployment image digest and resource cleanup confirmation.

**Open questions:** Was Compose started successfully? Did the app actually connect to the database? Which build demonstrated recovery from a failed rollout?

[Back to main README](../../README.md).
