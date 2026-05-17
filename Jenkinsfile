pipeline {
    agent any

    environment {
        IMAGE_NAME = "coadervikas/auth-service"
        IMAGE_TAG = "latest"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out source code"
                git branch: 'feature_dev',
                url: 'https://github.com/CoaderVikas/Auth_service.git'
            }
        }

        stage('Build') {
            steps {
                echo "Building Spring Boot app"
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker image"
                sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG .'
            }
        }

        stage('Docker Push') {
            steps {
                echo "Pushing Docker image"

                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {

                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $IMAGE_NAME:$IMAGE_TAG'
                }
            }
        }

        stage('Kubernetes Deploy') {
            steps {
                echo "Deploying to Kubernetes"

                sh 'kubectl apply -f k8s/dev/'
                sh 'kubectl rollout restart deployment auth-service'
            }
        }
    }

    post {
        success {
            echo "SUCCESS: App deployed to Kubernetes 🚀"
        }

        failure {
            echo "FAILED: Check Jenkins logs ❌"
        }
    }
}