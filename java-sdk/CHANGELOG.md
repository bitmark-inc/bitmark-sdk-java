# Changelog
All notable changes to Java SDK will be documented in this file.

## 7-10-2019 [2.1.0]
### Added
- `Migration#rekey(Account, Account, Callback1<List<String>>)` function that help to transfer all own bitmarks from old account to another one.
- `Account#verify(String, byte[], byte[])` and `Account#sign(byte[])` that help to sign/verify message from `Account`
- `RecoveryPhrase#getWords(Locale)` to get the BIP39 words in different languages.
- `Account#fromSeed(String)` support to construct an `Account` from encoded seed string

### Changed
- Change recovery phrase language support from `Locale.CHINESE` to `Locale.TRADITIONAL_CHINESE`
- Remove `Account#parseAccountNumber()`
- Remove `Migration.migrate()` and replaced by `Migration.rekey(Account, Account, Callback1<List<String>>)`

### Bug Fixes & Improvement
- Speedup getting authentication/encryption key from `Account`

### Reference
- API Service: `com.bitmark.sdk:api-service:2.1.0`