package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: DataFormatter para n�meros de telefones.<br>
 * Atualmente formata para n�meros no padr�o nacional, no futuro podemos melhorar a implementa��o desta classe para receber um Location e formatar n�meros de diferentes pa�ses.<br>
 * <br Refer�ncia: http://pt.wikipedia.org/wiki/N%C3%BAmeros_de_telefone_no_Brasil
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (12/05/2015)
 */
public class RFWPhoneDataFormatter implements RFWDataFormatter<String, String> {

  /**
   * Enumera��o com os tipos de n�meros de telefones reconhecidos por esta classe
   */
  public static enum PhoneType {
    /**
     * N�mero de telefone desconhecido pelo sistema.
     */
    UNKNOW,
    /**
     * N�meros de utilidades publicas brasileiras, como 190 (Pol�cia), 199 (Defesa Civ�l)
     */
    BRAZIL_PUBLICUTILS,
    /**
     * Telefones residenciais brasileiros
     */
    BRAZIL_PHONE,
    /**
     * Telefones celulares brasileiros.
     */
    BRAZIL_MOBILE,
    /**
     * Telefones de servi�os n�o geogr�ficos, como 0800 (liga��o gr�tis nacional), 0500 (Doa��es e utilidade p�blica)
     */
    BRAZIL_SERVICES
  }

  public RFWPhoneDataFormatter() {
  }

  /**
   * Este m�todo tenta reconhecer o n�mero realizando valida��es. Se o n�mero n�o for reconhecido ou n�o for v�lido, retorna nulo.<br>
   * Note que este m�todo no momento n�o tem suporte � n�meros internacionais, mas se algum dia vier a ter, n�meros pequenos e sem prefixos internacionais devem ser considerados prioritariamente como telefones nacionais.
   *
   * @param number n�mero a ser identificado, validado e formatado
   * @return Retorna o tipo de telefone identificado.
   */
  public static PhoneType getPhoneType(String number) {
    if (number != null) {
      number = number.replaceAll("[^\\d]", ""); // Remove o que n�o for d�gito

      switch (number.length()) { // A formata��o � baseada no tamanho da enumera��o
        case 3:
          switch (number) {
            case "100": // Secretaria dos Direitos Humanos
            case "128": // Servi�os de Emerg�ncia no Mercosul
            case "151": // Funda��o Procon
            case "152": // Ibama
            case "153": // Guarda Municipal
            case "156": // Servi�o P�blico Municipal
            case "180": // Central de Atendimento � Mulher
            case "181": // Disque Den�ncia
            case "185": // Salvamar
            case "188": // Centro de Valoriza��o da Vida
            case "190": // Pol�cia Militar
            case "191": // Pol�cia Rodovi�ria Federal
            case "192": // Servi�o de Atendimento M�vel de Urg�ncia
            case "193": // Corpo de Bombeiros
            case "194": // Pol�cia Federal
            case "197": // Pol�cia Civil
            case "198": // Pol�cia Rodovi�ria Estadual
            case "199": // Defesa Civil
            case "102": // Aux�lio � Lista Dispon�vel apenas em Telefonia Fixa
            case "130": // Hora Certa Tarifado, Dispon�vel apenas em Telefonia Fixa
            case "134": // Despertador Tarifado, Dispon�vel apenas em Telefonia Fixa
              return PhoneType.BRAZIL_PUBLICUTILS;
            default:
              return PhoneType.UNKNOW;
          }
          // Ex: 190 - Telefones de servi�os p�blicos
        case 4:
          if (number.startsWith("105")) {// 105X - Central de Relacionamento SMP X = Identifica��o da Operadora
            return PhoneType.BRAZIL_PUBLICUTILS;
          } else {
            return PhoneType.UNKNOW;
          }
        case 5:
          if (number.startsWith("103")) {// 103XX - Central de Relacionamento STFC XX = C�digo da Operadora
            return PhoneType.BRAZIL_PUBLICUTILS;
          } else if (number.startsWith("106")) {// 106XX - Central de Relacionamento DTH/TDT XX = C�digo da Prestadora
            return PhoneType.BRAZIL_PUBLICUTILS;
          } else {
            return PhoneType.UNKNOW;
          }
        case 8:
          // Ex: 3236-3309 - Telefone Fixo
          // Ex: 9999-5555 - Telefones celulares ainda sem o nono d�gito (caindo em desuso por isso n�o considerado mais por este m�todo)
          return PhoneType.BRAZIL_PHONE;
        case 9:
          // Ex: 98189-5510 - Telefone celular com o nono d�gito
          return PhoneType.BRAZIL_MOBILE;
        case 10:
          // Ex: 19 3236-3309 - Telefones Fixos (ou celular antidos) com DDD
          return PhoneType.BRAZIL_PHONE;
        case 11:
          // Ex: 0800 543 1010 - Telefones de atendimento
          // Ex: 19 98189-5519 - Telefones Celular com DDD
          if (number.substring(0, 4).matches("0[3589]00")) { // Se come�a com 0800, 0300, 0500 e 0900
            return PhoneType.BRAZIL_SERVICES;
          } else {
            return PhoneType.BRAZIL_MOBILE;
          }
        case 12:
          // Ex: +55 19 3236-3309 - Telefone fixo brasileiro com DDI e DDD
          return PhoneType.BRAZIL_PHONE;
        case 13:
          // Ex: +55 19 981895519 - Telefone celular com no no d�gito com DDI e DDD
          return PhoneType.BRAZIL_MOBILE;
      }
    }
    return PhoneType.UNKNOW;
  }

