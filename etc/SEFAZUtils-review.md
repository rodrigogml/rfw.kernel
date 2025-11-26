# Análise de métodos da `SEFAZUtils`

Este arquivo resume quais métodos da classe apresentada podem ser incorporados às utilitárias `RU*` do projeto e quais já possuem equivalentes existentes.

## Métodos passíveis de migração para `RU*`

- **Conversão decimal com validação**: `convertBigDecimalToJava(String)` e sua variação com escala repetem a lógica de parse seguro de `BigDecimal` que já está em `RUTypes.parseBigDecimal`. A funcionalidade poderia ser consolidada em `RUTypes` (incluindo a opção de `setScale`) para evitar duplicidade de validação e arredondamento. 【F:src/br/eng/rodrigogml/rfw/kernel/utils/RUTypes.java†L1207-L1252】
- **Serialização/desserialização JAXB**: `readXMLToObject` e `writeXMLFromObject` são sobrecargas específicas de marshal/unmarshal que já existem de forma genérica em `RUSerializer.serializeToXML` e `RUSerializer.desserializeFromXML`. Ajustes como remoção do fragmento `<xml-fragment>` ou controle de `JAXB_FRAGMENT` poderiam ser incorporados como opções em `RUSerializer` em vez de manter implementações paralelas. 【F:src/br/eng/rodrigogml/rfw/kernel/utils/RUSerializer.java†L142-L178】
- **Regex de extração de atributo**: `extractNFeProcVersion` apenas lê o atributo `versao` de uma tag. Essa utilidade genérica poderia virar um helper em `RUXML` (por exemplo, método de extração via regex simples) para reutilização em outros fluxos que precisem ler atributos sem parse completo.
- **Criação de `JAXBElement` genérico**: `auxCreateJAXBElement` não depende de NFe e pode ser movido para `RUXML` ou `RUSerializer` como factory utilitário para encapsular objetos arbitrários com namespace padrão do portal fiscal.

## Métodos já cobertos por utilitários existentes

- **Parser de datas**: `parseDate` da `SEFAZUtils` apenas delega para `RUTypes.parseDate`, que já cobre os formatos de data aceitos e converte para `Date` com suporte a timezone. Duplicar o método na `SEFAZUtils` não traz valor adicional. 【F:src/br/eng/rodrigogml/rfw/kernel/utils/RUTypes.java†L311-L358】
- **Conversão segura para `BigDecimal`**: a lógica de validação/parse já está centralizada em `RUTypes.parseBigDecimal`, tornando redundante manter a versão `convertBigDecimalToJava` separada. 【F:src/br/eng/rodrigogml/rfw/kernel/utils/RUTypes.java†L1207-L1252】
- **Marshal/unmarshal JAXB**: `RUSerializer` já oferece operações genéricas de serialização/desserialização XML via JAXB, cobrindo o mesmo objetivo de `readXMLToObject`/`writeXMLFromObject` da classe externa. 【F:src/br/eng/rodrigogml/rfw/kernel/utils/RUSerializer.java†L142-L178】

## Observações adicionais

- `signNfeAutorizacaoLoteV400Message` utiliza `RUXML.signXmlDocument`, que já encapsula a assinatura digital genérica de tags. A lógica adicional (remoção de namespace `ns2` e acentos) é específica do layout NFe; se for reaproveitada, poderia virar um adaptador específico, mantendo a assinatura centralizada em `RUXML`. 【F:src/br/eng/rodrigogml/rfw/kernel/utils/RUXML.java†L600-L665】
- Métodos de montagem de mensagens (`mount*`) e geração de QR Code são altamente acoplados ao domínio SEFAZ/NFCe e não se enquadram nos utilitários genéricos `RU*` sem perder o foco de domínio.
