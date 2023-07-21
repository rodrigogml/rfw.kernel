package br.eng.rodrigogml.rfw.kernel.dataformatters;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWRunTimeException;

/**
 * Description: Classe que formata dados de Data e Hor�rio. n�o valida pois n�o � utilizado em campos, s� para formata��o em relat�rios e campos de exibi��o.<br>
 * Esta classe aceita tanto o LocaleDate quanto o LocalDateTime. Embora o 'output' de formata��o dependa do tipo da instancia obtido.
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (13/12/2014)
 */
public class RFWDateTimeDataFormatter implements RFWDataFormatter<String, Object> {

  /**
   * Define o modelo de formata��o.<br>
   * Utilizei como int, para n�o criar uma enum que seria utilizada s� dentro desta classe.<br>
   * 0 = Data e Hora; 1 = Data
   */
  private int mode = 0;

  private RFWDateTimeDataFormatter(int mode) {
    this.mode = mode;
  }

  /**
   * Recupera uma instancia que formata o valor no formato de Data e Hora.
   */
  public static RFWDateTimeDataFormatter createDateTimeInstance() {
    return new RFWDateTimeDataFormatter(0);
  }

  /**
   * Recupera uma instancia que formata o valor no formato de Data.
   */
  public static RFWDateTimeDataFormatter createDateInstance() {
    return new RFWDateTimeDataFormatter(1);
  }

  @Override
  public String toPresentation(Object value, Locale locale) {
    String result = "";
    if (value != null) {
      // Sempre convertemos para o LocalDateTime para simplificar e trabalhar com uma classe s�
      if (value instanceof LocalDate) {
        value = ((LocalDate) value).atStartOfDay();
      } else if (value instanceof Timestamp) {
        value = ((Timestamp) value).toLocalDateTime();
      }

      if (value instanceof LocalDateTime) {
        if (this.mode == 0) { // Data e Hora
          result = ((LocalDateTime) value).format(RFW.getDateTimeFormattter());
        } else if (this.mode == 1) { // Data
          result = ((LocalDateTime) value).format(RFW.getDateFormattter());
        }
      } else {
        throw new RFWRunTimeException("O RFWDataTimeDataFormatter n�o sabe trabalhar com a classe '" + value.getClass().getCanonicalName() + "'.");
      }
    }
    return result;
  }

  @Override
  public Object toVO(String formattedvalue, Locale locale) {
    throw new RFWRunTimeException("O RFWDateTimeDataFormatter n�o foi implementado para realizar o parser de Datas, apenas para formatar para relat�rios e campos de exibi��o (como Grids, etc.)");
  }

  @Override
  public void validate(Object value, Locale locale) throws RFWException {
    throw new RFWRunTimeException("O RFWDateTimeDataFormatter n�o foi implementado para realizar o parser de Datas, apenas para formatar para relat�rios e campos de exibi��o (como Grids, etc.)");
  }

  @Override
  public int getMaxLenght() {
    return 19;
  }
}
