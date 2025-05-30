package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;
import br.eng.rodrigogml.rfw.kernel.utils.extra.Base32;

/**
 * Description: Classe com m�todos �teis para tratamentos e manipula��o de String.<br>
 *
 * @author Rodrigo Leit�o
 * @since 1.0.0 (AGO / 2007)
 * @version 4.1.0 (23/06/2011) - rodrigogml - Nome alterado de StringUtils, para ficar no padr�o do sistema.
 */
public class RUString {

  /*
   * Referencia os m�todos de normaliza��o de string para cada um dos jdks, para evitar de se fazer diversas reflex�es em cada vez que se usa o m�todo de remover acentos.
   */
  private static Method normalizerJDK5 = null; // Salva o m�todo que ser� usado na normaliza��o da string no JDK5
  private static Method normalizerJDK6 = null;// Salva o m�todo que ser� usado na normaliza��o da string no JDK6
  private static Object normalizerJDK6form = null; // Salva o form necess�rio para o normalizer do jdk6
  private static Boolean unknownormalizer = null; // Salva se o m�todo de normaliza��o � desconhecido, null n�o procurado ainda, true desconhecido (usa modo manual), false conhecido

  /**
   * Construtor privado para classe exclusivamente est�tica.
   */
  private RUString() {
  }

  /**
   * Corta uma string caso ela passe do tamanho m�ximo definido.
   *
   * @param value Texto a ser avaliado e cortado caso necess�rio.
   * @param length Tamanho m�ximo que a String pode ter.
   * @return Valor igual ao passado caso n�o ultrapasse o tamanho informado, ou a string cortada para ter o tamanho m�ximo definido.
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
   * Faz a mesma fun��o que os m�todo {@link #completeUntilLengthLeft(String, String, int)}, mas tamb�m faz a fun��o de {@link #truncate(String, int)} caso o tamanho recebido em data j� seja maior que o tamanho de length ou as concatena��es geram um dado maior (caso do appendValue ser maior que 1 caracteres).
   *
   * @param appendvalue string que ser� adicionada a string principal
   * @param data string principal.
   * @param length tamanho a ser atingido
   * @return string principal com a string appendvalue concatenada n vezes � sua esquerda.
   */
  public static String completeOrTruncateUntilLengthLeft(String appendvalue, String data, int length) {
    return truncate(completeUntilLengthLeft(appendvalue, data, length), length);
  }

  /**
   * Faz a mesma fun��o que os m�toso {@link #completeUntilLengthRight(String, String, int)}, mas tamb�m faz a fun��o de {@link #truncate(String, int)} caso o tamanho recebido em data j� seja maior que o tamanho de length ou as concatena��es geram um dado maior (caso do appendValue ser maior que 1 caracteres).
   *
   * @param appendvalue string que ser� adicionada a string principal
   * @param data string principal.
   * @param length tamanho a ser atingido
   * @return string principal com a string appendvalue concatenada n vezes � sua esquerda.
   */
  public static String completeOrTruncateUntilLengthRight(String appendvalue, String data, int length) {
    return truncate(completeUntilLengthRight(appendvalue, data, length), length);
  }

  /**
   * Incrementa uma string, pela esquerda, com outra passada at� que o tamanho ultrapasse o valor informado.
   *
   * @param appendvalue string que ser� adicionada a string principal
   * @param data string principal.
   * @param length tamanho a ser atingido
   * @return string principal com a string appendvalue concatenada n vezes � sua esquerda.
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
   * Incrementa uma string, pela direita, com outra passada at� que o tamanho ultrapasse o valor informado.
   *
   * @param appendvalue string que ser� adicionada a string principal
   * @param data string principal.
   * @param length tamanho a ser atingido
   * @return string principal com a string appendvalue concatenada n vezes � sua direita.
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
   * Este m�todo tira todos os espa�os em excesso de uma String, deixando apenas 1. N�o importa se tiver 2, 3, 4 ou 1000. Ele substituir� todos por 1 �nico.
   *
   * @param value Texto a ser analizado e ter os espa�o duplicados exterminados.
   * @return Texto com apenas 1 espa�o entre as palavras.
   */
  public static String replaceDoubleSpaces(String value) {
    if (value != null) {
      value = value.replaceAll("[ ]{2,}", " ");
    }
    return value;
  }

  /**
   * Este m�todo tira todos "TABS" (\\t) de uma String, deixando apenas 1 espa�o. N�o importa se tiver 2, 3, 4 ou 1000. Ele substituir� todos por 1 �nico.
   *
   * @param value Texto a ser analizado e ter os espa�o duplicados exterminados.
   * @return Texto com apenas 1 espa�o entre as palavras.
   */
  public static String replaceTabsByUniqueSpace(String value) {
    if (value != null) {
      value = value.replaceAll("[\\t]+", " ");
    }
    return value;
  }

  /**
   * Este m�todo tira todos "Falsos Espa�os conhecidos" (como o \\u00a0) de uma String, deixando apenas 1 espa�o. N�o importa se tiver 2, 3, 4 ou 1000. Ele substituir� todos por 1 �nico.
   *
   * @param value Texto a ser analizado e ter os espa�o duplicados exterminados.
   * @return Texto com apenas 1 espa�o entre as palavras.
   */
  public static String replaceFakeSpacesByUniqueSpace(String value) {
    if (value != null) {
      value = value.replaceAll("[\u00a0]+", " ");
    }
    return value;
  }

  /**
   * Converte o valor da enumera��o em "chave" que nada mais � do que sua qualifica��o completa de class + enumera��o + objeto da enumera��o. �til para IDs e para unifica��o na internacionaliza��es de labels.
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
   * Substitui todas as ocorr�ncias de {@code oldValue} por {@code newValue} no texto de {@code text}.
   * <p>
   * De acordo com as defini��es passadas, pode ignorar acentos e diferencia��o entre mai�sculas e min�sculas.
   * </p>
   *
   * @param text Texto a ser manipulado.
   * @param oldValue Valor a ser procurado e substitu�do.
   * @param newValue Valor que substituir� {@code oldValue}.
   * @param distinctAccents {@code true} diferencia acentos, {@code false} ignora acentos.
   * @param distinctCase {@code true} diferencia mai�sculas de min�sculas, {@code false} ignora diferencia��o de case.
   * @return Texto com as substitui��es realizadas.
   * @throws IllegalArgumentException Se {@code oldValue} for uma string vazia.
   */
  public static String replaceAll(String text, String oldValue, String newValue, boolean distinctAccents, boolean distinctCase) {
    if (oldValue.isEmpty()) throw new IllegalArgumentException("Old value must have content.");

    String normalizedText = text;
    String normalizedOldValue = oldValue;

    if (!distinctAccents) {
      normalizedText = removeAccents(normalizedText);
      normalizedOldValue = removeAccents(normalizedOldValue);
    }
    if (!distinctCase) {
      normalizedText = normalizedText.toUpperCase();
      normalizedOldValue = normalizedOldValue.toUpperCase();
    }

    StringBuilder result = new StringBuilder();
    int startIdx = 0, idxOld;

    while ((idxOld = normalizedText.indexOf(normalizedOldValue, startIdx)) >= 0) {
      result.append(text, startIdx, idxOld).append(newValue);
      startIdx = idxOld + oldValue.length();
    }
    result.append(text.substring(startIdx));

    return result.toString();
  }

