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
 * Description: Objeto que representa um endere�o f�sico de im�vel.<br>
 *
 * @author Rodrigo Leit�o
 * @since 10.0.0 (7 de ago de 2018)
 */
public class LocationAddressVO extends RFWVO {

  private static final long serialVersionUID = -2735534535038218202L;

  /**
   * C�digo de Endere�o Postal (CEP). Sempre no formato "#####-###".<br>
   * CEP n�o � um valor 'unique' no banco de dados. Isso porque ruas muito compridas podem ter o mesmo cep e pertencer a bairros diferentes, ou mesmo no cadastro dos correios algumas vezes as ruas vem com abrevia��es diferentes e o registro duplicado.
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
   * Cidade � qual este endere�o pertence.
   */
  @RFWMetaRelationshipField(caption = "Cidade", required = true, relationship = RelationshipTypes.ASSOCIATION, column = "idk_locationcity")
  private LocationCityVO locationCityVO = null;

  /**
   * Data da �ltima altera��o do objeto. Esta data � utilizada para o controle de sincronia dos dados com sistemas externos.<br>
   * Toda vez que o objeto sofrer qualquer altera��o esta data deve ser aualizada para a data corrente.
   */
  @RFWMetaDateField(caption = "�ltima Altera��o", required = true, resolution = DateResolution.SECOND)
  private LocalDate lastChange = null;

  /**
   * Recupera o c�digo de Endere�o Postal (CEP). Sempre no formato "#####-###".<br>
   * CEP n�o � um valor 'unique' no banco de dados. Isso porque ruas muito compridas podem ter o mesmo cep e pertencer a bairros diferentes, ou mesmo no cadastro dos correios algumas vezes as ruas vem com abrevia��es diferentes e o registro duplicado.
   *
   * @return the c�digo de Endere�o Postal (CEP)
   */
  public String getCep() {
    return cep;
  }

  /**
   * Define o c�digo de Endere�o Postal (CEP). Sempre no formato "#####-###".<br>
   * CEP n�o � um valor 'unique' no banco de dados. Isso porque ruas muito compridas podem ter o mesmo cep e pertencer a bairros diferentes, ou mesmo no cadastro dos correios algumas vezes as ruas vem com abrevia��es diferentes e o registro duplicado.
   *
   * @param cep the new c�digo de Endere�o Postal (CEP)
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
   * Recupera o cidade � qual este endere�o pertence.
   *
   * @return the cidade � qual este endere�o pertence
   */
  public LocationCityVO getLocationCityVO() {
    return locationCityVO;
  }

  /**
   * Define o cidade � qual este endere�o pertence.
   *
   * @param locationCityVO the new cidade � qual este endere�o pertence
   */
  public void setLocationCityVO(LocationCityVO locationCityVO) {
    this.locationCityVO = locationCityVO;
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
   * @param lastChange the new data da �ltima altera��o do objeto
   */
  public void setLastChange(LocalDate lastChange) {
    this.lastChange = lastChange;
  }

}
