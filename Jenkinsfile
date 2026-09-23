pipeline {
    agent any

    stages {
        stage('Build discovery-service') {
            steps {
                dir('infrastructure/discovery-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean verify'
                }
            }
        }

        stage('Build api-gateway') {
            steps {
                dir('infrastructure/api-gateway') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean verify'
                }
            }
        }

        stage('Build user-service') {
            steps {
                dir('services/user-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean verify'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished'
        }

        success {
            echo 'All services built successfully'
        }

        failure {
            echo 'One or more services failed'
        }
    }
}