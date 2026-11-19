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
#include <android/binder_parcelable_utils.h>
#include <android/binder_to_string.h>
#include <aidl/android/hardware/security/keymint/SecurityLevel.h>
#include <aidl/android/system/keystore2/Authorization.h>
#include <aidl/android/system/keystore2/KeyDescriptor.h>
#ifdef BINDER_STABILITY_SUPPORT
#include <android/binder_stability.h>
#endif  // BINDER_STABILITY_SUPPORT

namespace aidl::android::system::keystore2 {
class Authorization;
class KeyDescriptor;
}  // namespace aidl::android::system::keystore2
namespace aidl {
namespace android {
namespace system {
namespace keystore2 {
class KeyMetadata {
public:
  typedef std::false_type fixed_size;
  static const char* descriptor;

  ::aidl::android::system::keystore2::KeyDescriptor key;
  ::aidl::android::hardware::security::keymint::SecurityLevel keySecurityLevel = ::aidl::android::hardware::security::keymint::SecurityLevel(0);
  std::vector<::aidl::android::system::keystore2::Authorization> authorizations;
  std::optional<std::vector<uint8_t>> certificate;
  std::optional<std::vector<uint8_t>> certificateChain;
  int64_t modificationTimeMs = 0L;

  binder_status_t readFromParcel(const AParcel* parcel);
  binder_status_t writeToParcel(AParcel* parcel) const;

  inline bool operator==(const KeyMetadata& _rhs) const {
    return std::tie(key, keySecurityLevel, authorizations, certificate, certificateChain, modificationTimeMs) == std::tie(_rhs.key, _rhs.keySecurityLevel, _rhs.authorizations, _rhs.certificate, _rhs.certificateChain, _rhs.modificationTimeMs);
  }
  inline bool operator<(const KeyMetadata& _rhs) const {
    return std::tie(key, keySecurityLevel, authorizations, certificate, certificateChain, modificationTimeMs) < std::tie(_rhs.key, _rhs.keySecurityLevel, _rhs.authorizations, _rhs.certificate, _rhs.certificateChain, _rhs.modificationTimeMs);
  }
  inline bool operator!=(const KeyMetadata& _rhs) const {
    return !(*this == _rhs);
  }
  inline bool operator>(const KeyMetadata& _rhs) const {
    return _rhs < *this;
  }
  inline bool operator>=(const KeyMetadata& _rhs) const {
    return !(*this < _rhs);
  }
  inline bool operator<=(const KeyMetadata& _rhs) const {
    return !(_rhs < *this);
  }

  static const ::ndk::parcelable_stability_t _aidl_stability = ::ndk::STABILITY_LOCAL;
  inline std::string toString() const {
    std::ostringstream _aidl_os;
    _aidl_os << "KeyMetadata{";
    _aidl_os << "key: " << ::android::internal::ToString(key);
    _aidl_os << ", keySecurityLevel: " << ::android::internal::ToString(keySecurityLevel);
    _aidl_os << ", authorizations: " << ::android::internal::ToString(authorizations);
    _aidl_os << ", certificate: " << ::android::internal::ToString(certificate);
    _aidl_os << ", certificateChain: " << ::android::internal::ToString(certificateChain);
    _aidl_os << ", modificationTimeMs: " << ::android::internal::ToString(modificationTimeMs);
    _aidl_os << "}";
    return _aidl_os.str();
  }
};
}  // namespace keystore2
}  // namespace system
}  // namespace android
}  // namespace aidl
