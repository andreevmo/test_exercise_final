pipeline {

    agent any

    parameters {
        booleanParam(name: 'IS_SELENOID', defaultValue: true, description: 'Use selenoid for tests')
        booleanParam(name: 'IS_VIDEO', defaultValue: false, description: 'Use video recording on selenoid')
        choice(name: 'TEST_SUITE', choices: ['*', 'web', 'api'], description: 'Choice test suite for run (* = all test)')
    }

    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '14', numToKeepStr: '5')
    }

    stages {

        stage('GIT') {
          steps {
            git branch: 'main', url: 'https://github.com/andreevmo/test_exercise_final.git'
          }
        }

        stage('RUN TESTS') {
          steps {
            withMaven(globalMavenSettingsConfig: '', jdk: '', maven: 'Maven_3.9.4', mavenSettingsConfig: '', traceability: true) {
                sh "mvn clean '-Dtest=ru.andreev.${TEST_SUITE}.*Test' test"
            }

          }
        }

    }

    post {
        always {
            allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
        }
    }
}