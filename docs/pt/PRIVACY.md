> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · Português · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Privacidade

Privacidade não é um recurso do VD Infos. É o ponto inteiro. Esta página declara exatamente
o que o app faz e o que não faz com seus dados, em termos simples que você pode verificar.

## A promessa

- **Tudo roda no dispositivo.** Nenhuma leitura é enviada, carregada ou transmitida a nenhum
  servidor. O app funciona totalmente com a internet bloqueada.
- **Sem conta, sem login, sem trabalho em segundo plano.** O app lê apenas enquanto está aberto,
  não mantém nada rodando em segundo plano e se fecha quando deixado ocioso.
- **Nada muda no seu sistema.** O VD Infos apenas lê e compara; ele nunca escreve
  no estado do seu dispositivo.

## O relatório fica com você

Um relatório sai do dispositivo **apenas quando você compartilha ou salva explicitamente**. Até lá, ele
existe em dois lugares, ambos no dispositivo:

- **Na tela**, enquanto o app está aberto.
- No **armazenamento privado** do app (`files/last_snapshot.json`), que só o próprio app
  ou o root pode ler. A próxima varredura o sobrescreve, e ele nunca sai do dispositivo.
  Ele existe para que ferramentas e agentes de IA possam ler o resultado completo; veja o
  [guia para agentes de IA](AI-AGENTS.md).

## As exportações são mascaradas

Quando você compartilha ou salva um relatório, os valores que carregam identidade - números de série, IMEI, endereços
MAC, IDs de publicidade e afins - são **mascarados** antes de o relatório ser escrito,
para que um relatório que você cole em uma thread pública não vaze seus próprios identificadores. Os
vereditos e a estrutura são preservados; apenas os valores sensíveis são redigidos. A
cópia privada no dispositivo mantém os valores reais para seu próprio uso.

## Permissões

O app começa sem nenhuma permissão exigida. Algumas leituras só ficam disponíveis se você
conceder a permissão correspondente (por exemplo, acesso de uso), e concedê-la é totalmente
sua escolha; onde uma leitura está indisponível, o app simplesmente diz isso.

## Verifique você mesmo

Você não precisa aceitar nada disto por confiança:

- O código-fonte é público: <https://github.com/VD171/VD-Infos>.
- O manifesto está no código-fonte; confirme quais permissões ele declara.
- Bloqueie o acesso à rede do app com um firewall, ou desligue a internet, e ele continua
  funcionando igual.

Veja o [guia do usuário](USER-GUIDE.md) para saber como ler um relatório e o que a exportação
contém.

<!-- doc-footer -->

---

> _Esta documentação foi gerada com o Claude (Anthropic) e é mantida por VD171. Correções e melhorias são bem-vindas; veja [CONTRIBUTING.md](CONTRIBUTING.md)._
