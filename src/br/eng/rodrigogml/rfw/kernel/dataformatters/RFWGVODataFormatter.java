package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.vo.GVO;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Data formatter utilizado para componentes que trabalham com lista de VOs, mas por falta do Equals por ID o componente n�o identifica corretamente os valores "definidos".<Br>
 * <br>
 * Confira mais detalhes na Documenta��o do {@link GVO}.
 */
public class RFWGVODataFormatter<VO extends RFWVO> implements RFWDataFormatter<GVO<VO>, VO> {

  @Override
  public GVO<VO> toPresentation(VO value, Locale locale) throws RFWException {
    return new GVO<VO>(value);
  }

  @Override
  public VO toVO(GVO<VO> formattedvalue, Locale locale) throws RFWException {
    if (formattedvalue == null) return null;
    return formattedvalue.getVO();
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
  }

  @Override
  public int getMaxLenght() {
    return 999;
  }

}
