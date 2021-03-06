
pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        LP_BIONIC_BASE="1833388"
        LP_BEAVER_OSP1="1838519"
        LP_BEAVER_OSP1_OLD="1854911"
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
        stage('fish-fix-manifest') {
            agent {
                label 'docker'
            }
            steps {
                fish_fix_manifest();
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
        } catch(e) {
            echo "exception = " + e
            currentBuild.result = 'FAILURE'
        }
    }
}
def fish_fix_manifest() {
    env.new_pkgs="true"
    script {
        try {
            copyArtifacts(
            projectName: "${JOB_NAME}",
            filter: "artifacts/*.tar.gz",
            target: 'latest_build',
            selector: specific("${BUILD_NUMBER}"));
        } catch(e) {
            echo "Not a successful build, so only fish-manifest to stagings"
            env.new_pkgs="false"
        }
        try {
            sh '''#!/bin/bash
                set -ex
                docker run -d -t --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot bash
                # a workaround to wait credential is ready and FishInitFile is there
                while [ -z "$(docker exec -t oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} ls | grep FishInitFile)" ]; do
                    sleep 10
                done
                if [ "${new_pkgs}" == "true" ]; then
                    find latest_build
                    bionic_base_fish_tarball="$(find latest_build -name "*_fish1.tar.gz" | grep bionic-base)"
                    beaver_osp1_fish_tarball="$(find latest_build -name "*_fish1.tar.gz" | grep beaver-osp1)"
                    echo fish-fix $fish_tarball
                    docker cp $bionic_base_fish_tarball oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}:/home/oem-taipei-bot/
                    docker cp $beaver_osp1_fish_tarball oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}:/home/oem-taipei-bot/
                    bionic_base_target_fish=$(basename $bionic_base_fish_tarball)
                    beaver_osp1_target_fish=$(basename $beaver_osp1_fish_tarball)
                    # host tarball on lp ticket
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "ls"
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "yes| fish-fix --nodep -b -f $bionic_base_target_fish -c misc $LP_BIONIC_BASE"
                    docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "yes| fish-fix --nodep -b -f $beaver_osp1_target_fish -c misc $LP_BEAVER_OSP1"
                fi

                # land the fish to staging manifest
                docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "fish-manifest -b -p somerville -r bionic -e -c --target bionic-master-staging  bionic-master --postRTS -u $LP_BIONIC_BASE"
                docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "fish-manifest -b -p somerville -r bionic -e -c --target beaver-osp1-staging  beaver-osp1 --postRTS -u $LP_BEAVER_OSP1 --delete $LP_BEAVER_OSP1_OLD"

                docker stop oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                docker rm oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
            '''
        } catch (e) {
            error("exception:" + e)
        }
    }
}
