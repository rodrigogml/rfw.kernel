package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
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
    File file = createFileInTemporaryPathWithDelete(fileName, delayToDelete); // Exclui em 5 minutos
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

}
