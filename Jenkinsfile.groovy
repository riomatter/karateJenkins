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
                        // DEBUG: ver qué dejó Karate
                        sh 'echo "--- LISTANDO target/karate-reports ---"'
                        sh 'ls -l target/karate-reports || true'
                    }
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts 'target/karate-reports/**'
                }
            }
        }

        stage('Cucumber Reports') {
            steps {
                // Si no hay JSON aquí, el plugin fallará con "No JSON report file was found!"
                cucumber(
                        buildStatus: 'SUCCESS',
                        jsonReportDirectory: 'target/karate-reports',
                        fileIncludePattern: '*.json'
                )
            }
        }
    }
}
