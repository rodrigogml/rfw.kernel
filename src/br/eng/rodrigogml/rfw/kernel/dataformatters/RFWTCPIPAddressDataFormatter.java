package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;
import br.eng.rodrigogml.rfw.kernel.utils.RUValueValidation;

/**
 * Description: Data formatter para IPs IPv4.<BR>
 *
 * @author Rodrigo Leitão
 * @since 7.1.0 (15/06/2015)
 */
public class RFWTCPIPAddressDataFormatter implements RFWDataFormatter<String, Object> {

  private static RFWTCPIPAddressDataFormatter instance = null;

  private RFWTCPIPAddressDataFormatter() {
  }

  public static RFWTCPIPAddressDataFormatter getInstance() {
    if (RFWTCPIPAddressDataFormatter.instance == null) {
      RFWTCPIPAddressDataFormatter.instance = new RFWTCPIPAddressDataFormatter();
    }
    return RFWTCPIPAddressDataFormatter.instance;
  }

  @Override
  public String toPresentation(Object value, Locale locale) throws RFWException {
    if (value == null) return "";
    return value.toString();
  }

  @Override
  public String toVO(String formattedvalue, Locale locale) throws RFWException {
    if ("".equals(formattedvalue)) return null;
    // Quebramos de acordo com os pontos, convertemos para Integer e reconcatemaos para garantir que não ficamos com nenhum 0 não significativo em cada um dos valores.
    final String[] parts = formattedvalue.split("\\.");
    return Integer.parseInt(parts[0]) + "." + Integer.parseInt(parts[1]) + "." + Integer.parseInt(parts[2]) + "." + Integer.parseInt(parts[3]);
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    // Contrata o valor contra uma expressão regular que verifica digito por digito do IP
    value = PreProcess.processStringToNull((String) value);
    if (value != null) {
      try {
        RUValueValidation.validateIPv4Address((String) value);
      } catch (RFWException e) {
        throw new RFWValidationException("RFW_ERR_200306");
      }
    }
  }

  @Override
  public int getMaxLenght() {
    return 15;
  }

}
