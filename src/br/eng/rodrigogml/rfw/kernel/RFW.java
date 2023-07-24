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
 * Description: Classe utilitária geral do RFW com métodos utilitários comuns genéricos.<br>
 *
 * @author Rodrigo GML
 * @since 10.0 (12 de out de 2020)
 */
public class RFW {

  /**
   * Constante com o BigDecimal de "100", para evitar sua construção o tempo todo.<br>
   * Por ser a base do percentual, e da divisão principal financeira (toda moeda é dividida em centavos por padrão) ela é requisitada o tempo todo.
   */
  public static final BigDecimal BIGHUNDRED = new BigDecimal("100");

  /**
   * Método padrão de arredondamento do sistema.<br>
   * Valor Padrão inicial: RoundingMode.HALF_EVEN.
   */
  private static RoundingMode roundingMode = RoundingMode.HALF_EVEN;

  /**
   * Locale padrão do sistema.<br>
   * Valor Padrão inicial: Locale("pt", "BR").
   */
  private static Locale locale = new Locale("pt", "BR"); // Locale.of("pt", "BR"); //JDK 20

  /**
   * ZoneID padrão do sistema.<br>
   * Valor padrão inicial: "America/Sao_Paulo.
   */
  private static ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

  /**
   * Batiza o sistema com um nome. É recomendado que se crie um label utilizando "Nome" = "Versão", algo como "RFW v10.0.0". Algo preferencialmente curto que identifique o sistema e sua versão.<br>
   * Lembrando que esse nome deve abrangir o escopo da instância da classe estática {@link RFW}, uma vez que esse nome será utilizado para o mesmo escopo.<br>
   * A função deste nome é identificação, e será utilizado por exemplo pelo serviço RFWLogger para gerar tags nos relatórios criados.
   */
  private static String systemName = null;

  /**
   * Indicador se foi solicitado que o sistema deve finalizar.
   */
  private static boolean shuttingDown = false;

  /**
   * Construtor privado para uma classe completamente estática
   */
  private RFW() {
  }

  /**
   * Inicializa o valor padrão do ZoneId utilizado no Sistema. Gera efeitos em todo o sistema que utilizar a mesma instância estática do RFW.<br>
   * Valor padrão inicial: "America/Sao_Paulo".
   *
   * @param zoneId ZoneID para ser utilizado em todo o framework.
   */
  public static void initializeZoneID(ZoneId zoneId) {
    RFW.zoneId = zoneId;
  }

  /**
   * Inicializa o valor padrão do Locale utilizado no Sistema. Gera efeitos em todo o sistema que utilizar a mesma instância estática do RFW.<br>
   * Valor Padrão inicial: Locale("pt", "BR").
   *
   * @param locale Locale para ser uitilizado em todo o framework.
   */
  public static void initializeLocale(Locale locale) {
    RFW.locale = locale;
  }

  /**
   * Define o método padrão de arredondamento do sistema.<br>
   * Valor Padrão inicial: RoundingMode.HALF_EVEN.
   *
   * @param roundingMode the new método padrão de arredondamento do sistema
   */
  public static void initializeRoundingMode(RoundingMode roundingMode) {
    RFW.roundingMode = roundingMode;
  }

  /**
   * Define o {@link RFWLoggerImplementation} que tratará os registros de log do framework.
   *
   * @param rfwLoggerImplementation Implementação desejada de tratamento dos Logs do Framework / Sistema.
   * @throws RFWException Lançado em caso de falha, como valor null.
   */
  public static void initializeRFWLogger(RFWLoggerImplementation rfwLoggerImplementation) throws RFWException {
    RFWLogger.setImpl(rfwLoggerImplementation);
  }

  /**
   * Batiza o sistema com um nome. É recomendado que se crie um label utilizando "Nome" = "Versão", algo como "RFW v10.0.0". Algo preferencialmente curto que identifique o sistema e sua versão.<br>
   * Lembrando que esse nome deve abrangir o escopo da instância da classe estática {@link RFW}, uma vez que esse nome será utilizado para o mesmo escopo.<br>
   * A função deste nome é identificação, e será utilizado por exemplo pelo serviço RFWLogger para gerar tags nos relatórios criados.
   *
   * @param systemName
   */
  public static void initializeSystemName(String systemName) {
    RFW.systemName = systemName;
  }

  /**
   * Este método inicializa {@link RFWBundle} com um novo arquivo.<br>
   * Note que cada novo arquivo carregado é lido sobre o mesmo properties. Isso faz com que em caso de conflito de chaves o conteúdo do último arquivo lido se sobreponha. Embora pareça uma falha, a ideia é proposital, assim é possível substituir mensagens padrão do RFWDeprec pelo sistema sendo feito.
   *
   * @param bundleName
   * @throws RFWException
   */
  public static void initializeBundle(String bundleName) throws RFWException {
    RFWBundle.loadBundle(bundleName);
  }

  /**
   * Retorna o DateTime do sistema, levando em consideração o {@link ZoneId} configurado nesta classe.
   *
   * @return DateTime atual
   */
  public static LocalDateTime getDateTime() {
    return LocalDateTime.now(getZoneId());
  }

  /**
   * Retorna o Time do sistema, levando em consideração o {@link ZoneId} configurado nesta classe.
   *
   * @return Time atual
   */
  public static LocalTime getTime() {
    return LocalTime.now(getZoneId());
  }

  /**
   * Retorna o Date do sistema, levando em consideração o {@link ZoneId} configurado nesta classe.
   *
   * @return Date Atual
   */
  public static LocalDate getDate() {
    return LocalDate.now(getZoneId());
  }

