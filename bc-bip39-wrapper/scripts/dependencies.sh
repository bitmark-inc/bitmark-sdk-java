#!/bin/bash -ev

if uname -a | grep -q -i darwin; then
    ./dependencies-mac.sh
else
    . setenv.sh
    ./dependencies-linux.sh
    #./android-emulator.sh
fi
