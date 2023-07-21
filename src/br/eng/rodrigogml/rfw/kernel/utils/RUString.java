package br.eng.rodrigogml.rfw.kernel.utils;

/**
 * Description: Classe com métodos úteis para tratamentos e manipulação de String.<br>
 *
 * @author Rodrigo Leitão
 * @since 1.0.0 (AGO / 2007)
 * @version 4.1.0 (23/06/2011) - rodrigogml - Nome alterado de StringUtils, para ficar no padrão do sistema.
 */
public class RUString {

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
   * Faz a mesma função que os método {@link #completeUntilLengthLeft(String, String, int)}, mas também faz a função de {@link #truncate(String, int)} caso o tamanho recebido em data já seja maior que o tamanho de length.
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
   * Faz a mesma função que os métoso {@link #completeUntilLengthRight(String, String, int)}, mas também faz a função de {@link #truncate(String, int)} caso o tamanho recebido em data já seja maior que o tamanho de length.
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
}
