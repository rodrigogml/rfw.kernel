package br.eng.rodrigogml.rfw.kernel.utils;

import static br.eng.rodrigogml.rfw.kernel.utils.RUString.extract;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.extractCNPJ;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.extractCodes;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.extractDateDDMMYYYY;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.extractDateMMYYYY;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.extractDecimalValues;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.extractServiceNumericCode;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.extractTimeHHMMSS;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.left;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.parseCSVLine;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.removeLeadingZeros;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.replaceAll;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.replaceAllRecursively;
import static br.eng.rodrigogml.rfw.kernel.utils.RUString.right;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

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
   *
   * @throws RFWException
   */
  @Test
  public void t00_replaceAllRecursively() throws RFWException {
    assertEquals("Deve substituir corretamente uma �nica ocorr�ncia.", "Ol� Mundo!", replaceAllRecursively("Ol� Teste!", "Teste", "Mundo", true, true));
    assertEquals("Deve substituir m�ltiplas ocorr�ncias recursivamente.", "Mundo Mundo!", replaceAllRecursively("Teste Teste!", "Teste", "Mundo", true, true));
    assertEquals("Deve substituir corretamente ignorando case.", "Ol� Mundo!", replaceAllRecursively("Ol� TeStE!", "teste", "Mundo", true, false));
    assertEquals("Deve substituir corretamente ignorando acentos.", "Ola Teste!", replaceAllRecursively("�l� Teste!", "Ol�", "Ola", false, true));
    assertEquals("Deve substituir corretamente ignorando case e acentos.", "ola T�StE!", replaceAllRecursively("�l� T�StE!", "ol�", "ola", false, false));
    assertEquals("Nenhuma substitui��o quando oldValue n�o est� presente.", "Ol� Teste!", replaceAllRecursively("Ol� Teste!", "XYZ", "Mundo", true, true));
    assertEquals("Deve evitar substitui��es infinitas.", "!", replaceAllRecursively("LoLoLoLoLoopopopopop!", "Loop", "", true, true));
    assertThrows(IllegalArgumentException.class, () -> replaceAllRecursively("Ol� Teste!", "", "Mundo", true, true));
    assertThrows(RFWCriticalException.class, () -> replaceAllRecursively("Loop!", "Loop", "Loop!", true, true));
  }

  /**
   * Teste unit�rio para o m�todo removeLeadingZeros.
   * <p>
   * Este teste cobre os seguintes cen�rios:
   * <ul>
   * <li>String null deve retornar null.</li>
   * <li>String vazia deve retornar vazia.</li>
   * <li>String contendo apenas zeros deve retornar vazia.</li>
   * <li>String com zeros � esquerda deve ter os zeros removidos.</li>
   * <li>String com espa�os antes dos zeros deve permanecer inalterada.</li>
   * <li>String sem zeros � esquerda deve permanecer inalterada.</li>
   * </ul>
   */
  @Test
  public void t00_removeLeadingZeros() {
    assertNull(removeLeadingZeros(null));
    assertEquals("", removeLeadingZeros(""));
    assertEquals("", removeLeadingZeros("0000000"));
    assertEquals("1234", removeLeadingZeros("0001234"));
    assertEquals("12340000", removeLeadingZeros("00012340000"));
    assertEquals(" 00012340000", removeLeadingZeros(" 00012340000"));
    assertEquals("test", removeLeadingZeros("test"));
    assertEquals("test", removeLeadingZeros("0test"));
    assertEquals("1234", removeLeadingZeros("0001234"));
    assertEquals(null, removeLeadingZeros(null));
    assertEquals("12340000", removeLeadingZeros("00012340000"));
    assertEquals(" 00012340000", removeLeadingZeros(" 00012340000"));
    assertEquals("", removeLeadingZeros("000000000000000000"));
  }

  /**
   * Testa o m�todo right(String, int).
   *
   * Cen�rios testados: - String nula - Tamanho maior que a string original - Tamanho igual ao da string original - Tamanho zero ou negativo - Tamanho menor que a string original
   */
  @Test
  public void t00_right() {
    assertNull(right(null, 5)); // Teste com string nula
    assertEquals("abcdef", right("abcdef", 10)); // Tamanho maior que a string original
    assertEquals("abcdef", right("abcdef", 6)); // Tamanho igual � string original
    assertEquals("", right("abcdef", 0)); // Tamanho zero
    assertEquals("", right("abcdef", -1)); // Tamanho negativo
    assertEquals("def", right("abcdef", 3)); // Parte direita menor que a original
  }

  /**
   * Testa o m�todo left(String, int).
   *
   * Cen�rios testados: - String nula - Tamanho maior que a string original - Tamanho igual ao da string original - Tamanho zero ou negativo - Tamanho menor que a string original
   */
  @Test
  public void t00_left() {
    assertNull(left(null, 5)); // Teste com string nula
    assertEquals("abcdef", left("abcdef", 10)); // Tamanho maior que a string original
    assertEquals("abcdef", left("abcdef", 6)); // Tamanho igual � string original
    assertEquals("", left("abcdef", 0)); // Tamanho zero
    assertEquals("", left("abcdef", -1)); // Tamanho negativo
    assertEquals("abc", left("abcdef", 3)); // Parte esquerda menor que a original
  }

  /**
   * Testes unit�rios para o m�todo parseCSVLine.
   */
  @Test
  public void t00_parseCSVLine() {
    try {
      // Teste 1: Linha CSV simples sem aspas
      String[] result1 = parseCSVLine("campo1,campo2,campo3");
      assertArrayEquals(new String[] { "campo1", "campo2", "campo3" }, result1);

      // Teste 2: Linha CSV com aspas escapando v�rgulas
      String[] result2 = parseCSVLine("\"Meu CSV tem , no meio do texto\",campo2,etc.");
      assertArrayEquals(new String[] { "Meu CSV tem , no meio do texto", "campo2", "etc." }, result2);

      // Teste 3: Linha CSV com aspas dentro do campo
      String[] result3 = parseCSVLine("\"Meu CSV tem \"\"\",\"campo 2\",etc.");
      assertArrayEquals(new String[] { "Meu CSV tem \"", "campo 2", "etc." }, result3);

      // Teste 4: Linha CSV vazia
      String[] result4 = parseCSVLine("");
      assertArrayEquals(new String[] {}, result4);

      // Teste 5: Linha CSV com espa�o extra entre campos
      String[] result5 = parseCSVLine(" campo1 , campo2 , campo3 ");
      assertArrayEquals(new String[] { " campo1 ", " campo2 ", " campo3 " }, result5);

      // Teste 6: Linha CSV com diferentes separadores
      String[] result6 = parseCSVLine("campo1|campo2|campo3", '|', '"');
      assertArrayEquals(new String[] { "campo1", "campo2", "campo3" }, result6);

      // Teste 7: Linha CSV com tabula��o como separador
      String[] result7 = parseCSVLine("campo1\tcampo2\tcampo3", '\t', '"');
      assertArrayEquals(new String[] { "campo1", "campo2", "campo3" }, result7);

      // Teste 8: Linha CSV com quebras de linha
      String[] result8 = parseCSVLine("campo1,campo2\ncampo3");
      assertArrayEquals(new String[] { "campo1", "campo2\ncampo3" }, result8);

      // Teste 9: Campo com espa�os internos
      String[] result9 = parseCSVLine("\"  espa�ado  \",normal");
      assertArrayEquals(new String[] { "  espa�ado  ", "normal" }, result9);

    } catch (RFWException e) {
      fail("N�o deveria lan�ar exce��o: " + e.getMessage());
    }
  }

  @Test
  public void t00_extractDecimalValues() throws RFWException {
    String init = "bla bla blasxe";
    String end = "asdalsdfadf asdf asdf af";

    String[] values = new String[] { "1,24", "1.456,00", "41.455.245,6", "1.455.245,6", "1455245,6" };

    // Itera todos os valores considerando a String no come�o da linha, no fim da linha, sozinha na linha, ou com conte�do no in�cio e no fim
    for (int i = 0; i < values.length; i++) {
      assertEquals("Falha no Valor: " + values[i], values[i], extractDecimalValues(values[i], 1, true));
      assertEquals("Falha no Valor: " + values[i], values[i], extractDecimalValues(init + values[i], 1, true));
      assertEquals("Falha no Valor: " + values[i], values[i], extractDecimalValues(values[i] + end, 1, true));
      assertEquals("Falha no Valor: " + values[i], values[i], extractDecimalValues(init + values[i] + end, 1, true));
    }
  }

  /**
   * Teste unit�rio para o m�todo extractDecimalValues.<br>
   * Verifica a extra��o correta de valores decimais considerando diferentes formatos de separadores.<br>
   */
  @Test
  public void t01_extractDecimalValues() throws RFWException {
    assertEquals("1.234,56", extractDecimalValues("O valor � 1.234,56.", 1, true));
    assertEquals("1,234.56", extractDecimalValues("Total: 1,234.56$", 1, false));
    assertEquals(null, extractDecimalValues("Nenhum n�mero aqui.", 1, true));
    assertEquals("3.456,78", extractDecimalValues("Pre�o: 3.456,78", 1, true));
  }

  /**
   * Teste unit�rio para o m�todo extract.<br>
   * Verifica a extra��o correta de substrings baseadas em express�es regulares.<br>
   */
  @Test
  public void t00_extract() throws RFWException {
    assertEquals("123", extract("O c�digo � 123.", "\\b(\\d{3})\\b"));
    assertEquals("456", extract("N�mero 456 est� correto.", "\\b(\\d{3})\\b"));
    assertEquals(null, extract("Texto sem n�meros.", "\\b(\\d{3})\\b"));
  }

  /**
   * Teste unit�rio para o m�todo extract com grupo espec�fico.<br>
   * Verifica a extra��o correta de grupos nomeados ou numerados.<br>
   */
  @Test
  public void t00_extract_withGroup() throws RFWException {
    assertEquals("456", extract("N�mero: 123-456-789", "(\\d{3})-(\\d{3})-(\\d{3})", 2));
    assertEquals("789", extract("N�mero: 123-456-789", "(\\d{3})-(\\d{3})-(\\d{3})", 3));
    assertEquals(null, extract("Sem grupo v�lido", "(\\d{3})-(\\d{3})-(\\d{3})", 2));
  }

  /**
   * Teste unit�rio para o m�todo extractDateDDMMYYYY.<br>
   * Verifica a extra��o de datas no formato dd/MM/yyyy, considerando diferentes meses e anos.<br>
   *
   * @throws RFWException
   */
  @Test
  public void t00_extractDateDDMMYYYY() throws RFWException {
    assertEquals("01/01/2020", extractDateDDMMYYYY("Data: 01/01/2020.", 1));
    assertEquals("31/12/2021", extractDateDDMMYYYY("Data final: 31/12/2021.", 1));
    assertEquals(null, extractDateDDMMYYYY("Texto sem data.", 1));
    assertEquals("29/02/2020", extractDateDDMMYYYY("Fevereiro: 29/02/2020.", 1));
  }

  /**
   * Teste unit�rio para o m�todo extractDateMMYYYY.<br>
   * Verifica a extra��o de datas no formato MM/yyyy.<br>
   *
   * @throws RFWException
   */
  @Test
  public void t00_extractDateMMYYYY() throws RFWException {
    assertEquals("01/2020", extractDateMMYYYY("M�s: 01/2020.", 1));
    assertEquals("12/2021", extractDateMMYYYY("Data final: 12/2021.", 1));
    assertEquals(null, extractDateMMYYYY("Texto sem data.", 1));
  }

  /**
   * Teste unit�rio para o m�todo extractTimeHHMMSS.<br>
   * Verifica a extra��o de hor�rios no formato hh:mm:ss.<br>
   *
   * @throws RFWException
   */
  @Test
  public void t00_extractTimeHHMMSS() throws RFWException {
    assertEquals("23:59:59", extractTimeHHMMSS("Hora: 23:59:59.", 1));
    assertEquals("00:00:00", extractTimeHHMMSS("In�cio: 00:00:00.", 1));
    assertEquals(null, extractTimeHHMMSS("Sem hor�rio v�lido.", 1));
  }

  /**
   * Teste unit�rio para o m�todo extractCodes.<br>
   * Verifica a extra��o de uma sequ�ncia num�rica com um n�mero exato de d�gitos.<br>
   *
   * @throws RFWException
   */
  @Test
  public void t00_extractCodes() throws RFWException {
    assertEquals("1234", extractCodes("C�digo: 1234", 4, 1));
    assertEquals("5678", extractCodes("N�mero: 5678", 4, 1));
    assertEquals(null, extractCodes("Sem c�digo", 4, 1));
  }

  /**
   * Teste unit�rio para o m�todo extractCNPJ.<br>
   * Verifica a extra��o de um CNPJ do texto.<br>
   *
   * @throws RFWException
   */
  @Test
  public void t00_extractCNPJ() throws RFWException {
    assertEquals("12.345.678/0001-99", extractCNPJ("CNPJ: 12.345.678/0001-99", 1));
    assertEquals("98.765.432/0001-11", extractCNPJ("CNPJ: 98.765.432/0001-11", 1));
    assertEquals(null, extractCNPJ("Sem CNPJ", 1));
  }

  /**
   * Teste unit�rio para o m�todo extractServiceNumericCode.<br>
   * Verifica a extra��o de c�digo num�rico de servi�o/consumo.<br>
   *
   * @throws RFWException
   */
  @Test
  public void t00_extractServiceNumericCode() throws RFWException {
    assertEquals("12345678901 12345678901 12345678901 12345678901", extractServiceNumericCode("C�digo de servi�o: 12345678901 12345678901 12345678901 12345678901", 1));
    assertEquals("1234567890-1 1234567890-1 1234567890-1 1234567890-1", extractServiceNumericCode("C�digo de servi�o: 1234567890-1 1234567890-1 1234567890-1 1234567890-1", 1));
    assertEquals("1234567890 1 1234567890 1 1234567890 1 1234567890 1", extractServiceNumericCode("C�digo de servi�o: 1234567890 1 1234567890 1 1234567890 1 1234567890 1", 1));
    assertEquals(null, extractServiceNumericCode("Servi�o: 09876543210 Outro codigo 09876543210 e mais um 09876543210, 09876543210", 1));
    assertEquals(null, extractServiceNumericCode("Texto sem c�digo de servi�o.", 1));
  }

}
