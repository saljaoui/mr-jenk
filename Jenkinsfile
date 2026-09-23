pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo '✅ Code checked out'
            }
        }

        // ---------- BUILD: discovery-service ----------
        stage('Build: discovery-service') {
            steps {
                dir('infrastructure/discovery-service') {
                    sh './mvnw spring-boot:run'
                }
            }
        }

        // ---------- BUILD: api-gateway ----------
        stage('Build: api-gateway') {
            steps {
                dir('infrastructure/api-gateway') {
                    sh './mvnw spring-boot:run'
                }
            }
        }

        // ---------- BUILD: user-service ----------
        stage('Build: user-service') {
            steps {
                dir('services/user-service') {
                    sh './mvnw spring-boot:run'
                }
            }
        }
    }

    post {
        always {
            echo '🏁 Pipeline finished'
        }
        success {
            echo '✅ Build succeeded'
        }
        failure {
            echo '❌ Build failed'
        }
    }
}