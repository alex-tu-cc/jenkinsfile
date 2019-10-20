
pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        LP_BIONIC_BASE="1698071"
        LP_BEAVER_OSP1="1698071"
    }
    stages {
        stage('prepare') {
            agent {
                label 'docker'
            }
            steps {
                script {
                    deleteDir()
                }
            }
        }
        stage('parallel') {
            parallel {
                stage('beaver-osp1') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="upgrade"
                    }
                    steps {
                        pack_fish();
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'artifacts/*', fingerprint: true
                            echo "[${STAGE_NAME}] success and pushing artifacts"
                        }
                    }
                }
                stage('bionic-base') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="upgrade"
                    }
                    steps {
                        pack_fish();
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: 'artifacts/*', fingerprint: true
                            echo "[${STAGE_NAME}] success and pushing artifacts"
                        }
                    }
                }
            }
        }
    }
}

def pack_fish() {
    script {
        try {
            copyArtifacts(
            projectName: "${JOB_NAME}",
            filter: "artifacts/*.dell",
            target: 'latest_build',
            selector: lastSuccessful());
        } catch(e) {
            echo "No lastSuccessful build, let treat this build as 1st build"
        }

        try {
            status = sh(returnStatus: true,
                script: '''#!/bin/bash
                set -ex
                mkdir -p ${OUTDIR}
                mkdir -p artifacts
                rm -rf  artifacts/*
                eval ${RUN_DOCKER_TAIPEI_BOT} \\"pack-fish.sh --base ${STAGE_NAME} --template ${TEMPLATE} --outdir ${OUTDIR}\\"
                cp ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz
                tar -C artifacts -xf ${OUTDIR}/${TEMPLATE}_fish1.tar.gz ./prepackage.dell
                mv artifacts/prepackage.dell artifacts/${GIT_BRANCH##origin/}-${STAGE_NAME}-`date +%Y%m%d`_fish1.tar.gz.prepackage.dell
                [ -d latest_build ] && [ \"$(find artifacts latest_build -name ${GIT_BRANCH##origin/}-${STAGE_NAME}-*dell | xargs md5sum |cut -d ' ' -f1 | uniq | wc -l)\" == "1" ] && ret=2
                rm -rf ${OUTDIR} latest_build
                exit $ret
            ''')
            echo "status = " + status
            if ( status == 1 ) {throw new Exception("packing fish failed somewhere!")}
            if ( status == 2 ) {unstable('no new package need to be updated'); echo "set UNSTABLE"}
        } cache(e) {
            echo "exception = " + e
            currentBuild.result = 'FAILURE'
        }
    }
}
