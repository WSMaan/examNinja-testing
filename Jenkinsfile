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

        stage('Build Docker Images Locally') {
            steps {
                script {
                    // Build the Docker images but do not push to ECR yet
                    sh "docker build -t ${ECR_REPOSITORY_NAME}:backend_latest ${BACKEND_DIR}"
                }
            }
            post {
                failure {
                    script {
                        env.FAILURE_REASON = 'docker build'
                    }
                }
            }
        }

        stage('Run Docker Containers') {
            steps {
                script {
                    // Write docker-compose file and use locally built images
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
                          interval: 60s
                          timeout: 10s
                          retries: 5
                        networks:
                          - examninja-network
                          
                      backend:
                        image: ${ECR_REPOSITORY_NAME}:backend_latest
                        container_name: examninja-backend
                        depends_on:
                          mysql:
                            condition: service_healthy
                        environment:
                          SPRING_DATASOURCE_URL: jdbc:mysql://mysql-container:3306/exam
                          SPRING_DATASOURCE_USERNAME: root
                          SPRING_DATASOURCE_PASSWORD: root@123
                        ports:
                          - "8081:8081"
                        networks:
                          - examninja-network

                    networks:
                      examninja-network:
                    """)

                    sh 'docker-compose up -d'
                    sh 'docker logs examninja-backend' // Fetch backend logs immediately after startup
                }
            }
        }

        stage('Register and Login') {
            steps {
                script {
                    sh 'sleep 30' // Allow time for backend to initialize

                    def registerResponse = sh(script: """
                    curl -X POST http://localhost:8081/api/users/register \
                    -H "Content-Type: application/json" \
                    -d '{ "email": "foo@example.com", "password": "password@123", "firstName": "Foo", "lastName": "Bar" }'
                    """, returnStdout: true).trim()
                    echo "Registration Response: ${registerResponse}"

                    def loginResponse = sh(script: """
                    curl -X POST http://localhost:8081/api/users/login \
                    -H "Content-Type: application/json" \
                    -d '{ "email": "foo@example.com", "password": "password@123" }'
                    """, returnStdout: true).trim()

                    if (loginResponse.contains("token")) {
                        env.AUTH_TOKEN = sh(script: "echo ${loginResponse} | jq -r .token", returnStdout: true).trim()
                        echo "Obtained Auth Token: ${AUTH_TOKEN}"
                    } else {
                        error("Login failed: ${loginResponse}")
                    }
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
                    sh "mvn clean test -DauthToken=${AUTH_TOKEN}"
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

        stage('Push Docker Images to ECR') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws_key']]) {
                        sh 'aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}'
                        sh "docker tag ${ECR_REPOSITORY_NAME}:backend_latest ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest"
                        sh "docker push ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest"
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'docker-compose down'
            cleanWs()
        }
        failure {
            script {
                echo "Pipeline failed due to failure in the ${env.FAILURE_REASON} stage."
            }
        }
        success {
            echo 'Pipeline succeeded and Docker images have been pushed to ECR!'
        }
    }
}
