# Reproducing the Jenkins Bootcamp exercises

**Scope:** safe reproduction procedure. **Not a report of successful execution.**

## 1. Select the exact exercise

Each branch has a distinct Jenkinsfile. Select a named branch before running commands:

```bash
git clone https://github.com/asambataiden/twn-devops-bootcamp.git
cd twn-devops-bootcamp
git fetch --all --prune
git branch -r
git switch main
git rev-parse HEAD
```

Record the SHA, date, host OS and versions before reproducing. Do not rewrite history or replace historical Jenkinsfiles just to align them.

## 2. Local Java build validation

This is the lower-risk first step:

```bash
java -version
mvn -version
mvn -B clean verify
find target -maxdepth 1 -type f -name '*.jar' -print
```

The source declares Java 17 and Maven-related settings. Record any compilation, dependency or test failures accurately. Do not assume a successful historical run.

## 3. Jenkins configuration dependencies

Before a pipeline test, verify (without revealing credentials):

- Jenkins controller and agent versions, installed plugins and agent Docker/Maven access.
- A Maven tool installation with the **exact name** `maven-3.9`.
- A global Shared Library named `jenkins-shared-library` with an authorized SCM source and pinned, reviewed revision.
- Jenkins credential IDs `docker-hub-repo-asambataiden` and `aws-ec2-docker-server-ssh`; the version-commit branch also references `github-credentials`.
- A multibranch pipeline if using the `when { branch ... }` conditions.
- Access to an authorized Docker Hub repository and disposable EC2 target.

Never paste PATs, passwords, SSH private keys or Jenkins configuration exports into Git.

## 4. Build and image flow

The shared `buildJar()` calls `mvn clean package`. The Docker helper builds the image and logs in via Jenkins credential binding and `docker login --password-stdin`.

To understand the image locally, inspect `Dockerfile` and only then, if you choose, run:

```bash
mvn -B clean verify
docker build -t demo-app:local-check .
docker image inspect demo-app:local-check --format '{{.Id}}'
```

These are **validation commands to run**, not observed output. Running the container requires checking the actual application startup behavior.

## 5. EC2 deployment prerequisites

- Dedicated non-production EC2 instance with Docker, sufficient disk and restricted network access.
- Explicit trust for the host's authentic SSH key in the Jenkins agent's `known_hosts`.
- A least-privilege Jenkins SSH credential and correctly scoped Docker Hub token on the target.
- A clear rollback image reference and safe teardown plan.

The original stage includes `StrictHostKeyChecking=no`, which bypasses host identity validation. **Do not reproduce that flag outside the historical study environment.**

## 6. Branch variants

- **Direct Docker branch:** pull, remove existing container and launch a replacement.
- **Compose branch:** transfer Compose definition; write non-secret environment selection; validate/pull/reconcile services. The inspected Compose file includes an inline PostgreSQL password, which must be replaced with secured provisioning before executing in a shared environment.
- **Version-commit branch:** mutates `pom.xml` and pushes updates; test in a controlled branch with restricted write credentials.

See [the branch use-case guides](../README.md#5-branches-and-distinct-use-cases) for precise differences.

## 7. Cleanup

After tests, stop disposable containers, delete cloud resources created solely for learning, review Docker Hub images, and record the final teardown. Do not delete resources belonging to other projects. AWS resources can incur charges while idle.
