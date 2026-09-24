def buildJar() {
    echo 'Building the Application ..........'
    sh 'mvn package'
}

def buildImage() {
    echo 'Building the docker image...........'
    withCredentials(
            [
                    usernamePassword(
                            credentialsId: 'docker-hub-repo-asambataiden',
                            passwordVariable: 'DOCKERHUB_PASSWORD',
                            usernameVariable: 'DOCKERHUB_USERNAME')
            ]
    )
            {
                sh 'docker build -t asambataiden/demo-app:jma-2.0 .'
                sh 'echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_USERNAME --password-stdin'
                sh 'docker push asambataiden/demo-app:jma-2.0'
            }
}

def testApp() {
    echo 'Testing the Application ..........'
    echo "Executing pipeline for branch $BRANCH_NAME "
    echo "Testing ${params.APP_NAME} version ${params.APP_VERSION} .........."
}

def deployApp() {
    echo 'Deploying the Application ..........'
    echo "Deploying ${params.APP_NAME} version ${params.APP_VERSION} .........."
}

return this