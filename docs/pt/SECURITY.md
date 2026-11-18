> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · Português · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Política de segurança

O VD Infos é uma ferramenta de privacidade que lê estado sensível do dispositivo, então relatos de segurança são
levados a sério.

## Relatando uma vulnerabilidade

Por favor, relate problemas de segurança **em privado**, não em um issue público:

- E-mail **vd.priv8@pm.me**.
- Descreva o problema, a versão afetada e como reproduzi-lo.
- Se for sobre exposição de dados, descreva a classe do problema; você não precisa anexar
  um exploit funcional.

Você receberá uma resposta o mais rápido possível. Por favor, dê uma janela razoável para corrigir o
problema antes de qualquer divulgação pública.

## O que conta como um problema de segurança

Por causa do que o VD Infos promete, estes são especialmente importantes:

- Qualquer coisa que faria o app alcançar a rede ou enviar qualquer leitura para fora do
  dispositivo.
- Qualquer coisa que vazaria um valor que carrega identidade para uma **exportação** compartilhada ou salva
  (um número de série, IMEI, endereço MAC, ID de publicidade e afins) apesar do mascaramento.
- Qualquer coisa que permita a outro app ler o arquivo de relatório privado que deve ser
  legível apenas pelo próprio app ou pelo root.

## O que não é um problema de segurança

- Uma leitura que parece "errada" mas é o valor honesto que um método retornou. O VD Infos
  relata o que cada método disse; ler a comparação é tarefa sua. Veja o
  [guia do usuário](USER-GUIDE.md).
- Uma leitura marcada como indisponível. Isso significa que um método não conseguiu ler aquele valor no seu
  dispositivo, o que em si faz parte da comparação.

## Versões suportadas

Apenas a versão lançada mais recente é suportada. Por favor, reproduza na build mais recente
antes de relatar.

<!-- doc-footer -->

---

> _Esta documentação foi gerada com o Claude (Anthropic) e é mantida por VD171. Correções e melhorias são bem-vindas; veja [CONTRIBUTING.md](CONTRIBUTING.md)._
