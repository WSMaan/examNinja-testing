pipeline {
    agent any
    environment {
        AWS_ACCOUNT_ID = "583187964056"
        AWS_REGION = "us-east-2"
        ECR_REPOSITORY_NAME = "examninja"
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        AWS_ACCESS_KEY_ID = 'AKIAYPSFWECMEWUAFVUR'
        AWS_SECRET_ACCESS_KEY = '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'
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

        stage('Build Docker Images') {
            steps {
                dir(BACKEND_DIR) {
                    sh 'docker build -t ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest .'
                }
            }
        }
        
        stage('Push Docker Images to ECR') {
            steps {
                sh 'aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}'
                sh 'docker push ${ECR_REGISTRY}/${ECR_REPOSITORY_NAME}:backend_latest'
            }
        }

        // stage('Deploy to EKS') {
        //     steps {
        //         // Ensure kubectl is configured for your EKS cluster
        //         sh 'aws eks --region ${AWS_REGION} update-kubeconfig --name examninja' // Change 'my-cluster' to your cluster name
        //         // Apply Kubernetes deployment files
        //         dir(BACKEND_DIR) {
        //             sh 'kubectl apply -f k8s/backend-deployment.yaml' // Ensure your backend deployment file is correctly defined
        //         }
        //     }
        // }
        
        // stage('Run JMeter Load Tests') {
        //     steps {
        //         script {
        //             sh '/opt/jmeter/bin/jmeter -n -t ${WORKSPACE}/${TESTING_DIR}/Test_Plan.jmx -l ${WORKSPACE}/results.jtl -Jbackend_url=http://your-backend-url -Jfrontend_url=http://your-frontend-url'
        //         }
        //     }
        //     post {
        //         always {
        //             archiveArtifacts artifacts: 'results.jtl', fingerprint: true
        //         }
        //     }
        // }
    }

    // post {
    //     always {
    //         cleanWs()
    //     }
    //     failure {
    //         script {
    //             echo "Pipeline failed due to failure in the ${env.FAILURE_REASON} stage."
    //             // slackSend(channel: '#exam-ninja', color: 'danger', message: "Pipeline failed due to failure in the ${env.FAILURE_REASON} stage. Check Jenkins for details.")
    //         }
    //     }
    //     success {
    //         // slackSend(channel: '#exam-ninja', color: 'good', message: 'Pipeline succeeded!')
    //         echo 'Pipeline succeeded!'
    //     }
    // }
}
