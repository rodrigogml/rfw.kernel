package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.dataformatters.LocaleConverter;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWWarningException;

/**
 * Description: M�todos utilit�rios para gerenciar arquivos, escrever, ler, etc.<br>
 *
 * @author Rodrigo Leit�o
 * @since 3.1.0 (NOV / 2009)
 */
public class RUFile {

  /**
   * Verifica a exist�ncia de um arquivo. Simplesmente redireciona para o m�todo {@link File#exists()}
   *
   * @param filename Caminho completo para o arquivo, ser� utilizado no construtor de {@link File}
   * @return boolean informando se o arquivo existe ou n�o.
   */
  public static boolean fileExists(String filename) {
    return fileExists(new File(filename));
  }

  /**
   * Verifica a exist�ncia de um arquivo. Simplesmente redireciona para o m�todo {@link File#exists()}
   *
   * @param file Objeto File j� montado com a refer�ncia para o arquivo, que chama o m�todo diretamente no objeto.
   * @return boolean informando se o arquivo existe ou n�o.
   */
  public static boolean fileExists(File file) {
    return file.exists();
  }

  /**
   * Escreve o conte�do de texto em um arquivo de forma bin�ria.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conte�do a ser escrito dentro do arquivo.
   * @throws RFWException
   */
  public static void writeFileContent(File file, byte[] fileContent) throws RFWException {
    try (FileOutputStream writer = new FileOutputStream(file)) {
      writer.write(fileContent);
    } catch (Throwable e) {
      throw new RFWWarningException("N�o foi poss�vel abrir o aquivo para escrita: '${0}'", new String[] { file.getAbsolutePath() }, e);
    }
  }

  /**
   * Escreve o conte�do de texto em um arquivo de forma bin�ria.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conte�do a ser escrito dentro do arquivo.
   * @param append Indica se devemos anexar o conte�do no conte�do j� existente do arquivo (true) ou se devemos sobreescrever o conte�do atual (false).
   * @throws RFWException
   */
  public static void writeFileContent(File file, byte[] fileContent, boolean append) throws RFWException {
    try (FileOutputStream writer = new FileOutputStream(file, append)) {
      writer.write(fileContent);
    } catch (Throwable e) {
      throw new RFWWarningException("N�o foi poss�vel abrir o aquivo para escrita: '${0}'", new String[] { file.getAbsolutePath() }, e);
    }
  }

  /**
   * Escreve o conte�do de texto em um arquivo existente utilizando {@link StandardCharsets#UTF_8}.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conte�do a ser escrito dentro do arquivo
   * @throws RFWException
   */
  public static void writeFileContent(File file, String filecontent) throws RFWException {
    writeFileContent(file, filecontent, false, StandardCharsets.UTF_8);
  }

