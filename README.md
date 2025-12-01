# aws_opensearch_tests

Certification tests for AWS OpenSearch

# Setup

Create a AWS OpenSearch `t3.small.search` instance with the following security configuration: 

``
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::<ARN_NUMBER>:user/<KEY>"
      },
      "Action": "es:*",
      "Resource": "arn:aws:es:us-east-1:<ARN_NUMBER>:domain/<KEY>/*"
    }
  ]
}
``

# How to run

Export as environment variables:

CLUSTERURL=<URL_LINK>.us-east-1.es.amazonaws.com;
ACCESSKEY=<KEY>;
SECRETKEY=<SECRET>;
REGION=us-east-1
