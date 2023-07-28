package wedding.backend.infrastructure

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.IVpc
import software.amazon.awscdk.services.sqs.Queue
import software.constructs.Construct

class WeddingBackendServerStack(scope: Construct, id: String, env: String, vpc: IVpc, userQueue: Queue, props: StackProps) :
    Stack(scope, id, props) {
    init {
        val computeConstruct = WeddingBackendComputeConstruct(this, vpc, userQueue)
        WeddingBackendCodePipelineConstruct(this, env, computeConstruct.asg)
    }
}