#!/bin/bash

pipelineId=$(aws codepipeline start-pipeline-execution --name DefaultPipeline | jq -r -e '.pipelineExecutionId')

echo "Pipeline started with id: $pipelineId"
echo "$pipelineId" > artifact.txt