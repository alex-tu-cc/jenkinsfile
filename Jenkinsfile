err_count = 0
unstable_count = 0
INJ_RECOVERY = "true"
cmd_before_plan = ""
skip_build_maas = ""
pipeline{
    agent any
    options{
        timeout(time: 6, unit: 'HOURS')
    }
    stages{
        stage('image build'){
            steps{
                script{
                    skip_build_iso = "false"
                    if ( "${IMAGE_NO}" != "lastSuccessfulBuild" || "${IMAGE_NO}" == "no-provision" || "${SKIP_BUILD_IMG}" == "true" ){
                        skip_build_iso = "true"
                    }
                    if ( "${skip_build_iso}" != "true" ){
                        echo "Starting ${TARGET_IMG} image build..."
                        def imagebuild = build job: "${TARGET_IMG}",
                        parameters: [[$class: 'StringParameterValue', name: 'VAGRANT_CLEAN', value: "${VAGRANT_CLEAN}"],
                                    [$class: 'StringParameterValue', name: 'SPFISH', value: "false"]
                                    ]
                        IMAGE_NO = imagebuild.getNumber()
                    } else {
                        echo "Skip build ISO."
                    }
                }
            }
        }
        stage('test on VM'){
            steps{
                script{
                    if ( "${SKIP_BUILD_IMG}" != "true" ){
                        echo 'Starting to make iso to MAAS image.'
                        build job: 'sanity-3-testflinger-dell-bto-focal-fossa-990000-00001-staging',
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                                    [$class: 'StringParameterValue', name: 'EXCLUDE_TASK', value: ".*miscellanea/debsums .*somerville/platform-meta-test .*miscellanea/screen-pkg-not-public"],
                                    [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-software-test"],
                                    [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "${cmd_before_plan}"],
                                    [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                                    [$class: 'StringParameterValue', name: 'GITBRANCH_OEM_SANITY', value: "${GITBRANCH_OEM_SANITY}"],
                                    [$class: 'StringParameterValue', name: 'INJ_RECOVERY', value: "${INJ_RECOVERY}"]
                                     ]
                    }
                }
            }
        }
    }
    post {
        always {
            script {
                //if( err_count == 0 ) {
                //    echo "Running infrastructure-dispatch-auto-release"
                //    build job: 'infrastructure-dispatch-auto-release',
                //    parameters: [string(name: 'UPSTREAM', value: '${JOB_NAME}'),
                //                string(name: 'SANITY_LINK', value: '${JOB_NAME}')
                //                ]
                //}
                echo "${err_count}"
                echo "${unstable_count}"
                if (err_count > 0) {
                    currentBuild.result = "FAILURE"
                }
                else if (unstable_count > 0) {
                    currentBuild.result = "UNSTABLE"
                }
                else{
                    currentBuild.result = "SUCCESS"
                }
            }
        }
    }
}




