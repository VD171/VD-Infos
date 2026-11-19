/*
 * ======================================================================
 *  VD INFOS  ::  method debugger
 *  Read every device info by every method, then compare.
 *
 *  Copyright (C) 2026  VD171
 *  SPDX-License-Identifier: AGPL-3.0-or-later
 *
 *  Free software under the GNU AGPL v3 or later. NETWORK COPYLEFT: run a
 *  modified version, even as a service, and you MUST offer its source.
 *
 *  Site           : https://vd171.ru
 *  Site           : https://vd.priv8.ru
 *  Source         : https://github.com/VD171/VD-Infos
 *  GitHub         : @VD171 https://github.com/VD171
 *  XDA-Developers : @VD171 https://xdaforums.com/m/vd171.4699873/
 *  XDA thread     : https://xdaforums.com/t/VD-Infos.4097379/
 *  Telegram       : @VD_Priv8 https://t.me/VD_Priv8
 *  Channel        : @Priv8_VD https://t.me/Priv8_VD
 *  Discord        : @VD.Priv8 https://discord.com/users/1296831918989639721
 *  E-mail         : vd.priv8@pm.me
 * ======================================================================
 */

#include <jni.h>
#include <vector>
#include <optional>
#include <dlfcn.h>
#include <android/binder_ibinder.h>
#include <android/binder_auto_utils.h>

#include "aidl/android/system/keystore2/IKeystoreService.h"
#include "aidl/android/system/keystore2/IKeystoreSecurityLevel.h"
#include "aidl/android/system/keystore2/KeyDescriptor.h"
#include "aidl/android/system/keystore2/KeyMetadata.h"
#include "aidl/android/system/keystore2/Domain.h"
#include "aidl/android/hardware/security/keymint/SecurityLevel.h"
#include "aidl/android/hardware/security/keymint/KeyParameter.h"
#include "aidl/android/hardware/security/keymint/KeyParameterValue.h"
#include "aidl/android/hardware/security/keymint/Tag.h"
#include "aidl/android/hardware/security/keymint/Algorithm.h"
#include "aidl/android/hardware/security/keymint/EcCurve.h"
#include "aidl/android/hardware/security/keymint/KeyPurpose.h"
#include "aidl/android/hardware/security/keymint/Digest.h"

namespace ks = aidl::android::system::keystore2;
namespace km = aidl::android::hardware::security::keymint;

extern "C" JNIEXPORT jbyteArray JNICALL
Java_ru_vd171_vdinfos_probe_NativeBridge_nAttestNative(JNIEnv* env, jobject, jbyteArray jchallenge) {
    jsize clen = env->GetArrayLength(jchallenge);
    std::vector<uint8_t> challenge((size_t) clen);
    if (clen > 0) env->GetByteArrayRegion(jchallenge, 0, clen, reinterpret_cast<jbyte*>(challenge.data()));

    void* libbinder = dlopen("libbinder_ndk.so", RTLD_NOW);
    if (!libbinder) return nullptr;
    using CheckServiceFn = AIBinder* (*)(const char*);
    auto checkService = reinterpret_cast<CheckServiceFn>(dlsym(libbinder, "AServiceManager_checkService"));
    if (!checkService) { dlclose(libbinder); return nullptr; }
    ndk::SpAIBinder binder(checkService("android.system.keystore2.IKeystoreService/default"));
    dlclose(libbinder);
    std::shared_ptr<ks::IKeystoreService> service = ks::IKeystoreService::fromBinder(binder);
    if (!service) return nullptr;

    std::shared_ptr<ks::IKeystoreSecurityLevel> level;
    if (!service->getSecurityLevel(km::SecurityLevel::TRUSTED_ENVIRONMENT, &level).isOk() || !level) return nullptr;

    ks::KeyDescriptor desc;
    desc.domain = ks::Domain::APP;
    desc.nspace = 0;
    desc.alias = "vdinfos_attest_nat92";

    auto param = [](km::Tag tag, km::KeyParameterValue value) {
        km::KeyParameter p;
        p.tag = tag;
        p.value = std::move(value);
        return p;
    };
    std::vector<km::KeyParameter> params;
    params.push_back(param(km::Tag::ALGORITHM, km::KeyParameterValue::make<km::KeyParameterValue::algorithm>(km::Algorithm::EC)));
    params.push_back(param(km::Tag::EC_CURVE, km::KeyParameterValue::make<km::KeyParameterValue::ecCurve>(km::EcCurve::P_256)));
    params.push_back(param(km::Tag::PURPOSE, km::KeyParameterValue::make<km::KeyParameterValue::keyPurpose>(km::KeyPurpose::SIGN)));
    params.push_back(param(km::Tag::PURPOSE, km::KeyParameterValue::make<km::KeyParameterValue::keyPurpose>(km::KeyPurpose::VERIFY)));
    params.push_back(param(km::Tag::DIGEST, km::KeyParameterValue::make<km::KeyParameterValue::digest>(km::Digest::SHA_2_256)));
    params.push_back(param(km::Tag::NO_AUTH_REQUIRED, km::KeyParameterValue::make<km::KeyParameterValue::boolValue>(true)));
    params.push_back(param(km::Tag::ATTESTATION_CHALLENGE, km::KeyParameterValue::make<km::KeyParameterValue::blob>(challenge)));

    ks::KeyMetadata meta;
    bool ok = level->generateKey(desc, std::nullopt, params, 0, std::vector<uint8_t>{}, &meta).isOk();
    service->deleteKey(desc);
    if (!ok) return nullptr;

    std::vector<uint8_t> der;
    if (meta.certificate && !meta.certificate->empty()) der.insert(der.end(), meta.certificate->begin(), meta.certificate->end());
    if (meta.certificateChain && !meta.certificateChain->empty()) der.insert(der.end(), meta.certificateChain->begin(), meta.certificateChain->end());
    if (der.empty()) return nullptr;

    jbyteArray out = env->NewByteArray((jsize) der.size());
    if (out) env->SetByteArrayRegion(out, 0, (jsize) der.size(), reinterpret_cast<const jbyte*>(der.data()));
    return out;
}
