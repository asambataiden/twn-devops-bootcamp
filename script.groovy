def buildApp() {
    echo 'Building the Application ..........'
    echo "Building ${params.APP_NAME} version ${params.APP_VERSION} .........."
}

def testApp() {
    echo 'Testing the Application ..........'
    echo "Testing ${params.APP_NAME} version ${params.APP_VERSION} .........."
}

def deployApp() {
    echo 'Deploying the Application ..........'
    echo "Deploying ${params.APP_NAME} version ${params.APP_VERSION} .........."
}

return this