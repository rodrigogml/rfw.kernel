package br.eng.rodrigogml.rfw.kernel.measureruler.interfaces;

import java.math.BigDecimal;
import java.util.HashMap;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.measureruler.MeasureRuler;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Esta interface define os métodos que o sistema deve implementar para que o {@link MeasureRuler} consiga realizar a transformação entre unidades de medidas de dimenções diferentes. Incluindo unidades definidas pelo usuário.<br>
 *
 * @author Rodrigo Leitão
 * @since 10.0 (22 de nov. de 2021)
 */
public interface MeasureRulerEquivalenceInterface {

  /**
   * Deve retornar uma hash com as unidades de medidas da régua de equivalência e seus pesos de conversão. Não confundir com os {@link MeasureUnit#getRatio()}, esses pesos são os pesos de conversão entre dimensões diferentes e/ou unidades de medidas personalizadas, não entre as unidades da mesma dimensão.
   *
   * @return Hash contendo as unidades de medidas e seus pessos para conversão entre dimensões de medidas.
   * @throws RFWException
   */
  public HashMap<MeasureUnit, BigDecimal> getMeasureUnitHash() throws RFWException;

  /**
   * Permite incluir uma comparação nesta régua de equivalências sem atrapalhar o conteúdo atual.<br>
   * Por exemplo: Temos uma régua com as especificações 1Kg = 1Lt. E queremos adicionar a informação de que 1Und = 650g. Teremos que trocar o valor de "1Und" para "XUnd" de forma a não atrapalhar a equivalência já existente. O cálculo de X é feito internamente e aplicado na régua.<br>
   *
   * @param newValue Valor da nova relação. 1 (de 1Und) do no exemplo.
   * @param newMeasureUnit Unidade de medida da nova relação. Und do nosso exemplo.
   * @param refValue Valor da unidade referênciada. 650 do nosso exemplo.
   * @param refMeasureUnit Unidade de medida referênciada. 'g' no nosso exemplo.
   * @throws RFWException Em caso de falhar ou, se a nova unidade já existir na régua. Se a unidade referênciada não existir na régua.
   */
  public default void addComparativeEquivalence(BigDecimal newValue, MeasureUnit newMeasureUnit, BigDecimal refValue, MeasureUnit refMeasureUnit) throws RFWException {
    PreProcess.requiredNonNullCritical(newValue, "Todos os parametros devem ser diferentes de nulo!");
    PreProcess.requiredNonNullCritical(newMeasureUnit, "Todos os parametros devem ser diferentes de nulo!");
    PreProcess.requiredNonNullCritical(refValue, "Todos os parametros devem ser diferentes de nulo!");
    PreProcess.requiredNonNullCritical(refMeasureUnit, "Todos os parametros devem ser diferentes de nulo!");

    // Valida a unidade de referência existe na régua
    final BigDecimal existRefRatio = MeasureRuler.getRatio(this, refMeasureUnit);
    PreProcess.requiredNonNull(existRefRatio, "É esperado que a unidade de medida de referência já exista na régua!");

    // Valida se a nova unidade ainda não existe na régua
    final BigDecimal existNewRatio = MeasureRuler.getRatio(this, newMeasureUnit);
    PreProcess.requiredNull(existNewRatio, "A unidade de medida a ser adicionada não deve existir na régua!");

    // Passo 1: Calculamos quantas vezes o valor passado como referência é maior que o valor atualmente na régia.
    BigDecimal ratio = refValue.divide(existRefRatio, 10, RFW.getRoundingMode());

    // Passo 2: Ajustamos o valor da nova unidade de medida para ficar na mesma proporção que as comparações existentes na régua atualmente
    newValue = newValue.divide(ratio, 10, RFW.getRoundingMode());

    // Incluímos o novo valor e sua unidade de medida na régua de comparação
    getMeasureUnitHash().put(newMeasureUnit, newValue);
  }

}
