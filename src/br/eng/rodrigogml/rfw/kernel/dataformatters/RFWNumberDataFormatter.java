package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.dataformatters.LocaleConverter.ROUNDPOLICY;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe padr�o formatadora de n�meros. Permite e trata algumas configura��es<br>
 *
 * @author Rodrigo Leit�o
 * @since 4.1.0 (23/06/2011)
 */
public abstract class RFWNumberDataFormatter implements RFWDataFormatter<String, Object> {

  /**
   * Define a pol�tica de arredondamento do n�mero.
   */
  protected ROUNDPOLICY roundpolicy = null;

  /**
   * Valor m�nimo aceito.
   */
  protected Double minvalue = null;

  /**
   * Valor m�ximo aceito.
   */
  protected Double maxvalue = null;

  /**
   * n�mero de d�gitos fracion�rios.
   */
  protected Integer decimals = null;

  /**
   * Caso true, retornar� sempre um valor positivo, simplesmente ignorando e arrancando o sinal. Como se fosse o m�dulo (ou valor absoluto) do n�mero.<br>
   */
  protected Boolean ignoresignal = null;

  /**
   * Define se usaremos agrupamento de milhares ou n�o.
   */
  protected boolean groupingused = true;

  public RFWNumberDataFormatter(ROUNDPOLICY roundpolicy, Integer decimals, Double minvalue, Double maxvalue, Boolean ignoresignal) {
    this(roundpolicy, decimals, minvalue, maxvalue, ignoresignal, true);
  }

  public RFWNumberDataFormatter(ROUNDPOLICY roundpolicy, Integer decimals, Double minvalue, Double maxvalue, Boolean ignoresignal, boolean groupingused) {
    this.roundpolicy = roundpolicy;
    this.decimals = decimals;
    this.minvalue = minvalue;
    this.maxvalue = maxvalue;
    this.ignoresignal = ignoresignal;
    this.groupingused = groupingused;
  }

  @Override
  public String toPresentation(Object value, Locale locale) {
    String result = "";
    if (value != null && !"".equals(value.toString().trim())) {
      Double db = new Double("" + value);
      if (this.ignoresignal) db = Math.abs(db);
      result = LocaleConverter.formatDouble(db, locale, this.decimals, this.groupingused);
    }
    return result;
  }

  @Override
  public Object toVO(String formattedvalue, Locale locale) throws RFWException {
    return processValue(formattedvalue, locale);
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    if (value != null && !"".equals(value.toString().trim())) {
      Double nv = processValue(value.toString(), locale);
      // Verifica range de valores
      if (this.minvalue != null && nv.doubleValue() < this.minvalue) {
        throw new RFWValidationException("RFW_ERR_300055", new String[] { "" + LocaleConverter.formatDouble(this.minvalue, locale, this.decimals) });
      } else if (this.maxvalue != null && nv.doubleValue() > this.maxvalue) {
        throw new RFWValidationException("RFW_ERR_300056", new String[] { "" + LocaleConverter.formatDouble(this.maxvalue, locale, this.decimals) });
      }
    }
  }

  /**
   * Faz as modifica��es necess�rias ao n�mero, como remover o sinal e arredondamentos. n�o faz valida��es, apenas corre��es.
   *
   * @param value
   * @return
   * @throws RFWException
   */
  protected Double processValue(String value, Locale locale) throws RFWException {
    // Verifica se � objeto num�rico tentando convertelo em double
    Double nv = null;
    if (value != null && !"".equals(value.trim())) {
      try {
        nv = LocaleConverter.parseDouble(value.trim(), locale, ignoresignal, decimals, this.roundpolicy);
      } catch (RFWException e) {
        throw new RFWValidationException("RFW_ERR_300054");
      }
    }
    return nv;
  }

  @Override
  public int getMaxLenght() {
    if (this.maxvalue != null) {
      return ("" + this.maxvalue.longValue()).length();
    } else {
      return ("" + Double.MAX_VALUE).length();
    }
  }
}
