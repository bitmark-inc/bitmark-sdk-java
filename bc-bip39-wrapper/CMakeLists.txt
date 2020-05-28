# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)
project(bip39jni)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

file(GLOB_RECURSE BC_CRYPTO_BASE_SRC "${CMAKE_SOURCE_DIR}/bc-crypto-base/src/*.c")
file(GLOB_RECURSE BC_BIP39_SRC "${CMAKE_SOURCE_DIR}/bc-bip39/src/*.c")

include_directories(${CMAKE_SOURCE_DIR}/bc-crypto-base/src ${CMAKE_SOURCE_DIR}/bc-bip39/src /usr/local/include)

add_library(bc-crypto-base STATIC ${BC_CRYPTO_BASE_SRC})

add_library(bc-bip39 STATIC ${BC_BIP39_SRC})

target_link_libraries(bc-bip39 bc-crypto-base)

add_library(bip39jni SHARED ${CMAKE_SOURCE_DIR}/jni/bip39_wrap.c)

target_link_libraries(bip39jni bc-bip39)



