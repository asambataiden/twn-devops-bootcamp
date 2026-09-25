
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