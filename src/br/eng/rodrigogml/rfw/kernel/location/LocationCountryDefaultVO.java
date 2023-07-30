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
public abstract class LocationCountryDefaultVO extends RFWVO {

  private static final long serialVersionUID = 5053934057657155145L;

  public LocationCountryDefaultVO() {
    super();
  }

  public LocationCountryDefaultVO(Long id) {
    super(id);
  }

  /**
   * Nome do País
   *
   * @return Retorna o nome do País
   */
  public abstract String getName();

  /**
   * Código do País no Bacen, se houver.
   *
   * @return Recupera o código do País no Cadastro do Bacen.
   */
  public abstract String getBacenCode();

}