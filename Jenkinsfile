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
                branch 'deployToAWSDockerServer'
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

       /* stage('Deploy') {
            when {
                branch 'main'
            }

            steps {

                script {

                    echo "Deploying application version ${env.APP_VERSION}"

                    def dockerCmd = "docker run -p 8080:8080 -d ${env.DOCKER_IMAGE_REPOSITORY}:${env.IMAGE_NAME}"

                    sshagent(credentials: ['aws-ec2-docker-server-ssh'], executable: '') {
                        // some block

                    }



                    // Replace with Shared Library step when migrated:
                    // deployApp()

                }

            }
        }*/


        stage('Deploy') {
            when {
                branch 'deployToAWSDockerServer'
            }

            steps {
                script {
                    def image =
                    "${env.DOCKER_IMAGE_REPOSITORY}:${env.IMAGE_NAME}"

                    def containerName = 'demo-app'
                    def host = env.EC2_HOST

                    echo "Deploying ${image} to ${host}"

                    sshagent(credentials: ['aws-ec2-docker-server-ssh']) {
                        sh """
                            set -eu

                            ssh -o StrictHostKeyChecking=no ec2-user@${host} '
                                set -eu

                                docker pull ${image}

                                docker rm -f ${containerName} 2>/dev/null || true

                                docker run \
                                    --detach \
                                    --name ${containerName} \
                                    --restart unless-stopped \
                                    --publish 8080:8080 \
                                    ${image}

                                sleep 3

                                docker ps \
                                    --filter "name=${containerName}" \
                                    --filter "status=running"
                            '
                        """
                    }
                }
            }
        }


        stage('Commit Version Update') {
            when {
                branch 'commitVersionUpdate'
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