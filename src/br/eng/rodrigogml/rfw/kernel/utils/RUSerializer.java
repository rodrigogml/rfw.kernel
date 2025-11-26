package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe com métodos para auxiliar na serialização e desserialização de objetos.<br>
 *
 * @author Rodrigo Leitão
 * @since 7.1.0 (30/06/2015)
 */
public class RUSerializer {

  /**
   * Serializa um objeto qualquer para um arquivo.
   *
   * @param obj Objeto a ser serializado.
   * @param filepath Caminho + nome do arquivo + extensão à receber o arquivo serializado.
   * @throws RFWException Lançado caso falhe em serializar o objeto.
   */
  public static void serializeToFile(Object obj, String filepath) throws RFWException {
    try (final FileOutputStream out = new FileOutputStream(filepath)) {
      try (final ObjectOutputStream oo = new ObjectOutputStream(out)) {
        oo.writeObject(obj);
        oo.flush();
      }
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200330", e);
    }
  }

  /**
   * Serializa um objeto qualquer para um OutputStream.
   *
   * @param obj Objeto a ser serializado.
   * @param stream OutputStream para escrever os bytes do conteúdo da serialização.
   * @throws RFWException Lançado caso falhe em serializar o objeto.
   */
  public static byte[] serializeToByteArray(Object obj) throws RFWException {
    try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
      serializeToOutputStream(obj, stream);
      return stream.toByteArray();
    } catch (IOException e) {
      throw new RFWCriticalException("RFW_ERR_900005", e);
    }
  }

  /**
   * Serializa um objeto qualquer para um OutputStream.
   *
   * @param obj Objeto a ser serializado.
   * @param stream OutputStream para escrever os bytes do conteúdo da serialização.
   * @throws RFWException Lançado caso falhe em serializar o objeto.
   */
  public static void serializeToOutputStream(Object obj, OutputStream stream) throws RFWException {
    try (ObjectOutputStream oo = new ObjectOutputStream(stream)) {
      oo.writeObject(obj);
      oo.flush();
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_900006", e);
    }
  }

  /**
   * Desserializa o objeto do arquivo.
   *
   * @param filePath Caminho completo do arquivo que contém o objeto.
   * @return Objeto desserializado.
   * @throws RFWException Lançado em caso de falha ao realizar a operação.
   */
  public static Object desserializeFromFile(String filePath) throws RFWException {
    final File file = new File(filePath);
    return desserializeFromFile(file);
  }

  /**
   * Desserializa o objeto do arquivo.
   *
   * @param filepath Caminho completo do arquivo que contém o objeto.
   * @return Objeto desserializado.
   * @throws RFWException Lançado em caso de falha ao realizar a operação.
   */
  public static Object desserializeFromFile(File file) throws RFWException {
    Object obj = null;
    if (file.exists()) {
      try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
        obj = input.readObject();
        input.close();
      } catch (Exception e) {
        throw new RFWCriticalException("RFW_ERR_200331", e);
      }
    }
    return obj;
  }

  /**
   * Desserializa o objeto de um array de bytes.
   *
   * @param filepath Caminho completo do arquivo que contém o objeto.
   * @return Objeto desserializado.
   * @throws RFWException Lançado em caso de falha ao realizar a operação.
   */
  public static Object desserializeFromByteArray(byte[] data) throws RFWException {
    return desserializeFromStream(new ByteArrayInputStream(data));
  }

  /**
   * Desserializa o objeto do arquivo.
   *
   * @param filepath Caminho completo do arquivo que contém o objeto.
   * @return Objeto desserializado.
   * @throws RFWException Lançado em caso de falha ao realizar a operação.
   */
  public static Object desserializeFromStream(InputStream in) throws RFWException {
    Object obj = null;
    try (ObjectInputStream input = new ObjectInputStream(in)) {
      obj = input.readObject();
      input.close();
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200331", e);
    }
    return obj;
  }

  /**
   * Serializa um objeto para XML utilizando JAXB. Mantém o comportamento original: XML formatado e documento completo (sem fragment).
   *
   * @param object Objeto a ser serializado.
   * @param clazz Classe do objeto.
   * @return XML formatado como String.
   * @throws RFWException Em caso de falha na serialização.
   */
  public static String serializeToXML(Object object, Class<?> clazz) throws RFWException {
    return serializeToXMLInternal(object, clazz, true, false, "UTF-8", null, null);
  }

  /**
   * Serializa um objeto para XML utilizando JAXB, permitindo definir se a saída deve ser formatada (pretty-print) ou não.
   *
   * @param object Objeto a ser serializado.
   * @param clazz Classe raiz do objeto.
   * @param formatted Se true, gera XML indentado e com quebras de linha.
   * @return XML gerado como String.
   * @throws RFWException Em caso de falha na serialização.
   */
  public static String serializeToXML(Object object, Class<?> clazz, boolean formatted) throws RFWException {
    return serializeToXMLInternal(object, clazz, formatted, false, "UTF-8", null, null);
  }

  /**
   * Serializa um objeto para XML usando JAXB, com parâmetros completos de configuração.<br>
   *
   * @param object Objeto a ser serializado.
   * @param clazz Classe raiz usada para o JAXBContext.
   * @param formatted Se true, o XML será gerado com indentação e quebras de linha.
   * @param fragment Se true, gera XML em modo fragmento (sem prólogo e podendo suprimir raiz). Este modo é equivalente ao comportamento antigo do método writeXMLFromObject.
   * @param encoding Codificação desejada (ex.: "UTF-8"). Se null, assume UTF-8.
   * @param schemaLocation Valor para xsi:schemaLocation (opcional).
   * @param noNamespaceSchemaLocation Valor para xsi:noNamespaceSchemaLocation (opcional).
   * @return XML serializado como String.
   * @throws RFWException Em caso de falha na serialização.
   */
  private static String serializeToXMLInternal(Object object, Class<?> clazz, boolean formatted, boolean fragment, String encoding, String schemaLocation, String noNamespaceSchemaLocation) throws RFWException {
    PreProcess.requiredNonNull(object);
    try {
      JAXBContext jc = JAXBContext.newInstance(clazz);
      Marshaller m = jc.createMarshaller();

      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
      m.setProperty(Marshaller.JAXB_FRAGMENT, fragment);
      if (encoding == null) {
        encoding = "UTF-8";
      }
      m.setProperty(Marshaller.JAXB_ENCODING, encoding);
      if (schemaLocation != null) {
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
      }
      if (noNamespaceSchemaLocation != null) {
        m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, noNamespaceSchemaLocation);
      }
      try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        m.marshal(object, os);
        return new String(os.toByteArray(), encoding);
      }
    } catch (Throwable e) {
      throw new RFWCriticalException("RFW_ERR_200492", e);
    }
  }

  /**
   * Desserializa um objeto a partir do seu XML utilizando o padrão JAXB do nativo do Java.
   *
   * @param xml String contendo o XML para desserialização.
   * @param clazz Classe do Objeto que será montado a partir do XML.
   * @throws RFWException
   */
  public static Object desserializeFromXML(String xml, Class<?> clazz) throws RFWException {
    try {
      JAXBContext jc = JAXBContext.newInstance(clazz);
      Unmarshaller m = jc.createUnmarshaller();
      Object r = m.unmarshal(new StringReader(xml));
      return r;
    } catch (Throwable e) {
      throw new RFWCriticalException("RFW_ERR_200492", e);
    }
  }

}
