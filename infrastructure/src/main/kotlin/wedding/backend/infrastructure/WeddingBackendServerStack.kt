package wedding.backend.infrastructure

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.IVpc
import software.amazon.awscdk.services.s3.Bucket
import software.constructs.Construct

class WeddingBackendServerStack(scope: Construct, id: String, env: String, vpc: IVpc, props: StackProps) :
    Stack(scope, id, props) {
    init {
        val computeConstruct = WeddingBackendComputeConstruct(this, vpc)
        WeddingBackendCodePipelineConstruct(this, env, computeConstruct.asg)
    }
}