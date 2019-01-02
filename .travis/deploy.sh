#!/usr/bin/env bash

JDK="oraclejdk8"
BRANCH="master"

set -e

if [ "$TRAVIS_JDK_VERSION" != "$JDK" ]; then
    echo "Skipping deployment: wrong JDK. Expected '$JDK' but was '$TRAVIS_JDK_VERSION'."
elif [ "$TRAVIS_BRANCH" != "$BRANCH" ]; then
    echo "Skipping deployment: wrong branch. Expected '$BRANCH' but was '$TRAVIS_BRANCH'."
else
    echo "Start to deploy to bintray ..."
    ./gradlew bintrayUpload -p cryptography
    ./gradlew bintrayUpload -p api-service
    ./gradlew bintrayUpload -p java-sdk
    ./gradlew bintrayUpload -p android-sdk

    echo "Deploy done :)"
fi