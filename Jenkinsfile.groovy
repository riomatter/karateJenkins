pipeline {
    agent any

    // Opciones del job (rotación/retención y timestamps en consola)
    options {
        // Mantén como máximo 10 builds o 30 días (y artefactos 10 builds / 14 días)
        buildDiscarder(logRotator(
                daysToKeepStr: '30',
                numToKeepStr: '10',
                artifactDaysToKeepStr: '14',
                artifactNumToKeepStr: '10'
        ))
        timestamps()
    }

    stages {

        stage('Clean workspace (pre)') {
            steps {
                // Limpia el workspace antes de empezar (no borra el historial del job)
                cleanWs()
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    def mvnHome = tool name: 'maven_3_9_11', type: 'maven'
                    withEnv(["PATH+MAVEN=${mvnHome}/bin"]) {
                        sh "mvn -B clean compile"
                        // Ejecuta Karate (ajusta el runner si tiene otro nombre)
                        sh "mvn -B test -Dtest=UsersRunner"
                        // DEBUG opcional: lista lo que generó Karate
                        sh "ls -l target/karate-reports || true"
                    }
                }
            }
            post {
                always {
                    // Publica resultados JUnit (para tendencias en Jenkins)
                    junit 'target/surefire-reports/*.xml'

                    // Archiva todos los reportes de Karate (HTML, PNG, etc.)
                    archiveArtifacts artifacts: 'target/karate-reports/**', allowEmptyArchive: false

                    // Publica la página HTML principal de Karate en el menú del build
                    publishHTML(target: [
                            reportName: 'Karate HTML',
                            reportDir: 'target/karate-reports',
                            reportFiles: 'karate-summary.html',
                            keepAll: true,
                            alwaysLinkToLastBuild: true,
                            allowMissing: false
                    ])
                }
            }
        }
    }

    // Limpieza final del workspace (lo archivado ya quedó guardado en el build)
    post {
        always {
            cleanWs()
        }
    }
}
