# Use case 04 — Jenkins deployment to AWS EC2 with Docker

**Branch:** [`deployToAWSDockerServer`](https://github.com/asambataiden/twn-devops-bootcamp/tree/deployToAWSDockerServer) · **Reviewed commit:** [`8c67eaef96c2`](https://github.com/asambataiden/twn-devops-bootcamp/tree/8c67eaef96c21f2983f546bd8dbcba3b488f84f3) · **State:** implementation reviewed; deployment result unverified.

## Purpose and actual behavior

After versioning/building and publishing the Docker image, the branch executes a deployment stage gated on `when { branch 'deployToAWSDockerServer' }`.

The stage uses Jenkins `sshagent(credentials: ['aws-ec2-docker-server-ssh'])` to connect as `ec2-user` to the configured EC2 target. It runs `docker pull`, removes any old `demo-app` container, starts the new container on port 8080 using `--restart unless-stopped`, sleeps briefly and checks `docker ps`.

This is a genuine **deployment script**, but the `docker ps` check does not prove HTTP readiness. The replacement sequence can cause downtime and has no automatic rollback.

## Security and troubleshooting

The branch history includes a commit relating to an SSH `known_hosts`/host-verification failure. The inspected stage works around SSH host verification using `StrictHostKeyChecking=no`. This is **not recommended for a production implementation**: configure verified host keys and fail on unexpected changes.

No actual private key or host address is needed in this guide.

## Evidence and validation plan

Use a disposable EC2 host with Docker, scoped SSH and registry credentials, validated SSH host key and controlled network access. Collect redacted Jenkins Deploy-stage output, immutable image digest, process status, an **actual HTTP/application probe**, and cleanup evidence. Document failures as well as success.

**Current evidence:** source and commit history only. Do not claim successful deployment without run proof.

[Back to main README](../../README.md).
