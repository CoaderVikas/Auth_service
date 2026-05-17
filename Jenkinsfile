pipeline {
    agent any

    environment {
        IMAGE_NAME = "coadervikas/auth-service"
        IMAGE_TAG = "latest"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'feature_dev',
                    url: 'https://github.com/CoaderVikas/Auth_service.git'
            }
        }

        stage('Build') {
            steps {
                sh '''
                    set -e
                    chmod +x gradlew
                    ./gradlew clean build -x test
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    set -e
                    docker build -t $IMAGE_NAME:$IMAGE_TAG .
                '''
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        set -e
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push $IMAGE_NAME:$IMAGE_TAG
                    '''
                }
            }
        }

        stage('Kubernetes Deploy') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-file', variable: 'KUBECONFIG_FILE')]) {

                    sh '''
                        set -e

                        export KUBECONFIG=$KUBECONFIG_FILE

                        echo "Checking cluster access..."
                        kubectl get nodes

                        echo "Applying manifests..."
                        kubectl apply -f k8s/dev/

                        echo "Restarting deployment..."
                        kubectl rollout restart deployment/auth-service

                        echo "Waiting for rollout..."
                        kubectl rollout status deployment/auth-service --timeout=180s
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "SUCCESS: App deployed successfully to Kubernetes"
        }

        failure {
            echo "FAILED: Pipeline failed. Check logs (Build/Docker/K8s issue)"
        }
    }
}