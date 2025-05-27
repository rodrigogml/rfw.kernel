package br.eng.rodrigogml.rfw.kernel.utils;

import java.util.Arrays;
import java.util.List;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe com métodos utilitários de validação de documentos e cálculos de Digitos Verificadores (DVs).<br>
 * Os métodos dessa classe são organizados de acordo com seu prefixo da seguinte forma: <br>
 * <li><b>validate</b> - faz a validação de um número de documento. Deve receber o número do documento completo, incluindo o DV, e lançar exception caso não seja um documento válido. Normalmente este método é 'void'.
 * <li><b>isValid</b> - valida o conteúdo mas retorna apenas um true/false impedindo qualquer exception de sair. Em geral encapsula o mesmo método com o prefixo validate e trata a exception.
 * <li><b>calcDV</b> - Faz o cálculo do Dígito verificador de acordo com os documento passado. Veja a documentação de cada método para saber como passar os valores com ou sem o DV.
 *
 * @author Rodrigo Leitão
 * @since (21 de fev. de 2025)
 */
public class RUDocVal {

  /**
   * Valida um número de CNPJ (Cadastro Nacional de Pessoa Jurídica).
   *
   * @param cnpj Número do CNPJ contendo apenas dígitos (sem pontos, traços, etc.), incluindo o dígito verificador.
   * @throws RFWException
   */
  public static void validateCNPJ(String cnpj) throws RFWException {
    if (cnpj == null) throw new RFWValidationException("RFW_ERR_200011", new String[] { cnpj });
    if (!cnpj.matches("\\d{14}")) throw new RFWValidationException("RFW_ERR_200012", new String[] { cnpj });
    if (Integer.parseInt(cnpj.substring(8, 12)) == 0) throw new RFWValidationException("RFW_ERR_200013", new String[] { cnpj });
    if (Integer.parseInt(cnpj.substring(0, 8)) == 0) throw new RFWValidationException("RFW_ERR_200014", new String[] { cnpj });
    if (!cnpj.substring(12).equals(calcDVCNPJ(cnpj.substring(0, 12)))) throw new RFWValidationException("RFW_ERR_200016", new String[] { cnpj });
  }

  /**
   * Calcula os dígitos verificadores do CNPJ.
   * <p>
   * O cálculo é feito pelo módulo 11, primeiro para o primeiro dígito verificador e depois para o segundo, deslocando a matriz multiplicadora.
   * </p>
   *
   * @param cnpj Sequência de 12 dígitos numéricos do CNPJ, sem os dois dígitos verificadores.
   * @return String contendo os dois dígitos verificadores calculados.
   * @throws RFWException
   */
  public static String calcDVCNPJ(String cnpj) throws RFWException {
    PreProcess.requiredNonNull(cnpj);
    if (!cnpj.matches("\\d{12}")) throw new RFWValidationException("RFW_000048");

    int[] weights1 = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
    int[] weights2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3 };

    int sum1 = 0, sum2 = 0;
    for (int i = 0; i < 12; i++) {
      int num = cnpj.charAt(i) - '0';
      sum1 += num * weights1[i];
      sum2 += num * weights2[i];
    }

    int dv1 = (sum1 % 11 < 2) ? 0 : (11 - sum1 % 11);
    sum2 += dv1 * 2;
    int dv2 = (sum2 % 11 < 2) ? 0 : (11 - sum2 % 11);

