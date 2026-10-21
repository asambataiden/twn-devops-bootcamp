# Lessons learned and engineering progression

> **Note:** These are technical lessons suggested by code review, not a claim that the learner personally reported each one. Confirm and personalize this section before using it in the TWN assessment.

## What the repo demonstrates

1. **Declarative pipeline boundaries:** build, package, publish, deploy and SCM mutations have different risk profiles and should have explicit branch policies.
2. **Reusable pipeline code:** shared Groovy steps reduce duplication, but version pinning, unit tests and limited credentials remain important.
3. **Traceable artifact versions:** Maven versions and Jenkins build numbers improve identification; content-addressed image digests make deployment stronger.
4. **Cloud operations:** remote Docker deployment is a useful exercise in provisioning, SSH and container lifecycle, but mutable EC2 hosts require automation and recovery plans.
5. **Compose versus single-container deployment:** Compose models more than one service and desired state; dependencies alone do not imply service readiness.
6. **Git history as evidence:** separate branches reveal iterative learning, including failed experiments and improvements.

## Questions to answer in your own words

- What did the initial webhook experiment reveal about Jenkins branch indexing?
- Which problems led you to introduce a Jenkins Shared Library?
- How did Maven version increments affect Docker image names and Git commits?
- What SSH or Docker errors did you encounter deploying to EC2, and how did you diagnose them?
- Why did you move from `docker run` to Compose, and what did that add?
- Which Jenkins build numbers or deployment screenshots demonstrate working results?
- What would you change now to meet production security and reliability requirements?

## Candidate production capstone (future work; not implemented)

A separate project could combine branch protection, automated unit/integration tests, immutable artifact builds, registry image signing/scanning, least-privilege deployment, health-gated rollout, automated rollback, secret management and Prometheus/Grafana monitoring.

Do **not** retroactively mark those features as completed in this bootcamp repository.
