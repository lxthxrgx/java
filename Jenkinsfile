pipeline {
    agent any

    environment {
        JAVA_HOME = "/opt/java/jdk-24"
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Check Java') {
            steps {
                sh 'java -version'
                sh 'echo $JAVA_HOME'
            }
        }

        stage('Checkout') {
            steps {
                echo '🔄 Cloning repository...'
                checkout scm
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
    }

    post {
        success {
            echo '✅ Build pipeline completed successfully!'
        }
        failure {
            echo '❌ Build pipeline failed.'
        }
    }
}
