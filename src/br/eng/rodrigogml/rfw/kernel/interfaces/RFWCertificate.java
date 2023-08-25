package br.eng.rodrigogml.rfw.kernel.interfaces;

/**
 * Description: Interface que define o conte�do necess�rio de um certificado para ser utilizado por outros m�dulos.<br>
 *
 * @author Rodrigo GML
 * @since 10.0 (19 de jul de 2021)
 */
public interface RFWCertificate {
  /**
   * Defini��es do Tipo de certificado.
   */
  public static enum CertificateType {
    /**
     * Certificado do Tipo A1 com arquivo feito Upload.
     */
    A1,
    /**
     * Certificado/Arquivo JKS padr�o do KeyStore do java. Que ser� lido no KeyStore com o {@link java.security.KeyStore#getDefaultType()}.
     */
    KeyStore
  }

  public CertificateType getType();

  public byte[] getCertificateFileContent();

  public String getCertificateFilePassword();

}
