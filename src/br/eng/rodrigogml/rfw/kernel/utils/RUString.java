package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Base64;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;
import br.eng.rodrigogml.rfw.kernel.utils.extra.Base32;

/**
 * Description: Classe com métodos úteis para tratamentos e manipulação de String.<br>
 *
 * @author Rodrigo Leitão
 * @since 1.0.0 (AGO / 2007)
 * @version 4.1.0 (23/06/2011) - rodrigogml - Nome alterado de StringUtils, para ficar no padrão do sistema.
 */
public class RUString {

  /*
   * Referencia os métodos de normalização de string para cada um dos jdks, para evitar de se fazer diversas reflexões em cada vez que se usa o método de remover acentos.
   */
  private static Method normalizerJDK5 = null; // Salva o método que será usado na normalização da string no JDK5
  private static Method normalizerJDK6 = null;// Salva o método que será usado na normalização da string no JDK6
  private static Object normalizerJDK6form = null; // Salva o form necessário para o normalizer do jdk6
  private static Boolean unknownormalizer = null; // Salva se o método de normalização é desconhecido, null não procurado ainda, true desconhecido (usa modo manual), false conhecido

  /**
   * Construtor privado para classe exclusivamente estática.
   */
  private RUString() {
  }

  /**
   * Corta uma string caso ela passe do tamanho máximo definido.
   *
   * @param value Texto a ser avaliado e cortado caso necessário.
   * @param length Tamanho máximo que a String pode ter.
   * @return Valor igual ao passado caso não ultrapasse o tamanho informado, ou a string cortada para ter o tamanho máximo definido.
   */
  public static String truncate(String value, int length) {
    if (value != null) {
      if (value.length() > length) {
        value = value.substring(0, length);
      }
    }
    return value;
  }

  /**
   * Faz a mesma função que os método {@link #completeUntilLengthLeft(String, String, int)}, mas também faz a função de {@link #truncate(String, int)} caso o tamanho recebido em data já seja maior que o tamanho de length ou as concatenações geram um dado maior (caso do appendValue ser maior que 1 caracteres).
   *
   * @param appendvalue string que será adicionada a string principal
   * @param data string principal.
   * @param length tamanho a ser atingido
   * @return string principal com a string appendvalue concatenada n vezes à sua esquerda.
   */
  public static String completeOrTruncateUntilLengthLeft(String appendvalue, String data, int length) {
    return truncate(completeUntilLengthLeft(appendvalue, data, length), length);
  }

  /**
   * Faz a mesma função que os métoso {@link #completeUntilLengthRight(String, String, int)}, mas também faz a função de {@link #truncate(String, int)} caso o tamanho recebido em data já seja maior que o tamanho de length ou as concatenações geram um dado maior (caso do appendValue ser maior que 1 caracteres).
   *
   * @param appendvalue string que será adicionada a string principal
   * @param data string principal.
   * @param length tamanho a ser atingido
   * @return string principal com a string appendvalue concatenada n vezes à sua esquerda.
   */
  public static String completeOrTruncateUntilLengthRight(String appendvalue, String data, int length) {
    return truncate(completeUntilLengthRight(appendvalue, data, length), length);
  }

  /**
   * Incrementa uma string, pela esquerda, com outra passada até que o tamanho ultrapasse o valor informado.
   *
   * @param appendvalue string que será adicionada a string principal
   * @param data string principal.
   * @param length tamanho a ser atingido
   * @return string principal com a string appendvalue concatenada n vezes à sua esquerda.
   */
  public static String completeUntilLengthLeft(String appendvalue, String data, int length) {
    if (data == null) data = "";
    StringBuilder buff = new StringBuilder(length + appendvalue.length());
    int appendlimit = length - data.length();
    while (buff.length() < appendlimit) {
      buff.append(appendvalue);
    }
    buff.append(data);
    return buff.toString();
  }

