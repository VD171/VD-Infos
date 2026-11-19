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
enum class KeyPermission : int32_t {
  NONE = 0,
  DELETE = 1,
  GEN_UNIQUE_ID = 2,
  GET_INFO = 4,
  GRANT = 8,
  MANAGE_BLOB = 16,
  REBIND = 32,
  REQ_FORCED_OP = 64,
  UPDATE = 128,
  USE = 256,
  USE_DEV_ID = 512,
  USE_NO_LSKF_BINDING = 1024,
  CONVERT_STORAGE_KEY_TO_EPHEMERAL = 2048,
};

}  // namespace keystore2
}  // namespace system
}  // namespace android
}  // namespace aidl
namespace aidl {
namespace android {
namespace system {
namespace keystore2 {
[[nodiscard]] static inline std::string toString(KeyPermission val) {
  switch(val) {
  case KeyPermission::NONE:
    return "NONE";
  case KeyPermission::DELETE:
    return "DELETE";
  case KeyPermission::GEN_UNIQUE_ID:
    return "GEN_UNIQUE_ID";
  case KeyPermission::GET_INFO:
    return "GET_INFO";
  case KeyPermission::GRANT:
    return "GRANT";
  case KeyPermission::MANAGE_BLOB:
    return "MANAGE_BLOB";
  case KeyPermission::REBIND:
    return "REBIND";
  case KeyPermission::REQ_FORCED_OP:
    return "REQ_FORCED_OP";
  case KeyPermission::UPDATE:
    return "UPDATE";
  case KeyPermission::USE:
    return "USE";
  case KeyPermission::USE_DEV_ID:
    return "USE_DEV_ID";
  case KeyPermission::USE_NO_LSKF_BINDING:
    return "USE_NO_LSKF_BINDING";
  case KeyPermission::CONVERT_STORAGE_KEY_TO_EPHEMERAL:
    return "CONVERT_STORAGE_KEY_TO_EPHEMERAL";
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
constexpr inline std::array<aidl::android::system::keystore2::KeyPermission, 13> enum_values<aidl::android::system::keystore2::KeyPermission> = {
  aidl::android::system::keystore2::KeyPermission::NONE,
  aidl::android::system::keystore2::KeyPermission::DELETE,
  aidl::android::system::keystore2::KeyPermission::GEN_UNIQUE_ID,
  aidl::android::system::keystore2::KeyPermission::GET_INFO,
  aidl::android::system::keystore2::KeyPermission::GRANT,
  aidl::android::system::keystore2::KeyPermission::MANAGE_BLOB,
  aidl::android::system::keystore2::KeyPermission::REBIND,
  aidl::android::system::keystore2::KeyPermission::REQ_FORCED_OP,
  aidl::android::system::keystore2::KeyPermission::UPDATE,
  aidl::android::system::keystore2::KeyPermission::USE,
  aidl::android::system::keystore2::KeyPermission::USE_DEV_ID,
  aidl::android::system::keystore2::KeyPermission::USE_NO_LSKF_BINDING,
  aidl::android::system::keystore2::KeyPermission::CONVERT_STORAGE_KEY_TO_EPHEMERAL,
};
#pragma clang diagnostic pop
}  // namespace internal
}  // namespace ndk
