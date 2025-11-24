package br.eng.rodrigogml.rfw.kernel.utils;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe de utilidades de manipulação e validação de documentos.<br>
 *
 * @author Rodrigo Leitão
 * @since 3.1.0 (NOV / 2009)
 * @version 4.1.0 (24/06/2011) - rodrigogml - Nome alterado de DocumentValidationUtils para ficar no padrão do Framework.
 * @deprecated O conteúdo desta classe deve migrar para {@link RUValueValidation}
 */
@Deprecated
public class RUDocValidation {

  /**
   * Classe estática, denecessário instancia-la.
   */
  private RUDocValidation() {
  }

  /**
   * Este método tenta validar a IE (Inscrição Estadual) em qualquer estado do país. Caso em qualquer um deles o valor seja válido este método aceitará o valor.<br>
   * As descrições de das validações dos estados podem ser encontradas no link: http://www.sintegra.gov.br/insc_est.html
   *
   * @param ie
   * @throws RFWValidationException Lançará a exceção caso o valor não seja um IR válido em nenhum estado.
   */
  public static void validateIE(String ie) throws RFWValidationException {
    try {
      validateIEonSP(ie);
    } catch (Exception e) {
      try {
        validateIEonMG(ie);
      } catch (Exception e2) {
        try {
          validateIEonRJ(ie);
        } catch (Exception e3) {
          try {
            validateIEonAP(ie);
          } catch (Exception e4) {
            try {
              validateIEonAM(ie);
            } catch (Exception e5) {
              try {
                validateIEonBA(ie);
              } catch (Exception e51) {
                try {
                  validateIEonCE(ie);
                } catch (Exception e6) {
                  try {
                    validateIEonDF(ie);
                  } catch (Exception e7) {
                    try {
                      validateIEonES(ie);
                    } catch (Exception e8) {
                      try {
                        validateIEonGO(ie);
                      } catch (Exception e81) {
                        try {
                          validateIEonMA(ie);
                        } catch (Exception e9) {
                          try {
                            validateIEonMT(ie);
                          } catch (Exception e10) {
                            try {
                              validateIEonMS(ie);
                            } catch (Exception e11) {
                              try {
                                validateIEonAC(ie);
                              } catch (Exception e12) {
                                try {
                                  validateIEonPA(ie);
                                } catch (Exception e13) {
                                  try {
                                    validateIEonPB(ie);
                                  } catch (Exception e14) {
                                    try {
                                      validateIEonPR(ie);
                                    } catch (Exception e15) {
                                      try {
                                        validateIEonPE(ie);
                                      } catch (Exception e16) {
                                        try {
                                          validateIEonPI(ie);
                                        } catch (Exception e17) {
                                          try {
                                            validateIEonAL(ie);
                                          } catch (Exception e18) {
                                            try {
                                              validateIEonRN(ie);
                                            } catch (Exception e19) {
                                              try {
                                                validateIEonRS(ie);
                                              } catch (Exception e20) {
                                                try {
                                                  validateIEonRO(ie);
                                                } catch (Exception e21) {
                                                  try {
                                                    validateIEonRR(ie);
                                                  } catch (Exception e22) {
                                                    try {
                                                      validateIEonSC(ie);
                                                    } catch (Exception e23) {
                                                      try {
                                                        validateIEonSE(ie);
                                                      } catch (Exception e24) {
                                                        try {
                                                          validateIEonTO(ie);
                                                        } catch (Exception e25) {
                                                          throw new RFWValidationException("RFW_ERR_200298", new String[] { ie });
                                                        }
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Acre.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonAC(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("[0-9]{13}")) {
      throw new RFWValidationException("RFW_ERR_210010");
    }

    // valida os dois primeiros digitos - devem ser iguais a 01
    for (int i = 0; i < 2; i++) {
      if (Integer.parseInt(String.valueOf(ie.charAt(i))) != i) {
        throw new RFWValidationException("RFW_ERR_210012");
      }
    }

    int soma = 0;
    int pesoInicial = 4;
    int pesoFinal = 9;
    int d1 = 0; // primeiro digito verificador
    int d2 = 0; // segundo digito verificador

    // calcula o primeiro digito
    for (int i = 0; i < ie.length() - 2; i++) {
      if (i < 3) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicial;
        pesoInicial--;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFinal;
        pesoFinal--;
      }
    }
    d1 = 11 - (soma % 11);
    if (d1 == 10 || d1 == 11) {
      d1 = 0;
    }

    // calcula o segundo digito
    soma = d1 * 2;
    pesoInicial = 5;
    pesoFinal = 9;
    for (int i = 0; i < ie.length() - 2; i++) {
      if (i < 4) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicial;
        pesoInicial--;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFinal;
        pesoFinal--;
      }
    }

    d2 = 11 - (soma % 11);
    if (d2 == 10 || d2 == 11) {
      d2 = 0;
    }

    // valida os digitos verificadores
    String dv = d1 + "" + d2;
    if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
      throw new RFWValidationException("RFW_ERR_210011");
    }

  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Alagoas.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonAL(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210013");
    }

    // valida os dois primeiros dígitos - deve ser iguais a 24
    if (!ie.substring(0, 2).equals("24")) {
      throw new RFWValidationException("RFW_ERR_210014");
    }

    // valida o terceiro dígito - deve ser 0,3,5,7,8
    int[] digits = { 0, 3, 5, 7, 8 };
    boolean check = false;
    for (int i = 0; i < digits.length; i++) {
      if (Integer.parseInt(String.valueOf(ie.charAt(2))) == digits[i]) {
        check = true;
        break;
      }
    }
    if (!check) {
      throw new RFWValidationException("RFW_ERR_210015");
    }

    // calcula o dígito verificador
    int soma = 0;
    int peso = 9;
    int d = 0; // dígito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }
    d = ((soma * 10) % 11);
    if (d == 10) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210015");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Amazonas.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonAM(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210019");
    }

    int soma = 0;
    int peso = 9;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    if (soma < 11) {
      d = 11 - soma;
    } else if ((soma % 11) <= 1) {
      d = 0;
    } else {
      d = 11 - (soma % 11);
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210020");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Amapá.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonAP(String ie) throws RFWValidationException {
    // valida quantida de dígito
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210016");
    }

    // verifica os dois primeiros dígito - deve ser igual 03
    if (!ie.substring(0, 2).equals("03")) {
      throw new RFWValidationException("RFW_ERR_210017");
    }

    // calcula o dígito verificador
    int d1 = -1;
    int soma = -1;
    int peso = 9;

    // configura o valor do digito verificador e da soma de acordo com faixa das inscrições
    long x = Long.parseLong(ie.substring(0, ie.length() - 1)); // x = inscrição estadual sem o dígito verificador
    if (x >= 3017001L && x <= 3019022L) {
      d1 = 1;
      soma = 9;
    } else if (x >= 3000001L && x <= 3017000L) {
      d1 = 0;
      soma = 5;
    } else if (x >= 3019023L) {
      d1 = 0;
      soma = 0;
    }

    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    int d = 11 - ((soma % 11)); // d = armazena o digito verificador após cálculo
    if (d == 10) {
      d = 0;
    } else if (d == 11) {
      d = d1;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210018");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Bahia.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonBA(String ie) throws RFWValidationException {
    // valida quantida de dígitos
    if (ie == null || !ie.matches("[0-9]{8}") && !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210021");
    }

    // Cálculo do módulo de acordo com o primeiro dígito da IE
    int modulo = 10;
    int firstDigit = Integer.parseInt(String.valueOf(ie.charAt(ie.length() == 8 ? 0 : 1)));
    if (firstDigit == 6 || firstDigit == 7 || firstDigit == 9) modulo = 11;

    // Cálculo do segundo dígito
    int d2 = -1; // segundo dígito verificador
    int soma = 0;
    int peso = ie.length() == 8 ? 7 : 8;
    for (int i = 0; i < ie.length() - 2; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    int resto = soma % modulo;

    if (resto == 0 || (modulo == 11 && resto == 1)) {
      d2 = 0;
    } else {
      d2 = modulo - resto;
    }

    // Calculo do primeiro digito
    int d1 = -1; // primeiro digito verificador
    soma = d2 * 2;
    peso = ie.length() == 8 ? 8 : 9;
    for (int i = 0; i < ie.length() - 2; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    resto = soma % modulo;

    if (resto == 0 || (modulo == 11 && resto == 1)) {
      d1 = 0;
    } else {
      d1 = modulo - resto;
    }

    // valida os digitos verificadores
    String dv = d1 + "" + d2;
    if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
      throw new RFWValidationException("RFW_ERR_210022");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Ceará.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonCE(String ie) throws RFWValidationException {
    // valida quantida de dígitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210023");
    }

    // Cálculo do dígito verificador
    int soma = 0;
    int peso = 9;
    int d = -1; // dígito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    d = 11 - (soma % 11);
    if (d == 10 || d == 11) {
      d = 0;
    }
    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210024");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Distrito Federal.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonDF(String ie) throws RFWValidationException {
    // valida quantida de dígitos
    if (ie == null || !ie.matches("[0-9]{13}")) {
      throw new RFWValidationException("RFW_ERR_210025");
    }

    // Cálculo do primeiro dígito verificador
    int soma = 0;
    int pesoInicio = 4;
    int pesoFim = 9;
    int d1 = -1; // primeiro dígito verificador
    for (int i = 0; i < ie.length() - 2; i++) {
      if (i < 3) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
        pesoInicio--;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
        pesoFim--;
      }
    }

    d1 = 11 - (soma % 11);
    if (d1 == 11 || d1 == 10) {
      d1 = 0;
    }

    // Cálculo do segundo dígito verificador
    soma = d1 * 2;
    pesoInicio = 5;
    pesoFim = 9;
    int d2 = -1; // segundo dígito verificador
    for (int i = 0; i < ie.length() - 2; i++) {
      if (i < 4) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
        pesoInicio--;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
        pesoFim--;
      }
    }

    d2 = 11 - (soma % 11);
    if (d2 == 11 || d2 == 10) {
      d2 = 0;
    }

    // valida os digitos verificadores
    String dv = d1 + "" + d2;
    if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
      throw new RFWValidationException("RFW_ERR_210026");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Espírito Santo.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonES(String ie) throws RFWValidationException {
    // valida quantida de dígitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210027");
    }

    // Cálculo do dígito verificador
    int soma = 0;
    int peso = 9;
    int d = -1; // dígito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    int resto = soma % 11;
    if (resto < 2) {
      d = 0;
    } else if (resto > 1) {
      d = 11 - resto;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210028");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Goiás.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonGO(String ie) throws RFWValidationException {
    // valida quantida de dígitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210029");
    }

    // valida os dois primeiros dígitos
    if (!"10".equals(ie.substring(0, 2))) {
      if (!"11".equals(ie.substring(0, 2))) {
        if (!"15".equals(ie.substring(0, 2))) {
          throw new RFWValidationException("RFW_ERR_210030");
        }
      }
    }

    // Quando a inscrição for 11094402 o dígito verificador pode ser zero (0) e pode ser um (1);
    if (ie.substring(0, ie.length() - 1).equals("11094402")) {
      if (!ie.substring(ie.length() - 1, ie.length()).equals("0")) {
        if (!ie.substring(ie.length() - 1, ie.length()).equals("1")) {
          throw new RFWValidationException("RFW_ERR_210031");
        }
      }
    } else {

      // Cálculo do dígito verificador
      int soma = 0;
      int peso = 9;
      int d = -1; // dígito verificador
      for (int i = 0; i < ie.length() - 1; i++) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
        peso--;
      }

      int resto = soma % 11;
      long faixaInicio = 10103105;
      long faixaFim = 10119997;
      long insc = Long.parseLong(ie.substring(0, ie.length() - 1));
      if (resto == 0) {
        d = 0;
      } else if (resto == 1) {
        if (insc >= faixaInicio && insc <= faixaFim) {
          d = 1;
        } else {
          d = 0;
        }
      } else if (resto != 0 && resto != 1) {
        d = 11 - resto;
      }

      // valida o digito verificador
      String dv = d + "";
      if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
        throw new RFWValidationException("RFW_ERR_210031");
      }
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Maranhão.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonMA(String ie) throws RFWValidationException {
    // valida quantida de dígitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210032");
    }

    // valida os dois primeiros digitos
    if (!ie.substring(0, 2).equals("12")) {
      throw new RFWValidationException("RFW_ERR_210033");
    }

    // Cálculo do dígito verificador
    int soma = 0;
    int peso = 9;
    int d = -1; // dígito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    d = 11 - (soma % 11);
    if ((soma % 11) == 0 || (soma % 11) == 1) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210034");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Minas Gerais.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonMG(String ie) throws RFWValidationException {
    /*
     * FORMATO GERAL: A1A2A3B1B2B3B4B5B6C1C2D1D2 Onde: A= Código do Município B= Número da inscrição C= Número de ordem do estabelecimento D= Dígitos de controle
     */

    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{13}")) {
      throw new RFWValidationException("RFW_ERR_210040");
    }

    // iguala a casas para o calculo
    // em inserir o algarismo zero "0" imediatamente após o número de código do município,
    // desprezando-se os dígitos de controle.
    String str = "";
    for (int i = 0; i < ie.length() - 2; i++) {
      if (Character.isDigit(ie.charAt(i))) {
        if (i == 3) {
          str += "0";
          str += ie.charAt(i);
        } else {
          str += ie.charAt(i);
        }
      }
    }

    // Calculo do primeiro digito verificador
    int soma = 0;
    int pesoInicio = 1;
    int pesoFim = 2;
    int d1 = -1; // primeiro dígito verificador
    for (int i = 0; i < str.length(); i++) {
      if (i % 2 == 0) {
        int x = Integer.parseInt(String.valueOf(str.charAt(i))) * pesoInicio;
        String strX = Integer.toString(x);
        for (int j = 0; j < strX.length(); j++) {
          soma += Integer.parseInt(String.valueOf(strX.charAt(j)));
        }
      } else {
        int y = Integer.parseInt(String.valueOf(str.charAt(i))) * pesoFim;
        String strY = Integer.toString(y);
        for (int j = 0; j < strY.length(); j++) {
          soma += Integer.parseInt(String.valueOf(strY.charAt(j)));
        }
      }
    }

    int dezenaExata = soma;
    while (dezenaExata % 10 != 0) {
      dezenaExata++;
    }
    d1 = dezenaExata - soma; // resultado - primeiro digito verificador

    // Calculo do segundo digito verificador
    soma = d1 * 2;
    pesoInicio = 3;
    pesoFim = 11;
    int d2 = -1;
    for (int i = 0; i < ie.length() - 2; i++) {
      if (i < 2) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
        pesoInicio--;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
        pesoFim--;
      }
    }

    d2 = 11 - (soma % 11); // resultado - segundo digito verificador
    if ((soma % 11 == 0) || (soma % 11 == 1)) {
      d2 = 0;
    }

    // valida os digitos verificadores
    String dv = d1 + "" + d2;
    if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
      throw new RFWValidationException("RFW_ERR_210041");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Matro Grosso do Sul.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonMS(String ie) throws RFWValidationException {
    // valida quantida de dígitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210037");
    }

    // valida os dois primeiros digitos
    if (!ie.substring(0, 2).equals("28")) {
      throw new RFWValidationException("RFW_ERR_210038");
    }

    // Calcula o digito verificador
    int soma = 0;
    int peso = 9;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    int resto = soma % 11;
    int result = 11 - resto;
    if (resto == 0) {
      d = 0;
    } else if (resto > 0) {
      if (result > 9) {
        d = 0;
      } else if (result < 10) {
        d = result;
      }
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210039");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Mato Grosso.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonMT(String ie) throws RFWValidationException {
    // valida quantida de dígitos
    if (ie == null || !ie.matches("[0-9]{11}")) {
      throw new RFWValidationException("RFW_ERR_210035");
    }

    // Calcula o digito verificador
    int soma = 0;
    int pesoInicial = 3;
    int pesoFinal = 9;
    int d = -1;

    for (int i = 0; i < ie.length() - 1; i++) {
      if (i < 2) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicial;
        pesoInicial--;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFinal;
        pesoFinal--;
      }
    }

    d = 11 - (soma % 11);
    if ((soma % 11) == 0 || (soma % 11) == 1) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210036");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Pará.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonPA(String ie) throws RFWValidationException {
    // valida quantidade de dígitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210042");
    }

    // valida os dois primeiros digitos
    if (!ie.substring(0, 2).equals("15")) {
      throw new RFWValidationException("RFW_ERR_210043");
    }

    // Calcula o digito verificador
    int soma = 0;
    int peso = 9;
    int d = -1; // dígito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    d = 11 - (soma % 11);
    if ((soma % 11) == 0 || (soma % 11) == 1) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210044");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Paraíba.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonPB(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210045");
    }

    // Calcula o digito verificador
    int soma = 0;
    int peso = 9;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    d = 11 - (soma % 11);
    if (d == 10 || d == 11) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210046");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Pernambuco.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonPE(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{14}") && !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210049");
    } else if (ie.matches("[0-9]{9}")) {
      // Se é com 9 dígitos, então faz a validação abaixo copiada do site da Sintegra
      long[] numero = new long[9];

      for (int i = 0; i < 7; i++) {
        numero[i] = (ie.charAt(i) - 48);
      }
      // *** O primeiro digito verificador do Numero de Inscricao Estadual ******
      long soma1 = 0;

      for (int i = 0; i < 7; i++) {
        soma1 += numero[i] * (8 - i);
      }
      long resto1 = soma1 % 11;
      if (resto1 == 0 || resto1 == 1) {
        numero[7] = 0;
      } else {
        numero[7] = 11 - resto1;
      }
      long soma2 = (numero[7] * 2);
      for (int i = 0; i < 7; i++) {
        soma2 += numero[i] * (9 - i);
      }

      long resto2 = soma2 % 11;

      if (resto2 == 0 || resto2 == 1) {
        numero[8] = 0;
      } else {
        numero[8] = 11 - resto2;
      }
      String dv = "" + numero[7] + numero[8];
      if (!ie.substring(ie.length() - 2, ie.length()).equals(dv)) {
        throw new RFWValidationException("RFW_ERR_210050");
      }

    } else {
      // Senão, é a de 14 dígitos

      // Cálculo do dígito verificador
      int soma = 0;
      int pesoInicio = 5;
      int pesoFim = 9;
      int d = -1; // dígito verificador

      for (int i = 0; i < ie.length() - 1; i++) {
        if (i < 5) {
          soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
          pesoInicio--;
        } else {
          soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
          pesoFim--;
        }
      }

      d = 11 - (soma % 11);
      if (d > 9) {
        d -= 10;
      }

      // valida o digito verificador
      String dv = d + "";
      if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
        throw new RFWValidationException("RFW_ERR_210050");
      }
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Piauí.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonPI(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210051");
    }

    // Calculo do digito verficador
    int soma = 0;
    int peso = 9;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    d = 11 - (soma % 11);
    if (d == 11 || d == 10) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210052");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Paraná.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonPR(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{10}")) {
      throw new RFWValidationException("RFW_ERR_210047");
    }

    // cálculo do primeiro digito
    int soma = 0;
    int pesoInicio = 3;
    int pesoFim = 7;
    int d1 = -1; // digito verificador
    for (int i = 0; i < ie.length() - 2; i++) {
      if (i < 2) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
        pesoInicio--;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
        pesoFim--;
      }
    }

    d1 = 11 - (soma % 11);
    if ((soma % 11) == 0 || (soma % 11) == 1) {
      d1 = 0;
    }

    // cálculo do segundo digito
    soma = d1 * 2;
    pesoInicio = 4;
    pesoFim = 7;
    int d2 = -1; // segundo digito
    for (int i = 0; i < ie.length() - 2; i++) {
      if (i < 3) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
        pesoInicio--;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
        pesoFim--;
      }
    }

    d2 = 11 - (soma % 11);
    if ((soma % 11) == 0 || (soma % 11) == 1) {
      d2 = 0;
    }

    // valida os digitos verificadores
    String dv = d1 + "" + d2;
    if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
      throw new RFWValidationException("RFW_ERR_210048");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Rio de Janeiro.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonRJ(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{8}")) {
      throw new RFWValidationException("RFW_ERR_210053");
    }

    // Calculo do digito verficador
    int soma = 0;
    int peso = 7;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      if (i == 0) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * 2;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
        peso--;
      }
    }

    d = 11 - (soma % 11);
    if ((soma % 11) <= 1) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210054");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Rio Grande do Norte.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonRN(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{10}") && !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210055");
    }

    // valida os dois primeiros digitos
    if (!ie.substring(0, 2).equals("20")) {
      throw new RFWValidationException("RFW_ERR_210056");
    }

    // calcula o digito para inscrição de 9 d?gitos
    if (ie.length() == 9) {
      int soma = 0;
      int peso = 9;
      int d = -1; // digito verificador
      for (int i = 0; i < ie.length() - 1; i++) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
        peso--;
      }

      d = ((soma * 10) % 11);
      if (d == 10) {
        d = 0;
      }

      // valida o digito verificador
      String dv = d + "";
      if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
        throw new RFWValidationException("RFW_ERR_210057");
      }
    } else {
      int soma = 0;
      int peso = 10;
      int d = -1; // digito verificador
      for (int i = 0; i < ie.length() - 1; i++) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
        peso--;
      }
      d = ((soma * 10) % 11);
      if (d == 10) {
        d = 0;
      }

      // valida o digito verificador
      String dv = d + "";
      if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
        throw new RFWValidationException("RFW_ERR_210057");
      }
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Rondônia.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonRO(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{14}")) {
      throw new RFWValidationException("RFW_ERR_210060");
    }

    // Calculo do digito verificador
    int soma = 0;
    int pesoInicio = 6;
    int pesoFim = 9;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      if (i < 5) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
        pesoInicio--;
      } else {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
        pesoFim--;
      }
    }

    d = 11 - (soma % 11);
    if (d == 11 || d == 10) {
      d -= 10;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210061");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Roraima.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonRR(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210062");
    }

    // valida os dois primeiros digitos
    if (!ie.substring(0, 2).equals("24")) {
      throw new RFWValidationException("RFW_ERR_210063");
    }

    int soma = 0;
    int peso = 1;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso++;
    }

