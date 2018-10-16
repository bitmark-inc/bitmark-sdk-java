#!/bin/bash -ev

set -e

. ../setenv.sh

if [ -z "$JAVA_HOME" ]; then
    echo "ERROR You should set JAVA_HOME"
    echo "Exiting!"
    exit 1
fi

echo "${JAVA_HOME}"


C_INCLUDE_PATH="${JAVA_HOME}/include:${JAVA_HOME}/include/linux:/System/Library/Frameworks/JavaVM.framework/Headers"
export C_INCLUDE_PATH

rm -f *.java
rm -f *.c
rm -f *.so

export PATH=/usr/local/bin:$PATH

swig -I../libsodium/src/libsodium/include -java -package cryptography.crypto.sodium -outdir ../src/main/java/cryptography/crypto/sodium sodium.i


./jnilib.sh
