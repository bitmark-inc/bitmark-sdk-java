#!/bin/bash -ev

set -ev

. setenv.sh

rm -rf ../bc-bip39

git submodule init
git submodule sync
#git submodule update --remote --merge
git submodule update

pushd ../bc-bip39

git fetch && git checkout master
git reset --hard origin/master
git pull
popd
