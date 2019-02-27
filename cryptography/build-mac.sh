#!/bin/bash -ev

. ./setenv.sh

# Un-comment when you want to upgrade latest libsodium
#./submodule-update.sh

#needed for mac osx
C_INCLUDE_PATH="${JAVA_HOME}/include:${JAVA_HOME}/include/linux:/System/Library/Frameworks/JavaVM.framework/Headers"
export C_INCLUDE_PATH

./build-libsodium-host.sh

# Un-comment when you want to generate swig source
#gradle :cryptography:generateSWIGsource --full-stacktrace

gradle clean bundleReleaseAar build -p cryptography --stacktrace

# Un-comment when you want to distribute/deploy
#gradle :cryptography:bintrayUpload

