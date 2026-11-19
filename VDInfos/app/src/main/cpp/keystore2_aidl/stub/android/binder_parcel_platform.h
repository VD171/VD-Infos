// Shim for the platform-only NDK header. The public app NDK ships binder_parcel.h
// but not its _platform sibling; the generated AIDL backend for the keystore2
// interfaces (key material is sensitive) calls AParcel_markSensitive unconditionally.
// The symbol is a real platform export present on the device from API 33 on, but it
// is absent from the app NDK link stub, so it is declared weak here and resolved at
// load time against the device libbinder_ndk. vdks2 is only loaded on API 33+.
#pragma once
#include <android/binder_parcel.h>
__BEGIN_DECLS
binder_status_t AParcel_markSensitive(const AParcel* parcel) __attribute__((weak));
__END_DECLS
