package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe que formata e valida um c�digo NCM, de acordo com a quantidade de d�gitos fornecida no campo.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (13/12/2014)
 */
public class RFWNCMCodeDataFormatter implements RFWDataFormatter<String, Object> {

  private RFWNCMCodeDataFormatter() {
  }

  public static RFWNCMCodeDataFormatter createInstance() {
    return new RFWNCMCodeDataFormatter();
  }

  @Override
  public String toPresentation(Object value, Locale locale) {
    StringBuilder result = new StringBuilder();
    if (value != null && !"".equals(value.toString().trim())) {
      String t = value.toString().trim().replaceAll("[^0-9]", "");
      result.append(t.substring(0, 2));
      if (t.length() > 2) {
        result.append(".").append(t.substring(2, 4));
        if (t.length() > 4) {
          result.append(".").append(t.substring(4, 5));
          if (t.length() > 5) {
            result.append(t.substring(5, 6));
            if (t.length() > 6) {
              result.append(".").append(t.substring(6, 7));
              if (t.length() > 7) {
                result.append(t.substring(7, 8));
              }
            }
          }
        }
      }
    }
    return result.toString();
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
      if (v.length() != 2 && (v.length() < 4 || v.length() > 8)) {
        throw new RFWValidationException("N�o � um c�digo NCM v�lido!");
      }
    }
  }

  @Override
  public int getMaxLenght() {
    return 11;
  }
}
