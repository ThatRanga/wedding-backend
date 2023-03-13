#!/bin/bash

PIPELINE_ID=$1
STATE=$(aws codepipeline get-pipeline-execution --pipeline-name DefaultPipeline --pipeline-execution-id "$PIPELINE_ID" | jq -e '.pipelineExecution.status')

while [ "$STATE" = "InProgress" ]
do
  sleep 10
  STATE=$(aws codepipeline get-pipeline-execution --pipeline-name DefaultPipeline --pipeline-execution-id "$PIPELINE_ID" | jq -e '.pipelineExecution.status')
done

if [ "$STATE" != "Succeeded" ] ; then
  echo "Pipeline failed with state: $STATE"
  exit 1
fi