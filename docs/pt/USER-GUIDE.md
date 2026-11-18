> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · Português · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# Guia do usuário do VD Infos

O VD Infos é um **depurador de métodos do dispositivo**. Ele lê cada valor do dispositivo por cada
método capaz de lê-lo e alinha as leituras lado a lado para que você possa compará-las.
Este guia cobre a instalação do app, a execução de uma varredura, a leitura do resultado
e o compartilhamento de um relatório. Ele não interpreta nenhum achado específico para você: a comparação
é sua para ler, no seu próprio dispositivo.

## Sumário

1. [O que é o VD Infos](#1-o-que-é-o-vd-infos)
2. [O que ele não é](#2-o-que-ele-não-é)
3. [Requisitos](#3-requisitos)
4. [Qual APK instalar](#4-qual-apk-instalar)
5. [Instalação e primeira execução](#5-instalação-e-primeira-execução)
6. [Executando uma varredura](#6-executando-uma-varredura)
7. [Lendo o resultado](#7-lendo-o-resultado)
8. [Exporte seu relatório](#8-exporte-seu-relatório)
9. [Para agentes de IA](#9-para-agentes-de-ia)
10. [Idioma e tema](#10-idioma-e-tema)
11. [Privacidade em resumo](#11-privacidade-em-resumo)
12. [Perguntas frequentes](#12-perguntas-frequentes)

## 1. O que é o VD Infos

O Android entrega muitas informações do seu dispositivo a cada app que você instala. O VD Infos
mostra a você um exemplo do que um app comum pode ler, e faz isso como um *depurador de
métodos*: para cada informação, ele lê o valor por **cada método capaz de
lê-lo** - APIs de plataforma, propriedades do sistema, chamadas nativas,
gerenciadores de sistema, provedores de conteúdo, arquivos, syscalls e
atestação de chave de hardware (TEE) - e os alinha para que você possa comparar. É uma lente,
não uma máquina de veredito: ela traz as leituras à tona e deixa a leitura delas com você.

## 2. O que ele não é

- Ele **não** é um limpador, um acelerador ou uma ferramenta de ocultação. Ele não muda nada no seu
  sistema; apenas lê e compara.
- Ele **não** é uma nota de passou/reprovou. Ele mostra as leituras brutas; o que elas significam
  para a sua configuração é você quem julga, no seu dispositivo.
- Ele **não** precisa de internet e funciona totalmente com a internet bloqueada.

## 3. Requisitos

- Android 8.0 (API 26) ou mais recente.
- Nenhuma permissão especial é exigida para começar. Algumas leituras só ficam disponíveis se
  você conceder a permissão correspondente (por exemplo, acesso de uso), e algumas só existem
  em um dispositivo com root; onde uma leitura está indisponível, o app simplesmente mostra que ela não
  estava disponível.

## 4. Qual APK instalar

Cada lançamento publica **dois APKs**:

- **SDK_35** - visa o sandbox moderno estrito. É este que você deve usar.
- **SDK_27** - visa um nível de API mais antigo. É útil
  apenas para comparação lado a lado; a maioria das pessoas não precisa dele.

Ambos instalam no Android 8.0 e mais recente. Baixe-os na página de
[Releases](https://github.com/VD171/VD-Infos/releases).

## 5. Instalação e primeira execução

1. Baixe o APK **SDK_35** em Releases e instale-o (talvez você precise permitir
   instalações do seu navegador ou gerenciador de arquivos).
2. Abra o app. Ele abre direto na tela de varredura; não há conta, não há
   login e não há configuração.
3. Opcionalmente, conceda uma permissão que o app peça (como acesso de uso) para desbloquear
   as leituras que dependem dela. Isso é totalmente opcional.

## 6. Executando uma varredura

Toque na ação de varredura. O app dispara suas sondas em paralelo e transmite os resultados para
a lista conforme chegam, com um indicador de progresso ao vivo. Uma varredura termina por conta própria; ela
lê apenas enquanto o app está aberto, não mantém nada rodando em segundo plano e
se fecha quando deixada ociosa.

Para executá-la de novo, faça outra varredura: cada varredura substitui o resultado anterior.

## 7. Lendo o resultado

Cada item é um cartão que alinha as leituras de cada lente (o método Java SDK,
o método nativo, o shell e assim por diante). A cor do cartão diz a você, num relance,
como essas leituras se relacionam entre si:

- **Coincidência** - cada lente concordou. Os métodos retornaram o mesmo valor.
- **Divergência** - as lentes não concordaram. Um método retornou algo diferente
  dos outros.
- **Informação** - uma leitura mostrada para referência, não votada.
- **Única / indisponível** - apenas uma lente conseguiu lê-la, ou nenhuma conseguiu.

O cartão sempre mostra a matriz completa de leituras, para que você veja exatamente qual lente
disse o quê. Um cartão recolhido mostra sua primeira leitura; expanda-o para ver todas.

É até aí que o app fala por você. **O que uma dada divergência significa no seu
dispositivo, e o que fazer a respeito, é você quem deve investigar** - esse é o ponto inteiro
de um depurador: ele coloca as evidências diante de você e deixa você lê-las. Use a caixa
de busca para filtrar por propriedade ou valor, e os chips de filtro para estreitar a lista.

## 8. Exporte seu relatório

As ações de compartilhar e salvar entregam a você o **resultado inteiro**, não apenas o que está na tela:

- **Compartilhar / salvar como JSON** - o relatório completo em forma legível por máquina, os mesmos
  dados que o app mantém internamente. É o formato para guardar ou para alimentar uma ferramenta.
- **Compartilhar como texto** - um resumo compacto e legível por humanos das divergências.

Valores que carregam identidade (números de série, IMEI, endereços MAC, IDs de publicidade e afins)
são **mascarados no relatório exportado** para que um relatório que você cole em uma thread pública
não vaze seus próprios identificadores; os vereditos e a estrutura são preservados.
A cópia no dispositivo, que só você (ou o root) pode ler, mantém os valores reais.

## 9. Para agentes de IA

O VD Infos é feito para ser lido por ferramentas, não apenas por pessoas. Cada varredura escreve seu
relatório completo no armazenamento privado do app, e a exportação é o mesmo JSON legível por máquina.
Um assistente de codificação de IA ou um script de análise rodando no dispositivo pode ler o
resultado inteiro com um comando em vez de analisar capturas de tela. Veja o
[guia para agentes de IA](AI-AGENTS.md) dedicado e a referência do [formato de exportação](EXPORT-FORMAT.md).

## 10. Idioma e tema

O app segue o idioma do seu sistema onde existe uma tradução, e você pode escolher um
manualmente. Ele usa Material 3 com cor dinâmica e segue sua configuração claro/escuro.

## 11. Privacidade em resumo

Tudo roda no dispositivo. Nenhuma leitura é enviada a lugar nenhum, e um relatório sai do dispositivo
apenas quando você compartilha ou salva explicitamente. Cada varredura mantém seu relatório completo no
armazenamento privado do app (`files/last_snapshot.json`) para que ferramentas possam lê-lo; só o próprio app
ou o root pode ler esse arquivo, a próxima varredura o sobrescreve e ele nunca sai do dispositivo.
Veja [Privacidade](PRIVACY.md) para a declaração completa.

## 12. Perguntas frequentes

**Ele muda alguma coisa no meu dispositivo?** Não. Ele apenas lê e compara.

**Ele precisa de root?** Não. O app roda sem root. Um punhado de leituras só existe
em um dispositivo com root, e o arquivo de relatório privado só pode ser lido de volta pelo root ou pelo
próprio app; todo o resto funciona sem root.

**Ele precisa de internet?** Não. Ele funciona totalmente offline; bloqueie seu acesso à rede se
quiser.

**Qual APK eu devo usar?** SDK_35. A build SDK_27 existe apenas para comparação.

**Por que alguns itens dizem indisponível?** Porque aquele método não conseguiu ler aquele valor no
seu dispositivo - uma permissão ausente, uma regra de sandbox ou hardware que não está presente.
Isso em si faz parte da comparação.

**Onde eu reporto um problema ou sugiro um alvo de detecção?** Veja
[Contribuindo](CONTRIBUTING.md); a maioria das listas de detecção é texto puro e não precisa de código.

<!-- doc-footer -->

---

> _Esta documentação foi gerada com o Claude (Anthropic) e é mantida por VD171. Correções e melhorias são bem-vindas; veja [CONTRIBUTING.md](CONTRIBUTING.md)._
