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
public class RFWIntegerDataFormatter extends RFWNumberDataFormatter {

  /**
   * Para validadores usados com grande frequ�ncia.
   *
   * @param minvalue Valor m�nimo que pode ser atribuido no campo.
   * @param maxvalue Valor m�ximo que pode ser atribuido no campo.
   * @param ignoresignal Caso true, ignora o sinal, deixando sempre o valor positivo (valor absoluto do n�mero)
   */
  public RFWIntegerDataFormatter(Integer minvalue, Integer maxvalue, Boolean ignoresignal) {
    super(ROUNDPOLICY.BESTROUND, 0, minvalue != null ? new Double(minvalue) : null, maxvalue != null ? new Double(maxvalue) : null, ignoresignal);
  }

  /**
   * Cria uma inst�ncia que permite zero e qualquer n�mero positivo at� o limite de Integer.MAX_VALUE.
   */
  public static RFWIntegerDataFormatter createInstanceZeroAndPositive() {
    return new RFWIntegerDataFormatter(0, Integer.MAX_VALUE, true);
  }

  /**
   * Cria uma inst�ncia que permite zero e qualquer n�mero positivo at� o limite de Integer.MAX_VALUE.
   */
  public static RFWIntegerDataFormatter createInstance() {
    return new RFWIntegerDataFormatter(Integer.MIN_VALUE, Integer.MAX_VALUE, false);
  }

  @Override
  public Object toVO(String formattedvalue, Locale locale) throws RFWException {
    Double vn = (Double) super.toVO(formattedvalue, locale);
    Integer ret = null;
    if (vn != null) {
      if (vn > Integer.MAX_VALUE) {
        ret = Integer.MAX_VALUE;
      } else if (vn < Integer.MIN_VALUE) {
        ret = Integer.MIN_VALUE;
      } else {
        ret = vn.intValue();
      }
      if (this.minvalue != null && ret < this.minvalue) {
        ret = this.minvalue.intValue();
      } else if (this.maxvalue != null && ret > this.maxvalue) {
        ret = this.maxvalue.intValue();
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
      if (vn > Integer.MAX_VALUE) {
        throw new RFWValidationException("RFW_ERR_300057");
      } else if (vn < Integer.MIN_VALUE) {
        throw new RFWValidationException("RFW_ERR_300058");
      }
    }
  }
}
