# Use case 02 — Jenkins Shared Library integration

**Branch:** [`jenkins-shared-library`](https://github.com/asambataiden/twn-devops-bootcamp/tree/jenkins-shared-library) · **Reviewed commit:** [`7cfd5b9392b`](https://github.com/asambataiden/twn-devops-bootcamp/tree/7cfd5b9392b644ae1b5f7e4d5843e951aa0430ec) · **State:** code reviewed; pipeline execution unverified.

## Purpose

Move repeated pipeline operations out of the application repository and into a reusable version-controlled Jenkins Shared Library.

The branch's `Jenkinsfile` references `@Library('jenkins-shared-library')`, calls `buildJar()`, `buildImage`, `dockerLogin()` and `dockerPush` from the library, and retains local `script.groovy` for a demo `deployApp()` function.

The helper functions exist in [the separate Shared Library repository](https://github.com/asambataiden/jenkins-shared-library). The `Docker.groovy` class wraps Docker build, credentials-bound login and push. The library `deployApp.groovy` is a **logging placeholder**, not an EC2 deployer.

**Important:** The branch-specific Jenkinsfile gates image building and deployment on `main`, so checking out this historical feature branch does not establish that those gated stages ran. The exact `@Library` annotation and library resolution should be checked in Jenkins rather than assumed valid.

## Validation plan

1. Verify that Jenkins has a configured Global Pipeline Library named `jenkins-shared-library`, sourced from the intended repository/revision.
2. Run this branch in an isolated multibranch job and capture the library resolution logs.
3. Record which stages execute and which are skipped.
4. Check that registry credentials are masked and have only necessary permissions.

The original library is not necessarily pinned to an immutable commit; document the actual version Jenkins used.

See [Shared Library integration](../shared-library.md) and [main README](../../README.md).