  /**
   * Incrementa uma string, pela direita, com outra passada até que o tamanho ultrapasse o valor informado.
   *
   * @param appendvalue string que será adicionada a string principal
   * @param data string principal.
   * @param length tamanho a ser atingido
   * @return string principal com a string appendvalue concatenada n vezes à sua direita.
   */
  public static String completeUntilLengthRight(String appendvalue, String data, int length) {
    if (data == null) data = "";
    StringBuilder buff = new StringBuilder(length + appendvalue.length());
    buff.append(data);
    while (buff.length() < length) {
      buff.append(appendvalue);
    }
    return buff.toString();
  }

  /**
   * Este método tira todos os espaços em excesso de uma String, deixando apenas 1. Não importa se tiver 2, 3, 4 ou 1000. Ele substituirá todos por 1 único.
   *
   * @param value Texto a ser analizado e ter os espaço duplicados exterminados.
   * @return Texto com apenas 1 espaço entre as palavras.
   */
  public static String replaceDoubleSpaces(String value) {
    if (value != null) {
      value = value.replaceAll("[ ]{2,}", " ");
    }
    return value;
  }

  /**
   * Este método tira todos "TABS" (\\t) de uma String, deixando apenas 1 espaço. Não importa se tiver 2, 3, 4 ou 1000. Ele substituirá todos por 1 único.
   *
   * @param value Texto a ser analizado e ter os espaço duplicados exterminados.
   * @return Texto com apenas 1 espaço entre as palavras.
   */
  public static String replaceTabsByUniqueSpace(String value) {
    if (value != null) {
      value = value.replaceAll("[\\t]+", " ");
    }
    return value;
  }

  /**
   * Este método tira todos "Falsos Espaços conhecidos" (como o \\u00a0) de uma String, deixando apenas 1 espaço. Não importa se tiver 2, 3, 4 ou 1000. Ele substituirá todos por 1 único.
   *
   * @param value Texto a ser analizado e ter os espaço duplicados exterminados.
   * @return Texto com apenas 1 espaço entre as palavras.
   */
  public static String replaceFakeSpacesByUniqueSpace(String value) {
    if (value != null) {
      value = value.replaceAll("[\u00a0]+", " ");
    }
    return value;
  }

  /**
   * Converte o valor da enumeração em "chave" que nada mais é do que sua qualificação completa de class + enumeração + objeto da enumeração. Útil para IDs e para unificação na internacionalizações de labels.
   *
   * @param value enum desejado.
   * @return String com da chave.
   */
  public static String getEnumKey(Enum<?> value) {
    String enumkey = null;
    if (value != null) {
      enumkey = value.getDeclaringClass().getCanonicalName() + "." + value.name();
    }
    return enumkey;
  }

  /**
   * Substitui todas as ocorrencias de 'oldvalue' por 'newvalue' no texto de 'value'. No entanto este método diferencia maiúsculas, minúsculas, acentos, etc.
   *
   * @param text texto a ser manipulado
   * @param oldvalue valor a ser procurado e substituido.
   * @param newvalue valor que substiuirá oldvalue.
   * @return
   */
  public static String replaceAll(String text, String oldvalue, String newvalue) {
    return replaceAll(text, oldvalue, newvalue, Boolean.TRUE, Boolean.TRUE);
  }

  /**
   * Substitui todas as ocorrencias de 'oldvalue' por 'newvalue' no texto de 'value'.<Br>
   * De acordo com as definições passadas, ele ignora acentos e case de letras.
   *
   * @param text texto a ser manipulado
   * @param oldvalue valor a ser procurado e substituido.
   * @param newvalue valor que substiuirá oldvalue.
   * @param distinctaccents true distingue acentos, false ignora acentos
   * @param distinctcase true distingue letras maiusculas de minusculas, false ignora case das letras.
   * @return
   */
  public static String replaceAll(String text, String oldvalue, String newvalue, Boolean distinctaccents, Boolean distinctcase) {
    if (oldvalue.equals("")) {
      throw new IllegalArgumentException("Old value must have content.");
    }

    String ntext = text;
    String noldvalue = oldvalue;
    if (!distinctaccents) {
      ntext = removeAccents(ntext);
      noldvalue = removeAccents(noldvalue);
    }
    if (!distinctcase) {
      ntext = ntext.toUpperCase();
      noldvalue = noldvalue.toUpperCase();
    }

    // Com os parametros corrigos (tirados os acentos se for o caso, ou em maiusculas se for o caso) verificamos as ocorrencias
    StringBuilder buff = new StringBuilder();

    int startIdx = 0;
    int idxOld = 0;
    while ((idxOld = ntext.indexOf(noldvalue, startIdx)) >= 0) {
      // grab a part of aInput which does not include aOldPattern
      buff.append(text.substring(startIdx, idxOld));
      // add aNewPattern to take place of aOldPattern
      buff.append(newvalue);

      // reset the startIdx to just after the current match, to see
      // if there are any further matches
      startIdx = idxOld + noldvalue.length();
    }
    // the final chunk will go to the end of aInput
    buff.append(text.substring(startIdx));

    return buff.toString();
  }

