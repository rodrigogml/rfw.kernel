package br.eng.rodrigogml.rfw.kernel.eventdispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLogger;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;
import br.eng.rodrigogml.rfw.kernel.utils.RUGenerators;

/**
 * Description: Classe de l�gica/controle dos disparos dos eventos do sistema.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (27 de set de 2018)
 */
public class EventDispatcher {

  /**
   * Listeners registrados no EventDispatcher. <br>
   * Chave da Hash � o ID do evento para o qual o Listener foi registrado. O conte�do � uma Hash com todos os listeners registrados para o evento. (Usamos a HashSet para evitar que o mesmo listener seja registrado m�ltiplas vezes)
   */
  private static final HashMap<String, HashSet<EventDispatcherListener>> listeners = new HashMap<>();

  /** The Constant eventIDUUID. */
  /*
   * Nome da propriedade tempor�ria utilizada para salvar o ID do evento dentro da pr�pria hash de par�metros do evento. Esse artif�cio foi utilizado para n�o termos de complicar a estrutura de objetos dos eventos do escopo.
   */
  private static final String eventIDUUID = RUGenerators.generateUUID();

  /**
   * De fora para dentro:<Br>
   * HashMap mais externa � utilizada para armezanar a pilha de escopos associados a Thread. Assim, a chave da Hash � a pr�pria inst�ncia da Thread corrente.<Br>
   * O valor da hash � uma pilha. Ou seja, cada Thread passa a ter sua pr�pria pilha. Sempre que abrimos um novo escopo para a Thread uma nova Hash � colocada na pilha, quando fechamos esse escopo, ela � removida. Assim, o total de itens da pilha � a quantidade de escopos abertos. A aus�ncia de pilha indica nenhum escopo aberto.<br>
   * **Aqui chamamos de escopo cada transaction criada, por exemplo, cada vez que a Thread chama uma fachada com o Interceptor que abre o escopo, e fechanda quando retorna.<Br>
   * O objeto da Pilha � uma Lista de outra HashMap, Cada item da lista � um evento que foi registrado dentro deste escopo.<br>
   * A HashMap da lista � o par�metro recebido para disparar o evento, mesma HashMap que ser� passada no disparo do evento do fechamento do escopo.<br>
   * Note que, para n�o criarmos uma nova estrutura para salvar o eventID, vamos utilizar a pr�pria hash de par�metros para salvar o eventID. O eventID ser� colocado na Hash com a chave definida em {@link #eventIDUUID}.<br>
   * Caso tenhamos recebido uma HashNula no evento, instanciamos uma para salvar o eventID e inclu�mos um segundo atributo identificado por {@link #eventIDUUID} + "-isNull", com um valor qualquer. A simples presen�a dessa chave indica que a hash original era nula e n�o vazia.
   */
  private static final HashMap<Thread, Stack<List<HashMap<String, Object>>>> threadScopes = new HashMap<Thread, Stack<List<HashMap<String, Object>>>>();

  /**
   * Prioridade da Thread de Notifica��o de Evento.<br>
   * Por padr�o a prioridade � muito baixa pois eventos normalmente s�o tarefas que s�o assincronas � opera��o que lan�ou o evento. Sendo assim o usu�rio n�o est� esperando que a tarefa ocorra, nem � um grande problema se ela demorar um pouco mais.
   */
  private static Integer eventThreadPriority = Thread.MIN_PRIORITY;

  /**
   * Construtor privado, classe est�tica.
   */
  private EventDispatcher() {
  }

  /**
   * Registra um Listener para um detemrinado evento do sistema.
   *
   * @param eventID ID do evento
   * @param listener Inst�ncia do Listener que ser� notificado na ocorr�ncia do evento.
   * @throws RFWException
   */
  public static void addListener(EventDispatcherListener listener, String... eventIDs) throws RFWException {
    PreProcess.requiredNonEmptyCritical(eventIDs, "RFW_000014");

    synchronized (listeners) {
      for (String eventID : eventIDs) {
        HashSet<EventDispatcherListener> set = listeners.get(eventID);
        if (set == null) {
          set = new HashSet<>();
          listeners.put(eventID, set);
        }
        set.add(listener);
      }
    }
  }