  /**
   * Remove a acentua��o de um texto passado. Incluindo '�' por 'c', mai�sculas e min�scas (preservando a captaliza��o da letra).
   *
   * @param text String que ter� seus acentos removidos.
   * @return String sem caracteres acentuados, trocados pelos seus correspondentes.
   */
  public static String removeAccents(String text) {
    // Verifica se conhece o m�todo de normaliza��o
    if (unknownormalizer == null) { // Se ainda n�o foi procurado, procura
      try {
        // Tenta Compatibilidade com JDK 6 - Evita o Import para evitar erros de compila��o e execu��o
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
          // Compatibilidade com JDK 5 - Evita o Import para evitar erros de compila��o e execu��o
          Class<?> normalizerC = Class.forName("sun.text.Normalizer");
          normalizerJDK5 = normalizerC.getMethod("decompose", new Class[] { String.class, boolean.class, int.class });
          unknownormalizer = Boolean.FALSE;
          return ((String) normalizerJDK5.invoke(null, new Object[] { text, false, 0 })).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        } catch (Exception ex2) {
          // Salva como modo manual, normalizador desconhecido
          unknownormalizer = Boolean.TRUE;
          text = text.replaceAll("[�����]", "a");
          text = text.replaceAll("[����]", "e");
          text = text.replaceAll("[����]", "i");
          text = text.replaceAll("[�����]", "o");
          text = text.replaceAll("[����]", "u");
          text = text.replaceAll("[�]", "c");
          text = text.replaceAll("[�]", "n");
          text = text.replaceAll("[�����]", "A");
          text = text.replaceAll("[����]", "E");
          text = text.replaceAll("[����]", "I");
          text = text.replaceAll("[�����]", "O");
          text = text.replaceAll("[����]", "U");
          text = text.replaceAll("[�]", "N");
          return text;
        }
      }
    } else if (unknownormalizer) {
      text = text.replaceAll("[�����]", "a");
      text = text.replaceAll("[����]", "e");
      text = text.replaceAll("[����]", "i");
      text = text.replaceAll("[�����]", "o");
      text = text.replaceAll("[����]", "u");
      text = text.replaceAll("[�]", "c");
      text = text.replaceAll("[�]", "n");
      text = text.replaceAll("[�����]", "A");
      text = text.replaceAll("[����]", "E");
      text = text.replaceAll("[����]", "I");
      text = text.replaceAll("[�����]", "O");
      text = text.replaceAll("[����]", "U");
      text = text.replaceAll("[�]", "N");
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
   * Remove da String tudo o que n�o for d�gitos.<br>
   * M�todo para remover pontua��o de valores num�ridos como CPF, CNPJ, Representa��es Num�ricas de C�digos de Barras, CEP, etc.<Br>
   *
   * @param value Valor a ter os "n�o n�meros" estripados
   * @return String apenas com os n�meros/d�gitos recebidos.
   * @throws RFWException
   */
  public static String removeNonDigits(String value) {
    if (value == null) return null;
    return value.replaceAll("\\D+", "");
  }

  /**
   * Cria uma String com n repeti��es de uma determinada cadeira de caracteres (ou caracter simples).
   *
   * @param repeats N�mero de repeti��es na String final.
   * @param base Conte�do a ser repetido na String.
   * @return String montada conforme as defini��es. Com tamanho total = repeats * base.length();
   */
  public static String repeatString(int repeats, String base) {
    return new String(new char[repeats]).replaceAll("\0", base);
  }

  /**
   * Remove os caracteres inv�lidospara UTF-8.<br>
   * Trocando letras acentuadas por suas correspondentes sem acentos, e outros caracteres inv�lidos pelo caractere '?'.
   *
   * @param text Texto a ser processado.
   * @return Texto processado.
   * @throws RFWException Lan�ado caso ocorra alguma falha em processar o texto.
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
   * Este m�todo realiza o "escape" dos carateres que atrapalham o parser do do XML. N�o faz escape de todos os caracteres acentuados e fora da tabela padr�o, simplesmente faz escape dos seguintes caracteres que atrapalham a estrutura do XML:<br>
   * <li>< - &amp;lt;</li>
   * <li>> - &amp;gt;</li>
   * <li>& - &amp;amp;</li>
   * <li>� - &amp;quot;</li>
   * <li>� - &amp;#39;</li>
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
   * M�todo utilizado para "escapar" os caracteres especiais em HTML.<Br>
   *
   * @param text
   */
  public static String escapeHTML(String text) {
    text = text.replaceAll("\\&", "&amp;");
    text = text.replaceAll("\\�", "&yen;");
    text = text.replaceAll("\\�", "&Yacute;");
    text = text.replaceAll("\\�", "&yacute;");
    text = text.replaceAll("\\�", "&Uuml;");
    text = text.replaceAll("\\�", "&uuml;");
    text = text.replaceAll("\\�", "&uml;");
    text = text.replaceAll("\\�", "&Ugrave;");
    text = text.replaceAll("\\�", "&ugrave;");
    text = text.replaceAll("\\�", "&Ucirc;");
    text = text.replaceAll("\\�", "&ucirc;");
    text = text.replaceAll("\\�", "&Uacute;");
    text = text.replaceAll("\\�", "&uacute;");
    text = text.replaceAll("\\�", "&times;");
    text = text.replaceAll("\\�", "&THORN;");
    text = text.replaceAll("\\�", "&thorn;");
    text = text.replaceAll("\\�", "&szlig;");
    text = text.replaceAll("\\�", "&sup3;");
    text = text.replaceAll("\\�", "&sup2;");
    text = text.replaceAll("\\�", "&sup1;");
    text = text.replaceAll("\\�", "&sect;");
    text = text.replaceAll("\\�", "&reg;");
    text = text.replaceAll("\\�", "&raquo;");
    text = text.replaceAll("\\\"", "&quot;");
    text = text.replaceAll("\\�", "&pound;");
    text = text.replaceAll("\\�", "&plusmn;");
    text = text.replaceAll("\\�", "&para;");
    text = text.replaceAll("\\�", "&Ouml;");
    text = text.replaceAll("\\�", "&ouml;");
    text = text.replaceAll("\\�", "&Otilde;");
    text = text.replaceAll("\\�", "&otilde;");
    text = text.replaceAll("\\�", "&Oslash;");
    text = text.replaceAll("\\�", "&oslash;");
    text = text.replaceAll("\\�", "&ordm;");
    text = text.replaceAll("\\�", "&ordf;");
    text = text.replaceAll("\\�", "&Ograve;");
    text = text.replaceAll("\\�", "&ograve;");
    text = text.replaceAll("\\�", "&Ocirc;");
    text = text.replaceAll("\\�", "&ocirc;");
    text = text.replaceAll("\\�", "&Oacute;");
    text = text.replaceAll("\\�", "&oacute;");
    text = text.replaceAll("\\�", "&Ntilde;");
    text = text.replaceAll("\\�", "&ntilde;");
    text = text.replaceAll("\\�", "&not;");
    text = text.replaceAll("\\�", "&middot;");
    text = text.replaceAll("\\�", "&micro;");
    text = text.replaceAll("\\�", "&macr;");
    text = text.replaceAll("\\<", "&lt;");
    text = text.replaceAll("\\�", "&Iuml;");
    text = text.replaceAll("\\�", "&iuml;");
    text = text.replaceAll("\\�", "&iquest;");
    text = text.replaceAll("\\�", "&Igrave;");
    text = text.replaceAll("\\�", "&igrave;");
    text = text.replaceAll("\\�", "&iexcl;");
    text = text.replaceAll("\\�", "&Icirc;");
    text = text.replaceAll("\\�", "&icirc;");
    text = text.replaceAll("\\�", "&Iacute;");
    text = text.replaceAll("\\�", "&iacute;");
    text = text.replaceAll("\\>", "&gt;");
    text = text.replaceAll("\\�", "&frac34;");
    text = text.replaceAll("\\�", "&frac14;");
    text = text.replaceAll("\\�", "&frac12;");
    text = text.replaceAll("\\�", "&euro;");
    text = text.replaceAll("\\�", "&Euml;");
    text = text.replaceAll("\\�", "&euml;");
    text = text.replaceAll("\\�", "&ETH;");
    text = text.replaceAll("\\�", "&eth;");
    text = text.replaceAll("\\�", "&Egrave;");
    text = text.replaceAll("\\�", "&egrave;");
    text = text.replaceAll("\\�", "&Ecirc;");
    text = text.replaceAll("\\�", "&ecirc;");
    text = text.replaceAll("\\�", "&Eacute;");
    text = text.replaceAll("\\�", "&eacute;");
    text = text.replaceAll("\\�", "&divide;");
    text = text.replaceAll("\\�", "&deg;");
    text = text.replaceAll("\\�", "&curren;");
    text = text.replaceAll("\\�", "&copy;");
    text = text.replaceAll("\\�", "&cent;");
    text = text.replaceAll("\\�", "&cedil;");
    text = text.replaceAll("\\�", "&Ccedil;");
    text = text.replaceAll("\\�", "&ccedil;");
    text = text.replaceAll("\\�", "&brvbar;");
    text = text.replaceAll("\\�", "&Auml;");
    text = text.replaceAll("\\�", "&auml;");
    text = text.replaceAll("\\�", "&Atilde;");
    text = text.replaceAll("\\�", "&atilde;");
    text = text.replaceAll("\\�", "&Aring;");
    text = text.replaceAll("\\�", "&aring;");
    text = text.replaceAll("\\�", "&Agrave;");
    text = text.replaceAll("\\�", "&agrave;");
    text = text.replaceAll("\\�", "&AElig;");
    text = text.replaceAll("\\�", "&aelig;");
    text = text.replaceAll("\\�", "&acute;");
    text = text.replaceAll("\\�", "&Acirc;");
    text = text.replaceAll("\\�", "&acirc;");
    text = text.replaceAll("\\�", "&Aacute;");
    text = text.replaceAll("\\�", "&aacute;");
    return text;
  }

  /**
   * M�todo utilizado para remover o "escapar" os caracteres especiais em HTML.<Br>
   *
   * @param text
   */
  public static String unescapeHTML(String text) {
    text = text.replaceAll("&yen;", "\\�");
    text = text.replaceAll("&Yacute;", "\\�");
    text = text.replaceAll("&yacute;", "\\�");
    text = text.replaceAll("&Uuml;", "\\�");
    text = text.replaceAll("&uuml;", "\\�");
    text = text.replaceAll("&uml;", "\\�");
    text = text.replaceAll("&Ugrave;", "\\�");
    text = text.replaceAll("&ugrave;", "\\�");
    text = text.replaceAll("&Ucirc;", "\\�");
    text = text.replaceAll("&ucirc;", "\\�");
    text = text.replaceAll("&Uacute;", "\\�");
    text = text.replaceAll("&uacute;", "\\�");
    text = text.replaceAll("&times;", "\\�");
    text = text.replaceAll("&THORN;", "\\�");
    text = text.replaceAll("&thorn;", "\\�");
    text = text.replaceAll("&szlig;", "\\�");
    text = text.replaceAll("&sup3;", "\\�");
    text = text.replaceAll("&sup2;", "\\�");
    text = text.replaceAll("&sup1;", "\\�");
    text = text.replaceAll("&sect;", "\\�");
    text = text.replaceAll("&reg;", "\\�");
    text = text.replaceAll("&raquo;", "\\�");
    text = text.replaceAll("\\\"", "&quot;");
    text = text.replaceAll("&pound;", "\\�");
    text = text.replaceAll("&plusmn;", "\\�");
    text = text.replaceAll("&para;", "\\�");
    text = text.replaceAll("&Ouml;", "\\�");
    text = text.replaceAll("&ouml;", "\\�");
    text = text.replaceAll("&Otilde;", "\\�");
    text = text.replaceAll("&otilde;", "\\�");
    text = text.replaceAll("&Oslash;", "\\�");
    text = text.replaceAll("&oslash;", "\\�");
    text = text.replaceAll("&ordm;", "\\�");
    text = text.replaceAll("&ordf;", "\\�");
    text = text.replaceAll("&Ograve;", "\\�");
    text = text.replaceAll("&ograve;", "\\�");
    text = text.replaceAll("&Ocirc;", "\\�");
    text = text.replaceAll("&ocirc;", "\\�");
    text = text.replaceAll("&Oacute;", "\\�");
    text = text.replaceAll("&oacute;", "\\�");
    text = text.replaceAll("&Ntilde;", "\\�");
    text = text.replaceAll("&ntilde;", "\\�");
    text = text.replaceAll("&not;", "\\�");
    text = text.replaceAll("&middot;", "\\�");
    text = text.replaceAll("&micro;", "\\�");
    text = text.replaceAll("&macr;", "\\�");
    text = text.replaceAll("&lt;", "\\<");
    text = text.replaceAll("&Iuml;", "\\�");
    text = text.replaceAll("&iuml;", "\\�");
    text = text.replaceAll("&iquest;", "\\�");
    text = text.replaceAll("&Igrave;", "\\�");
    text = text.replaceAll("&igrave;", "\\�");
    text = text.replaceAll("&iexcl;", "\\�");
    text = text.replaceAll("&Icirc;", "\\�");
    text = text.replaceAll("&icirc;", "\\�");
    text = text.replaceAll("&Iacute;", "\\�");
    text = text.replaceAll("&iacute;", "\\�");
    text = text.replaceAll("&gt;", "\\>");
    text = text.replaceAll("&frac34;", "\\�");
    text = text.replaceAll("&frac14;", "\\�");
    text = text.replaceAll("&frac12;", "\\�");
    text = text.replaceAll("&euro;", "\\�");
    text = text.replaceAll("&Euml;", "\\�");
    text = text.replaceAll("&euml;", "\\�");
    text = text.replaceAll("&ETH;", "\\�");
    text = text.replaceAll("&eth;", "\\�");
    text = text.replaceAll("&Egrave;", "\\�");
    text = text.replaceAll("&egrave;", "\\�");
    text = text.replaceAll("&Ecirc;", "\\�");
    text = text.replaceAll("&ecirc;", "\\�");
    text = text.replaceAll("&Eacute;", "\\�");
    text = text.replaceAll("&eacute;", "\\�");
    text = text.replaceAll("&divide;", "\\�");
    text = text.replaceAll("&deg;", "\\�");
    text = text.replaceAll("&curren;", "\\�");
    text = text.replaceAll("&copy;", "\\�");
    text = text.replaceAll("&cent;", "\\�");
    text = text.replaceAll("&cedil;", "\\�");
    text = text.replaceAll("&Ccedil;", "\\�");
    text = text.replaceAll("&ccedil;", "\\�");
    text = text.replaceAll("&brvbar;", "\\�");
    text = text.replaceAll("&Auml;", "\\�");
    text = text.replaceAll("&auml;", "\\�");
    text = text.replaceAll("&Atilde;", "\\�");
    text = text.replaceAll("&atilde;", "\\�");
    text = text.replaceAll("&Aring;", "\\�");
    text = text.replaceAll("&aring;", "\\�");
    text = text.replaceAll("&Agrave;", "\\�");
    text = text.replaceAll("&agrave;", "\\�");
    text = text.replaceAll("&AElig;", "\\�");
    text = text.replaceAll("&aelig;", "\\�");
    text = text.replaceAll("&acute;", "\\�");
    text = text.replaceAll("&Acirc;", "\\�");
    text = text.replaceAll("&acirc;", "\\�");
    text = text.replaceAll("&Aacute;", "\\�");
    text = text.replaceAll("&aacute;", "\\�");
    text = text.replaceAll("&amp;", "\\&");
    return text;
  }

  /**
   * Capitaliza a primeira letra de uma string.
   *
   * @param str A string a ser capitalizada.
   * @return A string com a primeira letra em mai�scula.
   */
  public static String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  /**
   * Este m�todo decodifica uma string codificada em base 64.<br>
   * A diferen�a do m�todo 'mime' � que ele quebra a linha a cada 76 caracteres (comp�tivel com e-mails), enquanto que o m�todo padr�o n�o considera as quebras de linha.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   */
  public static String decodeMimeBase64(String encodedContent) {
    return new String(Base64.getMimeDecoder().decode(encodedContent));
  }

  /**
   * Este m�todo decodifica uma string codificada em base 64.<br>
   * A diferen�a do m�todo 'mime' � que ele quebra a linha a cada 76 caracteres (comp�tivel com e-mails), enquanto que o m�todo padr�o n�o considera as quebras de linha.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   * @throws UnsupportedEncodingException
   */
  public static String decodeMimeBase64(String encodedContent, String charset) throws RFWException {
    try {
      return new String(Base64.getMimeDecoder().decode(encodedContent), charset);
    } catch (UnsupportedEncodingException e) {
      throw new RFWCriticalException("Charset inv�lido: '" + charset + "'!");
    }
  }

  /**
   * Este m�todo decodifica uma string codificada em base 64.<br>
   * A diferen�a do m�todo 'mime' � que ele quebra a linha a cada 76 caracteres (comp�tivel com e-mails), enquanto que o m�todo padr�o n�o considera as quebras de linha.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   */
  public static byte[] decodeMimeBase64ToByte(String encodedContent) {
    return Base64.getMimeDecoder().decode(encodedContent);
  }

  /**
   * Este m�todo codifica uma string em base 64.<br>
   * A diferen�a do m�todo 'mime' � que ele quebra a linha a cada 76 caracteres (comp�tivel com e-mails), enquanto que o m�todo padr�o n�o considera as quebras de linha.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeMimeBase64(String content) {
    return new String(Base64.getMimeEncoder().encodeToString(content.getBytes()));
  }

  /**
   * Este m�todo codifica um array de bytes em base 64.<br>
   * A diferen�a do m�todo 'mime' � que ele quebra a linha a cada 76 caracteres (comp�tivel com e-mails), enquanto que o m�todo padr�o n�o considera as quebras de linha.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeMimeBase64(byte[] content) {
    return new String(Base64.getMimeEncoder().encodeToString(content));
  }

  /**
   * Este m�todo decodifica uma string codificada em base 64.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   */
  public static String decodeBase64(String encodedContent) {
    return new String(Base64.getDecoder().decode(encodedContent));
  }

  /**
   * Este m�todo decodifica uma string codificada em base 64.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   * @throws UnsupportedEncodingException
   */
  public static String decodeBase64(String encodedContent, String charset) throws RFWException {
    try {
      return new String(Base64.getDecoder().decode(encodedContent), charset);
    } catch (UnsupportedEncodingException e) {
      throw new RFWCriticalException("Charset inv�lido: '" + charset + "'!");
    }
  }

  /**
   * Este m�todo decodifica uma string codificada em base 64.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   */
  public static byte[] decodeBase64ToByte(String encodedContent) {
    return Base64.getDecoder().decode(encodedContent);
  }

  /**
   * Este m�todo codifica uma string em base 64.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase64(String content) {
    return Base64.getEncoder().encodeToString(content.getBytes());
  }

  /**
   * Este m�todo codifica um array de bytes em base 64.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase64(byte[] content) {
    return Base64.getEncoder().encodeToString(content);
  }

  /**
   * Este m�todo codifica um array de bytes em base 32.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase32(byte[] content) {
    // Estamos usando o Google Guava (j� presente no RFW por conta do Vaadin e outras bibliotecas) Outra op��o seria utilizar o Apache Commons, mas este ainda n�o est� presente no RFW. No futuro quem sabe ter a pr�pria implementa��o
    // return BaseEncoding.base32().encode(content);
    return Base32.encode(content);
  }

  /**
   * Este m�todo codifica uma String em base 32.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase32(String content) {
    // Estamos usando o Google Guava (j� presente no RFW por conta do Vaadin e outras bibliotecas) Outra op��o seria utilizar o Apache Commons, mas este ainda n�o est� presente no RFW. No futuro quem sabe ter a pr�pria implementa��o
    // return BaseEncoding.base32().encode(content.getBytes());
    return Base32.encode(content.getBytes());
  }

  /**
   * Este m�todo decodifica uma String em base 32.
   *
   * @param content String para codificada.
   * @return String codificada
   */
  public static String decodeBase32(String content) {
    // Estamos usando o Google Guava (j� presente no RFW por conta do Vaadin e outras bibliotecas) Outra op��o seria utilizar o Apache Commons, mas este ainda n�o est� presente no RFW. No futuro quem sabe ter a pr�pria implementa��o
    // return new String(BaseEncoding.base32().decode(content));
    return new String(Base32.decode(content));
  }

  /**
   * Este m�todo decodifica uma String em base 32.
   *
   * @param content String para codificada.
   * @return String codificada
   */
  public static byte[] decodeBase32ToByte(String content) {
    // Estamos usando o Google Guava (j� presente no RFW por conta do Vaadin e outras bibliotecas) Outra op��o seria utilizar o Apache Commons, mas este ainda n�o est� presente no RFW. No futuro quem sabe ter a pr�pria implementa��o
    // return BaseEncoding.base32().decode(content);
    return Base32.decode(content);
  }

  /**
   * M�todo utilizado para converter um byte array de base 64 em uma String para Hexadecimal.<br>
   * Este m�todo � utilizado por exemplo para receber o bytearray do campo DigestValue do XML da NFe/NFCe (cuja base � 64), e converte para uma representa��o Hexadecimal. Essa representa��o Hexa � utilizada na gera��o da URL no QRCode da NFCe.
   *
   * @param bytearray
   * @return String com o valor em HexaDecimal com as letras em lowercase.
   */
  public static String toHexFromBase64(byte[] bytearray) throws RFWException {
    return toHex(Base64.getEncoder().encodeToString(bytearray));
  }

  /**
   * M�todo utilizado extrair o byte array de base 64 a partir de uma string que represente um valor em hexa.<br>
   * Faz o procedimento contr�rio ao {@link #toHexFromBase64(byte[])}<br>
   *
   * @param bytearray
   * @param hexstring
   * @return String com o valor em HexaDecimal com as letras em lowercase.
   */
  public static byte[] fromHexToByteArrayBase64(String hexstring) throws RFWException {
    return Base64.getDecoder().decode(fromHexToByteArray(hexstring));
  }

  /**
   * M�todo utilizado para converter uma String para Hexadecimal.<br>
   * Este m�todo utiliza o CharSet Padr�o do ambiente.
   *
   * @param value Valor a ser convertido
   * @return String com o valor em HexaDecimal com as letras em lowercase.
   */
  public static String toHex(String value) throws RFWException {
    return toHex(value.getBytes(/* YOUR_CHARSET? */));
  }

  /**
   * M�todo utilizado para converter uma String para Hexadecimal.<br>
   * Este m�todo permite identificar o charset usado para decodificar a String.
   *
   * @param value Valor a ser convertido
   * @param charset Charset para decodifica��o da String
   * @return String com o valor em HexaDecimal com as letras em lowercase.
   */
  public static String toHex(String value, Charset charset) throws RFWException {
    return toHex(value.getBytes(charset));
  }

  /**
   * M�todo utilizado para converter um array de bytes para Hexadecimal.<br>
   *
   * @param bytes cadeia de bytes a ser convertido para uma String representando o valor Hexadecimal
   * @return String com o valor em HexaDecimal com as letras em lowercase.
   */
  public static String toHex(byte[] bytes) throws RFWException {
    return String.format("%040x", new BigInteger(1, bytes));
  }

  /**
   * Este m�todo recebe uma string representando valores em hexa e retorna os valores em um array de bytes.
   *
   * @param hexstring String representando um valor hexa
   * @return array de bytes com os mesmos valores representados em hexa na string.
   * @throws RFWException
   */
  public static byte[] fromHexToByteArray(String hexstring) throws RFWException {
    // Valida a String recebida se s� tem caracteres em Hexa
    if (!hexstring.matches("[0-9A-Fa-f]*")) {
      throw new RFWValidationException("BISERP_000362");
    }
    return new BigInteger(hexstring, 16).toByteArray();
  }

  /**
   * M�todo utilizado para converter uma string de valor hexadecimal para uma String. Utiliza os bytes dos valores hexa decimal para converter em String utilizando o charset padr�o do sistema.
   *
   * @param hexstring String com valores em hexa
   * @return String montada usando os bytes do valor hexa com o charset padr�o do sistema.
   * @throws RFWException
   */
  public static String fromHexToString(String hexstring) throws RFWException {
    // Valida a String recebida se s� tem caracteres em Hexa
    if (!hexstring.matches("[0-9A-Fa-f]*")) {
      throw new RFWValidationException("BISERP_000362");
    }
    return new String(new BigInteger(hexstring, 16).toByteArray());
  }

  /**
   * Calcula a Hash SHA1 de uma String.
   *
   * @param value Valor a ter a Hash calculada.
   * @return Valor em Hexa calculado com o algor�timo de SHA1.
   * @throws RFWException
   */
  public static String calcSHA1(String value) throws RFWException {
    try {
      MessageDigest cript = MessageDigest.getInstance("SHA-1");
      cript.reset();
      cript.update(value.getBytes());
      return toHex(cript.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new RFWCriticalException("BISERP_000307", e);
    }
  }

  /**
   * Calcula a Hash SHA1 de uma String.
   *
   * @param value Valor a ter a Hash calculada.
   * @param charset Defineo charset do valor, usado para converter corretamente em bytes.
   * @return Valor em Hexa calculado com o algor�timo de SHA1.
   * @throws RFWException
   */
  public static String calcSHA1(String value, String charset) throws RFWException {
    try {
      MessageDigest cript = MessageDigest.getInstance("SHA-1");
      cript.reset();
      cript.update(value.getBytes(charset));
      return toHex(cript.digest());
    } catch (Exception e) {
      throw new RFWCriticalException("BISERP_000307", e);
    }
  }

  /**
   * Substitui o texto recursivamente at� que o texto n�o sofra mais altera��es.
   * <p>
   * O texto ser� processado do in�cio ao fim quantas vezes forem necess�rias at� que nenhuma substitui��o ocorra.
   * </p>
   * <p>
   * <b>ATEN��O:</b> Pode causar um loop infinito e gerar {@code StackOverflowError} se {@code newValue} contiver {@code oldValue}!
   * </p>
   *
   * @param text Texto a ser manipulado.
   * @param oldValue Valor a ser procurado e substitu�do.
   * @param newValue Valor que substituir� {@code oldValue}.
   * @param distinctAccents {@code true} diferencia acentos, {@code false} ignora acentos.
   * @param distinctCase {@code true} diferencia mai�sculas de min�sculas, {@code false} ignora diferencia��o de case.
   * @return Texto processado com todas as substitui��es aplicadas recursivamente.
   * @throws RFWException
   * @throws StackOverflowError Se a substitui��o entrar em um ciclo infinito.
   */
  public static String replaceAllRecursively(String text, String oldValue, String newValue, boolean distinctAccents, boolean distinctCase) throws RFWException {
    if (oldValue.isEmpty()) throw new IllegalArgumentException("Old value must have content.");
    if (newValue.indexOf(oldValue) > -1) throw new RFWCriticalException("O valor substituto: '${0}' inclui o valor a ser substitu�do: '${1}' isso resulta resulta em substitui��es infinitas!", new String[] { newValue, oldValue }); // Evita loop infinito

    String previousText;
    do {
      previousText = text;
      text = replaceAll(text, oldValue, newValue, distinctAccents, distinctCase);
    } while (!previousText.equals(text));

    return text;
  }

  /**
   * Substitui todas as ocorrencias de 'oldvalue' por 'newvalue' no texto de 'value'. No entanto este m�todo diferencia mai�sculas, min�sculas, acentos, etc.
   *
   * @param text texto a ser manipulado
   * @param oldValue Valor a ser procurado e substitu�do.
   * @param newValue Valor que substituir� {@code oldValue}.
   * @return
   */
  public static String replaceAll(String text, String oldValue, String newValue) {
    return replaceAll(text, oldValue, newValue, Boolean.TRUE, Boolean.TRUE);
  }

  /**
   * Substitui o texto recursivamente at� que o texto n�o sofra mais altera��es, isto �, o texto ser� procurado do inicio ao fim pela substitui��o quantas vezes for necess�rias at� que seja feita uma busca completa e nada seja encontrado.<br>
   * <b>ATEN��O:</b> Pode gerar StackOverflow facilmente se substituimos um texto por outro que cont�m o valor sendo procurado!<Br>
   *
   * @param text texto a ser manipulado
   * @param oldValue Valor a ser procurado e substitu�do.
   * @param newValue Valor que substituir� {@code oldValue}.
   * @return
   */
  public static String replaceAllRecursively(String text, String oldValue, String newValue) {
    String oldtext = text;
    text = replaceAll(text, oldValue, newValue);
    while (!oldtext.equals(text)) {
      oldtext = text;
      text = replaceAll(text, oldValue, newValue);
    }
    return text;
  }

  /**
   * Coloca espa�os no come�o do texto para dar a sensa��o de centraliza��o de um texto com n�mero de colunas certo. �til quando trabalhamos com fontes de largura fixa e queremos deixar o texto centralizado em um espa�o, como em impress�es matriciais (por colunas) ou janelas de terminais.
   *
   * @param text Texto para ser centralizado
   * @param columns N�mero de colunas totais para calcular o "offset" inicial
   * @return Texto recebido com espa�os em brancos no come�o equivalente a metade do espa�o restante entre o n�mero de colunas passado e o tamanho do texto.
   */
  public static String centerTextInColumns(String text, int columns) {
    return completeUntilLengthLeft(" ", text, text.length() + (columns - text.length()) / 2);
  }

  /**
   * Este m�todo recebe um valor string e quebra em linhas com o tamanho m�ximo definido. Este m�todo quebrar� as linhas somente nos espa�os em branco entre as palavras, n�o quebra as palavras no meio.
   *
   * @param content Conte�do a ser quebrado em linhas
   * @param maxlength tamanho m�ximo de cada linha.
   * @return Array de String com todas as linhas criadas.
   */
  public static String[] breakLineInBlankSpaces(String content, int maxlength) {
    final LinkedList<String> lines = new LinkedList<>();

    String[] blines = content.split("\\ ");
    final StringBuilder b = new StringBuilder(maxlength);
    for (int i = 0; i < blines.length; i++) {
      // Verifica se ainda cabe na mesmoa linha
      if (b.length() + blines[i].length() + 1 <= maxlength) { // O +1 refere-se ao espa�o que ser� adicionado entre o conte�do do buffer e a nova palavra
        b.append(" ").append(blines[i]);
      } else {
        lines.add(b.toString());
        b.delete(0, b.length());
        b.append(blines[i]);
      }
    }
    // Ao acabar, verificamose se temos conte�do no buff e passamos e acrescentamos � lista, caso contr�rio perdemos a �ltima linha
    if (b.length() > 0) lines.add(b.toString());
    String[] a = new String[lines.size()];
    return lines.toArray(a);
  }

  /**
   * Escreve um valor por extenso. Apesar de aceitar um BigDecimal por causa do tamanho dos n�meros, os valores fracion�rios ser�o simplesmente ignorados.
   *
   * @param value Valor a ser transformado por extenso.
   * @return String com o valor por extenso em Portugu�s Brasileiro.
   */
  public static Object valueToExtense_BrazilianPortuguese(BigDecimal value) {
    final StringBuilder buff = new StringBuilder();
    final BigDecimal BIGTHOUSAND = new BigDecimal("1000");

    // Garante que os decimais ser�o ignorados
    value = value.setScale(0, RoundingMode.FLOOR);

    // Se o valor � zero j� retorna logo, n�o sai tentando calcular e esrever para n�o escrever errado. Esse � o �nico n�mero em que "zero" � escrito
    if (value.compareTo(BigDecimal.ZERO) == 0) {
      return "zero";
    }

    // Quebra o valor em cada milhar para ir compondo o valor
    int pow = 0;
    while (value.compareTo(BigDecimal.ZERO) > 0) {
      long hundreds = value.remainder(BIGTHOUSAND).longValue();
      value = value.divide(BIGTHOUSAND, 0, RoundingMode.FLOOR);

      if (hundreds > 0) {
        // Decop�e o n�mero em unidades, dezens e centenas para criar o texto
        int uvalue = (int) (hundreds % 10);
        int dvalue = (int) ((hundreds / 10f) % 10);
        int cvalue = (int) ((hundreds / 100f) % 10);

        String ctext = null;
        String dtext = null;
        String utext = null;

        if (hundreds == 100) {
          ctext = "cem";
        } else {
          if (cvalue == 1) {
            if (dvalue > 0 || uvalue > 0) {
              ctext = "cento";
            } else {
              ctext = "cem";
            }
          } else if (cvalue == 2) {
            ctext = "duzentos";
          } else if (cvalue == 3) {
            ctext = "trezentos";
          } else if (cvalue == 4) {
            ctext = "quatrocentos";
          } else if (cvalue == 5) {
            ctext = "quinhentos";
          } else if (cvalue == 6) {
            ctext = "seiscentos";
          } else if (cvalue == 7) {
            ctext = "setecentos";
          } else if (cvalue == 8) {
            ctext = "oitocentos";
          } else if (cvalue == 9) {
            ctext = "novecentos";
          }

          // Verifica o texto das dezenas
          if (dvalue == 1) {
            if (uvalue == 0) {
              dtext = "dez";
            } else if (uvalue == 1) {
              dtext = "onze";
            } else if (uvalue == 2) {
              dtext = "doze";
            } else if (uvalue == 3) {
              dtext = "treze";
            } else if (uvalue == 4) {
              dtext = "quatorze";
            } else if (uvalue == 5) {
              dtext = "quinze";
            } else if (uvalue == 6) {
              dtext = "dezesseis";
            } else if (uvalue == 7) {
              dtext = "dezessete";
            } else if (uvalue == 8) {
              dtext = "dezoito";
            } else if (uvalue == 9) {
              dtext = "dezenove";
            }
          } else {
            // Se n�o tem nome espec�fico para o conjunto dezena e unidade, separamos em dezena e unidade
            if (dvalue == 2) {
              dtext = "vinte";
            } else if (dvalue == 3) {
              dtext = "trinta";
            } else if (dvalue == 4) {
              dtext = "quarenta";
            } else if (dvalue == 5) {
              dtext = "cinquenta";
            } else if (dvalue == 6) {
              dtext = "sessenta";
            } else if (dvalue == 7) {
              dtext = "setenta";
            } else if (dvalue == 8) {
              dtext = "oitenta";
            } else if (dvalue == 9) {
              dtext = "noventa";
            }
            // Texto das unidades
            if (uvalue == 1) {
              utext = "um";
            } else if (uvalue == 2) {
              utext = "dois";
            } else if (uvalue == 3) {
              utext = "tr�s";
            } else if (uvalue == 4) {
              utext = "quatro";
            } else if (uvalue == 5) {
              utext = "cinco";
            } else if (uvalue == 6) {
              utext = "seis";
            } else if (uvalue == 7) {
              utext = "sete";
            } else if (uvalue == 8) {
              utext = "oito";
            } else if (uvalue == 9) {
              utext = "nove";
            }
          }
        }

        String text = ctext;
        if (dtext != null) {
          if (text != null) {
            text = text + " e " + dtext;
          } else {
            text = dtext;
          }
        }
        if (utext != null) {
          if (text != null) {
            text = text + " e " + utext;
          } else {
            text = utext;
          }
        }

        // Depois que o n�mero est� pronto, verificamos em que casa de milhar estamos para anexar o valor
        switch (pow) {
          case 0:
            // N�o h� nada, s� o n�mero mesmo
            break;
          case 1:
            text += " mil";
            break;
          case 2:
            text += (hundreds == 1 ? " milh�o" : " milh�es");
            break;
          case 3:
            text += (hundreds == 1 ? " bilh�o" : " bilh�es");
            break;
          case 4:
            text += (hundreds == 1 ? " trilh�o" : " trilh�es");
            break;
          case 5:
            text += (hundreds == 1 ? " quatrilh�o" : " quatrilh�es");
            break;
          case 6:
            text += (hundreds == 1 ? " quintilh�o" : " quintilh�es");
            break;
          case 7:
            text += (hundreds == 1 ? " sextilh�o" : " sextilh�es");
            break;
          case 8:
            text += (hundreds == 1 ? " setilh�o" : " setilh�es");
            break;
          case 9:
            text += (hundreds == 1 ? " octilh�o" : " octilh�es");
            break;
          case 10:
            text += (hundreds == 1 ? " nonilh�o" : " nonilh�es");
            break;
          default:
            break;
        }
        if (buff.length() > 0) buff.insert(0, "e ");
        buff.insert(0, text + ' ');
      }
      pow++;
    }
    return buff.toString().trim();
  }

  /**
   * Recebe um valor em BigDecimal e o escreve por extenso. Se passado algum valor com mais de 2 casas decimais o valor ser� arredondado.
   *
   * @param value Valor a ser transformado por extenso.
   * @return String com o texto do valor por extenso em Reais, escrito em Portugu�s brasileiro.
   */
  public static String currencyToExtense_BrazilianReal_BrazilianPortuguese(BigDecimal value) {
    // Garante que teremos apenas duas casas decimais
    value = value.setScale(2, RFW.getRoundingMode());

    final StringBuilder buff = new StringBuilder();

    // Separa a parte inteira e os centavos do valor
    BigDecimal integer = value.setScale(0, RoundingMode.FLOOR);
    BigDecimal fraction = value.remainder(BigDecimal.ONE);

    // Recuperar o extenso da parte inteira
    buff.append(valueToExtense_BrazilianPortuguese(integer));
    // Anexa a moeda
    if (integer.compareTo(BigDecimal.ONE) == 0) {
      buff.append(" Real");
    } else {
      buff.append(" Reais");
    }

    // S� processa os centavos se ele existir, se estiver zerado n�o escreve "zero centavos"
    if (fraction.signum() != 0) {
      // Recupera o extendo da parte dos centavos
      buff.append(" e ").append(valueToExtense_BrazilianPortuguese(fraction.movePointRight(2)));
      // Anexa a palavra centavos se existir
      if (fraction.compareTo(BigDecimal.ONE) == 0) {
        buff.append(" centavo");
      } else {
        buff.append(" centavos");
      }
    }

    return buff.toString();
  }

  /**
   * Remove todos os caracteres que n�o comp�e os primeiros 128 caracteres da tabela UTF-8 pelo texto passado.
   *
   * @param text Texto a ser tratado.
   * @return Texto sem os caracteres fora dos primeiros caracteres da tabela UTF-8.
   * @throws RFWException
   */
  public static String removeNonUTF8BaseCaracters(String text) throws RFWException {
    return replaceNonUTF8BaseCaracters(text, "");
  }

  /**
   * Substitui todos os caracteres que n�o comp�e os primeiros 128 caracteres da tabela UTF-8 pelo texto passado.
   *
   * @param text Texto a ser tratado.
   * @param replacement Valor que substituir� os caracteres removidos
   * @return Texto tratado.
   * @throws RFWException
   */
  public static String replaceNonUTF8BaseCaracters(String text, String replacement) throws RFWException {
    return text.replaceAll("[^\\u0000-\\u007E]", replacement);
  }

  /**
   * Escreve uma String de tr�s para frente.
   *
   * @param content - Conte�do para ser invertido.
   * @return String invertida
   */
  public static String invert(String content) {
    return new StringBuilder(content).reverse().toString();
  }

  /**
   * Remove zeros � esquerda no in�cio da String.
   * <p>
   * Casos de uso:
   * <ul>
   * <li>null -> null</li>
   * <li>"0001234" -> "1234"</li>
   * <li>"00012340000" -> "12340000"</li>
   * <li>" 00012340000" -> " 00012340000" (Mant�m espa�os iniciais)</li>
   * <li>"000000000000000000" -> "" (Retorna String vazia e n�o null)</li>
   * </ul>
   *
   * @param input Texto a ser processado.
   * @return A mesma String sem zeros iniciais ou String vazia se for composta apenas por zeros.
   */
  public static String removeLeadingZeros(String input) {
    if (input == null) return null;
    int length = input.length();
    int i = 0;
    while (i < length && input.charAt(i) == '0') {
      i++;
    }
    return input.substring(i);
  }

  /**
   * Recupera a parte direita de uma string.
   *
   * @param value String original
   * @param length Tamanho da parte desejada.
   * @return Retorna uma string contendo a parte direita da string original com o tamanho especificado. Se o tamanho solicitado for maior ou igual ao da string original, retorna a pr�pria string original. Se a string original for nula, retorna null. Se o tamanho solicitado for menor ou igual a zero, retorna uma string vazia.
   */
  public static String right(String value, int length) {
    if (value == null) {
      return null;
    }
    int strLength = value.length();
    return length >= strLength ? value : (length <= 0 ? "" : value.substring(strLength - length));
  }

  /**
   * Recupera a parte esquerda de uma string.
   *
   * @param value String original
   * @param length Tamanho da parte desejada.
   * @return Retorna uma string contendo a parte esquerda da string original com o tamanho especificado. Se o tamanho solicitado for maior ou igual ao da string original, retorna a pr�pria string original. Se a string original for nula, retorna null. Se o tamanho solicitado for menor ou igual a zero, retorna uma string vazia.
   */
  public static String left(String value, int length) {
    if (value == null) {
      return null;
    }
    return length >= value.length() ? value : (length <= 0 ? "" : value.substring(0, length));
  }

  /**
   * Separa os campos de uma linha de arquivo CSV, considerando aspas e caracteres de escape.
   *
   * @param line linha do arquivo a ser processada.
   * @return Array de strings contendo os valores separados.
   * @throws RFWException em caso de erro no processamento da linha.
   */
  public static String[] parseCSVLine(String line) throws RFWException {
    return parseCSVLine(line, ',', '"');
  }

  /**
   * Separa os campos de uma linha de arquivo CSV, considerando aspas e caracteres de escape.
   *
   * O separador define o caractere delimitador entre os campos e as aspas determinam quais trechos devem ser tratados como um �nico valor, mesmo contendo o separador interno.
   *
   * Regras:
   * <li>Valores entre aspas ignoram separadores internos.
   * <li>Aspas duplas dentro de um campo s�o representadas por aspas duplas consecutivas.
   * <li>Caracteres de quebra de linha (\r ou \n) s�o mantidos e n�o s�o considerados como outro registro, pois este m�todo j� espera receber uma �nica linha do CSV.
   *
   * @param line linha do arquivo a ser processada.
   * @param separator caractere delimitador entre os campos (exemplo: ',', '|', '\t').
   * @param quote aspas utilizadas para envolver valores com separadores internos.
   * @return Array de strings contendo os valores separados.
   * @throws RFWException em caso de erro no processamento da linha.
   */
  public static String[] parseCSVLine(String line, char separator, char quote) throws RFWException {
    if (line == null || line.isEmpty()) {
      return new String[0];
    }

    List<String> result = new ArrayList<>();
    StringBuilder currentField = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);

      if (inQuotes) {
        if (ch == quote) {
          if (i + 1 < line.length() && line.charAt(i + 1) == quote) {
            // Aspas duplas dentro de um campo s�o escapadas com aspas duplas consecutivas
            currentField.append(quote);
            i++; // Pular a pr�xima aspa duplicada
          } else {
            inQuotes = false; // Fechar cita��o
          }
        } else {
          currentField.append(ch);
        }
      } else {
        if (ch == quote) {
          inQuotes = true;
        } else if (ch == separator) {
          result.add(currentField.toString());
          currentField.setLength(0);
        } else { // if (ch != '\r' && ch != '\n') {
          currentField.append(ch);
        }
      }
    }

    // Adiciona o �ltimo campo
    result.add(currentField.toString());

    return result.toArray(new String[0]);
  }