  /**
   * Remove a acentuação de um texto passado. Incluindo 'ç' por 'c', maiúsculas e minúscas (preservando a captalização da letra).
   *
   * @param text String que terá seus acentos removidos.
   * @return String sem caracteres acentuados, trocados pelos seus correspondentes.
   */
  public static String removeAccents(String text) {
    // Verifica se conhece o método de normalização
    if (unknownormalizer == null) { // Se ainda não foi procurado, procura
      try {
        // Tenta Compatibilidade com JDK 6 - Evita o Import para evitar erros de compilação e execução
        // Recupera a Classe do Normalizer
        Class<?> normalizer = Class.forName("java.text.Normalizer");
        // Encontra a classe do Form
        Class<?> normalizerform = Class.forName("java.text.Normalizer$Form");
        // Encontra e enum NFD
        normalizerJDK6form = null;
        for (int i = 0; i < normalizerform.getEnumConstants().length; i++) {
          if ("NFD".equals(normalizerform.getEnumConstants()[i].toString())) {
            normalizerJDK6form = normalizerform.getEnumConstants()[i];
            break;
          }
        }
        normalizerJDK6 = normalizer.getMethod("normalize", new Class[] { CharSequence.class, normalizerform });
        unknownormalizer = Boolean.FALSE;
        return ((String) normalizerJDK6.invoke(null, new Object[] { text, normalizerJDK6form })).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
      } catch (Exception ex) {
        try {
          // Compatibilidade com JDK 5 - Evita o Import para evitar erros de compilação e execução
          Class<?> normalizerC = Class.forName("sun.text.Normalizer");
          normalizerJDK5 = normalizerC.getMethod("decompose", new Class[] { String.class, boolean.class, int.class });
          unknownormalizer = Boolean.FALSE;
          return ((String) normalizerJDK5.invoke(null, new Object[] { text, false, 0 })).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        } catch (Exception ex2) {
          // Salva como modo manual, normalizador desconhecido
          unknownormalizer = Boolean.TRUE;
          text = text.replaceAll("[áàãâä]", "a");
          text = text.replaceAll("[éèêë]", "e");
          text = text.replaceAll("[íìîï]", "i");
          text = text.replaceAll("[óòõôö]", "o");
          text = text.replaceAll("[úùûü]", "u");
          text = text.replaceAll("[ç]", "c");
          text = text.replaceAll("[ñ]", "n");
          text = text.replaceAll("[ÁÀÃÂÄ]", "A");
          text = text.replaceAll("[ÉÈÊË]", "E");
          text = text.replaceAll("[ÍÌÎÏ]", "I");
          text = text.replaceAll("[ÓÒÕÔÖ]", "O");
          text = text.replaceAll("[ÚÙÛÜ]", "U");
          text = text.replaceAll("[Ñ]", "N");
          return text;
        }
      }
    } else if (unknownormalizer) {
      text = text.replaceAll("[áàãâä]", "a");
      text = text.replaceAll("[éèêë]", "e");
      text = text.replaceAll("[íìîï]", "i");
      text = text.replaceAll("[óòõôö]", "o");
      text = text.replaceAll("[úùûü]", "u");
      text = text.replaceAll("[ç]", "c");
      text = text.replaceAll("[ñ]", "n");
      text = text.replaceAll("[ÁÀÃÂÄ]", "A");
      text = text.replaceAll("[ÉÈÊË]", "E");
      text = text.replaceAll("[ÍÌÎÏ]", "I");
      text = text.replaceAll("[ÓÒÕÔÖ]", "O");
      text = text.replaceAll("[ÚÙÛÜ]", "U");
      text = text.replaceAll("[Ñ]", "N");
      return text;
    } else if (!unknownormalizer) {
      if (normalizerJDK6 != null) {
        try {
          return ((String) normalizerJDK6.invoke(null, new Object[] { text, normalizerJDK6form })).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException("Error while normalizing string with JDK6 compatible method!");
        }
      } else if (normalizerJDK5 != null) {
        try {
          return ((String) normalizerJDK5.invoke(null, new Object[] { text, false, 0 })).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException("Error while normalizing string with JDK5 compatible method!");
        }
      }
    }
    return null;
  }

