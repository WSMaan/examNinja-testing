pipeline {
    agent any
    environment {
        AWS_ACCOUNT_ID = "583187964056"
        AWS_REGION = "us-east-2"
        ECR_REPOSITORY_NAME = "examninja"
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        BACKEND_DIR = 'backend'
        TESTING_DIR = 'testing'
        FAILURE_REASON = ''  // To capture failure reason
    }
    stages {
        stage('Clone Repositories') {
            steps {
                dir(BACKEND_DIR) {
                    git branch: 'master', url: 'https://github.com/WSMaan/examNinja-backend.git', credentialsId: 'GIT_HUB'
                }
                dir(TESTING_DIR) {
                    git branch: 'master', url: 'https://github.com/WSMaan/examNinja-testing.git', credentialsId: 'GIT_HUB'
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir(BACKEND_DIR) {
                    sh 'mvn clean install'
                }
            }
            post {
                failure {
                    script {
                        env.FAILURE_REASON = 'backend'
                    }
                }
            }
        }

        stage('Build Docker Images Locally') {
            steps {
                script {
                    sh "docker build -t examninja:backend_latest ${BACKEND_DIR}"
                }
            }
        }

        stage('Run Docker Containers') {
            steps {
                script {
                    // Reference the docker-compose.yml file from the testing directory
                    sh 'docker-compose -f testing/docker-compose.yml up -d'
                    sh 'docker logs examninja-backend' // Fetch backend logs immediately after startup
                }
            }
        }

        stage('Run RestAssured Tests') {
            steps {
                dir(TESTING_DIR) {
                    // Run the RestAssured tests using Maven, passing any needed parameters
                    sh "mvn clean test -DapiUrl=http://localhost:8081"
                }
            }
            post {
                always {
                    // Publish JUnit test results if you’re using JUnit
                    junit '**/target/surefire-reports/*.xml'
                }
                failure {
                    script {
                        env.FAILURE_REASON = 'tests'
                    }
                }
            }
        }

        stage('Push Docker Images to ECR') {
            when {
                expression { env.FAILURE_REASON == null || env.FAILURE_REASON == '' } // Only push if there are no failures
            }
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws_key']]) {
                        sh 'aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}'
                        sh "docker tag examninja:backend_latest ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest"
                        sh "docker push ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest"
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'docker-compose -f testing/docker-compose.yml down'
            cleanWs()
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
