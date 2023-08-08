package br.eng.rodrigogml.rfw.kernel.location.vo;

import java.time.LocalDate;

import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess.PreProcessOption;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField.DateResolution;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField.RelationshipTypes;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringCEPField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringField;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Objeto que representa um endereço físico de imóvel.<br>
 *
 * @author Rodrigo Leitão
 * @since 10.0.0 (7 de ago de 2018)
 */
public class LocationAddressVO extends RFWVO {

  private static final long serialVersionUID = -2735534535038218202L;

  /**
   * Código de Endereço Postal (CEP). Sempre no formato "#####-###".<br>
   * CEP não é um valor 'unique' no banco de dados. Isso porque ruas muito compridas podem ter o mesmo cep e pertencer a bairros diferentes, ou mesmo no cadastro dos correios algumas vezes as ruas vem com abreviações diferentes e o registro duplicado.
   */
  @RFWMetaStringCEPField(caption = "CEP", required = true)
  private String cep = null;

  /**
   * Nome da rua/avenida/travessa/etc.
   */
  @RFWMetaStringField(caption = "Logradouro", maxlength = 250, required = true, preProcess = PreProcessOption.STRING_SPACESCLEAN_TO_NULL)
  private String name = null;

  /**
   * Bairro
   */
  @RFWMetaStringField(caption = "Bairro", maxlength = 250, required = false, preProcess = PreProcessOption.STRING_SPACESCLEAN_TO_NULL)
  private String neighborhood = null;

  /**
   * Cidade à qual este endereço pertence.
   */
  @RFWMetaRelationshipField(caption = "Cidade", required = true, relationship = RelationshipTypes.ASSOCIATION, column = "idk_locationcity")
  private LocationCityVO locationCityVO = null;

  /**
   * Data da última alteração do objeto. Esta data é utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer alteração esta data deve ser aualizada para a data corrente.
   */
  @RFWMetaDateField(caption = "Última Alteração", required = true, resolution = DateResolution.SECOND)
  private LocalDate lastChange = null;

  /**
   * Recupera o código de Endereço Postal (CEP). Sempre no formato "#####-###".<br>
   * CEP não é um valor 'unique' no banco de dados. Isso porque ruas muito compridas podem ter o mesmo cep e pertencer a bairros diferentes, ou mesmo no cadastro dos correios algumas vezes as ruas vem com abreviações diferentes e o registro duplicado.
   *
   * @return the código de Endereço Postal (CEP)
   */
  public String getCep() {
    return cep;
  }

  /**
   * Define o código de Endereço Postal (CEP). Sempre no formato "#####-###".<br>
   * CEP não é um valor 'unique' no banco de dados. Isso porque ruas muito compridas podem ter o mesmo cep e pertencer a bairros diferentes, ou mesmo no cadastro dos correios algumas vezes as ruas vem com abreviações diferentes e o registro duplicado.
   *
   * @param cep the new código de Endereço Postal (CEP)
   */
  public void setCep(String cep) {
    this.cep = cep;
  }

  /**
   * Recupera o nome da rua/avenida/travessa/etc.
   *
   * @return the nome da rua/avenida/travessa/etc
   */
  public String getName() {
    return name;
  }

  /**
   * Define o nome da rua/avenida/travessa/etc.
   *
   * @param name the new nome da rua/avenida/travessa/etc
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Recupera o bairro.
   *
   * @return the bairro
   */
  public String getNeighborhood() {
    return neighborhood;
  }

  /**
   * Define o bairro.
   *
   * @param neighborhood the new bairro
   */
  public void setNeighborhood(String neighborhood) {
    this.neighborhood = neighborhood;
  }

  /**
   * Recupera o cidade à qual este endereço pertence.
   *
   * @return the cidade à qual este endereço pertence
   */
  public LocationCityVO getLocationCityVO() {
    return locationCityVO;
  }

  /**
   * Define o cidade à qual este endereço pertence.
   *
   * @param locationCityVO the new cidade à qual este endereço pertence
   */
  public void setLocationCityVO(LocationCityVO locationCityVO) {
    this.locationCityVO = locationCityVO;
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
   * @param lastChange the new data da última alteração do objeto
   */
  public void setLastChange(LocalDate lastChange) {
    this.lastChange = lastChange;
  }

}
