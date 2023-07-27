package br.eng.rodrigogml.rfw.kernel.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

/**
 * Description: Esta classe implementa m�todos para encripta��o e desencripta��o de dados (quando poss�vel).<BR>
 * Classe do tipo est�tica.
 *
 * @author Rodrigo Leit�o
 * @since 4.2.0 (29/10/2011)
 */
public class RUEncrypter {

  private static final int ITERATIONCOUNT = 19;
  private static byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };

  /**
   * Construtor privado para classe exclusivamente est�tica.
   */
  private RUEncrypter() {
  }

  public static String encryptDES(String str, SecretKey key) throws RFWException {
    Cipher ecipher = null;
    try {
      ecipher = Cipher.getInstance("DES");
      ecipher.init(Cipher.ENCRYPT_MODE, key);
      // Encode the string into bytes using utf-16
      byte[] utf16 = str.getBytes("UTF16");
      // Encrypt
      byte[] enc = ecipher.doFinal(utf16);
      // Encode bytes to base64 to get a string
      return Base64.getEncoder().encodeToString(enc);
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200161", e);
    }
  }

  public static String encryptDES(String str, String passphrase) throws RFWException {
    SecretKey key = generateDESSecretKey(passphrase);
    try {
      Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
      AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATIONCOUNT);
      ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

      // Encode the string into bytes using utf-16
      byte[] utf16 = str.getBytes("UTF16");
      // Encrypt
      byte[] enc = ecipher.doFinal(utf16);
      // Encode bytes to base64 to get a string
      return Base64.getEncoder().encodeToString(enc);
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200161", e);
    }
  }

  /**
   * Somente para uso privado dos m�todos que criam a chave a partir da frase senha.<br>
   * N�o pode ser recuperada a senha para depois usar como senha nos m�todos que aceitam o KeySecret pq � necess�rio mais configura��es.
   */
  private static SecretKey generateDESSecretKey(String passphrase) throws RFWException {
    try {
      KeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt, ITERATIONCOUNT);
      SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
      return key;
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200163", e);
    }
  }

  public static SecretKey generateDESSecretKey() throws RFWException {
    try {
      return KeyGenerator.getInstance("DES").generateKey();
    } catch (NoSuchAlgorithmException e) {
      throw new RFWCriticalException("RFW_ERR_200163", e);
    }
  }

  public static String decryptDES(String str, SecretKey key) throws RFWException {
    try {
      Cipher dcipher = Cipher.getInstance("DES");
      dcipher.init(Cipher.DECRYPT_MODE, key);
      // Decode base64 to get bytes
      byte[] dec = Base64.getDecoder().decode(str);
      // Decrypt
      byte[] utf16 = dcipher.doFinal(dec);
      // Decode using utf-16
      return new String(utf16, "UTF16");
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200162", e);
    }
  }

  public static String decryptDES(String str, String passphrase) throws RFWException {
    SecretKey key = generateDESSecretKey(passphrase);
    try {
      Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
      AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATIONCOUNT);
      dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

      // Decode base64 to get bytes
      byte[] dec = Base64.getDecoder().decode(str);
      // Decrypt
      byte[] utf16 = dcipher.doFinal(dec);
      // Decode using utf-16
      return new String(utf16, "UTF16");
    } catch (Exception e) {
      throw new RFWCriticalException("Falha ao descriptografar o conte�do. Provavelmente chave errada!", e);
    }
  }

}