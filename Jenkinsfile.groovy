pipeline {
    agent any

    stages {
        stage('Compile Stage') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_11', type: 'maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        sh "${mvnHome}/bin/mvn -B clean compile"
                    }
                }
            }
        }

        stage('Test Stage') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_11', type: 'maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        // Ajusta el -Dtest si tu runner/clase es otra
                        sh "${mvnHome}/bin/mvn -B test -Dtest=UsersRunner"
                    }
                }
            }
        }

        stage('Cucumber Reports') {
            steps {
                cucumber(
                        buildStatus: 'UNSTABLE',
                        fileIncludePattern: '**/cucumber.json',
                        jsonReportDirectory: 'target'
                )
            }
        }
    }
}