  /**
   * Extrai valores num�ricos do texto. Aceita que os milhares dos n�meros estejam separados por pontos e os decimais por v�rgula ou vice-versa.<br>
   * Este m�todo considera apenas valores que contenham um separador decimal.<br>
   *
   * @param text Texto de entrada do qual os n�meros decimais ser�o extra�dos.
   * @param groupId �ndice do grupo da express�o regular a ser retornado.
   * @param useCommaToDecimal Define se a v�rgula deve ser usada como separador decimal (true) ou o ponto (false).
   * @return O n�mero decimal extra�do do texto ou null caso nenhum n�mero v�lido seja encontrado.
   * @throws RFWException Se ocorrer erro na extra��o.
   */
  public static String extractDecimalValues(String text, int groupId, boolean useCommaToDecimal) throws RFWException {
    if (text == null || text.isEmpty()) {
      return null;
    }

    String decimalSeparator = useCommaToDecimal ? "," : "\\.";
    String thousandSeparator = useCommaToDecimal ? "\\." : ",";

    String regex = "(?:^|[^0-9" + decimalSeparator + "])([0-9]{1,3}(?:[" + thousandSeparator + "]?[0-9]{3})*[" + decimalSeparator + "][0-9]+)(?:$|[^0-9" + decimalSeparator + "])";
    return extract(text, regex, groupId);
  }

