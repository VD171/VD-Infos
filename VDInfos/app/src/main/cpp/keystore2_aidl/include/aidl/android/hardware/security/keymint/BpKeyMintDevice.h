/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /home/vd/Documentos/Softs/Android/SDK/build-tools/35.0.1/aidl --lang=ndk --structured --min_sdk_version=31 -I . -o ../src -h ../include ./android/system/keystore2/KeyMetadata.aidl ./android/system/keystore2/KeyEntryResponse.aidl ./android/system/keystore2/KeyDescriptor.aidl ./android/system/keystore2/AuthenticatorSpec.aidl ./android/system/keystore2/ResponseCode.aidl ./android/system/keystore2/CreateOperationResponse.aidl ./android/system/keystore2/IKeystoreSecurityLevel.aidl ./android/system/keystore2/OperationChallenge.aidl ./android/system/keystore2/Authorization.aidl ./android/system/keystore2/EphemeralStorageKeyResponse.aidl ./android/system/keystore2/KeyParameters.aidl ./android/system/keystore2/IKeystoreOperation.aidl ./android/system/keystore2/Domain.aidl ./android/system/keystore2/KeyPermission.aidl ./android/system/keystore2/IKeystoreService.aidl ./android/hardware/security/keymint/KeyOrigin.aidl ./android/hardware/security/keymint/BeginResult.aidl ./android/hardware/security/keymint/BlockMode.aidl ./android/hardware/security/keymint/KeyParameterValue.aidl ./android/hardware/security/keymint/KeyCreationResult.aidl ./android/hardware/security/keymint/HardwareAuthToken.aidl ./android/hardware/security/keymint/HardwareAuthenticatorType.aidl ./android/hardware/security/keymint/KeyFormat.aidl ./android/hardware/security/keymint/Tag.aidl ./android/hardware/security/keymint/AttestationKey.aidl ./android/hardware/security/keymint/KeyCharacteristics.aidl ./android/hardware/security/keymint/Digest.aidl ./android/hardware/security/keymint/Algorithm.aidl ./android/hardware/security/keymint/ErrorCode.aidl ./android/hardware/security/keymint/SecurityLevel.aidl ./android/hardware/security/keymint/KeyParameter.aidl ./android/hardware/security/keymint/EcCurve.aidl ./android/hardware/security/keymint/TagType.aidl ./android/hardware/security/keymint/Certificate.aidl ./android/hardware/security/keymint/IKeyMintOperation.aidl ./android/hardware/security/keymint/PaddingMode.aidl ./android/hardware/security/keymint/IKeyMintDevice.aidl ./android/hardware/security/keymint/KeyPurpose.aidl ./android/hardware/security/keymint/KeyMintHardwareInfo.aidl ./android/hardware/security/secureclock/Timestamp.aidl ./android/hardware/security/secureclock/TimeStampToken.aidl ./android/hardware/security/secureclock/ISecureClock.aidl ./android/hardware/security/sharedsecret/SharedSecretParameters.aidl ./android/hardware/security/sharedsecret/ISharedSecret.aidl
 */
#pragma once

#include "aidl/android/hardware/security/keymint/IKeyMintDevice.h"

#include <android/binder_ibinder.h>

namespace aidl {
namespace android {
namespace hardware {
namespace security {
namespace keymint {
class BpKeyMintDevice : public ::ndk::BpCInterface<IKeyMintDevice> {
public:
  explicit BpKeyMintDevice(const ::ndk::SpAIBinder& binder);
  virtual ~BpKeyMintDevice();

  ::ndk::ScopedAStatus getHardwareInfo(::aidl::android::hardware::security::keymint::KeyMintHardwareInfo* _aidl_return) override;
  ::ndk::ScopedAStatus addRngEntropy(const std::vector<uint8_t>& in_data) override;
  ::ndk::ScopedAStatus generateKey(const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& in_keyParams, const std::optional<::aidl::android::hardware::security::keymint::AttestationKey>& in_attestationKey, ::aidl::android::hardware::security::keymint::KeyCreationResult* _aidl_return) override;
  ::ndk::ScopedAStatus importKey(const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& in_keyParams, ::aidl::android::hardware::security::keymint::KeyFormat in_keyFormat, const std::vector<uint8_t>& in_keyData, const std::optional<::aidl::android::hardware::security::keymint::AttestationKey>& in_attestationKey, ::aidl::android::hardware::security::keymint::KeyCreationResult* _aidl_return) override;
  ::ndk::ScopedAStatus importWrappedKey(const std::vector<uint8_t>& in_wrappedKeyData, const std::vector<uint8_t>& in_wrappingKeyBlob, const std::vector<uint8_t>& in_maskingKey, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& in_unwrappingParams, int64_t in_passwordSid, int64_t in_biometricSid, ::aidl::android::hardware::security::keymint::KeyCreationResult* _aidl_return) override;
  ::ndk::ScopedAStatus upgradeKey(const std::vector<uint8_t>& in_keyBlobToUpgrade, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& in_upgradeParams, std::vector<uint8_t>* _aidl_return) override;
  ::ndk::ScopedAStatus deleteKey(const std::vector<uint8_t>& in_keyBlob) override;
  ::ndk::ScopedAStatus deleteAllKeys() override;
  ::ndk::ScopedAStatus destroyAttestationIds() override;
  ::ndk::ScopedAStatus begin(::aidl::android::hardware::security::keymint::KeyPurpose in_purpose, const std::vector<uint8_t>& in_keyBlob, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& in_params, const std::optional<::aidl::android::hardware::security::keymint::HardwareAuthToken>& in_authToken, ::aidl::android::hardware::security::keymint::BeginResult* _aidl_return) override;
  ::ndk::ScopedAStatus deviceLocked(bool in_passwordOnly, const std::optional<::aidl::android::hardware::security::secureclock::TimeStampToken>& in_timestampToken) override;
  ::ndk::ScopedAStatus earlyBootEnded() override;
  ::ndk::ScopedAStatus convertStorageKeyToEphemeral(const std::vector<uint8_t>& in_storageKeyBlob, std::vector<uint8_t>* _aidl_return) override;
  ::ndk::ScopedAStatus getKeyCharacteristics(const std::vector<uint8_t>& in_keyBlob, const std::vector<uint8_t>& in_appId, const std::vector<uint8_t>& in_appData, std::vector<::aidl::android::hardware::security::keymint::KeyCharacteristics>* _aidl_return) override;
};
}  // namespace keymint
}  // namespace security
}  // namespace hardware
}  // namespace android
}  // namespace aidl
