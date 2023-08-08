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
 * Description: VO de Estado/Provincia do servi�o de localiza��o.<br>
 *
 * @author Rodrigo Leit�o
 * @since 4.1.0 (12/05/2011)
 */
public class LocationStateVO extends RFWVO {

  private static final long serialVersionUID = 3071326358745978964L;

  /**
   * Sigla de duas letras do estado (UF), Sempre mai�sculas.<br>
   * Campo obrigat�rio para Estados Brasileiros. Normalmente �nicos em cada pa�s, mas podem se repetir em pa�ses diferentes.
   */
  @RFWMetaStringField(caption = "UF", required = false, maxlength = 2, minlength = 2, pattern = "[A-Z] {2}", unique = false)
  private String acronym = null;

  /**
   * Nome completo do Estado.
   */
  @RFWMetaStringField(caption = "Estado", maxlength = 250, required = true, preProcess = PreProcessOption.STRING_SPACESCLEAN_TO_NULL)
  private String name = null;

  /**
   * Pa�s ao qual este estado pertence.
   */
  @RFWMetaRelationshipField(caption = "Pa�s", required = true, relationship = RelationshipTypes.ASSOCIATION, column = "idk_locationcountry")
  private LocationCountryVO locationCountryVO = null;

  /**
   * Mant�m o C�digo do IBGE para o estado. Obrigat�rio para estados brasileiros.
   */
  @RFWMetaStringField(caption = "C�digo IBGE", maxlength = 2, required = false)
  private String ibgeCode = null;

  /**
   * Data da �ltima altera��o do objeto. Esta data � utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer altera��o esta data deve ser aualizada para a data corrente.
   */
  @RFWMetaDateField(caption = "�ltima Altera��o", required = true, resolution = DateResolution.SECOND)
  private LocalDate lastChange = null;

  /**
   * Recupera o sigla de duas letras do estado (UF), Sempre mai�sculas.<br>
   * Campo obrigat�rio para Estados Brasileiros. Normalmente �nicos em cada pa�s, mas podem se repetir em pa�ses diferentes.
   *
   * @return the sigla de duas letras do estado (UF), Sempre mai�sculas
   */
  public String getAcronym() {
    return acronym;
  }

  /**
   * Define o sigla de duas letras do estado (UF), Sempre mai�sculas.<br>
   * Campo obrigat�rio para Estados Brasileiros. Normalmente �nicos em cada pa�s, mas podem se repetir em pa�ses diferentes.
   *
   * @param acronym the new sigla de duas letras do estado (UF), Sempre mai�sculas
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
   * Recupera o pa�s ao qual este estado pertence.
   *
   * @return the pa�s ao qual este estado pertence
   */
  public LocationCountryVO getLocationCountryVO() {
    return locationCountryVO;
  }

  /**
   * Define o pa�s ao qual este estado pertence.
   *
   * @param locationCountryVO the new pa�s ao qual este estado pertence
   */
  public void setLocationCountryVO(LocationCountryVO locationCountryVO) {
    this.locationCountryVO = locationCountryVO;
  }

  /**
   * Recupera o mant�m o C�digo do IBGE para o estado. Obrigat�rio para estados brasileiros.
   *
   * @return the mant�m o C�digo do IBGE para o estado
   */
  public String getIbgeCode() {
    return ibgeCode;
  }

  /**
   * Define o mant�m o C�digo do IBGE para o estado. Obrigat�rio para estados brasileiros.
   *
   * @param ibgecode the new mant�m o C�digo do IBGE para o estado
   */
  public void setIbgeCode(String ibgecode) {
    this.ibgeCode = ibgecode;
  }

  /**
   * Recupera o data da �ltima altera��o do objeto. Esta data � utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer altera��o esta data deve ser aualizada para a data corrente.
   *
   * @return the data da �ltima altera��o do objeto
   */
  public LocalDate getLastChange() {
    return lastChange;
  }

  /**
   * Define o data da �ltima altera��o do objeto. Esta data � utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer altera��o esta data deve ser aualizada para a data corrente.
   *
   * @param lastchange the new data da �ltima altera��o do objeto
   */
  public void setLastChange(LocalDate lastchange) {
    this.lastChange = lastchange;
  }

}
