package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe usada para formatar valores BigDecimal.<BR>
 * Esta classe � muito similar (praticamente uma s�sia) da {@link RFWNumberDataFormatter}, s� n�o pudemos estender aquela classe por o processamento dela � todo baseado em Double, o que continuaria causando os problemas de arredondamento evitados na implementa��o do BigDecimal.
 *
 * @author Rodrigo Leit�o
 * @since 5.1.0 (20/10/2012)
 */
public class RFWBigDecimalDataFormatter implements RFWDataFormatter<String, Object> {

  public static RFWBigDecimalDataFormatter createInstanceMaxTreeDigitsNoSignal() {
    return new RFWBigDecimalDataFormatter(RFW.getRoundingMode(), 0, 3, null, null, true);
  }

  public static RFWBigDecimalDataFormatter createInstanceMaxTwoDigitsNoSignal() {
    return new RFWBigDecimalDataFormatter(RFW.getRoundingMode(), 0, 2, null, null, true);
  }

  public static RFWBigDecimalDataFormatter createInstanceTreeDigitsNoSignal() {
    return new RFWBigDecimalDataFormatter(RFW.getRoundingMode(), 3, null, null, true);
  }

  public static RFWBigDecimalDataFormatter createInstanceTwoDigitsNoSignal() {
    return new RFWBigDecimalDataFormatter(RFW.getRoundingMode(), 2, null, null, true);
  }

  public static RFWBigDecimalDataFormatter createInstanceOneDigitNoSignal() {
    return new RFWBigDecimalDataFormatter(RFW.getRoundingMode(), 1, null, null, true);
  }

  /**
   * n�mero de d�gitos fracion�rios.
   */
  private Integer decimals = null;

  /**
   * n�mero m�ximo de d�gitos fracion�rios.
   */
  private Integer maxDecimals = null;

  /**
   * Define a pol�tica de arredondamento do n�mero.
   */
  private RoundingMode roundingmode = null;

  /**
   * Caso true, retornar� sempre um valor positivo, simplesmente ignorando e arrancando o sinal. Como se fosse o m�dulo (ou valor absoluto) do n�mero.<br>
   */
  private Boolean ignoresignal = null;

  /**
   * Valor m�nimo aceito.
   */
  private BigDecimal minvalue = null;

  /**
   * Valor m�ximo aceito.
   */
  private BigDecimal maxvalue = null;

  public RFWBigDecimalDataFormatter(RoundingMode roundingmode, Integer decimals, BigDecimal minvalue, BigDecimal maxvalue, Boolean ignoresignal) {
    this(roundingmode, decimals, decimals, minvalue, maxvalue, ignoresignal);
  }

  public RFWBigDecimalDataFormatter(RoundingMode roundingmode, Integer decimals, Integer maxDecimals, BigDecimal minvalue, BigDecimal maxvalue, Boolean ignoresignal) {
    this.roundingmode = roundingmode;
    this.decimals = decimals;
    this.maxDecimals = maxDecimals;
    this.minvalue = minvalue;
    this.maxvalue = maxvalue;
    this.ignoresignal = ignoresignal;
  }

  @Override
  public String toPresentation(Object value, Locale locale) {
    String result = "";
    if (value != null && !"".equals(value.toString().trim())) {
      BigDecimal bigvalue = null;
      if (value instanceof BigDecimal) {
        bigvalue = (BigDecimal) value;
      } else {
        bigvalue = new BigDecimal(value.toString());
      }

      if (this.maxDecimals != null) {
        bigvalue = bigvalue.setScale(this.maxDecimals, this.roundingmode);
      } else {
        bigvalue = bigvalue.setScale(this.decimals, this.roundingmode);
      }
      if (this.maxDecimals != null) {
        result = LocaleConverter.formatBigDecimal(bigvalue, locale, this.decimals, this.maxDecimals);
      } else {
        result = LocaleConverter.formatBigDecimal(bigvalue, locale, this.decimals);
      }
    }
    return result;
  }

  @Override
  public BigDecimal toVO(String formattedvalue, Locale locale) throws RFWException {
    return processValue(formattedvalue, locale);
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    if (value != null && !"".equals(value.toString().trim())) {
      BigDecimal nv = processValue(value.toString(), locale);
      // Verifica range de valores
      if (this.minvalue != null && this.minvalue.compareTo(nv) >= 0) {
        throw new RFWValidationException("RFW_ERR_200031", new String[] { value.toString() });
      } else if (this.maxvalue != null && this.maxvalue.compareTo(nv) <= 0) {
        throw new RFWValidationException("RFW_ERR_200032", new String[] { value.toString() });
      }
    }
  }

  @Override
  public int getMaxLenght() {
    return 25;
  }

  /**
   * Faz as modifica��es necess�rias ao n�mero, como remover o sinal e arredondamentos. n�o faz valida��es, apenas corre��es.
   *
   * @param value
   * @return
   * @throws RFWException
   */
  protected BigDecimal processValue(String value, Locale locale) throws RFWException {
    // Verifica se � objeto num�rico tentando convertelo em double
    BigDecimal nv = null;
    if (value != null && !"".equals(value)) {
      try {
        if (this.maxDecimals != null) {
          nv = LocaleConverter.parseBigDecimal(value, locale, ignoresignal, maxDecimals, this.roundingmode);
        } else {
          nv = LocaleConverter.parseBigDecimal(value, locale, ignoresignal, decimals, this.roundingmode);
        }
      } catch (RFWException e) {
        throw e;
      } catch (Exception e) {
        throw new RFWValidationException("RFW_ERR_200030", e);
      }
    }
    return nv;
  }
}
