#!/usr/bin/env groovy

@Library('jenkins-shared-library') _

pipeline {
    agent any

    tools {
        maven 'maven-3.9'
    }

    environment {
        DOCKER_IMAGE_REPOSITORY = 'asambataiden/demo-app'
        EC2_HOST = '56.228.31.131'
    }

    stages {

        stage('Increment Version') {
            steps {
                script {
                    echo 'Incrementing application version...'

                    sh '''
                        mvn build-helper:parse-version versions:set \
                          -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.nextIncrementalVersion} \
                          -DgenerateBackupPoms=false
                    '''
                    env.APP_VERSION = sh(
                        script: '''
                            mvn help:evaluate \
                              -Dexpression=project.version \
                              -q \
                              -DforceStdout
                        ''',
                        returnStdout: true
                    ).trim()

                    echo "Application version: ${env.APP_VERSION}"
                }
            }
        }

        stage('Build App') {
            steps {
                script {
                    buildJar()
                }
            }
        }

        stage('Build and Push Image') {
            when {
                branch 'deployToAWSDokerServer_With_DockerCompose'
            }

            steps {
                script {

                    env.IMAGE_NAME = "${env.APP_VERSION}-${BUILD_NUMBER}"
                    def imageName =
                    "${env.DOCKER_IMAGE_REPOSITORY}:${env.IMAGE_NAME}"

                    echo "Building image: ${imageName}"

                    buildImage(imageName)
                    dockerPush(imageName)
                }
            }
        }


        stage('Deploy to Ec2 with Docker Compose') {
            when {
                branch 'deployToAWSDokerServer_With_DockerCompose'
            }

            steps {
                script {
                    def host = env.EC2_HOST
                    def remoteDir = '/home/ec2-user/demo-app'
                    def image = "${env.DOCKER_IMAGE_REPOSITORY}:${env.IMAGE_NAME}"

                    echo "Deploying image ${image} to EC2 ${host}"

                    sshagent(credentials: ['aws-ec2-docker-server-ssh']) {

                        /*
						 * Bootstrap the deployment directory.
						 *
						 * This makes the pipeline self-contained:
						 * nothing needs to be prepared manually on EC2.
						 */
                        sh """
                    set -eu

                    ssh \
                      -o StrictHostKeyChecking=no \
                      ec2-user@${host} \
                      'mkdir -p ${remoteDir}'
                """

                        /*
						 * Copy the desired-state Docker Compose definition.
						 */
                        sh """
                    set -eu

                    scp \
                      -o StrictHostKeyChecking=no \
                      docker-compose.yaml \
                      ec2-user@${host}:${remoteDir}/docker-compose.yaml
                """

                        /*
						 * Create deployment configuration and reconcile
						 * the EC2 Docker Compose deployment.
						 */
                        sh """
                    set -eu

                    ssh \
                      -o StrictHostKeyChecking=no \
                      ec2-user@${host} '
                        set -eu

                        cd ${remoteDir}

                        printf "%s\\n" \
                          "DOCKER_IMAGE_REPOSITORY=${env.DOCKER_IMAGE_REPOSITORY}" \
                          "IMAGE_NAME=${env.IMAGE_NAME}" \
                          "SPRING_PROFILES_ACTIVE=default" \
                          > .env

                        echo "Validating Docker Compose configuration..."

                        docker compose \
                          --env-file .env \
                          -f docker-compose.yaml \
                          config \
                          --quiet

                        echo "Pulling deployment images..."

                        docker compose \
                          --env-file .env \
                          -f docker-compose.yaml \
                          pull

                        echo "Deploying application..."

                        docker compose \
                          --env-file .env \
                          -f docker-compose.yaml \
                          up \
                          --detach \
                          --remove-orphans

                        echo "Deployment status:"

                        docker compose \
                          --env-file .env \
                          -f docker-compose.yaml \
                          ps
                      '
                """
                    }
                }
            }
        }


        stage('Commit Version Update') {
            when {
                branch 'deployToAWSDokerServer_With_DockerCompose'
            }

            steps {
                script {
                    echo "Preparing version commit for ${env.APP_VERSION}"

                    sh '''
                set -eu

                git config user.name "Jenkins CI"
                git config user.email "jenkins-ci@users.noreply.github.com"

                git add pom.xml

                echo "Files staged for commit:"
                git diff --cached --name-only
            '''

                    def hasChanges = sh(
                        script: 'git diff --cached --quiet',
                        returnStatus: true
                    )

                    if (hasChanges == 0) {
                        echo 'No version change to commit. Skipping commit and push.'
                    } else {
                        sh """
                    git commit \
                      -m "chore(release): bump version to ${env.APP_VERSION}"
                """

                        withCredentials([
                            gitUsernamePassword(
                                credentialsId: 'github-credentials',
                                gitToolName: 'Default'
                            )
                        ]) {
                            sh '''
                        set -eu
                        git push origin HEAD:commitVersionUpdate
                    '''
                        }
                    }
                }
            }
        }

    }

    post {
        success {
            echo "Pipeline completed successfully for version ${env.APP_VERSION}"
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}
