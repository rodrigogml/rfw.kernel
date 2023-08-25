package br.eng.rodrigogml.rfw.kernel.utils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

/**
 * Description: Classe utilitária com métodos de definições de conexões.<br>
 *
 * @author Rodrigo GML
 * @since 1.0.0 (24 de ago. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public class RUConnection {

  /**
   * Construtor privado para classe exclusivamente utilitária com métodos estáticos.
   */
  private RUConnection() {
  }

  /**
   * Prepara o java para conseseguir realizar conexões HTTPS com encriptação TLS 1.2.
   *
   * @param km define o KeyManager com os certificados privados que podem ser usados para criptografia da conexão.
   * @param tm define o TrustManager, gerenciador de confiabilidade de certificados, para permitir que o java valide a realize a conexão com os servidores que usem um desses certificados.
   * @param sslProtocol Procolo de conexão SSL. Ex: SSLv3, TLSv1, TLSv1.1 and TLSv1.2
   * @throws RFWException
   */
  public static void setupTLSConnection(final KeyManager[] km, final TrustManager[] tm, String sslProtocol) throws RFWException {
    // Define que as conexões que usam o protocolo de encriptação PKGS devem utilizar a classe do pacote da SUN. Isso é necessário porque a implementação nativa do java.net.URL não dá suporte à HTTPS
    // System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");

    try {
      // Recupera o Contexto do SSL e define os certificados definidos
      SSLContext sc = SSLContext.getInstance(sslProtocol);
      sc.init(km, tm, null);
      SSLContext.setDefault(sc); // Define esse contexto de chaves para as conexões SSL
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_000034", e);
    }
  }

  /**
   * Define que o java deve permitir a renegociação insegura no handshake do SSL/TLS.<br>
   * Desabilitar essa propriedade não é uma boa prática, pois remove a segurança de validação dos certificados. <Br>
   * Considere incluir os certificados da outra parte no cacerts da JVM.<Br>
   * Use apenas para testes, não utilize em ambientes de produção.
   */
  public static void setUnsafeRenegotiation(final Boolean allow) {
    if (allow) {
      System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
    } else {
      System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "false");
    }
  }
}
