pipeline {
    agent any

    stages {
        stage('Build & Test') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_11', type: 'maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        sh "mvn -B clean compile"
                        sh "mvn -B test -Dtest=UsersRunner -Dkarate.outputCucumberJson=true"
                        // Debug: lista lo que generó Karate
                        sh "ls -l target/karate-reports || true"
                    }
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    // publica los HTML y JSON de Karate como artefactos descargables
                    archiveArtifacts 'target/karate-reports/**'
                }
            }
        }

        stage('Cucumber Reports') {
            steps {
                cucumber(
                        buildStatus: 'SUCCESS',
                        jsonReportDirectory: 'target/karate-reports',
                        fileIncludePattern: '*.json'
                )
            }
        }
    }
}