  /**
   * Retorna o formatador de DateTime Padrão do sistema: "dd/MM/uuuu HH:mm:ss".
   *
   * @return DateTimeFormatter padrão.
   */
  public static DateTimeFormatter getDateTimeFormattter() {
    return DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
  }

  /**
   * Retorna o formatador de DateTime Padrão do sistema: "HH:mm:ss".
   *
   * @return DateTimeFormatter padrão.
   */
  public static DateTimeFormatter getTimeFormattter() {
    return DateTimeFormatter.ofPattern("HH:mm:ss");
  }

  /**
   * Retorna o formatador de DateTime Padrão do sistema: "dd/MM/uuuu".
   *
   * @return DateTimeFormatter padrão.
   */
  public static DateTimeFormatter getDateFormattter() {
    return DateTimeFormatter.ofPattern("dd/MM/uuuu");
  }

  /**
   * Locale padrão do sistema.<br>
   * Valor Padrão inicial: Locale("pt", "BR").
   *
   * @return locale padrão do sistema
   */
  public static Locale getLocale() {
    return locale;
  }

  /**
   * Get método padrão de arredondamento do sistema.<br>
   * Valor Padrão inicial: RoundingMode.HALF_EVEN.
   *
   * @return the método padrão de arredondamento do sistema
   */
  public static RoundingMode getRoundingMode() {
    return roundingMode;
  }

  /**
   * Get zoneId padrão do sistema.<br>
   * Valor padrão inicial: "America/Sao_Paulo.
   *
   * @return the zoneId padrão do sistema
   */
  public static ZoneId getZoneId() {
    return zoneId;
  }

  /**
   * Esta flag tem o objetivo de permitir que o código tenha trechos que só devem ser executados quando em desenvolvimento, como por exemplo alguns prints para o console, ou mesmo algum trecho de código de testes pode ser encapsulado em um "if" que testa essa variável.<br>
   * Esta método retorna TRUE, caso encontre um arquivo "rfwdev.txt" na raiz do HD. No caso do Windows: "C:\rfwdev.txt". <Br>
   * O arquivo não precisa de nenhum conteúdo, apenas existir.
   *
   * @return true, se o arquivo for encontrado.
   */
  public static boolean isDevelopmentEnvironment() {
    return RUFile.fileExists("c:\\rfwdev.txt");
  }

  /**
   * Batiza o sistema com um nome. É recomendado que se crie um label utilizando "Nome" = "Versão", algo como "RFW v10.0.0". Algo preferencialmente curto que identifique o sistema e sua versão.<br>
   * Lembrando que esse nome deve abrangir o escopo da instância da classe estática {@link RFW}, uma vez que esse nome será utilizado para o mesmo escopo.<br>
   * A função deste nome é identificação, e será utilizado por exemplo pelo serviço RFWLogger para gerar tags nos relatórios criados.
   *
   * @return identificação do sistema
   */
  public static String getSystemName() {
    return systemName;
  }

  /**
   * Este método simplifica a impressão em console quando estamos em desenvolvimento.<Br>
   * Tem a mesma função que o código:<br>
   *
   * <pre>
   * if (RFW.isDevelopmentEnvironment()) System.out.println(content);
   * </pre>
   *
   * @param content Conteúdo a ser impresso no console
   */
  public static void pDev(String content) {
    if (RFW.isDevelopmentEnvironment()) System.out.println(content);
  }

  /**
   * Este método simplifica a impressão em console quando estamos em desenvolvimento.<Br>
   * Tem a mesma função que o código:<br>
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
   * Faz o mesmo que o {@link Thread#sleep(long)}, porém já captura o {@link InterruptedException} caso ele ocorra.<Br>
   * Para os casos em que a exception não é importante, deixa o código mais limpo.
   *
   * @param delay tempo em milisegundos que o código (Thread atual) deverá aguardar.
   */
  public static void sleep(long delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
    }
  }

  /**
   * Ao chamar este método, todos os serviços do RFW serão sinalizados para que finalizem seus serviços e Threads em andamento o mais rápido possível.<br>
   * A chamada deste método é irreversível.
   */
  public static void shutdownFW() {
    RFW.shuttingDown = true;
  }

  /**
   * @return Recupera se o Framework foi sinalizado que deve finalizar. Quando true, todos os seviços e Thread do RFW devem se encerrar para que a aplicação faça um undeploy
   */
  public static boolean isShuttingDown() {
    return RFW.shuttingDown;
  }

  /**
   * Executa uma tarefa em outra thread. A idéia é facilitar a execução de algumas tarefas em uma thread paralela para liberar a execução do código principal. Muito útil para tarefas que precisam ser disparadas mas não precisamos do resultado imediato para continuar a execução do método principal.
   *
   * @param threadName Nome da Thread
   * @param daemon Define se a thread de execução deve ser daemon ou não. O sistema se encerra quando apenas Threads do tipo daemon estão em execução. Em outras palavras, threads daemon não precisam ser forçadas a terminar para que o sistema finalize.
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
          // Garante que o Timer morra antes de finalizar a tarefa. Se a tarefa não for encerrada o Timer mantém a thread ativa esperando com a task na fila.
          t.cancel(); // Cancela a tarefa atual
          t.purge(); // Remove a referencia dessa tarefa na "queue" do Timer. Ao não encontrar nada na queue o Timer permite que a Thread termine ao invés de ficar aguardando outro reinício.
        }
      }
    }, delay);
    return t;
  }
}
