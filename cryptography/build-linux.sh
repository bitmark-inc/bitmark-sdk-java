#!/bin/bash -ev

. ./setenv.sh

# Un-comment when you want to upgrade latest libsodium
#./submodule-update.sh

./build-libsodium-host.sh

../gradlew :cryptography:tasks --all

../gradlew generateSWIGsource --full-stacktrace

../gradlew build --full-stacktrace

# Un-comment when you want to distribute/deploy
#gradle :cryptography:bintrayUpload

