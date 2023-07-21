package br.eng.rodrigogml.rfw.kernel.logger;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationGroupException;

/**
 * Description: Interface de defini��o para implementa��o da m�quina de Log do sistema.<br>
 * A implementa��o desta interface permite que as chamadas de log feitas na classe {@link RFWLogger} sejam repassadas para a implementa��o diretamente.
 *
 * @author Rodrigo Leit�o
 * @since 10.0 (20 de jul. de 2023)
 */
public interface RFWLoggerImplementation {

  /**
   * Prioridades/Severidade do evento.<br>
   */
  public static enum RFWLogSeverity {
    /**
     * Registra um objeto no LOG. O RFWLogger far� o m�ximo para imprimir as informa��es do objeto no log.
     */
    OBJECT,
    /**
     * Registra uma informa��o com a severidade DEBUG
     */
    DEBUG,
    /**
     * Registra uma informa��o com a severidade INFO
     */
    INFO,
    /**
     * Registra uma informa��o com a severidade WARN
     */
    WARN,
    /**
     * Registra uma informa��o com a severidade ERROR
     */
    ERROR,
    /**
     * Registra uma informa��o para os desenvolvedores. Criado para que seja poss�vel criar notifica��es para os desenvolvedores quando alguma nova informa��o entrada no sistema � relevante a ponto de ser informada para o DEV mas n�o � um erro ou aviso.
     */
    DEV,
    /**
     * Registra uma Exce��o que representa uma Valida��o.
     */
    VALIDATION,
    /**
     * Registra uma Exce��o que representa Erro/Problema.
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
   * @param e Exce��o a ser Logada.
   */
  public default void logException(Throwable e) {
    RFWLogSeverity severity = RFWLogSeverity.EXCEPTION;
    if (e instanceof RFWValidationException || e instanceof RFWValidationGroupException) {
      severity = RFWLogSeverity.VALIDATION;
    }
    String exPoint = e.getStackTrace()[0].toString();
    log(severity, e.getLocalizedMessage(), RFWLogger.convertExceptionToString(e), exPoint);
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
    log(severity, e.getLocalizedMessage(), RFWLogger.convertExceptionToString(e), exPoint, tags);
  }

  /**
   * Faz o log com diversas informa��es que podem ou n�o serem passadas.
   *
   * @param severity Enumeration indicando a severidade (n�vel) do log.
   * @param msg Mensagem a ser registrada no log. Esta mensagem deve ser curta e o mais explicativa poss�vel.
   * @param content Conte�do em caso de conte�do anexo. (Grande Volume de Dados).
   * @param exPoint Define o ponto da exception, assim consegimos encontrar mais registros da mesma exception.
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Utilize para categorizar os tipos de logs. Para dados vol�teis ou �nicos, crie outras entradas de INFO ou DEBUG.
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
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public default void logWarn(String msg, String[] tags) {
    log(RFWLogSeverity.WARN, msg, null, null, tags);
  }

}
