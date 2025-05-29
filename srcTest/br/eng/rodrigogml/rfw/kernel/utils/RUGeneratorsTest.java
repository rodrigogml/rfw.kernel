package br.eng.rodrigogml.rfw.kernel.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Classe de testes para a classe {@link RUGenerators}.<br>
 * Verifica o funcionamento dos métodos de geração de dados da classe utilitária.
 *
 * @author BIS DEV
 * @since 10.0.0
 */
public class RUGeneratorsTest {

  /**
   * Testa o método {@link RUGenerators#generateUUID()} para garantir que o UUID gerado corresponde à expressão regular definida em {@link RUGenerators#UUID_REGEXP}.
   */
  @Test
  public void testGenerateUUID() {
    String uuid = RUGenerators.generateUUID();
    assertNotNull("UUID não pode ser nulo", uuid);
    assertTrue("UUID inválido: " + uuid, Pattern.matches(RUGenerators.UUID_REGEXP, uuid));
  }

  /**
   * Testa o método {@link RUGenerators#generateString(int)} para diferentes tamanhos.
   */
  @Test
  public void testGenerateString() {
    for (int i = 1; i <= 50; i++) {
      String result = RUGenerators.generateString(i);
      assertNotNull("String gerada não pode ser nula", result);
      assertEquals("Tamanho incorreto da string gerada", i, result.length());
      assertTrue("String contém caracteres inválidos: " + result,
          result.matches("[A-Za-z0-9]{" + i + "}"));
    }
  }

  /**
   * Testa o método {@link RUGenerators#generateStringDigits(int)} para diferentes tamanhos.
   */
  @Test
  public void testGenerateStringDigits() {
    for (int i = 1; i <= 50; i++) {
      String result = RUGenerators.generateStringDigits(i);
      assertNotNull("String de dígitos não pode ser nula", result);
      assertEquals("Tamanho incorreto da string de dígitos", i, result.length());
      assertTrue("String de dígitos contém caracteres inválidos: " + result,
          result.matches("[0-9]{" + i + "}"));
    }
  }

  /**
   * Testa o método {@link RUGenerators#generateNumericSequence(int)} para diferentes tamanhos.
   */
  @Test
  public void testGenerateNumericSequence() {
    for (int i = 1; i <= 50; i++) {
      String result = RUGenerators.generateNumericSequence(i);
      assertNotNull("Sequência numérica não pode ser nula", result);
      assertEquals("Tamanho incorreto da sequência numérica", i, result.length());
      assertTrue("Sequência numérica contém caracteres inválidos: " + result,
          result.matches("[0-9]{" + i + "}"));
    }
  }
}
