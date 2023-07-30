package br.eng.rodrigogml.rfw.kernel.location;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Classe padrão de Location para Estados.<br>
 * Esta classe define os estados/províncias de uma região. A aplicação deve extender esta classe e definir seus atributos para que possa ser persistido ou gerencia-la como preferis.
 *
 * @author Rodrigo GML
 * @since 1.0.0 (29 de jul. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public abstract class LocationStateDefaultVO extends RFWVO {

  private static final long serialVersionUID = -5032971990664681669L;

  public LocationStateDefaultVO() {
    super();
  }

  public LocationStateDefaultVO(Long id) {
    super(id);
  }

  /**
   * Nome da cidade.
   *
   * @return o nome da cidade.
   */
  public abstract String getName();

  /**
   * Recupera a abreviatura/sigla/UD do estado/província.
   *
   * @return Abreviatura/Sigla/UF do estado província.
   */
  public abstract String getAcronym();

  /**
   * Objeto que representa o país à qual este estado/província está relacionado.
   *
   * @return Recupera o objeto que representa o país à qual este estado/província está relacionado.
   */
  public abstract LocationCountryDefaultVO getLocationCountryVO();

  /**
   * Código do Estado no cadastro do IBGE.
   *
   * @return Recupera o código do estado no cadastro do IBGE.
   */
  public abstract String getIbgeCode();

}