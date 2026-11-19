/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /home/vd/Documentos/Softs/Android/SDK/build-tools/35.0.1/aidl --lang=ndk --structured --min_sdk_version=31 -I . -o ../src -h ../include ./android/system/keystore2/KeyMetadata.aidl ./android/system/keystore2/KeyEntryResponse.aidl ./android/system/keystore2/KeyDescriptor.aidl ./android/system/keystore2/AuthenticatorSpec.aidl ./android/system/keystore2/ResponseCode.aidl ./android/system/keystore2/CreateOperationResponse.aidl ./android/system/keystore2/IKeystoreSecurityLevel.aidl ./android/system/keystore2/OperationChallenge.aidl ./android/system/keystore2/Authorization.aidl ./android/system/keystore2/EphemeralStorageKeyResponse.aidl ./android/system/keystore2/KeyParameters.aidl ./android/system/keystore2/IKeystoreOperation.aidl ./android/system/keystore2/Domain.aidl ./android/system/keystore2/KeyPermission.aidl ./android/system/keystore2/IKeystoreService.aidl ./android/hardware/security/keymint/KeyOrigin.aidl ./android/hardware/security/keymint/BeginResult.aidl ./android/hardware/security/keymint/BlockMode.aidl ./android/hardware/security/keymint/KeyParameterValue.aidl ./android/hardware/security/keymint/KeyCreationResult.aidl ./android/hardware/security/keymint/HardwareAuthToken.aidl ./android/hardware/security/keymint/HardwareAuthenticatorType.aidl ./android/hardware/security/keymint/KeyFormat.aidl ./android/hardware/security/keymint/Tag.aidl ./android/hardware/security/keymint/AttestationKey.aidl ./android/hardware/security/keymint/KeyCharacteristics.aidl ./android/hardware/security/keymint/Digest.aidl ./android/hardware/security/keymint/Algorithm.aidl ./android/hardware/security/keymint/ErrorCode.aidl ./android/hardware/security/keymint/SecurityLevel.aidl ./android/hardware/security/keymint/KeyParameter.aidl ./android/hardware/security/keymint/EcCurve.aidl ./android/hardware/security/keymint/TagType.aidl ./android/hardware/security/keymint/Certificate.aidl ./android/hardware/security/keymint/IKeyMintOperation.aidl ./android/hardware/security/keymint/PaddingMode.aidl ./android/hardware/security/keymint/IKeyMintDevice.aidl ./android/hardware/security/keymint/KeyPurpose.aidl ./android/hardware/security/keymint/KeyMintHardwareInfo.aidl ./android/hardware/security/secureclock/Timestamp.aidl ./android/hardware/security/secureclock/TimeStampToken.aidl ./android/hardware/security/secureclock/ISecureClock.aidl ./android/hardware/security/sharedsecret/SharedSecretParameters.aidl ./android/hardware/security/sharedsecret/ISharedSecret.aidl
 */
#pragma once

#include <array>
#include <cstdint>
#include <memory>
#include <optional>
#include <string>
#include <vector>
#include <android/binder_enums.h>
#ifdef BINDER_STABILITY_SUPPORT
#include <android/binder_stability.h>
#endif  // BINDER_STABILITY_SUPPORT

namespace aidl {
namespace android {
namespace system {
namespace keystore2 {
enum class ResponseCode : int32_t {
  LOCKED = 2,
  UNINITIALIZED = 3,
  SYSTEM_ERROR = 4,
  PERMISSION_DENIED = 6,
  KEY_NOT_FOUND = 7,
  VALUE_CORRUPTED = 8,
  KEY_PERMANENTLY_INVALIDATED = 17,
  BACKEND_BUSY = 18,
  OPERATION_BUSY = 19,
  INVALID_ARGUMENT = 20,
  TOO_MUCH_DATA = 21,
  OUT_OF_KEYS = 22,
};

}  // namespace keystore2
}  // namespace system
}  // namespace android
}  // namespace aidl
namespace aidl {
namespace android {
namespace system {
namespace keystore2 {
[[nodiscard]] static inline std::string toString(ResponseCode val) {
  switch(val) {
  case ResponseCode::LOCKED:
    return "LOCKED";
  case ResponseCode::UNINITIALIZED:
    return "UNINITIALIZED";
  case ResponseCode::SYSTEM_ERROR:
    return "SYSTEM_ERROR";
  case ResponseCode::PERMISSION_DENIED:
    return "PERMISSION_DENIED";
  case ResponseCode::KEY_NOT_FOUND:
    return "KEY_NOT_FOUND";
  case ResponseCode::VALUE_CORRUPTED:
    return "VALUE_CORRUPTED";
  case ResponseCode::KEY_PERMANENTLY_INVALIDATED:
    return "KEY_PERMANENTLY_INVALIDATED";
  case ResponseCode::BACKEND_BUSY:
    return "BACKEND_BUSY";
  case ResponseCode::OPERATION_BUSY:
    return "OPERATION_BUSY";
  case ResponseCode::INVALID_ARGUMENT:
    return "INVALID_ARGUMENT";
  case ResponseCode::TOO_MUCH_DATA:
    return "TOO_MUCH_DATA";
  case ResponseCode::OUT_OF_KEYS:
    return "OUT_OF_KEYS";
  default:
    return std::to_string(static_cast<int32_t>(val));
  }
}
}  // namespace keystore2
}  // namespace system
}  // namespace android
}  // namespace aidl
namespace ndk {
namespace internal {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wc++17-extensions"
template <>
constexpr inline std::array<aidl::android::system::keystore2::ResponseCode, 12> enum_values<aidl::android::system::keystore2::ResponseCode> = {
  aidl::android::system::keystore2::ResponseCode::LOCKED,
  aidl::android::system::keystore2::ResponseCode::UNINITIALIZED,
  aidl::android::system::keystore2::ResponseCode::SYSTEM_ERROR,
  aidl::android::system::keystore2::ResponseCode::PERMISSION_DENIED,
  aidl::android::system::keystore2::ResponseCode::KEY_NOT_FOUND,
  aidl::android::system::keystore2::ResponseCode::VALUE_CORRUPTED,
  aidl::android::system::keystore2::ResponseCode::KEY_PERMANENTLY_INVALIDATED,
  aidl::android::system::keystore2::ResponseCode::BACKEND_BUSY,
  aidl::android::system::keystore2::ResponseCode::OPERATION_BUSY,
  aidl::android::system::keystore2::ResponseCode::INVALID_ARGUMENT,
  aidl::android::system::keystore2::ResponseCode::TOO_MUCH_DATA,
  aidl::android::system::keystore2::ResponseCode::OUT_OF_KEYS,
};
#pragma clang diagnostic pop
}  // namespace internal
}  // namespace ndk
