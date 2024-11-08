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
                    // Check that the JAR files exist in the target directories
                    echo "Checking backend target directory contents:"
                    sh "ls ${BACKEND_DIR}/target"
                    
                    echo "Checking testing target directory contents:"
                    sh "ls ${TESTING_DIR}/target"
                    
                    // Build Docker images using the JAR files in the target directories
                    sh "docker build -t examninja:backend_latest ${BACKEND_DIR}"
                    sh "docker build -t examninja:testing_latest ${TESTING_DIR}"
                }
            }
        }

        stage('Push Docker Images to ECR') {
            when {
                expression { env.FAILURE_REASON == null || env.FAILURE_REASON == '' }
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

        stage('Deploy to EKS') {
            when {
                expression { env.FAILURE_REASON == null || env.FAILURE_REASON == '' }
            }
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws_key']]) {
                        // Update kubeconfig to connect to EKS
                        sh "aws eks --region ${AWS_REGION} update-kubeconfig --name examninja"
                        
                        // Apply backend deployment using the latest ECR image
                        sh """
                        kubectl set image deployment/backend backend=${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest -n default
                        kubectl rollout status deployment/backend -n default
                        """
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
