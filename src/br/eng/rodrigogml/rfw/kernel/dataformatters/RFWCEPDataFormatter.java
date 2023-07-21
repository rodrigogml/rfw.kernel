package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: <br>
 *
 * @author Rodrigo Leit�o
 * @since 4.1.0 (24/06/2011)
 */
public class RFWCEPDataFormatter implements RFWDataFormatter<String, String> {

  private static RFWCEPDataFormatter instance = new RFWCEPDataFormatter();

  public static RFWCEPDataFormatter getInstance() {
    return RFWCEPDataFormatter.instance;
  }

  @Override
  public String toPresentation(String value, Locale locale) {
    String result = "";
    if (value != null) {
      // Remove tudo o que n�o for n�mero
      result = value.replaceAll("[^0-9]*", "");
      // Verifica se o tamanho � maior que 5 para poder inserir o tra�o
      if (result.length() > 5) {
        result = result.substring(0, 5) + "-" + result.substring(5, result.length());
      }
    }
    return result;
  }

  @Override
  public String toVO(String formattedvalue, Locale locale) {
    String result = null;
    if (formattedvalue != null) {
      result = formattedvalue.replaceAll("[^0-9]*", "");
    }
    return result;
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    if (value != null) {
      // Remove tudo o que n�o for n�mero
      String result = value.toString().replaceAll("[^0-9]*", "");
      // Verifica se temos todos os 8 d�gitos do CEP
      if (result.length() != 8 && result.length() != 0) {
        throw new RFWValidationException("RFW_ERR_300050");
      }
    }
  }

  @Override
  public int getMaxLenght() {
    return 9;
  }

}