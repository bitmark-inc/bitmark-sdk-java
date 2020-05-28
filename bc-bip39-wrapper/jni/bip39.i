%module Bip39

%include "typemaps.i"
%include "stdint.i"
%include "arrays_java.i"
%include "carrays.i"
%include "various.i"
%include "java.swg"
%include "typemaps.i"

/* Basic mappings */
%apply short {int16_t};
%apply short {uint16_t};
%apply short[] {uint16_t*};
%apply short[] {int16_t*};
%apply int {unsigned long long};
%apply long[] {unsigned long long *};
%apply int {size_t};
%apply int {uint32_t};
%apply long {uint64_t};
%apply(char *STRING, size_t LENGTH) { (char *str, size_t len) };

/* uint16_t* */
%typemap(jni) uint16_t*       "jshortArray"
%typemap(jtype) uint16_t*     "short[]"
%typemap(jstype) uint16_t*    "short[]"
%typemap(in) uint16_t*{
    $1 = (uint16_t*) JCALL2(GetShortArrayElements, jenv, $input, 0);
}
%typemap(argout) uint16_t*{
    JCALL3(ReleaseShortArrayElements, jenv, $input, (jshort *) $1, 0);
}
%typemap(javain) uint16_t* "$javainput"
/* Prevent default freearg typemap from being used */
%typemap(freearg) uint16_t* ""

/* int16_t* */
%typemap(jni) int16_t*       "jshortArray"
%typemap(jtype) int16_t*     "short[]"
%typemap(jstype) int16_t*    "short[]"
%typemap(in) int16_t*{
    $1 = (int16_t*) JCALL2(GetShortArrayElements, jenv, $input, 0);
}
%typemap(argout) int16_t*{
    JCALL3(ReleaseShortArrayElements, jenv, $input, (jshort *) $1, 0);
}
%typemap(javain) int16_t* "$javainput"
/* Prevent default freearg typemap from being used */
%typemap(freearg) int16_t* ""

/* unsigned char* */
%typemap(jni) unsigned char *       "jbyteArray"
%typemap(jtype) unsigned char *     "byte[]"
%typemap(jstype) unsigned char *    "byte[]"
%typemap(in) unsigned char *{
    $1 = (unsigned char *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) unsigned char *{
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) unsigned char *"$javainput"
/* Prevent default freearg typemap from being used */
%typemap(freearg) unsigned char *""

/* uint8_t* */
%typemap(jni) uint8_t *"jbyteArray"
%typemap(jtype) uint8_t *"byte[]"
%typemap(jstype) uint8_t *"byte[]"
%typemap(in) uint8_t *{
    $1 = (uint8_t *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) uint8_t *{
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) uint8_t *"$javainput"
%typemap(freearg) uint8_t *""

/* char types */
%typemap(jni) char *BYTE "jbyteArray"
%typemap(jtype) char *BYTE "byte[]"
%typemap(jstype) char *BYTE "byte[]"
%typemap(in) char *BYTE {
    $1 = (char *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) char *BYTE {
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) char *BYTE "$javainput"
/* Prevent default freearg typemap from being used */
%typemap(freearg) char *BYTE ""

/* unsigned short */
%typemap(jni) unsigned short *       "jbyteArray"
%typemap(jtype) unsigned short *     "byte[]"
%typemap(jstype) unsigned short *    "byte[]"
%typemap(in) unsigned short *{
    $1 = (unsigned short *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) unsigned short *{
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) unsigned short *"$javainput"
/* Prevent default freearg typemap from being used */
%typemap(freearg) unsigned short *""

/* Fixed size strings/char arrays */
%typemap(jni) char [ANY]"jbyteArray"
%typemap(jtype) char [ANY]"byte[]"
%typemap(jstype) char [ANY]"byte[]"
%typemap(in) char [ANY]{
    $1 = (char *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) char [ANY]{
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) char [ANY]"$javainput"
%typemap(freearg) char [ANY]""

/* load library block  */
%pragma(java) jniclasscode = %{
        static {
                LibraryLoader.load();
        }
%}


%inline %{
// Returns the BIP39 word for the given English mnemonic string.
// Returns -1 if the string is not a valid BIP39 mnemonic.
int16_t bip39_word_from_mnemonic(const char *mnemonic);

// Writes out the BIP39 words for the given English mnemonics.
// Returns the number of words written.
size_t bip39_words_from_mnemonics(const char* mnemonics, uint16_t* words, size_t max_words_len);

// Writes out the secret for the given English mnemonics.
// Returns the number of bytes written.
size_t bip39_secret_from_mnemonics(const char* mnemonics, uint8_t* secret, size_t max_secret_len);

// Writes the 32-byte (BIP39_SEED_LEN) SHA256 hash of `string` to `seed`.
#define BIP39_SEED_LEN 32
void bip39_seed_from_string(const char* string, uint8_t* seed);
%}

/* Strings */
%typemap(jni) char *"jbyteArray"
%typemap(jtype) char *"byte[]"
%typemap(jstype) char *"byte[]"
%typemap(in) char *{
    $1 = (char *) JCALL2(GetByteArrayElements, jenv, $input, 0);
}
%typemap(argout) char *{
    JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, 0);
}
%typemap(javain) char *"$javainput"
%typemap(freearg) char *""

%{
#include "stdbool.h"
#include <bc-bip39/bc-bip39.h>
#include <string.h>
%}

// Returns the English mnemonic string for the given BIP39 word
// Returns NULL if the word is out of range (> 2047).
void bip39_mnemonic_from_word(uint16_t word, char* mnemonic);

// Writes out the BIP39 words for the given secret.
// Returns the number of words written;
size_t bip39_words_from_secret(const uint8_t* secret, size_t secret_len, uint16_t* words, size_t max_words_len);

// Writes out the BIP39 English mnemonics for the given secret.
// Returns the length of the string written.
size_t bip39_mnemonics_from_secret(const uint8_t* secret, size_t secret_len, char* mnemonics, size_t max_mnemonics_len);
