package br.eng.rodrigogml.rfw.kernel.utils;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilit�ria com m�todos relacionados a contas (boletos, c�digo de barras de contas, QRCodes de PIX, etc.).<br>
 *
 * @author Rodrigo Leit�o
 * @since (18 de fev. de 2025)
 */
public class RUBills {

  /**
   * Tipos de C�digo de Barras de boleto e outros t�tulos banc�rio.
   */
  public enum BankBillCodeType {
    /**
     * C�digo de barras do boleto.
     */
    BOLETO_BARCODE,
    /**
     * Linha digit�vel do boleto.
     */
    BOLETO_NUMERICCODE,
    /**
     * C�digo de barras de uma guia de arrecada��o.
     */
    SERVICE_BARCODE,
    /**
     * Linha digit�vel de uma guia de arrecada��o.
     */
    SERVICE_NUMERICCODE
  }

  /**
   * Classe est�tica.
   */
  private RUBills() {
  }

  /**
   * Verifica se a linha num�rica (linha digit�vel) � uma linha de boleto v�lida.<br>
   * O m�todo considera apenas os d�gitos, removendo automaticamente qualquer pontua��o ou formata��o existente.
   *
   * @param numericLine the numeric line
   * @throws RFWException the RFWDeprec exception
   */
  public static void isBoletoNumericCodeValid(String numericLine) throws RFWException {
    numericLine = RUString.removeNonDigits(numericLine);

    // Se o tamanho est� correto
    if (!(numericLine.length() == 33 || (numericLine.length() > 33 && numericLine.length() <= 36 && numericLine.substring(33) == "0000") || numericLine.length() >= 37 && numericLine.length() <= 47)) {
      throw new RFWValidationException("RFW_ERR_210055");
    }

    // Se possui c�digo do banco preenchido
    if (numericLine.substring(0, 3) == "000") {
      throw new RFWValidationException("RFW_ERR_210057");
    }

    // Se o c�digo da moeda � v�lido
    if (numericLine.charAt(3) != '9') {
      throw new RFWValidationException("RFW_ERR_210063");
    }

    // DV do primeiro bloco
    String firstBlockCode = numericLine.substring(0, 9);
    if (numericLine.charAt(9) != RUDVCalc.calcBoletoBlocoDigitavelDV(firstBlockCode).charAt(0)) {
      throw new RFWValidationException("RFW_ERR_210060");
    }

    // DV do segundo bloco
    String secondBlockCode = numericLine.substring(10, 20);
    if (numericLine.charAt(20) != RUDVCalc.calcBoletoBlocoDigitavelDV(secondBlockCode).charAt(0)) {
      throw new RFWValidationException("RFW_ERR_210061");
    }

    // DV do terceiro bloco
    String thirdBlockCode = numericLine.substring(21, 31);
    if (numericLine.charAt(31) != RUDVCalc.calcBoletoBlocoDigitavelDV(thirdBlockCode).charAt(0)) {
      throw new RFWValidationException("RFW_ERR_210062");
    }

    // O DV � calculado com base no c�digo de barras e n�o no c�digo num�rico. Por isso primeiro vamos converter o c�digo num�rico no c�digo de barras e depois calcular o DV
    String codeBar = convertNumericCodeToBarCode(numericLine);
    String codeBarWithoutDV = codeBar.substring(0, 4) + codeBar.substring(5);
    if (numericLine.charAt(32) != RUDVCalc.calcPaymentSlipDVForServices(codeBarWithoutDV).charAt(0)) {
      throw new RFWValidationException("RFW_ERR_210058");
    }
  }

