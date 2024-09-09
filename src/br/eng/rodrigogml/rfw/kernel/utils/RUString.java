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
   * Substitui todas as ocorrencias de 'oldvalue' por 'newvalue' no texto de 'value'. No entanto este m�todo diferencia mai�sculas, min�sculas, acentos, etc.
   *
   * @param text texto a ser manipulado
   * @param oldvalue valor a ser procurado e substituido.
   * @param newvalue valor que substiuir� oldvalue.
   * @return
   */
  public static String replaceAll(String text, String oldvalue, String newvalue) {
    return replaceAll(text, oldvalue, newvalue, Boolean.TRUE, Boolean.TRUE);
  }

  /**
   * Substitui todas as ocorrencias de 'oldvalue' por 'newvalue' no texto de 'value'.<Br>
   * De acordo com as defini��es passadas, ele ignora acentos e case de letras.
   *
   * @param text texto a ser manipulado
   * @param oldvalue valor a ser procurado e substituido.
   * @param newvalue valor que substiuir� oldvalue.
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
   * Substitui o texto recursivamente at� que o texto n�o sofra mais altera��es, isto �, o texto ser� procurado do inicio ao fim pela substitui��o quantas vezes for necess�rias at� que seja feita uma busca completa e nada seja encontrado.<br>
   * <b>ATEN��O:</b> Pode gerar StackOverflow facilmente se substituimos um texto por outro que cont�m o valor sendo procurado!<Br>
   *
   * @param text texto a ser manipulado
   * @param oldvalue valor a ser procurado e substituido.
   * @param newvalue valor que substiuir� oldvalue.
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
   * Substitui o texto recursivamente at� que o texto n�o sofra mais altera��es, isto �, o texto ser� procurado do inicio ao fim pela substitui��o quantas vezes for necess�rias at� que seja feita uma busca completa e nada seja encontrado.<br>
   * <b>ATEN��O:</b> Pode gerar StackOverflow facilmente se substituimos um texto por outro que cont�m o valor sendo procurado!<Br>
   *
   * @param text texto a ser manipulado
   * @param oldvalue valor a ser procurado e substituido.
   * @param newvalue valor que substiuir� oldvalue.
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
   * Este m�todo decodifica uma string codificada em base 64.
   *
   * @param encodedContent String codificada
   * @return String decodificada
   */
  public static String decodeBase64(String encodedContent) {
    return new String(Base64.getMimeDecoder().decode(encodedContent));
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
      return new String(Base64.getMimeDecoder().decode(encodedContent), charset);
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
    return Base64.getMimeDecoder().decode(encodedContent);
  }

  /**
   * Este m�todo codifica uma string em base 64.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase64(String content) {
    return new String(Base64.getMimeEncoder().encodeToString(content.getBytes()));
  }

  /**
   * Este m�todo codifica um array de bytes em base 64.
   *
   * @param content String para ser codificada.
   * @return String codificada
   */
  public static String encodeBase64(byte[] content) {
    return new String(Base64.getMimeEncoder().encodeToString(content));
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
}
