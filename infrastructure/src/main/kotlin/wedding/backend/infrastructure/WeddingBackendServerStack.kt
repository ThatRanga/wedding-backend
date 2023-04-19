package wedding.backend.infrastructure

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.constructs.Construct

class WeddingBackendServerStack(scope: Construct, id: String, env: String, props: StackProps) : Stack(scope, id, props) {
    init {
        val network = WeddingBackendNetworkConstruct(this)
        val computeConstruct = WeddingBackendComputeConstruct(this, network.vpc)
        WeddingBackendCodePipelineConstruct(this, env, computeConstruct.asg)
    }
}