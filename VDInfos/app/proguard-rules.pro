# ======================================================================
#  VD INFOS  ::  method debugger
#  Read every device info by every method, then compare. A divergence is a hook.
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
#  Telegram       : @VD_Priv8 https://t.me/VD_Priv8
#  Discord        : @VD.Priv8 https://discord.com/users/1296831918989639721
#  E-mail         : vd.priv8@pm.me
# ======================================================================

# JNI: native methods are bound by name via RegisterNatives in JNI_OnLoad, so
# NativeBridge and its native entry points must not be renamed or stripped.
-keepclasseswithmembernames,includedescriptorclasses class ru.vd171.vdinfos.probe.NativeBridge {
    native <methods>;
}
-keep class ru.vd171.vdinfos.probe.NativeBridge { *; }

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class ru.vd171.vdinfos.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class ru.vd171.vdinfos.**$$serializer { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ru.vd171.vdinfos.** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
