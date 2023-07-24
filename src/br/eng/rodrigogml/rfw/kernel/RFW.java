package br.eng.rodrigogml.rfw.kernel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import br.eng.rodrigogml.rfw.kernel.bundle.RFWBundle;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLogger;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLoggerImplementation;
import br.eng.rodrigogml.rfw.kernel.utils.RUFile;

/**
 * Description: Classe utilit�ria geral do RFW com m�todos utilit�rios comuns gen�ricos.<br>
 *
 * @author Rodrigo GML
 * @since 10.0 (12 de out de 2020)
 */
public class RFW {

  /**
   * Constante com o BigDecimal de "100", para evitar sua constru��o o tempo todo.<br>
   * Por ser a base do percentual, e da divis�o principal financeira (toda moeda � dividida em centavos por padr�o) ela � requisitada o tempo todo.
   */
  public static final BigDecimal BIGHUNDRED = new BigDecimal("100");

  /**
   * M�todo padr�o de arredondamento do sistema.<br>
   * Valor Padr�o inicial: RoundingMode.HALF_EVEN.
   */
  private static RoundingMode roundingMode = RoundingMode.HALF_EVEN;

  /**
   * Locale padr�o do sistema.<br>
   * Valor Padr�o inicial: Locale("pt", "BR").
   */
  private static Locale locale = new Locale("pt", "BR"); // Locale.of("pt", "BR"); //JDK 20

  /**
   * ZoneID padr�o do sistema.<br>
   * Valor padr�o inicial: "America/Sao_Paulo.
   */
  private static ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

  /**
   * Batiza o sistema com um nome. � recomendado que se crie um label utilizando "Nome" = "Vers�o", algo como "RFW v10.0.0". Algo preferencialmente curto que identifique o sistema e sua vers�o.<br>
   * Lembrando que esse nome deve abrangir o escopo da inst�ncia da classe est�tica {@link RFW}, uma vez que esse nome ser� utilizado para o mesmo escopo.<br>
   * A fun��o deste nome � identifica��o, e ser� utilizado por exemplo pelo servi�o RFWLogger para gerar tags nos relat�rios criados.
   */
  private static String systemName = null;

  /**
   * Indicador se foi solicitado que o sistema deve finalizar.
   */
  private static boolean shuttingDown = false;

  /**
   * Construtor privado para uma classe completamente est�tica
   */
  private RFW() {
  }

  /**
   * Inicializa o valor padr�o do ZoneId utilizado no Sistema. Gera efeitos em todo o sistema que utilizar a mesma inst�ncia est�tica do RFW.<br>
   * Valor padr�o inicial: "America/Sao_Paulo".
   *
   * @param zoneId ZoneID para ser utilizado em todo o framework.
   */
  public static void initializeZoneID(ZoneId zoneId) {
    RFW.zoneId = zoneId;
  }

  /**
   * Inicializa o valor padr�o do Locale utilizado no Sistema. Gera efeitos em todo o sistema que utilizar a mesma inst�ncia est�tica do RFW.<br>
   * Valor Padr�o inicial: Locale("pt", "BR").
   *
   * @param locale Locale para ser uitilizado em todo o framework.
   */
  public static void initializeLocale(Locale locale) {
    RFW.locale = locale;
  }

  /**
   * Define o m�todo padr�o de arredondamento do sistema.<br>
   * Valor Padr�o inicial: RoundingMode.HALF_EVEN.
   *
   * @param roundingMode the new m�todo padr�o de arredondamento do sistema
   */
  public static void initializeRoundingMode(RoundingMode roundingMode) {
    RFW.roundingMode = roundingMode;
  }

  /**
   * Define o {@link RFWLoggerImplementation} que tratar� os registros de log do framework.
   *
   * @param rfwLoggerImplementation Implementa��o desejada de tratamento dos Logs do Framework / Sistema.
   * @throws RFWException Lan�ado em caso de falha, como valor null.
   */
  public static void initializeRFWLogger(RFWLoggerImplementation rfwLoggerImplementation) throws RFWException {
    RFWLogger.setImpl(rfwLoggerImplementation);
  }

