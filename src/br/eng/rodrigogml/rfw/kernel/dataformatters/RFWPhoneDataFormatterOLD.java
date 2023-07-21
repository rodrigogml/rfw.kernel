package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: DataFormatter para n�meros de telefones.<br>
 * Atualmente formata para n�meros no padr�o nacional, no futuro podemos melhorar a implementa��o desta classe para receber um Location e formatar n�meros de diferentes pa�ses.<br>
 * <br Refer�ncia: http://pt.wikipedia.org/wiki/N%C3%BAmeros_de_telefone_no_Brasil
 *
 * @deprecated Utilize a nova classe {@link RFWPhoneDataFormatter}
 * @author Rodrigo Leit�o
 * @since 7.1.0 (12/05/2015)
 */
@Deprecated
// TODO rodrigogml me excluir quando n�o estiver mais em uso!
public class RFWPhoneDataFormatterOLD implements RFWDataFormatter<String, String> {

  /**
   * Indica se devemos usar o DDI no telefone formatado
   */
  boolean useDDI = false;

  /**
   * Indica se devemos usar o DDD no telefone formatado.
   */
  boolean useDDD = true;

  /**
   * Cria um DataFormatter para formatar n�meros de telefones.<br>
   * A informa��o de n�mero de telefone deve ter o seguinte formado <DDI>|<DDD>|<Telefone>, sendo que os 2 pipes s�o sempre obrigat�rios, mesmo que n�o haja informa��o do DDI ou DDD. N�o deve haver nenhuma pontua��o de formata��o, apenas n�meros.
   *
   */
  public RFWPhoneDataFormatterOLD(boolean useDDI, boolean useDDD) {
    this.useDDI = useDDI;
    this.useDDD = useDDD;
  }

  /**
   * Como os telefones s�o exibidos em campos especiais para tratar o telefone, este m�todo apenas devolve o conte�do como recebido no VO, toda a "formata��o" � tratada pelo pr�prio campo. Para exibir o telfone de forma formata em uma String verifique o m�todo {@link #format(String)}.
   */
  @Override
  public String toPresentation(String value, Locale locale) {
    return value;
  }

  /**
   * Formata o valor como recebido do banco. Ex: "55|19|32363309"
   *
   * @param value
   * @return
   */
  public String format(String value) {
    String fValue = "";
    if (value != null) {
      // Para formatar para a apresenta��o consideramos que o valor � sempre v�lido pois deve ter vindo do VO.
      String[] p = value.split("[\\|]");

      if (this.useDDI) fValue = formatDDI(p[0]) + ' ';
      if (this.useDDD) fValue += '(' + formatDDD(p[0], p[1]) + ") ";
      fValue += formatNumber(p[0], p[1], p[2]);
    }
    return fValue;
  }

  /**
   * Aplica pontua��o / formato em uma sequ�ncia de n�mero que possa ser um telefone.
   *
   * @param ddi C�digo do Pa�s para que possaos distinguir os tipos de numera��o de telefone usado em casa pa�s.
   * @param ddd C�digo de �rea da regi�o. Em conjunto com o c�digo de pa�s, � utilizado para dar o formato adequado a uma regi�o.
   * @param number N�mero do telefone que ser� formatado.
   * @return N�mero do telefone formatado. Note que as informa��es de DDI e DDD s�o utilizadas apenas para identificar a regi�o e como o n�mero deve aparecer, mas esses valores n�o ser�o retornados junto. Para formatar esses dados utilize em conjunto as fun��es {@link #formatDDI(String)} e {@link #formatDDD(String, String)}.
   */
  public static String formatNumber(String ddi, String ddd, String number) {
    if (ddi == null) ddi = "";
    ddi = ddi.replaceAll("[^0-9]*", "");
    if (ddd == null) ddd = "";
    ddd = ddd.replaceAll("[^0-9]*", "");
    if (number == null) number = "";
    number = number.replaceAll("[^0-9]*", "");

    String fValue = number;
    switch (ddi) {
      case "55": // BRASIL
        if (number.length() == 3) {
          // Com 3 d�gitos s�o telefones de servi�o retornamos somente o n�mero, independente da configura��o de DDI e DDD
          fValue = number;
        } else if (number.length() == 8) {
          // Com 8 n�meros � um telefone "local", fixo ou m�vel, apenas formatamos no padr�o XXXX-XXXX
          fValue = number.substring(0, 4) + "-" + number.substring(4, 8);
        } else if (number.length() == 9) {
          // Com 9 n�meros � o n�mero dos celulares das regi�es metropolitadas que tem o tal 9� d�gito
          fValue = number.substring(0, 5) + "-" + number.substring(5, 9);
        } else if (number.length() >= 4 && number.substring(0, 4).matches("0[3589]00")) {
          // Com 11 d�gitos tamb�m temos os n�meros de 0800, 0300, 0500 e 0900 neste caso formatamos diferente
          fValue = number.substring(0, 4) + " " + number.substring(4, 7) + " " + number.substring(7, 11);
        }
        break;
    }
    return fValue;
  }

  /**
   * Formata um c�digo de Pa�s para ser anexado em um telefone para ser exibido ao cliente.
   *
   * @param ddi C�digo do pa�s a ser formatado.
   * @return C�digo do pa�s formato.
   */
  public static String formatDDI(String ddi) {
    return '+' + ddi;
  }

