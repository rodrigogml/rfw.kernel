package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.dataformatters.LocaleConverter;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWWarningException;

/**
 * Description: Métodos utilitários para gerenciar arquivos, escrever, ler, etc.<br>
 *
 * @author Rodrigo Leitão
 * @since 3.1.0 (NOV / 2009)
 */
public class RUFile {

  /**
   * Verifica a existência de um arquivo. Simplesmente redireciona para o método {@link File#exists()}
   *
   * @param filename Caminho completo para o arquivo, será utilizado no construtor de {@link File}
   * @return boolean informando se o arquivo existe ou não.
   */
  public static boolean fileExists(String filename) {
    return fileExists(new File(filename));
  }

  /**
   * Verifica a existência de um arquivo. Simplesmente redireciona para o método {@link File#exists()}
   *
   * @param file Objeto File já montado com a referência para o arquivo, que chama o método diretamente no objeto.
   * @return boolean informando se o arquivo existe ou não.
   */
  public static boolean fileExists(File file) {
    return file.exists();
  }

  /**
   * Escreve o conteúdo de texto em um arquivo de forma binária.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conteúdo a ser escrito dentro do arquivo.
   * @throws RFWException
   */
  public static void writeFileContent(File file, byte[] fileContent) throws RFWException {
    try (FileOutputStream writer = new FileOutputStream(file)) {
      writer.write(fileContent);
    } catch (Throwable e) {
      throw new RFWWarningException("Não foi possível abrir o aquivo para escrita: '${0}'", new String[] { file.getAbsolutePath() }, e);
    }
  }

  /**
   * Escreve o conteúdo de texto em um arquivo de forma binária.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conteúdo a ser escrito dentro do arquivo.
   * @param append Indica se devemos anexar o conteúdo no conteúdo já existente do arquivo (true) ou se devemos sobreescrever o conteúdo atual (false).
   * @throws RFWException
   */
  public static void writeFileContent(File file, byte[] fileContent, boolean append) throws RFWException {
    try (FileOutputStream writer = new FileOutputStream(file, append)) {
      writer.write(fileContent);
    } catch (Throwable e) {
      throw new RFWWarningException("Não foi possível abrir o aquivo para escrita: '${0}'", new String[] { file.getAbsolutePath() }, e);
    }
  }

  /**
   * Escreve o conteúdo de texto em um arquivo existente utilizando {@link StandardCharsets#UTF_8}.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conteúdo a ser escrito dentro do arquivo
   * @throws RFWException
   */
  public static void writeFileContent(File file, String filecontent) throws RFWException {
    writeFileContent(file, filecontent, false, StandardCharsets.UTF_8);
  }

