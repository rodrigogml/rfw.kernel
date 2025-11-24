package br.eng.rodrigogml.rfw.kernel.utils.extra;

import br.eng.rodrigogml.rfw.kernel.utils.RUString;

/**
 * Description: Esta classe tem algumas funções auxiliares para os métodos da clase {@link RUString}: <code>decodeBase32</code>, <code>encodeBase32</code> e variações.<br>
 * O objetivo é manter o código interno ao RFWKernel sem importação de outras bibliotecas, como manda a especificação do RFW.Kernel.<br>
 * O resultado esperado é o mesmo oferecido pelas classes com.google.common.io.BaseEncoding. Como por exmeplo no código a seguir:<br>
 *
 * <pre>
 * BaseEncoding.base32().decode(content)
 * </pre>
 *
 * ou
 *
 * <pre>
 * BaseEncoding.base32().encode(content)
 * </pre>
 *
 * @author Rodrigo Leitão
 * @since (6 de set. de 2024)
 */
public class Base32 {

  private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
  private static final int[] BASE32_LOOKUP = new int[256];

  static {
    for (int i = 0; i < BASE32_LOOKUP.length; i++) {
      BASE32_LOOKUP[i] = -1;
    }
    for (int i = 0; i < BASE32_ALPHABET.length(); i++) {
      BASE32_LOOKUP[BASE32_ALPHABET.charAt(i)] = i;
    }
  }

  public static byte[] decode(String base32) {
    base32 = base32.toUpperCase().replaceAll("=", "");
    int length = base32.length();
    if (length == 0) {
      return new byte[0];
    }

    int numBytes = length * 5 / 8;
    byte[] result = new byte[numBytes];

    int buffer = 0;
    int bitsLeft = 0;
    int index = 0;

    for (int i = 0; i < length; i++) {
      char c = base32.charAt(i);
      int lookup = BASE32_LOOKUP[c];
      if (lookup == -1) {
        throw new IllegalArgumentException("Caractere Base32 inválido: " + c);
      }

      buffer <<= 5;
      buffer |= lookup & 31;
      bitsLeft += 5;

      if (bitsLeft >= 8) {
        result[index++] = (byte) (buffer >> (bitsLeft - 8));
        bitsLeft -= 8;
      }
    }

    return result;
  }

  public static String encode(byte[] bytes) {
    if (bytes.length == 0) {
      return "";
    }

    int outputLength = (bytes.length * 8 + 4) / 5;
    StringBuilder result = new StringBuilder(outputLength);

    int buffer = 0;
    int bitsLeft = 0;

    for (byte b : bytes) {
      buffer <<= 8;
      buffer |= b & 255;
      bitsLeft += 8;

      while (bitsLeft >= 5) {
        int index = (buffer >> (bitsLeft - 5)) & 31;
        result.append(BASE32_ALPHABET.charAt(index));
        bitsLeft -= 5;
      }
    }

    if (bitsLeft > 0) {
      int index = (buffer << (5 - bitsLeft)) & 31;
      result.append(BASE32_ALPHABET.charAt(index));
    }

    return result.toString();
  }
}
