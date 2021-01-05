//def call(String project = 'somerville', String series, String target, String base, String update,String delete ) {
def call(Map config) {
    node {
        env.base = "${config.base}"
        env.project = "somerville"
        env.tag = "${config.tag}"
        env.target = "${config.target}"
        env.series = "${config.series}"
        env.update = "${config.update}"
        env.delete = "${config.delete}"
        script {
            env.DOCKER_REPO=globalVar.internalDockerRepo()
            env.DOCKER_VOL=globalVar.internalDockerVolum()
            try {
            sh '''#!/bin/bash
                set -ex
                RUN_DOCKER_TAIPEI_BOT="docker run --name oem-taipei-bot-${BUILD_TAG}-${STAGE_NAME} --rm -h oem-taipei-bot --volumes-from ${DOCKER_VOL} ${DOCKER_REPO}/oem-taipei-bot"
                if [ -n "${base}" ] && [ "${base}" != "null" ]; then
                    if [ "${series}" == "bionic" ]; then
                        command="fish-manifest -b -p ${project} -r ${series} -e -c --target ${target} ${base} --postRTS "
                    else
                        if [ -n "${tag}" ] && [ "${tag}" != "null" ]; then
                            command="fish-manifest --git -b -p ${project} -r ${series} -e -c -t ${tag} -a --target ${target} ${base} --postRTS "
                        else
                            command="fish-manifest --git -b -p ${project} -r ${series} -e -c --target ${target} ${base} --postRTS "
                        fi
                    fi
                else
                    # reflash master manifest
                    if [ "${series}" == "bionic" ]; then
                        command="fish-manifest -b -p ${project} -r ${series} -e -c --target ${target} ${target}"
                    else
                        command="fish-manifest --git -b -p ${project} -r ${series} -e -c --target ${target} ${target}"
                    fi
                fi
                if [ -n "${update}" ] && [ "${update}" != "null" ]; then
                    for i in ${update}; do
                        command="$command -u ${i}"
                    done
                fi
                if [ -n "${delete}" ] && [ "${delete}" != "null" ]; then
                    for i in ${delete}; do
                        command="$command --delete ${i}"
                    done
                fi
                # ignore all duplicated packages
                $RUN_DOCKER_TAIPEI_BOT "yes| $command"
            '''
            } catch (e) {
                error("exception:" + e)
            }
        }
    }
}
