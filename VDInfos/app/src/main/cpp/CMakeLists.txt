# ======================================================================
#  VD INFOS  ::  method debugger
#  Read every device info by every method, then compare.
#
#  Copyright (C) 2026  VD171
#  SPDX-License-Identifier: AGPL-3.0-or-later
#
#  Free software under the GNU AGPL v3 or later. NETWORK COPYLEFT: run a
#  modified version, even as a service, and you MUST offer its source.
#
#  Site           : https://vd171.ru
#  Site           : https://vd.priv8.ru
#  Source         : https://github.com/VD171/VD-Infos
#  GitHub         : @VD171 https://github.com/VD171
#  XDA-Developers : @VD171 https://xdaforums.com/m/vd171.4699873/
#  XDA thread     : https://xdaforums.com/t/VD-Infos.4097379/
#  Telegram       : @VD_Priv8 https://t.me/VD_Priv8
#  Channel        : @Priv8_VD https://t.me/Priv8_VD
#  Discord        : @VD.Priv8 https://discord.com/users/1296831918989639721
#  E-mail         : vd.priv8@pm.me
# ======================================================================

cmake_minimum_required(VERSION 3.22.1)
project("vdinfos")

add_library(vdinfos SHARED native_probes.cpp)

find_library(log-lib log)
find_library(egl-lib EGL)
find_library(glesv2-lib GLESv2)

target_link_libraries(vdinfos ${log-lib} ${egl-lib} ${glesv2-lib})

target_compile_options(vdinfos PRIVATE -Wall -Wextra -fvisibility=hidden)

# Keystore2 attestation lens - isolated shared lib. libbinder_ndk exists only on
# API 29+, so this is never linked into the main lib (which must load on API 26);
# NativeBridge loads it lazily on API 33+ and treats a load failure as not-applicable.
file(GLOB_RECURSE KS2_AIDL_SRCS ${CMAKE_CURRENT_SOURCE_DIR}/keystore2_aidl/src/*.cpp)
add_library(vdks2 SHARED keystore2_native.cpp ${KS2_AIDL_SRCS})
target_include_directories(vdks2 PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/keystore2_aidl/include
    ${CMAKE_CURRENT_SOURCE_DIR}/keystore2_aidl/stub)
# libbinder_ndk is API 29+, keystore2 is API 31+, and the generated AIDL backend
# calls AParcel_markSensitive (API 33+); the android-26 (minSdk) sysroot has no stub
# for it. Link against the API-34 stub; the device binds its own lib at runtime and
# vdks2 is only loaded on API 33+.
if(ANDROID_ABI STREQUAL "arm64-v8a")
    set(VDKS2_CTRIPLE aarch64-linux-android)
    set(VDKS2_LTRIPLE aarch64-linux-android)
elseif(ANDROID_ABI STREQUAL "armeabi-v7a")
    set(VDKS2_CTRIPLE armv7a-linux-androideabi)
    set(VDKS2_LTRIPLE arm-linux-androideabi)
elseif(ANDROID_ABI STREQUAL "x86")
    set(VDKS2_CTRIPLE i686-linux-android)
    set(VDKS2_LTRIPLE i686-linux-android)
else()
    set(VDKS2_CTRIPLE x86_64-linux-android)
    set(VDKS2_LTRIPLE x86_64-linux-android)
endif()
set(VDKS2_STUB ${CMAKE_SYSROOT}/usr/lib/${VDKS2_LTRIPLE}/34/libbinder_ndk.so)
target_link_libraries(vdks2 ${VDKS2_STUB} ${log-lib})
# Raise the deployment target to 33 for this lib only (the AIDL backend needs the
# API 33 parcel symbols); keep --sysroot so the NDK headers are still found. The main
# lib stays at minSdk 26.
target_compile_options(vdks2 PRIVATE -w -fvisibility=hidden
    --target=${VDKS2_CTRIPLE}33 --sysroot=${CMAKE_SYSROOT})
