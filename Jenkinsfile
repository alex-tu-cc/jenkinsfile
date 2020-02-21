pipeline {
    agent none
    environment {
        DOCKER_REPO = "somerville-jenkins.cctu.space:5000"
        RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-\${BUILD_TAG}-\${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from docker-volumes \${DOCKER_REPO}/oem-taipei-bot"
        LP_NUM = "1838518"
        RM_LP_NUM = "1854917"
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
                stage('get-latest-cid-mapping') {
                    agent {
                        label 'docker'
                    }
                    steps {
                        reload_cid_map();
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

def reload_cid_map() {
    script {
        try {
            status = sh(returnStatus: true,
            script: '''#!/bin/bash
                set -ex
                docker run -d -t --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} -h oem-taipei-bot --volumes-from docker-volumes ${DOCKER_REPO}/oem-taipei-bot bash
                # a workaround to wait credential is ready and FishInitFile is there
                sleep 15
                # host tarball on lp ticket
cat << EOF > do.sh
#!/bin/bash
set -x
GIT_SSH_COMMAND="ssh -p 10022" git clone git@office.cctu.space:alextu/internal-db.git
git clone git+ssh://git.launchpad.net/~lyoncore-team/lyoncore/+git/solution-db
export source="\$(find solution-db -name "cid_clabel_mapping.json")" && cp $source internal-db/cid_clabel_mapping.json
cd internal-db
GIT_SSH_COMMAND="ssh -p 10022" git push origin master
EOF
                docker cp $fish_tarball oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME}:/home/oem-taipei-bot/
                docker exec oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} bash -c "bash -c ./do.sh"
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
