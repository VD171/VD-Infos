#!/usr/bin/env python3
# ======================================================================
#  VD docs  ::  canonical nav + footer generator (shared, idempotent)
#
#  Copyright (C) 2026  VD171
#  SPDX-License-Identifier: AGPL-3.0-or-later
#
#  Source : https://github.com/VD171
# ======================================================================
"""Maintain the `> **Languages:**` switcher and the attribution footer on
EVERY documentation page (content + governance + Home), the English source and
every translation, in place. Idempotent and sentinel-bounded.

Banner-aware: if a doc opens with an HTML-comment banner (`<!-- ... -->`), the
switcher is kept right after it, not on line 1. Works unchanged for docs with
no banner. The switcher lists only the languages whose file exists (no broken
links); a single-language doc gets no switcher. The footer link to
CONTRIBUTING.md is a real relative path to wherever that file lives.

    python3 .github/scripts/gen-doc-nav.py [repo_root]

Identical across all VD repos; per-repo differences are discovered from the
filesystem (docs/, root governance, module/etc/RECOVERY).
"""
import os, sys

ROOT = os.path.abspath(sys.argv[1]) if len(sys.argv) > 1 else os.getcwd()

LANGS = ["en", "ar", "cs", "de", "es", "fa", "fr", "hi", "id", "it", "ja", "ko", "nl", "pl", "pt", "ru", "sv", "th", "tr", "uk", "vi", "zh"]
ENDONYM = {"en": "English", "ar": "العربية", "cs": "Čeština", "de": "Deutsch", "es": "Español", "fa": "فارسی", "fr": "Français", "hi": "हिन्दी", "id": "Bahasa Indonesia", "it": "Italiano", "ja": "日本語", "ko": "한국어", "nl": "Nederlands", "pl": "Polski", "pt": "Português", "ru": "Русский", "sv": "Svenska", "th": "ไทย", "tr": "Türkçe", "uk": "Українська", "vi": "Tiếng Việt", "zh": "中文"}
FOOTER_TMPL = {"en": "_This documentation was generated with Claude (Anthropic) and is maintained by VD171. Corrections and improvements are welcome; see [CONTRIBUTING.md](%s)._", "ar": "_تم إنشاء هذا التوثيق باستخدام Claude (Anthropic) ويتولى صيانته VD171. التصحيحات والتحسينات مرحّب بها؛ راجع [CONTRIBUTING.md](%s)._", "cs": "_Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [CONTRIBUTING.md](%s)._", "de": "_Diese Dokumentation wurde mit Claude (Anthropic) erstellt und wird von VD171 gepflegt. Korrekturen und Verbesserungen sind willkommen; siehe [CONTRIBUTING.md](%s)._", "es": "_Esta documentación se generó con Claude (Anthropic) y la mantiene VD171. Las correcciones y mejoras son bienvenidas; consulta [CONTRIBUTING.md](%s)._", "fa": "_این مستندات با Claude (Anthropic) تولید شده و توسط VD171 نگهداری می‌شود. اصلاحات و بهبودها خوش‌آمد است؛ به [CONTRIBUTING.md](%s) مراجعه کنید._", "fr": "_Cette documentation a été générée avec Claude (Anthropic) et est maintenue par VD171. Les corrections et améliorations sont les bienvenues ; voir [CONTRIBUTING.md](%s)._", "hi": "_यह दस्तावेज़ Claude (Anthropic) से तैयार किया गया है और इसे VD171 द्वारा अनुरक्षित किया जाता है। सुधार और संशोधन का स्वागत है; देखें [CONTRIBUTING.md](%s)।_", "id": "_Dokumentasi ini dibuat dengan Claude (Anthropic) dan dikelola oleh VD171. Koreksi dan perbaikan dipersilakan; lihat [CONTRIBUTING.md](%s)._", "it": "_Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [CONTRIBUTING.md](%s)._", "ja": "_このドキュメントは Claude (Anthropic) で生成され、VD171 によって保守されています。修正や改善を歓迎します。[CONTRIBUTING.md](%s) を参照してください。_", "ko": "_이 문서는 Claude (Anthropic)로 생성되었으며 VD171이 유지 관리합니다. 수정 및 개선을 환영합니다. [CONTRIBUTING.md](%s)를 참조하세요._", "nl": "_Deze documentatie is gegenereerd met Claude (Anthropic) en wordt onderhouden door VD171. Correcties en verbeteringen zijn welkom; zie [CONTRIBUTING.md](%s)._", "pl": "_Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [CONTRIBUTING.md](%s)._", "pt": "_Esta documentação foi gerada com o Claude (Anthropic) e é mantida por VD171. Correções e melhorias são bem-vindas; veja [CONTRIBUTING.md](%s)._", "ru": "_Эта документация создана с помощью Claude (Anthropic) и поддерживается VD171. Исправления и улучшения приветствуются; см. [CONTRIBUTING.md](%s)._", "sv": "_Denna dokumentation genererades med Claude (Anthropic) och underhålls av VD171. Rättelser och förbättringar välkomnas; se [CONTRIBUTING.md](%s)._", "th": "_เอกสารนี้สร้างขึ้นด้วย Claude (Anthropic) และดูแลโดย VD171 ยินดีรับการแก้ไขและการปรับปรุง ดู [CONTRIBUTING.md](%s)_", "tr": "_Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [CONTRIBUTING.md](%s)._", "uk": "_Ця документація створена за допомогою Claude (Anthropic) і підтримується VD171. Виправлення та покращення вітаються; див. [CONTRIBUTING.md](%s)._", "vi": "_Tài liệu này được tạo bằng Claude (Anthropic) và được duy trì bởi VD171. Chúng tôi hoan nghênh các chỉnh sửa và cải tiến; xem [CONTRIBUTING.md](%s)._", "zh": "_本文档由 Claude (Anthropic) 生成，并由 VD171 维护。欢迎修正和改进；请参阅 [CONTRIBUTING.md](%s)。_"}   # %s = relative link to CONTRIBUTING.md
FOOTER_MARK = "<!-- doc-footer -->"
ROOT_DOCS = ["CONTRIBUTING", "SECURITY", "CODE_OF_CONDUCT", "CHANGELOG"]
SKIP = {"_Sidebar", "Languages", "bug_report", "feature_request",
        "PULL_REQUEST_TEMPLATE"}


