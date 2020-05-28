#!/bin/bash -ev

. setenv.sh

pushd ../bc-crypto-base
./configure
make clean
make check
make install
popd

pushd ../bc-bip39
./configure
make clean
make check
make install
popd
