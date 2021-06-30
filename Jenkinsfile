pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
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
                stage('ignore me') {
                    agent {
                        label 'docker'
                    }
                    environment {
                        OUTDIR="/srv/tmp/${BUILD_TAG}-${STAGE_NAME}"
                        TEMPLATE="master"
                    }
                    steps {
                        sh "echo ignore me"
                    }
                }
                stage('snapshot-checkbox-dev-verified') {
                    agent {
                        label 'docker'
                    }
                    steps {
                        trigger("50","alextu","tesst");
                    }
                    post {
                        success {
                            echo "[${STAGE_NAME}] success and pushing artifacts"
                        }
                    }
                }
            }
        }
    }
}

def trigger(String server, String user, String job) {
    script {
        try {
            status = sh(returnStatus: true,
            script: '''#!/bin/bash
                set -ex
                docker run -d -t --name fossa.collect-deps-${BUILD_TAG}-${STAGE_NAME} --entrypoint=bash somerville-jenkins.cctu.space:5000/fossa.collect-deps
cat << EOF > do.sh
#!/bin/bash
set -x
sudo add-apt-repository ppa:oem-taipei-bot/checkbox-snapshot-staging -y
sudo apt-get update;
apt-get install --dry-run prepare-checkbox-sanity 2>&1 | tee prepare-checkbox-sanity.list
apt-cache show \\$(apt-cache madison \\$(cat prepare-checkbox-sanity.list | grep Inst | awk '{print \\$2}' | xargs) | grep 'checkbox-snapshot-staging' | awk '{print \\$1}') | grep -E "(Package)|(Source)" | awk '{print \\$2}' | uniq > /tmp/src-pkg-list
EOF
                docker cp do.sh fossa.collect-deps-${BUILD_TAG}-${STAGE_NAME}:/tmp
                docker exec fossa.collect-deps-${BUILD_TAG}-${STAGE_NAME} bash -c "ls /tmp && cat /tmp/do.sh"
                docker exec fossa.collect-deps-${BUILD_TAG}-${STAGE_NAME} bash -c "bash /tmp/do.sh"
                docker cp fossa.collect-deps-${BUILD_TAG}-${STAGE_NAME}:/tmp/src-pkg-list .
                docker stop fossa.collect-deps-${BUILD_TAG}-${STAGE_NAME}
                docker rm fossa.collect-deps-${BUILD_TAG}-${STAGE_NAME}

                docker run -d -t --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot bash
                # a workaround to wait credential is ready and FishInitFile is there
                sleep 15
                # host tarball on lp ticket
cat << EOF > do.sh
#!/bin/bash
set -x
#sudo add-apt-repository ppa:checkbox-dev/ppa -y
sudo apt-get update; sudo apt-get install -y bzr ubuntu-dev-tools
bzr branch lp:ubuntu-archive-tools
cd ubuntu-archive-tools
#apt-get install --dry-run prepare-checkbox-sanity 2>&1 | tee prepare-checkbox-sanity.list
#apt-cache show \$(apt-cache madison \$(cat prepare-checkbox-sanity.list | grep Inst | awk '{print \$2}' | xargs) | grep 'checkbox-dev' | awk '{print \$1}') | grep -E "(Package)|(Source)" | awk '{print \$2}' | uniq > checkbox.list
#cat /tmp/log1 | grep -E "(Package)|(Source)" | awk '{print \\$2}' | uniq > /tmp/src-pkg-list
./copy-package \\$(cat /tmp/src-pkg-list | xargs) --from="ppa:oem-solutions-engineers/ubuntu/checkbox-snapshot-staging" --from-suit=focal --to="ppa:oem-solutions-engineers/ubuntu/checkbox-snapshot" --to-suite=focal -b -y --skip-missing
./copy-package plainbox-provider-pc-sanity --from=ppa:oem-solutions-engineers/ubuntu/checkbox-snapshot-staging --from-suit=focal --to=ppa:oem-solutions-engineers/ubuntu/plainbox-provider-pc-sanity-snapshot --to-suite=focal -b -y --skip-missing
EOF
                docker cp do.sh oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}:/home/oem-taipei-bot/
                docker cp src-pkg-list oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}:/tmp/
                docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "ls && cat ./do.sh"
                docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "bash ./do.sh"
                docker stop oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                docker rm oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                ''')
        } catch(e) {
            echo "exception = " + e
            currentBuild.result = 'FAILURE'
            sh '''#!/bin/bash
                docker stop oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
                docker rm oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}
            '''
        }
    }
}
