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
public abstract class LocationCountryDefaultVO extends RFWVO {

  private static final long serialVersionUID = 5053934057657155145L;

  public LocationCountryDefaultVO() {
    super();
  }

  public LocationCountryDefaultVO(Long id) {
    super(id);
  }

  /**
   * Nome do Pa�s
   *
   * @return Retorna o nome do Pa�s
   */
  public abstract String getName();

  /**
   * C�digo do Pa�s no Bacen, se houver.
   *
   * @return Recupera o c�digo do Pa�s no Cadastro do Bacen.
   */
  public abstract String getBacenCode();

}