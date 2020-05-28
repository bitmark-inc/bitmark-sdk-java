#!/bin/bash -ev

jnilib=libbip39jni.so
destlib=/usr/lib
if uname -a | grep -q -i darwin; then
  jnilib=libbip39jni.dylib
  destlib=/Library/Java/Extensions
  if [ ! -d $destlib ]; then
      sudo mkdir $destlib
  fi
else
  sudo ldconfig
fi
echo $jnilib
echo $destlib
echo $destlib/$jnilib

gcc -I/usr/local/include -I../bc-bip39/src -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -I${JAVA_HOME}/include/darwin ../bc-crypto-base/src/*.c ../bc-bip39/src/*.c bip39_wrap.c -shared -fPIC -L/usr/local/lib -L/usr/lib -o $jnilib
sudo rm -f $destlib/$jnilib
sudo cp $jnilib $destlib