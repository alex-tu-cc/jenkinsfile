err_count = 0
unstable_count = 0
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
        stage('iso to MAAS compatible image'){
            steps{
                script{
                    skip_build_maas = "false"
                    if ( "${IMAGE_NO}" == "no-provision" || "${SKIP_BUILD_IMG}" == "true" ){
                        skip_build_maas = "true"
                    }
                    if ( "${skip_build_maas}" != "true" ){
                        echo 'Starting to make iso to MAAS image.'
                        build job: 'sanity-1-generic-iso-to-maas-img',
                        parameters: [[$class: 'StringParameterValue', name: 'jenkins_job', value: "${TARGET_IMG}"],
                                    [$class: 'StringParameterValue', name: 'build_no', value: "${IMAGE_NO}"],
                                    [$class: 'StringParameterValue', name: 'gitbranch', value: "${GITBRANCH_OEM_SANITY}"],
                                    [$class: 'StringParameterValue', name: 'device_id', value: "generic"]
                                    ]
                    } else {
                        echo "Skip build MaaS image."
                    }
                }
            }
        }
        stage('start testflinger tests'){
            steps{
                parallel(
                    //job201904-26982start
                    job20190426982:{
                        script{
                            echo 'sanity check for 201904-26982'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-edge-alloem-201904-26982-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                                         [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                                        [$class: 'StringParameterValue', name: 'GITBRANCH_OEM_SANITY', value: "${GITBRANCH_OEM_SANITY}"],
                                         [$class: 'StringParameterValue', name: 'INJ_RECOVERY', value: "${INJ_RECOVERY}"]
                                        ]
                            if (result.getResult() == "UNSTABLE"){
                                echo 'The result of sanity check of 201904-26982 is UNSTABLE'
                                unstable_count++
                            }
                            else if (result.getResult() == "FAILURE"){
                                echo 'The result of sanity check of 201904-26982 is FAILURE'
                                err_count++
                            }
                        }
                    },
                    //job201904-26982end
                    //job202102-02468start
                    //job20210202468:{
                    //    script{
                    //        echo 'sanity check for 202102-02468'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-edge-alloem-202102-02468-staging', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                    //                     [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                    //                     [$class: 'StringParameterValue', name: 'GITBRANCH_OEM_SANITY', value: "${GITBRANCH_OEM_SANITY}"],
                    //                     [$class: 'StringParameterValue', name: 'INJ_RECOVERY', value: "${INJ_RECOVERY}"]
                    //                    ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of 202102-02468 is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of 202102-02468 is FAILURE'
                    //            err_count++
                    //        }
                    //    }
                    //},
                    //job202102-02468end
                    //AppendNewJobHere
                )
            }    
        }
    }
    post {
        always {
            script {
                if( err_count == 0 ) {
                    echo "Running infrastructure-dispatch-auto-release"
                    build job: 'infrastructure-dispatch-auto-release',
                    parameters: [string(name: 'UPSTREAM', value: '${JOB_NAME}'),
                                string(name: 'SANITY_LINK', value: '${JOB_NAME}')
                                ]
                }
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


