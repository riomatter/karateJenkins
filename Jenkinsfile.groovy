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
                    // (opcional) también guarda todos los archivos por si quieres descargarlos
                    archiveArtifacts artifacts: 'target/karate-reports/**', allowEmptyArchive: false

                    // Publica el índice de Karate
                    publishHTML(target: [
                            reportName: 'Karate HTML',
                            reportDir: 'target/karate-reports',     // carpeta dentro del workspace
                            reportFiles: 'karate-summary.html',     // archivo inicial a mostrar
                            keepAll: true,
                            alwaysLinkToLastBuild: true,
                            allowMissing: false
                    ])
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
