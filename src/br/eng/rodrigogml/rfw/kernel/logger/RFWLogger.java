package br.eng.rodrigogml.rfw.kernel.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Esta classe centraliza as chamadas de Log do sistema, e repassa as chamadas para a classe de implementação ativa para registrar e salvar os registros de Log.<br>
 * Além disso, contém alguns métodos estáticos para ajudar na geração de informações para o log.
 *
 * @author Rodrigo Leitão
 * @since 10.0 (20 de jul. de 2023)
 */
public final class RFWLogger {

  /**
   * Implementação de registro de log sendo utilizada no momento
   */
  private static RFWLoggerImplementation impl = new RFWLoggerImplementation() {

    @Override
    public void log(RFWLogSeverity severity, String msg, String content, String exPoint, String... tags) {
      System.out.println("#RFWLogger Não Definido# ");
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
   * Construtor privado para classe exclusivamente estática.
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
   * Faz o log de uma exception.
   *
   * @param e Exceção a ser Logada.
   */
  public synchronized final static void logException(Throwable e) {
    impl.logException(e);
  }

  /**
   * Faz o log de uma exception.
   *
   * @param e Exceção a ser Logada.
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags são utilizadas para ajudar a filtrar vários eventos de uma mesma natureza, não jogue informações que só aparecerão em um único evento por vez nas tags. Cria um log de debug ou info para isso.
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
   * @param tags permite que se adicione tags particulares ao Log. Tenha em mente que Tags são utilizadas para ajudar a filtrar vários eventos de uma mesma natureza, não jogue informações que só aparecerão em um único evento por vez nas tags. Cria um log de debug ou info para isso.
   */
  public final static void logWarn(String msg, String... tags) {
    impl.logWarn(msg, tags);
  }

  /**
   * Converte uma exception em um formato de texto para ser anexado ao LOG.
   */
  public static final String convertExceptionToString(Throwable e) {
    StringWriter sw = new StringWriter();
    sw.write("<MESSAGE>");
    if (e instanceof RFWException) sw.write(((RFWException) e).getExceptionCode() + " - ");
    sw.write(e.getLocalizedMessage() + "</MESSAGE>\r\n");
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
   * # implementação de registro de log sendo utilizada no momento.
   *
   * @return implementação de registro de log sendo utilizada no momento
   */
  public static RFWLoggerImplementation getImpl() {
    return impl;
  }

  /**
   * # implementação de registro de log sendo utilizada no momento.
   *
   * @param impl implementação de registro de log sendo utilizada no momento
   * @throws RFWException Lançado se a implementação for nula.
   */
  public static void setImpl(RFWLoggerImplementation impl) throws RFWException {
    PreProcess.requiredNonNull(impl);
    RFWLogger.impl = impl;
  }

}