    return "" + dv1 + dv2;
  }

  /**
   * Valida um número de CPF (Cadastro de Pessoa Física).
   *
   * @param cpf Número do CPF contendo apenas dígitos (sem pontos, traços, etc.), incluindo o dígito verificador.
   * @throws RFWException
   */
  public static void validateCPF(String cpf) throws RFWException {
    if (cpf == null) throw new RFWValidationException("RFW_ERR_200017", new String[] { cpf });
    if (!cpf.matches("\\d{11}")) throw new RFWValidationException("RFW_ERR_200018", new String[] { cpf });
    if (Integer.parseInt(cpf.substring(0, 9)) == 0) throw new RFWValidationException("RFW_ERR_200019", new String[] { cpf });
    if (!cpf.substring(9).equals(calcDVCPF(cpf.substring(0, 9)))) throw new RFWValidationException("RFW_ERR_200021", new String[] { cpf });
  }

  /**
   * Calcula o dígito verificador usado no CPF.
   * <p>
   * O cálculo é feito pelo módulo 11, primeiro para o primeiro dígito verificador e depois para o segundo, deslocando a matriz multiplicadora.
   * </p>
   *
   * @param cpf 9 algarismos que compõem o CPF.
   * @return Os dois dígitos verificadores concatenados.
   * @throws RFWException
   */
  public static String calcDVCPF(String cpf) throws RFWException {
    PreProcess.requiredNonNull(cpf);
    if (!cpf.matches("\\d{9}")) throw new RFWValidationException("RFW_000049");

    int[] weights1 = { 10, 9, 8, 7, 6, 5, 4, 3, 2 };
    int[] weights2 = { 11, 10, 9, 8, 7, 6, 5, 4, 3 };

    int sum1 = 0, sum2 = 0;
    for (int i = 0; i < 9; i++) {
      int num = cpf.charAt(i) - '0';
      sum1 += num * weights1[i];
      sum2 += num * weights2[i];
    }

    int dv1 = (sum1 % 11 < 2) ? 0 : (11 - sum1 % 11);
    sum2 += dv1 * 2;
    int dv2 = (sum2 % 11 < 2) ? 0 : (11 - sum2 % 11);

    return "" + dv1 + dv2;
  }

  /**
   * Valida um número de CPF ou CNPJ, garantindo que tenha um formato válido e que os dígitos verificadores sejam corretos.
   *
   * @param cpfOrCnpj Número do CPF (11 dígitos) ou CNPJ (14 dígitos), contendo apenas números, sem pontos ou traços.
   * @throws RFWException Se o CPF ou CNPJ for inválido.
   */
  public static void validateCPFOrCNPJ(String cpfOrCnpj) throws RFWException {
    if (cpfOrCnpj == null) throw new RFWValidationException("RFW_ERR_200018", new String[] { cpfOrCnpj });
    if (!cpfOrCnpj.matches("\\d{11}") && !cpfOrCnpj.matches("\\d{14}")) throw new RFWValidationException("RFW_ERR_200419", new String[] { cpfOrCnpj });
    if (Integer.parseInt(cpfOrCnpj.substring(0, 9)) == 0) throw new RFWValidationException("RFW_ERR_200420", new String[] { cpfOrCnpj });

    if (cpfOrCnpj.length() == 11) {
      if (!cpfOrCnpj.substring(9).equals(calcDVCPF(cpfOrCnpj.substring(0, 9)))) throw new RFWValidationException("RFW_ERR_200021", new String[] { cpfOrCnpj });
    } else {
      if (!cpfOrCnpj.substring(12).equals(calcDVCNPJ(cpfOrCnpj.substring(0, 12)))) throw new RFWValidationException("RFW_ERR_200021", new String[] { cpfOrCnpj });
    }
  }

  /**
   * Valida se a string recebida é uma UF válida do Brasil.
   * <p>
   * O método verifica se a UF contém exatamente 2 letras, ignorando case, e se está na lista de UFs válidas.
   * </p>
   *
   * @param uf UF a ser validada.
   * @throws RFWValidationException Se a UF for inválida.
   */
  public static void validateUF(String uf) throws RFWException {
    if (uf == null || uf.length() != 2) throw new RFWValidationException("BISERP_000417");

    // Lista otimizada de UFs válidas para Java 1.8
    final List<String> validUFs = Arrays.asList(
        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
        "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SE", "SP", "TO");

    if (!validUFs.contains(uf.toUpperCase())) throw new RFWValidationException("BISERP_000417");
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Tocantins.
   *
   * @param ie
   * @throws RFWValidationException
   */
  public static void validateIEonTO(String ie) throws RFWException {
    // valida quantida de digitos
    if (ie == null || !ie.matches("[0-9]{9}") && !ie.matches("[0-9]{11}")) {
      throw new RFWValidationException("BISERP_100069");
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
      throw new RFWValidationException("BISERP_100070");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado de Sergipe.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonSE(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100067");
    }

    int soma = 0;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) { // Apenas os 8 primeiros dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int dvCalculado = 11 - (soma % 11);
    if (dvCalculado >= 10) {
      dvCalculado = 0;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100068");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado de São Paulo.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonSP(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{12}")) {
      throw new RFWValidationException("BISERP_000299");
    }

    // Pesos para o primeiro e segundo dígito verificador
    int[] weights1 = { 1, 3, 4, 5, 6, 7, 8, 10 };
    int[] weights2 = { 3, 2, 10, 9, 8, 7, 6, 5, 4, 3, 2 };

    // Cálculo do primeiro dígito verificador
    int sum = 0;
    for (int i = 0; i < weights1.length; i++) {
      sum += Character.getNumericValue(ie.charAt(i)) * weights1[i];
    }
    char dv1 = Character.forDigit(sum % 11 % 10, 10);

    // Validação do primeiro dígito verificador
    if (ie.charAt(8) != dv1) throw new RFWValidationException("BISERP_000300");

    // Cálculo do segundo dígito verificador
    sum = 0;
    for (int i = 0; i < weights2.length; i++) {
      sum += Character.getNumericValue(ie.charAt(i)) * weights2[i];
    }
    char dv2 = Character.forDigit(sum % 11 % 10, 10);

    // Validação do segundo dígito verificador
    if (ie.charAt(11) != dv2) throw new RFWValidationException("BISERP_000300");
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado de Santa Catarina.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonSC(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100065");
    }

    int soma = 0;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) { // Apenas os 8 primeiros dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int dvCalculado = 11 - (soma % 11);
    if (dvCalculado == 10 || dvCalculado == 11) {
      dvCalculado = 0;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100066");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado de Roraima.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonRR(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100062");
    }

    // Validação dos dois primeiros dígitos
    if (!ie.startsWith("24")) {
      throw new RFWValidationException("BISERP_100063");
    }

    int soma = 0;
    int peso = 1;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) { // Apenas os 8 primeiros dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso++;
    }

    // Cálculo do dígito verificador
    int dvCalculado = soma % 9;

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100064");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado de Rondônia.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonRO(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{14}")) {
      throw new RFWValidationException("BISERP_100060");
    }

    int soma = 0;
    int pesoInicio = 6;
    int pesoFim = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 13; i++) { // Apenas os 13 primeiros dígitos
      int num = Character.getNumericValue(ie.charAt(i));
      if (i < 5) {
        soma += num * pesoInicio--;
      } else {
        soma += num * pesoFim--;
      }
    }

    // Cálculo do dígito verificador
    int dvCalculado = 11 - (soma % 11);
    if (dvCalculado >= 10) {
      dvCalculado -= 10;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(13)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100061");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Rio Grande do Sul.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonRS(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{10}")) {
      throw new RFWValidationException("BISERP_100058");
    }

    int soma = Character.getNumericValue(ie.charAt(0)) * 2;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 1; i < 9; i++) { // Apenas os primeiros 9 dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int dvCalculado = 11 - (soma % 11);
    if (dvCalculado >= 10) {
      dvCalculado = 0;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(9)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100059");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Rio Grande do Norte.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonRN(String ie) throws RFWException {
    if (ie == null || !(ie.matches("\\d{9}") || ie.matches("\\d{10}"))) {
      throw new RFWValidationException("BISERP_100055");
    }

    // Validação dos dois primeiros dígitos
    if (!ie.startsWith("20")) {
      throw new RFWValidationException("BISERP_100056");
    }

    int soma = 0;
    int peso = ie.length() == 9 ? 9 : 10; // Define o peso inicial com base no tamanho

    // Cálculo da soma ponderada
    for (int i = 0; i < ie.length() - 1; i++) {
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int dvCalculado = (soma * 10) % 11;
    if (dvCalculado == 10) {
      dvCalculado = 0;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(ie.length() - 1)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100057");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Rio de Janeiro.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonRJ(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{8}")) {
      throw new RFWValidationException("BISERP_100053");
    }

    int soma = Character.getNumericValue(ie.charAt(0)) * 2;
    int peso = 7;

    // Cálculo da soma ponderada
    for (int i = 1; i < 7; i++) { // Apenas os primeiros 7 dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int dvCalculado = 11 - (soma % 11);
    if (dvCalculado <= 1) {
      dvCalculado = 0;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(7)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100054");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Piauí.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonPI(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100051");
    }

    int soma = 0;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) { // Apenas os primeiros 8 dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int dvCalculado = 11 - (soma % 11);
    if (dvCalculado >= 10) {
      dvCalculado = 0;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100052");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado de Pernambuco.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonPE(String ie) throws RFWException {
    if (ie == null || !(ie.matches("\\d{9}") || ie.matches("\\d{14}"))) {
      throw new RFWValidationException("BISERP_100049");
    }

    if (ie.length() == 9) {
      // Validação para IE de 9 dígitos (modelo antigo)
      int[] numero = new int[9];

      for (int i = 0; i < 7; i++) {
        numero[i] = Character.getNumericValue(ie.charAt(i));
      }

      // Cálculo do primeiro dígito verificador
      int soma1 = 0;
      for (int i = 0; i < 7; i++) {
        soma1 += numero[i] * (8 - i);
      }
      int resto1 = soma1 % 11;
      numero[7] = (resto1 == 0 || resto1 == 1) ? 0 : 11 - resto1;

      // Cálculo do segundo dígito verificador
      int soma2 = (numero[7] * 2);
      for (int i = 0; i < 7; i++) {
        soma2 += numero[i] * (9 - i);
      }
      int resto2 = soma2 % 11;
      numero[8] = (resto2 == 0 || resto2 == 1) ? 0 : 11 - resto2;

      // Validação dos dígitos verificadores
      String dvCalculado = "" + numero[7] + numero[8];
      if (!ie.substring(7).equals(dvCalculado)) {
        throw new RFWValidationException("BISERP_100050");
      }
    } else {
      // Validação para IE de 14 dígitos (modelo novo)
      int soma = 0;
      int pesoInicio = 5;
      int pesoFim = 9;

      // Cálculo da soma ponderada
      for (int i = 0; i < 13; i++) {
        int num = Character.getNumericValue(ie.charAt(i));
        soma += (i < 5) ? num * pesoInicio-- : num * pesoFim--;
      }

      // Cálculo do dígito verificador
      int dvCalculado = 11 - (soma % 11);
      if (dvCalculado > 9) {
        dvCalculado -= 10;
      }

      // Validação do dígito verificador
      if (Character.getNumericValue(ie.charAt(13)) != dvCalculado) {
        throw new RFWValidationException("BISERP_100050");
      }
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Paraná.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonPR(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{10}")) {
      throw new RFWValidationException("BISERP_100047");
    }

    // Cálculo do primeiro dígito verificador
    int soma = 0;
    int pesoInicio = 3;
    int pesoFim = 7;

    for (int i = 0; i < 8; i++) {
      int num = Character.getNumericValue(ie.charAt(i));
      soma += (i < 2) ? num * pesoInicio-- : num * pesoFim--;
    }

    int d1 = 11 - (soma % 11);
    if (d1 >= 10) {
      d1 = 0;
    }

    // Cálculo do segundo dígito verificador
    soma = d1 * 2;
    pesoInicio = 4;
    pesoFim = 7;

    for (int i = 0; i < 8; i++) {
      int num = Character.getNumericValue(ie.charAt(i));
      soma += (i < 3) ? num * pesoInicio-- : num * pesoFim--;
    }

    int d2 = 11 - (soma % 11);
    if (d2 >= 10) {
      d2 = 0;
    }

    // Validação dos dígitos verificadores
    if (Character.getNumericValue(ie.charAt(8)) != d1 || Character.getNumericValue(ie.charAt(9)) != d2) {
      throw new RFWValidationException("BISERP_100048");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado da Paraíba.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonPB(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100045");
    }

    int soma = 0;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) { // Apenas os primeiros 8 dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int dvCalculado = 11 - (soma % 11);
    if (dvCalculado >= 10) {
      dvCalculado = 0;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100046");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Pará.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonPA(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100042");
    }

    // Validação dos dois primeiros dígitos
    if (!ie.startsWith("15")) {
      throw new RFWValidationException("BISERP_100043");
    }

    int soma = 0;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) { // Apenas os primeiros 8 dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int dvCalculado = 11 - (soma % 11);
    if (dvCalculado <= 1) {
      dvCalculado = 0;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100044");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado de Minas Gerais.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonMG(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{13}")) {
      throw new RFWValidationException("BISERP_100040");
    }

    // TODO o Código de validação a seguir precisa ser revisto, pois ele não validou a IE 460037914500, que é confirmada pelo sintegra a IE válida do CNPJ: 07.599.349/0001-30 Agroindustria Quinta Sao Jose Ltda

    // // Inserir "0" após o código do município para normalizar o formato
    // StringBuilder str = new StringBuilder();
    // for (int i = 0; i < 11; i++) { // Apenas os primeiros 11 dígitos (sem os dígitos verificadores)
    // if (i == 3) str.append("0"); // Adiciona o zero após o código do município
    // str.append(ie.charAt(i));
    // }
    //
    // // Cálculo do primeiro dígito verificador
    // int soma = 0;
    // for (int i = 0; i < str.length(); i++) {
    // int num = Character.getNumericValue(str.charAt(i));
    // int produto = num * (i % 2 == 0 ? 1 : 2); // Alterna entre multiplicadores 1 e 2
    // soma += (produto >= 10) ? (produto / 10) + (produto % 10) : produto; // Soma os dígitos do produto
    // }
    //
    // int d1 = (10 - (soma % 10)) % 10; // Obtém o primeiro dígito verificador
    //
    // // Cálculo do segundo dígito verificador
    // soma = d1 * 2;
    // int peso = 3;
    // for (int i = 0; i < 11; i++) {
    // soma += Character.getNumericValue(ie.charAt(i)) * peso;
    // peso = (peso == 3) ? 11 : peso - 1;
    // }
    //
    // int d2 = (11 - (soma % 11)) % 10; // Obtém o segundo dígito verificador
    //
    // // Validação dos dígitos verificadores
    // if (Character.getNumericValue(ie.charAt(11)) != d1 || Character.getNumericValue(ie.charAt(12)) != d2) {
    // throw new RFWValidationException("BISERP_100041");
    // }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Mato Grosso do Sul.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonMS(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100037");
    }

    // Validação dos dois primeiros dígitos
    if (!ie.startsWith("28")) {
      throw new RFWValidationException("BISERP_100038");
    }

    int soma = 0;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) { // Apenas os primeiros 8 dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int resto = soma % 11;
    int dvCalculado = (resto == 0 || 11 - resto > 9) ? 0 : 11 - resto;

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100039");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Mato Grosso.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonMT(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{11}")) {
      throw new RFWValidationException("BISERP_100035");
    }

    int soma = 0;
    int pesoInicial = 3;
    int pesoFinal = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 10; i++) { // Apenas os primeiros 10 dígitos
      int num = Character.getNumericValue(ie.charAt(i));
      soma += (i < 2) ? num * pesoInicial-- : num * pesoFinal--;
    }

    // Cálculo do dígito verificador
    int resto = soma % 11;
    int dvCalculado = (resto == 0 || resto == 1) ? 0 : 11 - resto;

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(10)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100036");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Maranhão.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonMA(String ie) throws RFWException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100032");
    }

    // Validação dos dois primeiros dígitos
    if (!ie.startsWith("12")) {
      throw new RFWValidationException("BISERP_100033");
    }

    int soma = 0;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) { // Apenas os primeiros 8 dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso;
      peso--;
    }

    // Cálculo do dígito verificador
    int resto = soma % 11;
    int dvCalculado = (resto == 0 || resto == 1) ? 0 : 11 - resto;

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100034");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado de Goiás.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonGO(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100029");
    }

    // Validação dos dois primeiros dígitos
    String prefix = ie.substring(0, 2);
    if (!prefix.equals("10") && !prefix.equals("11") && !prefix.equals("15")) {
      throw new RFWValidationException("BISERP_100030");
    }

    // Caso especial: a inscrição 11094402 pode ter DV 0 ou 1
    if (ie.startsWith("11094402")) {
      char lastDigit = ie.charAt(8);
      if (lastDigit != '0' && lastDigit != '1') {
        throw new RFWValidationException("BISERP_100031");
      }
      return;
    }

    // Cálculo do dígito verificador
    int soma = 0;
    int peso = 9;

    for (int i = 0; i < 8; i++) { // Apenas os primeiros 8 dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    }

    int resto = soma % 11;
    long faixaInicio = 10103105;
    long faixaFim = 10119997;
    long insc = Long.parseLong(ie.substring(0, 8));

    int dvCalculado;
    if (resto == 0) {
      dvCalculado = 0;
    } else if (resto == 1) {
      dvCalculado = (insc >= faixaInicio && insc <= faixaFim) ? 1 : 0;
    } else {
      dvCalculado = 11 - resto;
    }

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100031");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Espírito Santo.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonES(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100027");
    }

    int soma = 0;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) { // Apenas os primeiros 8 dígitos
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    }

    // Cálculo do dígito verificador
    int resto = soma % 11;
    int dvCalculado = (resto < 2) ? 0 : 11 - resto;

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100028");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Distrito Federal.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonDF(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("\\d{13}")) {
      throw new RFWValidationException("BISERP_100025");
    }

    // Cálculo do primeiro dígito verificador
    int soma = 0;
    int peso = 4;
    for (int i = 0; i < 3; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    peso = 9;
    for (int i = 3; i < 11; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;

    int d1 = 11 - (soma % 11);
    if (d1 >= 10) d1 = 0;

    // Cálculo do segundo dígito verificador
    soma = d1 * 2;
    peso = 5;
    for (int i = 0; i < 4; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    peso = 9;
    for (int i = 4; i < 11; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;

    int d2 = 11 - (soma % 11);
    if (d2 >= 10) d2 = 0;

    // Validação dos dígitos verificadores
    if (!ie.endsWith("" + d1 + d2)) {
      throw new RFWValidationException("BISERP_100026");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Ceará.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonCE(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100023");
    }

    int soma = 0;
    int peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) {
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    }

    // Cálculo do dígito verificador
    int resto = soma % 11;
    int dvCalculado = (resto == 10 || resto == 11) ? 0 : 11 - resto;

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100024");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado da Bahia.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonBA(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("\\d{8}|\\d{9}")) {
      throw new RFWValidationException("BISERP_100021");
    }

    // Determina o módulo com base no primeiro dígito
    int firstDigit = Character.getNumericValue(ie.charAt(ie.length() == 8 ? 0 : 1));
    int modulo = (firstDigit == 6 || firstDigit == 7 || firstDigit == 9) ? 11 : 10;

    // Cálculo do segundo dígito verificador
    int soma = 0, peso = (ie.length() == 8) ? 7 : 8;
    for (int i = 0; i < ie.length() - 2; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;

    int resto = soma % modulo;
    int d2 = (resto == 0 || (modulo == 11 && resto == 1)) ? 0 : modulo - resto;

    // Cálculo do primeiro dígito verificador
    soma = d2 * 2;
    peso = (ie.length() == 8) ? 8 : 9;
    for (int i = 0; i < ie.length() - 2; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;

    resto = soma % modulo;
    int d1 = (resto == 0 || (modulo == 11 && resto == 1)) ? 0 : modulo - resto;

    // Validação dos dígitos verificadores
    if (!ie.endsWith("" + d1 + d2)) {
      throw new RFWValidationException("BISERP_100022");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Amazonas.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonAM(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100019");
    }

    int soma = 0, peso = 9;

    // Cálculo da soma ponderada
    for (int i = 0; i < 8; i++) {
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    }

    // Cálculo do dígito verificador
    int dvCalculado = (soma < 11) ? (11 - soma) : ((soma % 11) <= 1 ? 0 : 11 - (soma % 11));

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != dvCalculado) {
      throw new RFWValidationException("BISERP_100020");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Amapá.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonAP(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100016");
    }

    // Verifica os dois primeiros dígitos - deve ser igual a "03"
    if (!ie.startsWith("03")) {
      throw new RFWValidationException("BISERP_100017");
    }

    // Define os valores de soma e d1 com base na faixa da inscrição estadual
    long x = Long.parseLong(ie.substring(0, 8)); // x = inscrição estadual sem o dígito verificador
    int d1 = (x >= 3017001L && x <= 3019022L) ? 1 : 0;
    int soma = (x >= 3000001L && x <= 3017000L) ? 5 : (x >= 3019023L ? 0 : 9);

    // Cálculo da soma ponderada
    int peso = 9;
    for (int i = 0; i < 8; i++) {
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    }

    // Cálculo do dígito verificador
    int d = 11 - (soma % 11);
    d = (d == 10) ? 0 : (d == 11 ? d1 : d);

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != d) {
      throw new RFWValidationException("BISERP_100018");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado de Alagoas.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonAL(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("\\d{9}")) {
      throw new RFWValidationException("BISERP_100013");
    }

    // Valida os dois primeiros dígitos - deve ser "24"
    if (!ie.startsWith("24")) {
      throw new RFWValidationException("BISERP_100014");
    }

    // Valida o terceiro dígito - deve ser 0,3,5,7,8
    char terceiroDigito = ie.charAt(2);
    if ("03578".indexOf(terceiroDigito) == -1) {
      throw new RFWValidationException("BISERP_100015");
    }

    // Cálculo do dígito verificador
    int soma = 0, peso = 9;
    for (int i = 0; i < 8; i++) {
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    }

    int d = (soma * 10) % 11;
    if (d == 10) d = 0;

    // Validação do dígito verificador
    if (Character.getNumericValue(ie.charAt(8)) != d) {
      throw new RFWValidationException("BISERP_100015");
    }
  }

  /**
   * Valida se o valor entrado é uma IE (Inscrição Estadual) válida de acordo com a validação do estado do Acre.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a IE for inválida.
   */
  public static void validateIEonAC(String ie) throws RFWValidationException {
    if (ie == null || !ie.matches("\\d{13}")) {
      throw new RFWValidationException("BISERP_100010");
    }

    // Valida os dois primeiros dígitos - devem ser "01"
    if (!ie.startsWith("01")) {
      throw new RFWValidationException("BISERP_100012");
    }

    // Cálculo do primeiro dígito verificador
    int soma = 0, peso = 4;
    for (int i = 0; i < 3; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    peso = 9;
    for (int i = 3; i < 11; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;

    int d1 = 11 - (soma % 11);
    if (d1 >= 10) d1 = 0;

    // Cálculo do segundo dígito verificador
    soma = d1 * 2;
    peso = 5;
    for (int i = 0; i < 4; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;
    peso = 9;
    for (int i = 4; i < 11; i++)
      soma += Character.getNumericValue(ie.charAt(i)) * peso--;

    int d2 = 11 - (soma % 11);
    if (d2 >= 10) d2 = 0;

    // Validação dos dígitos verificadores
    if (Character.getNumericValue(ie.charAt(11)) != d1 || Character.getNumericValue(ie.charAt(12)) != d2) {
      throw new RFWValidationException("BISERP_100011");
    }
  }

  /**
   * Valida uma Inscrição Estadual sem saber o estado. Testa a inscrição estadual para cada UF disponível, aceitando-a se for válida em pelo menos um estado.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @throws RFWValidationException Se a inscrição não for válida em nenhum estado.
   */
  public static void validateIE(String ie) throws RFWException {
    try {
      validateIEonSP(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonMG(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonRJ(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonAP(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonAM(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonBA(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonCE(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonDF(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonES(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonGO(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonMA(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonMT(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonMS(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonAC(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonPA(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonPB(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonPR(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonPE(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonPI(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonAL(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonRN(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonRS(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonRO(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonRR(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonSC(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonSE(ie);
      return;
    } catch (RFWValidationException e) {
    }
    try {
      validateIEonTO(ie);
      return;
    } catch (RFWValidationException e) {
    }

    throw new RFWValidationException("BISERP_000298", new String[] { ie });
  }

  /**
   * Valida a Inscrição Estadual de acordo com a UF informada. Este método delega a validação para o método correto de cada estado.
   *
   * @param ie Inscrição Estadual a ser validada.
   * @param acronym UF (sigla de 2 letras) do estado correspondente. Ex: "SP", "RJ", "MG", etc.
   * @throws RFWValidationException Se a UF não for válida ou a IE for inválida para a UF.
   */
  public static void validateIE(String ie, String acronym) throws RFWException {
    if (ie == null || acronym == null) throw new RFWValidationException("BISERP_000304");

    acronym = acronym.toUpperCase(); // Garante que a sigla esteja em maiúsculas
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
        throw new RFWValidationException("BISERP_000305"); // UF inválida
    }
  }

  /**
   * Este método calcula um Dígito Verificador baseado no módulo de 11 com uma implementação genérica para ser utilizada pelo sistema.<Br>
   * Muitos documentos utilizam a validação com o módulo de 11 (que se refere a utlização do resto da divisão por onze), no entando muitas diferem nas regras de definição do DV.<br>
   * NÃO ALTERE O FUNCIONAMENTO DESTE MÉTODO, pois ele já é utilizado no sistema para diversos cálculos de segurança. Para validação de DV de documentos específicos, crie métodos próprios.
   *
   * @param value valor contendo apenas dígitos para que seja calculado o DV.
   * @return String contendo apenas 1 caracter que será o DV.
   * @throws RFWException Lançado caso o valor não tenha apenas números, ou seja um valor nulo/vazio.
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
   * Valida se o endereço de e-mail tem uma sintaxe válida.
   *
   * @param email endereço de email.
   * @throws RFWException
   */
  public static void validateEmailAddress(String email) throws RFWException {
    if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
      throw new RFWValidationException("RFW_000062");
    }
  }

  /**
   * Valida se o código de barras GTIN é válido. Funciona para GTIN8, GTIN12, GTIN13 e GTIN14.<br>
   * Caso o valor passado seja nulo, resultará em NullPointerException para evitar que erros de programação em passar o valor sejam acobertados por uma "prevenção" interna do método.
   *
   * @param fullCodeBar Código de Barra completo, incluindo o dívido verificador
   * @return boolean indicando se o conteúdo recebido é um código de barras GTIN válido.
   */
  public static boolean isValidGTINCodeBar(String fullCodeBar) {
    try {
      validateGTINCodeBar(fullCodeBar);
      return true;
    } catch (RFWException e) {
      return false;
    }
  }

  /**
   * Valida se o código de barras GTIN é válido. Funciona para GTIN8, GTIN12, GTIN13 e GTIN14.<br>
   * Caso o valor passado seja nulo, resultará em NullPointerException para evitar que erros de programação em passar o valor sejam acobertados por uma "prevenção" interna do método.
   *
   * @param fullCodeBar Código de Barra completo, incluindo o dívido verificador
   * @throws RFWException Lançado se o conteúdo não for um código de barras GTIN válido.
   */
  public static void validateGTINCodeBar(String fullCodeBar) throws RFWException {
    if (fullCodeBar != null && (fullCodeBar.length() == 8 || fullCodeBar.length() == 12 || fullCodeBar.length() == 13 || fullCodeBar.length() == 14)) {
      if (fullCodeBar.matches("[0-9]*")) {
        int impSum = 0;
        // PS: Nas iterações não consideramos o último número apra os cálculos poide deve ser o DV
        for (int i = fullCodeBar.length() - 2; i >= 0; i -= 2) { // itera os números nas posições impares
          impSum += Integer.parseInt(fullCodeBar.substring(i, i + 1));
        }
        impSum *= 3; // Multiplicamos o resultado por 3
        for (int i = fullCodeBar.length() - 3; i >= 0; i -= 2) { // soma os números nas porições pares
          impSum += Integer.parseInt(fullCodeBar.substring(i, i + 1));
        }
        // Verificamos o número que "falta" para chegar no próximo múltiplo de 10
        int dv = (10 - (impSum % 10)) % 10; // <- O segundo módulo garante que quando o resultado do primeiro módulo der 0, o DV não resulta em 10, e sim em 0 como deve ser.

        // Verificamos se é válido
        if (!fullCodeBar.substring(fullCodeBar.length() - 1, fullCodeBar.length()).equals("" + dv)) {
          throw new RFWValidationException("RFW_000063");

        }
      }
    } else {
      throw new RFWValidationException("RFW_000063");
    }
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
}
