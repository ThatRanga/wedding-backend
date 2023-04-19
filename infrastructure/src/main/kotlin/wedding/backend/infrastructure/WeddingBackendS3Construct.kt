package wedding.backend.infrastructure

import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.services.s3.Bucket
import software.amazon.awscdk.services.s3.BucketProps
import software.amazon.awscdk.services.s3.LifecycleRule
import software.constructs.Construct

class WeddingBackendS3Construct(scope: Construct, env: String): Construct(scope, "bucket") {
    val bucket: Bucket
    init {
        bucket = Bucket(this, "user-data", BucketProps.builder()
            .bucketName("${env}-user-data")
            .enforceSsl(true)
            .autoDeleteObjects(true)
            .removalPolicy(RemovalPolicy.DESTROY)
            .lifecycleRules(listOf(LifecycleRule.builder()
                .id("TTL for objects")
                .expiration(Duration.hours(1))
                .build()))
            .build())
    }
}