  /**
   * Batiza o sistema com um nome. � recomendado que se crie um label utilizando "Nome" = "Vers�o", algo como "RFW v10.0.0". Algo preferencialmente curto que identifique o sistema e sua vers�o.<br>
   * Lembrando que esse nome deve abrangir o escopo da inst�ncia da classe est�tica {@link RFW}, uma vez que esse nome ser� utilizado para o mesmo escopo.<br>
   * A fun��o deste nome � identifica��o, e ser� utilizado por exemplo pelo servi�o RFWLogger para gerar tags nos relat�rios criados.
   *
   * @param systemName
   */
  public static void initializeSystemName(String systemName) {
    RFW.systemName = systemName;
  }

  /**
   * Este m�todo inicializa {@link RFWBundle} com um novo arquivo.<br>
   * Note que cada novo arquivo carregado � lido sobre o mesmo properties. Isso faz com que em caso de conflito de chaves o conte�do do �ltimo arquivo lido se sobreponha. Embora pare�a uma falha, a ideia � proposital, assim � poss�vel substituir mensagens padr�o do RFWDeprec pelo sistema sendo feito.
   *
   * @param bundleName
   * @throws RFWException
   */
  public static void initializeBundle(String bundleName) throws RFWException {
    RFWBundle.loadBundle(bundleName);
  }

  /**
   * Retorna o DateTime do sistema, levando em considera��o o {@link ZoneId} configurado nesta classe.
   *
   * @return DateTime atual
   */
  public static LocalDateTime getDateTime() {
    return LocalDateTime.now(getZoneId());
  }

  /**
   * Retorna o Time do sistema, levando em considera��o o {@link ZoneId} configurado nesta classe.
   *
   * @return Time atual
   */
  public static LocalTime getTime() {
    return LocalTime.now(getZoneId());
  }

  /**
   * Retorna o Date do sistema, levando em considera��o o {@link ZoneId} configurado nesta classe.
   *
   * @return Date Atual
   */
  public static LocalDate getDate() {
    return LocalDate.now(getZoneId());
  }

  /**
   * Retorna o formatador de DateTime Padr�o do sistema: "dd/MM/uuuu HH:mm:ss".
   *
   * @return DateTimeFormatter padr�o.
   */
  public static DateTimeFormatter getDateTimeFormattter() {
    return DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
  }

  /**
   * Retorna o formatador de DateTime Padr�o do sistema: "HH:mm:ss".
   *
   * @return DateTimeFormatter padr�o.
   */
  public static DateTimeFormatter getTimeFormattter() {
    return DateTimeFormatter.ofPattern("HH:mm:ss");
  }

  /**
   * Retorna o formatador de DateTime Padr�o do sistema: "dd/MM/uuuu".
   *
   * @return DateTimeFormatter padr�o.
   */
  public static DateTimeFormatter getDateFormattter() {
    return DateTimeFormatter.ofPattern("dd/MM/uuuu");
  }

  /**
   * Locale padr�o do sistema.<br>
   * Valor Padr�o inicial: Locale("pt", "BR").
   *
   * @return locale padr�o do sistema
   */
  public static Locale getLocale() {
    return locale;
  }

  /**
   * Get m�todo padr�o de arredondamento do sistema.<br>
   * Valor Padr�o inicial: RoundingMode.HALF_EVEN.
   *
   * @return the m�todo padr�o de arredondamento do sistema
   */
  public static RoundingMode getRoundingMode() {
    return roundingMode;
  }

  /**
   * Get zoneId padr�o do sistema.<br>
   * Valor padr�o inicial: "America/Sao_Paulo.
   *
   * @return the zoneId padr�o do sistema
   */
  public static ZoneId getZoneId() {
    return zoneId;
  }

  /**
   * Esta flag tem o objetivo de permitir que o c�digo tenha trechos que s� devem ser executados quando em desenvolvimento, como por exemplo alguns prints para o console, ou mesmo algum trecho de c�digo de testes pode ser encapsulado em um "if" que testa essa vari�vel.<br>
   * Esta m�todo retorna TRUE, caso encontre um arquivo "rfwdev.txt" na raiz do HD. No caso do Windows: "C:\rfwdev.txt". <Br>
   * O arquivo n�o precisa de nenhum conte�do, apenas existir.
   *
   * @return true, se o arquivo for encontrado.
   */
  public static boolean isDevelopmentEnvironment() {
    return RUFile.fileExists("c:\\rfwdev.txt");
  }

