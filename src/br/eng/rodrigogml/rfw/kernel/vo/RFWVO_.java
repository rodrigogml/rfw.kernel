package br.eng.rodrigogml.rfw.kernel.vo;

import java.io.Serializable;

import br.eng.rodrigogml.rfw.kernel.utils.RUReflex;

/**
 * Description: Classe padr�o de defini��o do Meta Object.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (15/07/2015)
 */
public abstract class RFWVO_ implements Serializable {

  private static final long serialVersionUID = 1070633715215781062L;

  private final String basepath;

  protected RFWVO_() {
    this.basepath = null;
  }

  public RFWVO_(String basepath) {
    this.basepath = basepath;
  }

  public String path() {
    return (basepath != null ? basepath : "");
  }

  protected String getAttributePath(String attribute) {
    return RUReflex.getAttributePath(attribute, basepath);
  }

  protected String getAttributePath(String attribute, int index) {
    return RUReflex.getAttributePath(attribute, index, basepath);
  }

  /**
   * Cria o caminho para um item dentro de uma hash.
   *
   * @param attribute Nome do atributo que cont�m a hash.
   * @param key Representa��o em String do objeto chave da hash.
   * @param clazz Classe da chave de Hash.
   * @return
   */
  protected String getAttributePath(String attribute, Object key, Class<?> clazz) {
    return RUReflex.getAttributePath(attribute, key, clazz, basepath);
  }

  public final String id() {
    return getAttributePath("id");
  }
}
