pipeline {
    agent none
    stages {
        stage('ubuntu') {
            agent {
                docker {
                    label 'docker'
                    image 'ubuntu:18.04'
                }
            }
            steps {
                sh 'cat /etc/*-release'
            }
        }
        stage('ubuntu-2') {
            agent {
                docker {
                    label 'docker'
                    image 'ubuntu:18.04'
                }
            }
            steps {
                sh 'cat /etc/*-release'
            }
        }
    }
}
