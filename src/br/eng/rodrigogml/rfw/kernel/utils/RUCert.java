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
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.interfaces.RFWCertificate;
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
}
