#!/usr/bin/env bash

echo ">>>>> Creating S3 buckets"
chmod +x init-aws.sh
awslocal s3api create-bucket --bucket person-management-bucket