  /**
   * Extrai o conte�do de uma String que seja compat�vel com uma express�o regular.<br>
   * O conte�do retornado � o conte�do dentro do primeiro grupo encontrado.<br>
   *
   * @param text Texto de onde o valor dever� ser extra�do.
   * @param regExp Express�o regular que define o bloco a ser recuperado.
   * @return Conte�do que combina com a express�o regular, extra�do do texto principal, ou null caso o conte�do n�o seja encontrado.
   * @throws RFWException Se ocorrer erro na compila��o da express�o regular.
   */
  public static String extract(String text, String regExp) throws RFWException {
    return extract(text, regExp, 1);
  }

  /**
   * Extrai o conte�do de uma String que seja compat�vel com uma express�o regular.<br>
   * O conte�do retornado � o conte�do dentro do grupo definido pelo �ndice informado.<br>
   *
   * @param text Texto de onde o valor dever� ser extra�do.
   * @param regExp Express�o regular que define o bloco a ser recuperado.
   * @param groupId �ndice do grupo a ser retornado.
   * @return Conte�do que combina com a express�o regular, extra�do do texto principal, ou null caso o conte�do n�o seja encontrado.
   * @throws RFWException Se ocorrer erro na compila��o da express�o regular.
   */
  public static String extract(String text, String regExp, int groupId) throws RFWException {
    if (text == null || regExp == null) {
      return null;
    }

    Pattern pattern = Pattern.compile(regExp);
    Matcher matcher = pattern.matcher(text);
    return matcher.find() ? matcher.group(groupId) : null;
  }

