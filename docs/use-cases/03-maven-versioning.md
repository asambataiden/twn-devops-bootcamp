# Use case 03 — Maven version increment and Docker image tag composition

**Branch:** [`incrementAppVersion`](https://github.com/asambataiden/twn-devops-bootcamp/tree/incrementAppVersion) · **Reviewed commit:** [`54089b679013`](https://github.com/asambataiden/twn-devops-bootcamp/tree/54089b67901323153b0903fb7e6c85e5bc1829f0).

## Implemented workflow

The `Increment Version` pipeline stage invokes Maven build-helper/versions goals to calculate a patch version and update `pom.xml` in the build workspace. It reads `project.version` into the pipeline environment variable `APP_VERSION`. The `Build App` stage runs `buildJar()` from the Shared Library.

The image-building stage constructs this tag pattern:

```text
asambataiden/demo-app:<APP_VERSION>-<BUILD_NUMBER>
```

**Branch condition:** the image/push stage uses `when { branch 'main' }` in this snapshot. It is therefore normally skipped when the `incrementAppVersion` branch is built. The `Deploy` stage also only echoes a message, not an actual remote deployment.

## Verification and evidence

```bash
git switch incrementAppVersion
git rev-parse HEAD
mvn -B clean verify
mvn help:evaluate -Dexpression=project.version -q -DforceStdout
```

Reproduce the version-setting step in a disposable checkout. Capture the version before and after the step, the exact Maven and Jenkins versions, output logs and final tag.

**Status:** source reviewed; historical run success and image publication unverified. Image tags can be overwritten; digest-based references are a future improvement.

[Back to main README](../../README.md).
