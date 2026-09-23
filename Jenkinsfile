pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                script {
                    echo 'Jenkins is working! 🚀'
                }
                sh 'echo "Current directory: $(pwd)"'
                sh 'ls -la'
            }
        }
    }
}