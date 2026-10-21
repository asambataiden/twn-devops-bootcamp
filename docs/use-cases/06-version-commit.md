# Use case 06 — Automated Maven version commits

- **Historical branch:** [`commitVersionUpdate`](https://github.com/asambataiden/twn-devops-bootcamp/tree/commitVersionUpdate)
- **Reviewed commit:** [`26045a9a72da`](https://github.com/asambataiden/twn-devops-bootcamp/tree/26045a9a72da61469de55e2f2e2d15e98b8313a9)
- **Recorded commit:** [`chore(release): bump version to 1.1.12`](https://github.com/asambataiden/twn-devops-bootcamp/commit/26045a9a72da61469de55e2f2e2d15e98b8313a9)
- **Status:** Git version-bump commit is verified; Jenkins execution provenance requires logs.

## Learning objective

Experiment with having Jenkins calculate a Maven version, stage the changed `pom.xml` and record a release-oriented commit back to a dedicated branch.

## Source-defined behavior

The Jenkinsfile includes `Commit Version Update`, with `git config`, `git add pom.xml`, a staged-diff check and conditional `git commit`/`git push`, using Jenkins-managed Git credentials.

**Significant observation:** In this branch's current Jenkinsfile, the image/push, deployment **and commit-version stages** use `when { branch 'deployToAWSDokerServer_With_DockerCompose' }`. A correctly configured multibranch run on **`commitVersionUpdate`** would skip those stages. The commit itself exists, but that does not prove the checked-in final Jenkinsfile produced it.

The `main` Jenkinsfile contains a stage gated for `commitVersionUpdate`, but each branch normally evaluates its **own Jenkinsfile**. This divergence makes current behavior branch-dependent.

## How to investigate accurately

1. Inspect commits preceding `26045a9a72da` and the associated Jenkins job history.
2. Match the version-bump commit time/SHA to a build URL, stage log and checked-out Jenkinsfile revision.
3. Test corrected branch conditions **only in a separate improvement branch**.
4. Protect against Git push → webhook → Jenkins feedback loops.
5. Use scoped Git credentials and branch protections.

**Do not rewrite the original commits** to make the history look cleaner. This discrepancy is useful troubleshooting and engineering-learning evidence.

[Back to main README](../../README.md).