  /**
   * Formata o c�digo de �rea de um determinado pa�s, para ser usado em conjunto com o restante do n�mero para exibi��o ao usu�rio.
   *
   * @param ddi c�digo do pa�s. Utilizado apenas para identificar a �rea e formatar corretamente o DDD.
   * @param ddd C�digo de �rea a ser formatado.
   * @return C�digo de �rea formatado.
   */
  public static String formatDDD(String ddi, String ddd) {
    return ddd;
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

  /**
   * Faz a valida��o do n�mero do telefone.
   *
   * @param ddi Indicador de pa�s para valida��o do n�mero.
   * @param ddd Indicador de �rea do telefone. Utilizado para validar o n�mero.
   * @param number N�mero do telefone que ser� validado para o c�digo de �rea e do pa�s.
   * @throws RFWException Lan�ado caso n�o seja um n�mero v�lido para o Pa�s e �rea passados.
   */
  public static void validateNumber(String ddi, String ddd, String number) throws RFWException {
    if (ddi == null) ddi = "";
    ddi = ddi.replaceAll("[^0-9]*", "");
    if (ddd == null) ddd = "";
    ddd = ddd.replaceAll("[^0-9]*", "");
    if (number == null) number = "";
    number = number.replaceAll("[^0-9]*", "");

    switch (ddi) {
      case "55": // BRASIL
        // O Brasil n�o tem distin��o de n�mero de acordo com a �rea.
        if ("".equals(ddd) && "".equals(number)) {
          // Se ambos est�o sem conte�do, consideramos que o campo n�o est� preenchido nada a validar
        } else if ("".equals(ddd) && number.length() == 3 && number.charAt(0) == '1') {
          // Considerado n�mero de servi�o: tipo 190, 102, etc. Aceita
        } else if (number.length() < 8) {
          // Tirando os n�meros de servi�os, n�o temos n�meros menores que 8 d�gitos no Brasil.
          throw new RFWValidationException("RFW_ERR_300063", new String[] { ddi, ddd, number });
        } else if (number.length() == 8 && (number.charAt(0) == '2' || number.charAt(0) == '3' || number.charAt(0) == '4' || number.charAt(0) == '5' || number.charAt(0) == '7' || number.charAt(0) == '8' || number.charAt(0) == '9')) {
          // Telefone com 8 d�gitos podem ser: de telefonia fixa e telefonia fixa rural (2, 3, 4 e 5) Exceto os teleofes que come�am com 400; telefones de servi�o com cobran�a: come�am com 400; telefones de celulares em cidades com menor popula��o (8, 9); e Trunking ou radio (7);
        } else if (number.length() == 9 && (number.charAt(0) == '9')) {
          // Telefones com 9 d�gitos s�o celulares de algumas cidades metropolitanas do pa�s que ganharam o 9� d�gito no celular. Por isso s� aceita telefone com 9 digitos se o primeiro for um 9
        } else if (number.length() == 11 && (number.charAt(0) == '0' && number.charAt(2) == '0')) {
          // Existem n�meros com 11 d�gitos que s�o os de 0800 e 0900. Como o N�mero de Celular (9 digitos) + DDD (2 digitos) tamb�m d� 11.
          // Verificamos j� no if acima se for 11 e for no padr�o 0?0... "aceitamos o n�mero" deixando entrar nesse IF e n�o executando os pr�ximos 'Elses'
        } else if (number.length() >= 9) {
          throw new RFWValidationException("RFW_ERR_300063");
        }
        break;
      default:
        throw new RFWValidationException("RFW_ERR_300062", new String[] { ddi, ddd });
    }
  }

  /**
   * Validador para o telefone no formato do VO: "DDI|DDD|Telefone"
   *
   * @param value
   * @throws RFWEXception
   */
  public static void validateVOData(String value) throws RFWException {
    if (value != null && !"".equals(value) && !"||".equals(value)) {
      value = value.replaceAll("[^\\|0-9]", "");
      if (!value.matches("([1-9][0-9]{1,2})?\\|([1-9][0-9])?\\|[0-9]*")) {
        throw new RFWValidationException("RFW_ERR_200354");
      }
      // Incluimos um �ltimo pipe com um conte�do (o '.') para que o split crie obrigatoriamente todos os elementos do array. pois se recebermos algo como "55||" o resultado � um Array s� com a posi��o 0, os campos vazios no final n�o s�o criados no array.
      String parts[] = (value + "|.").split("\\|");

      if ("".equals(parts[1])) {
        // Se n�o tiver DDD, o telefone deve ser de servi�o (3) ou um 0800 (11)
        if (!"".equals(parts[2]) && parts[2].length() != 3 && parts[2].length() != 11) {
          throw new RFWValidationException("RFW_ERR_200355");
        }
      } else {
        validateDDD(parts[0], parts[1]);
      }
      validateNumber(parts[0], parts[1], parts[2]); // Valida a parte do telefone.
    }
  }

  @Override
  public String toVO(String formattedvalue, Locale locale) throws RFWException {
    // Como esperamos que que na UI o campo gere o telefone j� no formato que utilizamos no VO, n�o h� nada para converter, s� devolter o dado j� pronto (que j� deve ter sido validado pelo m�todo validate()
    // S� verificamos se recebemos tanto o DDD quando o N�mero vazios, neste caso retornamos nulo para otimizar o banco de dados.
    formattedvalue = formattedvalue.replaceAll("[^\\|0-9]", "");
    String parts[] = (formattedvalue + "|.").split("\\|");
    if ("".equals(parts[1]) && "".equals(parts[2])) {
      formattedvalue = null;
    }
    return formattedvalue;
  }

  /**
   * Este m�todo valida o dado entrado pelo usu�rio na UI, como deve ser utilizado o RFWPhoneField j� recebemos o valor correto do VO.
   */
  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    // Como esperamos que que na UI o campo gere o telefone j� no formato que utilizamos no VO, vamos validar como sendo o pr�prio dado do VO
    validateVOData((String) value);
  }

  @Override
  public int getMaxLenght() {
    return 20;
  }
}