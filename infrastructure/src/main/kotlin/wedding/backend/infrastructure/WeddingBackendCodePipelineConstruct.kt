package wedding.backend.infrastructure

import software.amazon.awscdk.services.autoscaling.AutoScalingGroup
import software.amazon.awscdk.services.codebuild.*
import software.amazon.awscdk.services.codedeploy.*
import software.amazon.awscdk.services.codepipeline.Artifact
import software.amazon.awscdk.services.codepipeline.Pipeline
import software.amazon.awscdk.services.codepipeline.PipelineProps
import software.amazon.awscdk.services.codepipeline.StageProps
import software.amazon.awscdk.services.codepipeline.actions.*
import software.constructs.Construct

class WeddingBackendCodePipelineConstruct(scope: Construct, env: String, asg: AutoScalingGroup) :
    Construct(scope, "${scope.node.id}-code-pipeline") {
        init {
            val deployApplication = ServerApplication(this, "${this.node.id}-deploy-application")

            val deploymentGroup = ServerDeploymentGroup(this, "${this.node.id}-deployment-group", ServerDeploymentGroupProps.builder()
                .application(deployApplication)
                .installAgent(true)
                .autoScalingGroups(listOf(asg))
                .autoRollback(
                    AutoRollbackConfig.builder()
                    .stoppedDeployment(true)
                    .failedDeployment(true)
                    .build())
                .build())

            val codeBuildProject = PipelineProject(this, "${this.node.id}-build-project", PipelineProjectProps.builder()
                .concurrentBuildLimit(1)
                .buildSpec(BuildSpec.fromSourceFilename("buildspec.yml"))
                .environment(
                    BuildEnvironment.builder()
                    .computeType(ComputeType.SMALL)
                    .buildImage(LinuxBuildImage.AMAZON_LINUX_2_4)
                    .build())
                .build())


            val sourceArtifact = Artifact("sourceArtifact")
            val buildArtifact = Artifact("buildArtifact")

            Pipeline(this, "${this.node.id}-pipeline", PipelineProps.builder()
                .pipelineName("${env}-wedding-backend-pipeline")
                .crossAccountKeys(false)
                .stages(listOf(
                    StageProps.builder()
                        .stageName("Source")
                        .actions(listOf(
                            CodeStarConnectionsSourceAction(
                                CodeStarConnectionsSourceActionProps.builder()
                                .actionName("Github_Source")
                                .owner("ThatRanga")
                                .repo("wedding-backend")
                                .branch("main")
                                .output(sourceArtifact)
                                .connectionArn("arn:aws:codestar-connections:ap-southeast-2:781525612065:connection/bb640706-5030-48f2-8699-fa0ae5f65a11")
                                .triggerOnPush(false)
                                .build()
                            )
                        ))
                        .build(),
                    StageProps.builder()
                        .stageName("Build")
                        .actions(listOf(
                            CodeBuildAction(
                                CodeBuildActionProps.builder()
                                .actionName("Build")
                                .input(sourceArtifact)
                                .project(codeBuildProject)
                                .outputs(listOf(buildArtifact))
                                .build())
                        ))
                        .build(),
                    StageProps.builder()
                        .stageName("Deploy")
                        .actions(listOf(
                            CodeDeployServerDeployAction(
                                CodeDeployServerDeployActionProps.builder()
                                .actionName("Deploy_EC2")
                                .deploymentGroup(deploymentGroup)
                                .input(buildArtifact)
                                .build()
                            )
                        ))
                        .build()
                ))
                .build())
        }
    }