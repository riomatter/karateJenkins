pipeline {
    agent any

    stages {
        stage('Compile') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_11', type: 'maven'
                    sh "${mvnHome}/bin/mvn -B clean compile"
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_11', type: 'maven'
                    // Ejecuta tests y genera JSON compatible con Cucumber
                    sh "${mvnHome}/bin/mvn -B test -Dtest=UsersRunner -Dkarate.outputCucumberJson=true"
                }
            }
            post {
                always {
                    // Publica resultados JUnit (para tendencias en Jenkins)
                    junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
                    // Archiva los HTML de Karate (karate-summary.html y demás)
                    archiveArtifacts artifacts: 'target/karate-reports/**', allowEmptyArchive: false
                }
            }
        }

        stage('Cucumber Reports') {
            steps {
                script {
                    // Evita error si no hay JSON (por cualquier razón)
                    def hasJson = sh(script: 'ls target/karate-reports/*.json >/dev/null 2>&1', returnStatus: true) == 0
                    if (hasJson) {
                        cucumber(
                                buildStatus: 'SUCCESS',                 // no marque UNSTABLE por defecto
                                jsonReportDirectory: 'target/karate-reports',
                                fileIncludePattern: '*.json'
                        )
                    } else {
                        echo 'No se encontraron JSON de Cucumber en target/karate-reports/'
                    }
                }
            }
        }
    }
}
