/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /home/vd/Documentos/Softs/Android/SDK/build-tools/35.0.1/aidl --lang=ndk --structured --min_sdk_version=31 -I . -o ../src -h ../include ./android/system/keystore2/KeyMetadata.aidl ./android/system/keystore2/KeyEntryResponse.aidl ./android/system/keystore2/KeyDescriptor.aidl ./android/system/keystore2/AuthenticatorSpec.aidl ./android/system/keystore2/ResponseCode.aidl ./android/system/keystore2/CreateOperationResponse.aidl ./android/system/keystore2/IKeystoreSecurityLevel.aidl ./android/system/keystore2/OperationChallenge.aidl ./android/system/keystore2/Authorization.aidl ./android/system/keystore2/EphemeralStorageKeyResponse.aidl ./android/system/keystore2/KeyParameters.aidl ./android/system/keystore2/IKeystoreOperation.aidl ./android/system/keystore2/Domain.aidl ./android/system/keystore2/KeyPermission.aidl ./android/system/keystore2/IKeystoreService.aidl ./android/hardware/security/keymint/KeyOrigin.aidl ./android/hardware/security/keymint/BeginResult.aidl ./android/hardware/security/keymint/BlockMode.aidl ./android/hardware/security/keymint/KeyParameterValue.aidl ./android/hardware/security/keymint/KeyCreationResult.aidl ./android/hardware/security/keymint/HardwareAuthToken.aidl ./android/hardware/security/keymint/HardwareAuthenticatorType.aidl ./android/hardware/security/keymint/KeyFormat.aidl ./android/hardware/security/keymint/Tag.aidl ./android/hardware/security/keymint/AttestationKey.aidl ./android/hardware/security/keymint/KeyCharacteristics.aidl ./android/hardware/security/keymint/Digest.aidl ./android/hardware/security/keymint/Algorithm.aidl ./android/hardware/security/keymint/ErrorCode.aidl ./android/hardware/security/keymint/SecurityLevel.aidl ./android/hardware/security/keymint/KeyParameter.aidl ./android/hardware/security/keymint/EcCurve.aidl ./android/hardware/security/keymint/TagType.aidl ./android/hardware/security/keymint/Certificate.aidl ./android/hardware/security/keymint/IKeyMintOperation.aidl ./android/hardware/security/keymint/PaddingMode.aidl ./android/hardware/security/keymint/IKeyMintDevice.aidl ./android/hardware/security/keymint/KeyPurpose.aidl ./android/hardware/security/keymint/KeyMintHardwareInfo.aidl ./android/hardware/security/secureclock/Timestamp.aidl ./android/hardware/security/secureclock/TimeStampToken.aidl ./android/hardware/security/secureclock/ISecureClock.aidl ./android/hardware/security/sharedsecret/SharedSecretParameters.aidl ./android/hardware/security/sharedsecret/ISharedSecret.aidl
 */
#pragma once

#include <cstdint>
#include <memory>
#include <optional>
#include <string>
#include <vector>
#include <android/binder_interface_utils.h>
#include <aidl/android/hardware/security/sharedsecret/SharedSecretParameters.h>
#ifdef BINDER_STABILITY_SUPPORT
#include <android/binder_stability.h>
#endif  // BINDER_STABILITY_SUPPORT

namespace aidl::android::hardware::security::sharedsecret {
class SharedSecretParameters;
}  // namespace aidl::android::hardware::security::sharedsecret
namespace aidl {
namespace android {
namespace hardware {
namespace security {
namespace sharedsecret {
class ISharedSecretDelegator;

class ISharedSecret : public ::ndk::ICInterface {
public:
  typedef ISharedSecretDelegator DefaultDelegator;
  static const char* descriptor;
  ISharedSecret();
  virtual ~ISharedSecret();

  static const char* KEY_AGREEMENT_LABEL;
  static const char* KEY_CHECK_LABEL;
  static constexpr uint32_t TRANSACTION_getSharedSecretParameters = FIRST_CALL_TRANSACTION + 0;
  static constexpr uint32_t TRANSACTION_computeSharedSecret = FIRST_CALL_TRANSACTION + 1;

  static std::shared_ptr<ISharedSecret> fromBinder(const ::ndk::SpAIBinder& binder);
  static binder_status_t writeToParcel(AParcel* parcel, const std::shared_ptr<ISharedSecret>& instance);
  static binder_status_t readFromParcel(const AParcel* parcel, std::shared_ptr<ISharedSecret>* instance);
  static bool setDefaultImpl(const std::shared_ptr<ISharedSecret>& impl);
  static const std::shared_ptr<ISharedSecret>& getDefaultImpl();
  virtual ::ndk::ScopedAStatus getSharedSecretParameters(::aidl::android::hardware::security::sharedsecret::SharedSecretParameters* _aidl_return) = 0;
  virtual ::ndk::ScopedAStatus computeSharedSecret(const std::vector<::aidl::android::hardware::security::sharedsecret::SharedSecretParameters>& in_params, std::vector<uint8_t>* _aidl_return) = 0;
private:
  static std::shared_ptr<ISharedSecret> default_impl;
};
class ISharedSecretDefault : public ISharedSecret {
public:
  ::ndk::ScopedAStatus getSharedSecretParameters(::aidl::android::hardware::security::sharedsecret::SharedSecretParameters* _aidl_return) override;
  ::ndk::ScopedAStatus computeSharedSecret(const std::vector<::aidl::android::hardware::security::sharedsecret::SharedSecretParameters>& in_params, std::vector<uint8_t>* _aidl_return) override;
  ::ndk::SpAIBinder asBinder() override;
  bool isRemote() override;
};
}  // namespace sharedsecret
}  // namespace security
}  // namespace hardware
}  // namespace android
}  // namespace aidl