  /**
   * Substitui o texto recursivamente até que o texto não sofra mais alterações, isto é, o texto será procurado do inicio ao fim pela substituição quantas vezes for necessárias até que seja feita uma busca completa e nada seja encontrado.<br>
   * <b>ATENÇÂO:</b> Pode gerar StackOverflow facilmente se substituimos um texto por outro que contém o valor sendo procurado!<Br>
   *
   * @param text texto a ser manipulado
   * @param oldvalue valor a ser procurado e substituido.
   * @param newvalue valor que substiuirá oldvalue.
   * @return
   */
  public static String replaceAllRecursively(String text, String oldvalue, String newvalue) {
    String oldtext = text;
    text = replaceAll(text, oldvalue, newvalue);
    while (!oldtext.equals(text)) {
      oldtext = text;
      text = replaceAll(text, oldvalue, newvalue);
    }
    return text;
  }

  /**
   * Substitui o texto recursivamente até que o texto não sofra mais alterações, isto é, o texto será procurado do inicio ao fim pela substituição quantas vezes for necessárias até que seja feita uma busca completa e nada seja encontrado.<br>
   * <b>ATENÇÂO:</b> Pode gerar StackOverflow facilmente se substituimos um texto por outro que contém o valor sendo procurado!<Br>
   *
   * @param text texto a ser manipulado
   * @param oldvalue valor a ser procurado e substituido.
   * @param newvalue valor que substiuirá oldvalue.
   * @param distinctaccents true distingue acentos, false ignora acentos
   * @param distinctcase true distingue letras maiusculas de minusculas, false ignora case das letras.
   * @return
   */
  public static String replaceAllRecursively(String text, String oldvalue, String newvalue, Boolean distinctaccents, Boolean distinctcase) {
    String oldtext = text;
    text = replaceAll(text, oldvalue, newvalue, distinctaccents, distinctcase);
    while (!oldtext.equals(text)) {
      oldtext = text;
      text = replaceAll(text, oldvalue, newvalue, distinctaccents, distinctcase);
    }
    return text;
  }

  /**
   * Remove da String tudo o que não for dígitos.<br>
   * Método para remover pontuação de valores numéridos como CPF, CNPJ, Representações Numéricas de Códigos de Barras, CEP, etc.<Br>
   *
   * @param value Valor a ter os "não números" estripados
   * @return String apenas com os números/dígitos recebidos.
   * @throws RFWException
   */
  public static String removeNonDigits(String value) {
    if (value == null) return null;
    return value.replaceAll("\\D+", "");
  }

  /**
   * Cria uma String com n repetições de uma determinada cadeira de caracteres (ou caracter simples).
   *
   * @param repeats Número de repetições na String final.
   * @param base Conteúdo a ser repetido na String.
   * @return String montada conforme as definições. Com tamanho total = repeats * base.length();
   */
  public static String repeatString(int repeats, String base) {
    return new String(new char[repeats]).replaceAll("\0", base);
  }

  /**
   * Remove os caracteres inválidospara UTF-8.<br>
   * Trocando letras acentuadas por suas correspondentes sem acentos, e outros caracteres inválidos pelo caractere '?'.
   *
   * @param text Texto a ser processado.
   * @return Texto processado.
   * @throws RFWException Lançado caso ocorra alguma falha em processar o texto.
   */
  public static String removeNonUTF8(String text) throws RFWException {
    PreProcess.requiredNonNull(text);
    try {
      CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
      decoder.onMalformedInput(CodingErrorAction.REPLACE);
      decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
      CharBuffer parsed = decoder.decode(ByteBuffer.wrap(text.getBytes("UTF-8")));
      return parsed.toString();
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_000041", new String[] { text }, e);
    }
  }

