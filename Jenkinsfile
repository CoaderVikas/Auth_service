pipeline {
    agent any

    options {
        timestamps()  // Console me time show karega
    }

    stages {
        stage('Checkout') {
            steps {
                echo "✅ Checking out source code"
                git branch: 'feature_dev', url: 'https://github.com/CoaderVikas/Auth_service.git'
            }
        }

        stage('Install Dependencies') {
            steps {
                echo "📦 Installing npm dependencies"
                bat 'npm install'
            }
        }

        stage('Build') {
            steps {
                echo "🏗️ Building project"
                bat 'npm run build'
            }
        }

        stage('Archive Build') {
            steps {
                echo "📂 Archiving build artifacts"
                archiveArtifacts artifacts: 'dist/**', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "✅ Build completed successfully"
        }
        failure {
            echo "❌ Build failed"
        }
    }
}