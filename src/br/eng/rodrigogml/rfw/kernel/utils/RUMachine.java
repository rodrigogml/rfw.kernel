package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.IOException;
import java.util.Enumeration;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe utilit�ria para agrupar c�digo de obten��o de informa��es da m�quina, JVM, SO, etc..<br>
 *
 * @author Rodrigo Leit�o
 * @since (28 de ago. de 2024)
 */
public class RUMachine {

  /**
   * Construtor privado, classe estritamente est�tica.
   */
  private RUMachine() {
  }

  /**
   * Recupera em uma String de m�ltiplas linhas as informa��es sobre o classloader de uma classe.
   *
   * @return String multilinhas (utiliza o {@link System#lineSeparator()} com as informa��es.
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
   * @return .toString() do Classloader da classe/m�todo passada como argumento ou nulo nos casos de classe carregadas do "Bootstrap ClassLoader" (como a classe String, por exemplo).
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
}
