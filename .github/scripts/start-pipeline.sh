#!/bin/bash

env=$1

pipelineId=$(aws codepipeline start-pipeline-execution --name "$env"-wedding-backend-pipeline | jq -r -e '.pipelineExecutionId')

echo "Pipeline started with id: $pipelineId"
echo "$pipelineId" > artifact.txt