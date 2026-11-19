/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /home/vd/Documentos/Softs/Android/SDK/build-tools/35.0.1/aidl --lang=ndk --structured --min_sdk_version=31 -I . -o ../src -h ../include ./android/system/keystore2/KeyMetadata.aidl ./android/system/keystore2/KeyEntryResponse.aidl ./android/system/keystore2/KeyDescriptor.aidl ./android/system/keystore2/AuthenticatorSpec.aidl ./android/system/keystore2/ResponseCode.aidl ./android/system/keystore2/CreateOperationResponse.aidl ./android/system/keystore2/IKeystoreSecurityLevel.aidl ./android/system/keystore2/OperationChallenge.aidl ./android/system/keystore2/Authorization.aidl ./android/system/keystore2/EphemeralStorageKeyResponse.aidl ./android/system/keystore2/KeyParameters.aidl ./android/system/keystore2/IKeystoreOperation.aidl ./android/system/keystore2/Domain.aidl ./android/system/keystore2/KeyPermission.aidl ./android/system/keystore2/IKeystoreService.aidl ./android/hardware/security/keymint/KeyOrigin.aidl ./android/hardware/security/keymint/BeginResult.aidl ./android/hardware/security/keymint/BlockMode.aidl ./android/hardware/security/keymint/KeyParameterValue.aidl ./android/hardware/security/keymint/KeyCreationResult.aidl ./android/hardware/security/keymint/HardwareAuthToken.aidl ./android/hardware/security/keymint/HardwareAuthenticatorType.aidl ./android/hardware/security/keymint/KeyFormat.aidl ./android/hardware/security/keymint/Tag.aidl ./android/hardware/security/keymint/AttestationKey.aidl ./android/hardware/security/keymint/KeyCharacteristics.aidl ./android/hardware/security/keymint/Digest.aidl ./android/hardware/security/keymint/Algorithm.aidl ./android/hardware/security/keymint/ErrorCode.aidl ./android/hardware/security/keymint/SecurityLevel.aidl ./android/hardware/security/keymint/KeyParameter.aidl ./android/hardware/security/keymint/EcCurve.aidl ./android/hardware/security/keymint/TagType.aidl ./android/hardware/security/keymint/Certificate.aidl ./android/hardware/security/keymint/IKeyMintOperation.aidl ./android/hardware/security/keymint/PaddingMode.aidl ./android/hardware/security/keymint/IKeyMintDevice.aidl ./android/hardware/security/keymint/KeyPurpose.aidl ./android/hardware/security/keymint/KeyMintHardwareInfo.aidl ./android/hardware/security/secureclock/Timestamp.aidl ./android/hardware/security/secureclock/TimeStampToken.aidl ./android/hardware/security/secureclock/ISecureClock.aidl ./android/hardware/security/sharedsecret/SharedSecretParameters.aidl ./android/hardware/security/sharedsecret/ISharedSecret.aidl
 */
#include "aidl/android/system/keystore2/IKeystoreSecurityLevel.h"

#include <android/binder_parcel_utils.h>
#include <aidl/android/system/keystore2/BnKeystoreOperation.h>
#include <aidl/android/system/keystore2/BnKeystoreSecurityLevel.h>
#include <aidl/android/system/keystore2/BpKeystoreOperation.h>
#include <aidl/android/system/keystore2/BpKeystoreSecurityLevel.h>
#include <aidl/android/system/keystore2/IKeystoreOperation.h>

namespace aidl {
namespace android {
namespace system {
namespace keystore2 {
static binder_status_t _aidl_android_system_keystore2_IKeystoreSecurityLevel_onTransact(AIBinder* _aidl_binder, transaction_code_t _aidl_code, const AParcel* _aidl_in, AParcel* _aidl_out) {
  (void)_aidl_in;
  (void)_aidl_out;
  binder_status_t _aidl_ret_status = STATUS_UNKNOWN_TRANSACTION;
  std::shared_ptr<BnKeystoreSecurityLevel> _aidl_impl = std::static_pointer_cast<BnKeystoreSecurityLevel>(::ndk::ICInterface::asInterface(_aidl_binder));
  switch (_aidl_code) {
    case (FIRST_CALL_TRANSACTION + 0 /*createOperation*/): {
      ::aidl::android::system::keystore2::KeyDescriptor in_key;
      std::vector<::aidl::android::hardware::security::keymint::KeyParameter> in_operationParameters;
      bool in_forced;
      ::aidl::android::system::keystore2::CreateOperationResponse _aidl_return;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_key);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_operationParameters);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_forced);
      if (_aidl_ret_status != STATUS_OK) break;

