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
        stage('oem-taipei-bot-1') {
            agent {
                label 'docker'
            }
            steps {
                sh 'docker run --rm -h oem-taipei-bot --volumes-from docker-volumes somerville-jenkins.cctu.space:5000/oem-taipei-bot \"fish-fix help\"'
            }
        }
        stage('oem-taipei-bot-2') {
            agent {
                docker {
                    label 'docker'
                    image 'somerville-jenkins.cctu.space:5000/oem-taipei-bot'
                    args  '-h oem-taipei-bot --volumes-from docker-volumes'
                }
            }
            steps {
                sh 'echo yes'
                sh 'fish-fix --help'
                sh 'tail -f /var/log/apt/term.log'
            }
        }
    }
}
