package br.eng.rodrigogml.rfw.kernel.utils;

import java.lang.reflect.Method;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

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
}
