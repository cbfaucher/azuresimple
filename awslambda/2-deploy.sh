#!/bin/bash
set -eo pipefail
ARTIFACT_BUCKET=$(cat bucket-name.txt)
TEMPLATE=template-mvn.yml

echo "Running: mvn package"
mvn package

echo "Running: aws cloudformation package ..."
aws cloudformation package --template-file $TEMPLATE --s3-bucket $ARTIFACT_BUCKET --output-template-file out.yml

echo "Running: aws cloudformation deploy ..."
aws cloudformation deploy --template-file out.yml --stack-name aws-payment --capabilities CAPABILITY_NAMED_IAM