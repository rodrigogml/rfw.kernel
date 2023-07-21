package br.eng.rodrigogml.rfw.kernel.utils;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilitária para concentrar o cálculo de Dígivos Verificadores (DV).<br>
 * Útil já que na maioria dos casos de cálculos de DV são usados uma pequena coleção de algorítimos, como Mod10 e Mod11.
 *
 * @author Rodrigo Leitão
 * @since 4.0.0 (28/01/2011)
 */
public class RUDVCalc {

  private RUDVCalc() {
  }

  /**
   * Este método calcula o dígito verificador usado no CNPJ. Calcula por módulo de 11 o primeiro dígito verificador, e depois calcula o segundo digito verificador também com módulo de 11, mas com a matriz multiplicadora deslocada, começando em 3.
   *
   * @param cnpj 12 algarismos que compõe o CNPJ, incluindo os 4 que indica o número da filial.
   * @return
   * @throws RFWValidationException
   */
  public static String calcCNPJValidateDigit(String cnpj) throws RFWValidationException {
    // Verifica se o CNPJ tem 12 algarismos (sem os dois dígitos verificadores)
    if (!cnpj.matches("[0-9]{12}")) {
      throw new RFWValidationException("RFW_ERR_200015");
    }
    char[] dig = cnpj.toCharArray();
    int[] mat = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
    int[] mat2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3 };

    // Calculo do DV1
    long tot = 0;
    for (int i = 0; i < dig.length; i++) {
      tot += Integer.parseInt("" + dig[i]) * mat[i];
    }
    int mod = (int) (tot % 11);
    int dv1 = 0;
    if (mod >= 2) {
      dv1 = (11 - mod);
    }
    // Calculo do DV2
    tot = 0;
    for (int i = 0; i < dig.length; i++) {
      tot += Integer.parseInt("" + dig[i]) * mat2[i];
    }
    tot += dv1 * 2;
    mod = (int) (tot % 11);
    int dv2 = 0;
    if (mod >= 2) {
      dv2 = (11 - mod);
    }
    return "" + dv1 + dv2;
  }

  /**
   * Este método calcula o dígito verificador usado no CPF.<br>
   * Calcula por módulo de 11 o primeiro dígito verificador, e depois calcula o segundo digito verificador também com módulo de 11, mas com a matriz multiplicadora deslocada, começando em 3.
   *
   * @param cpf 9 algarismos que compõe o CPF.
   * @return
   * @throws RFWValidationException
   */
  public static String calcCPFValidateDigit(String cpf) throws RFWValidationException {
    // Verifica se o CNPJ tem 13 algarismos (sem os dois dígitos verificadores)
    if (!cpf.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_200020");
    }
    char[] dig = cpf.toCharArray();
    int[] mat = { 10, 9, 8, 7, 6, 5, 4, 3, 2 };
    int[] mat2 = { 11, 10, 9, 8, 7, 6, 5, 4, 3 };

    // Calculo do DV1
    long tot = 0;
    for (int i = 0; i < dig.length; i++) {
      tot += Integer.parseInt("" + dig[i]) * mat[i];
    }
    int mod = (int) (tot % 11);
    int dv1 = 0;
    if (mod >= 2) {
      dv1 = (11 - mod);
    }
    // Calculo do DV2
    tot = 0;
    for (int i = 0; i < dig.length; i++) {
      tot += Integer.parseInt("" + dig[i]) * mat2[i];
    }
    tot += dv1 * 2;
    mod = (int) (tot % 11);
    int dv2 = 0;
    if (mod >= 2) {
      dv2 = (11 - mod);
    }
    return "" + dv1 + dv2;
  }
}
