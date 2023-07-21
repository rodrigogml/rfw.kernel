package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.utils.RUString;

/**
 * Description: Classe que formata e valida um c�digo CEST, de acordo com a quantidade de d�gitos fornecida no campo.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (14 de dez de 2016)
 */
public class RFWCESTCodeDataFormatter implements RFWDataFormatter<String, Object> {

  public RFWCESTCodeDataFormatter() {
  }

  @Override
  public String toPresentation(Object value, Locale locale) {
    String result = "";
    if (value != null && !"".equals(value.toString().trim())) {
      result = value.toString().trim();
      result = result.replaceAll("[^0-9]", "");
      RUString.completeUntilLengthLeft("0", result, 7);
      result = result.substring(0, 2) + "." + result.substring(2, 5) + "." + result.substring(5, 7);
    }
    return result;
  }

  @Override
  public Object toVO(String formattedvalue, Locale locale) {
    String result = null;
    if (formattedvalue != null) {
      result = formattedvalue.replaceAll("[^0-9]", "");
    }
    return result;
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    if (value != null && !"".equals(value.toString().trim())) {
      String v = value.toString().replaceAll("[^0-9]", "");
      // Verifica se tem tamanho Correto depois que s� sobrarem n�meros - exige que tenha os 8 n�meros e n�o completa n�meros a esquerda para evitar que ao "esquecer" um n�mero durante a digita��o, o erro seja detectado.
      if (v.length() != 7) {
        throw new RFWValidationException("N�o � um c�digo CEST v�lido!");
      }
    }
  }

  @Override
  public int getMaxLenght() {
    return 9;
  }

}