  /**
   * Converte o c�digo num�rico para o c�digo de barras.<br>
   * Este m�todo espera receber um c�digo num�rico v�lido. N�O CALCULA O DV, copia ele do c�digo num�rico. N�O VALIDA O RESULTADO!
   *
   * @param numericCode A linha num�rica a ser convertida.
   * @return O c�digo de barras convertido.
   * @throws RFWException
   */
  public static String convertNumericCodeToBarCode(String numericCode) throws RFWException {
    numericCode = RUString.removeNonDigits(numericCode);

    String barCode = null;
    final BankBillCodeType type = getCodeType(numericCode, false);
    if (type == null) throw new RFWValidationException("C�digo Num�rico Inv�lido ou n�o conhecido pelo RFWDeprec!");

    switch (type) {
      case SERVICE_NUMERICCODE: {
        barCode = numericCode.substring(0, 11);
        barCode += numericCode.substring(12, 23);
        barCode += numericCode.substring(24, 35);
        barCode += numericCode.substring(36, 47);
      }
        break;
      case BOLETO_NUMERICCODE: {
        // Completa com zeros o c�digo caso ele n�o esteja no tamaho correto para facilitar a convers�o
        numericCode = RUString.completeUntilLengthRight("0", numericCode, 47);

        barCode = numericCode.substring(0, 4);
        barCode += numericCode.substring(32, 33);
        barCode += numericCode.substring(33, 47);
        barCode += numericCode.substring(4, 9);
        barCode += numericCode.substring(10, 20);
        barCode += numericCode.substring(21, 31);
      }
        break;
      case BOLETO_BARCODE:
      case SERVICE_BARCODE:
        // N�o converte neste m�todo
        barCode = numericCode;
        break;
    }
    return barCode;
  }

  /**
   * Identifica qual � o tipo de {@code BankBillCodeType} que uma string representa.<br>
   * ESTE M�TODO FAZ A VALIDA��O DOS DVs PARA GARANTIR QUE A NUMERA��O EST� SENDO CORRETAMENTE INTERPRETADA
   *
   * @param code O c�digo a ser verificado.
   * @return Um {@code BankBillCodeType} que identifica o c�digo passado ao m�todo, OU nulo caso o c�digo n�o seja reconhecido/v�lido
   * @throws RFWException
   */
  public static BankBillCodeType getCodeType(String code) throws RFWException {
    return getCodeType(code, true);
  }

  /**
   * Recupera o tipo do c�digo de barra baseado no tamanho e estrutura do c�digo.
   *
   * @param code C�digo a ser definido.
   * @param test Caso true, o m�todo testa a estrutura e DV para garantir que � o c�digo, caso false considera apenas o tamanho. (Em alguns casos n�o testa para evitar o loop infinito entre m�todos).
   * @return the code type
   * @throws RFWException
   */
  private static BankBillCodeType getCodeType(String code, boolean test) throws RFWException {
    code = RUString.removeNonDigits(code);
    if (code.length() == 44) { // Provavelmente � c�digo de barras
      if (code.startsWith("8")) { // Provavelmente c�digo de arrecada��o
        try {
          if (test) isServiceBarCodeValid(code);
          return BankBillCodeType.SERVICE_BARCODE;
        } catch (RFWValidationException e) {
        }
      }
      // Se n�o come�a com 8, ou se n�o era v�lido, tentamos como boleto
      try {
        if (test) isBoletoBarCodeValid(code);
        return BankBillCodeType.BOLETO_BARCODE;
      } catch (RFWValidationException e) {
      }
    }
    // Se o tamanho n�o � 44, ou se falhou em validar como c�digo de barras, vamos processar como representa��o num�rica
    // A primeira � a representa��o de arrecada��o, que � sempre fixa e � a maior
    if (code.length() == 48) {
      try {
        if (test) isServiceNumericCodeValid(code);
        return BankBillCodeType.SERVICE_NUMERICCODE;
      } catch (RFWValidationException e) {
      }
    } else if (code.length() >= 33 && code.length() <= 47) {
      // Se o tamanho est� entre os tamanhos que a representa��o num�rica do boleto pode ter, validamos para verificar
      try {
        if (test) isBoletoNumericCodeValid(code);
        return BankBillCodeType.BOLETO_NUMERICCODE;
      } catch (RFWValidationException e) {
      }
    }
    return null;
  }

