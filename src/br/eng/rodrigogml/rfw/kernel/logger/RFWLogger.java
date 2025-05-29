package br.eng.rodrigogml.rfw.kernel.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

import br.eng.rodrigogml.rfw.kernel.bundle.RFWBundle;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;
import br.eng.rodrigogml.rfw.kernel.utils.RUMachine;
import br.eng.rodrigogml.rfw.kernel.utils.RUReflex;

/**
 * Description: Esta classe centraliza as chamadas de Log do sistema, e repassa as chamadas para a classe de implementa��o ativa para registrar e salvar os registros de Log.<br>
 * Al�m disso, cont�m alguns m�todos est�ticos para ajudar na gera��o de informa��es para o log.
 *
 * @author Rodrigo Leit�o
 * @since 10.0 (20 de jul. de 2023)
 */
public final class RFWLogger {

  /**
   * Implementa��o de registro de log sendo utilizada no momento
   */
  private static RFWLoggerImplementation impl = new RFWLoggerImplementation() {

    @Override
    public void log(RFWLogSeverity severity, String msg, String content, String exPoint, String... tags) {
      System.out.println("#RFWLogger N�o Definido# ");
      try {
        System.out.println("#\t ClassLoaderName: " + RUMachine.getClassLoaderName(this.getClass()));
      } catch (RFWException e) {
        System.out.println("\t ClassLoaderName: <Erro ao Obter: " + e.getMessage() + ">");
      }
      System.out.print('\t' + severity.name());
      System.out.print(' ');
      System.out.println(msg);
      if (content != null) {
        System.out.println("==================================");
        System.out.println(content);
        System.out.println("==================================");
      }
      if (exPoint != null) {
        System.out.println("\tExpoint: " + exPoint);
      }
      if (tags != null) {
        System.out.println("\tTags:");
        for (String tag : tags) {
          System.out.println("\t - " + tag);
        }
      }
    }
  };

  /**
   * Construtor privado para classe exclusivamente est�tica.
   */
  private RFWLogger() {
  }

  /**
   * Realiza o log com a prioridade ERROR
   *
   * @param msg Mensagem a ser registrada
   */
  public static void logError(String msg) {
    impl.logError(msg);
  }

  /**
   * Realiza o log com a prioridade ERROR
   *
   * @param msg Mensagem a ser registrada
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public final static void logError(String msg, String... tags) {
    impl.logError(msg, tags);
  }

  /**
   * Faz o log de uma exception.
   *
   * @param e Exce��o a ser Logada.
   */
  public synchronized final static void logException(Throwable e) {
    impl.logException(e);
  }

  /**
   * Faz o log de uma exception.
   *
   * @param e Exce��o a ser Logada.
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public synchronized final static void logException(Throwable e, String... tags) {
    impl.logException(e, tags);
  }

  /**
   * Realiza o log com a prioridade WARN
   *
   * @param msg Mensagem a ser registrada
   */
  public final static void logWarn(String msg) {
    impl.logWarn(msg);
  }

  /**
   * Realiza o log com a prioridade WARN
   *
   * @param msg Mensagem a ser registrada
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public final static void logWarn(String msg, String... tags) {
    impl.logWarn(msg, tags);
  }

  /**
   * Faz o log do conte�do de um objeto. Utilizando o m�todo {@link RUReflex#printObject(Object)}
   *
   * @param msg Mensagem a ser colocada no registro do Log.
   * @param obj Objeto a ser impresso no anexo do Log.
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public final static void logObject(String msg, Object obj, String... tags) {
    impl.logObject(msg, obj, tags);
  }

  /**
   * Realiza o log com a prioridade DEBUG.
   *
   * @param msg Mensagem a ser registrada
   */
  public final static void logDebug(String msg) {
    impl.logDebug(msg);
  }

  /**
   * Realiza o log com a prioridade INFO
   *
   * @param msg Mensagem a ser registrada
   */
  public final static void logInfo(String msg) {
    impl.logInfo(msg);
  }

  /**
   * Realiza o log de uma mensagem para os desenvolvedores, registrando alguma informa��o para melhoria do c�digo no futuro.
   *
   * @param msg Mensagem a ser registrada
   */
  public final static void logImprovement(String msg) {
    impl.logImprovement(msg);
  }

