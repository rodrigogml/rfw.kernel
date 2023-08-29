package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

/**
 * Description: Classe utilit�ria para m�todos de IO.<br>
 *
 * @author Rodrigo GML
 * @since 1.0.0 (25 de ago. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public class RUIO {

  /**
   * Construtor privado para classe utilit�ria exclusivamente est�tica.
   */
  private RUIO() {
  }

  /**
   * L� o conte�do de um {@link InputStream} para um array de Bytes.
   *
   * @param in InputStream pronto para ser lido
   * @return array de bytes com o conte�do lido do InputStream
   * @throws RFWException Em caso de problemas para l�r o InputStream
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
      throw new RFWCriticalException("Falha ao l�r o conte�do do InputStream!", e);
    }
  }

  /**
   * L� o conte�do de um {@link InputStream} para uma String utilizando o Charset UTF_8.
   *
   * @param in Stream com o conte�do para ser lido.
   * @return String montada com os Bytes do InputStream convertidos utilizadno o UTF_8.
   * @throws RFWException
   */
  public static String toString(InputStream in) throws RFWException {
    return new String(toByteArray(in), StandardCharsets.UTF_8);
  }

  /**
   * L� o conte�do de um {@link InputStream} para uma String utilizando o Charset desejado.
   *
   * @param in Stream com o conte�do para ser lido.
   * @param charset que ser� utilizado para converter os bytes em caracteres.
   * @return String montada com os Bytes do InputStream convertidos utilizadno o Charset passado.
   * @throws RFWException
   */
  public static String toString(InputStream in, Charset charset) throws RFWException {
    return new String(toByteArray(in), charset);
  }

  /**
   * Escreve o conte�do lido de um InputStream em um OutputStream.<Br>
   * Este m�todo invoca o .close() de ambos os streams ao finalizar a c�pia.
   *
   * @param in InputStream para leitura dos dados.
   * @param out OutputStream para escrita dos dados.
   * @param closeStreams se true, o m�todo chama as fun��es .close() dos streams, caso false os streams s�o deixados em aberto ap�s a c�pia. �til quando vamos compiar multiplos conte�dos ou se os streams j� foram criandos dentro de blocos try() que executam o fechamento.
   * @throws RFWException Lan�ado caso n�o seja poss�vel manipular algum dos Streams.
   */
  public static void copy(InputStream in, OutputStream out, boolean closeStreams) throws RFWException {
    try {
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao copiar conte�do do InputStream para o OutputStream.", e);
    } finally {
      try {
        if (closeStreams) out.close();
      } catch (IOException e) {
      }
      try {
        if (closeStreams) in.close();
      } catch (IOException e) {
      }
    }
  }
}
