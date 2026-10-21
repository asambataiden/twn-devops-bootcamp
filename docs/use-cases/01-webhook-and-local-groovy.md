# Use case 01 — Multibranch webhook experiment and local Groovy steps

**Branch:** [`feature/webhook-test`](https://github.com/asambataiden/twn-devops-bootcamp/tree/feature/webhook-test) · **Reviewed commit:** [`45163d240ce8`](https://github.com/asambataiden/twn-devops-bootcamp/tree/45163d240ce8f2a48d7b860baee7e681091f924b) · **State:** code/history reviewed; Jenkins events not yet provided.

## Objective and actual implementation

Explore GitHub-to-Jenkins multibranch integration. A historical commit bears the message [`test: verify multibranch webhook`](https://github.com/asambataiden/twn-devops-bootcamp/commit/45163d240ce8f2a48d7b860baee7e681091f924b). This establishes a recorded exercise, **not independent proof of a successful webhook delivery**.

The branch's `Jenkinsfile` defines `Init`, `Build JAR`, `Build Image` and `Deploy`. `Init` loads `script.groovy` as `gv`, and `Build JAR` calls `gv.buildJar()`, which runs `mvn package`.

The image/push helper in `script.groovy` uses Jenkins credential binding and the Docker CLI; the local `deployApp()` **only prints a message**. Both image and deploy stages use `when { branch 'main' }`, so in a normal multibranch job running `feature/webhook-test` they should be skipped.

## Safe verification

```bash
git switch feature/webhook-test
git rev-parse HEAD
git show --stat 45163d240ce8f2a48d7b860baee7e681091f924b
mvn -B clean verify
```

Record GitHub webhook delivery outcome, Jenkins indexing log, build URL, triggering commit, executed/skipped stages, test results, and any errors. Screenshots/logs must be sanitized.

## Learning questions

What triggered Jenkins to index the branch? Why did image/deploy stages skip it? How is a loaded local Groovy script different from a versioned Shared Library?

[Back to main README](../../README.md).
