package br.eng.rodrigogml.rfw.kernel.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Classe de testes para a classe {@link RUGenerators}.<br>
 * Verifica o funcionamento dos m�todos de gera��o de dados da classe utilit�ria.
 *
 * @author BIS DEV
 * @since 10.0.0
 */
public class RUGeneratorsTest {

  /**
   * Testa o m�todo {@link RUGenerators#generateUUID()} para garantir que o UUID gerado corresponde � express�o regular definida em {@link RUGenerators#UUID_REGEXP}.
   */
  @Test
  public void testGenerateUUID() {
    String uuid = RUGenerators.generateUUID();
    assertNotNull("UUID n�o pode ser nulo", uuid);
    assertTrue("UUID inv�lido: " + uuid, Pattern.matches(RUGenerators.UUID_REGEXP, uuid));
  }

  /**
   * Testa o m�todo {@link RUGenerators#generateString(int)} para diferentes tamanhos.
   */
  @Test
  public void testGenerateString() {
    for (int i = 1; i <= 50; i++) {
      String result = RUGenerators.generateString(i);
      assertNotNull("String gerada n�o pode ser nula", result);
      assertEquals("Tamanho incorreto da string gerada", i, result.length());
      assertTrue("String cont�m caracteres inv�lidos: " + result,
          result.matches("[A-Za-z0-9]{" + i + "}"));
    }
  }

  /**
   * Testa o m�todo {@link RUGenerators#generateStringDigits(int)} para diferentes tamanhos.
   */
  @Test
  public void testGenerateStringDigits() {
    for (int i = 1; i <= 50; i++) {
      String result = RUGenerators.generateStringDigits(i);
      assertNotNull("String de d�gitos n�o pode ser nula", result);
      assertEquals("Tamanho incorreto da string de d�gitos", i, result.length());
      assertTrue("String de d�gitos cont�m caracteres inv�lidos: " + result,
          result.matches("[0-9]{" + i + "}"));
    }
  }

  /**
   * Testa o m�todo {@link RUGenerators#generateNumericSequence(int)} para diferentes tamanhos.
   */
  @Test
  public void testGenerateNumericSequence() {
    for (int i = 1; i <= 50; i++) {
      String result = RUGenerators.generateNumericSequence(i);
      assertNotNull("Sequ�ncia num�rica n�o pode ser nula", result);
      assertEquals("Tamanho incorreto da sequ�ncia num�rica", i, result.length());
      assertTrue("Sequ�ncia num�rica cont�m caracteres inv�lidos: " + result,
          result.matches("[0-9]{" + i + "}"));
    }
  }
}
