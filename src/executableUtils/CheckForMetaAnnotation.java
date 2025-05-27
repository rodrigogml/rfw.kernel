package executableUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: Classe utilitária para ajudar a encontrar Atributos em VOs que estejam faltando a RFWAnnotation.<br>
 *
 * @author Rodrigo Leitão
 * @since (29 de ago. de 2024)
 */
public class CheckForMetaAnnotation {

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: java AnnotationChecker <directory1> <directory2> ...");
      System.exit(1);
    }

    for (String directory : args) {
      File rootDir = new File(directory);

      if (!rootDir.isDirectory()) {
        System.out.printf("The provided path %s is not a directory.%n", directory);
        continue;
      }

      processDirectory(rootDir);
    }
  }

  private static void processDirectory(File dir) {
    File[] files = dir.listFiles();

    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          processDirectory(file); // Recursively process subdirectories
        } else if (file.getName().endsWith("VO.java")) {
          checkAnnotationsInFile(file.toPath());
        }
      }
    }
  }

  private static void checkAnnotationsInFile(Path filePath) {
    try {
      List<String> lines = Files.readAllLines(filePath, Charset.forName("Windows-1252"));
      String className = filePath.getFileName().toString().replace(".java", "");
      boolean inFieldSection = false;

      int index = 0;
      for (String line : lines) {
        line = line.trim();

        // Detecting start of fields section
        if (line.startsWith("public class " + className)) {
          inFieldSection = true;
        }

        if (inFieldSection && line.startsWith("private ") && !line.matches("\\s*(public|private)?\\s*enum\\s*\\w*\\s*\\{")) {
          if (!line.contains("serialVersionUID")) { // Ignore serialVersionUID
            String attributeName = extractAttributeName(line);

            if (!previousLineHasRFWMetaAnnotation(lines, index)) {
              System.out.printf("A classe %s não apresenta a RFWMeta no atributo %s.%n", className, attributeName);
            }
          }
        }
        index++;
      }

    } catch (IOException e) {
      System.err.println("Error reading file: " + filePath);
      e.printStackTrace();
    }
  }

  private static String extractAttributeName(String line) {
    // Extract the attribute name from the line
    Pattern pattern = Pattern.compile("private (?:final )?[\\w\\<\\, \\>\\.]+ (\\w+)(\\s+=\\s+.*)?;");
    Matcher matcher = pattern.matcher(line);

    if (matcher.find()) {
      return matcher.group(1);
    }
    return "Unknown!! Linha: " + line;
  }

  // private static boolean previousLineHasRFWMetaAnnotation(List<String> lines, int currentIndex) {
  // if (currentIndex > 0) {
  // String previousLine = lines.get(currentIndex - 1).trim();
  // String previousLine2 = lines.get(currentIndex - 2).trim();
  // return previousLine.startsWith("@RFWMeta") || previousLine2.startsWith("@RFWMeta");
  // }
  // return false;
  // }
  private static boolean previousLineHasRFWMetaAnnotation(List<String> lines, int currentIndex) {
    for (int i = currentIndex - 1; i >= 0; i--) {
      String previousLine = lines.get(i).trim();
      if (previousLine.startsWith("private ")) {
        return false; // Encontrou outro atributo
      }
      if (previousLine.startsWith("@RFWMeta")) {
        return true; // Encontrou a anotação
      }
      if (previousLine.startsWith("public class ") || previousLine.startsWith("class ")) {
        return false; // Encontrou a declaração da classe
      }
    }
    return false; // Não encontrou a anotação até a declaração da classe
  }

}
