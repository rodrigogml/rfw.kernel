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
 * @author Rodrigo Leit�o
 * @since (21 de fev. de 2025)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUStringTeste {

  /**
   * Testa o m�todo {@code replaceAll}, garantindo que as substitui��es ocorram corretamente.
   */
  @Test
  public void t00_replaceAll() {
    assertEquals("Deve substituir corretamente sem ignorar case e acentos.", "Ol� Mundo!", replaceAll("Ol� Teste!", "Teste", "Mundo", true, true));
    assertEquals("Deve substituir ignorando case.", "Ol� Mundo!", replaceAll("Ol� TeStE!", "TeStE", "Mundo", true, false));
    assertEquals("Deve substituir ignorando acentos.", "Ola Teste!", replaceAll("�l� Teste!", "Ol�", "Ola", false, true));
    assertEquals("Deve substituir ignorando case e acentos.", "ola T�StE!", replaceAll("�l� T�StE!", "ol�", "ola", false, false));
    assertEquals("Nenhuma substitui��o quando oldValue n�o est� presente.", "Ol� Teste!", replaceAll("Ol� Teste!", "XYZ", "Mundo", true, true));
    assertEquals("Deve substituir todas as ocorr�ncias corretamente.", "Mundo Mundo!", replaceAll("Teste Teste!", "Teste", "Mundo", true, true));
    assertThrows(IllegalArgumentException.class, () -> replaceAll("Ol� Teste!", "", "Mundo", true, true));
  }

  /**
   * Testa o m�todo {@code replaceAllRecursively}, garantindo que a substitui��o ocorra corretamente e de forma iterativa.
   */
  @Test
  public void t00_replaceAllRecursively() {
    assertEquals("Deve substituir corretamente uma �nica ocorr�ncia.", "Ol� Mundo!", replaceAllRecursively("Ol� Teste!", "Teste", "Mundo", true, true));
    assertEquals("Deve substituir m�ltiplas ocorr�ncias recursivamente.", "Mundo Mundo!", replaceAllRecursively("Teste Teste!", "Teste", "Mundo", true, true));
    assertEquals("Deve substituir corretamente ignorando case.", "Ol� Mundo!", replaceAllRecursively("Ol� TeStE!", "teste", "Mundo", true, false));
    assertEquals("Deve substituir corretamente ignorando acentos.", "Ola Teste!", replaceAllRecursively("�l� Teste!", "Ol�", "Ola", false, true));
    assertEquals("Deve substituir corretamente ignorando case e acentos.", "ola T�StE!", replaceAllRecursively("�l� T�StE!", "ol�", "ola", false, false));
    assertEquals("Nenhuma substitui��o quando oldValue n�o est� presente.", "Ol� Teste!", replaceAllRecursively("Ol� Teste!", "XYZ", "Mundo", true, true));
    assertEquals("Deve evitar substitui��es infinitas.", "Loop!", replaceAllRecursively("Loop!", "Loop", "Loop!", true, true));
    assertEquals("Deve evitar substitui��es infinitas.", "!", replaceAllRecursively("LoLoLoLoLoopopopopop!", "Loop", "", true, true));
    assertThrows(IllegalArgumentException.class, () -> replaceAllRecursively("Ol� Teste!", "", "Mundo", true, true));
  }
}
