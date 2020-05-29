#!/bin/bash -ev

./setenv.sh

./submodule-update.sh

#needed for mac osx
C_INCLUDE_PATH="${JAVA_HOME}/include:${JAVA_HOME}/include/linux:/System/Library/Frameworks/JavaVM.framework/Headers"
export C_INCLUDE_PATH

./build-bc-bip39-host.sh

pushd ../jni
./compile.sh
popd

gradle -p ../ generateSWIGsource --full-stacktrace

gradle -p ../ clean build bundleReleaseAar --full-stacktrace

# Un-comment when you want to distribute/deploy
#gradle -p ../ bintrayUpload

