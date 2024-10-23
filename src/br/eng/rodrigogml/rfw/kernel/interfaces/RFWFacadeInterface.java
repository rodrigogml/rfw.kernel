package br.eng.rodrigogml.rfw.kernel.interfaces;

import javax.naming.Context;

import br.eng.rodrigogml.rfw.kernel.utils.RUJNDI;

/**
 * Description: Esta interface define um m�todo padr�o para as interfaces de fachadas que s�o "buscadas" pelo {@link RUJNDI}.<br>
 * Se o objeto encontrado pela RUJNDI implementar essa interface ele realiza algumas opera��es, conforme descritas nos m�todos aqui definidos.
 *
 * @author Rodrigo Leit�o
 * @since (23 de out. de 2024)
 */
public interface RFWFacadeInterface {

  /**
   * Defineo o contexto utilizado para encontrar o objeto diretamente do objeto.<br>
   * Esta defini��o tem duas finalidades distintas:
   * <li>Permitir o acesso ao context da fachada retornada para realiza��o de outras fun��es diretamente;
   * <li>Previnir que o Garbage Collector descarte o context enquanto ele ainda est� sendo utilizado. (esse bug ocorre por exemplo ao utilizar uma chamada remota no WildFly 24.0.1-Final, em que o objeto � destru�do interrompendo o retorno do m�todo abruptamente).
   *
   * @param context
   */
  void setContext(Context context);

}
