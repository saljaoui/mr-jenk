pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building shared-events...'
                dir('services/shared-events') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean install -DskipTests'
                }

                echo 'Building discovery-service...'
                dir('infrastructure/discovery-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }

                echo 'Building api-gateway...'
                dir('infrastructure/api-gateway') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }

                echo 'Building user-service...'
                dir('services/user-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }

                echo 'Building media-service...'
                dir('services/media-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Testing shared-events...'
                dir('services/shared-events') {
                    sh './mvnw test'
                }

                echo 'Testing discovery-service...'
                dir('infrastructure/discovery-service') {
                    sh './mvnw test'
                }

                echo 'Testing api-gateway...'
                dir('infrastructure/api-gateway') {
                    sh './mvnw test'
                }

                echo 'Testing user-service...'
                dir('services/user-service') {
                    sh './mvnw test'
                }

                echo 'Testing media-service...'
                dir('services/media-service') {
                    sh './mvnw test'
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying with Docker Compose...'
                sh 'docker compose -f docker-compose.yml down || true'
                sh 'docker compose -f docker-compose.yml up -d'
            }
        }
    }

    post {
        always {
            echo '🏁 Pipeline finished'
        }

        success {
            echo '✅ Build, Test, and Deploy succeeded'
        }

        failure {
            echo '❌ Pipeline failed'
        }
    }
}