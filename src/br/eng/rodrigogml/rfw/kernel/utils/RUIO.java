package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

/**
 * Description: Classe utilitária para métodos de IO.<br>
 *
 * @author Rodrigo GML
 * @since 1.0.0 (25 de ago. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public class RUIO {

  /**
   * Construtor privado para classe utilitária exclusivamente estática.
   */
  private RUIO() {
  }

  /**
   * Lê o conteúdo de um {@link InputStream} para um array de Bytes.
   *
   * @param in InputStream pronto para ser lido
   * @return array de bytes com o conteúdo lido do InputStream
   * @throws RFWException Em caso de problemas para lêr o InputStream
   */
  public static byte[] toByteArray(InputStream in) throws RFWException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      byte[] tmpbytes = new byte[16384];
      int c;
      while ((c = in.read(tmpbytes)) > 0) {
        out.write(tmpbytes, 0, c);
      }
      out.flush();
      return out.toByteArray();
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao lêr o conteúdo do InputStream!", e);
    }
  }

  /**
   * Lê o conteúdo de um {@link InputStream} para uma String utilizando o Charset UTF_8.
   *
   * @param in Stream com o conteúdo para ser lido.
   * @return String montada com os Bytes do InputStream convertidos utilizadno o UTF_8.
   * @throws RFWException
   */
  public static String toString(InputStream in) throws RFWException {
    return new String(toByteArray(in), StandardCharsets.UTF_8);
  }

  /**
   * Lê o conteúdo de um {@link InputStream} para uma String utilizando o Charset desejado.
   *
   * @param in Stream com o conteúdo para ser lido.
   * @param charset que será utilizado para converter os bytes em caracteres.
   * @return String montada com os Bytes do InputStream convertidos utilizadno o Charset passado.
   * @throws RFWException
   */
  public static String toString(InputStream in, Charset charset) throws RFWException {
    return new String(toByteArray(in), charset);
  }
}
