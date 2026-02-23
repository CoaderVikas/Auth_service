pipeline {
    agent any

    tools {
        // Java version used by Spring Boot
        jdk 'Java17'
    }

    environment {
        // Agar aapko additional env variables set karne ho (optional)
        // Example: DB_USER, DB_PASSWORD, DB_HOST etc.
        // In your case, configuration already Git se read ho rahi hai
        // DB_USER = 'admin'
        // DB_PASSWORD = 'secret'
    }

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
                // Skip tests for deploy
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

        // Optional: DB Check (uncomment if needed)
        /*
        stage('DB Check') {
            steps {
                echo "🔍 Checking Oracle DB connectivity"
                bat '''
                sqlplus -S $DB_USER/$DB_PASSWORD@$DB_HOST:$DB_PORT/$DB_SERVICE << EOF
                exit
                EOF
                '''
            }
        }
        */
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