  /**
   * Batiza o sistema com um nome. � recomendado que se crie um label utilizando "Nome" = "Vers�o", algo como "RFW v10.0.0". Algo preferencialmente curto que identifique o sistema e sua vers�o.<br>
   * Lembrando que esse nome deve abrangir o escopo da inst�ncia da classe est�tica {@link RFW}, uma vez que esse nome ser� utilizado para o mesmo escopo.<br>
   * A fun��o deste nome � identifica��o, e ser� utilizado por exemplo pelo servi�o RFWLogger para gerar tags nos relat�rios criados.
   *
   * @return identifica��o do sistema
   */
  public static String getSystemName() {
    return systemName;
  }

  /**
   * Este m�todo simplifica a impress�o em console quando estamos em desenvolvimento.<Br>
   * Tem a mesma fun��o que o c�digo:<br>
   *
   * <pre>
   * if (RFW.isDevelopmentEnvironment()) System.out.println(content);
   * </pre>
   *
   * @param content Conte�do a ser impresso no console
   */
  public static void pDev(String content) {
    if (RFW.isDevelopmentEnvironment()) System.out.println(content);
  }

  /**
   * Este m�todo simplifica a impress�o em console quando estamos em desenvolvimento.<Br>
   * Tem a mesma fun��o que o c�digo:<br>
   *
   * <pre>
   * if (RFW.isDevelopmentEnvironment()) e.printStackTrace();
   * </pre>
   *
   * @param t Throwable a ser impresso no console
   */
  public static void pDev(Throwable t) {
    if (RFW.isDevelopmentEnvironment()) t.printStackTrace();
    ;
  }

  /**
   * Faz o mesmo que o {@link Thread#sleep(long)}, por�m j� captura o {@link InterruptedException} caso ele ocorra.<Br>
   * Para os casos em que a exception n�o � importante, deixa o c�digo mais limpo.
   *
   * @param delay tempo em milisegundos que o c�digo (Thread atual) dever� aguardar.
   */
  public static void sleep(long delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
    }
  }

  /**
   * Ao chamar este m�todo, todos os servi�os do RFW ser�o sinalizados para que finalizem seus servi�os e Threads em andamento o mais r�pido poss�vel.<br>
   * A chamada deste m�todo � irrevers�vel.
   */
  public static void shutdownFW() {
    RFW.shuttingDown = true;
  }

  /**
   * @return Recupera se o Framework foi sinalizado que deve finalizar. Quando true, todos os sevi�os e Thread do RFW devem se encerrar para que a aplica��o fa�a um undeploy
   */
  public static boolean isShuttingDown() {
    return RFW.shuttingDown;
  }

  /**
   * Executa uma tarefa em outra thread. A id�ia � facilitar a execu��o de algumas tarefas em uma thread paralela para liberar a execu��o do c�digo principal. Muito �til para tarefas que precisam ser disparadas mas n�o precisamos do resultado imediato para continuar a execu��o do m�todo principal.
   *
   * @param threadName Nome da Thread
   * @param daemon Define se a thread de execu��o deve ser daemon ou n�o. O sistema se encerra quando apenas Threads do tipo daemon est�o em execu��o. Em outras palavras, threads daemon n�o precisam ser for�adas a terminar para que o sistema finalize.
   * @param delay Tempo em milisegundos para aguardar antes de executar a tarefa
   * @param task Tarefa a ser executada
   */
  public static Timer runLater(String threadName, boolean daemon, long delay, Runnable task) {
    Timer t = new Timer(threadName, daemon);
    t.schedule(new TimerTask() {
      @Override
      public void run() {
        try {
          // Executa a tarefa passada
          task.run();
        } finally {
          // Garante que o Timer morra antes de finalizar a tarefa. Se a tarefa n�o for encerrada o Timer mant�m a thread ativa esperando com a task na fila.
          t.cancel(); // Cancela a tarefa atual
          t.purge(); // Remove a referencia dessa tarefa na "queue" do Timer. Ao n�o encontrar nada na queue o Timer permite que a Thread termine ao inv�s de ficar aguardando outro rein�cio.
        }
      }
    }, delay);
    return t;
  }
}
