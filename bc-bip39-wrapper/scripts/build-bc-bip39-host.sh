#!/bin/bash -ev

. setenv.sh

pushd ../bc-crypto-base
./configure
sudo make clean
sudo make check
sudo make install
popd

pushd ../bc-bip39
./configure
sudo make clean
sudo make check
sudo make install
popd