  /**
   * Escreve o conte�do de texto em um arquivo utilizando o {@link StandardCharsets#UTF_8}.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conte�do a ser escrito dentro do arquivo.
   * @param append Indica se devemos anexar o conte�do no conte�do j� existente do arquivo (true) ou se devemos sobreescrever o conte�do atual (false).
   * @throws RFWException
   */
  public static void writeFileContent(File file, String fileContent, boolean append) throws RFWException {
    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8)) {
      writer.write(fileContent);
    } catch (Throwable e) {
      throw new RFWWarningException("N�o foi poss�vel abrir o aquivo para escrita: '${0}'", new String[] { file.getAbsolutePath() }, e);
    }
  }

  /**
   * Escreve o conte�do de texto em um arquivo.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conte�do a ser escrito dentro do arquivo
   * @param append Indica se devemos anexar o conte�do no conte�do j� existente do arquivo (true) ou se devemos sobreescrever o conte�do atual (false).
   * @param charset Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @throws RFWException
   */
  public static void writeFileContent(File file, String fileContent, boolean append, Charset charset) throws RFWException {
    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file, append), charset)) {
      writer.write(fileContent);
    } catch (Throwable e) {
      throw new RFWWarningException("N�o foi poss�vel abrir o aquivo para escrita: '${0}'", new String[] { file.getAbsolutePath() }, e);
    }
  }

  /**
   * Escreve o conte�do de texto em um arquivo existente.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conte�do a ser escrito dentro do arquivo
   * @param charset Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @throws RFWException
   */
  public static void writeFileContent(File file, String filecontent, Charset charset) throws RFWException {
    writeFileContent(file, filecontent, false, charset);
  }

  public static void writeFileContent(String filename, byte[] filecontent) throws RFWException {
    writeFileContent(filename, filecontent, false);
  }

  public static void writeFileContent(String filename, byte[] filecontent, boolean append) throws RFWException {
    FileOutputStream o = null;
    try {
      o = new FileOutputStream(filename, append);
    } catch (IOException ex) {
      throw new RFWWarningException("N�o foi poss�vel abrir o aquivo para escrita: '${0}'", new String[] { filename }, ex);
    }
    try {
      o.write(filecontent);
    } catch (IOException ex) {
      throw new RFWWarningException("RFWERP_000065", new String[] { filename });
    } finally {
      if (o != null) {
        try {
          o.flush();
          o.close();
        } catch (IOException e) {
        }
      }
    }
  }

  public static void writeFileContent(String filename, String filecontent) throws RFWException {
    writeFileContent(new File(filename), filecontent);
  }

  /**
   * Escreve/Anexa o conte�do de texto em um arquivo utilizando o {@link StandardCharsets#UTF_8}
   *
   * @param fileName Arquivo para Escrita
   * @param fileContent Conte�do a ser escrito dentro do arquivo
   * @param append Indica se devemos anexar o conte�do no conte�do j� existente do arquivo (true) ou se devemos sobreescrever o conte�do atual (false).
   * @param charset Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @throws RFWException
   */
  public static void writeFileContent(String filename, String filecontent, boolean append) throws RFWException {
    writeFileContent(new File(filename), filecontent, append, StandardCharsets.UTF_8);
  }

  /**
   * Escreve/Anexa o conte�do de texto em um arquivo.
   *
   * @param fileName Arquivo para Escrita
   * @param fileContent Conte�do a ser escrito dentro do arquivo
   * @param append Indica se devemos anexar o conte�do no conte�do j� existente do arquivo (true) ou se devemos sobreescrever o conte�do atual (false).
   * @param charset Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @throws RFWException
   */
  public static void writeFileContent(String fileName, String filecontent, boolean append, Charset charset) throws RFWException {
    writeFileContent(new File(fileName), filecontent, append, charset);
  }

  /**
   * Escreve o conte�do de um arquivo em uma pasta tempor�ria com op��o de exclus�o.
   *
   * @param fileName Nome do Arquivo com a extens�o. Ex.: "meuarquivo.txt"
   * @param fileContent Conte�do a ser escrito dentro do arquivo.
   * @param delayToDelete define um tempo em milisegundos antes de excluir o arquivo. Se passado um valor negativo, o arquivo ser� excluindo quando a VM for finalizada (desde que n�o finalize abortando por erro ou fechamento for�ado pelo SO). Se a aplica��o finalizar antes do tempo passado, o arquivo � exclu�do no fechamento da aplica��o.
   * @return File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException
   */
  public static File writeFileContentInTemporaryPathWithDelete(String fileName, byte[] fileContent, long delayToDelete) throws RFWException {
    File file = createFileInTemporaryPathWithDelete(fileName, delayToDelete); // Exclui em 5 minutos
    writeFileContent(file, fileContent);
    return file;
  }

  /**
   * Equivalente ao m�todo {@link #createFileInTemporaryPath(String, String, Charset)} passando como charset {@link StandardCharsets#UTF_8} Cria um arquivo em uma pasta tempor�ria (em uma subpasta na pastas tempor�ria de acordo com o sistema) e escreve o conte�do no arquivo.<br>
   * <br>
   * A diferen�a entre este m�todo e o {@link #createTemporaryFile(String, String, String, Charset)} � que o nome do arquivo continuar� sendo o que o usu�rio passou, s� a pasta (caminho) � que receber� valores aleat�rios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus m�todos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); j� o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extens�o. Ex.: "meuarquivo.txt"
   * @param delayToDelete define um tempo em milisegundos antes de excluir o arquivo. Se passado um valor negativo, o arquivo ser� excluindo quando a VM for finalizada (desde que n�o finalize abortando por erro ou fechamento for�ado pelo SO). Se a aplica��o finalizar antes do tempo passado, o arquivo � exclu�do no fechamento da aplica��o.
   * @return File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException Em caso de falha durante a execu��o.
   */
  public static File createFileInTemporaryPathWithDelete(String fileName, long delayToDelete) throws RFWException {
    File file = createFileInTemporaryPath(fileName, null, StandardCharsets.UTF_8);
    if (delayToDelete >= 0) {
      RFW.runLater("### BUFile Delete Temporary File", true, delayToDelete, new Runnable() {
        @Override
        public void run() {
          file.delete();
        }
      });
    }
    file.deleteOnExit();
    return file;
  }

  /**
   * Equivalente ao m�todo {@link #createFileInTemporaryPath(String, String, Charset)} passando como charset {@link StandardCharsets#UTF_8} Cria um arquivo em uma pasta tempor�ria (em uma subpasta na pastas tempor�ria de acordo com o sistema) e escreve o conte�do no arquivo.<br>
   * <b>A diferen�a entre este m�todo e o {@link #createTemporaryFile(String, String, String, Charset)} � que o nome do arquivo continuar� sendo o que o usu�rio passou, s� a pasta (caminho) � que receber� valores aleat�rios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus m�todos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); j� o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extens�o. Ex.: "meuarquivo.txt"
   * @param charSet Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @return File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException Em caso de falha durante a execu��o.
   */
  public static File createFileInTemporaryPath(String fileName, Charset charSet) throws RFWException {
    return createFileInTemporaryPath(fileName, null, charSet);
  }

  /**
   * Cria um arquivo em uma pasta tempor�ria (em uma subpasta na pastas tempor�ria de acordo com o sistema) e escreve o conte�do no arquivo.<br>
   * <b>A diferen�a entre este m�todo e o {@link #createTemporaryFile(String, String, String, Charset)} � que o nome do arquivo continuar� sendo o que o usu�rio passou, s� a pasta (caminho) � que receber� valores aleat�rios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus m�todos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); j� o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extens�o. Ex.: "meuarquivo.txt"
   * @param content Cont�do do arquivo a ser escrito, ou null caso n�o deseje efetivamente criar o arquivo ainda.
   * @param charSet Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @return File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException Em caso de falha durante a execu��o.
   */
  public static File createFileInTemporaryPath(String fileName, String content, Charset charSet) throws RFWException {
    try {
      String mix = RUGenerators.generateString(4) + System.currentTimeMillis();
      Path path = Files.createTempDirectory(mix);
      String fullpath = path.toString();
      if (!File.separator.equals(fullpath.substring(fullpath.length() - 1, fullpath.length()))) {
        fullpath += File.separator;
      }
      File temp = new File(fullpath + fileName);
      if (content != null) writeFileContent(temp, content, charSet);
      return temp;
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao escrever o arquivo tempor�rio!", new String[] { fileName }, e);
    }
  }

  /**
   * Equivalente ao m�todo {@link #createFileInTemporaryPath(String, String, Charset)} passando como charset {@link StandardCharsets#UTF_8} Cria um arquivo em uma pasta tempor�ria (em uma subpasta na pastas tempor�ria de acordo com o sistema) e escreve o conte�do no arquivo.<br>
   * <b>A diferen�a entre este m�todo e o {@link #createTemporaryFile(String, String, String, Charset)} � que o nome do arquivo continuar� sendo o que o usu�rio passou, s� a pasta (caminho) � que receber� valores aleat�rios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus m�todos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); j� o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extens�o. Ex.: "meuarquivo.txt"
   * @param content Cont�do do arquivo a ser escrito, ou null caso n�o deseje efetivamente criar o arquivo ainda.
   * @return File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException Em caso de falha durante a execu��o.
   */
  public static File createFileInTemporaryPath(String fileName, String content) throws RFWException {
    return createFileInTemporaryPath(fileName, content, StandardCharsets.UTF_8);
  }

  /**
   * Cria um arquivo em uma pasta tempor�ria (em uma subpasta na pastas tempor�ria de acordo com o sistema) e escreve o conte�do no arquivo.<br>
   * <b>A diferen�a entre este m�todo e o {@link #createTemporaryFile(String, String, String, Charset)} � que o nome do arquivo continuar� sendo o que o usu�rio passou, s� a pasta (caminho) � que receber� valores aleat�rios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus m�todos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); j� o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extens�o. Ex.: "meuarquivo.txt"
   * @param content Cont�do do arquivo a ser escrito, ou null caso n�o deseje efetivamente criar o arquivo ainda.
   * @param charSet Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @param delayToDelete define um tempo em milisegundos antes de excluir o arquivo. Se passado um valor negativo, o arquivo ser� excluindo quando a VM for finalizada (desde que n�o finalize abortando por erro ou fechamento for�ado pelo SO). Se a aplica��o finalizar antes do tempo passado, o arquivo � exclu�do no fechamento da aplica��o.
   * @return File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException Em caso de falha durante a execu��o.
   */
  public static File createFileInTemporaryPathWithDelete(String fileName, String content, Charset charSet, long delayToDelete) throws RFWException {
    try {
      String mix = RUGenerators.generateString(4) + System.currentTimeMillis();
      Path path = Files.createTempDirectory(mix);
      String fullpath = path.toString();
      if (!File.separator.equals(fullpath.substring(fullpath.length() - 1, fullpath.length()))) {
        fullpath += File.separator;
      }
      File file = new File(fullpath + fileName);
      if (content != null) writeFileContent(file, content, charSet);

      if (delayToDelete >= 0) {
        RFW.runLater("### BUFile Delete Temporary File", true, delayToDelete, new Runnable() {
          @Override
          public void run() {
            file.delete();
          }
        });
      }
      file.deleteOnExit();

      return file;
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao escrever o arquivo tempor�rio!", new String[] { fileName }, e);
    }
  }

  /**
   * Cria um arquivo em uma pasta tempor�ria (em uma subpasta na pastas tempor�ria de acordo com o sistema) e escreve o conte�do no arquivo.<br>
   * <b>A diferen�a entre este m�todo e o {@link #createTemporaryFile(String, String, String, Charset)} � que o nome do arquivo continuar� sendo o que o usu�rio passou, s� a pasta (caminho) � que receber� valores aleat�rios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus m�todos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); j� o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extens�o. Ex.: "meuarquivo.txt"
   * @param content Cont�do do arquivo a ser escrito, ou null caso n�o deseje efetivamente criar o arquivo ainda.
   * @return File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException Em caso de falha durante a execu��o.
   */
  public static File createFileInTemporaryPath(String fileName, byte[] content) throws RFWException {
    try {
      String mix = RUGenerators.generateString(4) + System.currentTimeMillis();
      Path path = Files.createTempDirectory(mix);
      String fullpath = path.toString();
      if (!File.separator.equals(fullpath.substring(fullpath.length() - 1, fullpath.length()))) {
        fullpath += File.separator;
      }
      File temp = new File(fullpath + fileName);
      if (content != null) writeFileContent(temp, content, false);
      return temp;
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao escrever o arquivo tempor�rio!", new String[] { fileName }, e);
    }
  }

  /**
   * Cria um arquivo tempor�rio (na pastas tempor�ria de acordo com o sistema) e escreve o conte�do no arquivo.<br>
   * Para obter o caminho do arquivo utilize o File retornado e seus m�todos. O caminho completo para o arquivo pode ser recuperado com file.getAbsolutePath(); j� o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();<br>
   * Utiliza o Charset {@link StandardCharsets#UTF_8}.
   *
   * @param filename Nome do Arquivo
   * @param extension Extens�o do arquivo. N�o passar o "." somente a exten��o. Ex: "txt", "log", etc...
   * @param content Cont�do do arquivo.
   * @return Objeto File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException Em caso de falha durante a execu��o.
   */
  public static File createTemporaryFile(String filename, String extension, String content) throws RFWException {
    try {
      File temp = File.createTempFile(filename, "." + extension);
      if (content != null) writeFileContent(temp, content, StandardCharsets.UTF_8);
      return temp;
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao escrever o arquivo tempor�rio!", new String[] { filename, extension }, e);
    }
  }

  /**
   * Cria um arquivo tempor�rio (na pastas tempor�ria de acordo com o sistema) e escreve o conte�do no arquivo.<br>
   * Para obter o caminho do arquivo utilize o File retornado e seus m�todos. O caminho completo para o arquivo pode ser recuperado com file.getAbsolutePath(); j� o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param filename Nome do Arquivo
   * @param extension Extens�o do arquivo. N�o passar o "." somente a exten��o. Ex: "txt", "log", etc...
   * @param content Cont�do do arquivo.
   * @param charset Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @return Objeto File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException Em caso de falha durante a execu��o.
   */
  public static File createTemporaryFile(String filename, String extension, String content, Charset charset) throws RFWException {
    try {
      File temp = File.createTempFile(filename, "." + extension);
      if (content != null) writeFileContent(temp, content, charset);
      return temp;
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao escrever o arquivo tempor�rio!", new String[] { filename, extension }, e);
    }
  }

  /**
   * Equivalente ao m�todo {@link #createFileInTemporaryPath(String, String, Charset)} passando como charset {@link StandardCharsets#UTF_8} Cria um arquivo em uma pasta tempor�ria (em uma subpasta na pastas tempor�ria de acordo com o sistema) e escreve o conte�do no arquivo.<br>
   * <b>A diferen�a entre este m�todo e o {@link #createTemporaryFile(String, String, String, Charset)} � que o nome do arquivo continuar� sendo o que o usu�rio passou, s� a pasta (caminho) � que receber� valores aleat�rios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus m�todos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); j� o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extens�o. Ex.: "meuarquivo.txt"
   * @return File represetando o arquivo, usado para escrever o conte�do.
   * @throws RFWException Em caso de falha durante a execu��o.
   */
  public static File createFileInTemporaryPath(String fileName) throws RFWException {
    return createFileInTemporaryPath(fileName, null, StandardCharsets.UTF_8);
  }

  /**
   * Extrai o nome do arquivo de um caminho completo recebido.
   *
   * @param file Caminho com o nome do arquivo
   * @return
   */
  public static String extractFileExtension(File file) {
    return extractFileExtension(file.getAbsolutePath());
  }

  /**
   * Extrai o nome do arquivo de um caminho completo recebido.
   *
   * @param file Caminho com o nome do arquivo
   * @return
   */
  public static String extractFileExtension(String file) {
    return file.substring(file.lastIndexOf('.') + 1);
  }

  /**
   * Obt�m os arquivos de uma pasta/diret�rio e retorna o caminho completo (absolutePath) dos elementos encontrados.<br>
   * Utiliza o m�todo {@link #getFilesFromDirectory(String)} e chama o m�todo {@link File#getAbsolutePath()} de cada elemento.
   *
   * @param path Caminho da Pasta/Diret�rio para obter os elementos.
   * @return Array com os caminhos absolutos dos arquivos de um diret�rio.
   */
  public static String[] getFileNamesFromDirectory(String path) {
    File[] listOfFiles = getFilesFromDirectory(path);
    return Arrays.stream(listOfFiles).map(File::getAbsolutePath).toArray(String[]::new);
  }

  /**
   * Recupera todos os arquivos de um diret�rio j� representados pelo objeto {@link File}.
   *
   * @param path Caminho da pasta/diret�rio.
   * @return Array com os objetos {@link File} representando os elementos encontrados.
   */
  public static File[] getFilesFromDirectory(String path) {
    return new File(path).listFiles();
  }

  /**
   * L� o conte�do de um arquivo para uma String.
   *
   * @param fileName Arquivo para ser lido.
   * @param charset Charset do arquivo para interpreta��o do byte.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(String fileName) throws RFWException {
    File file = new File(fileName);
    return readFileContentToString(file);
  }

  /**
   * L� o conte�do de um arquivo para uma String.
   *
   * @param file Arquivo para ser lido.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(File file) throws RFWException {
    return new String(RUFile.readFileContent(file));
  }

  /**
   * L� o conte�do de um arquivo para uma String.
   *
   * @param fileName Arquivo para ser lido.
   * @param charset Charset do arquivo para interpreta��o do byte.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(String fileName, String charset) throws RFWException {
    File file = new File(fileName);
    return readFileContentToString(file, charset);
  }

  /**
   * L� o conte�do de um arquivo para uma String.
   *
   * @param fileName Arquivo para ser lido.
   * @param charset Charset do arquivo para interpreta��o do byte.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(String fileName, Charset charset) throws RFWException {
    File file = new File(fileName);
    return readFileContentToString(file, charset);
  }

  /**
   * L� o conte�do de um arquivo para uma String.
   *
   * @param file Arquivo para ser lido.
   * @param charset Charset do arquivo para interpreta��o do byte.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(File file, Charset charset) throws RFWException {
    return new String(RUFile.readFileContent(file), charset);
  }

  /**
   * L� o conte�do de um arquivo para uma String.
   *
   * @param file Arquivo para ser lido.
   * @param charset Charset do arquivo para interpreta��o do byte.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(File file, String charset) throws RFWException {
    try {
      return new String(RUFile.readFileContent(file), charset);
    } catch (UnsupportedEncodingException e) {
      throw new RFWCriticalException("RFW_ERR_200332");
    }
  }

  /**
   * L� o conte�do de um arquivo para um array de bytes.
   *
   * @param file O arquivo a ser lido.
   * @return Um array de bytes contendo o conte�do do arquivo.
   * @throws RFWException Se houver um erro ao ler o arquivo.
   */
  public static byte[] readFileContent(String fileName) throws RFWException {
    return readFileContent(new File(fileName));
  }

  /**
   * L� o conte�do de um arquivo para um array de bytes.
   *
   * @param file O arquivo a ser lido.
   * @return Um array de bytes contendo o conte�do do arquivo.
   * @throws RFWException Se houver um erro ao ler o arquivo.
   */
  public static byte[] readFileContent(File file) throws RFWException {
    if (!file.exists()) {
      throw new RFWValidationException("RFW_000027", new String[] { file.getPath() });
    }
    if (!file.canRead()) {
      throw new RFWValidationException("RFW_000028", new String[] { file.getPath() });
    }
    if (file.length() > Integer.MAX_VALUE) {
      throw new RFWValidationException("RFW_000030", new String[] { LocaleConverter.formatBytesSize(file.length(), null, 1) });
    }
    // L� o conte�do do arquivo para um array de bytes.
    byte[] bytes = new byte[(int) file.length()];
    try (FileInputStream in = new FileInputStream(file)) {
      in.read(bytes);
    } catch (IOException e) {
      throw new RFWValidationException("RFW_000031", new String[] { file.getPath() });
    }
    return bytes;
  }
}