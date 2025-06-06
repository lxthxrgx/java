pipeline {
    agent any

    environment {
        JAVA_HOME = "/opt/java/jdk-24"
        PATH = "${JAVA_HOME}/bin:${PATH}"
        IMAGE_NAME = "spring-app"
        IMAGE_TAG = "latest"
        DOCKER_HUB_CREDENTIALS = 'docker-hub-credentials'
        KUBECONFIG = "/root/.kube/config"
    }

    stages {
        stage('Check Java') {
            steps {
                sh 'java -version'
                sh 'echo $JAVA_HOME'
            }
        }

        stage('Build') {
            steps {
                echo '🏗️ Building project...'
                sh './mvnw clean compile'
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Running tests...'
                sh './mvnw test'
            }
        }

        stage('Package') {
            steps {
                echo '📦 Packaging JAR...'
                sh './mvnw package -DskipTests'
            }
        }

        stage('Debug K8s') {
            steps {
                sh '''
                echo "Current Kube context:"
                kubectl config current-context || echo "No context found"
                
                echo "Cluster info:"
                kubectl cluster-info || echo "Cannot connect"

                echo "Nodes:"
                kubectl get nodes || echo "No nodes"
                '''
            }
        }


        stage('Docker Build (local)') {
            steps {
                echo '🐳 Building Docker image locally for Minikube...'
                sh 'docker build -t spring-app:latest .'
            }
        }

        stage('Deploy to Minikube') {
            steps {
                echo '🚀 Deploying app to Minikube...'
                sh '''
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Build and deploy pipeline completed successfully!'
        }
        failure {
            echo '❌ Build or deploy pipeline failed.'
        }
    }
}
