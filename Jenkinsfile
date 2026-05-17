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
                steps {
                    echo "Building Spring Boot app"
                    sh '''
                        chmod +x gradlew
                        ./gradlew clean build -x test
                    '''
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker image"
                sh '''
                    set -e
                    docker build -t $IMAGE_NAME:$IMAGE_TAG .
                '''
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
                echo "Deploying to Kubernetes"

                script {
                    if (!fileExists('/tmp/kubeconfig.yaml')) {
                        error "Kubeconfig missing in Jenkins. Add credential with ID: kubeconfig-file"
                    }
                }

                withCredentials([file(credentialsId: 'kubeconfig-file', variable: 'KUBECONFIG_FILE')]) {

                    sh '''
                        set -e

                        export KUBECONFIG=$KUBECONFIG_FILE

                        echo "Testing cluster access..."
                        kubectl version --client
                        kubectl get nodes

                        echo "Deploying Kubernetes manifests..."
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
            echo "SUCCESS: App deployed to Kubernetes"
        }

        failure {
            echo "FAILED: Check Jenkins logs (Docker/K8s/Kubeconfig issue)"
        }
    }
}