package br.eng.rodrigogml.rfw.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import br.eng.rodrigogml.rfw.kernel.bundle.RFWBundle;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLogger;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLoggerImplementation;
import br.eng.rodrigogml.rfw.kernel.utils.RUTypes;

/**
 * Description: Classe utilitária geral do RFW com métodos utilitários comuns genéricos.<br>
 *
 * @author Rodrigo GML
 * @since 10.0 (12 de out de 2020)
 */
public class RFW {

  private static Object springEnvironment;
  private static Method springGetProperty;
  private static Properties applicationProperties;

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

  static {
    configurePropertySource();
  }

  /**
   * Construtor privado para uma classe completamente estática
   */
  private RFW() {
  }

  /**
   *
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
   * @deprecated Utilizar diretamente os métodos disponiblizados em {@link RUTypes}
   */
  @Deprecated
  public static DateTimeFormatter getDateTimeFormattter() {
    return DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
  }

  /**
   * Retorna a data e horaatual já no formado ISO
   *
   * @return
   */
  public static String getDateTimeISO() {
    return DateTimeFormatter.ISO_DATE_TIME.format(getDateTime());
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
   * Indica se a aplicação está em ambiente de desenvolvimento.
   * <p>
   * Essa flag permite a ativação condicional de trechos de código específicos para desenvolvimento, como logs adicionais, mensagens de depuração ou execuções auxiliares de teste.
   * <p>
   * O ambiente de desenvolvimento é considerado ativo quando é possível localizar o arquivo de configuração {@code rfwdev.properties}. A busca pelo arquivo segue o mecanismo padrão de resolução de propriedades da aplicação, incluindo:
   * <ul>
   * <li>{@code ./config/rfwdev.properties}</li>
   * <li>{@code ./rfwdev.properties}</li>
   * <li>{@code classpath:/config/rfwdev.properties}</li>
   * <li>{@code classpath:/rfwdev.properties}</li>
   * <li>{@code ${user.home}/rfwdev.properties} (fallback legado)</li>
   * </ul>
   * <p>
   * O arquivo não precisa conter qualquer conteúdo; sua simples existência é suficiente para caracterizar o ambiente como desenvolvimento.
   * <p>
   * Em caso de erro durante a verificação, o método retorna {@code false}. Nenhuma exceção é propagada, garantindo que o comportamento da aplicação em produção não seja impactado.
   *
   * @return {@code true} se o ambiente de desenvolvimento estiver ativo; {@code false} caso contrário.
   * @since BIS Orion
   */
  public static boolean isDevelopmentEnvironment() {
    try {
      return getDevFile() != null;
    } catch (RFWException e) {
      // Não lança nem registra exception por ser um método de teste para desenvolvimento, se não encontrar retornamos false, em caso de falhas ou problemas o desenvolvedor percebe sozinho.
      // O importante é o código não parar em hipótese alguma durante a produção
      return false;
    }
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
  }

  /**
   * Faz o mesmo que o {@link Thread#sleep(long)}, porém já captura o {@link InterruptedException} caso ele ocorra.<Br>
   * Para os casos em que a exception não é importante, deixa o código mais limpo.
   *
   * @param millisecondsDelay tempo em milisegundos que o código (Thread atual) deverá aguardar.
   */
  public static void sleep(long millisecondsDelay) {
    try {
      Thread.sleep(millisecondsDelay);
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

  /**
   * Lê uma propriedade definida no arquivo de configuração do ambiente de desenvolvimento.
   * <p>
   * O arquivo {@code rfwdev.properties} é localizado utilizando o mesmo mecanismo de busca adotado para configurações da aplicação, incluindo fallback legado. Caso o arquivo não exista, o ambiente não é considerado como desenvolvimento e o método retorna {@code null}.
   * <p>
   * O método realiza a leitura em tempo real e sem cache. A ausência da propriedade solicitada também resulta em {@code null}.
   *
   * @param property Nome da propriedade a ser lida.
   * @return Valor da propriedade encontrada, ou {@code null} se o arquivo de desenvolvimento não existir ou se a propriedade não estiver definida.
   * @throws RFWException
   *           <li>Critical - RFWERR_000002 - Falha ao ler o arquivo de properties.
   * @since BIS Orion
   */
  public static String getDevProperty(String property) throws RFWException {
    InputStream is;

    try {
      is = getDevFile();
    } catch (RFWException e) {
      // Qualquer falha na verificação do ambiente de desenvolvimento não deve impactar a execução da aplicação
      return null;
    }

    if (is == null) {
      return null;
    }

    try (InputStream in = is) {
      Properties properties = new Properties();
      properties.load(in);
      return properties.getProperty(property);
    } catch (IOException e) {
      throw new RFWCriticalException("RFWERR_000002", new String[] { "rfwdev.properties" }, e);
    }
  }

  /**
   * Obtém o valor de uma propriedade da aplicação.
   * <p>
   * Se o Spring estiver disponível, a resolução é delegada ao {@code Environment}, garantindo comportamento idêntico ao framework, incluindo precedência, profiles e variáveis externas.
   * <p>
   * Caso contrário, o valor é obtido a partir do arquivo {@code application.properties} previamente carregado e cacheado.
   *
   * @param property Nome da propriedade a ser obtida.
   * @return Valor da propriedade, ou {@code null} se a chave não existir.
   * @throws RFWException Se nenhuma fonte de propriedades estiver configurada.
   * @since BIS Orion
   */
  public static String getAppProperty(String property) throws RFWException {
    return getAppProperty(property, null);
  }

  /**
   * Obtém o valor de uma propriedade da aplicação.
   * <p>
   * Se o Spring estiver disponível, a resolução é delegada ao {@code Environment}, garantindo comportamento idêntico ao framework, incluindo precedência, profiles e variáveis externas.
   * <p>
   * Caso contrário, o valor é obtido a partir do arquivo {@code application.properties} previamente carregado e cacheado no carregamento desta classe.
   *
   * @param property Nome da propriedade a ser obtida.
   * @param defaultValue Valor padrão retornado se a propriedade ou o arquivo não for encontrado. Quando este atributo é diferente de nulo, o método não lança exceção se o arquivo não for encontrado.
   * @return Valor da propriedade, ou {@code null} se a chave não existir.
   * @throws RFWException Se nenhuma fonte de propriedades estiver configurada e se não houver um valor padrão definido.
   * @since BIS Orion
   */
  public static String getAppProperty(String property, String defaultValue) throws RFWException {
    try {
      if (springEnvironment != null) {
        return (String) springGetProperty.invoke(springEnvironment, property);
      }
      if (applicationProperties != null) {
        return applicationProperties.getProperty(property, defaultValue);
      }
      if (defaultValue != null) {
        return defaultValue;
      }
      throw new RFWCriticalException("Nenhum arquivo de propriedades da aplicação econtrado!");
    } catch (RFWException e) {
      throw e;
    } catch (Exception e) {
      throw new RFWCriticalException("Falha ao acessar a propriedade: " + property, e);
    }
  }

  /**
   * Configura a fonte de propriedades da aplicação.
   * <p>
   * A estratégia adotada é:
   * <ol>
   * <li>Tentar localizar um {@code org.springframework.core.env.Environment} ativo via reflexão, delegando integralmente ao Spring a resolução de propriedades, respeitando sua ordem, sobreposição, profiles e variáveis de ambiente.</li>
   * <li>Na ausência do Spring, carregar manualmente o arquivo {@code application.properties} seguindo a mesma ordem de precedência do Spring Boot:
   * <ul>
   * <li>{@code ./config/application.properties}</li>
   * <li>{@code ./application.properties}</li>
   * <li>{@code classpath:/config/application.properties}</li>
   * <li>{@code classpath:/application.properties}</li>
   * </ul>
   * mantendo o comportamento de sobreposição.</li>
   * </ol>
   * <p>
   * Este método é executado uma única vez, no carregamento da classe, e seu resultado é cacheado para evitar custo de reflexão ou I/O em chamadas subsequentes.
   *
   * @since BIS Orion
   */
  private static void configurePropertySource() {
    try {
      Object applicationContext = Class.forName("org.springframework.context.ApplicationContextProvider").getMethod("getApplicationContext").invoke(null);

      if (applicationContext != null) {
        springEnvironment = applicationContext.getClass().getMethod("getEnvironment").invoke(applicationContext);
        springGetProperty = springEnvironment.getClass().getMethod("getProperty", String.class);
        return;
      }
    } catch (Throwable ignored) {
    }

    Properties p = new Properties();

    try {
      File f1 = new File("./config/application.properties");
      if (f1.exists()) p.load(new FileInputStream(f1));

      File f2 = new File("./application.properties");
      if (f2.exists()) p.load(new FileInputStream(f2));

      InputStream c1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/application.properties");
      if (c1 != null) p.load(c1);

      InputStream c2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
      if (c2 != null) p.load(c2);
    } catch (IOException ignored) {
    }
    applicationProperties = p.isEmpty() ? null : p;
  }

  /**
   * Lê uma propriedade dentro do arquivo de definições do ambiente de desenvolvimento utilizando o método {@link #getDevProperty(String)} e verifica se a propriedade existe e está definida como "true".
   *
   * @param property Propriedade a ser verificada
   * @return true se o arquivo e a propriedade for encontrada com o valor true (case insensitive). False caso o contrário.
   * @throws RFWException Só é lançado em caso de erro de leitura ou de sistema, em geral o método retorna false se a propriedade não for encontrada com sucesso e com o valor 'true'.
   */
  public static boolean isDevPropertyTrue(String property) throws RFWException {
    String value = getDevProperty(property);
    return value != null && "true".equalsIgnoreCase(value);
  }

  /**
   * Localiza e abre um {@link InputStream} para o arquivo de propriedades de desenvolvimento.
   * <p>
   * A existência desse arquivo configura o sistema como ambiente de desenvolvimento, independentemente de seu conteúdo.
   * <p>
   * A busca é realizada na seguinte ordem:
   * <ol>
   * <li>Utilizando o fallback padrão de configuração, seguindo a ordem:
   * <ul>
   * <li>{@code ./config/rfwdev.properties}</li>
   * <li>{@code ./rfwdev.properties}</li>
   * <li>{@code classpath:/config/rfwdev.properties}</li>
   * <li>{@code classpath:/rfwdev.properties}</li>
   * </ul>
   * </li>
   * <li>Como fallback adicional (retrocompatibilidade): {@code ${user.home}/rfwdev.properties}</li>
   * </ol>
   * <p>
   * O método apenas localiza e abre o recurso, não realizando qualquer leitura. O {@link InputStream} retornado deve ser fechado pelo chamador.
   * <p>
   * Caso nenhum arquivo seja encontrado, o método retorna {@code null}.
   *
   * @return {@link InputStream} aberto para o arquivo de desenvolvimento ou {@code null} se não encontrado.
   * @throws RFWException
   *           <li>Critical - RFWERR_000002 - Falha ao ler arquivo de properties '${0}'!
   */
  public static InputStream getDevFile() throws RFWException {
    final String rfwdevFileName = "rfwdev.properties";

    InputStream is = searchConfigProperty(rfwdevFileName);
    if (is != null) {
      return is;
    }

    try {
      File devFile = new File(System.getProperty("user.home") + File.separatorChar + rfwdevFileName);

      if (devFile.exists()) {
        return new FileInputStream(devFile);
      }
      return null;
    } catch (IOException e) {
      throw new RFWCriticalException("RFWERR_000002", new String[] { rfwdevFileName }, e); // Falha ao ler arquivo de properties '${0}'!
    }
  }

  /**
   * Carrega e retorna um {@link Properties} a partir de um arquivo de propriedades específico, realizando a leitura em tempo real e sem qualquer tipo de cache.
   * <p>
   * Quando é informado apenas o nome do arquivo, a busca é realizada seguindo a ordem de precedência adotada pelo Spring Boot:
   * <ul>
   * <li>{@code ./config/&lt;propertiesFile&gt;}</li>
   * <li>{@code ./&lt;propertiesFile&gt;}</li>
   * <li>{@code classpath:/config/&lt;propertiesFile&gt;}</li>
   * <li>{@code classpath:/&lt;propertiesFile&gt;}</li>
   * </ul>
   * <p>
   * Caso seja informado um caminho contendo diretórios (relativo ou absoluto), o fallback padrão é ignorado e apenas o arquivo explicitamente indicado é considerado.
   * <p>
   * Se o arquivo for alterado entre chamadas, o conteúdo retornado refletirá o estado atual. Se nenhum arquivo for localizado no momento da chamada, é lançada exceção.
   *
   * @param propertiesFile Nome do arquivo ou caminho completo do arquivo de propriedades.
   * @return Instância de {@link Properties} carregada no momento da chamada.
   * @throws RFWException
   *           <li>Critical - RFWERR_000001 - Arquivo de properties '${0}' não encontrado!
   *           <li>Critical - RFWERR_000002 - Falha ao ler arquivo de properties '${0}'!
   * @since BIS Orion
   */
  public static Properties loadPropertiesFrom(String propertiesFile) throws RFWException {
    InputStream in = searchConfigProperty(propertiesFile);

    if (in == null) {
      throw new RFWCriticalException("RFWERR_000001", new String[] { propertiesFile }); // Arquivo de properties '${0}' não encontrado!
    }

    try (InputStream is = in) {
      Properties p = new Properties();
      p.load(is);
      return p;
    } catch (IOException e) {
      throw new RFWCriticalException("RFWERR_000002", new String[] { propertiesFile }, e); // Falha ao ler arquivo de properties '${0}'!
    }
  }

  /**
   * Localiza e abre um {@link InputStream} para um arquivo de configuração (.properties), realizando a busca em tempo real e sem qualquer tipo de cache.
   * <p>
   * Quando é informado apenas o nome do arquivo (sem diretórios), a busca segue a mesma ordem de precedência adotada pelo Spring Boot para arquivos de configuração externos e de classpath:
   * <ul>
   * <li>{@code ./config/&lt;configPropertyFileName&gt;}</li>
   * <li>{@code ./&lt;configPropertyFileName&gt;}</li>
   * <li>{@code classpath:/config/&lt;configPropertyFileName&gt;}</li>
   * <li>{@code classpath:/&lt;configPropertyFileName&gt;}</li>
   * </ul>
   * <p>
   * Caso seja informado um caminho contendo diretórios (relativo ou absoluto), o mecanismo de fallback é ignorado e apenas o arquivo explicitamente indicado é considerado.
   * <p>
   * O método apenas localiza e abre o recurso, não realizando qualquer leitura do conteúdo. O {@link InputStream} retornado deve ser obrigatoriamente fechado pelo chamador.
   * <p>
   * Se nenhum recurso for encontrado no momento da chamada, o método retorna {@code null}.
   *
   * @param configPropertyFileName Nome do arquivo de propriedades ou caminho completo do arquivo.
   * @return {@link InputStream} aberto para o recurso localizado, ou {@code null} se não encontrado.
   * @throws RFWException
   *           <li>Critical - RFWERR_000002 - Falha ao abrir arquivo de properties '${0}'.
   * @since BIS Orion
   */
  public static InputStream searchConfigProperty(String configPropertyFileName) throws RFWException {
    try {
      File explicitFile = new File(configPropertyFileName);
      if (explicitFile.getParent() != null) {
        if (explicitFile.exists()) {
          return new FileInputStream(explicitFile);
        }
      } else {
        File f1 = new File("./config/" + configPropertyFileName);
        if (f1.exists()) {
          return new FileInputStream(f1);
        }

        File f2 = new File("./" + configPropertyFileName);
        if (f2.exists()) {
          return new FileInputStream(f2);
        }

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        InputStream is = cl.getResourceAsStream("config/" + configPropertyFileName);
        if (is != null) {
          return is;
        }

        is = cl.getResourceAsStream(configPropertyFileName);
        if (is != null) {
          return is;
        }
      }
      return null;
    } catch (IOException e) {
      throw new RFWCriticalException("RFWERR_000002", new String[] { configPropertyFileName }, e); // Falha ao lêr arquivo de properties '${0}'!
    }
  }
}
