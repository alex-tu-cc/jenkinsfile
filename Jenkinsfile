pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        TARGET_DEB = "plymouth upower network-manager thermald modemmanager dkms fwupd fwupd-signed pulseaudio bolt libasound2-data mutter gnome-shell gnome-settings-daemon"
        LP_NUM = "1838518"
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
                stage('bionic-base') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="master"
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
                stage('beaver-osp1') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="master"
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

        stage('fish-fix-manifest') {
            agent {
                label 'docker'
            }
            steps {
               script {
                    fish_fix_manifest();
                }
            }
        }
    }
}

def pack_fish() {
    script {
        sh "mkdir -p artifacts"
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
                set -xe
                mkdir -p ${OUTDIR}
                eval ${RUN_DOCKER_TAIPEI_BOT} \\"pack-fish.sh --base ${STAGE_NAME} --template ${TEMPLATE} --deb ${TARGET_DEB} --outdir ${OUTDIR}\\"
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
        } catch(e) {
            echo "exception = " + e
            currentBuild.result = 'FAILURE'
        }
    }
}

def fish_fix_manifest() {
    script {
        sh "rm -rf latest_build/*"
        try {
            copyArtifacts(
            projectName: "${JOB_NAME}",
            filter: "artifacts/*.tar.gz",
            target: 'latest_build',
            selector: specific("${BUILD_NUMBER}"));
        } catch(e) {
            error("No lastSuccessful build, we should be be here!")
        }
        try {
            sh '''#!/bin/bash
                 set -ex
                 find latest_build
                 fish_tarball="$(find latest_build -name "*_fish1.tar.gz" | grep bionic-base --max-count=1)"
                 echo fish-fix $fish_tarball
                 docker run -d -t --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot bash
                 docker cp $fish_tarball oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}:/home/oem-taipei-bot/
                 target_fish=$(basename $fish_tarball)
                 # a workaround to wait credential is ready and FishInitFile is there
                 sleep 15
                 # host tarball on lp ticket
                 docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "ls"
                 docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "yes| fish-fix --nodep -b -f $target_fish -c misc $LP_NUM"

                 # land the fish to staging manifest
                 docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "fish-manifest -b -p somerville -r bionic -e -c --target bionic-master-staging  bionic-master --postRTS -u $LP_NUM"
                 docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "fish-manifest -b -p somerville -r bionic -e -c --target beaver-osp1-staging  beaver-osp1 --postRTS -u $LP_NUM"

                 docker stop oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                 docker rm oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
            '''
        } catch (e) {
            error("exception:" + e)
        }
     }
}

