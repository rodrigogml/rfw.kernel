package br.eng.rodrigogml.rfw.kernel.location;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: {@link RFWVO} com as defini��es padr�es para trabalhar com o modelo de Location do RFW.<br>
 * Este VO deve ser extendido pelow VOs da aplica��o para que possam ser persistidos e gerenciados pela aplica��o.
 *
 * @author Rodrigo GML
 * @since 1.0.0 (29 de jul. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public abstract class LocationAddressDefaultVO extends RFWVO {

  private static final long serialVersionUID = 5355976060439907724L;

  public LocationAddressDefaultVO() {
    super();
  }

  public LocationAddressDefaultVO(Long id) {
    super(id);
  }

  /**
   * Recupera o {@link LocationCityDefaultVO} � qual este endere�o pertence.
   *
   * @return Objeto que representa a localidade da cidade deste endere�o.
   */
  public abstract LocationCityDefaultVO getLocationCityVO();

  /**
   * Recupera o nome da rua/avenida/travessa/etc.
   *
   * @return the nome da rua/avenida/travessa/etc
   */
  public abstract String getName();

  /**
   * Recupera o bairro.
   *
   * @return the bairro
   */
  public abstract String getNeighborhood();

  /**
   * Recupera o c�digo de Endere�o Postal (CEP). Sempre no formato "#####-###".<br>
   * CEP n�o � um valor 'unique' no banco de dados. Isso porque ruas muito compridas podem ter o mesmo cep e pertencer a bairros diferentes, ou mesmo no cadastro dos correios algumas vezes as ruas vem com abrevia��es diferentes e o registro duplicado.
   *
   * @return the c�digo de Endere�o Postal (CEP)
   */
  public abstract String getCep();

}