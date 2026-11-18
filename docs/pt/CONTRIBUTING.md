> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · Português · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Contribuindo

Obrigado pelo seu interesse no VD Infos. Contribuições são bem-vindas: listas de detecção,
traduções, relatos de bugs, documentação e código. A maioria das contribuições não precisa de Kotlin.

## A contribuição mais fácil: as listas

A maior parte do que o app procura vive como **listas de texto puro** sob
`VDInfos/app/src/main/assets/data/`, não como código. Adicionar ou remover um alvo é editar
uma linha e abrir um pull request:

- `*_apps.txt` - um nome de pacote por linha.
- `props.txt` - o catálogo de propriedades de sistema, `CATEGORY<tab>key`.
- `spoof_keys.txt` - a matriz de settings, `key:TYPE`.
- `known_certs.txt` - o catálogo de certificados de assinatura, `SHA-256<tab>owner` (o hash
  completo, extraído de um APK real com `apksigner verify --print-certs`).
- outros arquivos `.txt` - uma entrada por linha (fragmentos de nome de módulo de kernel, nomes de arquivos
  e assim por diante).

Um `#` inicia um comentário e linhas em branco são ignoradas. Alguns arquivos compactam alguns campos separados por espaço
ou tabulação por linha, ou marcadores opcionais; mantenha o formato das linhas já
no arquivo.

## Regras básicas

- **O VD Infos lê a verdade e nunca falsifica nada.** Ele relata o que cada método
  retornou, exatamente. Não suavize uma leitura para fazê-la parecer mais bonita, e não adicione
  nada que mudaria um valor em vez de lê-lo.
- **Privacidade é absoluta.** Tudo roda no dispositivo. Nunca adicione uma chamada de rede, ou
  qualquer coisa que envie uma leitura para fora do dispositivo.
- **As exportações permanecem mascaradas.** Valores que carregam identidade são mascarados em um relatório compartilhado. Mantenha
  assim.

## Traduções

Traduções são uma contribuição valiosa. O readme é traduzido por idioma
(`README.<lang>.md`), as strings do app por idioma, e esta documentação sob
`docs/<lang>/`. Mantenha a terminologia consistente dentro de um idioma, acompanhe o tom existente
e nunca traduza por máquina um termo técnico do qual você não tem certeza.

## Relatando um bug

Abra um issue com o modelo do seu dispositivo, a versão do Android e, se for sobre uma leitura, uma
**exportação** compartilhada (seus valores que carregam identidade são mascarados). A exportação é feita exatamente
para isto.

## Código

- Compile release, nunca debug. Teste em um dispositivo real. Veja [Construindo](BUILDING.md).
- Mantenha comentários fora do código; nomes explicam a intenção. O único cabeçalho é o banner
  de licença no topo de cada arquivo-fonte.
- Acompanhe o estilo existente e mantenha as mudanças focadas.

## Licença

Ao contribuir, você concorda que sua contribuição é licenciada sob a
**GNU AGPL-3.0-or-later** do projeto.

<!-- doc-footer -->

---

> _Esta documentação foi gerada com o Claude (Anthropic) e é mantida por VD171. Correções e melhorias são bem-vindas; veja [CONTRIBUTING.md](CONTRIBUTING.md)._
