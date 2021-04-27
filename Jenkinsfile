err_count = 0
unstable_count = 0
INJ_RECOVERY = "true"
cmd_before_plan = ""
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
                    skip_build_maas = "true"
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
        stage('test on VM'){
            steps{
                script{
                    if ( "${SKIP_BUILD_IMG}" != "true" ){
                        echo 'Starting to make iso to MAAS image.'
                        build job: 'sanity-3-testflinger-dell-bto-focal-fossa-990000-00001-staging-testing',
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                                    [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-software-test"],
                                    [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "${cmd_before_plan}"],
                                    [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                                    [$class: 'StringParameterValue', name: 'INJ_RECOVERY', value: "${INJ_RECOVERY}"]
                                     ]
                    }
                }
            }
        }
        stage('start testflinger tests'){
            steps{
                parallel(
                    //job990000-00001start
                    //job99000000001:{
                    //    script{
                    //        echo 'sanity check for VM vm-testing-somerville'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-990000-00001-staging-testing', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                    //                [$class: 'StringParameterValue', name: 'PLAN', value: "ls"],
                    //                [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "${cmd_before_plan}"],
                    //                [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                    //                [$class: 'StringParameterValue', name: 'INJ_RECOVERY', value: "${INJ_RECOVERY}"],
                    //                [$class: 'StringParameterValue', name: 'DEVICE_IP', value: "192.168.101.216"]
                    //                 ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of VM vm-testing-somerville is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of VM vm-testing-somerville is FAILURE'
                    //            err_count++
                    //        } else {
                    //            echo 'The result of sanity check of VM vm-testing-somerville is PASS'
                    //        }
                    //    }
                    //},
                    //job802010-28303start
                    job80201028303:{
                        script{
                            echo 'sanity check for 802010-28303 RKL I+N EVT'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-edge-staging-802010-28303-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                                    [$class: 'StringParameterValue', name: 'EXCLUDE_TASK', value: ".*miscellanea/debsums .*somerville/platform-meta-test"],
                                    [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                                    [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "${cmd_before_plan}"],
                                    [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                                    [$class: 'StringParameterValue', name: 'INJ_RECOVERY', value: "${INJ_RECOVERY}"]
                                     ]
                            if (result.getResult() == "UNSTABLE"){
                                echo 'The result of sanity check of 802010-28303 RKL I+N EVT is UNSTABLE'
                                unstable_count++
                            }
                            else if (result.getResult() == "FAILURE"){
                                echo 'The result of sanity check of 802010-28303 RKL I+N EVT is FAILURE'
                                err_count++
                            }
                        }
                    },
                    //job201901-26774start
                    job20190126774:{
                        script{
                            echo 'sanity check for 201901-26774(I+N not support RTD3)'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-nvstaging-201901-26774-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                                [$class: 'StringParameterValue', name: 'EXCLUDE_TASK', value: ".*miscellanea/debsums .*somerville/platform-meta-test"],
                                [$class: 'StringParameterValue', name: 'PLAN', value: "graphics-dgpu-auto-switch-testing"],
                                [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                                [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "${cmd_before_plan}"],
                                [$class: 'StringParameterValue', name: 'INJ_RECOVERY', value: "${INJ_RECOVERY}"]
                                ]
                            if (result.getResult() == "UNSTABLE"){
                                echo 'The result of sanity check of 201901-26774(I+N not support RTD3) is UNSTABLE'
                                unstable_count++
                            }
                            else if (result.getResult() == "FAILURE"){
                                echo 'The result of sanity check of 201901-26774(I+N not support RTD3) is FAILURE'
                                err_count++
                            }
                            else {
                                echo 'The result of sanity check of 201901-26774(I+N not support RTD3) is PASS'
                            }
                        }
                    },
                    //job201901-26774end
                    //sanity-3-testflinger-dell-bto-focal-fossa-staging-902005-27873-staging
                    //job902005-27873start
                    //job90200527873:{
                    //    script{
                    //        echo 'sanity check for 902005-27873 I+N RTD3'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-staging-902005-27873-staging', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                    //                [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                    //                [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "${cmd_before_plan}"],
                    //                [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                    //                [$class: 'StringParameterValue', name: 'INJ_RECOVERY', value: "${INJ_RECOVERY}"]
                    //                 ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of 902005-27873 I+N RTD3 is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of 902005-27873 I+N RTD3 is FAILURE'
                    //            err_count++
                    //        } else {
                    //            echo 'The result of sanity check of 902005-27873 I+N RTD3 is SUCCESS'
                    //        }
                    //    }
                    //},
                    //job20181026535start
                    job20181026535:{
                        script{
                            echo 'sanity check for 201810-26535'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-201810-26535-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                              [$class: 'StringParameterValue', name: 'EXCLUDE_TASK', value: ".*miscellanea/debsums .*somerville/platform-meta-test"],
                              [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                              [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                              [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "${cmd_before_plan}"],
                              [$class: 'StringParameterValue', name: 'INJ_RECOVERY', value: "${INJ_RECOVERY}"]
                              ]
                            if (result.getResult() == "UNSTABLE"){
                                echo 'The result of sanity check of 201810-26535 is UNSTABLE'
                                unstable_count++
                            }
                            else if (result.getResult() == "FAILURE"){
                                echo 'The result of sanity check of 201810-26535 is FAILURE'
                                err_count++
                            }
                            else {
                                echo 'The result of sanity check of 201810-26535 is PASS'
                            }
                        }
                    },

                    //// sanity-3-testflinger-dell-bto-focal-fossa-edge-202101-28624-staging
                    ////job202101-28624end
                    ////job20210128624start
                    //job20210128624:{
                    //    script{
                    //        echo 'sanity check for 202101-28624(RKL I+N DVT2)'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-edge-202101-28624-staging', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                    //          [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                    //          [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"],
                    //          [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "${cmd_before_plan}"]
                    //          ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of 202101-28624(RKL I+N DVT2) is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of 202101-28624(RKL I+N DVT2) is FAILURE'
                    //            err_count++
                    //        }
                    //        else {
                    //            echo 'The result of sanity check of 202101-28624(RKL I+N DVT2) is PASS'
                    //        }
                    //    }
                    //},
                    //job201904-26948start
                    //job20190426948:{
                    //    script{
                    //        echo 'sanity check for 201904-26948'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-staging-201904-26948-staging', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMG_TYPE', value: "generic"]
                    //                    ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of 201904-26948 is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of 201904-26948 is FAILURE'
                    //            err_count++
                    //        }
                    //    }
                    //},
                    //job201904-26948end
                    //job201810-26588start
                    //job20181026588:{
                    //    script{
                    //        echo 'sanity check for 201810-26588'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-staging-201810-26588-staging', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMG_TYPE', value: "generic"],
                    //        		   [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                    //        		   [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "prepare-checkbox-sanity --side-load https://github.com/alex-tu-cc/plainbox-provider-resource.git#refine-for-nv-rtd3 --side-load https://github.com/alex-tu-cc/plainbox-provider-checkbox.git#refine-for-nv-rtd3"],
                    //        		   [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "dell-bto-focal-fossa-staging"]
                    //                    ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of 201810-26588 is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of 201810-26588 is FAILURE'
                    //            err_count++
                    //        }
                    //    }
                    //},
                    //job201810-26588end
                    //job201910-27444start
                    //job20191027444:{
                    //    script{
                    //        echo 'sanity check for 201910-27444'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-staging-201910-27444-staging', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMG_TYPE', value: "generic"]
                    //                    ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of 201910-27444 is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of 201910-27444 is FAILURE'
                    //           err_count++
                    //        }
                        //}
                    //},
                    //job201910-27444end
                    //job202004-27819start
                    //job20200427819:{
                    //    script{
                    //        echo 'sanity check for 202004-27819'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-staging-202004-27819-staging', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMG_TYPE', value: "generic"]
                    //                    ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of 202004-27819 is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of 202004-27819 is FAILURE'
                    //            err_count++
                    //        }
                    //    }
                    //},
                    //job202004-27819end
                    //job201708-25689start
                    //job20170825689:{
                    //    script{
                    //        echo 'sanity check for 201708-25689'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-staging-201708-25689-staging', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMG_TYPE', value: "generic"],
                    //        		   [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                    //        		   [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "prepare-checkbox-sanity --side-load https://github.com/alex-tu-cc/plainbox-provider-resource.git#refine-for-nv-rtd3 --side-load https://github.com/alex-tu-cc/plainbox-provider-checkbox.git#refine-for-nv-rtd3"],
                    //        		   [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "dell-bto-focal-fossa-staging"]
                    //                    ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of 201708-25689 is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of 201708-25689 is FAILURE'
                    //            err_count++
                    //        }
                    //    }
                    //},
                    //job201708-25689end
                    //AppendNewJobHere
                )
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