  /**
   * Verifica se o c�digo de barras � um c�digo de guia de arrecada��o/servi�os v�lido. <br>
   * O m�todo remove quaisquer caracteres que n�o sejam d�gitos do c�digo de barras antes de validar.
   *
   * @param codeBar O c�digo de barras a ser verificado.
   * @throws RFWException
   */
  public static void isServiceBarCodeValid(String codeBar) throws RFWException {
    codeBar = RUString.removeNonDigits(codeBar);

    // Tamanho exato deve ser de 44 algarismos
    if (codeBar.length() != 44) {
      throw new RFWValidationException("O tamanho do c�digo n�o � v�lido para uma guia de arrecada��o/servi�o.", new String[] { "44" });
    }

    // O primeiro d�gito deve ser "8"
    if (codeBar.charAt(0) != '8') {
      throw new RFWValidationException("O c�digo de barras da guia de arrecada��o deve sempre come�ar com 8.");
    }

    // Removendo o DV da string
    String codeToCheck = codeBar.substring(0, 3) + codeBar.substring(4);

    // Identificador do valor deve ser:
    // 6 (Valor a ser cobrado efetivamente em reais) - com d�gito verificador calculado pelo m�dulo 10
    // 7 (Quantidade de Moeda) - com d�gito verificador calculado pelo m�dulo 10
    // 8 (Valor a ser cobrado efetivamente em reais) - com d�gito verificador calculado pelo m�dulo 11
    // 9 (Quantidade de Moeda) - com d�gito verificador calculado pelo m�dulo 11
    if (codeBar.charAt(2) != '6' && codeBar.charAt(2) != '7' && codeBar.charAt(2) != '8' && codeBar.charAt(2) != '9') {
      throw new RFWValidationException("O c�digo da moeda do c�digo de barras n�o � v�lido, ou n�o reconhecido pelo RFWDeprec.");
    }

    // Se o campo 'segmento' possui um valor v�lido, ou seja, diferente de zero e 8 (os �nicos valores inv�lidos atualmente)
    if (codeBar.charAt(1) == '0' || codeBar.charAt(1) == '8') {
      throw new RFWValidationException("RFW_ERR_210065");
    }

    // O DV deve estar correto
    if ((codeBar.charAt(2) == '6' || codeBar.charAt(2) == '7') && codeBar.charAt(3) != RUDVCalc.calcBoletoBlocoDigitavelDV(codeToCheck).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do c�digo de barras n�o est� correto.");
    } else if ((codeBar.charAt(2) == '8' || codeBar.charAt(2) == '9')) {
      if (codeBar.charAt(1) == '5' || codeBar.charAt(1) == '1') { // 5 - Guias Governo // 1 - Prefeituras
        if (codeBar.charAt(3) != RUDVCalc.calcPaymentSlipDVForGovernment(codeToCheck).charAt(0)) {
          throw new RFWValidationException("O d�gito verificador do c�digo de barras n�o est� correto.");
        }
      } else if (codeBar.charAt(3) != RUDVCalc.calcPaymentSlipDVForServices(codeToCheck).charAt(0)) {
        throw new RFWValidationException("O d�gito verificador do c�digo de barras n�o est� correto.");
      }
    }
  }