      ::ndk::ScopedAStatus _aidl_status = _aidl_impl->createOperation(in_key, in_operationParameters, in_forced, &_aidl_return);
      _aidl_ret_status = AParcel_writeStatusHeader(_aidl_out, _aidl_status.get());
      if (_aidl_ret_status != STATUS_OK) break;

      if (!AStatus_isOk(_aidl_status.get())) break;

      _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_out, _aidl_return);
      if (_aidl_ret_status != STATUS_OK) break;

      break;
    }
    case (FIRST_CALL_TRANSACTION + 1 /*generateKey*/): {
      ::aidl::android::system::keystore2::KeyDescriptor in_key;
      std::optional<::aidl::android::system::keystore2::KeyDescriptor> in_attestationKey;
      std::vector<::aidl::android::hardware::security::keymint::KeyParameter> in_params;
      int32_t in_flags;
      std::vector<uint8_t> in_entropy;
      ::aidl::android::system::keystore2::KeyMetadata _aidl_return;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_key);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readNullableData(_aidl_in, &in_attestationKey);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_params);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_flags);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_entropy);
      if (_aidl_ret_status != STATUS_OK) break;

      ::ndk::ScopedAStatus _aidl_status = _aidl_impl->generateKey(in_key, in_attestationKey, in_params, in_flags, in_entropy, &_aidl_return);
      _aidl_ret_status = AParcel_writeStatusHeader(_aidl_out, _aidl_status.get());
      if (_aidl_ret_status != STATUS_OK) break;

      if (!AStatus_isOk(_aidl_status.get())) break;

      _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_out, _aidl_return);
      if (_aidl_ret_status != STATUS_OK) break;

      break;
    }
    case (FIRST_CALL_TRANSACTION + 2 /*importKey*/): {
      ::aidl::android::system::keystore2::KeyDescriptor in_key;
      std::optional<::aidl::android::system::keystore2::KeyDescriptor> in_attestationKey;
      std::vector<::aidl::android::hardware::security::keymint::KeyParameter> in_params;
      int32_t in_flags;
      std::vector<uint8_t> in_keyData;
      ::aidl::android::system::keystore2::KeyMetadata _aidl_return;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_key);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readNullableData(_aidl_in, &in_attestationKey);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_params);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_flags);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_keyData);
      if (_aidl_ret_status != STATUS_OK) break;

      ::ndk::ScopedAStatus _aidl_status = _aidl_impl->importKey(in_key, in_attestationKey, in_params, in_flags, in_keyData, &_aidl_return);
      _aidl_ret_status = AParcel_writeStatusHeader(_aidl_out, _aidl_status.get());
      if (_aidl_ret_status != STATUS_OK) break;

      if (!AStatus_isOk(_aidl_status.get())) break;

      _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_out, _aidl_return);
      if (_aidl_ret_status != STATUS_OK) break;

      break;
    }
    case (FIRST_CALL_TRANSACTION + 3 /*importWrappedKey*/): {
      ::aidl::android::system::keystore2::KeyDescriptor in_key;
      ::aidl::android::system::keystore2::KeyDescriptor in_wrappingKey;
      std::optional<std::vector<uint8_t>> in_maskingKey;
      std::vector<::aidl::android::hardware::security::keymint::KeyParameter> in_params;
      std::vector<::aidl::android::system::keystore2::AuthenticatorSpec> in_authenticators;
      ::aidl::android::system::keystore2::KeyMetadata _aidl_return;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_key);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_wrappingKey);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readNullableData(_aidl_in, &in_maskingKey);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_params);
      if (_aidl_ret_status != STATUS_OK) break;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_authenticators);
      if (_aidl_ret_status != STATUS_OK) break;

      ::ndk::ScopedAStatus _aidl_status = _aidl_impl->importWrappedKey(in_key, in_wrappingKey, in_maskingKey, in_params, in_authenticators, &_aidl_return);
      _aidl_ret_status = AParcel_writeStatusHeader(_aidl_out, _aidl_status.get());
      if (_aidl_ret_status != STATUS_OK) break;

      if (!AStatus_isOk(_aidl_status.get())) break;

      _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_out, _aidl_return);
      if (_aidl_ret_status != STATUS_OK) break;

      break;
    }
    case (FIRST_CALL_TRANSACTION + 4 /*convertStorageKeyToEphemeral*/): {
      ::aidl::android::system::keystore2::KeyDescriptor in_storageKey;
      ::aidl::android::system::keystore2::EphemeralStorageKeyResponse _aidl_return;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_storageKey);
      if (_aidl_ret_status != STATUS_OK) break;

      ::ndk::ScopedAStatus _aidl_status = _aidl_impl->convertStorageKeyToEphemeral(in_storageKey, &_aidl_return);
      _aidl_ret_status = AParcel_writeStatusHeader(_aidl_out, _aidl_status.get());
      if (_aidl_ret_status != STATUS_OK) break;

      if (!AStatus_isOk(_aidl_status.get())) break;

      _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_out, _aidl_return);
      if (_aidl_ret_status != STATUS_OK) break;

      break;
    }
    case (FIRST_CALL_TRANSACTION + 5 /*deleteKey*/): {
      ::aidl::android::system::keystore2::KeyDescriptor in_key;

      _aidl_ret_status = ::ndk::AParcel_readData(_aidl_in, &in_key);
      if (_aidl_ret_status != STATUS_OK) break;

      ::ndk::ScopedAStatus _aidl_status = _aidl_impl->deleteKey(in_key);
      _aidl_ret_status = AParcel_writeStatusHeader(_aidl_out, _aidl_status.get());
      if (_aidl_ret_status != STATUS_OK) break;

      if (!AStatus_isOk(_aidl_status.get())) break;

      break;
    }
  }
  return _aidl_ret_status;
}

