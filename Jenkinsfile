pipeline {
    agent any
    environment {
        AWS_ACCOUNT_ID = "954976294733"
        AWS_REGION = "us-east-1"
        ECR_REPOSITORY_NAME = "examninja"
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        AWS_ACCESS_KEY_ID = 'AKIA54WIF25G5VQOVB45'
        AWS_SECRET_ACCESS_KEY = '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'
        BACKEND_DIR = 'backend'  // Ensure this matches the correct directory structure
        TEST_DIR = 'testing'  // Directory for the Rest Assured test repository
        FAILURE_REASON = ''  // To capture failure reason
        DOCKER_IMAGE_TAG = "backend_latest"  // Tag for the backend Docker image
    }
    
    stages {
        // Step 1: Clone the Testing Repository
        stage('Clone Testing Repository') {
            steps {
                dir(TEST_DIR) {
                    // Clone the testing repository where your tests are located
                    git branch: 'ENJ-feature', url: 'https://github.com/WSMaan/examNinja-testing.git', credentialsId: 'GIT_HUB'
                }
            }
        }

        // Step 2: Run Rest Assured Tests
        stage('Run Rest Assured Tests') {
            steps {
                dir(TEST_DIR) {
                    // Execute your Rest Assured tests
                    sh 'mvn clean test -DbaseUrl=http://localhost:8081'
                }
            }
            post {
                failure {
                    script {
                        env.FAILURE_REASON = 'Rest Assured tests'
                    }
                    error("Rest Assured tests failed.")
                }
            }
        }
    }

    post {
        always {
            cleanWs() // Clean the workspace after execution
        }
        failure {
            script {
                echo "Pipeline failed due to failure in the ${env.FAILURE_REASON} stage."
            }
        }
        success {
            echo 'Pipeline succeeded!'
        }
    }
}
