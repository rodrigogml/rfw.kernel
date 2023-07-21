package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.eng.rodrigogml.rfw.kernel.RFW;

/**
 * Description: Formata um numero qualquer como valor monet�rio.<br>
 * Atualmente este formatador n�o tem diferen�a alguma para o padr�o de n�meros. A existencia desta classe � apenas para que j� fiquem todos os campos centralizados, para uma eventual mudan�a futura na forma��o de valores monet�rios.
 *
 * @author Rodrigo Leit�o
 * @since 5.1.0 (20/10/2012)
 */
public class RFWCurrencyBigDecimalDataFormatter extends RFWBigDecimalDataFormatter {

  public static RFWCurrencyBigDecimalDataFormatter createInstance() {
    return new RFWCurrencyBigDecimalDataFormatter(RFW.getRoundingMode(), null, null, false);
  }

  public static RFWCurrencyBigDecimalDataFormatter createInstancePositive() {
    return new RFWCurrencyBigDecimalDataFormatter(RFW.getRoundingMode(), null, null, true);
  }

  public RFWCurrencyBigDecimalDataFormatter(RoundingMode roundingmode, BigDecimal minvalue, BigDecimal maxvalue, Boolean ignoresignal) {
    this(roundingmode, 2, minvalue, maxvalue, ignoresignal);
  }

  public RFWCurrencyBigDecimalDataFormatter(RoundingMode roundingmode, Integer decimals, BigDecimal minvalue, BigDecimal maxvalue, Boolean ignoresignal) {
    super(roundingmode, decimals, minvalue, maxvalue, ignoresignal);
  }

  public RFWCurrencyBigDecimalDataFormatter(RoundingMode roundingmode, Integer decimals, Integer maxDecimals, BigDecimal minvalue, BigDecimal maxvalue, Boolean ignoresignal) {
    super(roundingmode, decimals, maxDecimals, minvalue, maxvalue, ignoresignal);
  }

}