static AIBinder_Class* _g_aidl_android_system_keystore2_IKeystoreSecurityLevel_clazz = ::ndk::ICInterface::defineClass(IKeystoreSecurityLevel::descriptor, _aidl_android_system_keystore2_IKeystoreSecurityLevel_onTransact);

BpKeystoreSecurityLevel::BpKeystoreSecurityLevel(const ::ndk::SpAIBinder& binder) : BpCInterface(binder) {}
BpKeystoreSecurityLevel::~BpKeystoreSecurityLevel() {}

::ndk::ScopedAStatus BpKeystoreSecurityLevel::createOperation(const ::aidl::android::system::keystore2::KeyDescriptor& in_key, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& in_operationParameters, bool in_forced, ::aidl::android::system::keystore2::CreateOperationResponse* _aidl_return) {
  binder_status_t _aidl_ret_status = STATUS_OK;
  ::ndk::ScopedAStatus _aidl_status;
  ::ndk::ScopedAParcel _aidl_in;
  ::ndk::ScopedAParcel _aidl_out;

  _aidl_ret_status = AIBinder_prepareTransaction(asBinder().get(), _aidl_in.getR());
  AParcel_markSensitive(_aidl_in.get());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_key);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_operationParameters);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_forced);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AIBinder_transact(
    asBinder().get(),
    (FIRST_CALL_TRANSACTION + 0 /*createOperation*/),
    _aidl_in.getR(),
    _aidl_out.getR(),
    FLAG_CLEAR_BUF
    #ifdef BINDER_STABILITY_SUPPORT
    | FLAG_PRIVATE_LOCAL
    #endif  // BINDER_STABILITY_SUPPORT
    );
  if (_aidl_ret_status == STATUS_UNKNOWN_TRANSACTION && IKeystoreSecurityLevel::getDefaultImpl()) {
    _aidl_status = IKeystoreSecurityLevel::getDefaultImpl()->createOperation(in_key, in_operationParameters, in_forced, _aidl_return);
    goto _aidl_status_return;
  }
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AParcel_readStatusHeader(_aidl_out.get(), _aidl_status.getR());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  if (!AStatus_isOk(_aidl_status.get())) goto _aidl_status_return;
  _aidl_ret_status = ::ndk::AParcel_readData(_aidl_out.get(), _aidl_return);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_error:
  _aidl_status.set(AStatus_fromStatus(_aidl_ret_status));
  _aidl_status_return:
  return _aidl_status;
}
::ndk::ScopedAStatus BpKeystoreSecurityLevel::generateKey(const ::aidl::android::system::keystore2::KeyDescriptor& in_key, const std::optional<::aidl::android::system::keystore2::KeyDescriptor>& in_attestationKey, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& in_params, int32_t in_flags, const std::vector<uint8_t>& in_entropy, ::aidl::android::system::keystore2::KeyMetadata* _aidl_return) {
  binder_status_t _aidl_ret_status = STATUS_OK;
  ::ndk::ScopedAStatus _aidl_status;
  ::ndk::ScopedAParcel _aidl_in;
  ::ndk::ScopedAParcel _aidl_out;

  _aidl_ret_status = AIBinder_prepareTransaction(asBinder().get(), _aidl_in.getR());
  AParcel_markSensitive(_aidl_in.get());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_key);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeNullableData(_aidl_in.get(), in_attestationKey);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_params);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_flags);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_entropy);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AIBinder_transact(
    asBinder().get(),
    (FIRST_CALL_TRANSACTION + 1 /*generateKey*/),
    _aidl_in.getR(),
    _aidl_out.getR(),
    FLAG_CLEAR_BUF
    #ifdef BINDER_STABILITY_SUPPORT
    | FLAG_PRIVATE_LOCAL
    #endif  // BINDER_STABILITY_SUPPORT
    );
  if (_aidl_ret_status == STATUS_UNKNOWN_TRANSACTION && IKeystoreSecurityLevel::getDefaultImpl()) {
    _aidl_status = IKeystoreSecurityLevel::getDefaultImpl()->generateKey(in_key, in_attestationKey, in_params, in_flags, in_entropy, _aidl_return);
    goto _aidl_status_return;
  }
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AParcel_readStatusHeader(_aidl_out.get(), _aidl_status.getR());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  if (!AStatus_isOk(_aidl_status.get())) goto _aidl_status_return;
  _aidl_ret_status = ::ndk::AParcel_readData(_aidl_out.get(), _aidl_return);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_error:
  _aidl_status.set(AStatus_fromStatus(_aidl_ret_status));
  _aidl_status_return:
  return _aidl_status;
}
::ndk::ScopedAStatus BpKeystoreSecurityLevel::importKey(const ::aidl::android::system::keystore2::KeyDescriptor& in_key, const std::optional<::aidl::android::system::keystore2::KeyDescriptor>& in_attestationKey, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& in_params, int32_t in_flags, const std::vector<uint8_t>& in_keyData, ::aidl::android::system::keystore2::KeyMetadata* _aidl_return) {
  binder_status_t _aidl_ret_status = STATUS_OK;
  ::ndk::ScopedAStatus _aidl_status;
  ::ndk::ScopedAParcel _aidl_in;
  ::ndk::ScopedAParcel _aidl_out;

  _aidl_ret_status = AIBinder_prepareTransaction(asBinder().get(), _aidl_in.getR());
  AParcel_markSensitive(_aidl_in.get());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_key);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeNullableData(_aidl_in.get(), in_attestationKey);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_params);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_flags);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_keyData);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AIBinder_transact(
    asBinder().get(),
    (FIRST_CALL_TRANSACTION + 2 /*importKey*/),
    _aidl_in.getR(),
    _aidl_out.getR(),
    FLAG_CLEAR_BUF
    #ifdef BINDER_STABILITY_SUPPORT
    | FLAG_PRIVATE_LOCAL
    #endif  // BINDER_STABILITY_SUPPORT
    );
  if (_aidl_ret_status == STATUS_UNKNOWN_TRANSACTION && IKeystoreSecurityLevel::getDefaultImpl()) {
    _aidl_status = IKeystoreSecurityLevel::getDefaultImpl()->importKey(in_key, in_attestationKey, in_params, in_flags, in_keyData, _aidl_return);
    goto _aidl_status_return;
  }
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AParcel_readStatusHeader(_aidl_out.get(), _aidl_status.getR());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  if (!AStatus_isOk(_aidl_status.get())) goto _aidl_status_return;
  _aidl_ret_status = ::ndk::AParcel_readData(_aidl_out.get(), _aidl_return);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_error:
  _aidl_status.set(AStatus_fromStatus(_aidl_ret_status));
  _aidl_status_return:
  return _aidl_status;
}
::ndk::ScopedAStatus BpKeystoreSecurityLevel::importWrappedKey(const ::aidl::android::system::keystore2::KeyDescriptor& in_key, const ::aidl::android::system::keystore2::KeyDescriptor& in_wrappingKey, const std::optional<std::vector<uint8_t>>& in_maskingKey, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& in_params, const std::vector<::aidl::android::system::keystore2::AuthenticatorSpec>& in_authenticators, ::aidl::android::system::keystore2::KeyMetadata* _aidl_return) {
  binder_status_t _aidl_ret_status = STATUS_OK;
  ::ndk::ScopedAStatus _aidl_status;
  ::ndk::ScopedAParcel _aidl_in;
  ::ndk::ScopedAParcel _aidl_out;

  _aidl_ret_status = AIBinder_prepareTransaction(asBinder().get(), _aidl_in.getR());
  AParcel_markSensitive(_aidl_in.get());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_key);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_wrappingKey);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeNullableData(_aidl_in.get(), in_maskingKey);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_params);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_authenticators);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AIBinder_transact(
    asBinder().get(),
    (FIRST_CALL_TRANSACTION + 3 /*importWrappedKey*/),
    _aidl_in.getR(),
    _aidl_out.getR(),
    FLAG_CLEAR_BUF
    #ifdef BINDER_STABILITY_SUPPORT
    | FLAG_PRIVATE_LOCAL
    #endif  // BINDER_STABILITY_SUPPORT
    );
  if (_aidl_ret_status == STATUS_UNKNOWN_TRANSACTION && IKeystoreSecurityLevel::getDefaultImpl()) {
    _aidl_status = IKeystoreSecurityLevel::getDefaultImpl()->importWrappedKey(in_key, in_wrappingKey, in_maskingKey, in_params, in_authenticators, _aidl_return);
    goto _aidl_status_return;
  }
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AParcel_readStatusHeader(_aidl_out.get(), _aidl_status.getR());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  if (!AStatus_isOk(_aidl_status.get())) goto _aidl_status_return;
  _aidl_ret_status = ::ndk::AParcel_readData(_aidl_out.get(), _aidl_return);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_error:
  _aidl_status.set(AStatus_fromStatus(_aidl_ret_status));
  _aidl_status_return:
  return _aidl_status;
}
::ndk::ScopedAStatus BpKeystoreSecurityLevel::convertStorageKeyToEphemeral(const ::aidl::android::system::keystore2::KeyDescriptor& in_storageKey, ::aidl::android::system::keystore2::EphemeralStorageKeyResponse* _aidl_return) {
  binder_status_t _aidl_ret_status = STATUS_OK;
  ::ndk::ScopedAStatus _aidl_status;
  ::ndk::ScopedAParcel _aidl_in;
  ::ndk::ScopedAParcel _aidl_out;

  _aidl_ret_status = AIBinder_prepareTransaction(asBinder().get(), _aidl_in.getR());
  AParcel_markSensitive(_aidl_in.get());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_storageKey);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AIBinder_transact(
    asBinder().get(),
    (FIRST_CALL_TRANSACTION + 4 /*convertStorageKeyToEphemeral*/),
    _aidl_in.getR(),
    _aidl_out.getR(),
    FLAG_CLEAR_BUF
    #ifdef BINDER_STABILITY_SUPPORT
    | FLAG_PRIVATE_LOCAL
    #endif  // BINDER_STABILITY_SUPPORT
    );
  if (_aidl_ret_status == STATUS_UNKNOWN_TRANSACTION && IKeystoreSecurityLevel::getDefaultImpl()) {
    _aidl_status = IKeystoreSecurityLevel::getDefaultImpl()->convertStorageKeyToEphemeral(in_storageKey, _aidl_return);
    goto _aidl_status_return;
  }
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AParcel_readStatusHeader(_aidl_out.get(), _aidl_status.getR());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  if (!AStatus_isOk(_aidl_status.get())) goto _aidl_status_return;
  _aidl_ret_status = ::ndk::AParcel_readData(_aidl_out.get(), _aidl_return);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_error:
  _aidl_status.set(AStatus_fromStatus(_aidl_ret_status));
  _aidl_status_return:
  return _aidl_status;
}
::ndk::ScopedAStatus BpKeystoreSecurityLevel::deleteKey(const ::aidl::android::system::keystore2::KeyDescriptor& in_key) {
  binder_status_t _aidl_ret_status = STATUS_OK;
  ::ndk::ScopedAStatus _aidl_status;
  ::ndk::ScopedAParcel _aidl_in;
  ::ndk::ScopedAParcel _aidl_out;

  _aidl_ret_status = AIBinder_prepareTransaction(asBinder().get(), _aidl_in.getR());
  AParcel_markSensitive(_aidl_in.get());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = ::ndk::AParcel_writeData(_aidl_in.get(), in_key);
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AIBinder_transact(
    asBinder().get(),
    (FIRST_CALL_TRANSACTION + 5 /*deleteKey*/),
    _aidl_in.getR(),
    _aidl_out.getR(),
    FLAG_CLEAR_BUF
    #ifdef BINDER_STABILITY_SUPPORT
    | FLAG_PRIVATE_LOCAL
    #endif  // BINDER_STABILITY_SUPPORT
    );
  if (_aidl_ret_status == STATUS_UNKNOWN_TRANSACTION && IKeystoreSecurityLevel::getDefaultImpl()) {
    _aidl_status = IKeystoreSecurityLevel::getDefaultImpl()->deleteKey(in_key);
    goto _aidl_status_return;
  }
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  _aidl_ret_status = AParcel_readStatusHeader(_aidl_out.get(), _aidl_status.getR());
  if (_aidl_ret_status != STATUS_OK) goto _aidl_error;

  if (!AStatus_isOk(_aidl_status.get())) goto _aidl_status_return;
  _aidl_error:
  _aidl_status.set(AStatus_fromStatus(_aidl_ret_status));
  _aidl_status_return:
  return _aidl_status;
}
// Source for BnKeystoreSecurityLevel
BnKeystoreSecurityLevel::BnKeystoreSecurityLevel() {}
BnKeystoreSecurityLevel::~BnKeystoreSecurityLevel() {}
::ndk::SpAIBinder BnKeystoreSecurityLevel::createBinder() {
  AIBinder* binder = AIBinder_new(_g_aidl_android_system_keystore2_IKeystoreSecurityLevel_clazz, static_cast<void*>(this));
  #ifdef BINDER_STABILITY_SUPPORT
  AIBinder_markCompilationUnitStability(binder);
  #endif  // BINDER_STABILITY_SUPPORT
  return ::ndk::SpAIBinder(binder);
}
// Source for IKeystoreSecurityLevel
const char* IKeystoreSecurityLevel::descriptor = "android.system.keystore2.IKeystoreSecurityLevel";
IKeystoreSecurityLevel::IKeystoreSecurityLevel() {}
IKeystoreSecurityLevel::~IKeystoreSecurityLevel() {}


