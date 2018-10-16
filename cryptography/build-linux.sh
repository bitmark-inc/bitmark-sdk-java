#!/bin/bash -ev

. ./setenv.sh

# Un-comment when you want to upgrade latest libsodium
#./submodule-update.sh

./build-libsodium-host.sh

gradle :cryptography:tasks --all

gradle generateSWIGsource --full-stacktrace

gradle build --full-stacktrace

# Un-comment when you want to distribute/deploy
#gradle :cryptography:bintrayUpload

