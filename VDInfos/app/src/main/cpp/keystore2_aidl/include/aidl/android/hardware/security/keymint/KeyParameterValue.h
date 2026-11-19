/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /home/vd/Documentos/Softs/Android/SDK/build-tools/35.0.1/aidl --lang=ndk --structured --min_sdk_version=31 -I . -o ../src -h ../include ./android/system/keystore2/KeyMetadata.aidl ./android/system/keystore2/KeyEntryResponse.aidl ./android/system/keystore2/KeyDescriptor.aidl ./android/system/keystore2/AuthenticatorSpec.aidl ./android/system/keystore2/ResponseCode.aidl ./android/system/keystore2/CreateOperationResponse.aidl ./android/system/keystore2/IKeystoreSecurityLevel.aidl ./android/system/keystore2/OperationChallenge.aidl ./android/system/keystore2/Authorization.aidl ./android/system/keystore2/EphemeralStorageKeyResponse.aidl ./android/system/keystore2/KeyParameters.aidl ./android/system/keystore2/IKeystoreOperation.aidl ./android/system/keystore2/Domain.aidl ./android/system/keystore2/KeyPermission.aidl ./android/system/keystore2/IKeystoreService.aidl ./android/hardware/security/keymint/KeyOrigin.aidl ./android/hardware/security/keymint/BeginResult.aidl ./android/hardware/security/keymint/BlockMode.aidl ./android/hardware/security/keymint/KeyParameterValue.aidl ./android/hardware/security/keymint/KeyCreationResult.aidl ./android/hardware/security/keymint/HardwareAuthToken.aidl ./android/hardware/security/keymint/HardwareAuthenticatorType.aidl ./android/hardware/security/keymint/KeyFormat.aidl ./android/hardware/security/keymint/Tag.aidl ./android/hardware/security/keymint/AttestationKey.aidl ./android/hardware/security/keymint/KeyCharacteristics.aidl ./android/hardware/security/keymint/Digest.aidl ./android/hardware/security/keymint/Algorithm.aidl ./android/hardware/security/keymint/ErrorCode.aidl ./android/hardware/security/keymint/SecurityLevel.aidl ./android/hardware/security/keymint/KeyParameter.aidl ./android/hardware/security/keymint/EcCurve.aidl ./android/hardware/security/keymint/TagType.aidl ./android/hardware/security/keymint/Certificate.aidl ./android/hardware/security/keymint/IKeyMintOperation.aidl ./android/hardware/security/keymint/PaddingMode.aidl ./android/hardware/security/keymint/IKeyMintDevice.aidl ./android/hardware/security/keymint/KeyPurpose.aidl ./android/hardware/security/keymint/KeyMintHardwareInfo.aidl ./android/hardware/security/secureclock/Timestamp.aidl ./android/hardware/security/secureclock/TimeStampToken.aidl ./android/hardware/security/secureclock/ISecureClock.aidl ./android/hardware/security/sharedsecret/SharedSecretParameters.aidl ./android/hardware/security/sharedsecret/ISharedSecret.aidl
 */
#pragma once

#include <array>
#include <cassert>
#include <cstdint>
#include <memory>
#include <optional>
#include <string>
#include <type_traits>
#include <utility>
#include <variant>
#include <vector>
#include <android/binder_enums.h>
#include <android/binder_interface_utils.h>
#include <android/binder_parcelable_utils.h>
#include <android/binder_to_string.h>
#include <aidl/android/hardware/security/keymint/Algorithm.h>
#include <aidl/android/hardware/security/keymint/BlockMode.h>
#include <aidl/android/hardware/security/keymint/Digest.h>
#include <aidl/android/hardware/security/keymint/EcCurve.h>
#include <aidl/android/hardware/security/keymint/HardwareAuthenticatorType.h>
#include <aidl/android/hardware/security/keymint/KeyOrigin.h>
#include <aidl/android/hardware/security/keymint/KeyPurpose.h>
#include <aidl/android/hardware/security/keymint/PaddingMode.h>
#include <aidl/android/hardware/security/keymint/SecurityLevel.h>
#ifdef BINDER_STABILITY_SUPPORT
#include <android/binder_stability.h>
#endif  // BINDER_STABILITY_SUPPORT

#ifndef __BIONIC__
#define __assert2(a,b,c,d) ((void)0)
#endif

namespace aidl {
namespace android {
namespace hardware {
namespace security {
namespace keymint {
class KeyParameterValue {
public:
  typedef std::false_type fixed_size;
  static const char* descriptor;

