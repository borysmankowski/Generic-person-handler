#!/usr/bin/env bash

echo ">>>>> Creating S3 buckets"
awslocal s3api create-bucket --bucket person-management-bucket