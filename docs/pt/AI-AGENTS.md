> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · Português · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos para agentes de IA

O VD Infos é **feito para ser lido por máquinas, não apenas por pessoas**. Um depurador de métodos
do dispositivo produz muita evidência estruturada - cada valor, lido por cada
método, alinhado para comparação - e essa evidência é muito mais útil para um agente como
dado do que como captura de tela. Por isso o resultado inteiro está disponível como um único documento
legível por máquina, e o app é projetado em torno disso desde a base.

Se você é um assistente de codificação de IA, uma ferramenta de LLM, um script de análise ou qualquer automação
que alcança o dispositivo por um shell, esta página é para você.

## Por que ele é amigável a agentes

- **Um documento, não uma tela.** A varredura inteira - cada sonda, cada lente, cada
  veredito - é um único objeto JSON. Sem OCR, sem raspagem de tela, sem tocar por
  cartões recolhidos.
- **Os mesmos dados que a interface tem.** O arquivo não é um resumo. É exatamente o que o app
  mantém internamente: a tela mostra a primeira leitura de um item recolhido, o arquivo
  tem todas elas.
- **Formato determinístico.** A estrutura é estável e documentada (veja
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), então um parser escrito uma vez continua funcionando.
- **Sem rede, sem conta, sem estado para gerenciar.** O app lê no dispositivo e escreve um
  arquivo. Seu agente lê o arquivo. Esse é o contrato inteiro.
- **Offline por design.** Ele não precisa de internet, então roda igual dentro de um ambiente
  trancado ou isolado em que um agente possa estar operando.

## A leitura com um comando

Cada varredura escreve seu relatório completo no armazenamento privado do app. Depois que uma varredura rodou,
um agente lê o resultado completo com um único comando:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **O que ele contém:** cada sonda com seu veredito e o valor de cada lente,
  no mesmo JSON do botão de exportação.
- **Quando ele atualiza:** ao fim de cada varredura. Faça uma varredura primeiro; o arquivo sempre
  guarda a mais recente.
- **Requisito:** root, porque o arquivo vive no armazenamento privado do app. Sem
  root, use a ação **compartilhar / salvar como JSON** do próprio app e leia o arquivo que o
  agente receber.

## Um fluxo típico de agente

1. Certifique-se de que o app está instalado (o APK **SDK_35** em
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Dispare uma varredura - uma pessoa toca em varrer, ou uma automação abre a tela de varredura
   do app - e espere ela terminar.
3. Leia `files/last_snapshot.json` com o comando acima.
4. Analise-o com o [formato de exportação](EXPORT-FORMAT.md) como seu schema: itere pelas
   sondas, leia o `verdict` de cada uma e alinhe os `values` de cada lente.
5. Aja sobre o resultado estruturado no seu próprio fluxo de trabalho.

O agente decide o que as leituras significam para a tarefa em questão; o VD Infos fornece as
evidências, de forma limpa e completa.

## O que o agente obtém, e o que não obtém

- O relatório carrega **cada leitura e cada veredito**, então um agente tem o quadro
  completo que o app construiu.
- Valores que carregam identidade (números de série, IMEI, endereços MAC, IDs de publicidade e afins)
  são **mascarados no relatório compartilhado/exportado**, então um relatório passado adiante permanece livre de
  identificadores pessoais enquanto mantém seus vereditos e estrutura. O `last_snapshot.json`
  **no dispositivo**, legível apenas pelo app ou pelo root, mantém os valores reais para um
  agente local que precise deles.

## Documentação, escrita para ser analisada

Este wiki é estruturado para leitura por máquina tanto quanto para pessoas: seções curtas,
cabeçalhos estáveis, nomes de campos explícitos e uma página dedicada de [formato de exportação](EXPORT-FORMAT.md)
que um agente pode tratar como um schema. Se você está construindo uma ferramenta sobre o VD Infos,
comece por lá.

## Páginas relacionadas

- [Formato de exportação](EXPORT-FORMAT.md) - o schema JSON, campo por campo.
- [Guia do usuário](USER-GUIDE.md) - instalar, varrer e compartilhar.
- [Arquitetura](ARCHITECTURE.md) - como o app é montado.

<!-- doc-footer -->

---

> _Esta documentação foi gerada com o Claude (Anthropic) e é mantida por VD171. Correções e melhorias são bem-vindas; veja [CONTRIBUTING.md](CONTRIBUTING.md)._