  /**
   * Como os telefones s�o exibidos em campos especiais para tratar o telefone, este m�todo apenas devolve o conte�do como recebido no VO, toda a "formata��o" � tratada pelo pr�prio campo. Para exibir o telfone de forma formata em uma String verifique o m�todo {@link #format(String)}.
   */
  @Override
  public String toPresentation(String value, Locale locale) {
    return format(value);
  }

  /**
   * Este m�todo formata o n�mero recebido. Para definir uma formata��o adequada ele primeiro tenta reconhecero tipo do n�mero. Se nao reconehcer, aplica uma formata��o padr�o baseado no tamanho.
   *
   * @param number n�mero a ser identificado, validado e formatado
   * @return Retorna o valor formatado
   */
  public String format(String number) {
    if (number == null) number = "";
    number = number.replaceAll("[^\\d]", ""); // Remove o que n�o for n�mero
    switch (getPhoneType(number)) {
      case BRAZIL_MOBILE:
        switch (number.length()) {
          case 8:
            // Ex: 9999-5555 - Telefones celulares ainda sem o nono d�gito
            return new StringBuilder().append(number.substring(0, 4)).append("-").append(number.substring(4, 8)).toString();
          case 9:
            // Ex: 98189-5510 - Telefone celular com o nono d�gito
            return new StringBuilder().append(number.substring(0, 5)).append("-").append(number.substring(5, 9)).toString();
          case 10:
            // Ex: 19 81895519 - Telefone celular (sem o nono d�gito) com com DDD
            return new StringBuilder().append(number.substring(0, 2)).append(" ").append(number.substring(2, 6)).append(" ").append(number.substring(6, 10)).toString();
          case 11:
            // Ex: 19 981895519 - Telefone celular com o nono d�gito com DDD
            return new StringBuilder().append(number.substring(0, 2)).append(" ").append(number.substring(2, 7)).append(" ").append(number.substring(7, 11)).toString();
          case 13:
            // Ex: +55 19 981895519 - Telefone celular com no no d�gito com DDI e DDD
            return new StringBuilder().append("+").append(number.substring(0, 2)).append(" ").append(number.substring(2, 4)).append(" ").append(number.substring(4, 8)).append("-").append(number.substring(8, 12)).toString();
        }
        break;
      case BRAZIL_PHONE:
        switch (number.length()) {
          case 8:
            // Ex: 3236-3309 - Telefone Fixo
            return new StringBuilder().append(number.substring(0, 4)).append("-").append(number.substring(4, 8)).toString();
          case 10:
            // Ex: 19 3236-3309 - Telefones Fixos (ou celular antidos) com DDD
            return new StringBuilder().append(number.substring(0, 2)).append(" ").append(number.substring(2, 6)).append("-").append(number.substring(6, 10)).toString();
          case 12:
            // Ex: +55 19 3236-3309 - Telefone fixo brasileiro com DDI e DDD
            return new StringBuilder().append("+").append(number.substring(0, 2)).append(" ").append(number.substring(2, 4)).append(" ").append(number.substring(4, 8)).append("-").append(number.substring(8, 12)).toString();
        }
        break;
      case BRAZIL_PUBLICUTILS:
        return new StringBuilder().append(number).toString();
      case BRAZIL_SERVICES:
        switch (number.length()) {
          case 11:
            return new StringBuilder().append(number.substring(0, 4)).append(" ").append(number.substring(4, 7)).append(" ").append(number.substring(7, 11)).toString();
        }
        break;
      case UNKNOW:
        // Se o n�mero n�o � reconhecido, n�o faz nenhuma formata��o
        break;
    }
    return number;
  }

