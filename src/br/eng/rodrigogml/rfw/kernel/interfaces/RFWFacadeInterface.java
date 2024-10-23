package br.eng.rodrigogml.rfw.kernel.interfaces;

import javax.naming.Context;

import br.eng.rodrigogml.rfw.kernel.utils.RUJNDI;

/**
 * Description: Esta interface define um método padrão para as interfaces de fachadas que são "buscadas" pelo {@link RUJNDI}.<br>
 * Se o objeto encontrado pela RUJNDI implementar essa interface ele realiza algumas operações, conforme descritas nos métodos aqui definidos.
 *
 * @author Rodrigo Leitão
 * @since (23 de out. de 2024)
 */
public interface RFWFacadeInterface {

  /**
   * Defineo o contexto utilizado para encontrar o objeto diretamente do objeto.<br>
   * Esta definição tem duas finalidades distintas:
   * <li>Permitir o acesso ao context da fachada retornada para realização de outras funções diretamente;
   * <li>Previnir que o Garbage Collector descarte o context enquanto ele ainda está sendo utilizado. (esse bug ocorre por exemplo ao utilizar uma chamada remota no WildFly 24.0.1-Final, em que o objeto é destruído interrompendo o retorno do método abruptamente).
   *
   * @param context
   */
  void setContext(Context context);

}
