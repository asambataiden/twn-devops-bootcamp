# Jenkins Shared Library — code review and integration

**Library repository:** [`asambataiden/jenkins-shared-library`](https://github.com/asambataiden/jenkins-shared-library) · **Code snapshot:** [`2873abfc37c8`](https://github.com/asambataiden/jenkins-shared-library/tree/2873abfc37c87a3abbfe1075aa491e9aa235ef91).

## Structure

The library uses the customary Jenkins Shared Library layout:

| File | Observed role |
|---|---|
| `vars/buildJar.groovy` | Runs `mvn clean package` |
| `vars/buildImage.groovy` | Delegates to `Docker.buildDockerImage` |
| `vars/dockerLogin.groovy` | Delegates to `Docker.dockerLogin` |
| `vars/dockerPush.groovy` | Authenticates and pushes an image |
| `vars/deployApp.groovy` | Only emits a log message — **placeholder** |
| `src/com/example/Docker.groovy` | Wraps `docker build`, `docker login` and `docker push` |

The Docker helper uses Jenkins' `withCredentials` mechanism with credential ID `docker-hub-repo-asambataiden` and invokes `docker login --password-stdin`. The ID is a configuration reference, not a secret.

## Integration dependency

Jenkins must be configured with a Global Pipeline Library named `jenkins-shared-library`, connected to the intended SCM repository and revision. A successful import or pipeline execution has not yet been evidenced. Record exactly which library commit a tested Jenkins build used.

## Quality and security considerations

- Prefer pinning approved library revisions instead of following uncontrolled changes.
- Keep registry permissions read-only unless publishing is required, and rotate credentials.
- Add pipeline-library unit tests before reusing shared steps in critical builds.
- Replace the placeholder `deployApp()` only in a separate functional improvement.
- Review large local Groovy JAR artifacts in the library's `lib/` directory for reproducibility and repository hygiene.

This is the distinction between the early **in-repository `script.groovy`** exercise and the later reusable CI abstraction.

[Back to main README](../README.md).
