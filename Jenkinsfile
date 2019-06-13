
pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        TARGET_DEB = "plymouth upower network-manager thermald modemmanager dkms"
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
                                sh 'eval ${RUN_DOCKER_TAIPEI_BOT} \"fish-fix help\"'
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
