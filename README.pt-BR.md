[English](README.md) | **Português**

# VD Infos

Tópico no XDA: https://xdaforums.com/t/app-vd-infos-package-com-vitaodoidao-vdinfos.4097379/

<img src="docs/vdinfos-01.png" height="420"/> <img src="docs/vdinfos-02.png" height="420"/>

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
TRANSMITIDA A QUALQUER ARQUIVO OU SERVIDOR** - tudo roda no aparelho, os valores de
identidade ficam mascarados até você revelar, e um relatório só sai do aparelho
quando você compartilha ou salva explicitamente; se quiser, bloqueie o acesso à
internet com um firewall ou simplesmente desligue a internet.

## O que inspeciona

Cada item é lido por todos os métodos que conseguem lê-lo (SDK Java / nativo /
shell), comparados lado a lado:

* **~445 propriedades do sistema** lidas de CINCO formas: `SystemProperties.get`,
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

## Línguas

Português Brasileiro e Inglês.

## Catálogo

As referências de ocultação e detecção de root (guias, módulos, frameworks,
detectores) ficam num catálogo dedicado: [CATALOG.pt-BR.md](CATALOG.pt-BR.md).

## Arquitetura

```
core/model      domínio imutável: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (reflexão) - PropCatalog (dado)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out em coroutines, streaming via Flow
data/           SnapshotStore (persistência + diff) - Exporter (export JSON/texto)
work/           SnapshotWorker - varredura periódica em background + notificação
ui/             Jetpack Compose, Material 3, cor dinâmica, progresso ao vivo
cpp/            native_probes.cpp - a lente nativa, sem dependências
```

* **Paralelismo**: 777 sondas em fan-out no dispatcher default com concorrência
  limitada; os resultados entram no UI conforme chegam.
* **Ação em background**: um job do WorkManager re-varre no cronograma, faz diff do
  último snapshot e notifica quando um valor muda.
* **Camada nativa**: um `.so` pequeno, ligado por nome via `RegisterNatives`,
  deliberadamente minúsculo porque é a parte que precisa ser difícil de enganar.

## Contatos

* **Telegram:** VD_Priv8 https://t.me/VD_Priv8
* **E-mail:** vd.priv8 @ pm.me
* **XDA-Developers:** VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** VD171 https://github.com/VD171

## Download e suporte

* Release (APK): https://github.com/VD171/VD-Infos/releases/tag/v2.15
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Licença

**GNU AGPL-3.0-or-later** - veja [LICENSE](LICENSE). Copyleft, incluindo a cláusula
de rede: quem rodar uma versão modificada (mesmo como serviço) tem que oferecer a
fonte. Escolhida de propósito pra uma ferramenta de pesquisa/anti-detecção, pra
manter forks abertos.
