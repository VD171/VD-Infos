// Shim for the platform-only NDK header. The public app NDK ships binder_ibinder.h
// but not its _platform sibling. The generated AIDL backend passes FLAG_CLEAR_BUF as
// a transaction flag so the kernel wipes the buffer holding key material; it is a
// plain transaction-flag constant, not a linked symbol. @VintfStability was stripped
// from the vendored .aidl, so none of the stability entry points are referenced.
#pragma once
#include <android/binder_ibinder.h>
enum {
    FLAG_CLEAR_BUF = 0x20,
};
