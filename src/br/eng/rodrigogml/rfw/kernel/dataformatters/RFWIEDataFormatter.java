package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.utils.RUDocVal;

/**
 * Description: Classe que formata e valida um n�mero de IE (Inscri��o Estadual) de forma gen�rica. Isto �, tenta dar formato e validar a IE para qualquer estado nacional.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (05/05/2015)
 */
public class RFWIEDataFormatter implements RFWDataFormatter<String, String> {

  private static final RFWIEDataFormatter instance = new RFWIEDataFormatter();

  private RFWIEDataFormatter() {
  }

  public static RFWIEDataFormatter getInstance() {
    return RFWIEDataFormatter.instance;
  }

  @Override
  public String toPresentation(String value, Locale locale) {
    String result = "";
    if (value != null && !"".equals(value.toString().trim())) {
      result = value.toString().trim();
      result = result.replaceAll("[^0-9]", "");
      // Inscri��es Estaduais tem tamanhos diferentes para cada estado. De acordo com o tamanho formatamos de modo diferente
      if (result.length() == 12) { // SP
        result = result.substring(0, 3) + "." + result.substring(3, 6) + "." + result.substring(6, 9) + "." + result.substring(9, 12);
      }
    }
    return result;
  }

  @Override
  public String toVO(String formattedvalue, Locale locale) {
    String result = null;
    if (formattedvalue != null) {
      result = formattedvalue.replaceAll("[^0-9]", "");
    }
    return result;
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    if (value != null && !"".equals(value.toString().trim())) {
      try {
        // Valida o IE
        value = value.toString().replaceAll("[^0-9]", "");
        RUDocVal.validateIE(value.toString());
      } catch (RFWValidationException e) {
        throw new RFWValidationException("RFW_ERR_300052");
      }
    }
  }

  @Override
  public int getMaxLenght() {
    return 25;
  }
}
