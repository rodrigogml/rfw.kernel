package br.eng.rodrigogml.rfw.kernel.location.vo;

import java.time.LocalDate;

import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess.PreProcessOption;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField.DateResolution;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringField;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: VO de Pa�s do servi�o de localiza��o.<br>
 *
 * @author Rodrigo Leit�o
 * @since 4.1.0 (12/05/2011)
 */
public class LocationCountryVO extends RFWVO {

  private static final long serialVersionUID = 5918641088186601780L;

  /**
   * Sigla de duas letras do pa�s, sempre em mai�sculas, seguindo a ISO 3166-1 alfa-2.
   */
  @RFWMetaStringField(caption = "Sigla ISO 3166-1", required = true, maxLength = 2, minlength = 2, pattern = "[A-Z]{2}", unique = true)
  private String acronym = null;

  /**
   * Nome completo do Pa�s
   */
  @RFWMetaStringField(caption = "Nome", required = true, maxLength = 250, unique = true, preProcess = PreProcessOption.STRING_SPACESCLEAN_TO_NULL)
  private String name = null;

  /**
   * C�digo do pa�s de acordo com a tabela do IBGE.<br>
   * C�digo com 5 algar�smos. Apenas n�meros.
   */
  @RFWMetaStringField(caption = "C�digo BACEN", maxLength = 5, required = false)
  private String bacenCode = null;

  /**
   * Data da �ltima altera��o neste objeto. Utilizado para fins de sincroniza��o com sistemas externos.<br>
   * Toda vez que este objeto sofrer alguma altera��o esta data deve ser atualizada para a data da altera��o.
   */
  @RFWMetaDateField(caption = "�ltima Altera��o", required = true, resolution = DateResolution.SECOND, preProcess = PreProcessOption.DATE_TONOW)
  private LocalDate lastchange = null;

  /**
   * Recupera o sigla de duas letras do pa�s, sempre em mai�sculas.
   *
   * @return the sigla de duas letras do pa�s, sempre em mai�sculas
   */
  public String getAcronym() {
    return acronym;
  }

  /**
   * Define o sigla de duas letras do pa�s, sempre em mai�sculas.
   *
   * @param acronym the new sigla de duas letras do pa�s, sempre em mai�sculas
   */
  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  /**
   * Recupera o nome completo do Pa�s.
   *
   * @return the nome completo do Pa�s
   */
  public String getName() {
    return name;
  }

  /**
   * Define o nome completo do Pa�s.
   *
   * @param name the new nome completo do Pa�s
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Recupera o c�digo do pa�s de acordo com a tabela do IBGE.<br>
   * C�digo com 5 algar�smos. Apenas n�meros.
   *
   * @return the c�digo do pa�s de acordo com a tabela do IBGE
   */
  public String getBacenCode() {
    return bacenCode;
  }

  /**
   * Define o c�digo do pa�s de acordo com a tabela do IBGE.<br>
   * C�digo com 5 algar�smos. Apenas n�meros.
   *
   * @param ibgecode the new c�digo do pa�s de acordo com a tabela do IBGE
   */
  public void setBacenCode(String ibgecode) {
    this.bacenCode = ibgecode;
  }

  /**
   * Recupera o data da �ltima altera��o neste objeto. Utilizado para fins de sincroniza��o com sistemas externos.<br>
   * Toda vez que este objeto sofrer alguma altera��o esta data deve ser atualizada para a data da altera��o.
   *
   * @return the data da �ltima altera��o neste objeto
   */
  public LocalDate getLastchange() {
    return lastchange;
  }

  /**
   * Define o data da �ltima altera��o neste objeto. Utilizado para fins de sincroniza��o com sistemas externos.<br>
   * Toda vez que este objeto sofrer alguma altera��o esta data deve ser atualizada para a data da altera��o.
   *
   * @param lastchange the new data da �ltima altera��o neste objeto
   */
  public void setLastchange(LocalDate lastchange) {
    this.lastchange = lastchange;
  }

}