  /**
   * Realiza o log com a prioridade DEBUG.
   *
   * @param msg Mensagem a ser registrada
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public final static void logDebug(String msg, String... tags) {
    impl.logDebug(msg, tags);
  }

  /**
   * Realiza o log de uma mensagem para os desenvolvedores, registrando alguma informa��o para melhoria do c�digo no futuro.
   *
   * @param msg Mensagem a ser registrada
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public final static void logImprovement(String msg, String... tags) {
    impl.logImprovement(msg, tags);
  }

  /**
   * M�todo usado para Fazer o Log do Stack de onde for chamado. Esse tipo de log � registrado como o label "DEBUG".<br>
   * Mesmo que o m�todo {@link #logStack(String, int, String...) utilizando <code>Integer.MAX_VALUE</code> como par�metro do tamanho da Pilha.
   *
   * @param msg mensagem adicional.
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public synchronized final static void logStack(String msg, String... tags) {
    impl.logDebug(msg + "\r\n<STACK>\r\n" + getInvoker(Integer.MAX_VALUE) + "</STACK>\r\n", tags);
  }

  /**
   * M�todo usado para Fazer o Log do Stack de onde for chamado. Esse tipo de log � registrado como o label "DEBUG".<br>
   * Mesmo que o m�todo {@link #logStack(String, String...)} utilizando <code>Integer.MAX_VALUE</code> como par�metro do tamanho da Pilha.
   *
   * @param msg mensagem adicional.
   * @param stacksize Tamanho da pilha a ser logada.
   */
  public synchronized final static void logStack(String msg) {
    impl.logDebug(msg + "\r\n<STACK>\r\n" + getInvoker(Integer.MAX_VALUE) + "</STACK>\r\n");
  }

  /**
   * M�todo usado para Fazer o Log do Stack de onde for chamado. Esse tipo de log � registrado como o label "DEBUG".
   *
   * @param msg mensagem adicional.
   * @param stacksize Tamanho da pilha a ser logada.
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags s�o utilizadas para ajudar a filtrar v�rios eventos de uma mesma natureza, n�o jogue informa��es que s� aparecer�o em um �nico evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public synchronized final static void logStack(String msg, int stacksize, String... tags) {
    impl.logDebug(msg + "\r\n<STACK>\r\n" + getInvoker(stacksize) + "</STACK>\r\n", tags);
  }

  /**
   * M�todo usado para Fazer o Log do Stack de onde for chamado. Esse tipo de log � registrado como o label "DEBUG".
   *
   * @param msg mensagem adicional.
   * @param stacksize Tamanho da pilha a ser logada.
   */
  public synchronized final static void logStack(String msg, int stacksize) {
    impl.logDebug(msg + "\r\n<STACK>\r\n" + getInvoker(stacksize) + "</STACK>\r\n");
  }

  /**
   * Este m�todo retorna quem foi que chamou a classe RFWLogger, para registrar de maneira f�cil onde no c�digo foi registrado cada evento.
   */
  private static final String getInvoker(int stacksize) {
    StringBuilder buff = new StringBuilder();
    StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
    for (int i = 3; i < stacktrace.length && i - 3 < stacksize; i++) {
      if (!RFWLogger.class.getCanonicalName().equals(stacktrace[i].getClassName())) {
        buff.append('\t').append(stacktrace[i].getClassName()).append(".").append(stacktrace[i].getMethodName()).append("(").append(stacktrace[i].getFileName()).append(":").append(stacktrace[i].getLineNumber()).append(")").append("\r\n");
      }
    }
    return buff.toString();
  }

  /**
   * Converte uma exception em um formato de texto para ser anexado ao LOG.
   */
  public static final String convertExceptionToString(Throwable e) {
    StringWriter sw = new StringWriter();
    sw.write("<MESSAGE>");
    if (e instanceof RFWException) sw.write(((RFWException) e).getExceptionCode() + " - ");
    sw.write(RFWBundle.get(e) + "</MESSAGE>\r\n");
    // Verifica os paremetros para exibir no LOG
    if (e instanceof RFWException && ((RFWException) e).getParams() != null) {
      for (String p : ((RFWException) e).getParams()) {
        sw.write("<PARAMETER>" + p + "</PARAMETER>\r\n");
      }
    }
    sw.write("<STACK>\r\n");
    e.printStackTrace(new PrintWriter(sw));
    sw.write("</STACK>\r\n");
    sw.flush();
    return sw.toString();
  }

  /**
   * # implementa��o de registro de log sendo utilizada no momento.
   *
   * @return implementa��o de registro de log sendo utilizada no momento
   */
  public static RFWLoggerImplementation getImpl() {
    return impl;
  }

  /**
   * # implementa��o de registro de log sendo utilizada no momento.
   *
   * @param impl implementa��o de registro de log sendo utilizada no momento
   * @throws RFWException Lan�ado se a implementa��o for nula.
   */
  public static void setImpl(RFWLoggerImplementation impl) throws RFWException {
    PreProcess.requiredNonNull(impl);
    RFWLogger.impl = impl;
  }

}
