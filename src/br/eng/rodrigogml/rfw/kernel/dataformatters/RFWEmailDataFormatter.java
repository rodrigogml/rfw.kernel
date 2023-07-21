package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.utils.RUMail;

/**
 * Description: Data formatter para endere�os de e-mail.<BR>
 * Esta classe n�o "formata" os e-mail, o valor para o VO e para a Tela passa livremente, a �nica efici�ncia desta classe est� no M�todo de valida��o.
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (23/06/2015)
 */
public class RFWEmailDataFormatter implements RFWDataFormatter<String, String> {

  private static RFWEmailDataFormatter instance = null;

  private RFWEmailDataFormatter() {
  }

  public static RFWEmailDataFormatter getInstance() {
    if (RFWEmailDataFormatter.instance == null) {
      RFWEmailDataFormatter.instance = new RFWEmailDataFormatter();
    }
    return RFWEmailDataFormatter.instance;
  }

  @Override
  public String toPresentation(String value, Locale locale) {
    if (value == null) return "";
    return value.toString();
  }

  @Override
  public String toVO(String formattedvalue, Locale locale) {
    return formattedvalue;
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    if (value != null && !"".equals(value.toString())) {
      RUMail.validateMailAddress(value.toString());
    }
  }

  @Override
  public int getMaxLenght() {
    return 255;
  }

}