  /**
   * Verifica se a linha num�rica (linha digit�vel) � uma linha de guia de arrecada��o/servi�os v�lida. <br>
   * O m�todo remove quaisquer caracteres que n�o sejam d�gitos do c�digo de barras antes de validar.
   *
   * @param numericCode the numeric code
   * @throws RFWException
   */
  public static void isServiceNumericCodeValid(String numericCode) throws RFWException {
    numericCode = RUString.removeNonDigits(numericCode);

    // Tamanho exato de 48 algarismos
    if (numericCode.length() != 48) {
      throw new RFWValidationException("O tamanho do c�digo num�rico n�o � v�lido para uma conta servi�o/guia de arrecada��o.", new String[] { "48" });
    }

    // Como o a representa��o num�rica � o pr�prio c�digo de barras, s� acrescido de um DV a cada 11 posi��es, vamos remover elas e fazer a valida��o do c�digo de barras, depois s� validamos os DVs.
    // Assim as valida��es ficam todas concentradas s� no m�todo do c�digo de barras.
    String firstBlockCode = numericCode.substring(0, 11);
    String secondBlockCode = numericCode.substring(12, 23);
    String thirdBlockCode = numericCode.substring(24, 35);
    String fourthBlockCode = numericCode.substring(36, 47);

    String barCode = firstBlockCode + secondBlockCode + thirdBlockCode + fourthBlockCode;
    isServiceBarCodeValid(barCode);

    int modCalc;
    if (barCode.charAt(2) == '6' || barCode.charAt(2) == '7') {
      modCalc = 1;
    } else if (barCode.charAt(2) == '8' || barCode.charAt(2) == '9') {
      if (barCode.charAt(1) == '5' || barCode.charAt(1) == '1') { // 5 - Guias de Arrecada��o Governo / 1 - Prefeituras
        modCalc = 3;
      } else {
        modCalc = 2;
      }
    } else {
      throw new RFWValidationException("C�digo do Identificador desconhecido pelo RFWDeprec!");
    }

    // Validar DV do primeiro bloco
    if (modCalc == 1 && numericCode.charAt(11) != RUDVCalc.calcBoletoBlocoDigitavelDV(firstBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    } else if (modCalc == 2 && numericCode.charAt(11) != RUDVCalc.calcPaymentSlipDVForServices(firstBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    } else if (modCalc == 3 && numericCode.charAt(11) != RUDVCalc.calcPaymentSlipDVForGovernment(firstBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    }

    // Validar DV do segundo bloco
    if (modCalc == 1 && numericCode.charAt(23) != RUDVCalc.calcBoletoBlocoDigitavelDV(secondBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    } else if (modCalc == 2 && numericCode.charAt(23) != RUDVCalc.calcPaymentSlipDVForServices(secondBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    } else if (modCalc == 3 && numericCode.charAt(23) != RUDVCalc.calcPaymentSlipDVForGovernment(secondBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    }

    // Validar DV do terceiro bloco
    if (modCalc == 1 && numericCode.charAt(35) != RUDVCalc.calcBoletoBlocoDigitavelDV(thirdBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    } else if (modCalc == 2 && numericCode.charAt(35) != RUDVCalc.calcPaymentSlipDVForServices(thirdBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    } else if (modCalc == 3 && numericCode.charAt(35) != RUDVCalc.calcPaymentSlipDVForGovernment(thirdBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    }

    // Validar DV do terceiro bloco
    if (modCalc == 1 && numericCode.charAt(47) != RUDVCalc.calcBoletoBlocoDigitavelDV(fourthBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    } else if (modCalc == 2 && numericCode.charAt(47) != RUDVCalc.calcPaymentSlipDVForServices(fourthBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    } else if (modCalc == 3 && numericCode.charAt(47) != RUDVCalc.calcPaymentSlipDVForGovernment(fourthBlockCode).charAt(0)) {
      throw new RFWValidationException("O d�gito verificador do bloco 1 � inv�lido!");
    }
  }

  /**
   * Verifica se o c�digo de barras � um c�digo de boleto v�lido. <br>
   * O m�todo remove quaisquer caracteres que n�o sejam d�gitos do c�digo de barras antes de validar.
   *
   * @param codebar O c�digo de barras a ser verificado.
   * @throws RFWException
   */
  public static void isBoletoBarCodeValid(String codebar) throws RFWException {
    codebar = RUString.removeNonDigits(codebar);

    // Tamanho exato de 44 algarismos
    if (codebar.length() != 44) {
      throw new RFWValidationException("RFW_ERR_210054", new String[] { "44" });
    }

    // Removendo o DV da string
    String codeToCheck = codebar.substring(0, 4) + codebar.substring(5);
    // O DV tem que estar correto
    if (codebar.charAt(4) != RUDVCalc.calcPaymentSlipDVForServices(codeToCheck).charAt(0)) {
      throw new RFWValidationException("RFW_ERR_210058");
    }

    // Se o c�digo do banco n�o est� zerado
    if (codebar.substring(0, 3) == "000") {
      throw new RFWValidationException("RFW_ERR_210057");
    }

    // Se o campo de moeda � 9, pois n�o conhecemos outro valor
    if (codebar.charAt(3) != '9') {
      throw new RFWValidationException("RFW_ERR_210063");
    }
  }

  /**
   * Este m�todo recebe um c�digo, podendo ser um c�digo de barras ou uma representa��o num�rica (numericCode) e verifica se � v�lida. O m�todo tenta reconhecer entre todos os tipos de c�digo conhecido pelo Framework.
   *
   * @param code C�digo e ser validado.
   * @throws RFWException
   */
  public static void isCodeValid(String code) throws RFWException {
    code = RUString.removeNonDigits(code);
    final BankBillCodeType type = getCodeType(code);
    if (type == null) throw new RFWValidationException("C�digo inv�lido ou n�o reconhecido pelo RFWDeprec!");
    switch (type) {
      case BOLETO_BARCODE:
        isBoletoBarCodeValid(code);
        break;
      case BOLETO_NUMERICCODE:
        isBoletoNumericCodeValid(code);
        break;
      case SERVICE_BARCODE:
        isServiceBarCodeValid(code);
        break;
      case SERVICE_NUMERICCODE:
        isServiceNumericCodeValid(code);
        break;
    }
  }

  /**
   * Converte o c�digo de barras para o c�digo num�rico.<br>
   * Este m�todo espera receber um c�digo de barras v�lido. N�O CALCULA O DV GERAL (s� os intermedi�rios pois n�o existem no C�digo de Barras), copia ele do c�digo recebido. N�O VALIDA O RESULTADO!
   *
   * @param barCode C�digo de Barras ser convertido.
   * @return O c�digo de barras convertido em c�digo num�rico.
   * @throws RFWException
   */
  public static String convertBarCodeToNumericCode(String barCode) throws RFWException {
    barCode = RUString.removeNonDigits(barCode);

    String numericCode = null;
    final BankBillCodeType type = getCodeType(barCode);
    if (type == null) throw new RFWValidationException("C�digo de Barras Inv�lido ou n�o conhecido pelo RFWDeprec!");

    switch (type) {
      case SERVICE_BARCODE: {

        // Verifica qual Mod utilizar de acordo com o campo 'identificador do valor'
        int modCalc; // 1 M�dulo de 10, 2-M�dulo de 11, 3-Mod11 para Guias de Arreca��o do Governo
        if (barCode.charAt(2) == '6' || barCode.charAt(2) == '7') {
          modCalc = 1;
        } else if (barCode.charAt(2) == '8' || barCode.charAt(2) == '9') {
          if (barCode.charAt(1) == '5' || barCode.charAt(1) == '1') { // 5 - Guias de Arrecada��o Governo / 1 - Prefeituras
            modCalc = 3;
          } else {
            modCalc = 2;
          }
        } else {
          throw new RFWValidationException("C�digo do Identificador desconhecido pelo RFWDeprec!");
        }

        numericCode = "";

        String newBlock = barCode.substring(0, 11);
        if (modCalc == 1) {
          numericCode += newBlock + RUDVCalc.calcBoletoBlocoDigitavelDV(newBlock);
        } else if (modCalc == 2) {
          numericCode += newBlock + RUDVCalc.calcPaymentSlipDVForServices(newBlock);
        } else {
          numericCode += newBlock + RUDVCalc.calcPaymentSlipDVForGovernment(newBlock);
        }

        newBlock = barCode.substring(11, 22);
        if (modCalc == 1) {
          numericCode += newBlock + RUDVCalc.calcBoletoBlocoDigitavelDV(newBlock);
        } else if (modCalc == 2) {
          numericCode += newBlock + RUDVCalc.calcPaymentSlipDVForServices(newBlock);
        } else {
          numericCode += newBlock + RUDVCalc.calcPaymentSlipDVForGovernment(newBlock);
        }

        newBlock = barCode.substring(22, 33);
        if (modCalc == 1) {
          numericCode += newBlock + RUDVCalc.calcBoletoBlocoDigitavelDV(newBlock);
        } else if (modCalc == 2) {
          numericCode += newBlock + RUDVCalc.calcPaymentSlipDVForServices(newBlock);
        } else {
          numericCode += newBlock + RUDVCalc.calcPaymentSlipDVForGovernment(newBlock);
        }

        newBlock = barCode.substring(33, 44);
        if (modCalc == 1) {
          numericCode += newBlock + RUDVCalc.calcBoletoBlocoDigitavelDV(newBlock);
        } else if (modCalc == 2) {
          numericCode += newBlock + RUDVCalc.calcPaymentSlipDVForServices(newBlock);
        } else {
          numericCode += newBlock + RUDVCalc.calcPaymentSlipDVForGovernment(newBlock);
        }
      }
        break;
      case BOLETO_BARCODE: {
        // Completa com zeros o c�digo caso ele n�o esteja no tamaho correto para facilitar a convers�o
        barCode = RUString.completeUntilLengthRight("0", barCode, 47);

        String block = null;
        // Bloco 1
        block = barCode.substring(0, 4);
        block += barCode.substring(19, 24);
        numericCode = block + RUDVCalc.calcBoletoBlocoDigitavelDV(block);
        // Bloco 2
        block = barCode.substring(24, 34);
        numericCode += block + RUDVCalc.calcBoletoBlocoDigitavelDV(block);
        // Bloco 3
        block = barCode.substring(34, 44);
        numericCode += block + RUDVCalc.calcBoletoBlocoDigitavelDV(block);
        // DV Geral
        numericCode += barCode.substring(4, 5);
        // Vencimento e Valor
        numericCode += barCode.substring(5, 19);
      }
        break;
      case BOLETO_NUMERICCODE:
      case SERVICE_NUMERICCODE:
        // N�o converte neste m�todo
        numericCode = barCode;
        break;
    }
    return numericCode;
  }
}