def discover():
    pages = {}
    docs = os.path.join(ROOT, "docs")
    if os.path.isdir(docs):
        for fn in os.listdir(docs):
            if fn.endswith(".md") and os.path.isfile(os.path.join(docs, fn)):
                base = fn[:-3]
                if base not in SKIP:
                    pages[base] = "docs"
    for d in ROOT_DOCS:
        if os.path.isfile(os.path.join(ROOT, d + ".md")):
            pages[d] = "."
    if os.path.isfile(os.path.join(ROOT, "module", "etc", "RECOVERY.md")):
        pages["RECOVERY"] = "module/etc"
    return pages


def en_path(doc, home):
    return (os.path.join(ROOT, doc + ".md") if home == "."
            else os.path.join(ROOT, home, doc + ".md"))


def tr_path(doc, lang):
    return os.path.join(ROOT, "docs", lang, doc + ".md")


def avail_langs(doc, home):
    out = []
    if os.path.isfile(en_path(doc, home)):
        out.append("en")
    for l in LANGS:
        if l != "en" and os.path.isfile(tr_path(doc, l)):
            out.append(l)
    return out


# ---- banner (optional HTML-comment block at the very top) --------------
def split_banner(text):
    """(banner, rest): banner is a leading <!-- ... --> block, else ''."""
    if text.lstrip("\n").startswith("<!--"):
        lead = len(text) - len(text.lstrip("\n"))
        end = text.find("-->", lead)
        if end != -1:
            end += 3
            return text[:end], text[end:]
    return "", text


# ---- language switcher -------------------------------------------------
def _target(doc, home, cur, other):
    if other == "en":
        if cur == "en":
            return None
        if home == "docs":
            return "../%s.md" % doc
        if home == ".":
            return "../../%s.md" % doc
        return "../../%s/%s.md" % (home, doc)
    if cur == "en":
        if home == "docs":
            return "%s/%s.md" % (other, doc)
        if home == ".":
            return "docs/%s/%s.md" % (other, doc)
        return "../../docs/%s/%s.md" % (other, doc)
    return "../%s/%s.md" % (other, doc)


