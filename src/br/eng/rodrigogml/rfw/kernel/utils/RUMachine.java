package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe utilitária para agrupar código de obtenção de informações da máquina, JVM, SO, etc..<br>
 *
 * @author Rodrigo Leitão
 * @since (28 de ago. de 2024)
 */
public class RUMachine {

  /**
   * Construtor privado, classe estritamente estática.
   */
  private RUMachine() {
  }

  /**
   * Recupera em uma String de múltiplas linhas as informações sobre o classloader de uma classe.
   *
   * @return String multilinhas (utiliza o {@link System#lineSeparator()} com as informações.
   */
  public static String getClassLoaderDetails(Class<?> clazz) {
    StringBuilder details = new StringBuilder();

    ClassLoader classLoader = clazz.getClassLoader();
    details.append("ClassLoader (toString): ").append(classLoader).append(System.lineSeparator());

    if (classLoader != null) {
      details.append("ClassLoader Name: ").append(classLoader.getClass().getName()).append(System.lineSeparator());
      details.append("Parent ClassLoader (toString): ").append(classLoader.getParent()).append(System.lineSeparator());

      // List all URLs (if applicable)
      if (classLoader instanceof java.net.URLClassLoader) {
        try (java.net.URLClassLoader urlClassLoader = (java.net.URLClassLoader) classLoader) {
          details.append("URLs loaded by ClassLoader:").append(System.lineSeparator());
          for (java.net.URL url : urlClassLoader.getURLs()) {
            details.append("  - ").append(url).append(System.lineSeparator());
          }
        } catch (IOException e) {
          details.append(" - Exception Loading URLs:").append(e.getClass().getCanonicalName()).append(e.getMessage()).append(System.lineSeparator());
        }
      }

      // Get resources loaded by the ClassLoader
      try {
        Enumeration<java.net.URL> resources = classLoader.getResources("");
        details.append("Resources loaded by ClassLoader:").append(System.lineSeparator());
        while (resources.hasMoreElements()) {
          details.append("  - ").append(resources.nextElement()).append(System.lineSeparator());
        }
      } catch (Exception e) {
        details.append("Error fetching resources: ").append(e.getMessage()).append(System.lineSeparator());
      }
    } else {
      details.append("ClassLoader is Bootstrap (System) ClassLoader").append(System.lineSeparator());
    }

    return details.toString();
  }

  /**
   * Retorna o nome/identificador geral do classloader da classe passada.
   *
   * @return .toString() do Classloader da classe/método passada como argumento ou nulo nos casos de classe carregadas do "Bootstrap ClassLoader" (como a classe String, por exemplo).
   */
  public static String getClassLoaderName(Class<?> clazz) throws RFWException {
    PreProcess.requiredNonNull(clazz);
    ClassLoader classLoader = clazz.getClassLoader();
    return classLoader == null ? null : classLoader.toString();
  }

  /**
   * Retorna o nome/identificador geral do classloader da classe passada.
   *
   * @return .toString() do Classloader da Thread atual ou nulo nos casos raros de threads criadas diretaente pela JVM (como o Garbage Collector).
   */
  public static String getClassLoaderFromThread() throws RFWException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    return classLoader == null ? null : classLoader.toString();
  }

  /**
   * Recupera o MAC Address da primeira interface de rede considerada "válida".<br>
   * <br>
   * Critérios da interface escolhida:
   * <ul>
   * <li>Interface está "up" ({@link NetworkInterface#isUp()}).</li>
   * <li>Não é loopback ({@link NetworkInterface#isLoopback()}).</li>
   * <li>Não é virtual ({@link NetworkInterface#isVirtual()}).</li>
   * <li>Possui endereço físico ({@link NetworkInterface#getHardwareAddress()}) diferente de {@code null} e com tamanho &gt; 0.</li>
   * </ul>
   * O MAC é retornado no formato {@code "XX-XX-XX-XX-XX-XX"} em hexadecimal (maiúsculo).<br>
   * Se nenhuma interface válida for encontrada ou ocorrer erro ao obter as interfaces, é lançada {@link RFWCriticalException} com o código {@code RFW_ERR_900004}.
   *
   * @return MAC Address da máquina no formato {@code XX-XX-XX-XX-XX-XX}.
   * @throws RFWException Caso não consiga recuperar as informações necessárias.
   */
  public static String getLocalHostMacAddress() throws RFWException {
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      if (interfaces == null) {
        throw new RFWCriticalException("RFW_ERR_900004");
      }

      while (interfaces.hasMoreElements()) {
        NetworkInterface network = interfaces.nextElement();
        if (network == null) {
          continue;
        }
        try {
          if (!network.isUp() || network.isLoopback() || network.isVirtual()) {
            continue;
          }
        } catch (SocketException e) {
          // Se não conseguir consultar o estado dessa interface, tenta as próximas.
          continue;
        }

        byte[] mac = network.getHardwareAddress();
        if (mac == null || mac.length == 0) {
          continue;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
          if (i > 0) {
            sb.append("-");
          }
          sb.append(String.format("%02X", mac[i]));
        }
        return sb.toString();
      }

      // Nenhuma interface válida encontrada
      throw new RFWCriticalException("7");
    } catch (SocketException e) {
      throw new RFWCriticalException("RFW_ERR_900004", e);
    }
  }

  /**
   * Retorna true se o programa estiver rodando atualmente em uma arquitetura de 32 bits.
   */
  public static boolean isJava32Bit() {
    return "32".equals(getJavaArchitetureModel());
  }

  /**
   * Retorna true se o programa estiver rodando atualmente em uma arquitetura de 64 bits.
   */
  public static boolean isJava64Bit() {
    return "64".equals(getJavaArchitetureModel());
  }

  /**
   * Retorna o modelo da arquitetura em que o programa está rodando.
   */
  public static String getJavaArchitetureModel() {
    return System.getProperty("sun.arch.data.model");
  }
}
