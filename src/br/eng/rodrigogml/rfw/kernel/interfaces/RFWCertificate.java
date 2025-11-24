package br.eng.rodrigogml.rfw.kernel.interfaces;

/**
 * Description: Interface que define o conteúdo necessário de um certificado para ser utilizado por outros módulos.<br>
 *
 * @author Rodrigo GML
 * @since 10.0 (19 de jul de 2021)
 */
public interface RFWCertificate {
  /**
   * Definições do Tipo de certificado.
   */
  public static enum CertificateType {
    /**
     * Certificado do Tipo A1 com arquivo feito Upload.
     */
    A1,
    /**
     * Certificado A3.
     */
    /**
     * Certificado/Arquivo JKS padrão do KeyStore do java. Que será lido no KeyStore com o {@link java.security.KeyStore#getDefaultType()}.
     */
    KeyStore
  }

  public CertificateType getType();

  public byte[] getCertificateFileContent();

  public String getCertificateFilePassword();

}
