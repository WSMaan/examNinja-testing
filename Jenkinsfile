pipeline {
    agent any
    environment {
        AWS_ACCOUNT_ID = "545009823658"
        AWS_REGION = "eu-north-1"
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

        stage('Build Testing') {
            steps {
                dir(TESTING_DIR) {
                    sh 'mvn clean install'
                }
            }
            post {
                failure {
                    script {
                        env.FAILURE_REASON = 'testing'
                    }
                }
            }
        }

        stage('Build Docker Images Locally') {
            steps {
                script {
                    sh "docker build -t examninja:backend_latest ${BACKEND_DIR}"
                    sh "docker build -t examninja:testing_latest ${TESTING_DIR}"
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
                    sh "mvn clean test -DapiUrl=http://backend:8081"
                }
            }
            post {
                always {
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
                        // Log in to ECR
                        sh 'aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}'

                        // Tag and push the backend image
                        sh "docker tag examninja:backend_latest ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest"
                        sh "docker push ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest"

                        // Tag and push the testing image
                        sh "docker tag examninja:testing_latest ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:testing_latest"
                        sh "docker push ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:testing_latest"
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
