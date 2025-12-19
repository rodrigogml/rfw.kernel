package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.interfaces.RFWCertificate;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLogger;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe utilitária com métodos para manipulação e utilização de certificados digitais.<br>
 *
 * @author Rodrigo GML
 * @since 1.0.0 (24 de ago. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public class RUCert {

  /**
   * Construtor privado para classe exclusivamente estática.
   */
  private RUCert() {
  }

  /**
   * Carrega um arquivo de certificado A1 (geralmente arquivo .pfx) dentro de um KeyStore para ser utilizado.
   *
   * @param filePath arquivo do certificado (geralmente .pfx).
   * @param password Senha para abertura do arquivo.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lançado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStoreA1Certificate(String filePath, String password) throws RFWException {
    try {
      return loadKeyStoreA1Certificate(new FileInputStream(filePath), password);
    } catch (FileNotFoundException e) {
      throw new RFWCriticalException("Falha ao lêr o arquivo do certificado!", e);
    }
  }

  /**
   * Carrega um arquivo de certificado A1 (geralmente arquivo .pfx) dentro de um KeyStore para ser utilizado.
   *
   * @param file arquivo do certificado (geralmente .pfx).
   * @param password Senha para abertura do arquivo.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lançado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStoreA1Certificate(File file, String password) throws RFWException {
    try {
      return loadKeyStoreA1Certificate(new FileInputStream(file), password);
    } catch (FileNotFoundException e) {
      throw new RFWCriticalException("Falha ao lêr o arquivo do certificado!", e);
    }
  }

  /**
   * Carrega um arquivo de certificado A1 (geralmente arquivo .pfx) dentro de um KeyStore para ser utilizado.
   *
   * @param bytes conteúdo do arquivo já em array de bytes.
   * @param password Senha para abertura do arquivo.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lançado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStoreA1Certificate(byte[] bytes, String password) throws RFWException {
    return loadKeyStoreA1Certificate(new ByteArrayInputStream(bytes), password);
  }

  /**
   * Carrega um arquivo de certificado A1 (geralmente arquivo .pfx) dentro de um KeyStore para ser utilizado.
   *
   * @param bytes conteúdo do arquivo já em array de bytes.
   * @param in InputStream com o conteúdo do certificado.
   * @param password Senha para abertura do arquivo.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lançado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStoreA1Certificate(InputStream in, String password) throws RFWException {
    return loadKeyStore(in, password, "PKCS12");
  }

  /**
   * Lê um arquivo de keyStore.<Br>
   * Repassa a chamada para {@link #loadKeyStore(InputStream, String, String)} utilizando o {@link KeyStore#getDefaultType()}.
   *
   * @param filePath Arquivo do certificado para leitura.
   * @param password Senha do arquivo para abertura.
   * @return Keystore com o certificado pronto para uso.
   * @throws RFWException Lançado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(String filePath, String password) throws RFWException {
    try {
      return loadKeyStore(new FileInputStream(filePath), password, KeyStore.getDefaultType());
    } catch (FileNotFoundException e) {
      throw new RFWCriticalException("Falha ao lêr o arquivo do certificado!", e);
    }
  }

  /**
   * Lê um arquivo de keyStore.<Br>
   * Repassa a chamada para {@link #loadKeyStore(InputStream, String, String)} utilizando o {@link KeyStore#getDefaultType()}.
   *
   * @param filePath Arquivo do certificado para leitura.
   * @param password Senha do arquivo para abertura.
   * @return Keystore com o certificado pronto para uso.
   * @throws RFWException Lançado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(byte[] fileContent, String password) throws RFWException {
    return loadKeyStore(new ByteArrayInputStream(fileContent), password, KeyStore.getDefaultType());
  }

  /**
   * Lê um arquivo de keyStore.<Br>
   * Repassa a chamada para {@link #loadKeyStore(InputStream, String, String)} utilizando o {@link KeyStore#getDefaultType()}.
   *
   * @param file Arquivo do certificado para leitura.
   * @param password Senha do arquivo para abertura.
   * @return Keystore com o certificado pronto para uso.
   * @throws RFWException Lançado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(File file, String password) throws RFWException {
    try {
      return loadKeyStore(new FileInputStream(file), password, KeyStore.getDefaultType());
    } catch (FileNotFoundException e) {
      throw new RFWCriticalException("Falha ao lêr o arquivo do certificado!", e);
    }
  }

  /**
   * Lê um arquivo de keyStore.<Br>
   * Repassa a chamada para {@link #loadKeyStore(InputStream, String, String)} utilizando o {@link KeyStore#getDefaultType()}.
   *
   * @param in {@link InputStream} com o conteúdo do arquivo
   * @param password Senha do arquivo para abertura.
   * @return Keystore com o certificado pronto para uso.
   * @throws RFWException Lançado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(InputStream in, String password) throws RFWException {
    return loadKeyStore(in, password, KeyStore.getDefaultType());
  }

  /**
   * Carrega um arquivo de keyStore ou certificado dentro de um KeyStore para ser utilizado.
   *
   * @param in InputStream com o conteúdo do certificado.
   * @param password Senha para abertura do arquivo.
   * @param keyStoreType Tipo do KeyStore a ser lido.
   * @return KeyStore com o certificado pronto para uso.
   * @throws RFWException Lançado em casa de qualquer falha ao carregar o certificado.
   */
  public static KeyStore loadKeyStore(InputStream in, String password, String keyStoreType) throws RFWException {
    try {
      KeyStore keystore = KeyStore.getInstance(keyStoreType);
      keystore.load(in, password.toCharArray());
      return keystore;
    } catch (KeyStoreException e) {
      throw new RFWCriticalException("Falha ao criar o KeyStore do certificado!", e);
    } catch (NoSuchAlgorithmException e) {
      throw new RFWCriticalException("Algorítimo desconhecido para criação Store do certificado!", e);
    } catch (CertificateException e) {
      throw new RFWCriticalException("Falha ao carregar o certificado!", e);
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao ler o conteúdo do certificado!", e);
    }
  }

  /**
   * Gera uma String contendo os detalhes dos certificados dentro do KeyStore.<br>
   * Itera todos os certificados encontrados e passa para o método {@link #writeCertDetails(Certificate)}, verifique os tipos de certificados suportados pelo método.
   *
   * @param keyStore KeyStore com os certificados para obtenção dos detalhes.
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
   * Este método verifica o tipo de certificado para conseguir extrair as informações, atualmente o método tem suporte aos certificados do tipo:<br>
   * <li>X509Certificate
   *
   * @param certificate Certificado para impressão de seus atributos
   * @return String com seu conteúdo.
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
   * Essas Keymanager podem ser usadas nas conexões SSL para autenticar a origem da conexão.<br>
   * Utiliza o Algoritimo "SunX509" como padrão do certificado.
   *
   * @param keyStore KeyStore contendo os certificados para serem utilizados na conexão.
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
   * Utiliza o Algoritimo "SunX509" como padrão do certificado.
   *
   * @param keyStore KeyStore contendo os certificados que serão confiáveis durante o HandShake da comunicação.
   * @return TrustManager pronto para ser utilizado durante a comunicação.
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
   * Cria um TrustManager para ser usado em conexões SSL a partir de cadeias de certificados carregados de um arquivo criado pelo keytool (KeyStore).<Br>
   * Melhor explicação de como usar o keytool na documentação do BISERP.
   *
   * @param in arquivo com uma ou mais cadeias de certificados para serem confiados.
   * @param pass senha do arquivo para importação dos certificados.
   * @return
   * @throws RFWException
   */
  public static TrustManager[] createTrustManager(final InputStream in, final String pass) throws RFWException {
    try {
      // Cria um novo KeyStore com o tipo de algorítimo JKS
      KeyStore truststore = KeyStore.getInstance("JKS");
      truststore.load(in, pass.toCharArray());
      try {
        // Uma vez que já foi lido, forçamos seu fechamento para garantir que não teremos vazamento de recurso, mas se falhar não ligamos, logamos mas continuamos o método.
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
   * Carrega o {@link RFWCertificate} em um KeyStore baseando-se nas configurações padrão para cada tipo de certificado suportado.
   *
   * @param certificate Objeto com as definições do certificado.
   * @return KeyStore com o certificado inserido.
   * @throws RFWException Lançado em casa de falha ao criar o KeyStore.
   */
  public static KeyStore loadKeyStore(RFWCertificate certificate) throws RFWException {
    PreProcess.requiredNonNull(certificate, "Certificado não pode ser nulo!");

    KeyStore ks = null;
    switch (certificate.getType()) {
      case A1:
        ks = loadKeyStoreA1Certificate(certificate.getCertificateFileContent(), certificate.getCertificateFilePassword());
        break;
      case KeyStore:
        ks = loadKeyStore(certificate.getCertificateFileContent(), certificate.getCertificateFilePassword());
        break;
    }

    if (ks == null) throw new RFWCriticalException("Não foi possível criar o KeyStore para o Certificado passado!", new String[] { RUReflex.printObject(certificate) });
    return ks;
  }

  /**
   * Recupera a chave privada e o certificado X.509 a partir de um {@link RFWCertificate}.
   *
   * @param certificate Instância do certificado contendo o arquivo e senha.
   * @return Um {@link KeyStore.PrivateKeyEntry} contendo a chave privada e o certificado.
   * @throws RFWException Se houver falha ao carregar o certificado ou se nenhuma chave privada for encontrada.
   */
  public static KeyStore.PrivateKeyEntry extractPrivateKey(RFWCertificate certificate) throws RFWException {
    PreProcess.requiredNonNull(certificate, "Certificado não pode ser nulo!");

    KeyStore keyStore = loadKeyStore(certificate);

    try {
      Enumeration<String> aliases = keyStore.aliases();
      while (aliases.hasMoreElements()) {
        String alias = aliases.nextElement();
        if (keyStore.isKeyEntry(alias)) {
          return (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, new KeyStore.PasswordProtection(certificate.getCertificateFilePassword().toCharArray()));
        }
      }
    } catch (KeyStoreException | NoSuchAlgorithmException | java.security.UnrecoverableEntryException e) {
      throw new RFWCriticalException("Falha ao extrair a chave privada do certificado!", e);
    }

    throw new RFWCriticalException("Nenhuma chave privada encontrada no certificado!");
  }

  /**
   * Este método retorna uma string com as informações coletadas do Certificado montadas em um Array BiDimensional String[x][y].<br>
   * Onde x tem tamanho indefinido de acordo com a quantidade de parametros encontratos no certificado; e y vai de 0 à 1, sendo 0 o título do atributo e 1 o valor do atributo.
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
   * Este método retorna o fingerprint MD5 do certificado.
   *
   * @param certificate instancia do certificado.
   * @return retorna o fingerprint do certificado, Ex: '1A:DE:60:21:DE:B1:BF:C3:D1:AD:11:F1:21:22:D7:9E'
   * @throws RFWException Lançado caso o certificado não possua algorítimo MD5, ou ocorra algum erro ao decodificar o certificado.
   */
  public static String getMD5FingerPrintFromCertificate(Certificate certificate) throws RFWException {
    /*
     * Este código foi criado com base no código fonte da ferramenta keytool fornecida junto com o java. Não sei explicar exatamente seu funcionamento nem hoje, que estou implementando, não me pergunte no futuro! Link do SourceCode usado de Base: http://www.docjar.com/html/api/sun/security/tools/KeyTool.java.html
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
      // Lançado como erro crítico porque não sei quando esse erro ocorrerá, a medida que for acontecendo vamos melhorando o tratamento do erro
      throw new RFWCriticalException("BISERP_000220", e);
    }
  }

  /**
   * Este método retorna o fingerprint SHA1 do certificado.
   *
   * @param certificate instancia do certificado.
   * @return retorna o fingerprint do certificado, Ex: '72:3A:D9:2E:1A:DE:60:21:DE:B1:BF:C3:D1:AD:11:F1:21:22:D7:9E'
   * @throws RFWException Lançado caso o certificado não possua algorítimo SHA1, ou ocorra algum erro ao decodificar o certificado.
   */
  public static String getSHA1FingerPrintFromCertificate(Certificate certificate) throws RFWException {
    /*
     * Este código foi criado com base no código fonte da ferramenta keytool fornecida junto com o java. Não sei explicar exatamente seu funcionamento nem hoje, que estou implementando, não me pergunte no futuro! Link do SourceCode usado de Base: http://www.docjar.com/html/api/sun/security/tools/KeyTool.java.html
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
      // Lançado como erro crítico porque não sei quando esse erro ocorrerá, a medida que for acontecendo vamos melhorando o tratamento do erro
      throw new RFWCriticalException("BISERP_000220", e);
    }
  }

  /**
   * Este método retorna a data de início de vigência (validade) do certificado.
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
   * Este método retorna a data de fim de vigência (validade) do certificado.
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
   * Este método retorna uma string com as informações coletadas do Certificado.
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
        // Só não escreve se não tiver fingerprint md5
      }
      try {
        buff.append("\tSHA1:").append(RUCert.getSHA1FingerPrintFromCertificate(certificate)).append('\n');
      } catch (Exception e) {
        // Só não escreve se não tiver fingerprint sha1
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
   * @param keyStore Certificado já carregado em um KeyStore.
   * @return Lista com os alias de chaves privadas encontradas, ou uma lista vazia se nenhuma chave for encontrada.
   * @throws RFWException Lançado caso não seja possível recuperar os alias ou abrir o KeyStore.
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

  /**
   * Assina um array de bytes utilizando o algoritmo SHA1withRSA, a partir de um {@link RFWCertificate}.
   *
   * <p>
   * Este método é útil para cenários em que é necessário assinar um conteúdo textual ou binário que não está em formato XML, como por exemplo o payload do QR-Code v3 OFFLINE da NFC-e.
   * </p>
   *
   * @param data Conteúdo a ser assinado, em bytes.
   * @param certificate Certificado que contém a chave privada para assinatura.
   * @return Array de bytes contendo a assinatura gerada.
   * @throws RFWException Se ocorrer qualquer falha ao extrair a chave privada ou ao gerar a assinatura.
   */
  public static byte[] signBytesSHA1withRSA(byte[] data, RFWCertificate certificate) throws RFWException {
    try {
      KeyStore.PrivateKeyEntry keyEntry = extractPrivateKey(certificate);
      PrivateKey privateKey = keyEntry.getPrivateKey();

      Signature signature = Signature.getInstance("SHA1withRSA");
      signature.initSign(privateKey);
      signature.update(data);

      return signature.sign();
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("Falha ao assinar dados com SHA1withRSA.", e);
    }
  }

  /**
   * Assina um texto utilizando o algoritmo SHA1withRSA e retorna o resultado em Base64.
   *
   * <p>
   * Este método é uma conveniência para cenários como o QR-Code v3 OFFLINE da NFC-e, onde a especificação exige a assinatura digital (RSA-SHA1) em Base64 da string formada pelos parâmetros do QR-Code.
   * </p>
   *
   * @param text Texto a ser assinado.
   * @param charset Charset utilizado para converter o texto em bytes (ex.: UTF-8).
   * @param certificate Certificado que contém a chave privada para assinatura.
   * @return Assinatura digital em Base64 (sem quebras de linha).
   * @throws RFWException Se ocorrer falha ao assinar o texto.
   */
  public static String signTextSHA1withRSA(String text, Charset charset, RFWCertificate certificate) throws RFWException {
    byte[] data = text.getBytes(charset);
    byte[] signed = signBytesSHA1withRSA(data, certificate);
    return Base64.getEncoder().encodeToString(signed);
  }

  /**
   * Versão conveniente de {@link #signTextSHA1withRSA(String, Charset, RFWCertificate)} usando UTF-8.
   *
   * @param text Texto a ser assinado.
   * @param certificate Certificado que contém a chave privada para assinatura.
   * @return Assinatura digital em Base64 (sem quebras de linha).
   * @throws RFWException Se ocorrer falha ao assinar o texto.
   */
  public static String signTextSHA1withRSA(String text, RFWCertificate certificate) throws RFWException {
    return signTextSHA1withRSA(text, StandardCharsets.UTF_8, certificate);
  }

  /**
   * Abre uma conexão HTTPS autenticada via certificado digital (mTLS), utilizando um {@link RFWCertificate}.
   *
   * <p>
   * Este método:
   * <ul>
   * <li>Carrega o certificado do usuário</li>
   * <li>Cria KeyManager e TrustManager</li>
   * <li>Inicializa um SSLContext TLS</li>
   * <li>Configura o SSLSocketFactory na conexão</li>
   * </ul>
   * </p>
   *
   * @param url Endpoint HTTPS a ser acessado
   * @param certificate Certificado digital do cliente
   * @return {@link HttpsURLConnection} pronta para uso
   * @throws RFWException Em caso de falha ao carregar ou usar o certificado
   */
  public static HttpsURLConnection openHttpsConnectionWithCertificate(URL url, RFWCertificate certificate) throws RFWException {
    return openHttpsConnectionWithCertificate(url, certificate, null);
  }

  /**
   * Abre uma conexão HTTPS autenticada via certificado digital (mTLS), utilizando um {@link RFWCertificate}.
   *
   * <p>
   * Este método:
   * <ul>
   * <li>Carrega o certificado do usuário</li>
   * <li>Cria KeyManager e TrustManager</li>
   * <li>Inicializa um SSLContext TLS</li>
   * <li>Configura o SSLSocketFactory na conexão</li>
   * </ul>
   * </p>
   *
   * @param url Endpoint HTTPS a ser acessado
   * @param certificate Certificado digital do cliente
   * @param trustManagers TrustManagers personalizados para validação do servidor (pode ser null para usar o padrão)
   * @return {@link HttpsURLConnection} pronta para uso
   * @throws RFWException Em caso de falha ao carregar ou usar o certificado
   */
  public static HttpsURLConnection openHttpsConnectionWithCertificate(URL url, RFWCertificate certificate, TrustManager[] trustManagers) throws RFWException {

    if (url == null) {
      throw new RFWCriticalException("URL não pode ser nula!");
    }
    if (certificate == null) {
      throw new RFWCriticalException("Certificado não pode ser nulo!");
    }

    try {
      // 1) Carrega KeyStore do certificado
      KeyStore ks = RUCert.loadKeyStore(certificate);

      // 2) KeyManager (autenticação mTLS)
      KeyManager[] keyManagers = RUCert.createKeyManager(ks, certificate.getCertificateFilePassword());

      // 3) TrustManager (se não informado, cria a partir do próprio KeyStore)
      TrustManager[] tm = trustManagers;
      if (tm == null) {
        tm = RUCert.createTrustManager(ks);
      }

      // 4) SSLContext
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(keyManagers, tm, null);

      // 5) Abre conexão HTTPS
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.setSSLSocketFactory(sslContext.getSocketFactory());

      return connection;

    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("Falha ao abrir conexão HTTPS com certificado digital.", e);
    }
  }

  /**
   * Cria um {@link TrustManager} que confia em QUALQUER certificado apresentado durante o handshake SSL/TLS.
   *
   * <p>
   * <b> ATENÇÃO – RISCO DE SEGURANÇA</b>
   * </p>
   * <ul>
   * <li>Este TrustManager <b>NÃO valida</b> cadeia de certificação</li>
   * <li>Este TrustManager <b>NÃO valida</b> autoridade certificadora (CA)</li>
   * <li>Este TrustManager <b>NÃO valida</b> expiração do certificado</li>
   * <li>Este TrustManager <b>NÃO protege contra ataques Man-in-the-Middle (MITM)</b></li>
   * </ul>
   *
   * <p>
   * O uso deste método é aceitável apenas em cenários específicos, como:
   * </p>
   * <ul>
   * <li>Ambientes de desenvolvimento ou homologação</li>
   * <li>Integrações com endpoints internos e controlados</li>
   * <li>Casos onde a autenticação do cliente via certificado (mTLS) é suficiente e o risco do endpoint é conhecido</li>
   * </ul>
   *
   * <p>
   * <b>NÃO UTILIZAR EM PRODUÇÃO</b> para serviços públicos, internet ou qualquer cenário onde segurança de transporte seja crítica.
   * </p>
   *
   * @return Array de {@link TrustManager} que aceita qualquer certificado
   */
  public static TrustManager[] createTrustAllTrustManager() {

    TrustManager trustAll = new javax.net.ssl.X509TrustManager() {

      @Override
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0];
      }

      @Override
      public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        // NÃO valida nada
      }

      @Override
      public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        // NÃO valida nada
      }
    };

    return new TrustManager[] { trustAll };
  }

  /**
   * Carrega o repositório de certificados do Windows (Windows-MY) em um {@link KeyStore}.
   *
   * <p>
   * Este método acessa o repositório nativo do sistema operacional Windows, o mesmo utilizado por navegadores como Chrome e Edge, permitindo que o Java utilize certificados pessoais e suas cadeias completas (ex.: ICP-Brasil).
   * </p>
   *
   * <p>
   * Casos de uso típicos:
   * <ul>
   * <li>Autenticação mTLS com certificados ICP-Brasil sem necessidade de PFX</li>
   * <li>Ambientes de desenvolvimento Windows simulando comportamento do navegador</li>
   * <li>Diagnóstico e comparação entre execução Java e acesso via browser</li>
   * </ul>
   * </p>
   *
   * <p>
   * ⚠ <b>Observações importantes:</b>
   * <ul>
   * <li>Este método funciona <b>apenas em Windows</b>.</li>
   * <li>Em Linux ou containers, deve-se usar PFX + TrustStore (JKS).</li>
   * <li>O KeyStore retornado pode conter múltiplos certificados.</li>
   * </ul>
   * </p>
   *
   * @return {@link KeyStore} carregado a partir do Windows Certificate Store (Windows-MY)
   * @throws RFWException
   *           <ul>
   *           <li>Se o KeyStore não puder ser inicializado</li>
   *           <li>Se o ambiente não suportar Windows-MY</li>
   *           </ul>
   */
  public static KeyStore loadWindowsMyKeyStore() throws RFWException {
    try {
      KeyStore keyStore = KeyStore.getInstance("Windows-MY");
      keyStore.load(null, null);
      return keyStore;
    } catch (Exception e) {
      throw new RFWCriticalException("Falha ao carregar o repositório de certificados do Windows (Windows-MY)!", e);
    }
  }

}
