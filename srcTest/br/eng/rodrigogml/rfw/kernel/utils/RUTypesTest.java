package br.eng.rodrigogml.rfw.kernel.utils;

import static br.eng.rodrigogml.rfw.kernel.utils.RUTypes.formatMillis;
import static br.eng.rodrigogml.rfw.kernel.utils.RUTypes.formatMillisToHuman;
import static br.eng.rodrigogml.rfw.kernel.utils.RUTypes.parseInteger;
import static br.eng.rodrigogml.rfw.kernel.utils.RUTypes.parseLong;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
   * Testa o mtodo {@code parseInteger(String)} para valores nulos e vazios.
   */
  @Test
  public void t10_parseIntegerStringNullAndEmpty() throws RFWException {
    assertNull("null deve resultar em null", parseInteger((String) null));
    assertNull("String vazia deve resultar em null", parseInteger(""));
    assertNull("String em branco deve resultar em null", parseInteger("   "));
  }

  /**
   * Testa o mtodo {@code parseInteger(String)} para valores vlidos, incluindo limites de {@link Integer}.
   */
  @Test
  public void t11_parseIntegerStringValidValues() throws RFWException {
    assertEquals("0 deve ser convertido corretamente", Integer.valueOf(0), parseInteger("0"));
    assertEquals("Valor positivo simples", Integer.valueOf(123), parseInteger("123"));
    assertEquals("Valor negativo simples", Integer.valueOf(-456), parseInteger("-456"));
    assertEquals("Valor com sinal positivo explcito", Integer.valueOf(789), parseInteger("+789"));
    assertEquals("Valor com zeros  esquerda", Integer.valueOf(7), parseInteger("0007"));

    assertEquals("Limite superior de Integer", Integer.valueOf(Integer.MAX_VALUE), parseInteger(String.valueOf(Integer.MAX_VALUE)));
    assertEquals("Limite inferior de Integer", Integer.valueOf(Integer.MIN_VALUE), parseInteger(String.valueOf(Integer.MIN_VALUE)));
   * Testa o mtodo {@code parseInteger(String)} para formatos invlidos, garantindo que seja lanada {@link RFWValidationException}.
  @Test
  public void t12_parseIntegerStringInvalidFormat() {
   * Testa o mtodo {@code parseInteger(String)} para valores numricos fora da faixa de {@link Integer}, garantindo que seja lanada {@link RFWValidationException}.
  public void t13_parseIntegerStringOutOfRange() {
   * Testa o mtodo {@code parseInteger(String)} com um nmero extremamente grande, garantindo que uma {@link RFWCriticalException} seja lanada por falha interna no parser numrico.
  public void t14_parseIntegerStringHugeNumberCausesCritical() throws RFWException {
    parseInteger(sb.toString());
  /**
   * Testa o mtodo {@code parseInteger(Number)} para tipos integrais simples ({@link Integer}, {@link Long}, {@link Short}, {@link Byte}), incluindo tratamento de nulo.
   */
  @Test
  public void t20_parseIntegerNumberIntegralTypes() throws RFWException {
    assertNull("null Number deve resultar em null", parseInteger((Number) null));
    assertEquals("Integer deve ser retornado diretamente", Integer.valueOf(10), parseInteger(Integer.valueOf(10)));
    assertEquals("Long dentro da faixa deve ser convertido", Integer.valueOf(123), parseInteger(Long.valueOf(123L)));
    assertEquals("Short deve ser convertido corretamente", Integer.valueOf(5), parseInteger(Short.valueOf((short) 5)));
    assertEquals("Byte deve ser convertido corretamente", Integer.valueOf(-8), parseInteger(Byte.valueOf((byte) -8)));

   * Testa o mtodo {@code parseInteger(Number)} para valores integrais fora da faixa de {@link Integer}, garantindo que seja lanada {@link RFWValidationException}.
  public void t21_parseIntegerNumberIntegralOutOfRange() {
  }

  /**
   * Testa o mtodo {@code parseInteger(Number)} para {@link BigDecimal} com valores inteiros, incluindo zeros  direita e sinais.
   */
  @Test
  public void t22_parseIntegerNumberBigDecimalIntegral() throws RFWException {
    assertEquals("BigDecimal inteiro simples", Integer.valueOf(10), parseInteger(new BigDecimal("10")));
    assertEquals("BigDecimal com zeros  direita", Integer.valueOf(10), parseInteger(new BigDecimal("10.00")));
    assertEquals("BigDecimal negativo inteiro", Integer.valueOf(-5), parseInteger(new BigDecimal("-5")));
   * Testa o mtodo {@code parseInteger(Number)} para {@link BigDecimal} fracionrio, garantindo que seja lanada {@link RFWValidationException}.
  public void t23_parseIntegerNumberBigDecimalFractional() {
   * Testa o mtodo {@code parseInteger(Number)} para {@link BigDecimal} fora da faixa de {@link Integer}, garantindo que seja lanada {@link RFWValidationException}.
  public void t24_parseIntegerNumberBigDecimalOutOfRange() {

  /**
   * Testa o mtodo {@code parseInteger(Number)} para {@link Double} e {@link Float} representando inteiros exatos.
   */
  @Test
  public void t25_parseIntegerNumberFloatingIntegral() throws RFWException {
    assertEquals("Double inteiro exato", Integer.valueOf(10), parseInteger(Double.valueOf(10.0)));
    assertEquals("Float inteiro exato negativo", Integer.valueOf(-5), parseInteger(Float.valueOf(-5.0f)));
  }

  /**
   * Testa o mtodo {@code parseInteger(Number)} para {@link Double} e {@link Float} fracionrios, garantindo que seja lanada {@link RFWValidationException}.
   */
  public void t26_parseIntegerNumberFloatingFractional() {
   * Testa o mtodo {@code parseInteger(Number)} para valores especiais de ponto flutuante ({@link Double#NaN}, {@link Double#POSITIVE_INFINITY}), garantindo que sejam rejeitados.
  public void t27_parseIntegerNumberFloatingSpecials() {
   * Testa o mtodo {@code parseInteger(Object)} para valores nulos e para delegao correta a outros mtodos sobrecarregados ({@link Integer}, {@link Long}, {@link String}).
  public void t30_parseIntegerObjectNullAndDelegates() throws RFWException {
    assertNull("null deve resultar em null", parseInteger((Object) null));
    assertEquals("Integer via Object deve ser convertido corretamente", Integer.valueOf(10), parseInteger((Object) Integer.valueOf(10)));
    assertEquals("Long via Object deve ser delegado para parseInteger(Number)", Integer.valueOf(20), parseInteger((Object) Long.valueOf(20L)));

    assertEquals("String via Object deve ser delegado para parseInteger(String)", Integer.valueOf(30), parseInteger((Object) "30"));
   * Testa o mtodo {@code parseInteger(Object)} para valores booleanos, garantindo a converso {@code true -> 1} e {@code false -> 0}.
   */
  @Test
  public void t31_parseIntegerObjectBoolean() throws RFWException {
    assertEquals("Boolean TRUE deve resultar em 1", Integer.valueOf(1), parseInteger(Boolean.TRUE));
    assertEquals("Boolean FALSE deve resultar em 0", Integer.valueOf(0), parseInteger(Boolean.FALSE));
   * Testa o mtodo {@code parseInteger(Object)} para um tipo no suportado, garantindo que seja lanada {@link RFWValidationException}.
  public void t32_parseIntegerObjectUnsupportedType() throws RFWException {
    parseInteger(new Date());
   * Mtodo utilitrio para validar que {@link RUTypes#parseInteger(String)} lana {@link RFWValidationException} para o valor informado.
      parseInteger(value);

  /**
   * Mtodo utilitrio para validar que {@link RUTypes#parseInteger(Number)} lana {@link RFWValidationException} para o valor informado.
   */
      parseInteger(value);
   * Testa o mtodo {@code parseLong(String)} para valores nulos e vazios.
  public void t60_parseLongStringNullAndEmpty() throws RFWException {
    assertNull("null deve resultar em null", parseLong((String) null));
    assertNull("String vazia deve resultar em null", parseLong(""));
    assertNull("String em branco deve resultar em null", parseLong("   "));
   * Testa o mtodo {@code parseLong(String)} para valores vlidos, incluindo limites de {@link Long}.
  public void t61_parseLongStringValidValues() throws RFWException {
    assertEquals("0 deve ser convertido corretamente", Long.valueOf(0L), parseLong("0"));
    assertEquals("Valor positivo simples", Long.valueOf(123L), parseLong("123"));
    assertEquals("Valor negativo simples", Long.valueOf(-456L), parseLong("-456"));
    assertEquals("Valor com sinal positivo explcito", Long.valueOf(789L), parseLong("+789"));
    assertEquals("Valor com zeros  esquerda", Long.valueOf(7L), parseLong("0007"));
    assertEquals("Limite superior de Long", Long.valueOf(Long.MAX_VALUE), parseLong(String.valueOf(Long.MAX_VALUE)));
    assertEquals("Limite inferior de Long", Long.valueOf(Long.MIN_VALUE), parseLong(String.valueOf(Long.MIN_VALUE)));
   * Testa o mtodo {@code parseLong(String)} para formatos invlidos.
  public void t62_parseLongStringInvalidFormat() {
  }

  /**
   * Testa o mtodo {@code parseLong(String)} para valores fora da faixa de {@link Long}.
   */
  @Test
  public void t63_parseLongStringOutOfRange() {
   * Testa o mtodo {@code parseLong(Number)} para tipos integrais simples.
  public void t70_parseLongNumberIntegralTypes() throws RFWException {
    assertNull("null Number deve resultar em null", parseLong((Number) null));
    assertEquals("Long deve ser retornado diretamente", Long.valueOf(10L), parseLong(Long.valueOf(10L)));
    assertEquals("Integer deve ser convertido corretamente", Long.valueOf(123L), parseLong(Integer.valueOf(123)));
    assertEquals("Short deve ser convertido corretamente", Long.valueOf(5L), parseLong(Short.valueOf((short) 5)));
    assertEquals("Byte deve ser convertido corretamente", Long.valueOf(-8L), parseLong(Byte.valueOf((byte) -8)));
   * Testa o mtodo {@code parseLong(Number)} para {@link BigDecimal} inteiros.
  public void t71_parseLongNumberBigDecimalIntegral() throws RFWException {
    assertEquals("BigDecimal inteiro simples", Long.valueOf(10L), parseLong(new BigDecimal("10")));
    assertEquals("BigDecimal com zeros  direita", Long.valueOf(10L), parseLong(new BigDecimal("10.000")));
    assertEquals("BigDecimal negativo inteiro", Long.valueOf(-5L), parseLong(new BigDecimal("-5")));

  /**
   * Testa o mtodo {@code parseLong(Number)} para {@link BigDecimal} fracionrios.
   */
  @Test
  public void t72_parseLongNumberBigDecimalFractional() {
   * Testa o mtodo {@code parseLong(Number)} para {@link BigDecimal} fora da faixa de {@link Long}.
  public void t73_parseLongNumberBigDecimalOutOfRange() {
  }

  /**
   * Testa o mtodo {@code parseLong(Number)} para {@link Double} e {@link Float} inteiros exatos.
   */
  @Test
  public void t74_parseLongNumberFloatingIntegral() throws RFWException {
    assertEquals("Double inteiro exato", Long.valueOf(10L), parseLong(Double.valueOf(10.0)));
    assertEquals("Float inteiro exato negativo", Long.valueOf(-5L), parseLong(Float.valueOf(-5.0f)));
  }

  /**
   * Testa o mtodo {@code parseLong(Number)} para {@link Double} e {@link Float} fracionrios.
   */
  @Test
  public void t75_parseLongNumberFloatingFractional() {
   * Testa o mtodo {@code parseLong(Number)} para valores especiais de ponto flutuante.
  public void t76_parseLongNumberFloatingSpecials() {
   * Testa o mtodo {@code parseLong(Number)} para um tipo {@link Number} no suportado explicitamente.
  public void t77_parseLongNumberUnsupportedType() {

  /**
   * Testa o mtodo {@code parseLong(Object)} para nulo e delegaes para outros tipos.
   */
  @Test
  public void t80_parseLongObjectNullAndDelegates() throws RFWException {
    assertNull("null deve resultar em null", parseLong((Object) null));
    assertEquals("Long via Object deve ser convertido corretamente", Long.valueOf(10L), parseLong((Object) Long.valueOf(10L)));
    assertEquals("Integer via Object deve ser delegado para parseLong(Number)", Long.valueOf(20L), parseLong((Object) Integer.valueOf(20)));
    assertEquals("String via Object deve ser delegado para parseLong(String)", Long.valueOf(30L), parseLong((Object) "30"));
   * Testa o mtodo {@code parseLong(Object)} para valores booleanos.
  public void t81_parseLongObjectBoolean() throws RFWException {
    assertEquals("Boolean TRUE deve resultar em 1L", Long.valueOf(1L), parseLong(Boolean.TRUE));
    assertEquals("Boolean FALSE deve resultar em 0L", Long.valueOf(0L), parseLong(Boolean.FALSE));

  /**
   * Testa o mtodo {@code parseLong(Object)} para tipo no suportado.
   */
  public void t82_parseLongObjectUnsupportedType() throws RFWException {
    parseLong(new java.util.Date());
   * Helper: valida que {@link RUTypes#parseLong(String)} lana {@link RFWValidationException}.
      parseLong(value);
  }

  /**
   * Helper: valida que {@link RUTypes#parseLong(Number)} lana {@link RFWValidationException}.
   */
      parseLong(value);
  public void t32_parseIntegerObjectUnsupportedType() throws RFWException {
    parseInteger(new Date());
  }

  /**
   * Método utilitário para validar que {@link RUTypes#parseInteger(String)} lança {@link RFWValidationException} para o valor informado.
   *
   * @param value Valor textual a ser testado.
   */
  private void assertParseIntegerStringValidationFailure(String value) {
    try {
      parseInteger(value);
      fail("Era esperada RFWValidationException para o valor: " + value);
    } catch (RFWValidationException e) {
      // esperado
    } catch (RFWException e) {
      fail("Era esperada RFWValidationException, mas foi lançada: " + e.getClass().getSimpleName());
    }
  }

  /**
   * Método utilitário para validar que {@link RUTypes#parseInteger(Number)} lança {@link RFWValidationException} para o valor informado.
   *
   * @param value Valor numérico a ser testado.
   */
  private void assertParseIntegerNumberValidationFailure(Number value) {
    try {
      parseInteger(value);
      fail("Era esperada RFWValidationException para o valor: " + value);
    } catch (RFWValidationException e) {
      // esperado
    } catch (RFWException e) {
      fail("Era esperada RFWValidationException, mas foi lançada: " + e.getClass().getSimpleName());
    }
  }

  /**
   * Testa o método {@code parseLong(String)} para valores nulos e vazios.
   */
  @Test
  public void t60_parseLongStringNullAndEmpty() throws RFWException {
    assertNull("null deve resultar em null", parseLong((String) null));
    assertNull("String vazia deve resultar em null", parseLong(""));
    assertNull("String em branco deve resultar em null", parseLong("   "));
  }

  /**
   * Testa o método {@code parseLong(String)} para valores válidos, incluindo limites de {@link Long}.
   */
  @Test
  public void t61_parseLongStringValidValues() throws RFWException {
    assertEquals("0 deve ser convertido corretamente", Long.valueOf(0L), parseLong("0"));
    assertEquals("Valor positivo simples", Long.valueOf(123L), parseLong("123"));
    assertEquals("Valor negativo simples", Long.valueOf(-456L), parseLong("-456"));
    assertEquals("Valor com sinal positivo explícito", Long.valueOf(789L), parseLong("+789"));
    assertEquals("Valor com zeros à esquerda", Long.valueOf(7L), parseLong("0007"));
    assertEquals("Limite superior de Long", Long.valueOf(Long.MAX_VALUE), parseLong(String.valueOf(Long.MAX_VALUE)));
    assertEquals("Limite inferior de Long", Long.valueOf(Long.MIN_VALUE), parseLong(String.valueOf(Long.MIN_VALUE)));
  }

  /**
   * Testa o método {@code parseLong(String)} para formatos inválidos.
   */
  @Test
  public void t62_parseLongStringInvalidFormat() {
    assertParseLongStringValidationFailure("12.3");
    assertParseLongStringValidationFailure("abc");
    assertParseLongStringValidationFailure("1,000");
    assertParseLongStringValidationFailure("+");
    assertParseLongStringValidationFailure("-");
    assertParseLongStringValidationFailure("++1");
  }

  /**
   * Testa o método {@code parseLong(String)} para valores fora da faixa de {@link Long}.
   */
  @Test
  public void t63_parseLongStringOutOfRange() {
    assertParseLongStringValidationFailure("9223372036854775808"); // Long.MAX_VALUE + 1
    assertParseLongStringValidationFailure("-9223372036854775809"); // Long.MIN_VALUE - 1
  }

  /**
   * Testa o método {@code parseLong(Number)} para tipos integrais simples.
   */
  @Test
  public void t70_parseLongNumberIntegralTypes() throws RFWException {
    assertNull("null Number deve resultar em null", parseLong((Number) null));
    assertEquals("Long deve ser retornado diretamente", Long.valueOf(10L), parseLong(Long.valueOf(10L)));
    assertEquals("Integer deve ser convertido corretamente", Long.valueOf(123L), parseLong(Integer.valueOf(123)));
    assertEquals("Short deve ser convertido corretamente", Long.valueOf(5L), parseLong(Short.valueOf((short) 5)));
    assertEquals("Byte deve ser convertido corretamente", Long.valueOf(-8L), parseLong(Byte.valueOf((byte) -8)));
  }

  /**
   * Testa o método {@code parseLong(Number)} para {@link BigDecimal} inteiros.
   */
  @Test
  public void t71_parseLongNumberBigDecimalIntegral() throws RFWException {
    assertEquals("BigDecimal inteiro simples", Long.valueOf(10L), parseLong(new BigDecimal("10")));
    assertEquals("BigDecimal com zeros à direita", Long.valueOf(10L), parseLong(new BigDecimal("10.000")));
    assertEquals("BigDecimal negativo inteiro", Long.valueOf(-5L), parseLong(new BigDecimal("-5")));
  }

  /**
   * Testa o método {@code parseLong(Number)} para {@link BigDecimal} fracionários.
   */
  @Test
  public void t72_parseLongNumberBigDecimalFractional() {
    assertParseLongNumberValidationFailure(new BigDecimal("10.5"));
    assertParseLongNumberValidationFailure(new BigDecimal("-3.14159"));
  }

  /**
   * Testa o método {@code parseLong(Number)} para {@link BigDecimal} fora da faixa de {@link Long}.
   */
  @Test
  public void t73_parseLongNumberBigDecimalOutOfRange() {
    assertParseLongNumberValidationFailure(new BigDecimal("9223372036854775808"));
    assertParseLongNumberValidationFailure(new BigDecimal("-9223372036854775809"));
  }

  /**
   * Testa o método {@code parseLong(Number)} para {@link Double} e {@link Float} inteiros exatos.
   */
  @Test
  public void t74_parseLongNumberFloatingIntegral() throws RFWException {
    assertEquals("Double inteiro exato", Long.valueOf(10L), parseLong(Double.valueOf(10.0)));
    assertEquals("Float inteiro exato negativo", Long.valueOf(-5L), parseLong(Float.valueOf(-5.0f)));
  }

  /**
   * Testa o método {@code parseLong(Number)} para {@link Double} e {@link Float} fracionários.
   */
  @Test
  public void t75_parseLongNumberFloatingFractional() {
    assertParseLongNumberValidationFailure(Double.valueOf(10.5));
    assertParseLongNumberValidationFailure(Float.valueOf(3.14f));
  }

  /**
   * Testa o método {@code parseLong(Number)} para valores especiais de ponto flutuante.
   */
  @Test
  public void t76_parseLongNumberFloatingSpecials() {
    assertParseLongNumberValidationFailure(Double.valueOf(Double.NaN));
    assertParseLongNumberValidationFailure(Double.valueOf(Double.POSITIVE_INFINITY));
    assertParseLongNumberValidationFailure(Double.valueOf(Double.NEGATIVE_INFINITY));
  }

  /**
   * Testa o método {@code parseLong(Number)} para um tipo {@link Number} não suportado explicitamente.
   */
  @Test
  public void t77_parseLongNumberUnsupportedType() {
    Number customNumber = new Number() {
      private static final long serialVersionUID = 1L;

      @Override
      public int intValue() {
        return 0;
      }

      @Override
      public long longValue() {
        return 0L;
      }

      @Override
      public float floatValue() {
        return 0.0f;
      }

      @Override
      public double doubleValue() {
        return 0.0;
      }
    };
    assertParseLongNumberValidationFailure(customNumber);
  }

  /**
   * Testa o método {@code parseLong(Object)} para nulo e delegações para outros tipos.
   */
  @Test
  public void t80_parseLongObjectNullAndDelegates() throws RFWException {
    assertNull("null deve resultar em null", parseLong((Object) null));
    assertEquals("Long via Object deve ser convertido corretamente", Long.valueOf(10L), parseLong((Object) Long.valueOf(10L)));
    assertEquals("Integer via Object deve ser delegado para parseLong(Number)", Long.valueOf(20L), parseLong((Object) Integer.valueOf(20)));
    assertEquals("String via Object deve ser delegado para parseLong(String)", Long.valueOf(30L), parseLong((Object) "30"));
  }

  /**
   * Testa o método {@code parseLong(Object)} para valores booleanos.
   */
  @Test
  public void t81_parseLongObjectBoolean() throws RFWException {
    assertEquals("Boolean TRUE deve resultar em 1L", Long.valueOf(1L), parseLong(Boolean.TRUE));
    assertEquals("Boolean FALSE deve resultar em 0L", Long.valueOf(0L), parseLong(Boolean.FALSE));
  }

  /**
   * Testa o método {@code parseLong(Object)} para tipo não suportado.
   */
  @Test(expected = RFWValidationException.class)
  public void t82_parseLongObjectUnsupportedType() throws RFWException {
    parseLong(new java.util.Date());
  }

  /**
   * Helper: valida que {@link RUTypes#parseLong(String)} lança {@link RFWValidationException}.
   */
  private void assertParseLongStringValidationFailure(String value) {
    try {
      parseLong(value);
      fail("Era esperada RFWValidationException para o valor: " + value);
    } catch (RFWValidationException e) {
      // esperado
    } catch (RFWException e) {
      fail("Era esperada RFWValidationException, mas foi lançada: " + e.getClass().getSimpleName());
    }
  }

  /**
   * Helper: valida que {@link RUTypes#parseLong(Number)} lança {@link RFWValidationException}.
   */
  private void assertParseLongNumberValidationFailure(Number value) {
    try {
      parseLong(value);
      fail("Era esperada RFWValidationException para o valor: " + value);
    } catch (RFWValidationException e) {
      // esperado
    } catch (RFWException e) {
      fail("Era esperada RFWValidationException, mas foi lançada: " + e.getClass().getSimpleName());
    }
  }

  @Test
  public void t01_formatAndParseDateMethods() throws RFWException {
    ZoneId zoneSP = ZoneId.of("America/Sao_Paulo");
    LocalDateTime ldt = LocalDateTime.of(2024, 2, 20, 15, 30, 0);

    // ---- formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime, ZoneOffset) (UTC) ----
    String formattedUtc = RUTypes.formatToyyyy_MM_dd_T_HH_mm_ssXXX(ldt, ZoneOffset.UTC);
    assertEquals("2024-02-20T15:30:00Z", formattedUtc);

    // ---- formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime, ZoneId) ----
    String formattedSp = RUTypes.formatToyyyy_MM_dd_T_HH_mm_ssXXX(ldt, zoneSP);
    ZonedDateTime zdtSp = ldt.atZone(zoneSP);
    String expectedSp = zdtSp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
    assertEquals(expectedSp, formattedSp);

    // ---- parseLocalDateTime(String) - ISO local sem timezone ----
    LocalDateTime parsedLocal = RUTypes.parseLocalDateTime("2024-02-20T15:30:00");
    assertEquals(ldt, parsedLocal);

    // ---- parseDate(String, ZoneId) - ISO com timezone ----
    String withOffset = "2024-02-20T15:30:00-07:00";
    Date parsedDate = RUTypes.parseDate(withOffset, zoneSP);

    // Cálculo esperado com a API java.time
    OffsetDateTime odt = OffsetDateTime.parse(withOffset);
    Date expectedDate = Date.from(odt.atZoneSameInstant(zoneSP).toInstant());

    assertEquals(expectedDate.toInstant(), parsedDate.toInstant());
  }
}
