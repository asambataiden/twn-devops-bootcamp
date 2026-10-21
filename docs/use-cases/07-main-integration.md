# Use case 07 — Main Jenkins CI/CD integration

- **Current historical baseline:** [`main` at `34d423c01097`](https://github.com/asambataiden/twn-devops-bootcamp/tree/34d423c01097f65149837cc216c8f15e51605aa5)
- **Status:** pipeline and Shared Library code inspected; actual build/deployment evidence pending.

## Purpose

The default branch represents an integration point after the earlier exercises, combining a Maven application build, version calculation, Docker image publishing and direct EC2 Docker deployment.

## Pipeline as written

| Stage | Behavior | Branch condition |
|---|---|---|
| `Increment Version` | Set and evaluate Maven patch version | All branches |
| `Build App` | Shared `buildJar()` invoking `mvn clean package` | All branches |
| `Build and Push Image` | Tag `asambataiden/demo-app` with version and Jenkins build number; build and push | `main` |
| `Deploy` | Remote Docker pull/remove/run with Jenkins SSH credential | `main` |
| `Commit Version Update` | Stage, commit and push `pom.xml` if changed | `commitVersionUpdate` |

The `Commit Version Update` stage is **skipped on `main`**, and the similarly named historical branch contains different gate conditions. Main-branch Maven changes to the build workspace are therefore not necessarily committed.

## Tested functionality versus assumptions

The Java project includes a unit test calling `Application.getStatus()` and asserting `OK`. That method is **not an exposed HTTP health endpoint** in the reviewed code. Jenkins post-build success logs and `docker ps` also do not establish service readiness. No full CI pipeline execution has yet been reviewed for this documentation.

## Security/operational gaps

- SSH currently uses `StrictHostKeyChecking=no`.
- No verified deployment readiness, rollback, health checks or release gates.
- Image tags are mutable, no pinned digest is recorded by the pipeline.
- EC2 host is configured as a literal target rather than an infrastructure-inventory variable.
- Shared Library is referenced by name; the actual Jenkins-resolved library revision must be recorded.

## Evidence to capture

A sanitized main-branch Jenkins build/stage report, source SHA, Maven test summary, registry image digest, EC2 deployment log, **application readiness observation**, and cleanup/recovery procedure. Report failures without concealing them.

[Back to main README](../../README.md).
