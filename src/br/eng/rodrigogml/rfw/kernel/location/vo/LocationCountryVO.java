package br.eng.rodrigogml.rfw.kernel.location.vo;

import java.time.LocalDate;

import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess.PreProcessOption;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField.DateResolution;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringField;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: VO de País do serviço de localização.<br>
 *
 * @author Rodrigo Leitão
 * @since 4.1.0 (12/05/2011)
 */
public class LocationCountryVO extends RFWVO {

  private static final long serialVersionUID = 5918641088186601780L;

  /**
   * Sigla de duas letras do país, sempre em maiúsculas, seguindo a ISO 3166-1 alfa-2.
   */
  @RFWMetaStringField(caption = "Sigla ISO 3166-1", required = true, maxLength = 2, minLength = 2, pattern = "[A-Z]{2}", unique = true)
  private String acronym = null;

  /**
   * Nome completo do País
   */
  @RFWMetaStringField(caption = "Nome", required = true, maxLength = 250, unique = true, preProcess = PreProcessOption.STRING_SPACESCLEAN_TO_NULL)
  private String name = null;

  /**
   * Código do país de acordo com a tabela do IBGE.<br>
   * Código com 5 algarísmos. Apenas números.
   */
  @RFWMetaStringField(caption = "Código BACEN", maxLength = 5, required = false)
  private String bacenCode = null;

  /**
   * Data da última alteração neste objeto. Utilizado para fins de sincronização com sistemas externos.<br>
   * Toda vez que este objeto sofrer alguma alteração esta data deve ser atualizada para a data da alteração.
   */
  @RFWMetaDateField(caption = "Última Alteração", required = true, resolution = DateResolution.SECOND, preProcess = PreProcessOption.DATE_TONOW)
  private LocalDate lastchange = null;

  /**
   * Recupera o sigla de duas letras do país, sempre em maiúsculas.
   *
   * @return the sigla de duas letras do país, sempre em maiúsculas
   */
  public String getAcronym() {
    return acronym;
  }

  /**
   * Define o sigla de duas letras do país, sempre em maiúsculas.
   *
   * @param acronym the new sigla de duas letras do país, sempre em maiúsculas
   */
  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  /**
   * Recupera o nome completo do País.
   *
   * @return the nome completo do País
   */
  public String getName() {
    return name;
  }

  /**
   * Define o nome completo do País.
   *
   * @param name the new nome completo do País
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Recupera o código do país de acordo com a tabela do IBGE.<br>
   * Código com 5 algarísmos. Apenas números.
   *
   * @return the código do país de acordo com a tabela do IBGE
   */
  public String getBacenCode() {
    return bacenCode;
  }

  /**
   * Define o código do país de acordo com a tabela do IBGE.<br>
   * Código com 5 algarísmos. Apenas números.
   *
   * @param ibgecode the new código do país de acordo com a tabela do IBGE
   */
  public void setBacenCode(String ibgecode) {
    this.bacenCode = ibgecode;
  }

  /**
   * Recupera o data da última alteração neste objeto. Utilizado para fins de sincronização com sistemas externos.<br>
   * Toda vez que este objeto sofrer alguma alteração esta data deve ser atualizada para a data da alteração.
   *
   * @return the data da última alteração neste objeto
   */
  public LocalDate getLastchange() {
    return lastchange;
  }

  /**
   * Define o data da última alteração neste objeto. Utilizado para fins de sincronização com sistemas externos.<br>
   * Toda vez que este objeto sofrer alguma alteração esta data deve ser atualizada para a data da alteração.
   *
   * @param lastchange the new data da última alteração neste objeto
   */
  public void setLastchange(LocalDate lastchange) {
    this.lastchange = lastchange;
  }

}