  /**
   * Este método realiza o "escape" dos carateres que atrapalham o parser do do XML. Não faz escape de todos os caracteres acentuados e fora da tabela padrão, simplesmente faz escape dos seguintes caracteres que atrapalham a estrutura do XML:<br>
   * <li>< - &amp;lt;</li>
   * <li>> - &amp;gt;</li>
   * <li>& - &amp;amp;</li>
   * <li>“ - &amp;quot;</li>
   * <li>‘ - &amp;#39;</li>
   *
   * @param clientname
   * @return
   */
  public static String escapeXML(String text) {
    text = text.replaceAll("\\<", "&lt;");
    text = text.replaceAll("\\>", "&gt;");
    text = text.replaceAll("\\&", "&amp;");
    text = text.replaceAll("\\\"", "&quot;");
    text = text.replaceAll("\\'", "&#39;");
    return text;
  }

  /**
   * Método utilizado para "escapar" os caracteres especiais em HTML.<Br>
   *
   * @param text
   */
  public static String escapeHTML(String text) {
    text = text.replaceAll("\\&", "&amp;");
    text = text.replaceAll("\\¥", "&yen;");
    text = text.replaceAll("\\Ý", "&Yacute;");
    text = text.replaceAll("\\ý", "&yacute;");
    text = text.replaceAll("\\Ü", "&Uuml;");
    text = text.replaceAll("\\ü", "&uuml;");
    text = text.replaceAll("\\¨", "&uml;");
    text = text.replaceAll("\\Ù", "&Ugrave;");
    text = text.replaceAll("\\ù", "&ugrave;");
    text = text.replaceAll("\\Û", "&Ucirc;");
    text = text.replaceAll("\\û", "&ucirc;");
    text = text.replaceAll("\\Ú", "&Uacute;");
    text = text.replaceAll("\\ú", "&uacute;");
    text = text.replaceAll("\\×", "&times;");
    text = text.replaceAll("\\Þ", "&THORN;");
    text = text.replaceAll("\\þ", "&thorn;");
    text = text.replaceAll("\\ß", "&szlig;");
    text = text.replaceAll("\\³", "&sup3;");
    text = text.replaceAll("\\²", "&sup2;");
    text = text.replaceAll("\\¹", "&sup1;");
    text = text.replaceAll("\\§", "&sect;");
    text = text.replaceAll("\\®", "&reg;");
    text = text.replaceAll("\\»", "&raquo;");
    text = text.replaceAll("\\\"", "&quot;");
    text = text.replaceAll("\\£", "&pound;");
    text = text.replaceAll("\\±", "&plusmn;");
    text = text.replaceAll("\\¶", "&para;");
    text = text.replaceAll("\\Ö", "&Ouml;");
    text = text.replaceAll("\\ö", "&ouml;");
    text = text.replaceAll("\\Õ", "&Otilde;");
    text = text.replaceAll("\\õ", "&otilde;");
    text = text.replaceAll("\\Ø", "&Oslash;");
    text = text.replaceAll("\\ø", "&oslash;");
    text = text.replaceAll("\\º", "&ordm;");
    text = text.replaceAll("\\ª", "&ordf;");
    text = text.replaceAll("\\Ò", "&Ograve;");
    text = text.replaceAll("\\ò", "&ograve;");
    text = text.replaceAll("\\Ô", "&Ocirc;");
    text = text.replaceAll("\\ô", "&ocirc;");
    text = text.replaceAll("\\Ó", "&Oacute;");
    text = text.replaceAll("\\ó", "&oacute;");
    text = text.replaceAll("\\Ñ", "&Ntilde;");
    text = text.replaceAll("\\ñ", "&ntilde;");
    text = text.replaceAll("\\¬", "&not;");
    text = text.replaceAll("\\·", "&middot;");
    text = text.replaceAll("\\µ", "&micro;");
    text = text.replaceAll("\\¯", "&macr;");
    text = text.replaceAll("\\<", "&lt;");
    text = text.replaceAll("\\Ï", "&Iuml;");
    text = text.replaceAll("\\ï", "&iuml;");
    text = text.replaceAll("\\¿", "&iquest;");
    text = text.replaceAll("\\Ì", "&Igrave;");
    text = text.replaceAll("\\ì", "&igrave;");
    text = text.replaceAll("\\¡", "&iexcl;");
    text = text.replaceAll("\\Î", "&Icirc;");
    text = text.replaceAll("\\î", "&icirc;");
    text = text.replaceAll("\\Í", "&Iacute;");
    text = text.replaceAll("\\í", "&iacute;");
    text = text.replaceAll("\\>", "&gt;");
    text = text.replaceAll("\\¾", "&frac34;");
    text = text.replaceAll("\\¼", "&frac14;");
    text = text.replaceAll("\\½", "&frac12;");
    text = text.replaceAll("\\€", "&euro;");
    text = text.replaceAll("\\Ë", "&Euml;");
    text = text.replaceAll("\\ë", "&euml;");
    text = text.replaceAll("\\Ð", "&ETH;");
    text = text.replaceAll("\\ð", "&eth;");
    text = text.replaceAll("\\È", "&Egrave;");
    text = text.replaceAll("\\è", "&egrave;");
    text = text.replaceAll("\\Ê", "&Ecirc;");
    text = text.replaceAll("\\ê", "&ecirc;");
    text = text.replaceAll("\\É", "&Eacute;");
    text = text.replaceAll("\\é", "&eacute;");
    text = text.replaceAll("\\÷", "&divide;");
    text = text.replaceAll("\\°", "&deg;");
    text = text.replaceAll("\\¤", "&curren;");
    text = text.replaceAll("\\©", "&copy;");
    text = text.replaceAll("\\¢", "&cent;");
    text = text.replaceAll("\\¸", "&cedil;");
    text = text.replaceAll("\\Ç", "&Ccedil;");
    text = text.replaceAll("\\ç", "&ccedil;");
    text = text.replaceAll("\\¦", "&brvbar;");
    text = text.replaceAll("\\Ä", "&Auml;");
    text = text.replaceAll("\\ä", "&auml;");
    text = text.replaceAll("\\Ã", "&Atilde;");
    text = text.replaceAll("\\ã", "&atilde;");
    text = text.replaceAll("\\Å", "&Aring;");
    text = text.replaceAll("\\å", "&aring;");
    text = text.replaceAll("\\À", "&Agrave;");
    text = text.replaceAll("\\à", "&agrave;");
    text = text.replaceAll("\\Æ", "&AElig;");
    text = text.replaceAll("\\æ", "&aelig;");
    text = text.replaceAll("\\´", "&acute;");
    text = text.replaceAll("\\Â", "&Acirc;");
    text = text.replaceAll("\\â", "&acirc;");
    text = text.replaceAll("\\Á", "&Aacute;");
    text = text.replaceAll("\\á", "&aacute;");
    return text;
  }

