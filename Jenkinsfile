
pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        TARGET_DEB = "thermald"
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
                       label 'docker'
                    }
                    steps {
                        sh 'cat /etc/*-release'
                    }
                }
                stage('bionic-base') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="master"
                    }
                    steps {
                        sh '''
                            mkdir -p ${OUTDIR}
                            mkdir -p artifacts
                            rm -rf ${OUTDIR}/*
                            eval ${RUN_DOCKER_TAIPEI_BOT} \\"git clone -b test-jenkins git+ssh://oem-taipei-bot@git.launchpad.net/~oem-solutions-group/oem-dev-tools/+git/lp-fish-tools \\&\\& lp-fish-tools/bin/pack-fish.sh --base bionic-base --template ${TEMPLATE} --deb ${TARGET_DEB} --outdir ${OUTDIR}\\"
                            cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./artifacts/update-pkgs-`date +%Y%m%d`_fish1.tar.gz
                            tar xf ${TEMPLATE}_fish1.tar.gz ./artifacts/prepackage.dell
                            rm -rf ${OUTDIR}
                        '''
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'artifacts/*'
                        }
                    }
                }
            }
        }
    }
}

