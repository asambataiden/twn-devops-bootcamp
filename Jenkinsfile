#!/usr/bin/env groovy

@Library('jenkins-shared-library') _

pipeline {
    agent any

    tools {
        maven 'maven-3.9'
    }

    environment {
        DOCKER_IMAGE_REPOSITORY = 'asambataiden/demo-app'
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
                branch 'main'
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

        stage('Deploy') {
            when {
                branch 'main'
            }

            steps {
                echo "Deploying application version ${env.APP_VERSION}"

                // Replace with Shared Library step when migrated:
                // deployApp()
            }
        }

        stage('commit version update') {
            steps {
                script {
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]){
                        sh 'git config --global user.email "asamba.tamajong.aiden@outlook.com"'
                        sh 'git config --global user.name "Asamba Tamajong Aiden"'

                        sh 'git status'
                        sh 'git branch'
                        sh 'git config --list'

                        sh "git remote set url origin https://${USER}:${PASS}@github.com/asambataiden/twn-devops-bootcamp.git "
                        sh 'git add .'
                        sh "git commit -m 'ci : Update version bump  to ${env.APP_VERSION}'"
                        sh 'git push origin HEAD:commitVersionUpdate'
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