  /**
   * Escreve o conteúdo de texto em um arquivo utilizando o {@link StandardCharsets#UTF_8}.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conteúdo a ser escrito dentro do arquivo.
   * @param append Indica se devemos anexar o conteúdo no conteúdo já existente do arquivo (true) ou se devemos sobreescrever o conteúdo atual (false).
   * @throws RFWException
   */
  public static void writeFileContent(File file, String fileContent, boolean append) throws RFWException {
    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8)) {
      writer.write(fileContent);
    } catch (Throwable e) {
      throw new RFWWarningException("Não foi possível abrir o aquivo para escrita: '${0}'", new String[] { file.getAbsolutePath() }, e);
    }
  }

  /**
   * Escreve o conteúdo de texto em um arquivo.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conteúdo a ser escrito dentro do arquivo
   * @param append Indica se devemos anexar o conteúdo no conteúdo já existente do arquivo (true) ou se devemos sobreescrever o conteúdo atual (false).
   * @param charset Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @throws RFWException
   */
  public static void writeFileContent(File file, String fileContent, boolean append, Charset charset) throws RFWException {
    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file, append), charset)) {
      writer.write(fileContent);
    } catch (Throwable e) {
      throw new RFWWarningException("Não foi possível abrir o aquivo para escrita: '${0}'", new String[] { file.getAbsolutePath() }, e);
    }
  }

  /**
   * Escreve o conteúdo de texto em um arquivo existente.
   *
   * @param file Arquivo para Escrita
   * @param fileContent Conteúdo a ser escrito dentro do arquivo
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
      throw new RFWWarningException("Não foi possível abrir o aquivo para escrita: '${0}'", new String[] { filename }, ex);
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
   * Escreve/Anexa o conteúdo de texto em um arquivo utilizando o {@link StandardCharsets#UTF_8}
   *
   * @param fileName Arquivo para Escrita
   * @param fileContent Conteúdo a ser escrito dentro do arquivo
   * @param append Indica se devemos anexar o conteúdo no conteúdo já existente do arquivo (true) ou se devemos sobreescrever o conteúdo atual (false).
   * @param charset Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @throws RFWException
   */
  public static void writeFileContent(String filename, String filecontent, boolean append) throws RFWException {
    writeFileContent(new File(filename), filecontent, append, StandardCharsets.UTF_8);
  }

  /**
   * Escreve/Anexa o conteúdo de texto em um arquivo.
   *
   * @param fileName Arquivo para Escrita
   * @param fileContent Conteúdo a ser escrito dentro do arquivo
   * @param append Indica se devemos anexar o conteúdo no conteúdo já existente do arquivo (true) ou se devemos sobreescrever o conteúdo atual (false).
   * @param charset Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @throws RFWException
   */
  public static void writeFileContent(String fileName, String filecontent, boolean append, Charset charset) throws RFWException {
    writeFileContent(new File(fileName), filecontent, append, charset);
  }

  /**
   * Escreve o conteúdo de um arquivo em uma pasta temporária com opção de exclusão.
   *
   * @param fileName Nome do Arquivo com a extensão. Ex.: "meuarquivo.txt"
   * @param fileContent Conteúdo a ser escrito dentro do arquivo.
   * @param delayToDelete define um tempo em milisegundos antes de excluir o arquivo. Se passado um valor negativo, o arquivo será excluindo quando a VM for finalizada (desde que não finalize abortando por erro ou fechamento forçado pelo SO). Se a aplicação finalizar antes do tempo passado, o arquivo é excluído no fechamento da aplicação.
   * @return File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException
   */
  public static File writeFileContentInTemporaryPathWithDelete(String fileName, byte[] fileContent, long delayToDelete) throws RFWException {
    File file = createFileInTemporaryPathWithDelete(fileName, delayToDelete);
    writeFileContent(file, fileContent);
    return file;
  }

  /**
   * Equivalente ao método {@link #createFileInTemporaryPath(String, String, Charset)} passando como charset {@link StandardCharsets#UTF_8} Cria um arquivo em uma pasta temporária (em uma subpasta na pastas temporária de acordo com o sistema) e escreve o conteúdo no arquivo.<br>
   * <br>
   * A diferença entre este método e o {@link #createTemporaryFile(String, String, String, Charset)} é que o nome do arquivo continuará sendo o que o usuário passou, só a pasta (caminho) é que receberá valores aleatórios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus métodos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); já o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extensão. Ex.: "meuarquivo.txt"
   * @param delayToDelete define um tempo em milisegundos antes de excluir o arquivo. Se passado um valor negativo, o arquivo será excluindo quando a VM for finalizada (desde que não finalize abortando por erro ou fechamento forçado pelo SO). Se a aplicação finalizar antes do tempo passado, o arquivo é excluído no fechamento da aplicação.
   * @return File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException Em caso de falha durante a execução.
   */
  public static File createFileInTemporaryPathWithDelete(String fileName, long delayToDelete) throws RFWException {
    File file = createFileInTemporaryPath(fileName, null, StandardCharsets.UTF_8);
    if (delayToDelete >= 0) {
      RFW.runLater("### RUFile Delete Temporary File", true, delayToDelete, new Runnable() {
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
   * Equivalente ao método {@link #createFileInTemporaryPath(String, String, Charset)} passando como charset {@link StandardCharsets#UTF_8} Cria um arquivo em uma pasta temporária (em uma subpasta na pastas temporária de acordo com o sistema) e escreve o conteúdo no arquivo.<br>
   * <b>A diferença entre este método e o {@link #createTemporaryFile(String, String, String, Charset)} é que o nome do arquivo continuará sendo o que o usuário passou, só a pasta (caminho) é que receberá valores aleatórios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus métodos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); já o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extensão. Ex.: "meuarquivo.txt"
   * @param charSet Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @return File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException Em caso de falha durante a execução.
   */
  public static File createFileInTemporaryPath(String fileName, Charset charSet) throws RFWException {
    return createFileInTemporaryPath(fileName, null, charSet);
  }

  /**
   * Cria um arquivo em uma pasta temporária (em uma subpasta na pastas temporária de acordo com o sistema) e escreve o conteúdo no arquivo.<br>
   * <b>A diferença entre este método e o {@link #createTemporaryFile(String, String, String, Charset)} é que o nome do arquivo continuará sendo o que o usuário passou, só a pasta (caminho) é que receberá valores aleatórios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus métodos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); já o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extensão. Ex.: "meuarquivo.txt"
   * @param content Contúdo do arquivo a ser escrito, ou null caso não deseje efetivamente criar o arquivo ainda.
   * @param charSet Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @return File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException Em caso de falha durante a execução.
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
      throw new RFWCriticalException("Falha ao escrever o arquivo temporário!", new String[] { fileName }, e);
    }
  }

  /**
   * Equivalente ao método {@link #createFileInTemporaryPath(String, String, Charset)} passando como charset {@link StandardCharsets#UTF_8} Cria um arquivo em uma pasta temporária (em uma subpasta na pastas temporária de acordo com o sistema) e escreve o conteúdo no arquivo.<br>
   * <b>A diferença entre este método e o {@link #createTemporaryFile(String, String, String, Charset)} é que o nome do arquivo continuará sendo o que o usuário passou, só a pasta (caminho) é que receberá valores aleatórios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus métodos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); já o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extensão. Ex.: "meuarquivo.txt"
   * @param content Contúdo do arquivo a ser escrito, ou null caso não deseje efetivamente criar o arquivo ainda.
   * @return File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException Em caso de falha durante a execução.
   */
  public static File createFileInTemporaryPath(String fileName, String content) throws RFWException {
    return createFileInTemporaryPath(fileName, content, StandardCharsets.UTF_8);
  }

  /**
   * Cria um arquivo em uma pasta temporária (em uma subpasta na pastas temporária de acordo com o sistema) e escreve o conteúdo no arquivo.<br>
   * <b>A diferença entre este método e o {@link #createTemporaryFile(String, String, String, Charset)} é que o nome do arquivo continuará sendo o que o usuário passou, só a pasta (caminho) é que receberá valores aleatórios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus métodos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); já o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extensão. Ex.: "meuarquivo.txt"
   * @param content Contúdo do arquivo a ser escrito, ou null caso não deseje efetivamente criar o arquivo ainda.
   * @param charSet Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @param delayToDelete define um tempo em milisegundos antes de excluir o arquivo. Se passado um valor negativo, o arquivo será excluindo quando a VM for finalizada (desde que não finalize abortando por erro ou fechamento forçado pelo SO). Se a aplicação finalizar antes do tempo passado, o arquivo é excluído no fechamento da aplicação.
   * @return File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException Em caso de falha durante a execução.
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
      throw new RFWCriticalException("Falha ao escrever o arquivo temporário!", new String[] { fileName }, e);
    }
  }

  /**
   * Cria um arquivo em uma pasta temporária (em uma subpasta na pastas temporária de acordo com o sistema) e escreve o conteúdo no arquivo.<br>
   * <b>A diferença entre este método e o {@link #createTemporaryFile(String, String, String, Charset)} é que o nome do arquivo continuará sendo o que o usuário passou, só a pasta (caminho) é que receberá valores aleatórios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus métodos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); já o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extensão. Ex.: "meuarquivo.txt"
   * @param content Contúdo do arquivo a ser escrito, ou null caso não deseje efetivamente criar o arquivo ainda.
   * @return File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException Em caso de falha durante a execução.
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
      throw new RFWCriticalException("Falha ao escrever o arquivo temporário!", new String[] { fileName }, e);
    }
  }

  /**
   * Cria um arquivo temporário (na pastas temporária de acordo com o sistema) e escreve o conteúdo no arquivo.<br>
   * Para obter o caminho do arquivo utilize o File retornado e seus métodos. O caminho completo para o arquivo pode ser recuperado com file.getAbsolutePath(); já o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();<br>
   * Utiliza o Charset {@link StandardCharsets#UTF_8}.
   *
   * @param filename Nome do Arquivo
   * @param extension Extensão do arquivo. Não passar o "." somente a extenção. Ex: "txt", "log", etc...
   * @param content Contúdo do arquivo.
   * @return Objeto File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException Em caso de falha durante a execução.
   */
  public static File createTemporaryFile(String filename, String extension, String content) throws RFWException {
    try {
      File temp = File.createTempFile(filename, "." + extension);
      if (content != null) writeFileContent(temp, content, StandardCharsets.UTF_8);
      return temp;
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao escrever o arquivo temporário!", new String[] { filename, extension }, e);
    }
  }

  /**
   * Cria um arquivo temporário (na pastas temporária de acordo com o sistema) e escreve o conteúdo no arquivo.<br>
   * Para obter o caminho do arquivo utilize o File retornado e seus métodos. O caminho completo para o arquivo pode ser recuperado com file.getAbsolutePath(); já o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param filename Nome do Arquivo
   * @param extension Extensão do arquivo. Não passar o "." somente a extenção. Ex: "txt", "log", etc...
   * @param content Contúdo do arquivo.
   * @param charset Charset a ser utilizado na escrita do arquivo. {@link StandardCharsets}
   * @return Objeto File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException Em caso de falha durante a execução.
   */
  public static File createTemporaryFile(String filename, String extension, String content, Charset charset) throws RFWException {
    try {
      File temp = File.createTempFile(filename, "." + extension);
      if (content != null) writeFileContent(temp, content, charset);
      return temp;
    } catch (IOException e) {
      throw new RFWCriticalException("Falha ao escrever o arquivo temporário!", new String[] { filename, extension }, e);
    }
  }

  /**
   * Equivalente ao método {@link #createFileInTemporaryPath(String, String, Charset)} passando como charset {@link StandardCharsets#UTF_8} Cria um arquivo em uma pasta temporária (em uma subpasta na pastas temporária de acordo com o sistema) e escreve o conteúdo no arquivo.<br>
   * <b>A diferença entre este método e o {@link #createTemporaryFile(String, String, String, Charset)} é que o nome do arquivo continuará sendo o que o usuário passou, só a pasta (caminho) é que receberá valores aleatórios para evitar arquivos com o mesmo nome. Para obter o caminho do arquivo utilize o File retornado e seus métodos. O caminho completo para o arquivo pode ser recuperado com
   * file.getAbsolutePath(); já o caminho incluindo o arquivo pode ser obtido com file.getAbsoluteFile();
   *
   * @param fileName Nome do Arquivo com a extensão. Ex.: "meuarquivo.txt"
   * @return File represetando o arquivo, usado para escrever o conteúdo.
   * @throws RFWException Em caso de falha durante a execução.
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
   * Obtém os arquivos de uma pasta/diretório e retorna o caminho completo (absolutePath) dos elementos encontrados.<br>
   * Utiliza o método {@link #getFilesFromDirectory(String)} e chama o método {@link File#getAbsolutePath()} de cada elemento.
   *
   * @param path Caminho da Pasta/Diretório para obter os elementos.
   * @return Array com os caminhos absolutos dos arquivos de um diretório.
   */
  public static String[] getFileNamesFromDirectory(String path) {
    File[] listOfFiles = getFilesFromDirectory(path);
    return Arrays.stream(listOfFiles).map(File::getAbsolutePath).toArray(String[]::new);
  }

  /**
   * Recupera todos os arquivos de um diretório já representados pelo objeto {@link File}.
   *
   * @param path Caminho da pasta/diretório.
   * @return Array com os objetos {@link File} representando os elementos encontrados.
   */
  public static File[] getFilesFromDirectory(String path) {
    return new File(path).listFiles();
  }

  /**
   * Lê o conteúdo de um arquivo para uma String.
   *
   * @param fileName Arquivo para ser lido.
   * @param charset Charset do arquivo para interpretação do byte.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(String fileName) throws RFWException {
    File file = new File(fileName);
    return readFileContentToString(file);
  }

  /**
   * Lê o conteúdo de um arquivo para uma String.
   *
   * @param file Arquivo para ser lido.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(File file) throws RFWException {
    return new String(RUFile.readFileContent(file));
  }

  /**
   * Lê o conteúdo de um arquivo para uma String.
   *
   * @param fileName Arquivo para ser lido.
   * @param charset Charset do arquivo para interpretação do byte.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(String fileName, String charset) throws RFWException {
    File file = new File(fileName);
    return readFileContentToString(file, charset);
  }

  /**
   * Lê o conteúdo de um arquivo para uma String.
   *
   * @param fileName Arquivo para ser lido.
   * @param charset Charset do arquivo para interpretação do byte.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(String fileName, Charset charset) throws RFWException {
    File file = new File(fileName);
    return readFileContentToString(file, charset);
  }

  /**
   * Lê o conteúdo de um arquivo para uma String.
   *
   * @param file Arquivo para ser lido.
   * @param charset Charset do arquivo para interpretação do byte.
   * @return String contendo o arquivo.
   * @throws RFWException Caso ocorra algum erro na leitura do arquivo.
   */
  public static String readFileContentToString(File file, Charset charset) throws RFWException {
    return new String(RUFile.readFileContent(file), charset);
  }

  /**
   * Lê o conteúdo de um arquivo para uma String.
   *
   * @param file Arquivo para ser lido.
   * @param charset Charset do arquivo para interpretação do byte.
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
   * Lê o conteúdo de um arquivo para um array de bytes.
   *
   * @param file O arquivo a ser lido.
   * @return Um array de bytes contendo o conteúdo do arquivo.
   * @throws RFWException Se houver um erro ao ler o arquivo.
   */
  public static byte[] readFileContent(String fileName) throws RFWException {
    return readFileContent(new File(fileName));
  }

  /**
   * Lê o conteúdo de um arquivo para um array de bytes.
   *
   * @param file O arquivo a ser lido.
   * @return Um array de bytes contendo o conteúdo do arquivo.
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
    // Lê o conteúdo do arquivo para um array de bytes.
    byte[] bytes = new byte[(int) file.length()];
    try (FileInputStream in = new FileInputStream(file)) {
      in.read(bytes);
    } catch (IOException e) {
      throw new RFWValidationException("RFW_000031", new String[] { file.getPath() });
    }
    return bytes;
  }

  /**
   * Extrai o nome do arquivo de um caminho completo recebido. NÃO RETORNA A EXTENSÃO DO ARQUIVO!
   *
   * @param file Caminho com o nome do arquivo
   * @return
   */
  public static String extractFileName(String file) {
    final String name = new File(file).getName();
    return name.substring(0, name.lastIndexOf('.'));
  }

  public static boolean isDirectory(String path) {
    return new File(path).isDirectory();
  }

  public static boolean isDirectory(File path) {
    return path.isDirectory();
  }

  /**
   * Retorna os arquivos de um determinado diretório que tenham a data de criação maiores ou iguais a uma determinada data.
   *
   * @param path Caminho/Diretório dos arquivos
   * @param timemillis Data de criação inicial em milisegundos (Equivalente ao System.currentTimemillis)
   * @return Arquivos criados depois da data definida
   * @throws RFWException
   */
  public static File[] getFilesNewerOrEqualThan(String path, long timemillis) throws RFWException {
    final File[] files = getFilesFromDirectory(path);

    final LinkedList<File> newerFiles = new LinkedList<>();
    try {
      for (File file : files) {
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        if (attr.creationTime().toMillis() >= timemillis) {
          newerFiles.add(file);
        }
      }
    } catch (IOException e) {
      throw new RFWCriticalException("BISERP_000443");
    }
    return newerFiles.toArray(new File[0]);
  }

  public static String[] getDirectoryFromDirectory(String path) {
    File[] listOfFiles = getFilesFromDirectory(path);

    ArrayList<String> directories = new ArrayList<>(listOfFiles.length);

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isDirectory()) {
        directories.add(listOfFiles[i].getAbsolutePath());
      }
    }

    return directories.toArray(new String[0]);
  }

  public static String readFileContentToString(InputStream in) throws RFWException {
    return new String(readFileContent(in));
  }

  public static byte[] readFileContent(InputStream in) throws RFWException {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] tmpbytes = new byte[4096];
      int c = 0;
      while ((c = in.read(tmpbytes)) > 0) {
        out.write(tmpbytes, 0, c);
      }
      out.flush();
      return out.toByteArray();
    } catch (IOException e) {
      throw new RFWValidationException("BISERP_000062");
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
  }

  public static String readInputStreamToString(InputStream is) throws RFWException {
    return new String(readInputStreamToByteArray(is));
  }

  public static String readInputStreamToString(InputStream is, String charset) throws RFWException {
    try {
      return new String(readInputStreamToByteArray(is), charset);
    } catch (UnsupportedEncodingException e) {
      throw new RFWCriticalException("BISERP_000332");
    }
  }

  public static byte[] readInputStreamToByteArray(InputStream is) throws RFWException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int nRead;
    byte[] data = new byte[16384];

    try {
      while ((nRead = is.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }
      buffer.flush();
      buffer.close();
      return buffer.toByteArray();
    } catch (IOException e) {
      throw new RFWValidationException("BISERP_000071", new String[] { e.getMessage() });
    }
  }

  public static boolean renameFile(String original, String destination) {
    return renameFile(new File(original), new File(destination));
  }

  public static boolean renameFile(File original, File destination) {
    return original.renameTo(destination);
  }

  public static boolean deleteFile(String filename) {
    return deleteFile(new File(filename));
  }

  public static boolean deleteFile(File file) {
    if (file.exists() && file.isFile()) {
      return file.delete();
    }
    return false;
  }

  /**
   * Retorna a data e hora de criação de um arquivo.
   *
   * @param fileName Caminho completo ou relativo à aplicação que leve ao arquivo.
   * @return Objeto Date criado com o timezone padrão do sistema, que deve equivaler como do arquivo.
   * @throws RFWException
   */
  public static Date getCreationDate(String fileName) throws RFWException {
    // Se o arquivo não existir lança crítico, isso deve ser validado adequadamente fora da classe utilitária
    if (!fileExists(fileName)) throw new RFWCriticalException("BISERP_000444", new String[] { fileName });

    Path path = new File(fileName).toPath();
    BasicFileAttributes attr;
    try {
      attr = Files.readAttributes(path, BasicFileAttributes.class);
    } catch (IOException e) {
      throw new RFWCriticalException("BISERP_000443");
    }
    return new Date(attr.creationTime().toMillis());
  }

  /**
   * Retorna a data e hora de criação de um arquivo no formato LocalDateTime.
   *
   * @param fileName Caminho completo ou relativo à aplicação que leva ao arquivo.
   * @return Objeto LocalDateTime representando a data e hora de criação do arquivo, usando o timezone padrão do sistema.
   * @throws RFWException se o arquivo não for encontrado ou ocorrer um erro ao ler os atributos do arquivo.
   */
  public static LocalDateTime getCreationLocalDateTime(String fileName) throws RFWException {
    // Se o arquivo não existir lança crítico, isso deve ser validado adequadamente fora da classe utilitária
    if (!fileExists(fileName)) throw new RFWCriticalException("BISERP_000444", new String[] { fileName });

    Path path = new File(fileName).toPath();
    BasicFileAttributes attr;
    try {
      attr = Files.readAttributes(path, BasicFileAttributes.class);
    } catch (IOException e) {
      throw new RFWCriticalException("BISERP_000443");
    }

    // Converte o FileTime para LocalDateTime utilizando o timezone padrão do sistema
    return LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneOffset.UTC);
  }

  /**
   * Retorna a data e hora do último acesso ao arquivo.
   *
   * @param fileName Caminho completo ou relativo à aplicação que leve ao arquivo.
   * @return Objeto Date criado com o timezone padrão do sistema, que deve equivaler como do arquivo.
   * @throws RFWException
   */
  public static Date getLastAccessTime(String fileName) throws RFWException {
    // Se o arquivo não existir lança crítico, isso deve ser validado adequadamente fora da classe utilitária
    if (!fileExists(fileName)) throw new RFWCriticalException("BISERP_000444", new String[] { fileName });

    Path path = new File(fileName).toPath();
    BasicFileAttributes attr;
    try {
      attr = Files.readAttributes(path, BasicFileAttributes.class);
    } catch (IOException e) {
      throw new RFWCriticalException("BISERP_000443");
    }
    return new Date(attr.lastAccessTime().toMillis());
  }

  /**
   * Retorna a data e hora da última modificação do arquivo.
   *
   * @param fileName Caminho completo ou relativo à aplicação que leve ao arquivo.
   * @return Objeto Date criado com o timezone padrão do sistema, que deve equivaler como do arquivo.
   * @throws RFWException
   */
  public static Date getLastModifiedTime(String fileName) throws RFWException {
    // Se o arquivo não existir lança crítico, isso deve ser validado adequadamente fora da classe utilitária
    if (!fileExists(fileName)) throw new RFWCriticalException("BISERP_000444", new String[] { fileName });

    Path path = new File(fileName).toPath();
    BasicFileAttributes attr;
    try {
      attr = Files.readAttributes(path, BasicFileAttributes.class);
    } catch (IOException e) {
      throw new RFWCriticalException("BISERP_000443");
    }
    return new Date(attr.lastModifiedTime().toMillis());
  }

  /**
   * Retorna a data e hora da última modificação de um arquivo no formato LocalDateTime.
   *
   * @param fileName Caminho completo ou relativo à aplicação que leva ao arquivo.
   * @return Objeto LocalDateTime representando a data e hora da última modificação do arquivo, sem considerar o fuso horário.
   * @throws RFWException se o arquivo não for encontrado ou ocorrer um erro ao ler os atributos do arquivo.
   */
  public static LocalDateTime getLastModifiedLocalDateTime(String fileName) throws RFWException {
    // Se o arquivo não existir lança crítico, isso deve ser validado adequadamente fora da classe utilitária
    if (!fileExists(fileName)) throw new RFWCriticalException("BISERP_000444", new String[] { fileName });

    Path path = new File(fileName).toPath();
    BasicFileAttributes attr;
    try {
      attr = Files.readAttributes(path, BasicFileAttributes.class);
    } catch (IOException e) {
      throw new RFWCriticalException("BISERP_000443");
    }

    // Converte FileTime para LocalDateTime sem considerar o fuso horário
    return LocalDateTime.ofInstant(attr.lastModifiedTime().toInstant(), ZoneOffset.UTC);
  }

  /**
   * Retorna se o objeto do caminho passado é um Symbolic Link.
   *
   * @param fileName Caminho completo ou relativo à aplicação que leve ao arquivo.
   * @return Boolean indicando 'true' caso o objeto seja um SymLink, 'false' não seja.
   * @throws RFWException
   */
  public static boolean isSymbolicLink(String fileName) throws RFWException {
    // Se o arquivo não existir lança crítico, isso deve ser validado adequadamente fora da classe utilitária
    if (!fileExists(fileName)) throw new RFWCriticalException("BISERP_000444", new String[] { fileName });

    Path path = new File(fileName).toPath();
    BasicFileAttributes attr;
    try {
      attr = Files.readAttributes(path, BasicFileAttributes.class);
    } catch (IOException e) {
      throw new RFWCriticalException("BISERP_000443");
    }
    return attr.isSymbolicLink();
  }

  /**
   * Este método verifica a existência do caminho e, caso não exista ainda, o cria.<br>
   * <b>Este método aceita o caminho do diretório sem o nome do arquivo. Caso tenha o caminho com o nome do arquivo utilize o {@link #createPathOfFile(String)}</b>
   *
   * @param pathName Caminho para o diretório a ser criado.
   * @throws RFWException
   */
  public static void createPath(String pathName) throws RFWException {
    File file = new File(pathName);
    file.mkdirs(); // Força criar os diretórios caso não existam
  }

  /**
   * Este método verifica a existência dos diretórios do caminho ddo arquivo passado, caso não exista ainda, o cria.<br>
   * <b>Este método aceita o caminho completo incluindo o nome do arquivo, e garante que sua pasta seja criada. Caso tenha apenas os nomes dos diretórios utilize o {@link #createPath(String)}</b>
   *
   * @param filePath Caminho completo do arquivo, cujo diretório deve ser criado.
   * @throws RFWException
   */
  public static void createPathOfFile(String filePath) throws RFWException {
    File file = new File(filePath);
    file.mkdirs(); // Força criar os diretórios caso não existam
  }

  /**
   * Extrai o nome do arquivo de um caminho completo recebido.
   *
   * @param file Caminho com o nome do arquivo
   * @return
   */
  public static String extractFileFullName(String file) {
    File f = new File(file);
    return f.getName();
  }

  /**
   * Extrai o caminho (diretório) de um caminho completo recebido. NÃO RETORNA O ÚLTIMO SEPARADOR! Nem mesmo quando está na raiz: Windows = "c:", no Linux = "".
   *
   * @param file Caminho com o nome do arquivo
   * @return
   */
  public static String extractFullPath(String file) {
    return file.substring(0, file.lastIndexOf(File.separatorChar));
  }

}