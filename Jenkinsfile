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
        stage('oem-taipei-bot') {
            agent {
                label 'docker'
                }
            }
            steps {
                sh 'docker run --rm -it -h oem-taipei-bot --volumes-from docker-volumes somerville-jenkins.cctu.space:5000/oem-taipei-bot "echo yes"'
            }
        }
        stage('oem-taipei-bot') {
            agent {
                docker {
                    label 'docker'
                    image 'somerville-jenkins.cctu.space:5000/oem-taipei-bot'
                    args  '-h oem-taipei-bot --volumes-from docker-volumes'
                }
            }
            steps {
                sh 'cat /etc/*-release'
                sh 'fish-fix --help'
                sh 'tail -f /var/log/apt/term.log'
            }
        }
    }
}
