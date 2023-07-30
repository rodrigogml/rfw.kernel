package br.eng.rodrigogml.rfw.kernel.location;

import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Classe padr�o de Location para Estados.<br>
 * Esta classe define os estados/prov�ncias de uma regi�o. A aplica��o deve extender esta classe e definir seus atributos para que possa ser persistido ou gerencia-la como preferis.
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
   * Recupera a abreviatura/sigla/UD do estado/prov�ncia.
   *
   * @return Abreviatura/Sigla/UF do estado prov�ncia.
   */
  public abstract String getAcronym();

  /**
   * Objeto que representa o pa�s � qual este estado/prov�ncia est� relacionado.
   *
   * @return Recupera o objeto que representa o pa�s � qual este estado/prov�ncia est� relacionado.
   */
  public abstract LocationCountryDefaultVO getLocationCountryVO();

  /**
   * C�digo do Estado no cadastro do IBGE.
   *
   * @return Recupera o c�digo do estado no cadastro do IBGE.
   */
  public abstract String getIbgeCode();

}