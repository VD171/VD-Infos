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
#include <aidl/android/hardware/security/secureclock/Timestamp.h>
#ifdef BINDER_STABILITY_SUPPORT
#include <android/binder_stability.h>
#endif  // BINDER_STABILITY_SUPPORT

namespace aidl::android::hardware::security::secureclock {
class Timestamp;
}  // namespace aidl::android::hardware::security::secureclock
namespace aidl {
namespace android {
namespace hardware {
namespace security {
namespace secureclock {
class TimeStampToken {
public:
  typedef std::false_type fixed_size;
  static const char* descriptor;

  int64_t challenge = 0L;
  ::aidl::android::hardware::security::secureclock::Timestamp timestamp;
  std::vector<uint8_t> mac;

  binder_status_t readFromParcel(const AParcel* parcel);
  binder_status_t writeToParcel(AParcel* parcel) const;

  inline bool operator==(const TimeStampToken& _rhs) const {
    return std::tie(challenge, timestamp, mac) == std::tie(_rhs.challenge, _rhs.timestamp, _rhs.mac);
  }
  inline bool operator<(const TimeStampToken& _rhs) const {
    return std::tie(challenge, timestamp, mac) < std::tie(_rhs.challenge, _rhs.timestamp, _rhs.mac);
  }
  inline bool operator!=(const TimeStampToken& _rhs) const {
    return !(*this == _rhs);
  }
  inline bool operator>(const TimeStampToken& _rhs) const {
    return _rhs < *this;
  }
  inline bool operator>=(const TimeStampToken& _rhs) const {
    return !(*this < _rhs);
  }
  inline bool operator<=(const TimeStampToken& _rhs) const {
    return !(_rhs < *this);
  }

  static const ::ndk::parcelable_stability_t _aidl_stability = ::ndk::STABILITY_LOCAL;
  inline std::string toString() const {
    std::ostringstream _aidl_os;
    _aidl_os << "TimeStampToken{";
    _aidl_os << "challenge: " << ::android::internal::ToString(challenge);
    _aidl_os << ", timestamp: " << ::android::internal::ToString(timestamp);
    _aidl_os << ", mac: " << ::android::internal::ToString(mac);
    _aidl_os << "}";
    return _aidl_os.str();
  }
};
}  // namespace secureclock
}  // namespace security
}  // namespace hardware
}  // namespace android
}  // namespace aidl
