> **Languages:** English · [العربية](ar/SECURITY.md) · [Čeština](cs/SECURITY.md) · [Deutsch](de/SECURITY.md) · [Español](es/SECURITY.md) · [فارسی](fa/SECURITY.md) · [Français](fr/SECURITY.md) · [हिन्दी](hi/SECURITY.md) · [Bahasa Indonesia](id/SECURITY.md) · [Italiano](it/SECURITY.md) · [日本語](ja/SECURITY.md) · [한국어](ko/SECURITY.md) · [Nederlands](nl/SECURITY.md) · [Polski](pl/SECURITY.md) · [Português](pt/SECURITY.md) · [Русский](ru/SECURITY.md) · [Svenska](sv/SECURITY.md) · [ไทย](th/SECURITY.md) · [Türkçe](tr/SECURITY.md) · [Українська](uk/SECURITY.md) · [Tiếng Việt](vi/SECURITY.md) · [中文](zh/SECURITY.md)

---

# Security policy

VD Infos is a privacy tool that reads sensitive device state, so security reports are
taken seriously.

## Reporting a vulnerability

Please report security issues **privately**, not in a public issue:

- E-mail **vd.priv8@pm.me**.
- Describe the problem, the affected version, and how to reproduce it.
- If it concerns data exposure, describe the class of problem; you do not need to attach
  a working exploit.

You will get a response as soon as possible. Please give a reasonable window to fix the
issue before any public disclosure.

## What counts as a security issue

Because of what VD Infos promises, these are especially important:

- Anything that would make the app reach the network, or send any reading off the
  device.
- Anything that would leak an identity-bearing value into a shared or saved **export**
  (a serial, IMEI, MAC address, advertising ID and the like) despite the masking.
- Anything that lets another app read the private report file that is meant to be
  readable only by the app itself or root.

## What is not a security issue

- A reading that looks "wrong" but is the honest value a method returned. VD Infos
  reports what each method said; reading the comparison is yours to do. See the
  [user guide](USER-GUIDE.md).
- A reading marked unavailable. That means a method could not read that value on your
  device, which is itself part of the comparison.

## Supported versions

Only the latest released version is supported. Please reproduce on the latest build
before reporting.

<!-- doc-footer -->

---

> _This documentation was generated with Claude (Anthropic) and is maintained by VD171. Corrections and improvements are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md)._