  /**
   * Procura e extrai uma data no formato dd/MM/yyyy dentro de uma String.<br>
   * <b>Aten��o:</b> Este m�todo n�o valida a data, apenas busca uma ocorr�ncia no formato e a retorna.<br>
   * O m�todo verifica a consist�ncia dos dias em meses de 30, 31 e 29 dias. Para anos iniciados com 21xx, ser� necess�rio atualizar o m�todo.
   *
   * @param text Texto de entrada onde a data ser� procurada.
   * @param groupId �ndice do grupo da express�o regular a ser retornado.
   * @return A data encontrada ou null caso n�o seja encontrada nenhuma.
   * @throws RFWException
   */
  public static String extractDateDDMMYYYY(String text, int groupId) throws RFWException {
    if (text == null || text.isEmpty()) {
      return null;
    }

    String regExp = "((?:(?:0[1-9]|1[0-9]|2[0-9]|3[0-1])/(?:01|03|05|07|08|10|12)/(19|20)[0-9]{2})|(?:(?:0[1-9]|1[0-9]|2[0-9]|30)/(?:04|06|09|11)/(19|20)[0-9]{2})|(?:(?:0[1-9]|1[0-9]|2[0-9])/(?:02)/(19|20)[0-9]{2}))";
    return extract(text, regExp, groupId);
  }

