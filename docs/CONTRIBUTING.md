> **Languages:** English · [العربية](ar/CONTRIBUTING.md) · [Čeština](cs/CONTRIBUTING.md) · [Deutsch](de/CONTRIBUTING.md) · [Español](es/CONTRIBUTING.md) · [فارسی](fa/CONTRIBUTING.md) · [Français](fr/CONTRIBUTING.md) · [हिन्दी](hi/CONTRIBUTING.md) · [Bahasa Indonesia](id/CONTRIBUTING.md) · [Italiano](it/CONTRIBUTING.md) · [日本語](ja/CONTRIBUTING.md) · [한국어](ko/CONTRIBUTING.md) · [Nederlands](nl/CONTRIBUTING.md) · [Polski](pl/CONTRIBUTING.md) · [Português](pt/CONTRIBUTING.md) · [Русский](ru/CONTRIBUTING.md) · [Svenska](sv/CONTRIBUTING.md) · [ไทย](th/CONTRIBUTING.md) · [Türkçe](tr/CONTRIBUTING.md) · [Українська](uk/CONTRIBUTING.md) · [Tiếng Việt](vi/CONTRIBUTING.md) · [中文](zh/CONTRIBUTING.md)

---

# Contributing

Thanks for your interest in VD Infos. Contributions are welcome: detection lists,
translations, bug reports, documentation and code. Most contributions need no Kotlin.

## The easiest contribution: the lists

Most of what the app looks for lives as **plain-text lists** under
`VDInfos/app/src/main/assets/data/`, not as code. Adding or removing a target is editing
one line and opening a pull request:

- `*_apps.txt` - one package name per line.
- `props.txt` - the system-property catalog, `CATEGORY<tab>key`.
- `spoof_keys.txt` - the settings matrix, `key:TYPE`.
- `known_certs.txt` - the signing-certificate catalog, `SHA-256<tab>owner` (the full
  hash, taken from a real APK with `apksigner verify --print-certs`).
- other `.txt` files - one entry per line (kernel-module name fragments, file names,
  and so on).

A `#` starts a comment and blank lines are ignored. Some files pack a few space- or
tab-separated fields per line, or optional markers; keep the shape of the lines already
in the file.

## Ground rules

- **VD Infos reads the truth and never fakes anything.** It reports what each method
  returned, exactly. Do not soften a reading to make it look nicer, and do not add
  anything that would change a value rather than read it.
- **Privacy is absolute.** Everything runs on-device. Never add a network call, or
  anything that sends a reading off the device.
- **Exports stay masked.** Identity-bearing values are masked in a shared report. Keep
  it that way.

## Translations

Translations are a valuable contribution. The readme is translated per language
(`README.<lang>.md`), the app strings per language, and this documentation under
`docs/<lang>/`. Keep terminology consistent within a language, match the existing tone,
and never machine-translate a technical term you are unsure of.

## Reporting a bug

Open an issue with your device model, Android version, and, if it is about a reading, a
shared **export** (its identity-bearing values are masked). The export is built for
exactly this.

## Code

- Build release, never debug. Test on a real device. See [Building](BUILDING.md).
- Keep comments out of the code; names explain intent. The only header is the license
  banner at the top of each source file.
- Match the existing style and keep changes focused.

## License

By contributing you agree that your contribution is licensed under the project's
**GNU AGPL-3.0-or-later**.

<!-- doc-footer -->

---

> _This documentation was generated with Claude (Anthropic) and is maintained by VD171. Corrections and improvements are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md)._
