package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;

/**
 * Description: Classe utilizada para formar valores booleanos utilizando os valores "Sim" para true e "Não" para false.<br>
 *
 * @author Rodrigo Leitão
 * @since 7.3.0 (20 de ago de 2017)
 */
public class RFWBooleanYesNoDataFormatter implements RFWDataFormatter<String, Boolean> {

  public RFWBooleanYesNoDataFormatter() {
  }

  @Override
  public String toPresentation(Boolean value, Locale locale) {
    String result = "";
    if (value != null) {
      if (value) {
        result = "Sim";
      } else {
        result = "Não";
      }
    }
    return result;
  }

  @Override
  public Boolean toVO(String formattedvalue, Locale locale) throws RFWException {
    if ("SIM".equalsIgnoreCase(formattedvalue)) {
      return Boolean.TRUE;
    } else if ("NÃO".equalsIgnoreCase(formattedvalue)) {
      return Boolean.TRUE;
    } else {
      return null;
    }
  }

  @Override
  public int getMaxLenght() {
    return 0;
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
  }

}
