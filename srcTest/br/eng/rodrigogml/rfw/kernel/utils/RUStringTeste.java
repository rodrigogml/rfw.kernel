package br.eng.rodrigogml.rfw.kernel.utils;

import static br.eng.rodrigogml.rfw.kernel.utils.RUString.replaceAll;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.replaceAllRecursively;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Description: Classe de teste da {@link RUString}.<br>
 *
 * @author Rodrigo Leitão
 * @since (21 de fev. de 2025)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUStringTeste {

  /**
   * Testa o método {@code replaceAll}, garantindo que as substituições ocorram corretamente.
   */
  @Test
  public void t00_replaceAll() {
    assertEquals("Deve substituir corretamente sem ignorar case e acentos.", "Olá Mundo!", replaceAll("Olá Teste!", "Teste", "Mundo", true, true));
    assertEquals("Deve substituir ignorando case.", "Olá Mundo!", replaceAll("Olá TeStE!", "TeStE", "Mundo", true, false));
    assertEquals("Deve substituir ignorando acentos.", "Ola Teste!", replaceAll("Ólá Teste!", "Olá", "Ola", false, true));
    assertEquals("Deve substituir ignorando case e acentos.", "ola TéStE!", replaceAll("Ólá TéStE!", "olá", "ola", false, false));
    assertEquals("Nenhuma substituição quando oldValue não está presente.", "Olá Teste!", replaceAll("Olá Teste!", "XYZ", "Mundo", true, true));
    assertEquals("Deve substituir todas as ocorrências corretamente.", "Mundo Mundo!", replaceAll("Teste Teste!", "Teste", "Mundo", true, true));
    assertThrows(IllegalArgumentException.class, () -> replaceAll("Olá Teste!", "", "Mundo", true, true));
  }

  /**
   * Testa o método {@code replaceAllRecursively}, garantindo que a substituição ocorra corretamente e de forma iterativa.
   */
  @Test
  public void t00_replaceAllRecursively() {
    assertEquals("Deve substituir corretamente uma única ocorrência.", "Olá Mundo!", replaceAllRecursively("Olá Teste!", "Teste", "Mundo", true, true));
    assertEquals("Deve substituir múltiplas ocorrências recursivamente.", "Mundo Mundo!", replaceAllRecursively("Teste Teste!", "Teste", "Mundo", true, true));
    assertEquals("Deve substituir corretamente ignorando case.", "Olá Mundo!", replaceAllRecursively("Olá TeStE!", "teste", "Mundo", true, false));
    assertEquals("Deve substituir corretamente ignorando acentos.", "Ola Teste!", replaceAllRecursively("Ólá Teste!", "Olá", "Ola", false, true));
    assertEquals("Deve substituir corretamente ignorando case e acentos.", "ola TéStE!", replaceAllRecursively("Ólá TéStE!", "olá", "ola", false, false));
    assertEquals("Nenhuma substituição quando oldValue não está presente.", "Olá Teste!", replaceAllRecursively("Olá Teste!", "XYZ", "Mundo", true, true));
    assertEquals("Deve evitar substituições infinitas.", "Loop!", replaceAllRecursively("Loop!", "Loop", "Loop!", true, true));
    assertEquals("Deve evitar substituições infinitas.", "!", replaceAllRecursively("LoLoLoLoLoopopopopop!", "Loop", "", true, true));
    assertThrows(IllegalArgumentException.class, () -> replaceAllRecursively("Olá Teste!", "", "Mundo", true, true));
  }
}
