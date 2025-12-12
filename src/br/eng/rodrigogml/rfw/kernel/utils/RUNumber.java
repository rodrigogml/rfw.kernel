package br.eng.rodrigogml.rfw.kernel.utils;

import java.math.BigDecimal;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

/**
 * Description: Classe de métodos utilitários para tratamento de números.<br>
 *
 * @author Rodrigo Leitão
 * @since 3.1.0 (NOV / 2009)
 * @version 4.1.0 (23/06/2011) - rodrigogml - Trocou o nome, antes NumberUtils, para ficar no padrão do Framework.
 * @deprecated Migrar os métodos dessa classe para a {@link RUTypes}
 */
@Deprecated
public class RUNumber {

  /**
   * Este método abstrai os cálculos que temos de fazer para gerar um número inteiro aleatório. Simplificando o código com uma chamada onde simplismente passamos os valores iniciais e final.<br>
   * <B>ATENÇÃO: </b> os valores passados são inclusivos, isto é, os valores passados podem ser retornados como resultado da função.
   *
   * @param min
   * @param max
   * @return número aleatório entre os valores min e max, inclusive.
   */
  public static int randomInt(int min, int max) {
    return (int) ((Math.random() * (max - min)) + min);
  }

  /**
   * Converte um array de Bytes para uma String no formato "[01]*", sendo a posição '0' do array impresso mais a esquerda.
   *
   * @param bytes Bytes que serão convertidos para uma String Binária.
   * @return Strig com os valores dos bytes em binário. Posição 0 do array = 8bits mais a esquerda, posição 1 = próximos 8 bits, e assim por diante
   */
  public static String byteArrayToBinaryString(byte[] bytes) {
    StringBuilder buff = new StringBuilder();
    for (int j = 0; j < bytes.length; j++) {
      buff.append(String.format("%8s", Integer.toBinaryString(bytes[j] & 0xFF)).replace(' ', '0'));
    }
    return buff.toString();
  }

  /**
   * Converte um Bytes para uma String no formato "[01]*", sendo a posição '0' do array impresso mais a esquerda.
   *
   * @param bytes Bytes que serão convertidos para uma String Binária.
   * @return Strig com os valores dos bytes em binário. Posição 0 do array = 8bits mais a esquerda, posição 1 = próximos 8 bits, e assim por diante
   */
  public static String byteToBinaryString(byte bt) {
    return String.format("%8s", Integer.toBinaryString(bt & 0xFF)).replace(' ', '0');
  }

  /**
   * Transforma uma sequencia de bytes no formato String (Ex: "11101110111011101110111011101110") em um array de bytes.<br>
   * Faz o processo inverso do {@link #byteArrayToBinaryString(byte[])}
   *
   * @param bytes String com a sequência de bits para ser transformada em bytes.
   * @return Array de bytes que representam os bytes (cada 8 bits) recebido. O tamanho do array será de 1/8 o tamanho da String.
   * @throws RFWException
   */
  public static byte[] binaryStringToByteArray(String bytes) throws RFWException {
    if (bytes.length() % 8 != 0) throw new RFWCriticalException("O valor binário não tem múltiplos de 8 bits! (Len: " + bytes.length() + ") - " + bytes);

    byte[] array = new byte[bytes.length() / 8];
    for (int i = 0; i < array.length; i++) {
      String bits = bytes.substring(8 * i, 8 * (i + 1));
      array[i] = (byte) Integer.parseUnsignedInt(bits, 2);
    }
    return array;
  }

  /**
   * Este método obtem um array de bytes e converte para uma string, utilizando os caretes que represemtam os bytes.
   *
   * @param bytes Array de bytes para serem transformados em HEX.
   *
   * @return String contendo os valores do array transformados em dígitos Hexadecimal.
   */
  public static String byteArrayToHexString(byte[] bytes) {
    char[] hexArray = "0123456789ABCDEF".toCharArray();
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  /**
   * Converte uma String (utilizando os bytes dos caractres como valores Hex) para um bytearray com valores Hexa.<br>
   * Ex: Se entrar: "3132333435363738393031323334353637383930" -> o byte array resultante monta a String "12345678901234567890" se new String(retByte[])
   *
   * @param s
   * @return
   */
  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

  /**
   * Garante que o BigDecimal terá no mínimo a escala desejada.
   *
   * @param value Valor para ter a escala corrigida
   * @param minScale Quantidade de dígitos mínimos na escala.
   * @return Retorna nulo se value for nulo. Retorna o value caso já tenha uma precisão maior ou igual ao minScale definido. Retorna o mesmo value com a a precisão aumentada para para o minScale caso a precisão atual seja menor que minScale.
   */
  public static BigDecimal minScale(BigDecimal value, int minScale) {
    if (value == null) return null;
    if (value.scale() < minScale) return value.setScale(minScale);
    return value;
  }

}