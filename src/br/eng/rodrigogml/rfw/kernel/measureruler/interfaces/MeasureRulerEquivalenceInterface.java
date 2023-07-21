package br.eng.rodrigogml.rfw.kernel.measureruler.interfaces;

import java.math.BigDecimal;
import java.util.HashMap;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.measureruler.MeasureRuler;

/**
 * Description: Esta interface define os m�todos que o sistema deve implementar para que o {@link MeasureRuler} consiga realizar a transforma��o entre unidades de medidas de dimen��es diferentes. Incluindo unidades definidas pelo usu�rio.<br>
 *
 * @author Rodrigo Leit�o
 * @since 10.0 (22 de nov. de 2021)
 */
public interface MeasureRulerEquivalenceInterface {

  /**
   * Deve retornar uma hash com as unidades de medidas da r�gua de equival�ncia e seus pesos de convers�o. N�o confundir com os {@link MeasureUnit#getRatio()}, esses pesos s�o os pesos de convers�o entre dimens�es diferentes e/ou unidades de medidas personalizadas, n�o entre as unidades da mesma dimens�o.
   *
   * @return Hash contendo as unidades de medidas e seus pessos para convers�o entre dimens�es de medidas.
   * @throws RFWException
   */
  public HashMap<MeasureUnit, BigDecimal> getMeasureUnitHash() throws RFWException;

}
