package br.eng.rodrigogml.rfw.kernel.utils;

import static br.eng.rodrigogml.rfw.kernel.utils.RUTypes.formatMillis;
import static br.eng.rodrigogml.rfw.kernel.utils.RUTypes.formatMillisToHuman;
import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Description: Classe de testes da classe {@link RUTypesTeste}.<br>
 *
 * @author Rodrigo Leitão
 * @since (21 de fev. de 2025)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUTypesTeste {

  /**
   * Testa o método {@code formatMillisToHuman}, garantindo a formatação correta.
   */
  @Test
  public void t00_formatMillisToHuman() {
    assertEquals("60.500 ms devem ser '01:00'500\"", "01:00'500\"", formatMillisToHuman(60500));
    assertEquals("3.600.000 ms devem ser '01:00:00'000\"", "01:00:00'000\"", formatMillisToHuman(3600000));
    assertEquals("500 ms devem ser '00'500\"", "00'500\"", formatMillisToHuman(500));
  }

  /**
   * Testa o método {@code formatMillis}, garantindo que a formatação seja aplicada corretamente.
   */
  @Test
  public void t00_formatMillis() {
    assertEquals("Timestamp 0 deve ser '00:00:00' em UTC.", "00:00:00", formatMillis("HH:mm:ss", 0));
    assertEquals("3600000 ms devem ser '01:00:00'.", "01:00:00", formatMillis("HH:mm:ss", 3600000));
  }
}
