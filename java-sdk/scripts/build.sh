#!/bin/bash

set -e

build_bc_bip39() {
    pushd deps/bc-libs-java/bip39/java

    ./scripts/build.sh
    ./gradlew clean assemble

    popd
}

copy_release_file() {
    cp -r deps/bc-libs-java/bip39/java/src/main/libs src/main/jniLibs
    cp -r deps/bc-libs-java/bip39/java/build/libs src/main/libs
}

(
    ./scripts/cleanup.sh

    echo "Building bc-bip39..."
    build_bc_bip39

    echo "Copy release files"
    copy_release_file

    echo "DONE"
)