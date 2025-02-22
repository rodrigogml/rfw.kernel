package br.eng.rodrigogml.rfw.kernel.utils;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilitária para concentrar o cálculo de Dígivos Verificadores (DV).<br>
 * Útil já que na maioria dos casos de cálculos de DV são usados uma pequena coleção de algorítimos, como Mod10 e Mod11.
 *
 * @author Rodrigo Leitão
 * @since 4.0.0 (28/01/2011)
 * @deprecated Esta classe está migrando para a {@link RUDV}
 */
@Deprecated
public class RUDVCalc {

  private RUDVCalc() {
  }

  /**
   * Calcula o Dígito Verificador (DV) da Chave de Acesso da NF-e versão 4.00 utilizando o algoritmo do Módulo 11, conforme especificado no Manual da NF-e.
   *
   * @param keyPrefix String contendo os 43 primeiros dígitos da chave de acesso.
   * @return String contendo o dígito verificador calculado.
   * @throws RFWException Se a entrada for nula, vazia ou não conter exatamente 43 dígitos numéricos.
   */
  public static String calcDVDANFeV400(String keyPrefix) throws RFWException {
    // Remover caracteres não numéricos
    keyPrefix = RUString.removeNonDigits(keyPrefix);

    // Validar se a chave possui exatamente 43 dígitos
    if (keyPrefix == null || keyPrefix.length() != 43 || !keyPrefix.matches("[0-9]+")) {
      throw new RFWValidationException("RFW_000047", new String[] { keyPrefix });
    }

    // Pesos definidos no manual da NF-e (sequência cíclica de 2 a 9)
    int[] weights = { 2, 3, 4, 5, 6, 7, 8, 9 };

    long sum = 0;
    int weightIndex = 0;

    // Percorrer os dígitos da direita para a esquerda
    for (int i = 42; i >= 0; i--) {
      int digit = Character.getNumericValue(keyPrefix.charAt(i));
      sum += digit * weights[weightIndex];

      // Incrementar o índice do peso e reiniciar quando atingir o final do array
      weightIndex = (weightIndex + 1) % weights.length;
    }

    // Aplicar a regra do Módulo 11
    long remainder = sum % 11;
    long checkDigit = 11 - remainder;

    // Se o resultado for 0 ou 1, o dígito verificador deve ser 0
    if (checkDigit >= 10) {
      checkDigit = 0;
    }

    return String.valueOf(checkDigit);
  }

  /**
   * Calcula um dígito verificador usando módulo de 10 utilizado nos blocos da linha digitável dos boletos.<br>
   *
   * @param value Valor contendo apenas dígitos do bloco para que seja calculado o DV.
   * @return String contendo apenas 1 caracter que será o DV.
   * @throws RFWValidationException Lançado caso o valor não tenha apenas números, ou seja um valor nulo/vazio.
   */
  public static String calcBoletoBlocoDigitavelDV(String value) throws RFWValidationException {
    if (value == null || !value.matches("[0-9]+")) {
      throw new RFWValidationException("RFW_000058");
    }
    int factor = 2;
    int counter = 0;
    for (int i = value.length() - 1; i >= 0; i--) {
      String tmpval = "" + value.charAt(i);

      int mult = factor * Integer.parseInt(tmpval);
      do {
        counter += (mult % 10);
        mult = mult / 10;
      } while (mult > 0);

      if (factor == 2) {
        factor = 1;
      } else {
        factor = 2;
      }
    }
    int tmod = counter % 10;
    if (tmod == 0) {
      return "0";
    } else {
      return "" + (10 - tmod);
    }
  }

  /**
   * Calcula um dígito verificador usando módulo de 11, com uma base de 2 à 9.<br>
   * Multiplicando cada número do valor passado pelos números da base (2, 3,..., 9) e somando, ao final obtém o módulo da dívisão por 11, e subtrai de 11. Caso o resultado do cálculo seja igual a 11 (resto 0) ou 10 (resto 1) o DV será 0.<br>
   * <br>
   * <b>Casos conhecidos que usam esta validação:</b>
   * <ul>
   * <li>Chave de Acesso da NFe</li>
   * <li>DV Geral do Boleto de Cobrança</li>
   * </ul>
   *
   * @param value valor contendo apenas dígitos para que seja calculado o DV.
   * @return String contendo apenas 1 caracter que será o DV.
   * @throws RFWValidationException Lançado caso o valor não tenha apenas números, ou seja um valor nulo/vazio.
   */
  public static String calcNFeDV(String value) throws RFWException {
    value = RUString.removeNonDigits(value);
    if (value == null || !value.matches("[0-9]+")) {
      throw new RFWValidationException("RFW_000052");
    }

    int[] base = { 2, 3, 4, 5, 6, 7, 8, 9 };
    String[] digits = value.split("|");

    long sum = 0;
    int basecount = 0;
    for (int i = digits.length - 1; i >= 0; i--) {
      sum += base[basecount] * new Long(digits[i]);
      basecount++;
      basecount = (basecount % base.length);
    }
    long mod = 11 - (sum % 11);
    if (mod >= 10) {
      mod = 1;
    }

    return "" + mod;
  }

