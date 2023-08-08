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
 * Description: VO de Cidade do serviço de localização.<br>
 *
 * @author Rodrigo Leitão
 * @since 4.1.0 (12/05/2011)
 */
public class LocationCityVO extends RFWVO {

  private static final long serialVersionUID = -9144907281519546430L;

  /**
   * Nome completo da cidade.
   */
  @RFWMetaStringField(caption = "Município", maxlength = 250, required = true)
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
   * Código telefônico do município.<br>
   * Para Cidades do Brasil, DDD tem 2 dígitos.
   */
  @RFWMetaIntegerField(caption = "DDD", maxvalue = 99, minvalue = 10, required = false)
  private Integer ddd = null;

  /**
   * Estado à qual este município pertence.
   */
  @RFWMetaRelationshipField(caption = "Estado", relationship = RelationshipTypes.ASSOCIATION, required = true)
  private LocationStateVO locationStateVO = null;

  /**
   * Mantém o código do IBGE para esta cidade. O código pode ser nulo para cidades não conhecidas ou para cidades não cadastradas no IBGE, como por exemplo cidades fora do Brasil.
   */
  @RFWMetaStringField(caption = "Código IBGE", maxlength = 7, required = false)
  private String ibgeCode = null;

  /**
   * Data da última alteração do objeto. Esta data é utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer alteração esta data deve ser aualizada para a data corrente.
   */
  @RFWMetaDateField(caption = "Última Alteração", required = true, resolution = DateResolution.SECOND)
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
   * Recupera o mantém o código do IBGE para esta cidade. O código pode ser nulo para cidades não conhecidas ou para cidades não cadastradas no IBGE, como por exemplo cidades fora do Brasil.
   *
   * @return the mantém o código do IBGE para esta cidade
   */
  public String getIbgeCode() {
    return ibgeCode;
  }

  /**
   * Define o mantém o código do IBGE para esta cidade. O código pode ser nulo para cidades não conhecidas ou para cidades não cadastradas no IBGE, como por exemplo cidades fora do Brasil.
   *
   * @param ibgecode the new mantém o código do IBGE para esta cidade
   */
  public void setIbgeCode(String ibgecode) {
    this.ibgeCode = ibgecode;
  }

  /**
   * Recupera o código telefônico do município.<br>
   * Para Cidades do Brasil, DDD tem 2 dígitos.
   *
   * @return the código telefônico do município
   */
  public Integer getDdd() {
    return ddd;
  }

  /**
   * Define o código telefônico do município.<br>
   * Para Cidades do Brasil, DDD tem 2 dígitos.
   *
   * @param ddd the new código telefônico do município
   */
  public void setDdd(Integer ddd) {
    this.ddd = ddd;
  }

  /**
   * Recupera o data da última alteração do objeto. Esta data é utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer alteração esta data deve ser aualizada para a data corrente.
   *
   * @return the data da última alteração do objeto
   */
  public LocalDate getLastchange() {
    return lastchange;
  }

  /**
   * Define o data da última alteração do objeto. Esta data é utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer alteração esta data deve ser aualizada para a data corrente.
   *
   * @param lastchange the new data da última alteração do objeto
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
   * Recupera o estado à qual este município pertence.
   *
   * @return the estado à qual este município pertence
   */
  public LocationStateVO getLocationStateVO() {
    return locationStateVO;
  }

  /**
   * Define o estado à qual este município pertence.
   *
   * @param locationstatevo the new estado à qual este município pertence
   */
  public void setLocationStateVO(LocationStateVO locationstatevo) {
    this.locationStateVO = locationstatevo;
  }

}
