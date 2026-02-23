pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                git branch: 'feature_dev', url: 'https://github.com/CoaderVikas/Auth_service.git'
            }
        }

        stage('Build') {
            steps {
                bat 'gradlew.bat clean build -x test'
            }
        }

        stage('Run') {
            steps {
                bat 'java -jar build\\libs\\*.jar'
            }
        }
    }
}