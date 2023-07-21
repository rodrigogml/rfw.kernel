package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.dataformatters.LocaleConverter.ROUNDPOLICY;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: DataFormatter de n�meros inteiros.<br>
 * Esta classe baseia-se apenas na classe pai em valida��es e considera��es. A �nica diferen�a � que o objeto retornado para o bean tem de ser um inteiro, assim, depois das valida��es da classe pai n�s verificamos se � poss�vel converter em um valor inteiro.
 *
 * @author Rodrigo Leit�o
 * @since 4.1.0 (23/06/2011)
 */
public class RFWLongDataFormatter extends RFWNumberDataFormatter {

  /**
   * Para validadores usados com grande frequ�ncia.
   *
   * @param minvalue Valor m�nimo que pode ser atribuido no campo.
   * @param maxvalue Valor m�ximo que pode ser atribuido no campo.
   * @param ignoresignal Caso true, ignora o sinal, deixando sempre o valor positivo (valor absoluto do n�mero)
   */
  public RFWLongDataFormatter(Long minvalue, Long maxvalue, Boolean ignoresignal) {
    super(ROUNDPOLICY.BESTROUND, 0, minvalue != null ? new Double(minvalue) : null, maxvalue != null ? new Double(maxvalue) : null, ignoresignal);
  }

  /**
   * Cria uma inst�ncia que permite zero e qualquer n�mero positivo at� o limite de Long.MAX_VALUE.
   */
  public static RFWLongDataFormatter createInstanceZeroAndPositive() {
    return new RFWLongDataFormatter(0L, Long.MAX_VALUE, true);
  }

  /**
   * Cria uma inst�ncia que permite zero e qualquer n�mero positivo at� o limite de Long.MAX_VALUE.
   */
  public static RFWLongDataFormatter createInstance() {
    return new RFWLongDataFormatter(Long.MIN_VALUE, Long.MAX_VALUE, false);
  }

  @Override
  public Object toVO(String formattedvalue, Locale locale) throws RFWException {
    Double vn = (Double) super.toVO(formattedvalue, locale);
    Long ret = null;
    if (vn != null) {
      if (vn > Long.MAX_VALUE) {
        ret = Long.MAX_VALUE;
      } else if (vn < Long.MIN_VALUE) {
        ret = Long.MIN_VALUE;
      } else {
        ret = vn.longValue();
      }
      if (ret < this.minvalue) {
        ret = this.minvalue.longValue();
      } else if (ret > this.maxvalue) {
        ret = this.maxvalue.longValue();
      }
      if (this.ignoresignal) ret = Math.abs(ret);
    }
    return ret;
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    if (value != null && !"".equals(value.toString().trim())) {
      super.validate(value, locale);
      // Se a valida��o do pai for um sucesso, ainda validamos se � um inteiro
      Double vn = processValue(value.toString(), locale);
      if (vn > Long.MAX_VALUE) {
        throw new RFWValidationException("RFW_ERR_300057");
      } else if (vn < Long.MIN_VALUE) {
        throw new RFWValidationException("RFW_ERR_300058");
      }
    }
  }
}
