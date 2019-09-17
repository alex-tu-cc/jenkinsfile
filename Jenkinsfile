
pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        TARGET_DEB=""
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
                        sh 'rm -rf artifacts/*'
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
                        TEMPLATE="checkbox-pkgs"
                        TEMPLATE_REPO="git+ssh://oem-taipei-bot@git.launchpad.net/~oem-solutions-engineers/somerville/+git/fish-template"
                    }
                    steps {
                        sh '''#!/bin/bash
                            set -ex
                            mkdir -p ${OUTDIR}
                            mkdir -p artifacts
                            rm -rf artifacts/*
                            eval ${RUN_DOCKER_TAIPEI_BOT} \\"git clone git+ssh://oem-taipei-bot@git.launchpad.net/~oem-solutions-group/oem-dev-tools/+git/lp-fish-tools -b alext-test \\&\\& lp-fish-tools/bin/pack-fish.sh --base bionic-base --template ${TEMPLATE} --template-repo ${TEMPLATE_REPO} --deb ${TARGET_DEB} --outdir ${OUTDIR}\\"
                            cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz
                            tar -C artifacts -xf ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./prepackage.dell
                            mv artifacts/prepackage.dell artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz.prepackage.dell
                            rm -rf ${OUTDIR}
                        '''
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'artifacts/*', fingerprint: true
                        }
                    }
                }
            }
        }
    }
}

