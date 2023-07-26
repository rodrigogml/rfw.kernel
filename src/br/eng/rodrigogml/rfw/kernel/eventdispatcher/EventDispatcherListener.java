package br.eng.rodrigogml.rfw.kernel.eventdispatcher;

import java.util.HashMap;

/**
 * Description: Listener de eventos do EventDispatcher.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (27 de set de 2018)
 */
public interface EventDispatcherListener {

  /**
   * Chamado sempre que alguma parte do sistema disparar um evento.
   *
   * <b>ATEN��O:</B> O m�todo n�o lan�a nenhuma exce��o pois elas devem ser totalmente tratadas dentro dos listeners. Qualquer exception que vaze (como {@link RuntimeException} ser� logada como erro cr�tico e n�o atrapalhar� o processo de notifica��o dos outros listeners nem desfar� a a��o atual.
   *
   * @param eventID Identificador do Evento. Normalmente uma STRING definida como constante na classe Framework com o prefixo "EVENT_".
   * @param params Hash com os parametros do evento. O primeiro argumento � uma chave String (como se fosse o nome da vari�vel) e o conte�do da Hash � qualquer objeto que o evento deseje passar. Para mais informa��es � necess�rio verificar a documenta��o do evento.
   */
  public void event(String eventID, HashMap<String, Object> params);

}