  /**
   * Procura e extrai uma data no formato MM/yyyy dentro de uma String.<br>
   * <b>Aten��o:</b> Este m�todo n�o valida a data, apenas busca uma ocorr�ncia no formato e a retorna.<br>
   * O m�todo valida anos iniciados com 19xx ou 20xx.
   *
   * @param text Texto de entrada onde a data ser� procurada.
   * @param groupId �ndice do grupo da express�o regular a ser retornado.
   * @return A data encontrada ou null caso n�o seja encontrada nenhuma.
   * @throws RFWException
   */
  public static String extractDateMMYYYY(String text, int groupId) throws RFWException {
    if (text == null || text.isEmpty()) {
      return null;
    }

    String regExp = "(?:^|[^/])((?:0[0-9]|1[0-2])/(?:(19|20)[0-9]{2}))(?:$|[^/])";
    return extract(text, regExp, groupId);
  }

  /**
   * Procura e extrai uma hora no formato hh:mm:ss dentro de uma String.<br>
   * <b>Aten��o:</b> Este m�todo n�o valida o hor�rio, apenas busca uma ocorr�ncia no formato e a retorna.<br>
   * Verifica se o formato est� dentro dos limites v�lidos para horas, minutos e segundos.
   *
   * @param text Texto de entrada onde a hora ser� procurada.
   * @param groupId �ndice do grupo da express�o regular a ser retornado.
   * @return A hora encontrada ou null caso n�o seja encontrada nenhuma.
   * @throws RFWException
   */
  public static String extractTimeHHMMSS(String text, int groupId) throws RFWException {
    if (text == null || text.isEmpty()) {
      return null;
    }

    String regExp = "((?:[01][0-9]|2[0-3])\\:(?:[0-5][0-9])\\:(?:[0-5][0-9]))";
    return extract(text, regExp, groupId);
  }

