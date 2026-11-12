<!--
  VD INFOS - device method debugger: read every device info by every method, then compare.
  Copyright (C) 2026  VD171 - SPDX-License-Identifier: AGPL-3.0-or-later
-->

# VD Infos

<p align="center">
  <img src="images/logo-wordmark.png" alt="VD Infos" width="320">
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases/latest"><img src="https://img.shields.io/github/v/release/VD171/VD-Infos?label=release&color=0D9488" alt="Release"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-AGPL--3.0--or--later-1a7f37" alt="License"></a>
  <a href="https://github.com/VD171/VD-Infos/releases"><img src="https://img.shields.io/github/downloads/VD171/VD-Infos/total?label=downloads&color=8250df" alt="Downloads"></a>
  <a href="https://github.com/VD171/VD-Infos/stargazers"><img src="https://img.shields.io/github/stars/VD171/VD-Infos?color=d29922" alt="Stars"></a>
</p>

<p align="center">
  <b>Debugger de métodos do aparelho.</b><br>
  Leia cada valor do aparelho por todos os métodos capazes de lê-lo e alinhe-os lado a lado para comparar.
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases">Releases</a> ·
  <a href="CATALOG.md">Catalog</a> ·
  <a href="#contributing">Contributing</a> ·
  <a href="#for-ai-agents-the-full-report-as-json">AI agents</a> ·
  <a href="CHANGELOG.md">Changelog</a> ·
  <a href="https://xdaforums.com/t/VD-Infos.4097379/">XDA thread</a>
</p>

