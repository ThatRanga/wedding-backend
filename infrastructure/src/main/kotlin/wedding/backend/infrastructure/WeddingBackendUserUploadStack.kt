package wedding.backend.infrastructure

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.IVpc
import software.amazon.awscdk.services.iam.*
import software.amazon.awscdk.services.sqs.Queue
import software.constructs.Construct

class WeddingBackendUserUploadStack(
    scope: Construct,
    id: String,
    env: String,
    vpc: IVpc,
    userQueue: Queue,
    stackProps: StackProps
) :
    Stack(scope, id, stackProps) {
    init {
        val bucket = WeddingBackendUserUploadS3Construct(this, env).bucket

        val lambdaExecutionRole = Role(
            this, "lambda-execution-role", RoleProps.builder()
                .assumedBy(ServicePrincipal("lambda.amazonaws.com"))
                .inlinePolicies(
                    mapOf(
                        "AllowS3Access" to PolicyDocument(
                            PolicyDocumentProps.builder()
                                .statements(
                                    listOf(
                                        PolicyStatement(
                                            PolicyStatementProps.builder()
                                                .actions(listOf("s3:GetObject"))
                                                .effect(Effect.ALLOW)
                                                .resources(
                                                    listOf(
                                                        bucket.bucketArn,
                                                        "${bucket.bucketArn}/*"
                                                    )
                                                )
                                                .build()
                                        )
                                    )
                                )
                                .build()
                        ),
                        "SendSQSMessage" to PolicyDocument(
                            PolicyDocumentProps.builder()
                                .statements(
                                    listOf(
                                        PolicyStatement(
                                            PolicyStatementProps.builder()
                                                .actions(listOf("sqs:SendMessage"))
                                                .effect(Effect.ALLOW)
                                                .resources(
                                                    listOf(
                                                        userQueue.queueArn
                                                    )
                                                )
                                                .build()
                                        )
                                    )
                                ).build()
                        )
                    )
                )
                .managedPolicies(
                    listOf(
                        ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole")
                    )
                )
                .build()
        )

        WeddingBackendUserUploadLambdaConstruct(this, vpc, bucket, lambdaExecutionRole)

        bucket.addToResourcePolicy(
            PolicyStatement(
                PolicyStatementProps.builder()
                    .actions(listOf("s3:GetObject"))
                    .effect(Effect.ALLOW)
                    .principals(listOf(ArnPrincipal(lambdaExecutionRole.roleArn)))
                    .resources(
                        listOf(
                            bucket.bucketArn,
                            "${bucket.bucketArn}/*"
                        )
                    )
                    .build()
            )

        )
    }
}