  /**
   * Procura e extrai uma sequ�ncia de n�meros com a quantidade exata de d�gitos.<br>
   * Verifica se a sequ�ncia est� isolada (n�o inserida em outra sequ�ncia de n�meros).
   *
   * @param text Texto de entrada onde a sequ�ncia de n�meros ser� procurada.
   * @param digitsCount N�mero de d�gitos esperados na sequ�ncia.
   * @param groupId �ndice do grupo da express�o regular a ser retornado.
   * @return A sequ�ncia encontrada ou null caso n�o seja encontrada nenhuma.
   * @throws RFWException
   */
  public static String extractCodes(String text, int digitsCount, int groupId) throws RFWException {
    if (text == null || text.isEmpty()) {
      return null;
    }

    String regExp = "(?:^|[ ])([0-9]{" + digitsCount + "})(?:$|[ ])";
    return extract(text, regExp, groupId);
  }

  /**
   * Procura e extrai um n�mero de CNPJ de um texto, no formato pontuado ou n�o.<br>
   * <b>Aten��o:</b> Este m�todo n�o valida o CNPJ, apenas encontra uma ocorr�ncia no formato e a retorna.
   *
   * @param text Texto de entrada onde o CNPJ ser� procurado.
   * @param groupId �ndice do grupo da express�o regular a ser retornado.
   * @return O CNPJ encontrado ou null caso n�o seja encontrado nenhum.
   * @throws RFWException
   */
  public static String extractCNPJ(String text, int groupId) throws RFWException {
    if (text == null || text.isEmpty()) {
      return null;
    }

    String regExp = "(?:^|[^\\d])([0-9]{2}\\.?[0-9]{3}\\.?[0-9]{3}/?[0-9]{4}\\-?[0-9]{2})(?:$|[^\\d])";
    return extract(text, regExp, groupId);
  }

  /**
   * Procura e extrai um c�digo num�rico de servi�o/consumo de um texto.<br>
   * <b>Aten��o:</b> Este m�todo n�o valida o c�digo, apenas encontra uma ocorr�ncia no formato e a retorna.
   *
   * @param text Texto de entrada onde o c�digo ser� procurado.
   * @param groupId �ndice do grupo da express�o regular a ser retornado.
   * @return O c�digo encontrado ou null caso n�o seja encontrado nenhum.
   * @throws RFWException
   */
  public static String extractServiceNumericCode(String text, int groupId) throws RFWException {
    if (text == null || text.isEmpty()) {
      return null;
    }

    String regExp = "(?:^|[^0-9])((?:[0-9]{10}[ \\.-]?[0-9][ \\.-]?){4})(?:$|[^0-9])";
    return extract(text, regExp, groupId);
  }

}