  /**
   * Dispara um determinado evento para todos os listeners registrados.<br>
   * Note que os eventos s�o executados em uma Thread paralela, sem sess�o ou Transaction Definidos.
   *
   * @param eventID ID do evento.
   * @param params Parametros do evento a ser compartilhado com os listeners.
   */
  public static void fire(final String eventID, final HashMap<String, Object> params) {
    final HashSet<EventDispatcherListener> set;
    synchronized (listeners) {
      if (listeners.get(eventID) == null || listeners.get(eventID).size() == 0) return;
      set = new HashSet<>();
    }
    if (set != null && set.size() > 0) {
      Thread t = new Thread("### EventDispatcher: " + eventID) {
        @Override
        public void run() {
          for (EventDispatcherListener listener : set) {
            try {
              listener.event(eventID, params);
            } catch (Throwable e) {
              RFWLogger.logError("O listener '" + listener.getClass().getCanonicalName() + "' do evento '" + eventID + "' deixou vazar a exception a seguir:");
              RFWLogger.logException(e);
            }
          }
        }
      };
      t.setPriority(EventDispatcher.eventThreadPriority);
      t.setDaemon(false);
      t.start();
    }
  }

  /**
   * Abre o escopo para iniciar o registro de eventos que devem ser disparados quando o escopo for fechado com sucesso.
   */
  public static void beginScope() {
    Stack<List<HashMap<String, Object>>> scope = threadScopes.get(Thread.currentThread());
    if (scope == null) {
      scope = new Stack<List<HashMap<String, Object>>>();
      threadScopes.put(Thread.currentThread(), scope);
    }

    scope.push(new ArrayList<HashMap<String, Object>>());
  }

  /**
   * Sinaliza a finaliza��o/fechamento de um escopo de eventos.<br>
   * <li>Caso n�o seja o �ltimo escopo aberto para a Thread:
   * <ul>
   * <li>e caso tenha terminado com sucesso, os eventos desse escopo s�o passados para o escopo pai para serem disparados ou descartados junto com o escopo anterior;
   * <li>e caso tenha terminado com exception (rollback), os evento desse escopo ser�o descartados e n�o ser�o disparados.
   * </ul>
   * <li>Se for o �ltimo escopo aberto para a Thread:
   * <ul>
   * <li>e caso tenha terminado com sucesso, os eventos ser�o disparados;
   * <li>e caso tenha terminado com exception (rollback), os evento ser�o descartados.
   * </ul>
   *
   * @param committed Indica se o escopo est� sendo finalizado com sucesso. True indica que devemos fazer commit dos eventos e dispara-los/associar ao escopo anterior conforme documentora��o. False indica que devemos dar um rollback e descartar os eventos desse escopo.
   * @throws RFWException
   */
  public static void endScope(boolean committed) throws RFWException {
    Stack<List<HashMap<String, Object>>> scopes = threadScopes.get(Thread.currentThread());
    PreProcess.requiredNonNullCritical(scopes, "RFW_000015");

    List<HashMap<String, Object>> eventList = scopes.pop();

    boolean lastScope = scopes.size() == 0;
    if (lastScope) {
      // se e o �ltimo scope, j� vamos limpar os objetos da mem�ria para liberar os recursos
      threadScopes.remove(Thread.currentThread());
    }

    // Se n�o tivermos nenhum evento, n�o precisamos nem processar nada
    if (eventList != null && eventList.size() > 0) {
      if (lastScope) {
        if (committed) {
          // Dispara todos os eventos da lista
          for (HashMap<String, Object> eventParam : eventList) {
            String eventID = (String) eventParam.get(eventIDUUID);
            if (eventParam.containsKey(eventIDUUID + "-isNull")) {
              eventParam = null;
            } else {
              eventParam.remove(eventIDUUID);
            }

            // Disparamos uma Thread para cada evento recebido, exatamente como se fosse um evento do fire(), se tivermos listeners para esse evento
            final HashSet<EventDispatcherListener> listenersSet;
            synchronized (listeners) {
              listenersSet = new HashSet<>(listeners.get(eventID));
            }
            if (listenersSet != null && listenersSet.size() > 0) {
              HashMap<String, Object> eventParamFinal = eventParam;
              Thread t = new Thread("### EventDispatcher: " + eventID) {
                @Override
                public void run() {
                  // Aguardamos uns segundos antes de iniciar, porque no caso do constrole dos escopos serem feitos pelo Interceptor, o commit n�o foi de fato realizado quando essa Thread iniciou
                  try {
                    Thread.sleep(1000);
                  } catch (InterruptedException e1) {
                  }
                  for (EventDispatcherListener listener : listenersSet) {
                    try {
                      listener.event(eventID, eventParamFinal);
                    } catch (Throwable e) {
                      RFWLogger.logError("O listener '" + listener.getClass().getCanonicalName() + "' do evento '" + eventID + "' deixou vazar a exception a seguir:");
                      RFWLogger.logException(e);
                    }
                  }
                }
              };
              t.setPriority(EventDispatcher.eventThreadPriority);
              t.setDaemon(false);
              t.start();
            }
          }
        } else {
          // N�o faz nada, s� vamos descartar toda a lista de eventos
        }
      } else {
        if (committed) {
          // Passamos todos os eventos para o pr�ximo escopo para avaliar no fim dele se os eventos ser�o disparados ou n�o
          scopes.get(scopes.size() - 1).addAll(eventList);
        } else {
          // N�o faz nada, s� vamos descartar toda a lista de eventos
        }
      }
    }

  }

