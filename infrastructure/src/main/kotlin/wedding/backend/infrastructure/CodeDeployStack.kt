package wedding.backend.infrastructure

import software.amazon.awscdk.*
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup
import software.amazon.awscdk.services.codebuild.*
import software.amazon.awscdk.services.codedeploy.*
import software.amazon.awscdk.services.codepipeline.Artifact
import software.amazon.awscdk.services.codepipeline.Pipeline
import software.amazon.awscdk.services.codepipeline.PipelineProps
import software.amazon.awscdk.services.codepipeline.StageProps
import software.amazon.awscdk.services.codepipeline.actions.CodeBuildAction
import software.amazon.awscdk.services.codepipeline.actions.CodeBuildActionProps
import software.amazon.awscdk.services.codepipeline.actions.CodeDeployServerDeployAction
import software.amazon.awscdk.services.codepipeline.actions.CodeDeployServerDeployActionProps
import software.amazon.awscdk.services.codepipeline.actions.CodeStarConnectionsSourceAction
import software.amazon.awscdk.services.codepipeline.actions.CodeStarConnectionsSourceActionProps
import software.constructs.Construct

val CODE_DEPLOY_EC2_TAG = Pair("code-deploy", "wedding-backend")

class CodeDeployStack(scope: Construct, id: String, props: StackProps) : Stack(scope, id, props)  {
    init {
        val deployApplication = ServerApplication(this, "wedding-CodeDeployApplication", ServerApplicationProps.builder()
            .applicationName("wedding-backend").build())

        val deploymentGroup = ServerDeploymentGroup(this, "CodeDeployDeploymentGroup", ServerDeploymentGroupProps.builder()
            .application(deployApplication)
            .deploymentGroupName("DefaultDeploymentGroup")
            .installAgent(true)
            .ec2InstanceTags(InstanceTagSet(mapOf(CODE_DEPLOY_EC2_TAG.let { Pair(it.first, listOf(it.second)) })))
            .autoRollback(AutoRollbackConfig.builder()
                .stoppedDeployment(true)
                .failedDeployment(true)
                .build())
            .build())

        val codeBuildProject = PipelineProject(this, "DefaultBuildProject", PipelineProjectProps.builder()
            .concurrentBuildLimit(1)
            .buildSpec(BuildSpec.fromSourceFilename("buildspec.yml"))
            .environment(BuildEnvironment.builder()
                .computeType(ComputeType.SMALL)
                .buildImage(LinuxBuildImage.AMAZON_LINUX_2_4)
                .build())
            .build())


        val sourceArtifact = Artifact("sourceArtifact")
        val buildArtifact = Artifact("buildArtifact")

      Pipeline(this, "DefaultPipeline", PipelineProps.builder()
            .pipelineName("DefaultPipeline")
            .crossAccountKeys(false)
            .stages(listOf(
                StageProps.builder()
                    .stageName("Source")
                    .actions(listOf(
                        CodeStarConnectionsSourceAction(CodeStarConnectionsSourceActionProps.builder()
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
                        CodeBuildAction(CodeBuildActionProps.builder()
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
                        CodeDeployServerDeployAction(CodeDeployServerDeployActionProps.builder()
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