  /**
   * Método utilizado para remover o "escapar" os caracteres especiais em HTML.<Br>
   *
   * @param text
   */
  public static String unescapeHTML(String text) {
    text = text.replaceAll("&yen;", "\\¥");
    text = text.replaceAll("&Yacute;", "\\Ý");
    text = text.replaceAll("&yacute;", "\\ý");
    text = text.replaceAll("&Uuml;", "\\Ü");
    text = text.replaceAll("&uuml;", "\\ü");
    text = text.replaceAll("&uml;", "\\¨");
    text = text.replaceAll("&Ugrave;", "\\Ù");
    text = text.replaceAll("&ugrave;", "\\ù");
    text = text.replaceAll("&Ucirc;", "\\Û");
    text = text.replaceAll("&ucirc;", "\\û");
    text = text.replaceAll("&Uacute;", "\\Ú");
    text = text.replaceAll("&uacute;", "\\ú");
    text = text.replaceAll("&times;", "\\×");
    text = text.replaceAll("&THORN;", "\\Þ");
    text = text.replaceAll("&thorn;", "\\þ");
    text = text.replaceAll("&szlig;", "\\ß");
    text = text.replaceAll("&sup3;", "\\³");
    text = text.replaceAll("&sup2;", "\\²");
    text = text.replaceAll("&sup1;", "\\¹");
    text = text.replaceAll("&sect;", "\\§");
    text = text.replaceAll("&reg;", "\\®");
    text = text.replaceAll("&raquo;", "\\»");
    text = text.replaceAll("\\\"", "&quot;");
    text = text.replaceAll("&pound;", "\\£");
    text = text.replaceAll("&plusmn;", "\\±");
    text = text.replaceAll("&para;", "\\¶");
    text = text.replaceAll("&Ouml;", "\\Ö");
    text = text.replaceAll("&ouml;", "\\ö");
    text = text.replaceAll("&Otilde;", "\\Õ");
    text = text.replaceAll("&otilde;", "\\õ");
    text = text.replaceAll("&Oslash;", "\\Ø");
    text = text.replaceAll("&oslash;", "\\ø");
    text = text.replaceAll("&ordm;", "\\º");
    text = text.replaceAll("&ordf;", "\\ª");
    text = text.replaceAll("&Ograve;", "\\Ò");
    text = text.replaceAll("&ograve;", "\\ò");
    text = text.replaceAll("&Ocirc;", "\\Ô");
    text = text.replaceAll("&ocirc;", "\\ô");
    text = text.replaceAll("&Oacute;", "\\Ó");
    text = text.replaceAll("&oacute;", "\\ó");
    text = text.replaceAll("&Ntilde;", "\\Ñ");
    text = text.replaceAll("&ntilde;", "\\ñ");
    text = text.replaceAll("&not;", "\\¬");
    text = text.replaceAll("&middot;", "\\·");
    text = text.replaceAll("&micro;", "\\µ");
    text = text.replaceAll("&macr;", "\\¯");
    text = text.replaceAll("&lt;", "\\<");
    text = text.replaceAll("&Iuml;", "\\Ï");
    text = text.replaceAll("&iuml;", "\\ï");
    text = text.replaceAll("&iquest;", "\\¿");
    text = text.replaceAll("&Igrave;", "\\Ì");
    text = text.replaceAll("&igrave;", "\\ì");
    text = text.replaceAll("&iexcl;", "\\¡");
    text = text.replaceAll("&Icirc;", "\\Î");
    text = text.replaceAll("&icirc;", "\\î");
    text = text.replaceAll("&Iacute;", "\\Í");
    text = text.replaceAll("&iacute;", "\\í");
    text = text.replaceAll("&gt;", "\\>");
    text = text.replaceAll("&frac34;", "\\¾");
    text = text.replaceAll("&frac14;", "\\¼");
    text = text.replaceAll("&frac12;", "\\½");
    text = text.replaceAll("&euro;", "\\€");
    text = text.replaceAll("&Euml;", "\\Ë");
    text = text.replaceAll("&euml;", "\\ë");
    text = text.replaceAll("&ETH;", "\\Ð");
    text = text.replaceAll("&eth;", "\\ð");
    text = text.replaceAll("&Egrave;", "\\È");
    text = text.replaceAll("&egrave;", "\\è");
    text = text.replaceAll("&Ecirc;", "\\Ê");
    text = text.replaceAll("&ecirc;", "\\ê");
    text = text.replaceAll("&Eacute;", "\\É");
    text = text.replaceAll("&eacute;", "\\é");
    text = text.replaceAll("&divide;", "\\÷");
    text = text.replaceAll("&deg;", "\\°");
    text = text.replaceAll("&curren;", "\\¤");
    text = text.replaceAll("&copy;", "\\©");
    text = text.replaceAll("&cent;", "\\¢");
    text = text.replaceAll("&cedil;", "\\¸");
    text = text.replaceAll("&Ccedil;", "\\Ç");
    text = text.replaceAll("&ccedil;", "\\ç");
    text = text.replaceAll("&brvbar;", "\\¦");
    text = text.replaceAll("&Auml;", "\\Ä");
    text = text.replaceAll("&auml;", "\\ä");
    text = text.replaceAll("&Atilde;", "\\Ã");
    text = text.replaceAll("&atilde;", "\\ã");
    text = text.replaceAll("&Aring;", "\\Å");
    text = text.replaceAll("&aring;", "\\å");
    text = text.replaceAll("&Agrave;", "\\À");
    text = text.replaceAll("&agrave;", "\\à");
    text = text.replaceAll("&AElig;", "\\Æ");
    text = text.replaceAll("&aelig;", "\\æ");
    text = text.replaceAll("&acute;", "\\´");
    text = text.replaceAll("&Acirc;", "\\Â");
    text = text.replaceAll("&acirc;", "\\â");
    text = text.replaceAll("&Aacute;", "\\Á");
    text = text.replaceAll("&aacute;", "\\á");
    text = text.replaceAll("&amp;", "\\&");
    return text;
  }

