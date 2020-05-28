#!/bin/bash -ev

./setenv.sh

./submodule-update.sh

./build-bc-bip39-host.sh

pushd ../jni
./compile.sh
popd

gradle -p ../ tasks --all

gradle -p ../ generateSWIGsource --full-stacktrace

gradle -p ../ clean build bundleReleaseAar --full-stacktrace

# Un-comment when you want to distribute/deploy
#gradle -p bc-bip39-wrapper bintrayUpload

