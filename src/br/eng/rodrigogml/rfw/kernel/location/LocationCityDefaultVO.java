package br.eng.rodrigogml.rfw.kernel.location;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: {@link RFWVO} com as definições padrões para trabalhar com o modelo de Location do RFW.<br>
 * Este VO deve ser extendido pelow VOs da aplicação para que possam ser persistidos e gerenciados pela aplicação.
 *
 * @author Rodrigo GML
 * @since 1.0.0 (29 de jul. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public abstract class LocationCityDefaultVO extends RFWVO {

  private static final long serialVersionUID = 1220254158188968736L;

  public LocationCityDefaultVO() {
    super();
  }

  public LocationCityDefaultVO(Long id) {
    super(id);
  }

  /**
   * Nome da cidade.
   *
   * @return o nome da cidade.
   */
  public abstract String getName();

  /**
   * Retorna o VO representando o estado/província a qual esta cidade pertence.
   *
   * @return VO do estado/província a qual a cidade pertence.
   */
  public abstract LocationStateDefaultVO getLocationStateVO();

  /**
   * Retorna o código
   *
   * @return
   */
  public abstract String getIbgeCode();

}