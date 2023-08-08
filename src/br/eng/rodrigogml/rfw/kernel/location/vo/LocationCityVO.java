package br.eng.rodrigogml.rfw.kernel.location.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaBigDecimalField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField.DateResolution;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaIntegerField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField.RelationshipTypes;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringField;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: VO de Cidade do servi�o de localiza��o.<br>
 *
 * @author Rodrigo Leit�o
 * @since 4.1.0 (12/05/2011)
 */
public class LocationCityVO extends RFWVO {

  private static final long serialVersionUID = -9144907281519546430L;

  /**
   * Nome completo da cidade.
   */
  @RFWMetaStringField(caption = "Munic�pio", maxlength = 250, required = true)
  private String name = null;

  /**
   * Latitude da cidade.
   */
  @RFWMetaBigDecimalField(caption = "Latitude", required = false, scale = 4)
  private BigDecimal latitude = null;

  /**
   * Longitude da cidade.
   */
  @RFWMetaBigDecimalField(caption = "Longitude", required = false, scale = 4)
  private BigDecimal longitude = null;

  /**
   * C�digo telef�nico do munic�pio.<br>
   * Para Cidades do Brasil, DDD tem 2 d�gitos.
   */
  @RFWMetaIntegerField(caption = "DDD", maxvalue = 99, minvalue = 10, required = false)
  private Integer ddd = null;

  /**
   * Estado � qual este munic�pio pertence.
   */
  @RFWMetaRelationshipField(caption = "Estado", relationship = RelationshipTypes.ASSOCIATION, required = true)
  private LocationStateVO locationStateVO = null;

  /**
   * Mant�m o c�digo do IBGE para esta cidade. O c�digo pode ser nulo para cidades n�o conhecidas ou para cidades n�o cadastradas no IBGE, como por exemplo cidades fora do Brasil.
   */
  @RFWMetaStringField(caption = "C�digo IBGE", maxlength = 7, required = false)
  private String ibgeCode = null;

  /**
   * Data da �ltima altera��o do objeto. Esta data � utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer altera��o esta data deve ser aualizada para a data corrente.
   */
  @RFWMetaDateField(caption = "�ltima Altera��o", required = true, resolution = DateResolution.SECOND)
  private LocalDate lastchange = null;

  /**
   * Recupera o nome completo da cidade.
   *
   * @return the nome completo da cidade
   */
  public String getName() {
    return name;
  }

  /**
   * Define o nome completo da cidade.
   *
   * @param name the new nome completo da cidade
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Recupera o mant�m o c�digo do IBGE para esta cidade. O c�digo pode ser nulo para cidades n�o conhecidas ou para cidades n�o cadastradas no IBGE, como por exemplo cidades fora do Brasil.
   *
   * @return the mant�m o c�digo do IBGE para esta cidade
   */
  public String getIbgeCode() {
    return ibgeCode;
  }

  /**
   * Define o mant�m o c�digo do IBGE para esta cidade. O c�digo pode ser nulo para cidades n�o conhecidas ou para cidades n�o cadastradas no IBGE, como por exemplo cidades fora do Brasil.
   *
   * @param ibgecode the new mant�m o c�digo do IBGE para esta cidade
   */
  public void setIbgeCode(String ibgecode) {
    this.ibgeCode = ibgecode;
  }

  /**
   * Recupera o c�digo telef�nico do munic�pio.<br>
   * Para Cidades do Brasil, DDD tem 2 d�gitos.
   *
   * @return the c�digo telef�nico do munic�pio
   */
  public Integer getDdd() {
    return ddd;
  }

  /**
   * Define o c�digo telef�nico do munic�pio.<br>
   * Para Cidades do Brasil, DDD tem 2 d�gitos.
   *
   * @param ddd the new c�digo telef�nico do munic�pio
   */
  public void setDdd(Integer ddd) {
    this.ddd = ddd;
  }

  /**
   * Recupera o data da �ltima altera��o do objeto. Esta data � utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer altera��o esta data deve ser aualizada para a data corrente.
   *
   * @return the data da �ltima altera��o do objeto
   */
  public LocalDate getLastchange() {
    return lastchange;
  }

  /**
   * Define o data da �ltima altera��o do objeto. Esta data � utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer altera��o esta data deve ser aualizada para a data corrente.
   *
   * @param lastchange the new data da �ltima altera��o do objeto
   */
  public void setLastchange(LocalDate lastchange) {
    this.lastchange = lastchange;
  }

  /**
   * Recupera o latitude da cidade.
   *
   * @return the latitude da cidade
   */
  public BigDecimal getLatitude() {
    return latitude;
  }

  /**
   * Define o latitude da cidade.
   *
   * @param latitude the new latitude da cidade
   */
  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  /**
   * Recupera o longitude da cidade.
   *
   * @return the longitude da cidade
   */
  public BigDecimal getLongitude() {
    return longitude;
  }

  /**
   * Define o longitude da cidade.
   *
   * @param longitude the new longitude da cidade
   */
  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  /**
   * Recupera o estado � qual este munic�pio pertence.
   *
   * @return the estado � qual este munic�pio pertence
   */
  public LocationStateVO getLocationStateVO() {
    return locationStateVO;
  }

  /**
   * Define o estado � qual este munic�pio pertence.
   *
   * @param locationstatevo the new estado � qual este munic�pio pertence
   */
  public void setLocationStateVO(LocationStateVO locationstatevo) {
    this.locationStateVO = locationstatevo;
  }

}
