package br.eng.rodrigogml.rfw.kernel.utils;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

/**
 * Description: Classe utilitária com métodos para acessos e operações via JNDI.<br>
 *
 * @author Rodrigo Leitão
 * @since (5 de set. de 2024)
 */
public class RUJNDI {

  /**
   * Construtor privado para classe utilitária exclusivamente statática.
   */
  private RUJNDI() {
  }

  /**
   * Faz o lookup por uma fachada utilizando um Context Local.<br>
   * Este método pode ser usado por exemplo para um módulo encontrar a fachada de outro quando estão deployed no mesmo servidor.
   *
   * @param jndiName JNDI Name para lookup.
   * @return Interface para o recurso solicitado se encontrado com sucesso.
   * @throws RFWException Lançado caso o método falhe em encontrar o recurso pelo JNDI name.
   */
  public static Object lookup(String jndiName) throws RFWException {
    Object facade = null;
    try {
      InitialContext context = new InitialContext();
      facade = context.lookup(jndiName);
    } catch (NamingException e) {
      throw new RFWCriticalException(e);
    }
    return facade;
  }

  /**
   * Recupera o contexto remoto passando um host e porta específicos.
   *
   * @param host host do servidor
   * @param port porta do servidor
   * @param user usuário para autenticação JNDI remota.
   * @param password senha para autenticação JNDI remota.
   * @return Contexto Remoto usado para recupear as fachadas dos EJBs dos módulos.
   * @throws RFWException
   */
  public static InitialContext getRemoteContextWildFly24(String host, Integer port, String user, String password) throws RFWException {
    Properties props = new Properties();
    props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
    props.put(Context.PROVIDER_URL, "http-remoting://" + host + ":" + port);
    props.put("jboss.naming.client.ejb.context", true);
    // props.put("jboss.naming.client.connect.options.org.xnio.Options.CONNECT_TIMEOUT", "60000");
    // props.put("jboss.naming.client.connect.options.org.xnio.Options.READ_TIMEOUT", "60000");
    // props.put("jboss.ejb.client.connection.timeout", "60000"); // Timeout de conexão (ms)
    // props.put("jboss.ejb.client.invocation.timeout", "60000"); // Timeout de invocação (ms)

    if (user != null) props.put(Context.SECURITY_PRINCIPAL, user);
    if (password != null) props.put(Context.SECURITY_CREDENTIALS, password);

    InitialContext context;
    try {
      context = new InitialContext(props);
    } catch (NamingException e) {
      throw new RFWCriticalException(e);
    }
    return context;
  }

  /**
   * Recupera o contexto remoto passando um host e porta específicos.
   *
   * @param host host do servidor
   * @param port porta do servidor
   * @return Contexto Remoto usado para recupear as fachadas dos EJBs dos módulos.
   * @throws RFWException
   */
  public static InitialContext getRemoteContextWildFly24(String host, Integer port) throws RFWException {
    return getRemoteContextWildFly24(host, port, null, null);
  }

  /**
   * Recupera a interface a partir de um contexto remoto passando um host e porta específicos e o JNDI name.
   *
   * @param host host do servidor
   * @param port porta do servidor
   * @param jndiName Nome do JNDI para o looup do EJB. Normalmente ao levantar um EJB o WildFly dá uma coleção de nomes no seu log, por exemplo:<br>
   *          <ul>
   *          2024-08-19 20:55:57,524 INFO [org.jboss.as.ejb3.deployment] (MSC service thread 1-8) WFLYEJB0473: JNDI bindings for session bean named 'APPKernelFacade' in deployment unit 'subdeployment "APPCoreEJB.jar" of deployment "APPEAR-8.0.0.ear"' are as follows:<br>
   *          <ul>
   *          java:global/APPERP/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          java:app/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          java:module/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          java:jboss/exported/APPERP/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          ejb:APPERP/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          java:global/APPERP/APPCoreEJB/APPKernelFacade<br>
   *          java:app/APPCoreEJB/APPKernelFacade<br>
   *          java:module/APPKernelFacade<br>
   *          </ul>
   *          Este método funcionará com o nome completo, incluindo a definição da interface remota (o nome da classe depois do !). Considerando os exemplos acima, o valor a ser passado neste argumento seria:<br>
   *          <ul>
   *          <li>/APPERP/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote</li>
   *          </ul>
   *          </ul>
   *
   * @return A interface solicitada.
   * @throws RFWException
   */
  public static Object lookupRemoteContextWildFly24(String host, Integer port, String jndiName) throws RFWException {
    InitialContext context = getRemoteContextWildFly24(host, port, null, null);
    try {
      return context.lookup(jndiName);
    } catch (NamingException e) {
      throw new RFWCriticalException(e);
    }
  }

  /**
   * Recupera a interface a partir de um contexto remoto passando um host e porta específicos e o JNDI name.
   *
   * @param host host do servidor
   * @param port porta do servidor
   * @param user usuário para autenticação JNDI remota.
   * @param password senha para autenticação JNDI remota.
   * @param jndiName Nome do JNDI para o looup do EJB. Normalmente ao levantar um EJB o WildFly dá uma coleção de nomes no seu log, por exemplo:<br>
   *          <ul>
   *          2024-08-19 20:55:57,524 INFO [org.jboss.as.ejb3.deployment] (MSC service thread 1-8) WFLYEJB0473: JNDI bindings for session bean named 'APPKernelFacade' in deployment unit 'subdeployment "APPCoreEJB.jar" of deployment "APPEAR-8.0.0.ear"' are as follows:<br>
   *          <ul>
   *          java:global/APPERP/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          java:app/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          java:module/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          java:jboss/exported/APPERP/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          ejb:APPERP/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote<br>
   *          java:global/APPERP/APPCoreEJB/APPKernelFacade<br>
   *          java:app/APPCoreEJB/APPKernelFacade<br>
   *          java:module/APPKernelFacade<br>
   *          </ul>
   *          Este método funcionará com o nome completo, incluindo a definição da interface remota (o nome da classe depois do !). Considerando os exemplos acima, o valor a ser passado neste argumento seria:<br>
   *          <ul>
   *          <li>/APPERP/APPCoreEJB/APPKernelFacade!br.com.app.kernel.facade.APPKernelFacadeRemote</li>
   *          </ul>
   *          </ul>
   *
   * @return A interface solicitada.
   * @throws RFWException
   */
  public static Object lookupRemoteContextWildFly24(String host, Integer port, String user, String password, String jndiName) throws RFWException {
    InitialContext context = getRemoteContextWildFly24(host, port, user, password);
    try {
      return context.lookup(jndiName);
    } catch (NamingException e) {
      throw new RFWCriticalException(e);
    }
  }

  /**
   * Recupera o contexto remoto passando um host e porta específicos.
   *
   * @param host host do servidor
   * @param port porta do servidor
   * @return Contexto Remoto usado para recupear as fachadas dos EJBs dos módulos.
   * @throws RFWException
   */
  public static InitialContext getRemoteContextGlassFish5(String host, Integer port) throws RFWException {
    Properties props = new Properties();
    props.setProperty("org.omg.CORBA.ORBInitialHost", host);
    props.setProperty("org.omg.CORBA.ORBInitialPort", "" + port);

    InitialContext context;
    try {
      context = new InitialContext(props);
    } catch (NamingException e) {
      throw new RFWCriticalException(e);
    }
    return context;
  }
}
