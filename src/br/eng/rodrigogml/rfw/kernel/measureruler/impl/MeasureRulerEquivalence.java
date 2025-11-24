package br.eng.rodrigogml.rfw.kernel.measureruler.impl;

import java.math.BigDecimal;
import java.util.HashMap;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.measureruler.MeasureRuler;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureRulerEquivalenceInterface;
import br.eng.rodrigogml.rfw.kernel.measureruler.interfaces.MeasureUnit;

/**
 * Description: Implementação padrão da interface {@link MeasureRulerEquivalenceInterface}. Utilizado para criar as regras de equivalências entre as dimenões (grandezas de medidas).<br>
 * O sistema pode utilizar sua própria classe, como por exemplo um VO caso queira persistir sua equivalência, bastando que sua classe implemente a interface {@link MeasureRulerEquivalenceInterface} para ser repassada para o {@link MeasureRuler}.
 *
 * @author Rodrigo GML
 * @since 1.0.0 (26 de jul. de 2023)
 * @version 1.0.0 - Rodrigo GML-(...)
 */
public class MeasureRulerEquivalence implements MeasureRulerEquivalenceInterface {

  /**
   * Hash com os valores de Equivalência
   */
  private HashMap<MeasureUnit, BigDecimal> measureUnitHash = new HashMap<MeasureUnit, BigDecimal>();

  /**
   * # hash com os valores de Equivalência.
   *
   * @return # hash com os valores de Equivalência
   */
  @Override
  public HashMap<MeasureUnit, BigDecimal> getMeasureUnitHash() throws RFWException {
    return measureUnitHash;
  }

}
