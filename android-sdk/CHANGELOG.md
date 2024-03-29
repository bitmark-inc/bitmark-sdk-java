# Changelog
All notable changes to Android SDK will be documented in this file.

## 09-03-2022 [2.2.1]
Allow Account to create from PrivateKey

## 03-02-2019 [2.2.0]
Upgrade module `api-service` to `2.2.0`

### Reference
- API Service: `com.bitmark.sdk:api-service:2.2.0`

## 10-07-2019 [2.1.0]

### Added
- `Migration#rekey(Account, Account, Callback1<List<String>>)` function that help to transfer all own bitmarks from old account to another one.
- `Account#verify(String, byte[], byte[])` and `Account#sign(byte[])` that help to sign/verify message from `Account`
- `KeyAuthenticationSpec#Builder#setUsePossibleAlternativeAuthentication(Boolean)` for using alternative authentication method if the expected one is not available.
- `RecoveryPhrase#getWords(Locale)` to get the BIP39 words in different languages.
- `Account#fromSeed(String)` support to construct an `Account` from encoded seed string
- Support building fat AAR that contains all transitive dependencies.

### Changed
- Change recovery phrase language support from `Locale.CHINESE` to `Locale.TRADITIONAL_CHINESE`
- Remove `Account#parseAccountNumber()`
- Remove `Migration.migrate()` and replaced by `Migration.rekey(Account, Account, Callback1<List<String>>)`

### Bug Fixes & Improvement
- Fix some issues on authenticating users for storing/retrieving `Account` 
- Speedup getting authentication/encryption key from `Account`

### Reference
- API Service: `com.bitmark.sdk:api-service:2.1.0`