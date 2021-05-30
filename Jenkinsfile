err_count = 0
unstable_count = 0
cmd_before_plan = "wget -O /home/ubuntu/checkbox-sideload-dev-staging.json  http://office.cctu.space:3000/alextu/internal-db/raw/branch/master/checkbox-sideload-dev-staging.json; prepare-checkbox-sanity --side-load-conf /home/ubuntu/checkbox-sideload-dev-staging.json"
pipeline{
    agent any
    options{
        timeout(time: 6, unit: 'HOURS')
    }
    stages{
        stage('image build'){
            steps{
                script{
                    if ( "${SKIP_BUILD_IMG}" != "true" ){
                        echo 'Starting ${TARGET_IMG} Sanity Check...'
                        def imagebuild = build job: "${TARGET_IMG}",
                        parameters: [[$class: 'StringParameterValue', name: 'VAGRANT_CLEAN', value: "${VAGRANT_CLEAN}"],
                                     [$class: 'StringParameterValue', name: 'SPFISH', value: "false"]
                                    ]
                        if ( "${IMAGE_NO}" == "lastSuccessfulBuild" ) IMAGE_NO = imagebuild.getNumber()
                    }
                }
            }
        }
        stage('iso to MAAS compatible image'){
            steps{
                script{
                    if ( "${SKIP_BUILD_IMG}" != "true" ){
                        echo 'Starting to make iso to MAAS image.'
                        build job: 'sanity-1-generic-iso-to-maas-img',
                        parameters: [[$class: 'StringParameterValue', name: 'jenkins_job', value: TARGET_IMG],
                                    [$class: 'StringParameterValue', name: 'build_no', value: "${IMAGE_NO}"],
                                    [$class: 'StringParameterValue', name: 'device_id', value: "generic"],
                                    [$class: 'StringParameterValue', name: 'gitbranch', value: "master"]
                                    ]
                    }
                }
            }
        }
        stage('start testflinger tests'){
            steps{
                parallel(
                    //sanity-3-testflinger-dell-bto-focal-fossa-rklstaging-902010-28303-staging
                    //job802010-28303start
                    job80201028303:{
                        script{
                            echo 'sanity check for 802010-28303 RKL I+N EVT'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-edge-staging-802010-28303-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                                    [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                                    [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "${cmd_before_plan}"],
                                    [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"]
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
                    ////job202010-28303start
                    //job20201028303:{
                    //    script{
                    //        echo 'sanity check for 202010-28303 RKL I+N'
                    //        def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-rklstaging-202010-28303-staging', propagate: false,
                    //        parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                    //        		   [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                    //        		   [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "prepare-checkbox-sanity --side-load https://github.com/alex-tu-cc/plainbox-provider-resource.git#refine-for-nv-rtd3 --side-load https://github.com/alex-tu-cc/plainbox-provider-checkbox.git#refine-for-nv-rtd3"],
                    //        		   [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "dell-bto-focal-fossa-rklstaging"]
                    //                    ]
                    //        if (result.getResult() == "UNSTABLE"){
                    //            echo 'The result of sanity check of 202010-28303 is UNSTABLE'
                    //            unstable_count++
                    //        }
                    //        else if (result.getResult() == "FAILURE"){
                    //            echo 'The result of sanity check of 202010-28303 is FAILURE'
                    //            err_count++
                    //        }
                    //    }
                    //},

                    //job201708-25689start
                    job20170825689:{
                        script{
                            echo 'sanity check for 201708-25689(I+A)'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-staging-201708-25689-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                            		   [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                            		   [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "prepare-checkbox-sanity --side-load https://git.launchpad.net/~alextu/plainbox-provider-pc-sanity#nvidia-rtd3-checking-v3"],
                            		   [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"]
                                        ]
                            if (result.getResult() == "UNSTABLE"){
                                echo 'The result of sanity check of 201708-25689 is UNSTABLE'
                                unstable_count++
                            }
                            else if (result.getResult() == "FAILURE"){
                                echo 'The result of sanity check of 201708-25689 is FAILURE'
                                err_count++
                            }
                        }
                    },
                    //job201708-25689end
                    //job202005-27873start
                    job20200527873:{
                        script{
                            echo 'sanity check for fossa-nvstaging-202005-27873(RTD3)'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-nvstaging-202005-27873-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                                           [$class: 'StringParameterValue', name: 'PLAN', value: "graphics-dgpu-auto-switch-testing"],
                            		   [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "prepare-checkbox-sanity --side-load https://git.launchpad.net/~alextu/plainbox-provider-pc-sanity#nvidia-rtd3-checking-v3"],
                                           [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"]
                                        ]
                            if (result.getResult() == "UNSTABLE"){
                                echo 'The result of sanity check of 201901-26774 is UNSTABLE'
                                unstable_count++
                            }
                            else if (result.getResult() == "FAILURE"){
                                echo 'The result of sanity check of 201901-26774 is FAILURE'
                                err_count++
                            }
                        }
                    },
                    //job201901-26774start
                    job20190126774:{
                        script{
                            echo 'sanity check for fossa-nvstaging-201901-26774(nonRTD3)'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-nvstaging-201901-26774-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                            		   [$class: 'StringParameterValue', name: 'PLAN', value: "graphics-dgpu-auto-switch-testing"],
                            		   [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "prepare-checkbox-sanity --side-load https://git.launchpad.net/~alextu/plainbox-provider-pc-sanity#dpkg-l-and-more-plan"],
                            		   [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"]
                                        ]
                            if (result.getResult() == "UNSTABLE"){
                                echo 'The result of sanity check of 201901-26774(nonRTD3) is UNSTABLE'
                                unstable_count++
                            }
                            else if (result.getResult() == "FAILURE"){
                                echo 'The result of sanity check of 201901-26774(nonRTD3) is FAILURE'
                                err_count++
                            }
                        }
                    },

                    // sanity-3-testflinger-dell-bto-focal-fossa-rklstaging-202009-28274-staging
                    //job202009-28274start
                    job20200928274:{
                        script{
                            echo 'sanity check for focal-fossa-rklstaging-202009-28274(RKL I+A)'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-rklstaging-202009-28274-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                            		   [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                            		   [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "prepare-checkbox-sanity --side-load https://git.launchpad.net/~alextu/plainbox-provider-pc-sanity#nvidia-rtd3-checking-v3"],
                            		   [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"]
                                        ]
                            if (result.getResult() == "UNSTABLE"){
                                echo 'The result of sanity check of 202009-28274(RKL I+A) is UNSTABLE'
                                unstable_count++
                            }
                            else if (result.getResult() == "FAILURE"){
                                echo 'The result of sanity check of 202009-28274(RKL I+A) is FAILURE'
                                err_count++
                            }
                        }
                    },
                    // sanity-3-testflinger-dell-bto-focal-fossa-rklstaging-202010-28295-staging
                    //job202010-28295start
                    job20201028295:{
                        script{
                            echo 'sanity check for focal-fossa-rklstaging-202010-28295(RKL I+N)'
                            def result = build job: 'sanity-3-testflinger-dell-bto-focal-fossa-rklstaging-202010-28295-staging', propagate: false,
                            parameters: [[$class: 'StringParameterValue', name: 'IMAGE_NO', value: "${IMAGE_NO}"],
                            		   [$class: 'StringParameterValue', name: 'PLAN', value: "pc-sanity-smoke-test"],
                            		   [$class: 'StringParameterValue', name: 'CMD_BEFOR_RUN_PLAN', value: "prepare-checkbox-sanity --side-load https://git.launchpad.net/~alextu/plainbox-provider-pc-sanity#nvidia-rtd3-checking-v3"],
                            		   [$class: 'StringParameterValue', name: 'TARGET_IMG', value: "${TARGET_IMG}"]
                                        ]
                            if (result.getResult() == "UNSTABLE"){
                                echo 'The result of sanity check of 202010-28295(RKL I+N) is UNSTABLE'
                                unstable_count++
                            }
                            else if (result.getResult() == "FAILURE"){
                                echo 'The result of sanity check of 202010-28295(RKL I+N) is FAILURE'
                                err_count++
                            }
                        }
                    },
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




