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
                    echo "Committing application version ${env.APP_VERSION}"

                    withCredentials([usernamePassword(credentialsId: 'github-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]){
                        echo "Committing application version ${env.APP_VERSION}"

                        sh '''
                            set -eu

                            git config user.name "Asamba Tamajong Aiden"
                            git config user.email "asamba.tamajong.aiden@outlook.com"

                            git add pom.xml

                            echo "Files staged for commit:"
                            git diff --cached --name-only

                            if git diff --cached --quiet; then
                                echo "No version change to commit."
                                exit 0
                            fi
                        '''

                        sh 'git status'
                        sh 'git branch'
                        sh 'git config --list'

                        sh """
                            git commit \
                              -m "chore(release): bump version to ${env.APP_VERSION}"
                        """

                        sh """
                            git remote set url origin https://${USER}:${PASS}@github.com/asambataiden/twn-devops-bootcamp.git
                            git push origin HEAD:commitVersionUpdate
                        """
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