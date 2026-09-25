#!/user/bin/env groovy

@Library('jenkins-shared-library')

def gv

pipeline {
    agent any

    tools {
        maven 'maven-3.9'
    }

    stages {

        stage('Init') {
            steps {
                script {
                    gv = load 'script.groovy'
                }
            }
        }

        stage('Build JAR') {
            steps {
                script {
                    buildJar()
                }
            }
        }

        stage('Build Image') {
            when {
                branch 'main'
            }

            steps {
                script {
                    buildImage()
                }
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
            }

            steps {
                script {
                    gv.deployApp()
                }
            }
        }
    }
}
