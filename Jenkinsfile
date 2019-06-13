
pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        TARGET_DEB = "--deb plymouth --deb upower --deb network-manager --deb thermald --deb modemmanager --deb dkms"
    }
    stages {
        stage('prepare') {
            agent {
                label 'docker'
            }
            steps {
                script {
                    try {
                        sh 'docker ps | grep docker-volumes'
                    } catch (e) {
                        sh 'echo error!'
                    }
                }
            }
        }
        stage('parallel') {
            parallel {
                stage('oem-taipei-bot-0') {
                    agent {
                        docker {
                            label 'docker'
                            image "${env.DOCKER_REPO}/oem-taipei-bot"
                            args 'fish-fix help'
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
                        script {
                                sh 'env'
                                sh 'eval ${RUN_DOCKER_TAIPEI_BOT} \"${TARGET_DEB}\"'
                        }
                    }
                }
                stage('oem-taipei-bot-2') {
                    agent {
                        label 'docker'
                    }
                    steps {
                        script {
                                sh 'env'
                                sh 'docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot \"fish-fix help\"'
                        }
                    }
                }
            }
        }
    }
}
