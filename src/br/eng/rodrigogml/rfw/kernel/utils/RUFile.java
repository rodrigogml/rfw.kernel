package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.File;

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

}
