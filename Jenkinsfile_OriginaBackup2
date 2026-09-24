def gv

pipeline {
    agent any

    parameters {
        string(
            name: 'APP_NAME',
            defaultValue: 'MyApp',
            description: 'Name of the Application'
        )

        choice(
            name: 'APP_VERSION',
            choices: ['1.0.0', '1.0.1', '1.1.0'],
            description: 'Version of the Application'
        )

        booleanParam(
            name: 'EXECUTE_TESTS',
            defaultValue: true,
            description: 'Execute Tests'
        )
    }

    stages {
        stage('Init') {
            steps {
                script {
                    gv = load 'script.groovy'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    gv.buildApp()
                }
            }
        }

        stage('Test') {
            when {
                expression {
                    return params.EXECUTE_TESTS
                }
            }

            steps {
                script {
                    gv.testApp()
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                   env.ENV = input message: "Select the enviroment to deploy to", ok: "Deploy", parameters: [
                        choice(name: 'ONE', choices: ['dev', 'staging', 'prod'], description: 'Environment to deploy to')
                    ]
                    gv.deployApp()
                    echo "Deployed to ${ENV}"
                }
            }
        }
    }
}