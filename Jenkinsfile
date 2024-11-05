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
        AUTH_TOKEN = '' // Variable to store the auth token
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

        stage('Build and Push Docker Image to ECR') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws_key']]) {
                        // Login to ECR
                        sh 'aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}'

                        // Build the Docker image
                        sh "docker build -t ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest ${BACKEND_DIR}"

                        // Push the Docker image to ECR
                        sh "docker push ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest"
                    }
                }
            }
        }

        stage('Run Docker Containers') {
            steps {
                script {
                    // Write the Docker Compose file with the latest image
                    writeFile(file: 'docker-compose.yml', text: """
                    version: '3.8'
                    services:
                      mysql:
                        image: mysql:latest
                        container_name: mysql-container
                        environment:
                          MYSQL_ROOT_PASSWORD: root@123
                          MYSQL_DATABASE: exam
                        ports:
                          - "9308:3306"
                        healthcheck:
                          test: [ "CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-proot@123" ]
                          interval: 30s
                          timeout: 10s
                          retries: 5
                        networks:
                          - examninja-network
                          
                      backend:
                        image: ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest
                        container_name: examninja-backend
                        depends_on:
                          mysql:
                            condition: service_healthy
                        ports:
                          - "8081:8081"
                        networks:
                          - examninja-network

                    volumes:
                      db_data:

                    networks:
                      examninja-network:
                    """)

                    // Start the services using the latest image
                    sh 'docker-compose up -d'
                }
            }
        }

        stage('Wait for Services to be Ready') {
            steps {
                script {
                    // Poll for the backend service to be up
                    timeout(time: 5, unit: 'MINUTES') {
                        waitUntil {
                            def response = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://backend:8081/api/health", returnStdout: true).trim()
                            return response == '200' // Adjust based on your health check endpoint
                        }
                    }
                }
            }
        }

        stage('Register and Login') {
            steps {
                script {
                    // Register user (this will likely be skipped if already registered)
                    def registerResponse = sh(script: """
                    curl -X POST http://backend:8081/api/users/register \
                    -H "Content-Type: application/json" \
                    -d '{ "email": "foo@example.com", "password": "password@123" }'
                    """, returnStdout: true).trim()

                    // Log the registration response (optional)
                    echo "Registration Response: ${registerResponse}"

                    // Login user and capture the response
                    def loginResponse = sh(script: """
                    curl -X POST http://backend:8081/api/users/login \
                    -H "Content-Type: application/json" \
                    -d '{ "email": "foo@example.com", "password": "password@123" }'
                    """, returnStdout: true).trim()

                    // Parse the auth token from the login response
                    env.AUTH_TOKEN = sh(script: "echo ${loginResponse} | jq -r .token", returnStdout: true).trim()
                    echo "Obtained Auth Token: ${AUTH_TOKEN}"
                }
            }
            post {
                failure {
                    script {
                        env.FAILURE_REASON = 'registration/login'
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                dir(TESTING_DIR) {
                    // You can pass the token as an environment variable or use it in your tests
                    sh "mvn clean test -DauthToken=${AUTH_TOKEN}" // Adjust if your test setup requires the token differently
                }
            }
            post {
                failure {
                    script {
                        env.FAILURE_REASON = 'tests'
                    }
                }
            }
        }
    }

    post {
        always {
            // Stop and remove Docker containers
            sh 'docker-compose down'
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
