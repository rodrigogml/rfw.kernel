package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWWarningException;

/**
 * Description: Classe utilit�ria respons�vel por dispobilizar classes de tratamento e manipula��o de arquivos compactados do tipo ZIP.<BR>
 *
 * @author Rodrigo Leit�o
 * @since 5.1.0 (11/10/2013)
 */
public class RUZip {

  private RUZip() {
  }

  /**
   * Este m�todo retorna os nomes dos arquivos contidos no arquivo .zip.<br>
   * Retorna uma lista de String contendo seu nome completo, isto �, caminho completo incluindo as pastas dentro do arquivo.<br>
   * <br>
   * Por exemplo, imagine um arquivo zip que tenha duas pastas: <b>"A"</b> e <b>"B"</b>, dentro da pasta <b>"A"</b> exista dois arquivos: <b>"1.txt"</b> e <b>"2.txt"</b>, j� na pasta <b>"B"</b> existe apenas o arquivo <b>"Cantarola.mp3"</b>. Neste caso os elementos retornados na lista ser�o:<br>
   * <ul>
   * <li>"A/1.txt"
   * <li>"A/2.txt"
   * <li>"B/Cantarola.mp3"
   * </ul>
   * <br>
   *
   * @param content conte�do do arquivo em bytes.
   * @return uma lista vazia caso o arquivo ZIP n�o contenha nenhuma entra, ou a lista de elementos na ordem em que foram encontrados no ZIP.
   * @throws RFWException Lan�ado caso ocorra algum problema tentando ler o arquivo .zip
   */
  public static List<String> getZipEntries(final byte[] content) throws RFWException {
    return getZipEntries(new ByteArrayInputStream(content));
  }

  /**
   * Este m�todo retorna os nomes dos arquivos contidos no arquivo .zip.<br>
   * Retorna uma lista de String contendo seu nome completo, isto �, caminho completo incluindo as pastas dentro do arquivo.<br>
   * <br>
   * Por exemplo, imagine um arquivo zip que tenha duas pastas: <b>"A"</b> e <b>"B"</b>, dentro da pasta <b>"A"</b> exista dois arquivos: <b>"1.txt"</b> e <b>"2.txt"</b>, j� na pasta <b>"B"</b> existe apenas o arquivo <b>"Cantarola.mp3"</b>. Neste caso os elementos retornados na lista ser�o:<br>
   * <ul>
   * <li>"A/1.txt"
   * <li>"A/2.txt"
   * <li>"B/Cantarola.mp3"
   * </ul>
   * <br>
   *
   * @param stream InputStream do Arquivo .zip
   * @return uma lista vazia caso o arquivo ZIP n�o contenha nenhuma entra, ou a lista de elementos na ordem em que foram encontrados no ZIP.
   * @throws RFWException Lan�ado caso ocorra algum problema tentando ler o arquivo .zip
   */
  public static List<String> getZipEntries(InputStream stream) throws RFWException {
    final List<String> entries = new ArrayList<>();
    try (ZipInputStream zip = new ZipInputStream(stream)) {
      ZipEntry ze = null;
      while ((ze = zip.getNextEntry()) != null) {
        entries.add(ze.getName());
      }
      zip.closeEntry();
    } catch (IOException e) {
      throw new RFWWarningException("RFW_000036", e);
    }
    return entries;
  }

