# Validation and evidence ledger

**Baseline date:** 2026-10-21. All runtime statuses below refer to **evidence inspected for this documentation**, not to whether a past exercise may have worked on the learner's machines.

| Capability | Reviewed source | Live/build evidence | Next evidence to capture |
|---|---|---|---|
| Java source and test | `Application.java`, `AppTest.java` | Not supplied | `mvn -B clean verify` output; command, date, commit |
| Maven packaging | `pom.xml` and `buildJar.groovy` | Not supplied | JAR build logs and artifact SHA |
| GitHub webhook | `feature/webhook-test` and history | Commit `test: verify multibranch webhook`; no webhook delivery record | Jenkins branch indexing/trigger events |
| Shared library | `jenkins-shared-library` branch and external library code | No successful Jenkins stage log | Jenkins library revision and execution |
| Maven version bump | `incrementAppVersion` branch | No execution log supplied | Before/after POM and Jenkins stage result |
| Docker image publish | Jenkins `dockerPush` helper | No matching historical CI log | Redacted build/push logs; image digest |
| EC2 via Docker | `deployToAWSDockerServer` | No current execution proof | Redacted `docker ps`, app probe, registry digest |
| EC2 via Compose | `deployToAWSDokerServer_With_DockerCompose` | No current execution proof | `docker compose config`, `ps`, health verification |
| Commit version update | `commitVersionUpdate` branch and its Git commits | Commit `26045a9` shows version bump; pipeline causality not fully proven | Jenkins build URL/log identifying the commit |
| Main integrated pipeline | `main` Jenkinsfile | No Jenkins stage/console log supplied | Success or failure evidence for each stage |

## Evidence collection format

For every manual or CI validation, record:

```text
Date (UTC):
Repository and exact commit SHA:
Branch:
Target environment:
Tool versions:
Command / pipeline run:
Expected behavior:
Observed result:
Log or screenshot path (sanitized):
Issues and resolution:
Reviewer:
```

### Evidence safeguards

- Never share console logs containing exposed environment variables, access tokens, passwords, IP allowlists or private SSH material.
- Remove or redact private host details when they add no assessment value.
- Do not equate a Git commit labeled "fix" or "test" with a passing test.
- Preserve failed tests: root-cause analysis and the eventual fix are legitimate DevOps evidence.
- For TWN Credly, pair links with **your own explanation** of design, failure investigation and lessons learned.

## Recommended validation order

1. Compile/test Java locally.
2. Validate Groovy pipeline configuration against installed Jenkins plugin versions.
3. Confirm shared library execution and protected registry authentication.
4. Test Docker image startup locally.
5. Use a disposable EC2 test target and authenticated SSH host keys.
6. Record negative cases, failed deployment behavior and cleanup.
7. Upgrade portfolio status only after proof is inspected.
