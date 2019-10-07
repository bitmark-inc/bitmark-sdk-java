# Changelog
All notable changes to API Service will be documented in this file.

## 7-10-2019 [2.1.0]
### Added
- New `HttpObserver` that can be configured from `GlobalConfiguration` that help to intercept to network request/response.
- Support web socket for realtime event triggering.
- New `edition` in `BitmarkRecord`, `previous_owner` and `confirmation` in `TransactionRecord`
- Support method to compute fingerprint. `RegistrationParams#computeFingerprint(byte[])`
- Support query `Transaction` along with `Block`
- Add `RegistrationParams#setFingerprintFromData(byte[])`

### Changed
- Rename `Address#getKey()` to `Address#getPublicKey()`
- Rename `RegistrationParams#generateFingerprint` to `RegistrationParams#setFingerprintFromFile`
- Remove `registrant` from constructor of `RegistrationParams`
- Allow to pass `null` for the metadata in `RegistrationParams`

### Bug Fixes & Improvement
- Fix bug that causes crash the process if running a large number of API request.
- Using a fixed `ThreadPoolExecutor` in API request for better resource usage.
- Http Logging is now optional instead of default. 

### Reference
- Cryptography: `com.bitmark.sdk:cryptography:1.5.0`
