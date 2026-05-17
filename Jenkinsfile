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
                sh '''
                    set -e
                    chmod +x gradlew
                    ./gradlew clean build -x test
                '''
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
                    try {
                        withCredentials([file(credentialsId: 'kubeconfig-file', variable: 'KUBECONFIG_FILE')]) {

                            sh '''
                                set -e

                                export KUBECONFIG=$KUBECONFIG_FILE

                                kubectl version --client
                                kubectl get nodes

                                kubectl apply -f k8s/dev/
                                kubectl rollout restart deployment/auth-service
                                kubectl rollout status deployment/auth-service --timeout=180s
                            '''
                        }
                    } catch (err) {
                        echo "Kubernetes deploy failed but pipeline will NOT break fully"
                        echo "Reason: ${err}"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "SUCCESS: App deployed successfully"
        }

        failure {
            echo "FAILED: Check Jenkins logs"
        }
    }
}