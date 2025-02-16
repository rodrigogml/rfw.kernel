package br.eng.rodrigogml.rfw.kernel.utils;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe utilit�ria para concentrar o c�lculo de D�givos Verificadores (DV).<br>
 * �til j� que na maioria dos casos de c�lculos de DV s�o usados uma pequena cole��o de algor�timos, como Mod10 e Mod11.
 *
 * @author Rodrigo Leit�o
 * @since 4.0.0 (28/01/2011)
 */
public class RUDVCalc {

  private RUDVCalc() {
  }

  /**
   * Este m�todo calcula o d�gito verificador usado no CNPJ. Calcula por m�dulo de 11 o primeiro d�gito verificador, e depois calcula o segundo digito verificador tamb�m com m�dulo de 11, mas com a matriz multiplicadora deslocada, come�ando em 3.
   *
   * @param cnpj 12 algarismos que comp�e o CNPJ, incluindo os 4 que indica o n�mero da filial.
   * @return
   * @throws RFWException
   */
  public static String calcDVCNPJ(String cnpj) throws RFWException {
    // Verifica se o CNPJ tem 12 algarismos (sem os dois d�gitos verificadores)
    PreProcess.requiredNonNull(cnpj);
    if (!cnpj.matches("[0-9]{12}")) {
      throw new RFWValidationException("RFW_000048");
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
   * Este m�todo calcula o d�gito verificador usado no CPF.<br>
   * Calcula por m�dulo de 11 o primeiro d�gito verificador, e depois calcula o segundo digito verificador tamb�m com m�dulo de 11, mas com a matriz multiplicadora deslocada, come�ando em 3.
   *
   * @param cpf 9 algarismos que comp�e o CPF.
   * @return
   * @throws RFWException
   */
  public static String calcDVCPF(String cpf) throws RFWException {
    // Verifica se o CNPJ tem 13 algarismos (sem os dois d�gitos verificadores)
    PreProcess.requiredNonNull(cpf);
    if (!cpf.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_000049");
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

  /**
   * Calcula o D�gito Verificador (DV) da Chave de Acesso da NF-e vers�o 4.00 utilizando o algoritmo do M�dulo 11, conforme especificado no Manual da NF-e.
   *
   * @param keyPrefix String contendo os 43 primeiros d�gitos da chave de acesso.
   * @return String contendo o d�gito verificador calculado.
   * @throws RFWException Se a entrada for nula, vazia ou n�o conter exatamente 43 d�gitos num�ricos.
   */
  public static String calcDVDANFeV400(String keyPrefix) throws RFWException {
    // Remover caracteres n�o num�ricos
    keyPrefix = RUString.removeNonDigits(keyPrefix);

    // Validar se a chave possui exatamente 43 d�gitos
    if (keyPrefix == null || keyPrefix.length() != 43 || !keyPrefix.matches("[0-9]+")) {
      throw new RFWValidationException("RFW_000047", new String[] { keyPrefix });
    }

    // Pesos definidos no manual da NF-e (sequ�ncia c�clica de 2 a 9)
    int[] weights = { 2, 3, 4, 5, 6, 7, 8, 9 };

    long sum = 0;
    int weightIndex = 0;

    // Percorrer os d�gitos da direita para a esquerda
    for (int i = 42; i >= 0; i--) {
      int digit = Character.getNumericValue(keyPrefix.charAt(i));
      sum += digit * weights[weightIndex];

      // Incrementar o �ndice do peso e reiniciar quando atingir o final do array
      weightIndex = (weightIndex + 1) % weights.length;
    }

    // Aplicar a regra do M�dulo 11
    long remainder = sum % 11;
    long checkDigit = 11 - remainder;

    // Se o resultado for 0 ou 1, o d�gito verificador deve ser 0
    if (checkDigit >= 10) {
      checkDigit = 0;
    }

    return String.valueOf(checkDigit);
  }

  /**
   * Este m�todo calcula um D�gito Verificador baseado no m�dulo de 11 com uma implementa��o gen�rica para ser utilizada pelo sistema.<Br>
   * Muitos documentos utilizam a valida��o com o m�dulo de 11 (que se refere a utliza��o do resto da divis�o por onze), no entando muitas diferem nas regras de defini��o do DV.<br>
   * N�O ALTERE O FUNCIONAMENTO DESTE M�TODO, pois ele j� � utilizado no sistema para diversos c�lculos de seguran�a. Para valida��o de DV de documentos espec�ficos, crie m�todos pr�prios.
   *
   * @param value valor contendo apenas d�gitos para que seja calculado o DV.
   * @return String contendo apenas 1 caracter que ser� o DV.
   * @throws RFWException Lan�ado caso o valor n�o tenha apenas n�meros, ou seja um valor nulo/vazio.
   */
  public static String calcDVGenericMod11(String value) throws RFWException {
    value = RUString.removeNonDigits(value);
    if (value == null || !value.matches("[0-9]+")) {
      throw new RFWValidationException("RFW_000050");
    }

    int[] base = { 2, 3, 4, 5, 6, 7, 8, 9 };
    String[] digits = value.split("|");

    long sum = 0;
    int basecount = 0;
    for (int i = value.length() - 1; i >= 0; i--) {
      sum += base[basecount++] * new Long(digits[i]);
      if (basecount > 7) basecount = 0;
    }
    long mod = 11 - (sum % 11);
    if (mod >= 10) mod = 0;
    return "" + mod;
  }

  /**
   * Calcula um d�gito verificador usando m�dulo de 11, com uma base de 2 � 9.<br>
   * Multiplicando cada n�mero do valor passado pelos n�meros da base (2, 3,..., 9) e somando, ao final obt�m o m�dulo da d�vis�o por 11, e subtrai de 11. Caso o resultado do c�lculo seja igual a 11 (resto 0) ou 10 (resto 1) o DV ser� 0.<br>
   * <br>
   * <b>Casos conhecidos que usam esta valida��o:</b>
   * <ul>
   * <li>Chave de Acesso da NFe</li>
   * <li>DV Geral do Boleto de Cobran�a</li>
   * </ul>
   *
   * @param value valor contendo apenas d�gitos para que seja calculado o DV.
   * @return String contendo apenas 1 caracter que ser� o DV.
   * @throws RFWValidationException Lan�ado caso o valor n�o tenha apenas n�meros, ou seja um valor nulo/vazio.
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
   * Calcula um d�gito verificador usando m�dulo de 11, com uma base de 2 � 9.<br>
   * Multiplicando cada n�mero do valor passado pelos n�meros da base (2, 3,..., 9) e somando, ao final obt�m o m�dulo da d�vis�o por 11, e subtrai de 11. Caso o resultado do c�lculo seja igual a 11 (resto 0) ou 10 (resto 1) o DV ser� 0.<br>
   * <br>
   * <b>Casos conhecidos que usam esta valida��o:</b>
   * <ul>
   * <li>Chave de Acesso da NFe</li>
   * <li>DV Geral do Boleto de Cobran�a</li>
   * </ul>
   *
   * @param value valor contendo apenas d�gitos para que seja calculado o DV.
   * @return String contendo apenas 1 caracter que ser� o DV.
   * @throws RFWValidationException Lan�ado caso o valor n�o tenha apenas n�meros, ou seja um valor nulo/vazio.
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
   * Calcula um d�gito verificador usando m�dulo de 11 APENAS PARA GUIAS DE ARRECA��O DO GOVERNO / BOLETOS DE SERVI�O, com uma base de 2 � 9.<br>
   * <br>
   * <b>Casos conhecidos que usam esta valida��o:</b>
   * <ul>
   * <li>Guia de FGTS</li>
   * <li>Guia de GPS</li>
   * </ul>
   *
   * @param value valor contendo apenas d�gitos para que seja calculado o DV.
   * @return String contendo apenas 1 caracter que ser� o DV.
   * @throws RFWValidationException Lan�ado caso o valor n�o tenha apenas n�meros, ou seja um valor nulo/vazio.
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
}
