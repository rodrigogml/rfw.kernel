package br.eng.rodrigogml.rfw.kernel.utils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

/**
 * Description: Classe utilit�ria com m�todos de defini��es de conex�es.<br>
 *
 * @author Rodrigo GML
 * @since 1.0.0 (24 de ago. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public class RUConnection {

  /**
   * Construtor privado para classe exclusivamente utilit�ria com m�todos est�ticos.
   */
  private RUConnection() {
  }

  /**
   * Prepara o java para conseseguir realizar conex�es HTTPS com encripta��o TLS 1.2.
   *
   * @param km define o KeyManager com os certificados privados que podem ser usados para criptografia da conex�o.
   * @param tm define o TrustManager, gerenciador de confiabilidade de certificados, para permitir que o java valide a realize a conex�o com os servidores que usem um desses certificados.
   * @param sslProtocol Procolo de conex�o SSL. Ex: SSLv3, TLSv1, TLSv1.1 and TLSv1.2
   * @throws RFWException
   */
  public static void setupTLSConnection(final KeyManager[] km, final TrustManager[] tm, String sslProtocol) throws RFWException {
    // Define que as conex�es que usam o protocolo de encripta��o PKGS devem utilizar a classe do pacote da SUN. Isso � necess�rio porque a implementa��o nativa do java.net.URL n�o d� suporte � HTTPS
    // System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");

    try {
      // Recupera o Contexto do SSL e define os certificados definidos
      SSLContext sc = SSLContext.getInstance(sslProtocol);
      sc.init(km, tm, null);
      SSLContext.setDefault(sc); // Define esse contexto de chaves para as conex�es SSL
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_000034", e);
    }
  }

  /**
   * Define que o java deve permitir a renegocia��o insegura no handshake do SSL/TLS.<br>
   * Desabilitar essa propriedade n�o � uma boa pr�tica, pois remove a seguran�a de valida��o dos certificados. <Br>
   * Considere incluir os certificados da outra parte no cacerts da JVM.<Br>
   * Use apenas para testes, n�o utilize em ambientes de produ��o.
   */
  public static void setUnsafeRenegotiation(final Boolean allow) {
    if (allow) {
      System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
    } else {
      System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "false");
    }
  }
}
