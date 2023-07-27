package br.eng.rodrigogml.rfw.kernel.measureruler.interfaces;

import java.math.BigDecimal;
import java.util.HashMap;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.measureruler.MeasureRuler;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

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

  /**
   * Permite incluir uma compara��o nesta r�gua de equival�ncias sem atrapalhar o conte�do atual.<br>
   * Por exemplo: Temos uma r�gua com as especifica��es 1Kg = 1Lt. E queremos adicionar a informa��o de que 1Und = 650g. Teremos que trocar o valor de "1Und" para "XUnd" de forma a n�o atrapalhar a equival�ncia j� existente. O c�lculo de X � feito internamente e aplicado na r�gua.<br>
   *
   * @param newValue Valor da nova rela��o. 1 (de 1Und) do no exemplo.
   * @param newMeasureUnit Unidade de medida da nova rela��o. Und do nosso exemplo.
   * @param refValue Valor da unidade refer�nciada. 650 do nosso exemplo.
   * @param refMeasureUnit Unidade de medida refer�nciada. 'g' no nosso exemplo.
   * @throws RFWException Em caso de falhar ou, se a nova unidade j� existir na r�gua. Se a unidade refer�nciada n�o existir na r�gua.
   */
  public default void addComparativeEquivalence(BigDecimal newValue, MeasureUnit newMeasureUnit, BigDecimal refValue, MeasureUnit refMeasureUnit) throws RFWException {
    PreProcess.requiredNonNullCritical(newValue, "Todos os parametros devem ser diferentes de nulo!");
    PreProcess.requiredNonNullCritical(newMeasureUnit, "Todos os parametros devem ser diferentes de nulo!");
    PreProcess.requiredNonNullCritical(refValue, "Todos os parametros devem ser diferentes de nulo!");
    PreProcess.requiredNonNullCritical(refMeasureUnit, "Todos os parametros devem ser diferentes de nulo!");

    // Valida a unidade de refer�ncia existe na r�gua
    final BigDecimal existRefRatio = MeasureRuler.getRatio(this, refMeasureUnit);
    PreProcess.requiredNonNull(existRefRatio, "� esperado que a unidade de medida de refer�ncia j� exista na r�gua!");

    // Valida se a nova unidade ainda n�o existe na r�gua
    final BigDecimal existNewRatio = MeasureRuler.getRatio(this, newMeasureUnit);
    PreProcess.requiredNull(existNewRatio, "A unidade de medida a ser adicionada n�o deve existir na r�gua!");

    // Passo 1: Calculamos quantas vezes o valor passado como refer�ncia � maior que o valor atualmente na r�gia.
    BigDecimal ratio = refValue.divide(existRefRatio, 10, RFW.getRoundingMode());

    // Passo 2: Ajustamos o valor da nova unidade de medida para ficar na mesma propor��o que as compara��es existentes na r�gua atualmente
    newValue = newValue.divide(ratio, 10, RFW.getRoundingMode());

    // Inclu�mos o novo valor e sua unidade de medida na r�gua de compara��o
    getMeasureUnitHash().put(newMeasureUnit, newValue);
  }

}
