package wedding.backend.infrastructure

import software.amazon.awscdk.BundlingOptions
import software.amazon.awscdk.BundlingOutput
import software.amazon.awscdk.DockerVolume
import software.amazon.awscdk.Duration
import software.amazon.awscdk.services.ec2.IVpc
import software.amazon.awscdk.services.iam.IRole
import software.amazon.awscdk.services.lambda.*
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.eventsources.S3EventSource
import software.amazon.awscdk.services.lambda.eventsources.S3EventSourceProps
import software.amazon.awscdk.services.logs.RetentionDays
import software.amazon.awscdk.services.s3.Bucket
import software.amazon.awscdk.services.s3.EventType
import software.amazon.awscdk.services.s3.assets.AssetOptions
import software.constructs.Construct

class WeddingBackendUserUploadLambdaConstruct(scope: Construct, vpc: IVpc, bucket: Bucket, executionRole: IRole): Construct(scope, "lambda")  {
    init {
        val packagingInstructions = listOf(
            "/bin/sh",
            "-c",
            "./gradlew -g \$PWD shadowJar " +
            "&& cp /asset-input/build/libs/user-lambda*.jar /asset-output/"
        )

        val builderOptions = BundlingOptions.builder()
            .command(packagingInstructions)
            .image(Runtime.JAVA_17.bundlingImage)
            .volumes(listOf(
                DockerVolume.builder()
                    .hostPath(System.getProperty("user.home") + "/.gradle/")
                    .containerPath("/root/.gradle/")
                    .build()
            ))
            .user("root")
            .outputType(BundlingOutput.ARCHIVED)
            .build()


        val function = Function(this, "user-lambda", FunctionProps.builder()
            .role(executionRole)
            .runtime(Runtime.JAVA_17)
            .code(Code.fromAsset("../user-lambda", AssetOptions.builder()
                .bundling(builderOptions)
                .build()
            ))
            .environment(mapOf(
                // Stop after C1 compilation
                "JAVA_TOOL_OPTIONS" to "-XX:+TieredCompilation -XX:TieredStopAtLevel=1",
                "CodeVersionString" to System.getenv("BUILD_NO"))
            )
            .handler("wedding.backend.userlambda.UserLambdaHandler")
            .memorySize(512)
            .allowPublicSubnet(false)
            .timeout(Duration.minutes(1))
            .logRetention(RetentionDays.ONE_WEEK)
            .build())

        val s3eventSource = S3EventSource(bucket, S3EventSourceProps.builder()
            .events(listOf(EventType.OBJECT_CREATED))
            .build())

        function.addEventSource(s3eventSource)

        (function.node.defaultChild as CfnFunction).setSnapStart(
            CfnFunction.SnapStartProperty.builder().applyOn("PublishedVersions").build()
        )

        function.currentVersion
    }
}