package br.eng.rodrigogml.rfw.kernel.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilitária utilizada para conversão entre tipos de dados.<br>
 * Os métodos dessa classe são organizados da seguinte forma de acordo com seu prefixo:<br>
 * <li><b>parse</b> - Indica métodos que extraem ou convertem valores entre tipos, podendo ou não considerar {@link Locale} para uma correta interpretação dos dados.</li>
 * <li><b>format</b> - Faz o inverso do método 'parse'. Converte o valor de um tipo de dado em uma string formatada para leitura do usuário, podendo considerar o Locale ou não.</li> <br>
 * Para métodos de geração de valores verifique {@link RUGenerators}.
 *
 * @author Rodrigo Leito
 * @since (21 de fev. de 2025)
 */
public class RUTypes {

  /**
   * Classe utilitária exclusivamente estática
   */
  private RUTypes() {
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date apenas no horário com o pattern '23:59:59', ignorando qualquer que seja a data.
   *
   * @param time
   * @return
   */
  public static String formatTimeHourMinuteSecond(Date time) {
    return new SimpleDateFormat("HH:mm:ss").format(time);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date em um TimeStamp com o pattern 'yyyyMMddHHmmss'.
   *
   * @param date
   * @return
   */
  public static String formatDateTimeYearMonthDayHourMinuteSecond(Date date) {
    return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o LocalDateTime em um TimeStamp com o pattern 'yyyyMMddHHmmss'.
   *
   * @param date Data/hora a ser formatada
   * @return String com a data formatada
   */
  public static String formatDateTimeYearMonthDayHourMinuteSecond(LocalDateTime date) {
    return formatLocalDateTime(date, "yyyyMMddHHmmss");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date em um TimeStamp com o pattern 'yyyyMMdd'.
   *
   * @param date
   * @return
   */
  public static String formatDateYearMonthDay(Date date) {
    return new SimpleDateFormat("yyyyMMdd").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o LocalDateTime com o pattern 'yyyyMMdd'.
   *
   * @param date Data/hora a ser formatada
   * @return String com a data formatada
   */
  public static String formatDateYearMonthDay(LocalDateTime date) {
    return formatLocalDateTime(date, "yyyyMMdd");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o LocalDate com o pattern 'yyyyMMdd'.
   *
   * @param date Data a ser formatada
   * @return String com a data formatada
   */
  public static String formatDateYearMonthDay(LocalDate date) {
    return formatLocalDate(date, "yyyyMMdd");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date em um TimeStamp com o pattern 'ddMMyyyy'.
   *
   * @param date
   * @return
   */
  public static String formatDateDayMonthYear(Date date) {
    return new SimpleDateFormat("ddMMyyyy").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o LocalDateTime em um TimeStamp com o pattern 'ddMMyyyy'.
   *
   * @param date Data/hora a ser formatada
   * @return String com a data formatada
   */
  public static String formatDateDayMonthYear(LocalDateTime date) {
    return formatLocalDateTime(date, "ddMMyyyy");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date em um TimeStamp com o pattern 'ddMMyyyyHHmmss'.
   *
   * @param date
   * @return
   */
  public static String formatDateTimeDayMonthYearHourMinuteSecond(Date date) {
    return new SimpleDateFormat("ddMMyyyyHHmmss").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o LocalDateTime em um TimeStamp com o pattern 'ddMMyyyyHHmmss'.
   *
   * @param date Data/hora a ser formatada
   * @return String com a data formatada
   */
  public static String formatDateTimeDayMonthYearHourMinuteSecond(LocalDateTime date) {
    return formatLocalDateTime(date, "ddMMyyyyHHmmss");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date em um formato completo com o pattern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatDateTimeDayMonthYearHourMinuteSecond_Slash(Date date) {
    return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date em um formato completo com o pattern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatDateTimeDayMonthYearHourMinuteSecond_Slash(LocalDateTime date) {
    return formatLocalDateTime(date, "dd/MM/yyyy HH:mm:ss");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date em um formato completo com o pattern 'dd/MM/yyyy'.
   *
   * @param date
   * @return
   */
  public static String formatDateDayMonthYear_Slash(Date date) {
    return new SimpleDateFormat("dd/MM/yyyy").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date em um formato completo com o pattern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatDateDayMonthYear_Slash(LocalDateTime date) {
    return formatLocalDateTime(date, "dd/MM/yyyy");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date em um formato completo com o pattern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatDateDayMonthYear_Slash(LocalDate date) {
    return formatLocalDate(date, "dd/MM/yyyy");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o Date apenas no horário com o pattern '23:59:59', ignorando qualquer que seja a data.
   *
   * @param dLastConsult
   * @return
   */
  public static String formatTimeHourMinuteSecond(long timemillis) {
    return formatTimeHourMinuteSecond(new Date(timemillis));
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o LocalDateTime apenas no horário com o pattern '23:59:59', ignorando qualquer que seja a data.
   *
   * @param dateTime
   * @return
   */
  public static String formatTimeHourMinuteSecond(LocalDateTime dateTime) {
    return formatLocalDateTime(dateTime, "HH:mm:ss");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formatar o LocalTime apenas no horário com o pattern '23:59:59'.
   *
   * @param time
   * @return
   */
  public static String formatTimeHourMinuteSecond(LocalTime time) {
    return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
  }

  /**
   * Formata um {@link LocalDate} baseada em um pattern
   *
   * @param date Data a ser formatada
   * @param pattern Pattern a ser utilizado.
   * @return String com a data no formato desejado.
   */
  public static String formatLocalDate(LocalDate date, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return date.format(formatter);
  }

  /**
   * Formata um {@link LocalDateTime} baseada em um pattern
   *
   * @param date Data/Hora a ser formatada
   * @param pattern Pattern a ser utilizado.
   * @return String com a data no formato desejado.
   */
  public static String formatLocalDateTime(LocalDateTime date, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return date.format(formatter);
  }

  /**
   * Este método formata um LocalDate para com o pattern 'ddMMyyyy'.
   *
   * @param date Objeto LocalDate com a data a formatar.
   * @return String com o dado formatado.
   */
  public static String formatDateDayMonthYear(LocalDate date) {
    return formatLocalDate(date, "ddMMyyyy");
  }

  /**
   * Formata um tempo em milissegundos no formato "HHH:MM:SS'mmm", onde HHH são horas, MM minutos, SS segundos e mmm milissegundos.
   * <p>
   * Os campos zerados são omitidos. Exemplo:
   * </p>
   * <ul>
   * <li>Se não houver horas, o resultado será "MM:SS'mmm".</li>
   * </ul>
   *
   * @param millis Tempo em milissegundos a ser formatado.
   * @return String formatada para leitura humana.
   */
  public static String formatMillisToHuman(long millis) {
    long h = TimeUnit.MILLISECONDS.toHours(millis);
    long m = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
    long s = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
    long ms = millis % 1000;

    return (h > 0 ? String.format("%02d:", h) : "") + (h > 0 || m > 0 ? String.format("%02d:", m) : "") + String.format("%02d'", s) + String.format("%03d\"", ms);
  }

  /**
   * Formata um tempo em milissegundos utilizando um padrão de data/hora.
   *
   * @param pattern Padrão de formatação do {@link SimpleDateFormat}.
   * @param millis Tempo em milissegundos para ser formatado.
   * @return Tempo formatado conforme o padrão informado.
   */
  public static String formatMillis(String pattern, long millis) {
    SimpleDateFormat sf = new SimpleDateFormat(pattern);
    sf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sf.format(new Date(millis));
  }

  /**
   * Interpreta diversos formatos de data e os converte para {@link LocalDateTime}.
   * <p>
   * Os formatos suportados são:
   * <ul>
   * <li>"yyyy-MM-dd'T'HH:mm:ssXXX" Exemplo: "2024-02-20T15:30:00-07:00" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ssZ" Exemplo: "2024-02-20T15:30:00-0700" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ss" Exemplo: "2024-02-20T15:30:00" (UTC sem timezone)</li>
   * <li>"yyyy-MM-dd" Exemplo: "2024-02-20"</li>
   * <li>"dd/MM/yyyy" Exemplo: "20/02/2024"</li>
   * </ul>
   * <p>
   * Quando o timezone está presente na string, ele é ignorado e a data é retornada no horário local. Se não houver timezone, assume-se o fuso horário do sistema.
   *
   * <p>
   * <b>Diferença para {@link #parseLocalDateTime(String, ZoneId)}:</b> Esse método apenas interpreta a data sem fazer conversão de fusos horários.
   * </p>
   *
   * @param date String representando a data.
   * @return {@link LocalDateTime} correspondente.
   * @throws RFWException Se o formato da data não for reconhecido ou se ocorrer um erro de conversão.
   */
  public static LocalDateTime parseLocalDateTime(String date) throws RFWException {
    if (date == null || date.trim().isEmpty()) {
      return null;
    }

    try {
      if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]")) {
        // yyyy-MM-dd (Apenas Data)
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssXXX (Padrão UTC com TimeZone, ex: -07:00)
        return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9][0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssZ (Padrão UTC com TimeZone sem separador, ex: -0700)
        return OffsetDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")).toLocalDateTime();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ss (UTC Sem TimeZone)
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      } else if (date.matches("[0-3][0-9]/[0-1][0-9]/[1-2][0-9]{3}")) {
        // dd/MM/yyyy
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
      } else {
        throw new RFWValidationException("Formato da Data no suportado. Data: '${0}'", new String[] { date });
      }
    } catch (DateTimeParseException e) {
      throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
    }
  }

  /**
   * Interpreta diversos formatos de data e os converte para {@link LocalDateTime}, ajustando a data para o timezone especificado.
   * <p>
   * Os formatos suportados são os mesmos do método {@link #parseLocalDateTime(String)}, porém, caso a data contenha um timezone, ele será convertido para o {@link ZoneId} fornecido.
   * </p>
   *
   * <p>
   * <b>Diferença para {@link #parseLocalDateTime(String)}:</b> Esse método converte o horário para o fuso horário recebido como argumento.
   * </p>
   *
   * @param date String representando a data.
   * @param zoneID O {@link ZoneId} para o qual a data deve ser convertida.
   * @return {@link LocalDateTime} correspondente no timezone especificado.
   * @throws RFWException Se o formato da data não for reconhecido ou se ocorrer um erro de conversão.
   */
  public static LocalDateTime parseLocalDateTime(String date, ZoneId zoneID) throws RFWException {
    if (date == null || date.trim().isEmpty()) {
      return null;
    }

    try {
      if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]")) {
        // yyyy-MM-dd (Apenas Data)
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(zoneID).toLocalDateTime();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssXXX (Padrão UTC com TimeZone, ex: -07:00)
        return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).atZoneSameInstant(zoneID).toLocalDateTime();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9][0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssZ (Padrão UTC com TimeZone sem separador, ex: -0700)
        return OffsetDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")).atZoneSameInstant(zoneID).toLocalDateTime();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ss (UTC Sem TimeZone)
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(zoneID).toLocalDateTime();
      } else if (date.matches("[0-3][0-9]/[0-1][0-9]/[1-2][0-9]{3}")) {
        // dd/MM/yyyy
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay(zoneID).toLocalDateTime();
      } else {
        throw new RFWValidationException("Formato da Data no suportado. Data: '${0}'", new String[] { date });
      }
    } catch (DateTimeParseException e) {
      throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
    }
  }

  /**
   * Interpreta diversos formatos de data e os converte para {@link Date}.
   * <p>
   * Os formatos suportados são:
   * <ul>
   * <li>"yyyy-MM-dd'T'HH:mm:ssXXX" Exemplo: "2024-02-20T15:30:00-07:00" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ssZ" Exemplo: "2024-02-20T15:30:00-0700" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ss" Exemplo: "2024-02-20T15:30:00" (UTC sem timezone)</li>
   * <li>"yyyy-MM-dd" Exemplo: "2024-02-20"</li>
   * <li>"dd/MM/yyyy" Exemplo: "20/02/2024"</li>
   * </ul>
   * <p>
   * Se um timezone for especificado na string, ele será utilizado para calcular o timestamp UTC. Caso contrário, assume-se o fuso horário do sistema.
   *
   * <p>
   * <b>Diferença para {@link #parseDate(String, ZoneId)}:</b> Esse método apenas faz o parser da data sem conversão de timezone.
   * </p>
   *
   * @param date String representando a data.
   * @return {@link Date} correspondente.
   * @throws RFWException Se o formato da data não for reconhecido ou se ocorrer um erro de conversão.
   */
  public static Date parseDate(String date) throws RFWException {
    return parseDate(date, RFW.getZoneId());
  }

  /**
   * Este método utiliza o SimpleDateFormat para realizar o parse de uma Date e j tratar a exception.
   *
   * @param value
   * @return
   */
  public static Date parseDate(String pattern, String value) throws RFWException {
    try {
      return new SimpleDateFormat(pattern).parse(value);
    } catch (ParseException e) {
      throw new RFWCriticalException("BISERP_000465", e);
    }
  }

  /**
   * Interpreta diversos formatos de data e os converte para {@link Date}, ajustando a data para o timezone especificado.
   * <p>
   * Os formatos suportados são os mesmos do método {@link #parseDate(String)}, porém, caso a data contenha um timezone, ele será convertido para o {@link ZoneId} fornecido.
   * </p>
   *
   * <p>
   * <b>Diferença para {@link #parseDate(String)}:</b> Esse método converte o horário para o fuso horário recebido como argumento.
   * </p>
   *
   * @param date String representando a data.
   * @param zoneID O {@link ZoneId} para o qual a data deve ser convertida.
   * @return {@link Date} correspondente no timezone especificado.
   * @throws RFWException Se o formato da data não for reconhecido ou se ocorrer um erro de conversão.
   */
  public static Date parseDate(String date, ZoneId zoneID) throws RFWException {
    if (date == null || date.trim().isEmpty()) {
      return null;
    }

    try {
      if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]")) {
        // yyyy-MM-dd (Apenas Data)
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        return Date.from(localDate.atStartOfDay(zoneID).toInstant());
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssXXX (UTC com TimeZone ex: -07:00)
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return Date.from(offsetDateTime.atZoneSameInstant(zoneID).toInstant());
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9][0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssZ (UTC com TimeZone sem separador ex: -0700)
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        return Date.from(offsetDateTime.atZoneSameInstant(zoneID).toInstant());
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ss (Sem TimeZone)
        LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return Date.from(localDateTime.atZone(zoneID).toInstant());
      } else if (date.matches("[0-3][0-9]/[0-1][0-9]/[1-2][0-9]{3}")) {
        // dd/MM/yyyy
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return Date.from(localDate.atStartOfDay(zoneID).toInstant());
      } else {
        throw new RFWValidationException("Formato da Data no suportado. Data: '${0}'", new String[] { date });
      }
    } catch (DateTimeParseException e) {
      throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
    }
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'yyyyMMddHHmmss'.
   *
   * @param value
   * @return
   */
  public static Date parseFromyyyyMMddHHmmss(String value) throws RFWException {
    try {
      return new SimpleDateFormat("yyyyMMddHHmmss").parse(value);
    } catch (ParseException e) {
      throw new RFWCriticalException("Falha ao fazer o parse da data '${0}'!", e);
    }
  }

  /**
   * Realiza o parser da String utilizando o {@link DateTimeFormatter}.<Br>
   *
   * @param date Data no formato String que precisa ser lida.
   * @param pattern no Formado especificado pela documentação do método {@link DateTimeFormatter#ofPattern(String)}.
   * @return Objeto Com o a Data.
   * @throws RFWException
   */
  public static LocalDate parseLocalDate(String date, String pattern) throws RFWException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return LocalDate.parse(date, formatter);
  }

  /**
   * Interpreta diversos formatos de data e os converte para {@link LocalDate}.
   * <p>
   * Os formatos suportados são:
   * <ul>
   * <li>"yyyy-MM-dd'T'HH:mm:ssXXX" Exemplo: "2024-02-20T15:30:00-07:00" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ssZ" Exemplo: "2024-02-20T15:30:00-0700" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ss" Exemplo: "2024-02-20T15:30:00" (UTC sem timezone)</li>
   * <li>"yyyy-MM-dd" Exemplo: "2024-02-20"</li>
   * <li>"dd/MM/yyyy" Exemplo: "20/02/2024"</li>
   * </ul>
   * <p>
   * Se um timezone for especificado na string, ele será utilizado apenas para conversão do timestamp UTC, mas o retornão será um {@link LocalDate} sem informações de horário.
   *
   * <p>
   * <b>Diferença para {@link #parseLocalDate(String, ZoneId)}:</b> Esse método apenas faz o parser da data sem conversão de timezone.
   * </p>
   *
   * @param date String representando a data.
   * @return {@link LocalDate} correspondente.
   * @throws RFWException Se o formato da data não for reconhecido ou se ocorrer um erro de conversão.
   */
  public static LocalDate parseLocalDate(String date) throws RFWException {
    return parseLocalDate(date, RFW.getZoneId());
  }

  /**
   * Interpreta múltiplos formatos de data e os converte para {@link LocalDate}.
   * <p>
   * Quando a string contém informação de fuso horário, a data/hora é convertida para o {@link ZoneId} informado antes de descartar a parte de horário. Quando não há fuso horário, a data/hora é assumida como pertencente ao {@link ZoneId} informado.
   * </p>
   *
   * <p>
   * <b>Diferença em relação a {@link #parseLocalDate(String)}:</b> este método ajusta a data/hora para o fuso horário recebido como argumento antes de extrair a data.
   * </p>
   *
   * @param date String representando a data.
   * @param zoneID {@link ZoneId} para conversão antes da extração da data.
   * @return {@link LocalDate} sem informações de horário, ou {@code null} se a string for nula ou vazia.
   * @throws RFWException Se o formato da data não for reconhecido ou ocorrer erro de conversão.
   */
  public static LocalDate parseLocalDate(String date, ZoneId zoneID) throws RFWException {
    if (date == null || date.trim().isEmpty()) {
      return null;
    }

    try {
      if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]")) {
        // yyyy-MM-dd
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssXXX
        return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).atZoneSameInstant(zoneID).toLocalDate();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9][0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssZ
        return OffsetDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")).atZoneSameInstant(zoneID).toLocalDate();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]\\.[0-9]{1,9}")) {
        // yyyy-MM-dd'T'HH:mm:ss.SSS (ou nanos)
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(zoneID).toLocalDate();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ss
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(zoneID).toLocalDate();
      } else if (date.matches("[0-3][0-9]/[0-1][0-9]/[1-2][0-9]{3}")) {
        // dd/MM/yyyy
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
      } else {
        throw new RFWValidationException("Formato da data não suportado. Data: '${0}'", new String[] { date });
      }
    } catch (DateTimeParseException e) {
      throw new RFWCriticalException("Falha ao realizar o parse da data. Data '${0}'.", new String[] { date }, e);
    }
  }

  /**
   * Converte um {@link LocalDate} para {@link Date} utilizando a Zona do Sistema {@link RFW#getZoneId()}.<br>
   * considerada a hora zero do dia passaro na conversão para a Zona.
   *
   * @param date Valor de Entrada a ser convertido
   * @return Valor Convertido
   */
  public static Date parseDate(LocalDate date) {
    return Date.from(date.atStartOfDay().atZone(RFW.getZoneId()).toInstant());
  }

  /**
   * Converte um {@link LocalDate} para {@link Date} utilizando uma Zona personalizada.<br>
   * considerada a hora zero do dia passaro na conversão para a Zona.
   *
   * @param date Valor de Entrada a ser convertido
   * @param zone Zona a ser utilizada.
   * @return Valor Convertido
   */
  public static Date parseDate(LocalDate date, ZoneId zone) {
    return Date.from(date.atStartOfDay().atZone(zone).toInstant());
  }

  /**
   * Converte um {@link LocalDateTime} para {@link Date} utilizando a Zona do Sistema {@link RFW#getZoneId()}.
   *
   * @param dateTime Valor de Entrada a ser convertido
   * @return Valor Convertido ou nulo caso o valor de entrada seja nulo.
   */
  public static Date parseDate(LocalDateTime dateTime) {
    if (dateTime == null) return null;
    return Date.from(dateTime.atZone(RFW.getZoneId()).toInstant());
  }

  /**
   * Converte um {@link LocalDateTime} para {@link Date} utilizando uma Zona personalizada.
   *
   * @param dateTime Valor de Entrada a ser convertido
   * @param zone Zona a ser utilizada.
   * @return Valor Convertido
   */
  public static Date parseDate(LocalDateTime dateTime, ZoneId zone) {
    return Date.from(dateTime.atZone(zone).toInstant());
  }

  /**
   * Converte um {@link Date} em {@link LocalDate}. Utiliza a Zona padrão do sistema
   *
   * @param date Data a ser convertida em LocalDate
   * @return LocalDate conforme a zona, ou nulo caso a entrada seja nula.
   */
  public static LocalDate parseLocalDate(Date date) {
    if (date == null) return null;
    return date.toInstant().atZone(RFW.getZoneId()).toLocalDate();
  }

  /**
   * Converte um {@link Date} em {@link LocalDate}.
   *
   * @param date Data a ser convertida em LocalDate
   * @param zone Zone para correta conversão entre objetos temporais.
   * @return LocalDate conforme a zona, ou nulo caso date == null;
   */
  public static LocalDate parseLocalDate(Date date, ZoneId zone) {
    if (date == null) return null;
    return date.toInstant().atZone(zone).toLocalDate();
  }

  /**
   * Converte um {@link java.sql.Date} em {@link LocalDate}. Utiliza a Zona padrão do sistema
   *
   * @param date Data a ser convertida em LocalDate
   * @return LocalDate conforme a zona ou null caso a entrada seja nula.
   */
  public static LocalDate parseLocalDate(java.sql.Date date) {
    if (date == null) return null;
    return date.toLocalDate();
  }

  /**
   * Converte um {@link LocalDateTime} em {@link LocalDate}.
   *
   * @param date Data a ser convertida em LocalDate
   * @return LocalDate conforme o valor de entrada, ou nulo caso a entrada seja nula.
   */
  public static LocalDate parseLocalDate(LocalDateTime date) {
    if (date == null) return null;
    return date.toLocalDate();
  }

  /**
   * Converte a {@link Timestamp} recebida para o LocalDate<br>
   *
   * @param stamp Data a ser convertida.
   * @return Objeto com o dia/horário convertido para a zona solicitada, ou nulo se receber uma entrada nula.
   * @throws RFWException
   */
  public static LocalDate parseLocalDate(Timestamp stamp) throws RFWException {
    if (stamp == null) return null;
    return stamp.toLocalDateTime().toLocalDate();
  }

  /**
   * Converte um {@link Date} em {@link LocalDateTime}. Utiliza a Zona padrão do sistema {@link RFWDeprec#getZoneId()}.
   *
   * @param date Data a ser convertida em LocalDateTime
   * @return LocalDateTime conforme a zona, ou nulo se receber o valor nulo como parmetro.
   */
  public static LocalDateTime parseLocalDateTime(Date date) {
    if (date == null) return null;
    return date.toInstant().atZone(RFW.getZoneId()).toLocalDateTime();
  }

  /**
   * Converte um {@link Date} em {@link LocalDateTime}.
   *
   * @param date Data a ser convertida em LocalDateTime
   * @param zone Zone para correta conversão entre objetos temporais.
   * @return LocalDateTime conforme a zona, ou nulo caso a entrada seja nula.
   */
  public static LocalDateTime parseLocalDateTime(Date date, ZoneId zone) {
    if (date == null) return null;
    return date.toInstant().atZone(zone).toLocalDateTime();
  }

  /**
   * Converte a {@link Timestamp} recebida para o LocalDateTime<br>
   *
   * @param stamp DataHora a ser convertida.
   * @return Objeto com o dia/horário convertido para a zona solicitada, ou nulo se receber uma entrada nula.
   * @throws RFWException
   */
  public static LocalDateTime parseLocalDateTime(Timestamp stamp) throws RFWException {
    if (stamp == null) return null;
    return stamp.toLocalDateTime();
  }

  /**
   * Formata um número decimal para String com o número de casas decimais especificado, usando o Locale informado.
   *
   * @param number Nmero a ser formatado
   * @param locale Localização usada para formatação
   * @param maxDecimals Nmero mximo de casas decimais
   * @return Representação em String formatada do número
   */
  public static String formatDecimalWithoutTrailingZeros(BigDecimal number, Locale locale, int maxDecimals) {
    if (number == null) {
      return "";
    }
    number = number.stripTrailingZeros();
    String pattern = RUString.completeUntilLengthRight("#", "0.", maxDecimals + 2);
    DecimalFormat df = new DecimalFormat(pattern, new DecimalFormatSymbols(locale));
    return df.format(number);
  }

  /**
   * Converte de forma segura um valor genrico para {@link Integer}.<br>
   * <p>
   * Regras de conversão:
   * <ul>
   * <li>Se o valor for {@code null}, retorna {@code null}.</li>
   * <li>Se o valor j for {@link Integer}, retornado diretamente.</li>
   * <li>Se o valor for {@link Number}, delega para {@link #parseInteger(Number)}.</li>
   * <li>Se o valor for {@link String}, delega para {@link #parseInteger(String)}.</li>
   * <li>Se o valor for {@link Boolean}, retorna {@code 1} para {@code true} e {@code 0} para {@code false}.</li>
   * </ul>
   * <p>
   * Caso o tipo não seja suportado, uma {@link RFWValidationException} lançada.<br>
   * Em situações de falha interna de conversão (por exemplo, falha em parsers de baixo nvel), uma {@link RFWCriticalException} poder ser propagada pelos métodos delegados.
   *
   * @param value Valor de entrada a ser convertido.
   * @return Valor convertido em {@link Integer} ou {@code null} caso o valor de entrada seja {@code null} ou {@link String} vazia.
   * @throws RFWException Em caso de erro de validação ou falha crítica de conversão.
   */
  public static Integer parseInteger(Object value) throws RFWException {
    // Trata nulo imediatamente
    if (value == null) {
      return null;
    }

    // J Integer, apenas retorna
    if (value instanceof Integer) {
      return (Integer) value;
    }

    // Converte Number por sobrecarga
    if (value instanceof Number) {
      return parseInteger((Number) value);
    }

    // Converte String por sobrecarga
    if (value instanceof String) {
      return parseInteger((String) value);
    }

    // Conveno simples para booleano: true = 1, false = 0
    if (value instanceof Boolean) {
      return ((Boolean) value) ? 1 : 0;
    }

    // Tipo no suportado
    throw new RFWValidationException("Tipo '${0}' no suportado para conversão em Integer.", new String[] { value.getClass().getName() });
  }

  /**
   * Converte de forma segura um {@link String} para {@link Integer}.<br>
   * <p>
   * Regras de conversão:
   * <ul>
   * <li>Se o valor for {@code null} ou vazio (apenas espaços), retorna {@code null}.</li>
   * <li>O formato aceito opcionalmente sinalizado (+/-) seguido apenas de dígitos.</li>
   * <li>Se o formato não for suportado, lança {@link RFWValidationException}.</li>
   * <li>Se ocorrer falha interna no parser (por exemplo, estouro no tratado), lança {@link RFWCriticalException}.</li>
   * <li>Se o valor estiver fora do intervalo de {@link Integer}, lança {@link RFWValidationException}.</li>
   * </ul>
   *
   * @param value Valor textual a ser convertido.
   * @return Valor convertido em {@link Integer} ou {@code null} caso o valor de entrada seja {@code null} ou vazio.
   * @throws RFWException Em caso de erro de validação ou falha crítica de conversão.
   */
  public static Integer parseInteger(String value) throws RFWException {
    // Trata nulo ou vazio como nulo
    if (value == null) {
      return null;
    }

    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      return null;
    }

    // Valida formato aceito: sinal opcional seguido de dígitos
    if (!trimmed.matches("[+-]?[0-9]+")) {
      throw new RFWValidationException("Formato de número inteiro no suportado. Valor: '${0}'", new String[] { value });
    }

    try {
      // Usa Long para detectar facilmente estouro de faixa do int
      long longValue = Long.parseLong(trimmed);

      // Validação de faixa do tipo Integer
      if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
        throw new RFWValidationException("Valor fora do intervalo suportado para Integer. Valor: '${0}'", new String[] { value });
      }

      return (int) longValue;
    } catch (NumberFormatException e) {
      // Falha interna de conversão numrica
      throw new RFWCriticalException("Falha ao converter valor para Integer. Valor '${0}'.", new String[] { value }, e);
    }
  }

  /**
   * Converte de forma segura um {@link Number} para {@link Integer}.<br>
   * <p>
   * Regras de conversão:
   * <ul>
   * <li>Se o valor for {@code null}, retorna {@code null}.</li>
   * <li>Se o valor j for {@link Integer}, retornado diretamente.</li>
   * <li>Para tipos integrais ({@link Long}, {@link Short}, {@link Byte}), feita validação de faixa.</li>
   * <li>Para {@link BigDecimal}, exigido que o valor seja inteiro (sem casas decimais) e dentro da faixa de {@link Integer}.</li>
   * <li>Para {@link Double} e {@link Float}, exigido que o valor não seja NaN/Infinito, seja inteiro (sem parte fracionária) e esteja na faixa de {@link Integer}.</li>
   * </ul>
   * <p>
   * Situações em que o valor não puder ser representado como inteiro sem perda de precisão ou estiver fora da faixa geram {@link RFWValidationException}.
   *
   * @param value Valor numérico a ser convertido.
   * @return Valor convertido em {@link Integer} ou {@code null} caso o valor de entrada seja {@code null}.
   * @throws RFWException Em caso de erro de validação ou falha crítica de conversão interna.
   */
  public static Integer parseInteger(Number value) throws RFWException {
    // Trata nulo
    if (value == null) {
      return null;
    }

    // J Integer
    if (value instanceof Integer) {
      return (Integer) value;
    }

    // Tipos integrais simples (Long, Short, Byte)
    if (value instanceof Long || value instanceof Short || value instanceof Byte) {
      long longValue = value.longValue();
      if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
        throw new RFWValidationException("Valor fora do intervalo suportado para Integer. Valor: '${0}'", new String[] { String.valueOf(value) });
      }
      return (int) longValue;
    }

    // BigDecimal: exige valor inteiro exato e dentro da faixa
    if (value instanceof BigDecimal) {
      BigDecimal bd = ((BigDecimal) value).stripTrailingZeros();
      try {
        long longValue = bd.longValueExact();
        if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
          throw new RFWValidationException("Valor fora do intervalo suportado para Integer. Valor: '${0}'", new String[] { bd.toPlainString() });
        }
        return (int) longValue;
      } catch (ArithmeticException e) {
        // Tem parte fracionária ou está fora da faixa de long
        throw new RFWValidationException("Valor decimal não pode ser convertido para Integer sem perda de precisão. Valor: '${0}'", new String[] { bd.toPlainString() });
      }
    }

    // Double/Float: verifica NaN, infinito, parte fracionária e faixa
    if (value instanceof Double || value instanceof Float) {
      double d = value.doubleValue();

      // NaN ou infinito não são valores válidos
      if (Double.isNaN(d) || Double.isInfinite(d)) {
        throw new RFWValidationException("Valor numérico inválido para conversão em Integer. Valor: '${0}'", new String[] { String.valueOf(value) });
      }

      // Verifica faixa de Integer
      if (d < Integer.MIN_VALUE || d > Integer.MAX_VALUE) {
        throw new RFWValidationException("Valor fora do intervalo suportado para Integer. Valor: '${0}'", new String[] { String.valueOf(value) });
      }

      // Exige que seja inteiro (sem casas decimais)
      if (Math.rint(d) != d) {
        throw new RFWValidationException("Valor decimal não pode ser convertido para Integer sem perda de precisão. Valor: '${0}'", new String[] { String.valueOf(value) });
      }

      return (int) d;
    }

    // Qualquer outro subtipo de Number: tenta via long com validação de faixa
    long longValue = value.longValue();
    if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
      throw new RFWValidationException("Valor fora do intervalo suportado para Integer. Valor: '${0}'", new String[] { String.valueOf(value) });
    }
    return (int) longValue;
  }

  /**
   * Converte uma {@link String} para {@link Long} de forma segura.<br>
   * <br>
   * Regras:
   * <ul>
   * <li>Se o valor for {@code null}, vazio ou em branco, retorna {@code null}.</li>
   * <li>Se o valor não for um número inteiro válido, lança {@link RFWValidationException}.</li>
   * <li>Se ocorrer falha interna no parser numérico, lança {@link RFWCriticalException}.</li>
   * </ul>
   *
   * @param value Valor textual a ser convertido.
   * @return {@link Long} correspondente ou {@code null}.
   * @throws RFWException Se o valor não for válido.
   */
  public static Long parseLong(String value) throws RFWException {
    if (value == null || value.trim().isEmpty()) return null;
    try {
      return Long.valueOf(value.trim());
    } catch (NumberFormatException e) {
      throw new RFWValidationException("Valor inválido para conversão em Long: '${0}'", new String[] { value });
    } catch (Exception e) {
      throw new RFWCriticalException("Falha inesperada ao converter valor '${0}' para Long.", new String[] { value }, e);
    }
  }

  /**
   * Converte um {@link Number} para {@link Long} de forma segura.<br>
   * <br>
   * Regras:
   * <ul>
   * <li>Se o valor for {@code null}, retorna {@code null}.</li>
   * <li>Se o valor for {@link Long}, retorna diretamente.</li>
   * <li>Se o valor for {@link BigDecimal}, deve ser inteiro exato e dentro da faixa de {@link Long}.</li>
   * <li>Se o valor for {@link Double} ou {@link Float}, deve representar número inteiro exato.</li>
   * <li>Outros tipos numéricos (Integer, Short, Byte) são convertidos diretamente.</li>
   * </ul>
   *
   * @param value Valor numérico a ser convertido.
   * @return {@link Long} correspondente ou {@code null}.
   * @throws RFWException Se o valor for inválido ou fora da faixa de {@link Long}.
   */
  public static Long parseLong(Number value) throws RFWException {
    if (value == null) return null;

    if (value instanceof Long) return (Long) value;
    if (value instanceof Integer || value instanceof Short || value instanceof Byte) return value.longValue();

    if (value instanceof BigDecimal) {
      BigDecimal bd = ((BigDecimal) value).stripTrailingZeros();
      try {
        return bd.longValueExact();
      } catch (ArithmeticException e) {
        throw new RFWValidationException("Valor decimal no  inteiro ou está fora da faixa de Long: '${0}'", new String[] { value.toString() });
      }
    }

    if (value instanceof Double || value instanceof Float) {
      double d = value.doubleValue();
      if (Double.isNaN(d) || Double.isInfinite(d)) throw new RFWValidationException("Valor inválido (NaN ou Infinito) para Long: '${0}'", new String[] { value.toString() });
      if (d % 1 != 0) throw new RFWValidationException("Valor no inteiro para conversão em Long: '${0}'", new String[] { value.toString() });
      if (d < Long.MIN_VALUE || d > Long.MAX_VALUE) throw new RFWValidationException("Valor fora da faixa de Long: '${0}'", new String[] { value.toString() });
      return (long) d;
    }

    throw new RFWValidationException("Tipo numérico no suportado para conversão em Long: '${0}'", new String[] { value.getClass().getName() });
  }

  /**
   * Converte um {@link Object} para {@link Long} de forma segura.<br>
   * <br>
   * Regras:
   * <ul>
   * <li>Se o valor for {@code null}, retorna {@code null}.</li>
   * <li>Se for instncia de {@link Number}, usa {@link #parseLong(Number)}.</li>
   * <li>Se for {@link String}, usa {@link #parseLong(String)}.</li>
   * <li>Se for {@link Boolean}, retorna {@code 1L} para {@code true} e {@code 0L} para {@code false}.</li>
   * <li>Qualquer outro tipo lança {@link RFWValidationException}.</li>
   * </ul>
   *
   * @param value Objeto genrico a ser convertido.
   * @return {@link Long} correspondente ou {@code null}.
   * @throws RFWException Se o tipo não for suportado ou o valor for inválido.
   */
  public static Long parseLong(Object value) throws RFWException {
    if (value == null) return null;

    if (value instanceof Number) return parseLong((Number) value);
    if (value instanceof String) return parseLong((String) value);
    if (value instanceof Boolean) return ((Boolean) value) ? 1L : 0L;

    throw new RFWValidationException("Tipo no suportado para conversão em Long: '${0}'", new String[] { value.getClass().getName() });
  }

  /**
   * Formata um valor decimal para percentual com 2 casas decimais.
   * <p>
   * O valor recebido deve estar em formato decimal (ex: 1.0 = 100%, 0.5 = 50%, 0.1234 = 12,34%).<br>
   * Caso o valor seja {@code null}, ser retornado {@code "0%"}.
   * </p>
   *
   * @param value Valor decimal a ser formatado.
   * @return String representando o valor em formato percentual, com 2 casas decimais.
   */
  public static String formatToPercentage(Double value) {
    return formatToPercentage(value, 1);
  }

  /**
   * Formata um valor decimal para percentual com o número de casas decimais especificado.
   * <p>
   * O valor recebido deve estar em formato decimal (ex: 1.0 = 100%, 0.5 = 50%, 0.1234 = 12,34%).<br>
   * Caso o valor seja {@code null}, ser retornado {@code "0%"}.
   * </p>
   *
   * @param value Valor decimal a ser formatado.
   * @param decimals Quantidade de casas decimais desejadas na parte fracionária.
   * @return String representando o valor em formato percentual conforme o número de casas solicitado.
   */
  public static String formatToPercentage(Double value, int decimals) {
    double safeValue = (value == null ? 0.0 : value) * 100.0;

    // Gera o padrão dinamicamente conforme as casas decimais desejadas
    StringBuilder pattern = new StringBuilder("0");
    if (decimals > 0) {
      pattern.append(".");
      for (int i = 0; i < decimals; i++)
        pattern.append("0");
    }
    pattern.append("%");

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    DecimalFormat df = new DecimalFormat(pattern.toString(), symbols);

    return df.format(safeValue / 100.0);
  }

  /**
   * Este método arredonda <b>para o lado mais prximo</b> números decimais (double) com um número mximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals número de casas mximo
   * @return
   */
  public static Double round(double value, int decimals) {
    double factor = Math.pow(10, decimals);
    double result = Math.round(value * factor) / factor;
    return result;
  }

  /**
   * Este método arredonda <b>para baixo</b> números decimais (double) com um número mximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals número de casas mximo
   * @return
   */
  public static Double roundFloor(double value, int decimals) {
    double factor = Math.pow(10, decimals);
    double result = Math.floor(value * factor) / factor;
    return result;
  }

  /**
   * Este método arredonda <b>para cima</b> números decimais (double) com um número mximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals número de casas mximo
   * @return
   */
  public static Double roundCeil(double value, int decimals) {
    double factor = Math.pow(10, decimals);
    double result = Math.ceil(value * factor) / factor;
    return result;
  }

  /**
   * Este método arredonda <b>para o lado mais prximo</b> números decimais (float) com um número mximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals número de casas mximo
   * @return
   */
  public static Float round(float value, int decimals) {
    double factor = Math.pow(10, decimals);
    float result = (float) (Math.round(value * factor) / factor);
    return result;
  }

  /**
   * Este método arredonda <b>para baixo</b> números decimais (float) com um número mximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals número de casas mximo
   * @return
   */
  public static Float roundFloor(float value, int decimals) {
    double factor = Math.pow(10, decimals);
    float result = (float) (Math.floor(value * factor) / factor);
    return result;
  }

  /**
   * Este método arredonda <b>para cima</b> números decimais (float) com um número mximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals número de casas mximo
   * @return
   */
  public static Float roundCeil(float value, int decimals) {
    double factor = Math.pow(10, decimals);
    float result = (float) (Math.ceil(value * factor) / factor);
    return result;
  }

  /**
   * Este método formata um {@link LocalDateTime} no padrão completo "yyyy-MM-dd'T'HH:mm:ssXXX", utilizando o {@link ZoneOffset} informado.
   *
   * @param dateTime Data/hora a ser formatada (no nula)
   * @param offset Offset a ser utilizado (ex: {@link ZoneOffset#UTC})
   * @return String com a data/hora formatada
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime dateTime, ZoneOffset offset) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    return dateTime.atOffset(offset).format(formatter);
  }

  /**
   * Este método formata um {@link Date} no padrão completo {@code "yyyy-MM-dd'T'HH:mm:ssXXX"}, utilizando a zona padrão do sistema {@link RFW#getZoneId()}.
   * <p>
   * Observação: quando o offset resultante for UTC (deslocamento zero), o {@link DateTimeFormatter} do Java imprimir {@code Z} ao final, em vez de {@code +00:00}, conforme a especificação ISO-8601.
   * </p>
   *
   * @param date Data a ser formatada (pode ser nula)
   * @return String com a data/hora formatada ou {@code null} se {@code date} for nulo.
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(Date date) {
    if (date == null) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    return date.toInstant().atZone(RFW.getZoneId()).format(formatter);
  }

  /**
   * Este método formata um {@link Date} no padrão completo {@code "yyyy-MM-dd'T'HH:mm:ssXXX"}, utilizando a {@link ZoneId} informada.
   * <p>
   * Observação: quando o offset resultante for UTC (deslocamento zero), o {@link DateTimeFormatter} do Java imprimir {@code Z} ao final, em vez de {@code +00:00}, conforme a especificação ISO-8601.
   * </p>
   *
   * @param date Data a ser formatada (pode ser nula)
   * @param zoneId Zona a ser utilizada na conversão (no nula)
   * @return String com a data/hora formatada ou {@code null} se {@code date} for nulo.
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(Date date, ZoneId zoneId) {
    if (date == null) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    return date.toInstant().atZone(zoneId).format(formatter);
  }

  /**
   * Este método formata um {@link LocalDateTime} no padrão completo {@code "yyyy-MM-dd'T'HH:mm:ssXXX"}, utilizando a zona padrão do sistema {@link RFW#getZoneId()}.
   * <p>
   * Observação: quando o offset resultante for UTC (deslocamento zero), o {@link DateTimeFormatter} do Java imprimir {@code Z} ao final, em vez de {@code +00:00}, conforme a especificação ISO-8601.
   * </p>
   *
   * @param dateTime Data/hora a ser formatada (pode ser nula)
   * @return String com a data/hora formatada ou {@code null} se {@code dateTime} for nulo.
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime dateTime) {
    if (dateTime == null) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    return dateTime.atZone(RFW.getZoneId()).format(formatter);
  }

  /**
   * Este método formata um {@link LocalDateTime} no padrão completo {@code "yyyy-MM-dd'T'HH:mm:ssXXX"}, utilizando a {@link ZoneId} informada.
   * <p>
   * Observação: quando o offset resultante for UTC (deslocamento zero), o {@link DateTimeFormatter} do Java imprimir {@code Z} ao final, em vez de {@code +00:00}, conforme a especificação ISO-8601.
   * </p>
   *
   * @param dateTime Data/hora a ser formatada (pode ser nula)
   * @param zoneId Zona a ser utilizada na conversão (no nula)
   * @return String com a data/hora formatada ou {@code null} se {@code dateTime} for nulo.
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime dateTime, ZoneId zoneId) {
    if (dateTime == null) return null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    return dateTime.atZone(zoneId).format(formatter);
  }

  /**
   * Formata uma data usando um padrão customizado.
   *
   * <p>
   * O parmetro {@code pattern} segue as regras do {@link java.text.SimpleDateFormat}. Abaixo esto os principais termos que podem ser utilizados no padrão:
   *
   * <ul>
   * <li><b>y</b> Ano (ex.: yyyy = 2025, yy = 25)</li>
   * <li><b>M</b> Ms (ex.: MM = 03, MMM = Mar, MMMM = Maro)</li>
   * <li><b>d</b> Dia do ms (ex.: dd = 09)</li>
   * <li><b>E</b> Dia da semana (ex.: EEE = Seg, EEEE = Segunda-feira)</li>
   * <li><b>H</b> Hora (023, ex.: HH = 17)</li>
   * <li><b>h</b> Hora (112, ex.: hh = 05)</li>
   * <li><b>m</b> Minutos (ex.: mm = 07)</li>
   * <li><b>s</b> Segundos (ex.: ss = 59)</li>
   * <li><b>S</b> Milissegundos (ex.: SSS = 123)</li>
   * <li><b>a</b> AM/PM (ex.: a = PM)</li>
   * <li><b>z</b> Fuso horário (ex.: z = BRT, zzzz = Braslia Standard Time)</li>
   * <li><b>Z</b> Offset numérico do fuso (ex.: -0300)</li>
   * </ul>
   *
   * Exemplos de uso:
   * <ul>
   * <li>{@code formatDate(date, "dd/MM/yyyy")} -> "21/11/2025"</li>
   * <li>{@code formatDate(date, "dd MMM yyyy HH:mm")} -> "21 Nov 2025 14:30"</li>
   * <li>{@code formatDate(date, "EEEE, dd 'de' MMMM 'de' yyyy")} -> "Sexta-feira, 21 de Novembro de 2025"</li>
   * </ul>
   *
   * @param date Data a ser formatada.
   * @param pattern Padrão de formatação.
   * @return String formatada ou {@code null} caso a data seja nula.
   * @throws IllegalArgumentException caso o padrão seja inválido.
   */
  public static String formatDate(Date date, String pattern) {
    if (date == null) {
      return null;
    }
    if (pattern == null || pattern.trim().isEmpty()) {
      throw new IllegalArgumentException("Pattern não pode ser nulo ou vazio");
    }
    return new java.text.SimpleDateFormat(pattern).format(date);
  }

  /**
   * Converte um {@link BigDecimal} em sua representação sem notação cientfica.
   *
   * <p>
   * Retorna {@code null} se o valor informado for {@code null}.
   * </p>
   *
   * @param value valor numérico a ser convertido
   * @return representação textual sem notação cientfica ou {@code null} se o valor for {@code null}
   */
  public static String parseString(BigDecimal value) {
    return value != null ? value.toPlainString() : null;
  }

  /**
   * Converte um {@link Long} em uma {@link String}.
   *
   * <p>
   * Retorna {@code null} caso o valor informado também seja {@code null}.
   * </p>
   *
   * @param value valor long a ser convertido
   * @return representação textual do valor ou {@code null} se o valor for {@code null}
   */
  public static String parseString(Long value) {
    return value != null ? value.toString() : null;
  }

  /**
   * Converte um {@link Integer} em uma {@link String}.
   *
   * <p>
   * Retorna {@code null} caso o valor informado também seja {@code null}.
   * </p>
   *
   * @param value valor inteiro a ser convertido
   * @return representação textual do valor ou {@code null} se o valor for {@code null}
   */
  public static String parseString(Integer value) {
    return value != null ? value.toString() : null;
  }

  /**
   * Converte um {@link Float} em uma {@link String}.
   *
   * <p>
   * Retorna {@code null} caso o valor informado também seja {@code null}.
   * </p>
   *
   * @param value valor float a ser convertido
   * @return representação textual do valor ou {@code null} se o valor for {@code null}
   */
  public static String parseString(Float value) {
    return value != null ? value.toString() : null;
  }

  /**
   * Converte um {@link Double} em uma {@link String}.
   *
   * <p>
   * Retorna {@code null} caso o valor informado também seja {@code null}.
   * </p>
   *
   * @param value valor double a ser convertido
   * @return representação textual do valor ou {@code null} se o valor for {@code null}
   */
  public static String parseString(Double value) {
    return value != null ? value.toString() : null;
  }

  /**
   * Converte de forma segura um {@link String} para {@link BigDecimal}.
   *
   * <p>
   * <b>Regras de conversão:</b>
   * </p>
   * <ul>
   * <li>Se o valor for {@code null} ou vazio (apenas espaços), retorna {@code null}.</li>
   * <li>O formato aceito é:
   * <ul>
   * <li>Sinal opcional (+/-)</li>
   * <li>Parte inteira composta apenas por dígitos</li>
   * <li>Opcionalmente um ponto decimal seguido apenas de dígitos</li>
   * </ul>
   * Ex.: {@code "10"}, {@code "-5"}, {@code "12.34"}, {@code "+0.99"}.</li>
   * <li>NÃO aceita formato com vírgula.</li>
   * <li>Se o formato não for suportado, lança {@link RFWValidationException}.</li>
   * <li>Se ocorrer falha interna no parser (ex.: valor muito grande ou formato inesperado), lança {@link RFWCriticalException}.</li>
   * </ul>
   *
   * @param value Valor textual a ser convertido.
   * @return Instância de {@link BigDecimal} representando o valor ou {@code null} se entrada nula ou vazia.
   * @throws RFWException Em caso de erro de validação ou falha crítica de conversão.
   */
  public static BigDecimal parseBigDecimal(String value) throws RFWException {
    if (value == null) {
      return null;
    }

    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      return null;
    }

    // Regex: sinal opcional, dígitos obrigatórios, decimal opcional com dígitos
    // Aceita: 123, -10, +50, 1.23, -0.50, +12.0001
    if (!trimmed.matches("[+-]?\\d+(\\.\\d+)?")) {
      throw new RFWValidationException("Formato numérico inválido para BigDecimal. Valor recebido: '${0}'", new String[] { value });
    }

    try {
      return new BigDecimal(trimmed);
    } catch (Exception e) {
      // BigDecimal pode lançar NumberFormatException ou ArithmeticException
      throw new RFWCriticalException("Falha ao converter valor para BigDecimal. Valor '${0}'.", new String[] { value }, e);
    }
  }

  /**
   * Converte uma {@link String} para {@link BigDecimal} definindo a escala desejada.
   *
   * <p>
   * Este método reaproveita a validação de {@link #parseBigDecimal(String)} e, quando um número de casas decimais é informado, aplica {@link BigDecimal#setScale(int, java.math.RoundingMode)} utilizando o {@link RFW#getRoundingMode()} configurado no sistema.
   * </p>
   *
   * @param value Texto a ser convertido.
   * @param decimals Quantidade de casas decimais desejada. Se {@code null}, a escala não é alterada.
   * @return {@link BigDecimal} convertido ou {@code null} se o texto estiver nulo ou vazio.
   * @throws RFWException Em caso de validação inválida ou falha ao ajustar a escala.
   */
  public static BigDecimal parseBigDecimal(String value, Integer decimals) throws RFWException {
    BigDecimal parsed = parseBigDecimal(value);
    if (parsed != null && decimals != null) {
      try {
        parsed = parsed.setScale(decimals, RFW.getRoundingMode());
      } catch (Exception e) {
        throw new RFWCriticalException("Falha ao ajustar escala do BigDecimal. Valor '${0}', escala '${1}'.", new String[] { value, String.valueOf(decimals) }, e);
      }
    }
    return parsed;
  }

  /**
   * Converts a {@link Date} into a string using the pattern {@code "yyyy/MM/dd"}.
   *
   * <p>
   * The output always follows the four-digit year, two-digit month, and two-digit day format, separated by slashes.
   * </p>
   *
   * @param date the date to format; must not be {@code null}
   * @return the formatted date string in the pattern {@code yyyy/MM/dd}
   * @throws IllegalArgumentException if {@code date} is {@code null}
   */
  public static String formatToyyyy_MM_dd(Date date) {
    return new SimpleDateFormat("yyyy/MM/dd").format(date);
  }

  /**
   * Converte um objeto {@link Date} em uma string usando o padrão {@code "yyyy-MM-dd"}.
   *
   * <p>
   * O resultado segue o formato: ano com quatro dígitos, mês com dois dígitos e dia com dois dígitos, separados por hífens.
   * </p>
   *
   * @param date a data a ser formatada; não deve ser {@code null}
   * @return a data formatada no padrão {@code yyyy-MM-dd}
   * @throws IllegalArgumentException se {@code date} for {@code null}
   */
  public static String formatToYYYY_MM_DDHyphen(Date date) {
    return new SimpleDateFormat("yyyy-MM-dd").format(date);
  }

  /**
   * Converte um {@link LocalDateTime} em uma string usando o padrão {@code "yyyy/MM/dd"}.
   *
   * @param dateTime o objeto a ser formatado; não deve ser {@code null}
   * @return data formatada em {@code yyyy/MM/dd}
   */
  public static String formatToYYYY_MM_DD(LocalDateTime dateTime) {
    return dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
  }

  /**
   * Converte um {@link LocalDateTime} em uma string usando o padrão {@code "yyyy-MM-dd"}.
   *
   * @param dateTime o objeto a ser formatado; não deve ser {@code null}
   * @return data formatada em {@code yyyy-MM-dd}
   */
  public static String formatToYYYY_MM_DDHyphen(LocalDateTime dateTime) {
    return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  /**
   * Converte um {@link LocalDate} em uma string usando o padrão {@code "yyyy/MM/dd"}.
   *
   * @param date o objeto a ser formatado; não deve ser {@code null}
   * @return data formatada em {@code yyyy/MM/dd}
   */
  public static String formatToYYYY_MM_DD(LocalDate date) {
    return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
  }

  /**
   * Converte um {@link LocalDate} em uma string usando o padrão {@code "yyyy-MM-dd"}.
   *
   * @param date o objeto a ser formatado; não deve ser {@code null}
   * @return data formatada em {@code yyyy-MM-dd}
   */
  public static String formatToYYYY_MM_DDHyphen(LocalDate date) {
    return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  /**
   * Remove todos os caracteres que não são dígitos de uma string.
   *
   * @param value valor de entrada
   * @return apenas dígitos ou {@code null} se {@code value} for {@code null}
   */
  public static String removeNonDigits(String value) {
    if (value == null) {
      return null;
    }
    return value.replaceAll("\\D", "");
  }

  /**
   * Retorna o maior valor entre os elementos informados, de forma segura para valores nulos.
   * <p>
   * O método ignora valores {@code null} e retorna {@code null} caso o array seja nulo, vazio ou contenha apenas valores nulos.
   * </p>
   *
   * <p>
   * Funciona para qualquer tipo que implemente {@link Comparable}, como {@link Integer}, {@link Long}, {@link Double}, {@link java.math.BigDecimal}, {@link java.math.BigInteger}, entre outros.
   * </p>
   *
   * @param <T> tipo do valor a ser comparado
   * @param values valores a serem avaliados
   * @return o maior valor encontrado ou {@code null} se não houver valores válidos
   * @since BIS Orion
   */
  @SafeVarargs
  public static <T extends Comparable<? super T>> T max(T... values) {
    T max = null;

    if (values == null) {
      return null;
    }

    for (T value : values) {
      if (value != null && (max == null || value.compareTo(max) > 0)) {
        max = value;
      }
    }

    return max;
  }

  /**
   * Retorna o menor valor entre os elementos informados, de forma segura para valores nulos.
   * <p>
   * O método ignora valores {@code null} e retorna {@code null} caso o array seja nulo, vazio ou contenha apenas valores nulos.
   * </p>
   *
   * <p>
   * Funciona para qualquer tipo que implemente {@link Comparable}, como {@link Integer}, {@link Long}, {@link Double}, {@link java.math.BigDecimal}, {@link java.math.BigInteger}, entre outros.
   * </p>
   *
   * @param <T> tipo do valor a ser comparado
   * @param values valores a serem avaliados
   * @return o menor valor encontrado ou {@code null} se não houver valores válidos
   * @since BIS Orion
   */
  @SafeVarargs
  public static <T extends Comparable<? super T>> T minSafe(T... values) {
    T min = null;

    if (values == null) {
      return null;
    }

    for (T value : values) {
      if (value != null && (min == null || value.compareTo(min) < 0)) {
        min = value;
      }
    }

    return min;
  }

  /**
   * Retorna o valor absoluto do número informado de forma segura para {@code null}.
   * <p>
   * Caso o valor seja {@code null}, o método retorna {@code null}.
   * </p>
   *
   * @param value valor inteiro a ser avaliado
   * @return valor absoluto ou {@code null} se o valor for {@code null}
   * @since BIS Orion
   */
  public static Integer abs(Integer value) {
    return value == null ? null : Math.abs(value);
  }

  /**
   * Retorna o valor absoluto do número informado de forma segura para {@code null}.
   * <p>
   * Caso o valor seja {@code null}, o método retorna {@code null}.
   * </p>
   *
   * @param value valor {@link Long} a ser avaliado
   * @return valor absoluto ou {@code null} se o valor for {@code null}
   * @since BIS Orion
   */
  public static Long abs(Long value) {
    return value == null ? null : Math.abs(value);
  }

  /**
   * Retorna o valor absoluto do número informado de forma segura para {@code null}.
   * <p>
   * Caso o valor seja {@code null}, o método retorna {@code null}.
   * </p>
   *
   * <p>
   * Para {@link Double}, este método preserva as regras do {@link Math#abs(double)}, incluindo o tratamento de {@code NaN}.
   * </p>
   *
   * @param value valor {@link Double} a ser avaliado
   * @return valor absoluto ou {@code null} se o valor for {@code null}
   * @since BIS Orion
   */
  public static Double abs(Double value) {
    return value == null ? null : Math.abs(value);
  }

  /**
   * Retorna o valor absoluto do número informado de forma segura para {@code null}.
   * <p>
   * Caso o valor seja {@code null}, o método retorna {@code null}.
   * </p>
   *
   * @param value valor {@link java.math.BigDecimal} a ser avaliado
   * @return valor absoluto ou {@code null} se o valor for {@code null}
   * @since BIS Orion
   */
  public static java.math.BigDecimal abs(java.math.BigDecimal value) {
    return value == null ? null : value.abs();
  }

  /**
   * Converte um {@link YearMonth} para {@link XMLGregorianCalendar} no padrão {@code xs:gYearMonth} (AAAA-MM).
   * <p>
   * A conversão preserva exclusivamente ano e mês, não definindo dia, hora ou fuso horário, garantindo compatibilidade total com schemas XSD do tipo {@code gYearMonth}.
   * <p>
   * Método null-safe: caso o valor informado seja {@code null}, retorna {@code null}.
   *
   * @param value {@link YearMonth} a ser convertido
   * @return {@link XMLGregorianCalendar} representando ano e mês, ou {@code null}
   * @throws IllegalStateException caso ocorra erro na criação do {@link XMLGregorianCalendar}
   */
  public static XMLGregorianCalendar parseXMLGregorianCalendar(YearMonth value) {
    if (value == null) {
      return null;
    }
    try {
      DatatypeFactory factory = DatatypeFactory.newInstance();

      return factory.newXMLGregorianCalendarDate(value.getYear(), value.getMonthValue(), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);

    } catch (DatatypeConfigurationException e) {
      throw new IllegalStateException("Erro ao converter YearMonth para XMLGregorianCalendar", e);
    }
  }

  /**
   * Converte um {@link XMLGregorianCalendar} para {@link YearMonth}.
   * <p>
   * Espera que o valor de entrada represente um {@code xs:gYearMonth}, considerando apenas os campos de ano e mês. Informações de dia, hora ou fuso horário, caso existam, são ignoradas.
   * <p>
   * Método null-safe: caso o valor informado seja {@code null}, retorna {@code null}.
   *
   * @param value {@link XMLGregorianCalendar} a ser convertido
   * @return {@link YearMonth} correspondente ao ano e mês informados, ou {@code null}
   */
  public static YearMonth parseYearMonth(XMLGregorianCalendar value) {
    if (value == null) {
      return null;
    }
    return YearMonth.of(value.getYear(), value.getMonth());
  }

}
