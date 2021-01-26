#!/bin/bash

set -e

build_bc_bip39() {
    pushd deps/bc-libs-java/bip39/android

    ./scripts/build.sh --bundle-only

    popd
}

copy_release_file() {
    cp -r deps/bc-libs-java/bip39/android/app/build/outputs/aar src/main/libs
}

(
    ./scripts/cleanup.sh

    echo "Building bc-bip39..."
    build_bc_bip39

    echo "Copy release files"
    copy_release_file

    echo "DONE"
)