  enum class Tag : int32_t {
    invalid = 0,
    algorithm = 1,
    blockMode = 2,
    paddingMode = 3,
    digest = 4,
    ecCurve = 5,
    origin = 6,
    keyPurpose = 7,
    hardwareAuthenticatorType = 8,
    securityLevel = 9,
    boolValue = 10,
    integer = 11,
    longInteger = 12,
    dateTime = 13,
    blob = 14,
  };

  // Expose tag symbols for legacy code
  static const inline Tag invalid = Tag::invalid;
  static const inline Tag algorithm = Tag::algorithm;
  static const inline Tag blockMode = Tag::blockMode;
  static const inline Tag paddingMode = Tag::paddingMode;
  static const inline Tag digest = Tag::digest;
  static const inline Tag ecCurve = Tag::ecCurve;
  static const inline Tag origin = Tag::origin;
  static const inline Tag keyPurpose = Tag::keyPurpose;
  static const inline Tag hardwareAuthenticatorType = Tag::hardwareAuthenticatorType;
  static const inline Tag securityLevel = Tag::securityLevel;
  static const inline Tag boolValue = Tag::boolValue;
  static const inline Tag integer = Tag::integer;
  static const inline Tag longInteger = Tag::longInteger;
  static const inline Tag dateTime = Tag::dateTime;
  static const inline Tag blob = Tag::blob;

  template<typename _Tp>
  static constexpr bool _not_self = !std::is_same_v<std::remove_cv_t<std::remove_reference_t<_Tp>>, KeyParameterValue>;

  KeyParameterValue() : _value(std::in_place_index<static_cast<size_t>(invalid)>, int32_t(0)) { }

  template <typename _Tp, typename = std::enable_if_t<_not_self<_Tp>>>
  // NOLINTNEXTLINE(google-explicit-constructor)
  constexpr KeyParameterValue(_Tp&& _arg)
      : _value(std::forward<_Tp>(_arg)) {}

  template <size_t _Np, typename... _Tp>
  constexpr explicit KeyParameterValue(std::in_place_index_t<_Np>, _Tp&&... _args)
      : _value(std::in_place_index<_Np>, std::forward<_Tp>(_args)...) {}

  template <Tag _tag, typename... _Tp>
  static KeyParameterValue make(_Tp&&... _args) {
    return KeyParameterValue(std::in_place_index<static_cast<size_t>(_tag)>, std::forward<_Tp>(_args)...);
  }

  template <Tag _tag, typename _Tp, typename... _Up>
  static KeyParameterValue make(std::initializer_list<_Tp> _il, _Up&&... _args) {
    return KeyParameterValue(std::in_place_index<static_cast<size_t>(_tag)>, std::move(_il), std::forward<_Up>(_args)...);
  }

  Tag getTag() const {
    return static_cast<Tag>(_value.index());
  }

  template <Tag _tag>
  const auto& get() const {
    if (getTag() != _tag) { __assert2(__FILE__, __LINE__, __PRETTY_FUNCTION__, "bad access: a wrong tag"); }
    return std::get<static_cast<size_t>(_tag)>(_value);
  }

  template <Tag _tag>
  auto& get() {
    if (getTag() != _tag) { __assert2(__FILE__, __LINE__, __PRETTY_FUNCTION__, "bad access: a wrong tag"); }
    return std::get<static_cast<size_t>(_tag)>(_value);
  }

  template <Tag _tag, typename... _Tp>
  void set(_Tp&&... _args) {
    _value.emplace<static_cast<size_t>(_tag)>(std::forward<_Tp>(_args)...);
  }

  binder_status_t readFromParcel(const AParcel* _parcel);
  binder_status_t writeToParcel(AParcel* _parcel) const;

  inline bool operator==(const KeyParameterValue& _rhs) const {
    return _value == _rhs._value;
  }
  inline bool operator<(const KeyParameterValue& _rhs) const {
    return _value < _rhs._value;
  }
  inline bool operator!=(const KeyParameterValue& _rhs) const {
    return !(*this == _rhs);
  }
  inline bool operator>(const KeyParameterValue& _rhs) const {
    return _rhs < *this;
  }
  inline bool operator>=(const KeyParameterValue& _rhs) const {
    return !(*this < _rhs);
  }
  inline bool operator<=(const KeyParameterValue& _rhs) const {
    return !(_rhs < *this);
  }