  /**
   * Extrai uma entrada de dentro do arquivo ZIP para um arquivo espec�fico.
   *
   * @param zipContent Conte�do do arquivo zip.
   * @param entryName Entrada a ser extra�da. Inclu� o caminho completo, ex:
   *          <ul>
   *          <li>"A/1.txt"
   *          <li>"A/2.txt"
   *          <li>"B/Cantarola.mp3"
   *          </ul>
   *          <br>
   * @param outputFile Arquivo destino do conte�do extra�do. Pode ser um arquivo tempor�rio. Inclu�r o nome do arquivo, n�o apenas o diret�rio.
   * @throws RFWException
   */
  public static void extractZipEntry(InputStream zipContent, final String entryName, File outputFile) throws RFWException {
    try (ZipInputStream zip = new ZipInputStream(zipContent)) {
      ZipEntry ze = null;
      while ((ze = zip.getNextEntry()) != null) {
        if (ze.getName().equals(entryName)) {
          break;
        }
      }
      if (ze == null) {
        throw new RFWValidationException("RFW_000037");
      }
      if (ze.isDirectory()) {
        throw new RFWValidationException("RFW_000038");
      }

      // Tudo OK? Extrai o arquivo para o ByteArrayOutputStream
      try (FileOutputStream buffer = new FileOutputStream(outputFile)) {
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = zip.read(data, 0, data.length)) != -1) {
          buffer.write(data, 0, nRead);
        }
      }
    } catch (IOException e) {
      throw new RFWWarningException("RFW_000036", e);
    }
  }

  public static byte[] decompressGZIP(byte[] compressedData) throws RFWException {
    try {
      ByteArrayInputStream bytein = new ByteArrayInputStream(compressedData);
      GZIPInputStream gzin = new GZIPInputStream(bytein);
      ByteArrayOutputStream byteout = new ByteArrayOutputStream();

      int res = 0;
      byte buf[] = new byte[1024];
      while ((res = gzin.read(buf, 0, buf.length)) != -1) {
        byteout.write(buf, 0, res);
      }
      return byteout.toByteArray();
    } catch (IOException e) {
      throw new RFWCriticalException("RFW_000039", e);
    }
  }

  /**
   * Cria um arquivo ZIP com o conte�do de outros arquivos dentro.
   *
   * @param zipFileName Nome do arquivo ZIP tempor�rio para ser criado em uma pasta tempor�ria. Ex: "CargaBalanca.zip"
   * @param compressionLevel N�vel de compress�o do ZIP de 0-9, sendo 0 menos comprimido e 9 o m�ximo de compress�o.
   * @param content array bi-dimensional onde o primeiro index indica os arquivos a serem adicionados, enquanto que o segundo index deve ter tamanho 2:
   *          <li>No index 0 uma {@link String} com o nome do arquivo que ser� colocado dentro do ZIP,
   *          <li>No index 1 uma das op��es:
   *          <ul>
   *          <li>{@link InputStream} com o conte�do do arquivo.
   *          <li>{@link String} com o caminho para uma arquivo tempor�rio.
   * @return Caminho para o arquivo tempor�rio criado com o resultado da opera��o
   * @throws RFWException
   */
  public static String createNewZipFile(String zipFileName, int compressionLevel, Object[][] content) throws RFWException {
    File file = RUFile.createFileInTemporaryPath(zipFileName, StandardCharsets.ISO_8859_1);
    try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file))) {
      zip.setMethod(ZipOutputStream.DEFLATED);
      zip.setLevel(compressionLevel);

      // Itera todo o conte�do para adicionar dentro do ZIP
      for (int i = 0; i < content.length; i++) {
        if (content[i] != null && content[i][1] != null) {
          if (content[i][1] instanceof InputStream) {
            try (InputStream fileContent = (InputStream) content[i][1]) {
              // Cria o "arquivo" dentro do zip
              zip.putNextEntry(new ZipEntry((String) content[i][0]));
              // Escreve o conte�do do arquivo dentro do zip
              RUIO.copy(fileContent, zip, false);
              zip.closeEntry();
            }
          } else if (content[i][1] instanceof String) {
            try (FileInputStream fileContent = new FileInputStream((String) content[i][1])) {
              zip.putNextEntry(new ZipEntry((String) content[i][0]));
              RUIO.copy(fileContent, zip, false);
              zip.closeEntry();
            }
          } else {
            throw new RFWCriticalException("Conte�do inexperado para ser colocado dentro do arquivo ZIP!", new String[] { content[i][1].getClass().getCanonicalName() });
          }
        }
      }
    } catch (Throwable e) {
      throw new RFWCriticalException("Falha ao criar o arquivo ZIP!", e);
    }
    return file.getAbsolutePath();
  }
}
