package br.eng.rodrigogml.rfw.kernel.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Description: Classe utilitária com métodos de geração de dados.<br>
 *
 * @author Rodrigo Leitão
 * @since 10.0.0 (12 de jul de 2018)
 */
public class RUGenerators {

  /**
   * Expressão regular para validar o UUID gerado pelo método {@link #generateUUID()}
   */
  public static final String UUID_REGEXP = "[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}";

  /**
   * Array com os digitos: 0-9.
   */
  public static final char[] digits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

  /**
   * Array com os caracteres: a-z, A-Z e 0-9.
   */
  public static final char[] simplechars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

  private RUGenerators() {
  }

  /**
   * Gera um identificador único baseado no UUID. Veja mais em http://www.baeldung.com/java-uuid <br>
   * <br>
   * Exemplo: '00e4a76d-c792-4909-994f-371633f60b63' 36 carecteres.
   *
   * <br>
   * <br>
   * Para validar a UUID utilize a regexp definida em {@link #UUID_REGEXP}.
   *
   * @return Identificador Único gerado
   */
  public static String generateUUID() {
    return UUID.randomUUID().toString();
  }

  /**
   * Gera uma String qualquer no padrão: [A-Za-z0-9]{length}
   *
   * @param length tamanho exato da String desejada
   * @return String gerada aleatoriamente
   */
  public static String generateString(int length) {
    StringBuilder buf = new StringBuilder(length);
    while (buf.length() < length) {
      buf.append(simplechars[(int) (Math.random() * simplechars.length)]);
    }
    return buf.toString();
  }

  /**
   * Gera uma String qualquer no padrão: [0-9]{length}
   *
   * @param length tamanho da String desejada
   * @return
   */
  public static String generateStringDigits(int length) {
    StringBuilder buf = new StringBuilder(length);
    while (buf.length() < length) {
      buf.append(digits[(int) (digits.length - (Math.random() * 10))]);
    }
    return buf.toString();
  }

  /**
   * Este método cria uma sequência numérica aleatória.
   *
   * @param length Tamanho/Quantida de Dígitos da sequência
   */
  public static String generateNumericSequence(int length) {
    String seq = "";
    while (seq.length() != length) {
      seq += ("" + Math.random()).replaceAll("\\.", "");
      if (seq.length() > length) {
        // Obtem os dígitos mais a direita pois eles se alteram mais do que os primeiros gerados pelo Math.random()
        seq = seq.substring(seq.length() - length, seq.length());
      }
    }
    return seq;
  }

  /**
   * Este método abstrai os cálculos que temos de fazer para gerar um número inteiro aleatório. Simplificando o código com uma chamada onde simplismente passamos os valores iniciais e final.<br>
   * <B>ATENÇÃO: </b> os valores passados são inclusivos, isto é, os valores passados podem ser retornados como resultado da função.
   *
   * @param min Menor número gerado (inclusivo)
   * @param max Maior número gerado (inclusivo)
   * @return número aleatório entre os valores min e max, inclusive.
   */
  public static int generateInt(int min, int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }
}
