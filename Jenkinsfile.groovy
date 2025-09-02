pipeline {
    agent any

    stages {
        stage('Compile') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_11', type: 'maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        sh "${mvnHome}/bin/mvn -B clean compile"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_11', type: 'maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        // Genera los JSON Cucumber y ejecuta tu runner
                        sh "${mvnHome}/bin/mvn -B test -Dtest=UsersRunner -Dkarate.outputCucumberJson=true"
                    }
                }
            }
            post {
                always {
                    // (opcional) publicar JUnit de Surefire
                    junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
                    // (opcional) guardar el HTML propio de Karate
                    archiveArtifacts artifacts: 'target/karate-reports/**', allowEmptyArchive: true
                }
            }
        }

        stage('Cucumber Reports') {
            steps {
                cucumber(
                        buildStatus: 'SUCCESS',                 // evita marcar UNSTABLE por default
                        jsonReportDirectory: 'target/karate-reports',
                        fileIncludePattern: '*.json'            // Karate genera varios json
                )
            }
        }
    }
}