  /**
   * Capitaliza a primeira letra de uma string.
   *
   * @param str A string a ser capitalizada.
   * @return A string com a primeira letra em maiúscula.
   */
  public static String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  /**
   * Este método decodifica uma string codificada em base 64.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   */
  public static String decodeBase64(String encodedContent) {
    return new String(Base64.getMimeDecoder().decode(encodedContent));
  }

  /**
   * Este método decodifica uma string codificada em base 64.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   * @throws UnsupportedEncodingException
   */
  public static String decodeBase64(String encodedContent, String charset) throws RFWException {
    try {
      return new String(Base64.getMimeDecoder().decode(encodedContent), charset);
    } catch (UnsupportedEncodingException e) {
      throw new RFWCriticalException("Charset inválido: '" + charset + "'!");
    }
  }

  /**
   * Este método decodifica uma string codificada em base 64.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   */
  public static byte[] decodeBase64ToByte(String encodedContent) {
    return Base64.getMimeDecoder().decode(encodedContent);
  }

  /**
   * Este método codifica uma string em base 64.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase64(String content) {
    return new String(Base64.getMimeEncoder().encodeToString(content.getBytes()));
  }

  /**
   * Este método codifica um array de bytes em base 64.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase64(byte[] content) {
    return new String(Base64.getMimeEncoder().encodeToString(content));
  }

  /**
   * Este método codifica um array de bytes em base 32.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase32(byte[] content) {
    // Estamos usando o Google Guava (já presente no RFW por conta do Vaadin e outras bibliotecas) Outra opção seria utilizar o Apache Commons, mas este ainda não está presente no RFW. No futuro quem sabe ter a própria implementação
    // return BaseEncoding.base32().encode(content);
    return Base32.encode(content);
  }

  /**
   * Este método codifica uma String em base 32.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase32(String content) {
    // Estamos usando o Google Guava (já presente no RFW por conta do Vaadin e outras bibliotecas) Outra opção seria utilizar o Apache Commons, mas este ainda não está presente no RFW. No futuro quem sabe ter a própria implementação
    // return BaseEncoding.base32().encode(content.getBytes());
    return Base32.encode(content.getBytes());
  }

  /**
   * Este método decodifica uma String em base 32.
   *
   * @param content String para codificada.
   * @return String codificada
   */
  public static String decodeBase32(String content) {
    // Estamos usando o Google Guava (já presente no RFW por conta do Vaadin e outras bibliotecas) Outra opção seria utilizar o Apache Commons, mas este ainda não está presente no RFW. No futuro quem sabe ter a própria implementação
    // return new String(BaseEncoding.base32().decode(content));
    return new String(Base32.decode(content));
  }

  /**
   * Este método decodifica uma String em base 32.
   *
   * @param content String para codificada.
   * @return String codificada
   */
  public static byte[] decodeBase32ToByte(String content) {
    // Estamos usando o Google Guava (já presente no RFW por conta do Vaadin e outras bibliotecas) Outra opção seria utilizar o Apache Commons, mas este ainda não está presente no RFW. No futuro quem sabe ter a própria implementação
    // return BaseEncoding.base32().decode(content);
    return Base32.decode(content);
  }
}
