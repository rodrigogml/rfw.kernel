package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.interfaces.RFWCertificate;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLogger;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe utilit�ria com m�todos para manipula��o e utiliza��o de certificados digitais.<br>
 *
 * @author Rodrigo GML
 * @since 1.0.0 (24 de ago. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public class RUCert {

  /**
   * Construtor privado para classe exclusivamente est�tica.
   */
  private RUCert() {
  }

  /**
   * Carrega um arquivo de certificado A1 (geralmente arquivo .pfx) dentro de um KeyStore para ser utilizado.
   *
   * @param filePath arquivo do certificado (geralmente .pfx).
   * @param password Senha para abertura do arquivo.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lan�ado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStoreA1Certificate(String filePath, String password) throws RFWException {
    try {
      return loadKeyStoreA1Certificate(new FileInputStream(filePath), password);
    } catch (FileNotFoundException e) {
      throw new RFWCriticalException("Falha ao l�r o arquivo do certificado!", e);
    }
  }

  /**
   * Carrega um arquivo de certificado A1 (geralmente arquivo .pfx) dentro de um KeyStore para ser utilizado.
   *
   * @param file arquivo do certificado (geralmente .pfx).
   * @param password Senha para abertura do arquivo.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lan�ado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStoreA1Certificate(File file, String password) throws RFWException {
    try {
      return loadKeyStoreA1Certificate(new FileInputStream(file), password);
    } catch (FileNotFoundException e) {
      throw new RFWCriticalException("Falha ao l�r o arquivo do certificado!", e);
    }
  }

  /**
   * Carrega um arquivo de certificado A1 (geralmente arquivo .pfx) dentro de um KeyStore para ser utilizado.
   *
   * @param bytes conte�do do arquivo j� em array de bytes.
   * @param password Senha para abertura do arquivo.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lan�ado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStoreA1Certificate(byte[] bytes, String password) throws RFWException {
    return loadKeyStoreA1Certificate(new ByteArrayInputStream(bytes), password);
  }

  /**
   * Carrega um arquivo de certificado A1 (geralmente arquivo .pfx) dentro de um KeyStore para ser utilizado.
   *
   * @param bytes conte�do do arquivo j� em array de bytes.
   * @param in InputStream com o conte�do do certificado.
   * @param password Senha para abertura do arquivo.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lan�ado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStoreA1Certificate(InputStream in, String password) throws RFWException {
    return loadKeyStore(in, password, "PKCS12");
  }

  /**
   * L� um arquivo de keyStore.<Br>
   * Repassa a chamada para {@link #loadKeyStore(InputStream, String, String)} utilizando o {@link KeyStore#getDefaultType()}.
   *
   * @param filePath Arquivo do certificado para leitura.
   * @param password Senha do arquivo para abertura.
   * @return Keystore com o certificado pronto para uso.
   * @throws RFWException Lan�ado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(String filePath, String password) throws RFWException {
    try {
      return loadKeyStore(new FileInputStream(filePath), password, KeyStore.getDefaultType());
    } catch (FileNotFoundException e) {
      throw new RFWCriticalException("Falha ao l�r o arquivo do certificado!", e);
    }
  }

  /**
   * L� um arquivo de keyStore.<Br>
   * Repassa a chamada para {@link #loadKeyStore(InputStream, String, String)} utilizando o {@link KeyStore#getDefaultType()}.
   *
   * @param filePath Arquivo do certificado para leitura.
   * @param password Senha do arquivo para abertura.
   * @return Keystore com o certificado pronto para uso.
   * @throws RFWException Lan�ado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(byte[] fileContent, String password) throws RFWException {
    return loadKeyStore(new ByteArrayInputStream(fileContent), password, KeyStore.getDefaultType());
  }

  /**
   * L� um arquivo de keyStore.<Br>
   * Repassa a chamada para {@link #loadKeyStore(InputStream, String, String)} utilizando o {@link KeyStore#getDefaultType()}.
   *
   * @param file Arquivo do certificado para leitura.
   * @param password Senha do arquivo para abertura.
   * @return Keystore com o certificado pronto para uso.
   * @throws RFWException Lan�ado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(File file, String password) throws RFWException {
    try {
      return loadKeyStore(new FileInputStream(file), password, KeyStore.getDefaultType());
    } catch (FileNotFoundException e) {
      throw new RFWCriticalException("Falha ao l�r o arquivo do certificado!", e);
    }
  }

  /**
   * L� um arquivo de keyStore.<Br>
   * Repassa a chamada para {@link #loadKeyStore(InputStream, String, String)} utilizando o {@link KeyStore#getDefaultType()}.
   *
   * @param in {@link InputStream} com o conte�do do arquivo
   * @param password Senha do arquivo para abertura.
   * @return Keystore com o certificado pronto para uso.
   * @throws RFWException Lan�ado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(InputStream in, String password) throws RFWException {
    return loadKeyStore(in, password, KeyStore.getDefaultType());
  }

  /**
   * Carrega um arquivo de keyStore ou certificado dentro de um KeyStore para ser utilizado.
   *
   * @param in InputStream com o conte�do do certificado.
   * @param password Senha para abertura do arquivo.
   * @param keyStoreType Tipo do KeyStore a ser lido.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lan�ado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(InputStream in, String password, String keyStoreType) throws RFWException {
    try {
      KeyStore keystore = KeyStore.getInstance(keyStoreType);
      keystore.load(in, password.toCharArray());
      return keystore;
    } catch (KeyStoreException e) {
      throw new RFWCriticalException("Falha ao criar o KeyStore do certificado!", e);
    } catch (NoSuchAlgorithmException e) {
      throw new RFWCriticalException("Algor�timo desconhecido para cria��o Store do certificado!", e);
    } catch (CertificateException e) {
      throw new RFWCriticalException("Falha ao carregar o certificado!", e);
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao ler o conte�do do certificado!", e);
    }
  }

  /**
   * Gera uma String contendo os detalhes dos certificados dentro do KeyStore.<br>
   * Itera todos os certificados encontrados e passa para o m�todo {@link #writeCertDetails(Certificate)}, verifique os tipos de certificados suportados pelo m�todo.
   *
   * @param keyStore KeyStore com os certificados para obten��o dos detalhes.
   * @return String contento os detalhes do certificado.
   * @throws RFWException
   */
  public static String writeCertDetails(KeyStore keyStore) throws RFWException {
    StringWriter buff = new StringWriter();
    try {
      Enumeration<String> aliases = keyStore.aliases();
      while (aliases.hasMoreElements()) {
        String alias = aliases.nextElement();
        Certificate certificate = keyStore.getCertificate(alias);

        buff.append("[Alias: ").append(alias).append("]\r\n");
        buff.append(writeCertDetails(certificate));
        buff.append("-----");
      }
      return buff.toString();
    } catch (KeyStoreException e) {
      throw new RFWCriticalException("Falha ao ler o KeyStore!", e);
    }
  }

  /**
   * Criar um texto com os detalhes do certificado.<br>
   * Este m�todo verifica o tipo de certificado para conseguir extrair as informa��es, atualmente o m�todo tem suporte aos certificados do tipo:<br>
   * <li>X509Certificate
   *
   * @param certificate Certificado para impress�o de seus atributos
   * @return String com seu conte�do.
   */
  public static String writeCertDetails(Certificate certificate) {
    StringWriter buff = new StringWriter();
    if (certificate instanceof X509Certificate) {
      X509Certificate cert = (X509Certificate) certificate;
      buff.append("Subject: " + cert.getSubjectDN()).append("\r\n");
      buff.append("Issuer: " + cert.getIssuerDN()).append("\r\n");
      buff.append("Serial Number: " + cert.getSerialNumber()).append("\r\n");
      buff.append("Valid From: " + cert.getNotBefore()).append("\r\n");
      buff.append("Valid Until: " + cert.getNotAfter()).append("\r\n");
    }
    return buff.toString();
  }

  /**
   * Coloca a KeyStore dentro da KeyManager do java.<br>
   * Essas Keymanager podem ser usadas nas conex�es SSL para autenticar a origem da conex�o.<br>
   * Utiliza o Algoritimo "SunX509" como padr�o do certificado.
   *
   * @param keyStore KeyStore contendo os certificados para serem utilizados na conex�o.
   * @param password Senha da KeyStore.
   * @return Array com as KeyManager criados.
   * @throws RFWException
   *           <li>RFW_ERR_000002 - Falha ao abrir certificado. Verifique o arquivo e a senha.
   */
  public static KeyManager[] createKeyManager(KeyStore keyStore, String password) throws RFWException {
    try {
      KeyManagerFactory keymanagerfactory = KeyManagerFactory.getInstance("SunX509"); // KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keymanagerfactory.init(keyStore, password.toCharArray());
      KeyManager[] keymanagers = keymanagerfactory.getKeyManagers();
      return keymanagers;
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_000033");
    }
  }

  /**
   * Cria um TrustManager a partir dos certificados de um KeyStore.<br>
   * Utiliza o Algoritimo "SunX509" como padr�o do certificado.
   *
   * @param keyStore KeyStore contendo os certificados que ser�o confi�veis durante o HandShake da comunica��o.
   * @return TrustManager pronto para ser utilizado durante a comunica��o.
   * @throws RFWException
   *           <li>RFW_ERR_000002 - Falha ao abrir certificado. Verifique o arquivo e a senha.
   */
  public static TrustManager[] createTrustManager(KeyStore keyStore) throws RFWException {
    try {
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
      trustManagerFactory.init(keyStore);
      return trustManagerFactory.getTrustManagers();
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_000033");
    }
  }

  /**
   * Cria um TrustManager para ser usado em conex�es SSL a partir de cadeias de certificados carregados de um arquivo criado pelo keytool (KeyStore).<Br>
   * Melhor explica��o de como usar o keytool na documenta��o do BISERP.
   *
   * @param in arquivo com uma ou mais cadeias de certificados para serem confiados.
   * @param pass senha do arquivo para importa��o dos certificados.
   * @return
   * @throws RFWException
   */
  public static TrustManager[] createTrustManager(final InputStream in, final String pass) throws RFWException {
    try {
      // Cria um novo KeyStore com o tipo de algor�timo JKS
      KeyStore truststore = KeyStore.getInstance("JKS");
      truststore.load(in, pass.toCharArray());
      try {
        // Uma vez que j� foi lido, for�amos seu fechamento para garantir que n�o teremos vazamento de recurso, mas se falhar n�o ligamos, logamos mas continuamos o m�todo.
        in.close();
      } catch (Exception e) {
        RFWLogger.logException(e);
      }

      return createTrustManager(truststore);
      // // Cria o gerenciador de confiabilidade de certificados passando a cadeia de certificados lida
      // TrustManagerFactory trustmanagerfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      // trustmanagerfactory.init(truststore);
      // TrustManager[] trustmanagers = trustmanagerfactory.getTrustManagers();
      //
      // return trustmanagers;
    } catch (KeyStoreException e) {
      throw new RFWCriticalException("BISERP_000082", e);
    } catch (NoSuchAlgorithmException e) {
      throw new RFWCriticalException("BISERP_000082", e);
    } catch (CertificateException e) {
      throw new RFWCriticalException("BISERP_000083", e);
    } catch (IOException e) {
      throw new RFWValidationException("BISERP_000081", e);
    }
  }

  /**
   * Carrega o {@link RFWCertificate} em um KeyStore baseando-se nas configura��es padr�o para cada tipo de certificado suportado.
   *
   * @param certificate Objeto com as defini��es do certificado.
   * @return KeyStore com o certificado inserido.
   * @throws RFWException Lan�ado em casa de falha ao criar o KeyStore.
   */
  public static KeyStore loadKeyStore(RFWCertificate certificate) throws RFWException {
    PreProcess.requiredNonNull(certificate, "Certificado n�o pode ser nulo!");

    KeyStore ks = null;
    switch (certificate.getType()) {
      case A1:
        ks = loadKeyStoreA1Certificate(certificate.getCertificateFileContent(), certificate.getCertificateFilePassword());
        break;
      case KeyStore:
        ks = loadKeyStore(certificate.getCertificateFileContent(), certificate.getCertificateFilePassword());
        break;
    }

    if (ks == null) throw new RFWCriticalException("N�o foi poss�vel criar o KeyStore para o Certificado passado!", new String[] { RUReflex.printObject(certificate) });
    return ks;
  }

  /**
   * Recupera a chave privada e o certificado X.509 a partir de um {@link RFWCertificate}.
   *
   * @param certificate Inst�ncia do certificado contendo o arquivo e senha.
   * @return Um {@link KeyStore.PrivateKeyEntry} contendo a chave privada e o certificado.
   * @throws RFWException Se houver falha ao carregar o certificado ou se nenhuma chave privada for encontrada.
   */
  public static KeyStore.PrivateKeyEntry extractPrivateKey(RFWCertificate certificate) throws RFWException {
    PreProcess.requiredNonNull(certificate, "Certificado n�o pode ser nulo!");

    KeyStore keyStore = loadKeyStore(certificate);

    try {
      Enumeration<String> aliases = keyStore.aliases();
      while (aliases.hasMoreElements()) {
        String alias = aliases.nextElement();
        if (keyStore.isKeyEntry(alias)) {
          return (KeyStore.PrivateKeyEntry) keyStore.getEntry(
              alias, new KeyStore.PasswordProtection(certificate.getCertificateFilePassword().toCharArray()));
        }
      }
    } catch (KeyStoreException | NoSuchAlgorithmException | java.security.UnrecoverableEntryException e) {
      throw new RFWCriticalException("Falha ao extrair a chave privada do certificado!", e);
    }

    throw new RFWCriticalException("Nenhuma chave privada encontrada no certificado!");
  }

  /**
   * Este m�todo retorna uma string com as informa��es coletadas do Certificado montadas em um Array BiDimensional String[x][y].<br>
   * Onde x tem tamanho indefinido de acordo com a quantidade de parametros encontratos no certificado; e y vai de 0 � 1, sendo 0 o t�tulo do atributo e 1 o valor do atributo.
   *
   * @return
   * @throws RFWException
   */
  public static String[][] getCertificateInfoArray(Certificate certificate) throws RFWException {
    String[][] ret = null;
    if (certificate instanceof X509Certificate) {
      X509Certificate x509 = (X509Certificate) certificate;

      final ArrayList<String> topic = new ArrayList<>();
      final ArrayList<String> value = new ArrayList<>();

      topic.add("Owner");
      value.add(x509.getSubjectDN().getName());

      topic.add("Issuer");
      value.add(x509.getIssuerDN().getName());

      topic.add("Serial Number");
      value.add(x509.getSerialNumber().toString());

      topic.add("Valid From");
      value.add(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM).format(x509.getNotBefore()));

      topic.add("Valid To");
      value.add(SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM).format(x509.getNotAfter()));

      topic.add("Certificate MD5 Fingerprint");
      value.add(RUCert.getMD5FingerPrintFromCertificate(certificate));

      topic.add("Certificate SHA1 Fingerprint");
      value.add(RUCert.getSHA1FingerPrintFromCertificate(certificate));

      topic.add("Signature Algorithm Name");
      value.add(x509.getSigAlgName());

      topic.add("Version");
      value.add("" + x509.getVersion());

      ret = new String[topic.size()][2];
      for (int i = 0; i < topic.size(); i++) {
        ret[i][0] = topic.get(i);
        ret[i][1] = value.get(i);
      }

    }
    return ret;
  }

  /**
   * Este m�todo retorna o fingerprint MD5 do certificado.
   *
   * @param certificate instancia do certificado.
   * @return retorna o fingerprint do certificado, Ex: '1A:DE:60:21:DE:B1:BF:C3:D1:AD:11:F1:21:22:D7:9E'
   * @throws RFWException Lan�ado caso o certificado n�o possua algor�timo MD5, ou ocorra algum erro ao decodificar o certificado.
   */
  public static String getMD5FingerPrintFromCertificate(Certificate certificate) throws RFWException {
    /*
     * Este c�digo foi criado com base no c�digo fonte da ferramenta keytool fornecida junto com o java. N�o sei explicar exatamente seu funcionamento nem hoje, que estou implementando, n�o me pergunte no futuro! Link do SourceCode usado de Base: http://www.docjar.com/html/api/sun/security/tools/KeyTool.java.html
     */
    try {
      byte[] digest = MessageDigest.getInstance("MD5").digest(certificate.getEncoded());
      StringBuilder buf = new StringBuilder();
      int len = digest.length;
      char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
      for (int i = 0; i < len; i++) {
        int high = ((digest[i] & 0xf0) >> 4);
        int low = (digest[i] & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
        if (i < len - 1) {
          buf.append(":");
        }
      }
      return buf.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RFWValidationException("BISERP_000219", e);
    } catch (CertificateEncodingException e) {
      // Lan�ado como erro cr�tico porque n�o sei quando esse erro ocorrer�, a medida que for acontecendo vamos melhorando o tratamento do erro
      throw new RFWCriticalException("BISERP_000220", e);
    }
  }

  /**
   * Este m�todo retorna o fingerprint SHA1 do certificado.
   *
   * @param certificate instancia do certificado.
   * @return retorna o fingerprint do certificado, Ex: '72:3A:D9:2E:1A:DE:60:21:DE:B1:BF:C3:D1:AD:11:F1:21:22:D7:9E'
   * @throws RFWException Lan�ado caso o certificado n�o possua algor�timo SHA1, ou ocorra algum erro ao decodificar o certificado.
   */
  public static String getSHA1FingerPrintFromCertificate(Certificate certificate) throws RFWException {
    /*
     * Este c�digo foi criado com base no c�digo fonte da ferramenta keytool fornecida junto com o java. N�o sei explicar exatamente seu funcionamento nem hoje, que estou implementando, n�o me pergunte no futuro! Link do SourceCode usado de Base: http://www.docjar.com/html/api/sun/security/tools/KeyTool.java.html
     */
    try {
      byte[] digest = MessageDigest.getInstance("SHA1").digest(certificate.getEncoded());
      StringBuilder buf = new StringBuilder();
      int len = digest.length;
      char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
      for (int i = 0; i < len; i++) {
        int high = ((digest[i] & 0xf0) >> 4);
        int low = (digest[i] & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
        if (i < len - 1) {
          buf.append(":");
        }
      }
      return buf.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RFWValidationException("BISERP_000219", e);
    } catch (CertificateEncodingException e) {
      // Lan�ado como erro cr�tico porque n�o sei quando esse erro ocorrer�, a medida que for acontecendo vamos melhorando o tratamento do erro
      throw new RFWCriticalException("BISERP_000220", e);
    }
  }

  /**
   * Este m�todo retorna a data de in�cio de vig�ncia (validade) do certificado.
   *
   * @return
   * @throws RFWException
   */
  public static Date getCertificateValidityStart(Certificate certificate) throws RFWException {
    Date dt = null;
    if (certificate instanceof X509Certificate) {
      X509Certificate x509 = (X509Certificate) certificate;
      dt = x509.getNotBefore();
    }
    if (dt == null) {
      throw new RFWValidationException("BISERP_000246");
    }
    return dt;
  }

  /**
   * Este m�todo retorna a data de fim de vig�ncia (validade) do certificado.
   *
   * @return
   * @throws RFWException
   */
  public static Date getCertificateValidityEnd(Certificate certificate) throws RFWException {
    Date dt = null;
    if (certificate instanceof X509Certificate) {
      X509Certificate x509 = (X509Certificate) certificate;
      dt = x509.getNotAfter();
    }
    if (dt == null) {
      throw new RFWValidationException("BISERP_000246");
    }
    return dt;
  }

  /**
   * Este m�todo retorna uma string com as informa��es coletadas do Certificado.
   *
   * @return
   * @throws RFWException
   */
  public static String getCertificateInfo(Certificate certificate) throws RFWException {
    StringBuilder buff = new StringBuilder();
    if (certificate instanceof X509Certificate) {
      X509Certificate x509 = (X509Certificate) certificate;

      final String[] subject = x509.getSubjectDN().getName().split(",");
      buff.append("Owner: ").append(subject[0]).append('\n');
      for (int i = 1; i < subject.length; i++) {
        buff.append("      ").append(subject[i]).append('\n');
      }
      final String[] issuerDN = x509.getIssuerDN().getName().split(",");
      buff.append("Issuer: ").append(issuerDN[0]).append('\n');
      for (int i = 1; i < issuerDN.length; i++) {
        buff.append("      ").append(issuerDN[i]).append('\n');
      }
      buff.append("Serial Number: ").append(x509.getSerialNumber()).append('\n');
      buff.append("Valid from: ").append(x509.getNotBefore()).append(" until: ").append(x509.getNotAfter()).append('\n');
      buff.append("Certificate fingerprints: \n");
      try {
        buff.append("\tMD5:").append(RUCert.getMD5FingerPrintFromCertificate(certificate)).append('\n');
      } catch (Exception e) {
        // S� n�o escreve se n�o tiver fingerprint md5
      }
      try {
        buff.append("\tSHA1:").append(RUCert.getSHA1FingerPrintFromCertificate(certificate)).append('\n');
      } catch (Exception e) {
        // S� n�o escreve se n�o tiver fingerprint sha1
      }
      buff.append("\tSignature algorithm name: ").append(x509.getSigAlgName()).append('\n');
      buff.append("\tVersion: ").append(x509.getVersion()).append('\n');

      // x509.getIssuerX500Principal();
      // x509.getKeyUsage();
      // x509.getNonCriticalExtensionOIDs();
      // x509.getSigAlgOID();
      // x509.getSubjectAlternativeNames();
      // x509.getSubjectX500Principal();
      // x509.getType();
      // x509.getVersion();
    }
    return buff.toString();
  }

  /**
   * Procura pelos Alias de chaves privadas dentro de um certificado.
   *
   * @param keyStore Certificado j� carregado em um KeyStore.
   * @return Lista com os alias de chaves privadas encontradas, ou uma lista vazia se nenhuma chave for encontrada.
   * @throws RFWException Lan�ado caso n�o seja poss�vel recuperar os alias ou abrir o KeyStore.
   */
  public static List<String> searchPrivateKeyAlias(KeyStore keyStore) throws RFWException {
    LinkedList<String> list = new LinkedList<String>();
    try {
      Enumeration<String> aliases = keyStore.aliases();
      while (aliases.hasMoreElements()) {
        String alias = aliases.nextElement();
        if (keyStore.isKeyEntry(alias)) {
          list.add(alias);
        }
      }
    } catch (Exception e) {
      throw new RFWCriticalException("Falha ao procurar por chaves privadas no Certificado!", e);
    }
    return list;
  }
}