  static const ::ndk::parcelable_stability_t _aidl_stability = ::ndk::STABILITY_LOCAL;
  inline std::string toString() const {
    std::ostringstream os;
    os << "KeyParameterValue{";
    switch (getTag()) {
    case invalid: os << "invalid: " << ::android::internal::ToString(get<invalid>()); break;
    case algorithm: os << "algorithm: " << ::android::internal::ToString(get<algorithm>()); break;
    case blockMode: os << "blockMode: " << ::android::internal::ToString(get<blockMode>()); break;
    case paddingMode: os << "paddingMode: " << ::android::internal::ToString(get<paddingMode>()); break;
    case digest: os << "digest: " << ::android::internal::ToString(get<digest>()); break;
    case ecCurve: os << "ecCurve: " << ::android::internal::ToString(get<ecCurve>()); break;
    case origin: os << "origin: " << ::android::internal::ToString(get<origin>()); break;
    case keyPurpose: os << "keyPurpose: " << ::android::internal::ToString(get<keyPurpose>()); break;
    case hardwareAuthenticatorType: os << "hardwareAuthenticatorType: " << ::android::internal::ToString(get<hardwareAuthenticatorType>()); break;
    case securityLevel: os << "securityLevel: " << ::android::internal::ToString(get<securityLevel>()); break;
    case boolValue: os << "boolValue: " << ::android::internal::ToString(get<boolValue>()); break;
    case integer: os << "integer: " << ::android::internal::ToString(get<integer>()); break;
    case longInteger: os << "longInteger: " << ::android::internal::ToString(get<longInteger>()); break;
    case dateTime: os << "dateTime: " << ::android::internal::ToString(get<dateTime>()); break;
    case blob: os << "blob: " << ::android::internal::ToString(get<blob>()); break;
    }
    os << "}";
    return os.str();
  }
private:
  std::variant<int32_t, ::aidl::android::hardware::security::keymint::Algorithm, ::aidl::android::hardware::security::keymint::BlockMode, ::aidl::android::hardware::security::keymint::PaddingMode, ::aidl::android::hardware::security::keymint::Digest, ::aidl::android::hardware::security::keymint::EcCurve, ::aidl::android::hardware::security::keymint::KeyOrigin, ::aidl::android::hardware::security::keymint::KeyPurpose, ::aidl::android::hardware::security::keymint::HardwareAuthenticatorType, ::aidl::android::hardware::security::keymint::SecurityLevel, bool, int32_t, int64_t, int64_t, std::vector<uint8_t>> _value;
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
[[nodiscard]] static inline std::string toString(KeyParameterValue::Tag val) {
  switch(val) {
  case KeyParameterValue::Tag::invalid:
    return "invalid";
  case KeyParameterValue::Tag::algorithm:
    return "algorithm";
  case KeyParameterValue::Tag::blockMode:
    return "blockMode";
  case KeyParameterValue::Tag::paddingMode:
    return "paddingMode";
  case KeyParameterValue::Tag::digest:
    return "digest";
  case KeyParameterValue::Tag::ecCurve:
    return "ecCurve";
  case KeyParameterValue::Tag::origin:
    return "origin";
  case KeyParameterValue::Tag::keyPurpose:
    return "keyPurpose";
  case KeyParameterValue::Tag::hardwareAuthenticatorType:
    return "hardwareAuthenticatorType";
  case KeyParameterValue::Tag::securityLevel:
    return "securityLevel";
  case KeyParameterValue::Tag::boolValue:
    return "boolValue";
  case KeyParameterValue::Tag::integer:
    return "integer";
  case KeyParameterValue::Tag::longInteger:
    return "longInteger";
  case KeyParameterValue::Tag::dateTime:
    return "dateTime";
  case KeyParameterValue::Tag::blob:
    return "blob";
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
constexpr inline std::array<aidl::android::hardware::security::keymint::KeyParameterValue::Tag, 15> enum_values<aidl::android::hardware::security::keymint::KeyParameterValue::Tag> = {
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::invalid,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::algorithm,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::blockMode,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::paddingMode,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::digest,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::ecCurve,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::origin,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::keyPurpose,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::hardwareAuthenticatorType,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::securityLevel,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::boolValue,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::integer,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::longInteger,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::dateTime,
  aidl::android::hardware::security::keymint::KeyParameterValue::Tag::blob,
};
#pragma clang diagnostic pop
}  // namespace internal
}  // namespace ndk