def nav_line(doc, home, cur, avail):
    order = [l for l in LANGS if l in avail]
    parts = [ENDONYM[l] if l == cur
             else "[%s](%s)" % (ENDONYM[l], _target(doc, home, cur, l))
             for l in order]
    return "> **Languages:** " + " · ".join(parts)


def strip_header(text):
    """Return (banner, body) with any leading switcher removed from body."""
    banner, rest = split_banner(text)
    r = rest.lstrip("\n")
    lines = r.split("\n")
    if lines and lines[0].startswith("> **Languages:**"):
        i = 1
        while i < len(lines) and lines[i].strip() == "":
            i += 1
        if i < len(lines) and lines[i].strip() == "---":
            i += 1
            while i < len(lines) and lines[i].strip() == "":
                i += 1
        return banner, "\n".join(lines[i:])
    return banner, r


# ---- footer ------------------------------------------------------------
def contrib_file(lang):
    if lang != "en":
        return os.path.join(ROOT, "docs", lang, "CONTRIBUTING.md")
    for c in (os.path.join(ROOT, "CONTRIBUTING.md"),
              os.path.join(ROOT, "docs", "CONTRIBUTING.md")):
        if os.path.isfile(c):
            return c
    return os.path.join(ROOT, "CONTRIBUTING.md")


def contrib_link(page_path, lang):
    return os.path.relpath(contrib_file(lang), os.path.dirname(page_path))


def strip_footer(text):
    i = text.find(FOOTER_MARK)
    if i != -1:
        text = text[:i]
    text = text.rstrip("\n")
    lines = text.split("\n")
    while lines and lines[-1].strip() == "":
        lines.pop()
    if lines:
        last = lines[-1].strip()
        if "CONTRIBUTING.md" in last and (
                (last.startswith("*") and last.endswith("*")) or
                (last.startswith("> _") and last.endswith("_")) or
                (last.startswith("> *") and last.endswith("*"))):
            lines.pop()
            while lines and lines[-1].strip() == "":
                lines.pop()
            if lines and lines[-1].strip() == "---":
                lines.pop()
    return "\n".join(lines).rstrip("\n")


# ---- passes ------------------------------------------------------------
def set_nav(path, doc, home, cur, avail):
    old = open(path, encoding="utf-8").read()
    banner, body = strip_header(old)
    body = body.lstrip("\n")
    nav = nav_line(doc, home, cur, avail)
    if banner:
        out = banner.rstrip("\n") + "\n\n" + nav + "\n\n---\n\n" + body
    else:
        out = nav + "\n\n---\n\n" + body
    if out != old:
        open(path, "w", encoding="utf-8").write(out)
        return 1
    return 0


def clear_nav(path):
    old = open(path, encoding="utf-8").read()
    banner, body = strip_header(old)
    body = body.lstrip("\n")
    out = (banner.rstrip("\n") + "\n\n" + body) if banner else body
    if out != old:
        open(path, "w", encoding="utf-8").write(out)
        return 1
    return 0


def set_footer(path, lang):
    old = open(path, encoding="utf-8").read()
    foot = FOOTER_TMPL[lang] % contrib_link(path, lang)
    out = strip_footer(old) + "\n\n" + FOOTER_MARK + "\n\n---\n\n> " + foot + "\n"
    if out != old:
        open(path, "w", encoding="utf-8").write(out)
        return 1
    return 0


def main():
    nav = foot = 0
    for doc, home in sorted(discover().items()):
        avail = avail_langs(doc, home)
        for lang in avail:
            path = en_path(doc, home) if lang == "en" else tr_path(doc, lang)
            if len(avail) > 1:
                nav += set_nav(path, doc, home, lang, avail)
            else:
                nav += clear_nav(path)
            foot += set_footer(path, lang)
    print("gen-doc-nav: %d switcher(s), %d footer(s) updated" % (nav, foot),
          file=sys.stderr)


if __name__ == "__main__":
    main()
