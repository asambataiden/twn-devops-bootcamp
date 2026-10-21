# Security review — Jenkins Bootcamp historical implementation

**Scope:** source review only, 2026-10-21. This file documents risks; this documentation PR intentionally does not modify executable code.

| Finding | Evidence in original exercises | Risk | Recommended remediation |
|---|---|---|---|
| SSH trust bypass | `StrictHostKeyChecking=no` in deployment Jenkinsfiles | MITM/host impersonation | Pre-provision verified SSH host keys, disallow unverified hosts |
| Hardcoded database password | `docker-compose.yaml` on the Compose branch | Unauthorized access and credential reuse | Rotate if reused; store in managed secrets; provision without plaintext in Git |
| Mutable deployment tags | Docker images use version + Jenkins build number, tags rather than pinned digests | Tags can be overwritten | Resolve/pin digest and verify provenance |
| No application readiness gate | `docker ps`/Compose `ps` are used after deployment | Process may run but app may fail | Add health endpoint/probe and bounded readiness checks |
| No failure rollback | Existing container removed before replacement; Compose up without rollback | Downtime following a bad image | Versioned rollback target and health-gated deployment |
| Remote SSH deployment | Jenkins agent SSHs directly to mutable host | Broad access, key handling, drift | Least privilege, inventory, audited provisioning, IaC |
| No demonstrated security scanning | Jenkinsfile has no source/image scanning stage | Vulnerabilities missed | Add SAST/dependency/image scanning and PR policies |
| Java runtime image | Dockerfile uses a JDK base image | Larger attack surface than necessary | Evaluate trusted minimal Java 17 runtime, SBOM and digest pinning |
| Shared library version not pinned in Jenkinsfile | `@Library('jenkins-shared-library')` | Upstream changes affect builds | Version-pin or govern approved library revisions |

## Credential handling: positive observation

The inspected [Jenkins Shared Library](https://github.com/asambataiden/jenkins-shared-library) binds Docker Hub credentials via Jenkins `withCredentials` and authenticates using `docker login --password-stdin`. This is preferable to placing a token literally in the pipeline file. Validate credential scope, account permissions, log masking and token lifecycle before production use.

## Priority response

1. If the literal database password in the Compose branch was ever used beyond a disposable learning environment, rotate it. It is already in Git history and must be treated as exposed.
2. Do not expose Jenkins publicly over unauthenticated/plain HTTP or grant it excessive permissions.
3. Verify real EC2 SSH host keys before re-running deployment.
4. Keep this documentation truthful: historical weaknesses are learning evidence, but not production best practices.

## Follow-up engineering changes

Changes to Jenkinsfiles, credentials and Compose manifests should be proposed as **separate functional pull requests** after the documentation baseline is reviewed. This retains accurate historical comparison and makes rollback straightforward.
