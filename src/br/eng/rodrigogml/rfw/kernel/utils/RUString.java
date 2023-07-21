package br.eng.rodrigogml.rfw.kernel.utils;

/**
 * Description: Classe com m�todos �teis para tratamentos e manipula��o de String.<br>
 *
 * @author Rodrigo Leit�o
 * @since 1.0.0 (AGO / 2007)
 * @version 4.1.0 (23/06/2011) - rodrigogml - Nome alterado de StringUtils, para ficar no padr�o do sistema.
 */
public class RUString {

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
   * Faz a mesma fun��o que os m�todo {@link #completeUntilLengthLeft(String, String, int)}, mas tamb�m faz a fun��o de {@link #truncate(String, int)} caso o tamanho recebido em data j� seja maior que o tamanho de length.
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
   * Faz a mesma fun��o que os m�toso {@link #completeUntilLengthRight(String, String, int)}, mas tamb�m faz a fun��o de {@link #truncate(String, int)} caso o tamanho recebido em data j� seja maior que o tamanho de length.
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
}
