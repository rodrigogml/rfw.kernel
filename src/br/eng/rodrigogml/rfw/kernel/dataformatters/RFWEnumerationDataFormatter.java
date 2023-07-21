package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.bundle.RFWBundle;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWRunTimeException;

/**
 * Description: Data Formatter para trocar o valor de uma enumeration para o valor do Bundle.<br>
 *
 * @author Rodrigo GML
 * @since 10.0 (20 de dez de 2019)
 */
public class RFWEnumerationDataFormatter implements RFWDataFormatter<String, Enum<?>> {

  @Override
  public String toPresentation(Enum<?> value, Locale locale) throws RFWException {
    return RFWBundle.get(value);
  }

  @Override
  public Enum<?> toVO(String formattedvalue, Locale locale) throws RFWException {
    throw new RFWRunTimeException("O RFWEnumerationDataFormatter n�o foi implementado para realizar o parser, apenas para formatar para relat�rios e campos de exibi��o (como Grids, etc.)");
  }

  @Override
  public int getMaxLenght() {
    return 500;
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    throw new RFWRunTimeException("O RFWEnumerationDataFormatter n�o foi implementado para realizar o parser, apenas para formatar para relat�rios e campos de exibi��o (como Grids, etc.)");

  }

}