  /**
   * Calcula um dígito verificador usando módulo de 11, com uma base de 2 à 9.<br>
   * Multiplicando cada número do valor passado pelos números da base (2, 3,..., 9) e somando, ao final obtém o módulo da dívisão por 11, e subtrai de 11. Caso o resultado do cálculo seja igual a 11 (resto 0) ou 10 (resto 1) o DV será 0.<br>
   * <br>
   * <b>Casos conhecidos que usam esta validação:</b>
   * <ul>
   * <li>Chave de Acesso da NFe</li>
   * <li>DV Geral do Boleto de Cobrança</li>
   * </ul>
   *
   * @param value valor contendo apenas dígitos para que seja calculado o DV.
   * @return String contendo apenas 1 caracter que será o DV.
   * @throws RFWValidationException Lançado caso o valor não tenha apenas números, ou seja um valor nulo/vazio.
   */
  public static String calcPaymentSlipDVForServices(String value) throws RFWException {
    value = RUString.removeNonDigits(value);
    if (value == null || !value.matches("[0-9]+")) {
      throw new RFWValidationException("RFW_000052");
    }

    int[] base = { 2, 3, 4, 5, 6, 7, 8, 9 };
    String[] digits = value.split("|");

    long sum = 0;
    int basecount = 0;
    for (int i = digits.length - 1; i >= 0; i--) {
      sum += base[basecount] * new Long(digits[i]);
      basecount++;
      basecount = (basecount % base.length);
    }
    long mod = 11 - (sum % 11);
    if (mod >= 10) {
      mod = 1;
    }

    return "" + mod;
  }

  /**
   * Calcula um dígito verificador usando módulo de 11 APENAS PARA GUIAS DE ARRECAÇÃO DO GOVERNO / BOLETOS DE SERVIÇO, com uma base de 2 à 9.<br>
   * <br>
   * <b>Casos conhecidos que usam esta validação:</b>
   * <ul>
   * <li>Guia de FGTS</li>
   * <li>Guia de GPS</li>
   * </ul>
   *
   * @param value valor contendo apenas dígitos para que seja calculado o DV.
   * @return String contendo apenas 1 caracter que será o DV.
   * @throws RFWValidationException Lançado caso o valor não tenha apenas números, ou seja um valor nulo/vazio.
   */
  public static String calcPaymentSlipDVForGovernment(String value) throws RFWException {
    value = RUString.removeNonDigits(value);
    if (value == null || !value.matches("[0-9]+")) {
      throw new RFWValidationException("RFW_000052");
    }

    int[] base = { 2, 3, 4, 5, 6, 7, 8, 9 };
    String[] digits = value.split("|");

    long sum = 0;
    int basecount = 0;
    for (int i = digits.length - 1; i >= 0; i--) {
      sum += base[basecount] * new Long(digits[i]);
      basecount++;
      basecount = (basecount % base.length);
    }
    long mod = 11 - (sum % 11);
    if (mod >= 10) {
      mod = 0;
    }

    return "" + mod;
  }

  /**
   * Calcula o dígito verificador para o código de barras EAN. Funciona para EAN8 e EAN13.
   *
   * @param code código que deveserá ser validado, note que para o EAN8 devem ser passados apenas 7 digitos, já no EAN13 devem ser passados apenas 12.
   * @return Último digito do código de barra que valida a numeração.
   *
   * @throws RFWException
   */
  public static String calcEANDV(String code) throws RFWValidationException {
    int n1 = 0;
    int n0 = 0;
    // Valida se o código não é de tamanho inválido
    if (code == null || (code.length() != 7 && code.length() != 12)) {
      throw new RFWValidationException("RFW_ERR_200058");
    }
    for (int i = 0; i < code.length(); i += 2) {
      n1 += new Integer(code.substring(i, i + 1));
      n0 += new Integer(code.substring(i + 1, i + 2));
    }
    n0 = n0 * 3;
    return "" + (10 - ((n1 + n0) % 10));
  }

}
