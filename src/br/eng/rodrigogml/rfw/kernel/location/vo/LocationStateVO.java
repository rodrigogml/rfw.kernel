package br.eng.rodrigogml.rfw.kernel.location.vo;

import java.time.LocalDate;

import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess.PreProcessOption;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField.DateResolution;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField.RelationshipTypes;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringField;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: VO de Estado/Provincia do serviço de localização.<br>
 *
 * @author Rodrigo Leitão
 * @since 4.1.0 (12/05/2011)
 */
public class LocationStateVO extends RFWVO {

  private static final long serialVersionUID = 3071326358745978964L;

  /**
   * Sigla de duas letras do estado (UF), Sempre maiúsculas.<br>
   * Campo obrigatório para Estados Brasileiros. Normalmente únicos em cada país, mas podem se repetir em países diferentes.
   */
  @RFWMetaStringField(caption = "UF", required = false, maxLength = 2, minLength = 2, pattern = "[A-Z] {2}", unique = false)
  private String acronym = null;

  /**
   * Nome completo do Estado.
   */
  @RFWMetaStringField(caption = "Estado", maxLength = 250, required = true, preProcess = PreProcessOption.STRING_SPACESCLEAN_TO_NULL)
  private String name = null;

  /**
   * País ao qual este estado pertence.
   */
  @RFWMetaRelationshipField(caption = "País", required = true, relationship = RelationshipTypes.ASSOCIATION)
  private LocationCountryVO locationCountryVO = null;

  /**
   * Mantém o Código do IBGE para o estado. Obrigatório para estados brasileiros.
   */
  @RFWMetaStringField(caption = "Código IBGE", maxLength = 2, required = false)
  private String ibgeCode = null;

  /**
   * Data da última alteração do objeto. Esta data é utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer alteração esta data deve ser aualizada para a data corrente.
   */
  @RFWMetaDateField(caption = "Última Alteração", required = true, resolution = DateResolution.SECOND)
  private LocalDate lastChange = null;

  /**
   * Recupera o sigla de duas letras do estado (UF), Sempre maiúsculas.<br>
   * Campo obrigatório para Estados Brasileiros. Normalmente únicos em cada país, mas podem se repetir em países diferentes.
   *
   * @return the sigla de duas letras do estado (UF), Sempre maiúsculas
   */
  public String getAcronym() {
    return acronym;
  }

  /**
   * Define o sigla de duas letras do estado (UF), Sempre maiúsculas.<br>
   * Campo obrigatório para Estados Brasileiros. Normalmente únicos em cada país, mas podem se repetir em países diferentes.
   *
   * @param acronym the new sigla de duas letras do estado (UF), Sempre maiúsculas
   */
  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  /**
   * Recupera o nome completo do Estado.
   *
   * @return the nome completo do Estado
   */
  public String getName() {
    return name;
  }

  /**
   * Define o nome completo do Estado.
   *
   * @param name the new nome completo do Estado
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Recupera o país ao qual este estado pertence.
   *
   * @return the país ao qual este estado pertence
   */
  public LocationCountryVO getLocationCountryVO() {
    return locationCountryVO;
  }

  /**
   * Define o país ao qual este estado pertence.
   *
   * @param locationCountryVO the new país ao qual este estado pertence
   */
  public void setLocationCountryVO(LocationCountryVO locationCountryVO) {
    this.locationCountryVO = locationCountryVO;
  }

  /**
   * Recupera o mantém o Código do IBGE para o estado. Obrigatório para estados brasileiros.
   *
   * @return the mantém o Código do IBGE para o estado
   */
  public String getIbgeCode() {
    return ibgeCode;
  }

  /**
   * Define o mantém o Código do IBGE para o estado. Obrigatório para estados brasileiros.
   *
   * @param ibgecode the new mantém o Código do IBGE para o estado
   */
  public void setIbgeCode(String ibgecode) {
    this.ibgeCode = ibgecode;
  }

  /**
   * Recupera o data da última alteração do objeto. Esta data é utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer alteração esta data deve ser aualizada para a data corrente.
   *
   * @return the data da última alteração do objeto
   */
  public LocalDate getLastChange() {
    return lastChange;
  }

  /**
   * Define o data da última alteração do objeto. Esta data é utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer alteração esta data deve ser aualizada para a data corrente.
   *
   * @param lastchange the new data da última alteração do objeto
   */
  public void setLastChange(LocalDate lastchange) {
    this.lastChange = lastchange;
  }

}
