AnySoftKeyboard for Research Study Use
======================================
This is a fork of AnySoftKeyboard to be used for psychological research use. The focus of this fork is the addition of a key & word logger while ensuring the privacy and anonymity of the test participant.

**Original readme file for AnySoftKeyboard can be found [here](./README_ASK.md).**

## Project Setup Instructions

Building this project requires sensitive configuration data to be defined in your local gradle.properties file.

* On Windows: %USER_HOME%.gradle/gradle.properties
* On Mac/Unix: $HOME/.gradle/gradle.properties

Add these lines and fill in your own values. Remove the (descriptions) from the end of each line.

### Amazon AWS

    aws_pool_id=us-east-1:1ab21ab2-1ab2--1ab2-1ab2-1ab21ab21ab2 (Identity Pool ID)
    aws_pool_region=us-east-1 (Identity Pool Region)
    aws_bucket_name=com.example.bucket (S3 Bucket Name)
    aws_bucket_region=us-west-2 (S3 Bucket Region)

### Encryption – *BE SURE TO CHANGE THESE VALUES!!*

    log_crypto_bytes={-11, 22, -33, 44, -55, 66, 77, -88, -99, 111, -121, 122, -123, -125, 126, 127}