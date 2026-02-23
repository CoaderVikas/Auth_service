pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                echo "✅ Checking out source code from Git"
                git branch: 'feature_dev', url: 'https://github.com/CoaderVikas/Auth_service.git'
            }
        }

        stage('Build') {
            steps {
                echo "📦 Building Spring Boot project with Gradle (tests skipped)"
                bat 'gradlew.bat clean build -x test --stacktrace --info'
            }
        }

        stage('Run') {
            steps {
                echo "🚀 Running Spring Boot application in background"
                bat '''
                REM Kill any running Java apps to avoid port conflict
                taskkill /F /IM java.exe || exit 0

                REM Start the jar file in background
                for %%f in (build\\libs\\*.jar) do (
                    start /B java -jar %%f
                )
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Build and deployment completed successfully!"
        }
        failure {
            echo "❌ Build failed. Check console logs for details."
        }
    }
}