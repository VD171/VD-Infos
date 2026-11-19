/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
///////////////////////////////////////////////////////////////////////////////
// THIS FILE IS IMMUTABLE. DO NOT EDIT IN ANY CASE.                          //
///////////////////////////////////////////////////////////////////////////////

// This file is a snapshot of an AIDL file. Do not edit it manually. There are
// two cases:
// 1). this is a frozen version file - do not edit this in any case.
// 2). this is a 'current' file. If you make a backwards compatible change to
//     the interface (from the latest frozen version), the build system will
//     prompt you to update this file with `m <name>-update-api`.
//
// You must not make a backward incompatible change to any AIDL file built
// with the aidl_interface module type with versions property set. The module
// type is used to build AIDL files in a way that they can be used across
// independently updatable components of the system. If a device is shipped
// with such a backward incompatible change, it has a high risk of breaking
// later when a module using the interface is updated, e.g., Mainline modules.

package android.hardware.security.keymint;
/* @hide */
@Backing(type="int") 
enum Tag {
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
}