  /**
   * Registra um evento que dever� ser disparado na finaliza��o do escopo quando terminado com sucesso.
   *
   * @param eventID ID do evento.
   * @param params Parametros do evento a ser compartilhado com os listeners.
   * @throws RFWException
   */
  public static void fireOnCommit(final String eventID, HashMap<String, Object> params) throws RFWException {
    Stack<List<HashMap<String, Object>>> scopes = threadScopes.get(Thread.currentThread());
    PreProcess.requiredNonNullCritical(scopes, "RFW_000016");

    List<HashMap<String, Object>> eventList = scopes.get(scopes.size() - 1);

    if (params == null) {
      params = new HashMap<String, Object>();
      params.put(eventIDUUID + "-isNull", Boolean.TRUE);
    }
    params.put(eventIDUUID, eventID);

    eventList.add(params);
  }

  /**
   * Gets the prioridade da Thread de Notifica��o de Evento.<br>
   * Por padr�o a prioridade � muito baixa pois eventos normalmente s�o tarefas que s�o assincronas � opera��o que lan�ou o evento. Sendo assim o usu�rio n�o est� esperando que a tarefa ocorra, nem � um grande problema se ela demorar um pouco mais.
   *
   * @return the prioridade da Thread de Notifica��o de Evento
   */
  public static Integer getEventThreadPriority() {
    return eventThreadPriority;
  }

  /**
   * Sets the prioridade da Thread de Notifica��o de Evento.<br>
   * Por padr�o a prioridade � muito baixa pois eventos normalmente s�o tarefas que s�o assincronas � opera��o que lan�ou o evento. Sendo assim o usu�rio n�o est� esperando que a tarefa ocorra, nem � um grande problema se ela demorar um pouco mais.
   *
   * @param eventThreadPriority the new prioridade da Thread de Notifica��o de Evento
   */
  public static void setEventThreadPriority(Integer eventThreadPriority) throws RFWException {
    PreProcess.requiredBetweenCritical(eventThreadPriority, Thread.MIN_PRIORITY, Thread.MAX_PRIORITY, "RFW_000017");
    EventDispatcher.eventThreadPriority = eventThreadPriority;
  }

}
