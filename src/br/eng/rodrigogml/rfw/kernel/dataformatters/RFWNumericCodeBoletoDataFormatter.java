package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.utils.RUDocVal;
import br.eng.rodrigogml.rfw.kernel.utils.RUString;

/**
 * Description: Classe que formata e valida o c�digo num�rico de um boleto de cobran�a.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (4 de mai de 2019)
 */
public class RFWNumericCodeBoletoDataFormatter implements RFWDataFormatter<String, String> {

  public RFWNumericCodeBoletoDataFormatter() {
  }

  @Override
  public String toPresentation(String value, Locale locale) {
    String result = "";
    try {
      if (value != null && !"".equals(value.toString().trim())) {
        result = RUString.removeNonDigits(value);
        if (result.length() == 47) {
          // Boleto de Cobran�a
          result = result.substring(0, 5) + "." + result.substring(5, 10) + " " + result.substring(10, 15) + "." + result.substring(15, 21) + " " + result.substring(21, 26) + "." + result.substring(26, 32) + " " + result.substring(32, 33) + " " + result.substring(33, 47);
        } else if (result.length() == 48) {
          // Guia de Arrecada��o/Servi�o
          result = result.substring(0, 11) + "-" + result.substring(11, 12) + " " + result.substring(12, 23) + "-" + result.substring(23, 24) + " " + result.substring(24, 35) + "-" + result.substring(35, 36) + " " + result.substring(36, 47) + "-" + result.substring(47, 48) + " ";
        }
      }
    } catch (Throwable e) {
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
        // Valida o CPF
        String v = value.toString().replaceAll("[^0-9]", "");
        RUDocVal.validateCPF(v);
      } catch (RFWValidationException e) {
        throw new RFWValidationException("RFW_ERR_300053", e);
      }
    }
  }

  @Override
  public int getMaxLenght() {
    return 14;
  }

}