std::shared_ptr<IKeystoreSecurityLevel> IKeystoreSecurityLevel::fromBinder(const ::ndk::SpAIBinder& binder) {
  if (!AIBinder_associateClass(binder.get(), _g_aidl_android_system_keystore2_IKeystoreSecurityLevel_clazz)) {
    #if __ANDROID_API__ >= 31
    const AIBinder_Class* originalClass = AIBinder_getClass(binder.get());
    if (originalClass == nullptr) return nullptr;
    if (0 == strcmp(AIBinder_Class_getDescriptor(originalClass), descriptor)) {
      return ::ndk::SharedRefBase::make<BpKeystoreSecurityLevel>(binder);
    }
    #endif
    return nullptr;
  }
  std::shared_ptr<::ndk::ICInterface> interface = ::ndk::ICInterface::asInterface(binder.get());
  if (interface) {
    return std::static_pointer_cast<IKeystoreSecurityLevel>(interface);
  }
  return ::ndk::SharedRefBase::make<BpKeystoreSecurityLevel>(binder);
}

binder_status_t IKeystoreSecurityLevel::writeToParcel(AParcel* parcel, const std::shared_ptr<IKeystoreSecurityLevel>& instance) {
  return AParcel_writeStrongBinder(parcel, instance ? instance->asBinder().get() : nullptr);
}
binder_status_t IKeystoreSecurityLevel::readFromParcel(const AParcel* parcel, std::shared_ptr<IKeystoreSecurityLevel>* instance) {
  ::ndk::SpAIBinder binder;
  binder_status_t status = AParcel_readStrongBinder(parcel, binder.getR());
  if (status != STATUS_OK) return status;
  *instance = IKeystoreSecurityLevel::fromBinder(binder);
  return STATUS_OK;
}
bool IKeystoreSecurityLevel::setDefaultImpl(const std::shared_ptr<IKeystoreSecurityLevel>& impl) {
  // Only one user of this interface can use this function
  // at a time. This is a heuristic to detect if two different
  // users in the same process use this function.
  assert(!IKeystoreSecurityLevel::default_impl);
  if (impl) {
    IKeystoreSecurityLevel::default_impl = impl;
    return true;
  }
  return false;
}
const std::shared_ptr<IKeystoreSecurityLevel>& IKeystoreSecurityLevel::getDefaultImpl() {
  return IKeystoreSecurityLevel::default_impl;
}
std::shared_ptr<IKeystoreSecurityLevel> IKeystoreSecurityLevel::default_impl = nullptr;
::ndk::ScopedAStatus IKeystoreSecurityLevelDefault::createOperation(const ::aidl::android::system::keystore2::KeyDescriptor& /*in_key*/, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& /*in_operationParameters*/, bool /*in_forced*/, ::aidl::android::system::keystore2::CreateOperationResponse* /*_aidl_return*/) {
  ::ndk::ScopedAStatus _aidl_status;
  _aidl_status.set(AStatus_fromStatus(STATUS_UNKNOWN_TRANSACTION));
  return _aidl_status;
}
::ndk::ScopedAStatus IKeystoreSecurityLevelDefault::generateKey(const ::aidl::android::system::keystore2::KeyDescriptor& /*in_key*/, const std::optional<::aidl::android::system::keystore2::KeyDescriptor>& /*in_attestationKey*/, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& /*in_params*/, int32_t /*in_flags*/, const std::vector<uint8_t>& /*in_entropy*/, ::aidl::android::system::keystore2::KeyMetadata* /*_aidl_return*/) {
  ::ndk::ScopedAStatus _aidl_status;
  _aidl_status.set(AStatus_fromStatus(STATUS_UNKNOWN_TRANSACTION));
  return _aidl_status;
}
::ndk::ScopedAStatus IKeystoreSecurityLevelDefault::importKey(const ::aidl::android::system::keystore2::KeyDescriptor& /*in_key*/, const std::optional<::aidl::android::system::keystore2::KeyDescriptor>& /*in_attestationKey*/, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& /*in_params*/, int32_t /*in_flags*/, const std::vector<uint8_t>& /*in_keyData*/, ::aidl::android::system::keystore2::KeyMetadata* /*_aidl_return*/) {
  ::ndk::ScopedAStatus _aidl_status;
  _aidl_status.set(AStatus_fromStatus(STATUS_UNKNOWN_TRANSACTION));
  return _aidl_status;
}
::ndk::ScopedAStatus IKeystoreSecurityLevelDefault::importWrappedKey(const ::aidl::android::system::keystore2::KeyDescriptor& /*in_key*/, const ::aidl::android::system::keystore2::KeyDescriptor& /*in_wrappingKey*/, const std::optional<std::vector<uint8_t>>& /*in_maskingKey*/, const std::vector<::aidl::android::hardware::security::keymint::KeyParameter>& /*in_params*/, const std::vector<::aidl::android::system::keystore2::AuthenticatorSpec>& /*in_authenticators*/, ::aidl::android::system::keystore2::KeyMetadata* /*_aidl_return*/) {
  ::ndk::ScopedAStatus _aidl_status;
  _aidl_status.set(AStatus_fromStatus(STATUS_UNKNOWN_TRANSACTION));
  return _aidl_status;
}
::ndk::ScopedAStatus IKeystoreSecurityLevelDefault::convertStorageKeyToEphemeral(const ::aidl::android::system::keystore2::KeyDescriptor& /*in_storageKey*/, ::aidl::android::system::keystore2::EphemeralStorageKeyResponse* /*_aidl_return*/) {
  ::ndk::ScopedAStatus _aidl_status;
  _aidl_status.set(AStatus_fromStatus(STATUS_UNKNOWN_TRANSACTION));
  return _aidl_status;
}
::ndk::ScopedAStatus IKeystoreSecurityLevelDefault::deleteKey(const ::aidl::android::system::keystore2::KeyDescriptor& /*in_key*/) {
  ::ndk::ScopedAStatus _aidl_status;
  _aidl_status.set(AStatus_fromStatus(STATUS_UNKNOWN_TRANSACTION));
  return _aidl_status;
}
::ndk::SpAIBinder IKeystoreSecurityLevelDefault::asBinder() {
  return ::ndk::SpAIBinder();
}
bool IKeystoreSecurityLevelDefault::isRemote() {
  return false;
}
}  // namespace keystore2
}  // namespace system
}  // namespace android
}  // namespace aidl