    d = soma % 9;

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210064");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Rio Grande do Sul.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonRS(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{10}")) {
      throw new RFWValidationException("RFW_ERR_210058");
    }

    // Calculo do difito verificador
    int soma = Integer.parseInt(String.valueOf(ie.charAt(0))) * 2;
    int peso = 9;
    int d = -1; // digito verificador
    for (int i = 1; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    d = 11 - (soma % 11);
    if (d == 10 || d == 11) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210059");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Santa Catarina.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonSC(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210065");
    }

    // Calculo do difito verificador
    int soma = 0;
    int peso = 9;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    d = 11 - (soma % 11);
    if ((soma % 11) == 0 || (soma % 11) == 1) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210066");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Sergipe.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonSE(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{9}")) {
      throw new RFWValidationException("RFW_ERR_210067");
    }

    // calculo do digito verificador
    int soma = 0;
    int peso = 9;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
      peso--;
    }

    d = 11 - (soma % 11);
    if (d == 11 || d == 11 || d == 10) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210068");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do São Paulo.<br>
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonSP(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("[0-9]{12}")) {
      throw new RFWValidationException("RFW_ERR_200299");
    }

    int[] base = new int[] { 1, 3, 4, 5, 6, 7, 8, 10 }; // Array com os pesos

    // Divide os dívigitos do IE recebido
    String[] digits = ie.split("|");

    // Calculamos o Primeiro DV
    long sum = 0; // Soma acumulada do primeiro DV
    for (int i = 0; i < base.length; i++) {
      sum += base[i] * new Long(digits[i]);
    }
    // Obtem o módulo de 11 do resultado
    String mod = "" + (sum % 11);
    // o primeiro DV é o dígito mais a direita do resultado
    char dv = mod.charAt(mod.length() - 1);

    // Validamos o primeiro DV antes de testar o segundo, afinal, se já falou no primeiro o IE não é válido, pra que perder clocks calculando o segundo.
    if (ie.charAt(8) != dv) throw new RFWValidationException("RFW_ERR_200300");

    // Tudo OK, procedemos para o cálculo do 2°DV - Praticamente a mesma lógica do anterior, só mudamos os pesos
    base = new int[] { 3, 2, 10, 9, 8, 7, 6, 5, 4, 3, 2 }; // Nova base de pesos
    sum = 0; // Soma acumulada do primeiro DV
    for (int i = 0; i < base.length; i++) {
      sum += base[i] * new Long(digits[i]);
    }
    // Obtem o módulo de 11 do resultado
    mod = "" + (sum % 11);
    // o primeiro DV é o dígito mais a direita do resultado
    dv = mod.charAt(mod.length() - 1);

    // Validamos o primeiro DV antes de testar o segundo, afinal, se já falou no primeiro o IE não é válido, pra que perder clocks calculando o segundo.
    if (ie.charAt(11) != dv) throw new RFWValidationException("RFW_ERR_200300");
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Tocantins.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonTO(String ie) throws RFWValidationException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{9}") && !ie.matches("[0-9]{11}")) {
      throw new RFWValidationException("RFW_ERR_210069");
    } else if (ie.length() == 9) {
      ie = ie.substring(0, 2) + "02" + ie.substring(2);
    }

    int soma = 0;
    int peso = 9;
    int d = -1; // digito verificador
    for (int i = 0; i < ie.length() - 1; i++) {
      if (i != 2 && i != 3) {
        soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
        peso--;
      }
    }
    d = 11 - (soma % 11);
    if ((soma % 11) < 2) {
      d = 0;
    }

    // valida o digito verificador
    String dv = d + "";
    if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
      throw new RFWValidationException("RFW_ERR_210070");
    }
  }

  /**
   * Valida a Instrição estadual de acordo com o estado passado.<br>
   * Este método nada mais é do que um delegate para o método correto de valida da IE de acordo com a UF passada no parametro acronym.
   *
   * @param ie IE a ser validada.
   * @param acronym UF (2 letras) do estado que validará a IE. Ex: 'SP', 'RJ', 'MG', etc.
   * @throws RFWValidationException
   */
  public static void validateIE(String ie, String acronym) throws RFWValidationException {
    if (ie == null || acronym == null) {
      throw new RFWValidationException("RFW_ERR_200304");
    }
    // Prepara a UF para comparação
    acronym = acronym.toUpperCase();
    switch (acronym) {
      case "SP":
        validateIEonSP(ie);
        break;
      case "AC":
        validateIEonAC(ie);
        break;
      case "AL":
        validateIEonAL(ie);
        break;
      case "AP":
        validateIEonAP(ie);
        break;
      case "AM":
        validateIEonAM(ie);
        break;
      case "BA":
        validateIEonBA(ie);
        break;
      case "CE":
        validateIEonCE(ie);
        break;
      case "DF":
        validateIEonDF(ie);
        break;
      case "ES":
        validateIEonES(ie);
        break;
      case "GO":
        validateIEonGO(ie);
        break;
      case "MA":
        validateIEonMA(ie);
        break;
      case "MT":
        validateIEonMT(ie);
        break;
      case "MS":
        validateIEonMS(ie);
        break;
      case "MG":
        validateIEonMG(ie);
        break;
      case "PA":
        validateIEonPA(ie);
        break;
      case "PB":
        validateIEonPB(ie);
        break;
      case "PR":
        validateIEonPR(ie);
        break;
      case "PE":
        validateIEonPE(ie);
        break;
      case "PI":
        validateIEonPI(ie);
        break;
      case "RJ":
        validateIEonRJ(ie);
        break;
      case "RN":
        validateIEonRN(ie);
        break;
      case "RS":
        validateIEonRS(ie);
        break;
      case "RO":
        validateIEonRO(ie);
        break;
      case "RR":
        validateIEonRR(ie);
        break;
      case "SC":
        validateIEonSC(ie);
        break;
      case "SE":
        validateIEonSE(ie);
        break;
      case "TO":
        validateIEonTO(ie);
        break;
      default:
        throw new RFWValidationException("RFW_ERR_200305");
    }

  }

  /**
   * Valida se pe uma UF válida para o Brasil.<br>
   * Este método simplesmente verifica se a string recebida é uma das UF do Brasil, considerando apenas 2 letras e ignorando o case.
   *
   * @param uf UFa ser validada.
   * @throws RFWValidationException caso seja inválida.
   */
  public static void validateUF(String uf) throws RFWException {
    if (uf == null) {
      throw new RFWValidationException("RFW_000003", new String[] { "" });
    }
    // Prepara a UF para comparação
    uf = uf.toUpperCase();
    switch (uf) {
      case "SP":
      case "AC":
      case "AL":
      case "AP":
      case "AM":
      case "BA":
      case "CE":
      case "DF":
      case "ES":
      case "GO":
      case "MA":
      case "MT":
      case "MS":
      case "MG":
      case "PA":
      case "PB":
      case "PR":
      case "PE":
      case "PI":
      case "RJ":
      case "RN":
      case "RS":
      case "RO":
      case "RR":
      case "SC":
      case "SE":
      case "TO":
        break;
      default:
        throw new RFWValidationException("RFW_000003", new String[] { "" });
    }

  }
}