> **Languages:** [English](README.md) · [العربية](README.ar.md) · [Čeština](README.cs.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [فارسی](README.fa.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Bahasa Indonesia](README.id.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Nederlands](README.nl.md) · [Polski](README.pl.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [Svenska](README.sv.md) · [ไทย](README.th.md) · [Türkçe](README.tr.md) · [Українська](README.uk.md) · [Tiếng Việt](README.vi.md) · [中文](README.zh.md)

<p align="center">
  <img src="images/vdinfos-01.png" height="420"/>
  <img src="images/vdinfos-02.png" height="420"/>
</p>

---

O Android é um sistema operacional poderoso e versátil; o que ninguém te conta é
que todos os seus detalhes pessoais e informações confidenciais ficam disponíveis
para cada aplicativo que você instala, e se proteger contra esse tipo de invasão de
privacidade é uma obrigação. O VD Infos te mostra um exemplo do que pode ser
capturado do seu aparelho, e faz isso como um *debugger de métodos*: para cada
informação, lê o valor por **todos os métodos capazes de lê-la** - `Build.*`,
`SystemProperties`, `getprop`, o nativo `__system_property_get`, managers do
sistema, content providers, arquivos, syscalls e attestation de chave por hardware
(TEE) - e alinha tudo pra você comparar. Quando um método discorda dos outros, é
porque algo no meio está reescrevendo aquela superfície: um framework de hook, um
spoofer, um shim de resolver. **NENHUMA INFORMAÇÃO É ARMAZENADA, ENVIADA OU
TRANSMITIDA A QUALQUER ARQUIVO¹ OU SERVIDOR** - tudo roda no aparelho, os valores de
identidade ficam mascarados até você revelar, e um relatório só sai do aparelho
quando você compartilha ou salva explicitamente; se quiser, bloqueie o acesso à
internet com um firewall ou simplesmente desligue a internet.

> **¹ Exceção:** cada varredura guarda o relatório completo no armazenamento privado do app (`files/last_snapshot.json`). Só o próprio app ou o root conseguem lê-lo, a varredura seguinte o sobrescreve e ele nunca sai do aparelho. Ele existe para que agentes de IA leiam o resultado completo; veja **Para agentes de IA: o relatório completo em JSON**.

## O que inspeciona

Cada item é lido por todos os métodos que conseguem lê-lo (SDK Java / nativo /
shell), comparados lado a lado:

* **~488 propriedades do sistema** lidas de CINCO formas: `SystemProperties.get`,
  as duas entradas da bionic (`__system_property_read_callback` e a de 92 bytes,
  `__system_property_get`), `getprop` disparado pela JVM e `getprop` disparado do
  código nativo por `popen` - um comando de shell rodado pelo `ProcessBuilder` e o
  mesmo comando rodado sem a JVM não são o mesmo ponto de vista, porque o primeiro
  é uma superfície que um framework de ocultação reescreve e o segundo não.
* **Identidade do device**: modelo, fabricante, marca, device, product, board,
  hardware, fingerprint, bootloader, build id/tags/type - cada campo `Build.*`
  contra todas as variantes `ro.product.*` (system/vendor/odm), nativo e shell.
* **Identificadores**: serial (vários getters), Android ID (settings e provider),
  GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefonia**: a superfície do TelephonyManager (operadora, SIM, rede, roaming...),
  SubscriptionManager (multi-SIM), cell info.
* **Rede**: **MAC** por interface (`NetworkInterface` vs `/sys/class/net` vs shell),
  Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Kernel / processo / sistema**: `uname`, boot id, uptime, UID/PID, SELinux,
  fuso, locale, ambiente, serviços/processos em execução.
* **Hardware / mídia**: CPU, memória, sensores, tela, câmeras, system features,
  GPU, ID DRM Widevine.
* **Pacotes / contas / WebView**: lista e contagem de pacotes instalados, digests
  de assinatura por app, assinatura deste app, auto-inspeção do próprio pacote, enumerações de resolução de intent, contas, contagem de call log, user agent.
* **Conformidade do sandbox**: superfícies que o sandbox deve FECHAR (CID/serial do
  eMMC, identidade do UFS e do SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`,
  `/system/build.prop`) e 22 serviços do `dumpsys`. Aqui a recusa é a resposta: o
  esperado é `EACCES`/`DENIED`, e um aparelho que entregar o conteúdo está fora de
  conformidade.
* **Identificadores de anúncio**: o GAID pelo serviço do Play, mais a família de
  chaves (AAID, OAID/VAID e `pps_*`) varrida nos três settings stores.
* **Detectores de root / hook / emulador**: artefatos de `su`/Magisk/KernelSU/
  APatch/Xposed/LSPosed/Riru, libs injetadas em `/proc/self/maps`, pacotes
  conhecidos, propriedades perigosas e de emulador, verified boot state.

## Download e suporte

Cada release publica dois APKs: **SDK_35** mira o sandbox moderno e estrito, e é o recomendado; **SDK_27** mira um nível de API mais antigo, com domínio SELinux mais frouxo, útil para comparação. Ambos instalam no Android 8.0 ou superior.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Para agentes de IA: o relatório completo em JSON

Feito para agentes de IA e ferramentas de LLM que trabalham no aparelho por um shell (assistentes de programação, scripts de análise). Em vez de ler prints da tela, o agente lê o resultado inteiro com um comando:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **O que contém:** cada sonda com o veredito e o valor de todas as lentes, no mesmo JSON do botão de exportar. A tela mostra só a primeira leitura de um item recolhido; o arquivo tem todas.
* **Quando atualiza:** ao fim de cada varredura, então rode uma varredura antes; o arquivo sempre traz a mais recente.
* **Requisito:** root, porque o arquivo fica no armazenamento privado do app.

## Arquitetura

```
core/model      domínio imutável: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (reflexão) - PropCatalog (dado)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out em coroutines, streaming via Flow
data/           SnapshotStore (persistência + diff) - Exporter (export JSON/texto)
ui/             Jetpack Compose, Material 3, cor dinâmica, progresso ao vivo
cpp/            native_probes.cpp - a lente nativa, sem dependências
```

* **Paralelismo**: 996 sondas em fan-out no dispatcher default com concorrência
  limitada; os resultados entram no UI conforme chegam.
* **Nada em background**: sem serviços nem varreduras agendadas; o app roda só
  enquanto está aberto e se fecha sozinho quando fica ocioso.
* **Camada nativa**: um `.so` pequeno, ligado por nome via `RegisterNatives`,
  deliberadamente minúsculo porque é a parte que precisa ser difícil de enganar.

## Contribuindo

A maioria das contribuições não exige Kotlin: as listas são texto puro em `VDInfos/app/src/main/assets/data/`. Adicione ou remova uma linha e abra um pull request.

* `*_apps.txt` - um nome de pacote por linha
* `props.txt` - o catálogo de propriedades do sistema, `CATEGORIA<tab>chave`
* `spoof_keys.txt` - a matriz de spoof de settings, `chave:TIPO`
* `known_certs.txt` - o catálogo de certificados de assinatura, `SHA-256<tab>dono` (hash completo, tirado de um APK real com `apksigner verify --print-certs`)
* outros arquivos `.txt` - também uma entrada por linha (fragmentos de nome de módulo de kernel, nomes em `/data/local/tmp`)

Um `#` inicia um comentário; linhas vazias são ignoradas. Alguns arquivos trazem alguns campos separados por espaço ou tab por linha, ou marcadores opcionais (`S` e `@field` no `props.txt`); mantenha o formato das linhas que já estão no arquivo. Contribuições de código também são bem-vindas. Ao contribuir, você concorda que seu trabalho segue a AGPL-3.0-or-later deste projeto.

## Catálogo

As referências de ocultação e detecção de root (guias, módulos, frameworks,
detectores) ficam num catálogo dedicado: [CATALOG.pt.md](CATALOG.pt.md).

## Agradecimentos

Agradecimento especial ao **[frknkrc44](https://github.com/frknkrc44)**, desenvolvedor
do **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, pelo carinho dedicado a ele.
O HMA-OSS é onde várias das correções que este app sugere
são aplicadas - esconder apps-alvo e definir presets de spoof - e toda menção a ele
dentro do app leva de volta à sua fonte.

Seus links, canais e como apoiá-lo: [HMA-OSS.md](HMA-OSS.md).

## Veja também

Projetos irmãos do mesmo autor:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: seus serviços Google (RCS, Wallet, atestações do Play Integrity), lidos no seu aparelho sem internet.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: backup e restauração completos de apps e dados como root, controlados inteiramente por uma WebUI.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: um spoofer de dispositivo que transforma um fingerprint novo num perfil coerente e consistente.

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork of @T3SL4).

## Contatos

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Channel:** @Priv8_VD https://t.me/Priv8_VD
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
* **GitHub:** @VD171 https://github.com/VD171

## Licença

**GNU AGPL-3.0-or-later** - veja [LICENSE](LICENSE). Copyleft, incluindo a cláusula
de rede: quem rodar uma versão modificada (mesmo como serviço) tem que oferecer a
fonte. Escolhida de propósito pra uma ferramenta de pesquisa/anti-detecção, pra
manter forks abertos.

## Marcas registradas

O VD Infos é uma ferramenta independente e não oficial de pesquisa e diagnóstico. Não é afiliado, endossado nem patrocinado por nenhum dos fornecedores cujo software, serviços ou marcas ele lê, detecta ou nomeia (incluindo a Google LLC e o Android Open Source Project). "Android", "Google", "Google Play", "Play Integrity", "Widevine" e todos os demais nomes de produtos, logotipos e marcas registradas são propriedade de seus respectivos donos. O VD Infos apenas lê o que já está no seu aparelho e não envia nada a lugar nenhum.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