  /**
   * Valida um DDD.
   *
   * @param ddi DDI para detectar qual � o pais a qual pertence o DDD para que seja poss�vel validar se � v�lido.
   * @param ddd DDD c�digo de �rea a ser validado.
   * @throws RFWException Lan�ado em caso de erro ou se o valor for inv�lido.
   */
  public static void validateDDD(String ddi, String ddd) throws RFWException {
    switch (ddi) {
      case "55": // BRASIL
        switch (ddd) {
          case "11":
          case "12":
          case "13":
          case "14":
          case "15":
          case "16":
          case "17":
          case "18":
          case "19":
          case "21":
          case "22":
          case "24":
          case "27":
          case "28":
          case "31":
          case "32":
          case "33":
          case "34":
          case "35":
          case "37":
          case "38":
          case "41":
          case "42":
          case "43":
          case "44":
          case "45":
          case "46":
          case "47":
          case "48":
          case "49":
          case "51":
          case "53":
          case "54":
          case "55":
          case "61":
          case "62":
          case "63":
          case "64":
          case "65":
          case "66":
          case "67":
          case "68":
          case "69":
          case "71":
          case "73":
          case "74":
          case "75":
          case "77":
          case "79":
          case "81":
          case "82":
          case "83":
          case "84":
          case "85":
          case "86":
          case "87":
          case "88":
          case "89":
          case "91":
          case "92":
          case "93":
          case "94":
          case "95":
          case "96":
          case "97":
          case "98":
          case "99":
            // DDDs v�lidos para o Brasi
            break;
          default:
            throw new RFWValidationException("DDD inv�lido!", new String[] { ddi, ddd });
        }
        break;
      default:
        throw new RFWValidationException("DDI desconhecido pelo RFWDeprec!", new String[] { ddi, ddd });
    }
  }

  @Override
  public String toVO(String value, Locale locale) throws RFWException {
    validate(value, locale);
    return value.replaceAll("[^\\d]", "");
  }

  /**
   * Este m�todo valida o dado entrado pelo usu�rio na UI, como deve ser utilizado o RFWPhoneField j� recebemos o valor correto do VO.
   */
  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    if (value != null && !"".equals(value)) {
      if (!(value instanceof String)) throw new RFWCriticalException("O Objeto recebido deveria ser uma String!");
      if (getPhoneType((String) value) == PhoneType.UNKNOW) throw new RFWValidationException("N�mero de telefone inv�lido!");
    }
  }

  @Override
  public int getMaxLenght() {
    return 20;
  }
}