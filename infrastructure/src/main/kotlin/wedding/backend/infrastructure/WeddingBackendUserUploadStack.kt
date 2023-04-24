package wedding.backend.infrastructure

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.IVpc
import software.constructs.Construct

class WeddingBackendUserUploadStack(scope: Construct, id: String, env: String, vpc: IVpc, stackProps: StackProps) :
    Stack(scope, id, stackProps) {
    init {
        val bucket = WeddingBackendUserUploadS3Construct(this, env).bucket
        WeddingBackendUserUploadLambdaConstruct(this, vpc, bucket)
    }
}