#!/bin/bash

PIPELINE_ID=$1
ENV=$2
echo "Pipeline ID = '$PIPELINE_ID'}"
STATE=$(aws codepipeline get-pipeline-execution --pipeline-name "$ENV"-wedding-backend-pipeline --pipeline-execution-id "$PIPELINE_ID" | jq -r -e '.pipelineExecution.status')

while [ "$STATE" = "InProgress" ]
do
  sleep 10
  STATE=$(aws codepipeline get-pipeline-execution --pipeline-name "$ENV"-wedding-backend-pipeline --pipeline-execution-id "$PIPELINE_ID" | jq -r -e '.pipelineExecution.status')
done

if [ "$STATE" != "Succeeded" ] ; then
  echo "Pipeline failed with state: $STATE"
  exit 1
fi