> **Languages:** [English](../BUILDING.md) · العربية · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# بناء VD Infos

VD Infos مشروع Android Gradle قياسي: Kotlin وJetpack Compose، مع عدسة أصلية صغيرة بلغة
C++ مبنية عبر الـ NDK. يمكن للـ CI بناؤه، ويمكنك إعادة إنتاجه محليًا.

## المتطلبات

- JDK (من 17 إلى 21) وAndroid SDK.
- Android NDK وCMake، للعدسة الأصلية (يجلب مكوّن Gradle Android الإضافي CMake مطابقًا إذا
  كان لديك مكوّن `cmake` من الـ SDK).
- `minSdk` 26، بحيث تعمل البنى على Android 8.0 وأحدث.

## بداية سريعة

```sh
# build both flavors, release
./gradlew assembleRelease
```

هذا يُنتج **ملفَّي APK**، واحدًا لكل flavor:

- **modern** (`SDK_35`) - الـ sandbox الحديث الصارم، البنية التي تُستخدم.
- **compat** (`SDK_27`) - هدف API أقدم، للمقارنة جنبًا إلى جنب فقط.

تهبط المخرجات في:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

لبناء flavor واحد فقط أثناء التكرار:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## التوقيع

تُوقَّع بنى الـ release بمفتاح **محلي وغير مُودَع** (الـ keystore وخصائصه متجاهَلة من قبل
git). بدونه تُبنى مهمة الـ release مع ذلك لكن الـ APK يخرج غير موقَّع. وفّر الـ keystore
الخاص بك لتوقيع بناءاتك الخاصة.

## الـ flavoran

يشحن التطبيق flavorين عن قصد. يستهدفان مستويات API مختلفة ويُبنيان من المصدر نفسه؛ ولا
يختلف إلا الهدف وبضع مسارات كود محروسة. التغيير الذي يمسّ كودًا أصليًا أو مسارًا خاصًا
بـ flavor ينبغي أن يُبنى ويُفحَص في **كلا** الـ flavorين، لا في الذي تكرّر عليه فقط.

## العدسة الأصلية

العدسة الأصلية هي وحدة ترجمة C++ واحدة خالية من التبعيات يبنيها الـ NDK كملف `.so` صغير
واحد، مربوط بالاسم عبر `RegisterNatives`. تُترجَم كجزء من بناء Gradle العادي؛ لا توجد خطوة
منفصلة. وتُبقى صغيرة وبسيطة عن قصد.

## أين تعيش الأشياء

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

انظر [البنية](ARCHITECTURE.md) لمعرفة كيف تتلاءم الأجزاء معًا، و[المساهمة](CONTRIBUTING.md)
لمعرفة كيفية إضافة هدف اكتشاف دون لمس الكود.

<!-- doc-footer -->

---

> _تم إنشاء هذا التوثيق باستخدام Claude (Anthropic) ويتولى صيانته VD171. التصحيحات والتحسينات مرحّب بها؛ راجع [CONTRIBUTING.md](CONTRIBUTING.md)._
