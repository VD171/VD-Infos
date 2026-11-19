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
namespace hardware {
namespace security {
namespace keymint {
enum class Tag : int32_t {
  INVALID = 0,
  PURPOSE = 536870913,
  ALGORITHM = 268435458,
  KEY_SIZE = 805306371,
  BLOCK_MODE = 536870916,
  DIGEST = 536870917,
  PADDING = 536870918,
  CALLER_NONCE = 1879048199,
  MIN_MAC_LENGTH = 805306376,
  EC_CURVE = 268435466,
  RSA_PUBLIC_EXPONENT = 1342177480,
  INCLUDE_UNIQUE_ID = 1879048394,
  RSA_OAEP_MGF_DIGEST = 536871115,
  BOOTLOADER_ONLY = 1879048494,
  ROLLBACK_RESISTANCE = 1879048495,
  HARDWARE_TYPE = 268435760,
  EARLY_BOOT_ONLY = 1879048497,
  ACTIVE_DATETIME = 1610613136,
  ORIGINATION_EXPIRE_DATETIME = 1610613137,
  USAGE_EXPIRE_DATETIME = 1610613138,
  MIN_SECONDS_BETWEEN_OPS = 805306771,
  MAX_USES_PER_BOOT = 805306772,
  USAGE_COUNT_LIMIT = 805306773,
  USER_ID = 805306869,
  USER_SECURE_ID = -1610612234,
  NO_AUTH_REQUIRED = 1879048695,
  USER_AUTH_TYPE = 268435960,
  AUTH_TIMEOUT = 805306873,
  ALLOW_WHILE_ON_BODY = 1879048698,
  TRUSTED_USER_PRESENCE_REQUIRED = 1879048699,
  TRUSTED_CONFIRMATION_REQUIRED = 1879048700,
  UNLOCKED_DEVICE_REQUIRED = 1879048701,
  APPLICATION_ID = -1879047591,
  APPLICATION_DATA = -1879047492,
  CREATION_DATETIME = 1610613437,
  ORIGIN = 268436158,
  ROOT_OF_TRUST = -1879047488,
  OS_VERSION = 805307073,
  OS_PATCHLEVEL = 805307074,
  UNIQUE_ID = -1879047485,
  ATTESTATION_CHALLENGE = -1879047484,
  ATTESTATION_APPLICATION_ID = -1879047483,
  ATTESTATION_ID_BRAND = -1879047482,
  ATTESTATION_ID_DEVICE = -1879047481,
  ATTESTATION_ID_PRODUCT = -1879047480,
  ATTESTATION_ID_SERIAL = -1879047479,
  ATTESTATION_ID_IMEI = -1879047478,
  ATTESTATION_ID_MEID = -1879047477,
  ATTESTATION_ID_MANUFACTURER = -1879047476,
  ATTESTATION_ID_MODEL = -1879047475,
  VENDOR_PATCHLEVEL = 805307086,
  BOOT_PATCHLEVEL = 805307087,
  DEVICE_UNIQUE_ATTESTATION = 1879048912,
  IDENTITY_CREDENTIAL_KEY = 1879048913,
  STORAGE_KEY = 1879048914,
  ASSOCIATED_DATA = -1879047192,
  NONCE = -1879047191,
  MAC_LENGTH = 805307371,
  RESET_SINCE_ID_ROTATION = 1879049196,
  CONFIRMATION_TOKEN = -1879047187,
  CERTIFICATE_SERIAL = -2147482642,
  CERTIFICATE_SUBJECT = -1879047185,
  CERTIFICATE_NOT_BEFORE = 1610613744,
  CERTIFICATE_NOT_AFTER = 1610613745,
  MAX_BOOT_LEVEL = 805307378,
};

}  // namespace keymint
}  // namespace security
}  // namespace hardware
}  // namespace android
}  // namespace aidl
namespace aidl {
namespace android {
namespace hardware {
namespace security {
namespace keymint {
[[nodiscard]] static inline std::string toString(Tag val) {
  switch(val) {
  case Tag::INVALID:
    return "INVALID";
  case Tag::PURPOSE:
    return "PURPOSE";
  case Tag::ALGORITHM:
    return "ALGORITHM";
  case Tag::KEY_SIZE:
    return "KEY_SIZE";
  case Tag::BLOCK_MODE:
    return "BLOCK_MODE";
  case Tag::DIGEST:
    return "DIGEST";
  case Tag::PADDING:
    return "PADDING";
  case Tag::CALLER_NONCE:
    return "CALLER_NONCE";
  case Tag::MIN_MAC_LENGTH:
    return "MIN_MAC_LENGTH";
  case Tag::EC_CURVE:
    return "EC_CURVE";
  case Tag::RSA_PUBLIC_EXPONENT:
    return "RSA_PUBLIC_EXPONENT";
  case Tag::INCLUDE_UNIQUE_ID:
    return "INCLUDE_UNIQUE_ID";
  case Tag::RSA_OAEP_MGF_DIGEST:
    return "RSA_OAEP_MGF_DIGEST";
  case Tag::BOOTLOADER_ONLY:
    return "BOOTLOADER_ONLY";
  case Tag::ROLLBACK_RESISTANCE:
    return "ROLLBACK_RESISTANCE";
  case Tag::HARDWARE_TYPE:
    return "HARDWARE_TYPE";
  case Tag::EARLY_BOOT_ONLY:
    return "EARLY_BOOT_ONLY";
  case Tag::ACTIVE_DATETIME:
    return "ACTIVE_DATETIME";
  case Tag::ORIGINATION_EXPIRE_DATETIME:
    return "ORIGINATION_EXPIRE_DATETIME";
  case Tag::USAGE_EXPIRE_DATETIME:
    return "USAGE_EXPIRE_DATETIME";
  case Tag::MIN_SECONDS_BETWEEN_OPS:
    return "MIN_SECONDS_BETWEEN_OPS";
  case Tag::MAX_USES_PER_BOOT:
    return "MAX_USES_PER_BOOT";
  case Tag::USAGE_COUNT_LIMIT:
    return "USAGE_COUNT_LIMIT";
  case Tag::USER_ID:
    return "USER_ID";
  case Tag::USER_SECURE_ID:
    return "USER_SECURE_ID";
  case Tag::NO_AUTH_REQUIRED:
    return "NO_AUTH_REQUIRED";
  case Tag::USER_AUTH_TYPE:
    return "USER_AUTH_TYPE";
  case Tag::AUTH_TIMEOUT:
    return "AUTH_TIMEOUT";
  case Tag::ALLOW_WHILE_ON_BODY:
    return "ALLOW_WHILE_ON_BODY";
  case Tag::TRUSTED_USER_PRESENCE_REQUIRED:
    return "TRUSTED_USER_PRESENCE_REQUIRED";
  case Tag::TRUSTED_CONFIRMATION_REQUIRED:
    return "TRUSTED_CONFIRMATION_REQUIRED";
  case Tag::UNLOCKED_DEVICE_REQUIRED:
    return "UNLOCKED_DEVICE_REQUIRED";
  case Tag::APPLICATION_ID:
    return "APPLICATION_ID";
  case Tag::APPLICATION_DATA:
    return "APPLICATION_DATA";
  case Tag::CREATION_DATETIME:
    return "CREATION_DATETIME";
  case Tag::ORIGIN:
    return "ORIGIN";
  case Tag::ROOT_OF_TRUST:
    return "ROOT_OF_TRUST";
  case Tag::OS_VERSION:
    return "OS_VERSION";
  case Tag::OS_PATCHLEVEL:
    return "OS_PATCHLEVEL";
  case Tag::UNIQUE_ID:
    return "UNIQUE_ID";
  case Tag::ATTESTATION_CHALLENGE:
    return "ATTESTATION_CHALLENGE";
  case Tag::ATTESTATION_APPLICATION_ID:
    return "ATTESTATION_APPLICATION_ID";
  case Tag::ATTESTATION_ID_BRAND:
    return "ATTESTATION_ID_BRAND";
  case Tag::ATTESTATION_ID_DEVICE:
    return "ATTESTATION_ID_DEVICE";
  case Tag::ATTESTATION_ID_PRODUCT:
    return "ATTESTATION_ID_PRODUCT";
  case Tag::ATTESTATION_ID_SERIAL:
    return "ATTESTATION_ID_SERIAL";
  case Tag::ATTESTATION_ID_IMEI:
    return "ATTESTATION_ID_IMEI";
  case Tag::ATTESTATION_ID_MEID:
    return "ATTESTATION_ID_MEID";
  case Tag::ATTESTATION_ID_MANUFACTURER:
    return "ATTESTATION_ID_MANUFACTURER";
  case Tag::ATTESTATION_ID_MODEL:
    return "ATTESTATION_ID_MODEL";
  case Tag::VENDOR_PATCHLEVEL:
    return "VENDOR_PATCHLEVEL";
  case Tag::BOOT_PATCHLEVEL:
    return "BOOT_PATCHLEVEL";
  case Tag::DEVICE_UNIQUE_ATTESTATION:
    return "DEVICE_UNIQUE_ATTESTATION";
  case Tag::IDENTITY_CREDENTIAL_KEY:
    return "IDENTITY_CREDENTIAL_KEY";
  case Tag::STORAGE_KEY:
    return "STORAGE_KEY";
  case Tag::ASSOCIATED_DATA:
    return "ASSOCIATED_DATA";
  case Tag::NONCE:
    return "NONCE";
  case Tag::MAC_LENGTH:
    return "MAC_LENGTH";
  case Tag::RESET_SINCE_ID_ROTATION:
    return "RESET_SINCE_ID_ROTATION";
  case Tag::CONFIRMATION_TOKEN:
    return "CONFIRMATION_TOKEN";
  case Tag::CERTIFICATE_SERIAL:
    return "CERTIFICATE_SERIAL";
  case Tag::CERTIFICATE_SUBJECT:
    return "CERTIFICATE_SUBJECT";
  case Tag::CERTIFICATE_NOT_BEFORE:
    return "CERTIFICATE_NOT_BEFORE";
  case Tag::CERTIFICATE_NOT_AFTER:
    return "CERTIFICATE_NOT_AFTER";
  case Tag::MAX_BOOT_LEVEL:
    return "MAX_BOOT_LEVEL";
  default:
    return std::to_string(static_cast<int32_t>(val));
  }
}
}  // namespace keymint
}  // namespace security
}  // namespace hardware
}  // namespace android
}  // namespace aidl
namespace ndk {
namespace internal {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wc++17-extensions"
template <>
constexpr inline std::array<aidl::android::hardware::security::keymint::Tag, 65> enum_values<aidl::android::hardware::security::keymint::Tag> = {
  aidl::android::hardware::security::keymint::Tag::INVALID,
  aidl::android::hardware::security::keymint::Tag::PURPOSE,
  aidl::android::hardware::security::keymint::Tag::ALGORITHM,
  aidl::android::hardware::security::keymint::Tag::KEY_SIZE,
  aidl::android::hardware::security::keymint::Tag::BLOCK_MODE,
  aidl::android::hardware::security::keymint::Tag::DIGEST,
  aidl::android::hardware::security::keymint::Tag::PADDING,
  aidl::android::hardware::security::keymint::Tag::CALLER_NONCE,
  aidl::android::hardware::security::keymint::Tag::MIN_MAC_LENGTH,
  aidl::android::hardware::security::keymint::Tag::EC_CURVE,
  aidl::android::hardware::security::keymint::Tag::RSA_PUBLIC_EXPONENT,
  aidl::android::hardware::security::keymint::Tag::INCLUDE_UNIQUE_ID,
  aidl::android::hardware::security::keymint::Tag::RSA_OAEP_MGF_DIGEST,
  aidl::android::hardware::security::keymint::Tag::BOOTLOADER_ONLY,
  aidl::android::hardware::security::keymint::Tag::ROLLBACK_RESISTANCE,
  aidl::android::hardware::security::keymint::Tag::HARDWARE_TYPE,
  aidl::android::hardware::security::keymint::Tag::EARLY_BOOT_ONLY,
  aidl::android::hardware::security::keymint::Tag::ACTIVE_DATETIME,
  aidl::android::hardware::security::keymint::Tag::ORIGINATION_EXPIRE_DATETIME,
  aidl::android::hardware::security::keymint::Tag::USAGE_EXPIRE_DATETIME,
  aidl::android::hardware::security::keymint::Tag::MIN_SECONDS_BETWEEN_OPS,
  aidl::android::hardware::security::keymint::Tag::MAX_USES_PER_BOOT,
  aidl::android::hardware::security::keymint::Tag::USAGE_COUNT_LIMIT,
  aidl::android::hardware::security::keymint::Tag::USER_ID,
  aidl::android::hardware::security::keymint::Tag::USER_SECURE_ID,
  aidl::android::hardware::security::keymint::Tag::NO_AUTH_REQUIRED,
  aidl::android::hardware::security::keymint::Tag::USER_AUTH_TYPE,
  aidl::android::hardware::security::keymint::Tag::AUTH_TIMEOUT,
  aidl::android::hardware::security::keymint::Tag::ALLOW_WHILE_ON_BODY,
  aidl::android::hardware::security::keymint::Tag::TRUSTED_USER_PRESENCE_REQUIRED,
  aidl::android::hardware::security::keymint::Tag::TRUSTED_CONFIRMATION_REQUIRED,
  aidl::android::hardware::security::keymint::Tag::UNLOCKED_DEVICE_REQUIRED,
  aidl::android::hardware::security::keymint::Tag::APPLICATION_ID,
  aidl::android::hardware::security::keymint::Tag::APPLICATION_DATA,
  aidl::android::hardware::security::keymint::Tag::CREATION_DATETIME,
  aidl::android::hardware::security::keymint::Tag::ORIGIN,
  aidl::android::hardware::security::keymint::Tag::ROOT_OF_TRUST,
  aidl::android::hardware::security::keymint::Tag::OS_VERSION,
  aidl::android::hardware::security::keymint::Tag::OS_PATCHLEVEL,
  aidl::android::hardware::security::keymint::Tag::UNIQUE_ID,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_CHALLENGE,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_APPLICATION_ID,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_ID_BRAND,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_ID_DEVICE,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_ID_PRODUCT,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_ID_SERIAL,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_ID_IMEI,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_ID_MEID,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_ID_MANUFACTURER,
  aidl::android::hardware::security::keymint::Tag::ATTESTATION_ID_MODEL,
  aidl::android::hardware::security::keymint::Tag::VENDOR_PATCHLEVEL,
  aidl::android::hardware::security::keymint::Tag::BOOT_PATCHLEVEL,
  aidl::android::hardware::security::keymint::Tag::DEVICE_UNIQUE_ATTESTATION,
  aidl::android::hardware::security::keymint::Tag::IDENTITY_CREDENTIAL_KEY,
  aidl::android::hardware::security::keymint::Tag::STORAGE_KEY,
  aidl::android::hardware::security::keymint::Tag::ASSOCIATED_DATA,
  aidl::android::hardware::security::keymint::Tag::NONCE,
  aidl::android::hardware::security::keymint::Tag::MAC_LENGTH,
  aidl::android::hardware::security::keymint::Tag::RESET_SINCE_ID_ROTATION,
  aidl::android::hardware::security::keymint::Tag::CONFIRMATION_TOKEN,
  aidl::android::hardware::security::keymint::Tag::CERTIFICATE_SERIAL,
  aidl::android::hardware::security::keymint::Tag::CERTIFICATE_SUBJECT,
  aidl::android::hardware::security::keymint::Tag::CERTIFICATE_NOT_BEFORE,
  aidl::android::hardware::security::keymint::Tag::CERTIFICATE_NOT_AFTER,
  aidl::android::hardware::security::keymint::Tag::MAX_BOOT_LEVEL,
};
#pragma clang diagnostic pop
}  // namespace internal
}  // namespace ndk
