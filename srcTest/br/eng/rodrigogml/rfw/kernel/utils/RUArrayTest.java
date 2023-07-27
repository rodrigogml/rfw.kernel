package br.eng.rodrigogml.rfw.kernel.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUArrayTest {

  @Test
  public void t00_concatArrays() throws RFWException {

    final String[] itens = new String[] { "a", null, "b", null, null, null, "d" };

    String[] result = RUArray.<String> createArrayWithAllNonNullAndSeparator("|", itens);

    assertEquals("a", result[0]);
    assertEquals("|", result[1]);
    assertEquals("b", result[2]);
    assertEquals("|", result[3]);
    assertEquals("d", result[4]);
  }

  @Test
  public void t001_removeValues() {
    final String[] itens = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
    final String[] removeItens = new String[] { "a", "e", "i", "o", "u" };
    final String[] expected = new String[] { "b", "c", "d", "f", "g", "h" };

    assertArrayEquals(expected, RUArray.removeValues(itens, removeItens));
  }

  @Test
  public void t001_removeSingleValue() {
    final String[] itens = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i" };
    final String[] expected = new String[] { "a", "b", "c", "e", "f", "g", "h", "i" };

    assertArrayEquals(expected, RUArray.removeValues(itens, "d"));
  }
}
