package br.eng.rodrigogml.rfw.kernel.logger;

import br.eng.rodrigogml.rfw.kernel.bundle.RFWBundle;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationGroupException;
import br.eng.rodrigogml.rfw.kernel.utils.RUReflex;

/**
 * Description: Interface de definição para implementação da máquina de Log do sistema.<br>
 * A implementação desta interface permite que as chamadas de log feitas na classe {@link RFWLogger} sejam repassadas para a implementação diretamente.
 *
 * @author Rodrigo Leitão
 * @since 10.0 (20 de jul. de 2023)
 */
public interface RFWLoggerImplementation {

  /**
   * Prioridades/Severidade do evento.<br>
   */
  public static enum RFWLogSeverity {
    /**
     * Registra um objeto no LOG. O RFWLogger fará o máximo para imprimir as informações do objeto no log.
     */
    OBJECT,
    /**
     * Registra uma informação com a severidade DEBUG
     */
    DEBUG,
    /**
     * Registra uma informação com a severidade INFO
     */
    INFO,
    /**
     * Registra uma informação com a severidade WARN
     */
    WARN,
    /**
     * Registra uma informação com a severidade ERROR
     */
    ERROR,
    /**
     * Registra uma informação para os desenvolvedores. Criado para que seja possível criar notificações para os desenvolvedores quando alguma nova informação entrada no sistema é relevante a ponto de ser informada para o DEV mas não é um erro ou aviso.
     */
    DEV,
    /**
     * Registra uma Exceção que representa uma Validação.
     */
    VALIDATION,
    /**
     * Registra uma Exceção que representa Erro/Problema.
     */
    EXCEPTION;
  }

  /**
   * Realiza o log com a prioridade ERROR
   *
   * @param msg Mensagem a ser registrada
   */
  public default void logError(String msg) {
    log(RFWLogSeverity.ERROR, msg, null, null);
  }

  /**
   * Faz o log de uma exception.
   *
   * @param e Exceção a ser Logada.
   */
  public default void logException(Throwable e) {
    RFWLogSeverity severity = RFWLogSeverity.EXCEPTION;
    if (e instanceof RFWValidationException || e instanceof RFWValidationGroupException) {
      severity = RFWLogSeverity.VALIDATION;
    }
    String exPoint = e.getStackTrace()[0].toString();
    log(severity, RFWBundle.get(e), RFWLogger.convertExceptionToString(e), exPoint);
  }

  /**
   * Faz o log de uma exception passando tags para categorizar o erro.
   *
   * @param e Exception para ser registrada.
   * @param tags Conjunto de tags para associar ao registro.
   */
  public default void logException(Throwable e, String[] tags) {
    RFWLogSeverity severity = RFWLogSeverity.EXCEPTION;
    if (e instanceof RFWValidationException || e instanceof RFWValidationGroupException) {
      severity = RFWLogSeverity.VALIDATION;
    }
    String exPoint = e.getStackTrace()[0].toString();
    log(severity, RFWBundle.get(e), RFWLogger.convertExceptionToString(e), exPoint, tags);
  }

  /**
   * Faz o log com diversas informações que podem ou não serem passadas.
   *
   * @param severity Enumeration indicando a severidade (nível) do log.
   * @param msg Mensagem a ser registrada no log. Esta mensagem deve ser curta e o mais explicativa possível.
   * @param content Conteúdo em caso de conteúdo anexo. (Grande Volume de Dados).
   * @param exPoint Define o ponto da exception, assim consegimos encontrar mais registros da mesma exception.
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags são utilizadas para ajudar a filtrar vários eventos de uma mesma natureza, não jogue informações que só aparecerão em um único evento por vez nas tags. Utilize para categorizar os tipos de logs. Para dados voláteis ou únicos, crie outras entradas de INFO ou DEBUG.
   */
  public void log(RFWLogSeverity severity, String msg, String content, String exPoint, String... tags);

  /**
   * Realiza o log com a prioridade WARN
   *
   * @param msg Mensagem a ser registrada
   */
  public default void logWarn(String msg) {
    log(RFWLogSeverity.WARN, msg, null, null);
  }

  /**
   * Realiza o log com a prioridade WARN
   *
   * @param msg Mensagem a ser registrada
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags são utilizadas para ajudar a filtrar vários eventos de uma mesma natureza, não jogue informações que só aparecerão em um único evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public default void logWarn(String msg, String[] tags) {
    log(RFWLogSeverity.WARN, msg, null, null, tags);
  }

  /**
   * Realiza o log com a prioridade ERROR
   *
   * @param msg Mensagem a ser registrada
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags são utilizadas para ajudar a filtrar vários eventos de uma mesma natureza, não jogue informações que só aparecerão em um único evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public default void logError(String msg, String[] tags) {
    log(RFWLogSeverity.ERROR, msg, null, null, tags);
  }

  /**
   * Faz o log do conteúdo de um objeto. Utilizando o método {@link RUReflex#printObject(Object)}
   *
   * @param msg Mensagem a ser colocada no registro do Log.
   * @param obj Objeto a ser impresso no anexo do Log.
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags são utilizadas para ajudar a filtrar vários eventos de uma mesma natureza, não jogue informações que só aparecerão em um único evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public default void logObject(String msg, Object obj, String... tags) {
    log(RFWLogSeverity.OBJECT, msg, RUReflex.printObject(obj) + "\r\n========INVOKER:===========\r\n" + RUReflex.getInvoker(3, 10), null, tags);
  }

  /**
   * Realiza o log com a prioridade DEBUG.
   *
   * @param msg Mensagem a ser registrada
   */
  public default void logDebug(String msg) {
    log(RFWLogSeverity.DEBUG, msg, null, null);
  }

  /**
   * Realiza o log com a prioridade INFO
   *
   * @param msg Mensagem a ser registrada
   */
  public default void logInfo(String msg) {
    log(RFWLogSeverity.INFO, msg, null, null);
  }

  /**
   * Realiza o log de uma mensagem para os desenvolvedores, registrando alguma informação para melhoria do código no futuro.
   *
   * @param msg Mensagem a ser registrada
   */
  public default void logImprovement(String msg) {
    log(RFWLogSeverity.DEV, msg, null, null);
  }

  /**
   * Realiza o log com a prioridade DEBUG.
   *
   * @param msg Mensagem a ser registrada
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags são utilizadas para ajudar a filtrar vários eventos de uma mesma natureza, não jogue informações que só aparecerão em um único evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public default void logDebug(String msg, String... tags) {
    log(RFWLogSeverity.DEBUG, msg, null, null, tags);
  }

  /**
   * Realiza o log de uma mensagem para os desenvolvedores, registrando alguma informação para melhoria do código no futuro.
   *
   * @param msg Mensagem a ser registrada
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags são utilizadas para ajudar a filtrar vários eventos de uma mesma natureza, não jogue informações que só aparecerão em um único evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public default void logImprovement(String msg, String... tags) {
    log(RFWLogSeverity.DEV, msg, null, null, tags);
  }
}
