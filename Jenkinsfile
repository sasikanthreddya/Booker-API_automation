pipeline {
    agent any

    tools {
        maven 'Maven3'     // Must match the name in Jenkins → Global Tool Configuration
        jdk   'JDK21'      // Must match the name in Jenkins → Global Tool Configuration
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'e253e72e-a5e3-452d-a7b4-ac8a39b53b1a',
                    url: 'https://github.com/sasikanthreddya/Booker-API_automation'
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean test -B'
            }
        }
    }

    post {
        always {
            // JUnit / Surefire results
            junit testResults: '**/surefire-reports/TEST-*.xml',
                  allowEmptyResults: true

            // Cucumber HTML report
            publishHTML(target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : 'target/cucumber-reports',
                reportFiles          : 'cucumber.html',
                reportName           : 'Cucumber Report'
            ])

            // Archive JSON + HTML reports as build artifacts
            archiveArtifacts artifacts: 'target/cucumber-reports/**,target/extent-reports/**',
                             allowEmptyArchive: true
        }

        failure {
            echo 'Build FAILED — check test results above.'
        }

        success {
            echo 'All tests PASSED.'
        }
    }
}
