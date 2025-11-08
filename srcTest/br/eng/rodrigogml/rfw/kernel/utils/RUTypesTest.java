package br.eng.rodrigogml.rfw.kernel.utils;

import static br.eng.rodrigogml.rfw.kernel.utils.RUTypes.formatMillis;
import static br.eng.rodrigogml.rfw.kernel.utils.RUTypes.formatMillisToHuman;
import static br.eng.rodrigogml.rfw.kernel.utils.RUTypes.toInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe de testes da classe {@link RUTypesTest}.<br>
 *
 * @author Rodrigo Leitão
 * @since (21 de fev. de 2025)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUTypesTest {

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

  // NOVOS MÉTODOS PARA ADICIONAR NA CLASSE RUTypesTest

  /**
   * Testa o método {@code toInteger(String)} para valores nulos e vazios.
   */
  @Test
  public void t10_toIntegerStringNullAndEmpty() throws RFWException {
    assertNull("null deve resultar em null", toInteger((String) null));
    assertNull("String vazia deve resultar em null", toInteger(""));
    assertNull("String em branco deve resultar em null", toInteger("   "));
  }

  /**
   * Testa o método {@code toInteger(String)} para valores válidos, incluindo limites de {@link Integer}.
   */
  @Test
  public void t11_toIntegerStringValidValues() throws RFWException {
    assertEquals("0 deve ser convertido corretamente", Integer.valueOf(0), toInteger("0"));
    assertEquals("Valor positivo simples", Integer.valueOf(123), toInteger("123"));
    assertEquals("Valor negativo simples", Integer.valueOf(-456), toInteger("-456"));
    assertEquals("Valor com sinal positivo explícito", Integer.valueOf(789), toInteger("+789"));
    assertEquals("Valor com zeros à esquerda", Integer.valueOf(7), toInteger("0007"));

    assertEquals("Limite superior de Integer", Integer.valueOf(Integer.MAX_VALUE),
        toInteger(String.valueOf(Integer.MAX_VALUE)));
    assertEquals("Limite inferior de Integer", Integer.valueOf(Integer.MIN_VALUE),
        toInteger(String.valueOf(Integer.MIN_VALUE)));
  }

  /**
   * Testa o método {@code toInteger(String)} para formatos inválidos, garantindo que seja lançada {@link RFWValidationException}.
   */
  @Test
  public void t12_toIntegerStringInvalidFormat() {
    assertToIntegerStringValidationFailure("12.3");
    assertToIntegerStringValidationFailure("abc");
    assertToIntegerStringValidationFailure("1,000");
    assertToIntegerStringValidationFailure("+");
    assertToIntegerStringValidationFailure("-");
    assertToIntegerStringValidationFailure("++1");
  }

  /**
   * Testa o método {@code toInteger(String)} para valores numéricos fora da faixa de {@link Integer}, garantindo que seja lançada {@link RFWValidationException}.
   */
  @Test
  public void t13_toIntegerStringOutOfRange() {
    assertToIntegerStringValidationFailure("2147483648"); // Integer.MAX_VALUE + 1
    assertToIntegerStringValidationFailure("-2147483649"); // Integer.MIN_VALUE - 1
  }

  /**
   * Testa o método {@code toInteger(String)} com um número extremamente grande, garantindo que uma {@link RFWCriticalException} seja lançada por falha interna no parser numérico.
   */
  @Test(expected = RFWCriticalException.class)
  public void t14_toIntegerStringHugeNumberCausesCritical() throws RFWException {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 40; i++) {
      sb.append('9');
    }
    toInteger(sb.toString());
  }

  /**
   * Testa o método {@code toInteger(Number)} para tipos integrais simples ({@link Integer}, {@link Long}, {@link Short}, {@link Byte}), incluindo tratamento de nulo.
   */
  @Test
  public void t20_toIntegerNumberIntegralTypes() throws RFWException {
    assertNull("null Number deve resultar em null", toInteger((Number) null));

    assertEquals("Integer deve ser retornado diretamente", Integer.valueOf(10), toInteger(Integer.valueOf(10)));
    assertEquals("Long dentro da faixa deve ser convertido", Integer.valueOf(123), toInteger(Long.valueOf(123L)));
    assertEquals("Short deve ser convertido corretamente", Integer.valueOf(5), toInteger(Short.valueOf((short) 5)));
    assertEquals("Byte deve ser convertido corretamente", Integer.valueOf(-8), toInteger(Byte.valueOf((byte) -8)));
  }

  /**
   * Testa o método {@code toInteger(Number)} para valores integrais fora da faixa de {@link Integer}, garantindo que seja lançada {@link RFWValidationException}.
   */
  @Test
  public void t21_toIntegerNumberIntegralOutOfRange() {
    long aboveMax = Integer.MAX_VALUE + 1L;
    long belowMin = Integer.MIN_VALUE - 1L;

    assertToIntegerNumberValidationFailure(Long.valueOf(aboveMax));
    assertToIntegerNumberValidationFailure(Long.valueOf(belowMin));
  }

  /**
   * Testa o método {@code toInteger(Number)} para {@link BigDecimal} com valores inteiros, incluindo zeros à direita e sinais.
   */
  @Test
  public void t22_toIntegerNumberBigDecimalIntegral() throws RFWException {
    assertEquals("BigDecimal inteiro simples", Integer.valueOf(10), toInteger(new BigDecimal("10")));
    assertEquals("BigDecimal com zeros à direita", Integer.valueOf(10), toInteger(new BigDecimal("10.00")));
    assertEquals("BigDecimal negativo inteiro", Integer.valueOf(-5), toInteger(new BigDecimal("-5")));
  }

  /**
   * Testa o método {@code toInteger(Number)} para {@link BigDecimal} fracionário, garantindo que seja lançada {@link RFWValidationException}.
   */
  @Test
  public void t23_toIntegerNumberBigDecimalFractional() {
    assertToIntegerNumberValidationFailure(new BigDecimal("10.5"));
    assertToIntegerNumberValidationFailure(new BigDecimal("-3.14159"));
  }

  /**
   * Testa o método {@code toInteger(Number)} para {@link BigDecimal} fora da faixa de {@link Integer}, garantindo que seja lançada {@link RFWValidationException}.
   */
  @Test
  public void t24_toIntegerNumberBigDecimalOutOfRange() {
    assertToIntegerNumberValidationFailure(new BigDecimal("2147483648"));
    assertToIntegerNumberValidationFailure(new BigDecimal("-2147483649"));
  }

  /**
   * Testa o método {@code toInteger(Number)} para {@link Double} e {@link Float} representando inteiros exatos.
   */
  @Test
  public void t25_toIntegerNumberFloatingIntegral() throws RFWException {
    assertEquals("Double inteiro exato", Integer.valueOf(10), toInteger(Double.valueOf(10.0)));
    assertEquals("Float inteiro exato negativo", Integer.valueOf(-5), toInteger(Float.valueOf(-5.0f)));
  }

  /**
   * Testa o método {@code toInteger(Number)} para {@link Double} e {@link Float} fracionários, garantindo que seja lançada {@link RFWValidationException}.
   */
  @Test
  public void t26_toIntegerNumberFloatingFractional() {
    assertToIntegerNumberValidationFailure(Double.valueOf(10.5));
    assertToIntegerNumberValidationFailure(Float.valueOf(3.14f));
  }

  /**
   * Testa o método {@code toInteger(Number)} para valores especiais de ponto flutuante ({@link Double#NaN}, {@link Double#POSITIVE_INFINITY}), garantindo que sejam rejeitados.
   */
  @Test
  public void t27_toIntegerNumberFloatingSpecials() {
    assertToIntegerNumberValidationFailure(Double.valueOf(Double.NaN));
    assertToIntegerNumberValidationFailure(Double.valueOf(Double.POSITIVE_INFINITY));
    assertToIntegerNumberValidationFailure(Double.valueOf(Double.NEGATIVE_INFINITY));
  }

  /**
   * Testa o método {@code toInteger(Object)} para valores nulos e para delegação correta a outros métodos sobrecarregados ({@link Integer}, {@link Long}, {@link String}).
   */
  @Test
  public void t30_toIntegerObjectNullAndDelegates() throws RFWException {
    assertNull("null deve resultar em null", toInteger((Object) null));

    assertEquals("Integer via Object deve ser convertido corretamente",
        Integer.valueOf(10), toInteger((Object) Integer.valueOf(10)));

    assertEquals("Long via Object deve ser delegado para toInteger(Number)",
        Integer.valueOf(20), toInteger((Object) Long.valueOf(20L)));

    assertEquals("String via Object deve ser delegado para toInteger(String)",
        Integer.valueOf(30), toInteger((Object) "30"));
  }

  /**
   * Testa o método {@code toInteger(Object)} para valores booleanos, garantindo a conversão {@code true -> 1} e {@code false -> 0}.
   */
  @Test
  public void t31_toIntegerObjectBoolean() throws RFWException {
    assertEquals("Boolean TRUE deve resultar em 1",
        Integer.valueOf(1), toInteger(Boolean.TRUE));
    assertEquals("Boolean FALSE deve resultar em 0",
        Integer.valueOf(0), toInteger(Boolean.FALSE));
  }

  /**
   * Testa o método {@code toInteger(Object)} para um tipo não suportado, garantindo que seja lançada {@link RFWValidationException}.
   */
  @Test(expected = RFWValidationException.class)
  public void t32_toIntegerObjectUnsupportedType() throws RFWException {
    toInteger(new Date());
  }

  /**
   * Método utilitário para validar que {@link RUTypes#toInteger(String)} lança {@link RFWValidationException} para o valor informado.
   *
   * @param value Valor textual a ser testado.
   */
  private void assertToIntegerStringValidationFailure(String value) {
    try {
      toInteger(value);
      fail("Era esperada RFWValidationException para o valor: " + value);
    } catch (RFWValidationException e) {
      // esperado
    } catch (RFWException e) {
      fail("Era esperada RFWValidationException, mas foi lançada: " + e.getClass().getSimpleName());
    }
  }

  /**
   * Método utilitário para validar que {@link RUTypes#toInteger(Number)} lança {@link RFWValidationException} para o valor informado.
   *
   * @param value Valor numérico a ser testado.
   */
  private void assertToIntegerNumberValidationFailure(Number value) {
    try {
      toInteger(value);
      fail("Era esperada RFWValidationException para o valor: " + value);
    } catch (RFWValidationException e) {
      // esperado
    } catch (RFWException e) {
      fail("Era esperada RFWValidationException, mas foi lançada: " + e.getClass().getSimpleName());
    }
  }

}
