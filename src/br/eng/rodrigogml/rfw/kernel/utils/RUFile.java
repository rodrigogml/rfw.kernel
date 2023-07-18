package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.File;

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

}
