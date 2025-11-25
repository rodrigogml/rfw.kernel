package br.eng.rodrigogml.rfw.kernel.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilitria utilizada para converso entre tipos de dados.<br>
 * Os mtodos dessa classe so organizados da seguinte forma de acordo com seu prefixo:<br>
 * <li><b>parse</b> - Indica mtodos que extraem ou convertem valores entre tipos, podendo ou no considerar {@link Locale} para uma correta interpretao dos dados.</li>
 * <li><b>format</b> - Faz o inverso do mtodo 'parse'. Converte o valor de um tipo de dado em uma string formatada para leitura do usurio, podendo considerar o Locale ou no.</li> <br>
 * Para mï¿½todos de geraï¿½ï¿½o de valores verifique {@link RUGenerators}.
 *
 * @author Rodrigo Leitï¿½o
 * @since (21 de fev. de 2025)
 */
public class RUTypes {

  private static final DateTimeFormatter FORMATTER_yyyy_MM_dd_T_HH_mm_ssXXX = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

  /**
   * Classe utilitï¿½ria exclusivamente estï¿½tica
   */
  private RUTypes() {
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para formar o Date apenas no horï¿½rio com o patern '23:59:59', ignorando qualquer que seja a data.
   *
   * @param time
   * @return
   */
  public static String formatTo235959(Date time) {
    return new SimpleDateFormat("HH:mm:ss").format(time);
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'yyyyMMddHHmmss'.
   *
   * @param date
   * @return
   */
  public static String formatToyyyyMMddHHmmss(Date date) {
    return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'yyyyMMdd'.
   *
   * @param date
   * @return
   */
  public static String formatToyyyyMMdd(Date date) {
    return new SimpleDateFormat("yyyyMMdd").format(date);
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'ddMMyyyy'.
   *
   * @param date
   * @return
   */
  public static String formatToddMMyyyy(Date date) {
    return new SimpleDateFormat("ddMMyyyy").format(date);
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'ddMMyyyyHHmmss'.
   *
   * @param date
   * @return
   */
  public static String formatToddMMyyyyHHmmss(Date date) {
    return new SimpleDateFormat("ddMMyyyyHHmmss").format(date);
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatTodd_MM_yyyy_HH_mm_ss(Date date) {
    return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatTodd_MM_yyyy_HH_mm_ss(LocalDateTime date) {
    return formatLocalDateTime(date, "dd/MM/yyyy HH:mm:ss");
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para formar o Date apenas no horï¿½rio com o patern '23:59:59', ignorando qualquer que seja a data.
   *
   * @param dLastConsult
   * @return
   */
  public static String formatTo235959(long timemillis) {
    return formatTo235959(new Date(timemillis));
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
   * Este mï¿½todo formata um LocalDate para com o patern 'ddMMyyyy'.
   *
   * @param date Objeto LocalDate com a data ï¿½ formatar.
   * @return String com o Dado Formatado.
   */
  public static String formatToddMMyyyy(LocalDate date) {
    return formatLocalDate(date, "ddMMyyyy");
  }

  /**
   * Formata um tempo em milissegundos no formato "HHH:MM:SS'mmm", onde HHH sï¿½o horas, MM minutos, SS segundos e mmm milissegundos.
   * <p>
   * Os campos zerados sï¿½o omitidos. Exemplo:
   * </p>
   * <ul>
   * <li>Se nï¿½o houver horas, o resultado serï¿½ "MM:SS'mmm".</li>
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
   * Formata um tempo em milissegundos utilizando um padrï¿½o de data/hora.
   *
   * @param pattern Padrï¿½o de formataï¿½ï¿½o do {@link SimpleDateFormat}.
   * @param millis Tempo em milissegundos para ser formatado.
   * @return Tempo formatado conforme o padrï¿½o informado.
   */
  public static String formatMillis(String pattern, long millis) {
    SimpleDateFormat sf = new SimpleDateFormat(pattern);
    sf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sf.format(new Date(millis));
  }

  /**
   * Interpreta diversos formatos de data e os converte para {@link LocalDateTime}.
   * <p>
   * Os formatos suportados sï¿½o:
   * <ul>
   * <li>"yyyy-MM-dd'T'HH:mm:ssXXX" Exemplo: "2024-02-20T15:30:00-07:00" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ssZ" Exemplo: "2024-02-20T15:30:00-0700" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ss" Exemplo: "2024-02-20T15:30:00" (UTC sem timezone)</li>
   * <li>"yyyy-MM-dd" Exemplo: "2024-02-20"</li>
   * <li>"dd/MM/yyyy" Exemplo: "20/02/2024"</li>
   * </ul>
   * <p>
   * Quando o timezone estï¿½ presente na string, ele ï¿½ ignorado e a data ï¿½ retornada no horï¿½rio local. Se nï¿½o houver timezone, assume-se o fuso horï¿½rio do sistema.
   *
   * <p>
   * <b>Diferenï¿½a para {@link #parseLocalDateTime(String, ZoneId)}:</b> Esse mï¿½todo apenas interpreta a data sem fazer conversï¿½o de fusos horï¿½rios.
   * </p>
   *
   * @param date String representando a data.
   * @return {@link LocalDateTime} correspondente.
   * @throws RFWException Se o formato da data nï¿½o for reconhecido ou se ocorrer um erro de conversï¿½o.
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
        // yyyy-MM-dd'T'HH:mm:ssXXX (Padrï¿½o UTC com TimeZone, ex: -07:00)
        return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9][0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssZ (Padrï¿½o UTC com TimeZone sem separador, ex: -0700)
        return OffsetDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")).toLocalDateTime();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ss (UTC Sem TimeZone)
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      } else if (date.matches("[0-3][0-9]/[0-1][0-9]/[1-2][0-9]{3}")) {
        // dd/MM/yyyy
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
      } else {
        throw new RFWValidationException("Formato da Data nï¿½o suportado. Data: '${0}'", new String[] { date });
      }
    } catch (DateTimeParseException e) {
      throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
    }
  }

  /**
   * Interpreta diversos formatos de data e os converte para {@link LocalDateTime}, ajustando a data para o timezone especificado.
   * <p>
   * Os formatos suportados sï¿½o os mesmos do mï¿½todo {@link #parseLocalDateTime(String)}, porï¿½m, caso a data contenha um timezone, ele serï¿½ convertido para o {@link ZoneId} fornecido.
   * </p>
   *
   * <p>
   * <b>Diferenï¿½a para {@link #parseLocalDateTime(String)}:</b> Esse mï¿½todo converte o horï¿½rio para o fuso horï¿½rio recebido como argumento.
   * </p>
   *
   * @param date String representando a data.
   * @param zoneID O {@link ZoneId} para o qual a data deve ser convertida.
   * @return {@link LocalDateTime} correspondente no timezone especificado.
   * @throws RFWException Se o formato da data nï¿½o for reconhecido ou se ocorrer um erro de conversï¿½o.
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
        // yyyy-MM-dd'T'HH:mm:ssXXX (Padrï¿½o UTC com TimeZone, ex: -07:00)
        return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).atZoneSameInstant(zoneID).toLocalDateTime();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9][0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssZ (Padrï¿½o UTC com TimeZone sem separador, ex: -0700)
        return OffsetDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")).atZoneSameInstant(zoneID).toLocalDateTime();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ss (UTC Sem TimeZone)
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(zoneID).toLocalDateTime();
      } else if (date.matches("[0-3][0-9]/[0-1][0-9]/[1-2][0-9]{3}")) {
        // dd/MM/yyyy
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay(zoneID).toLocalDateTime();
      } else {
        throw new RFWValidationException("Formato da Data nï¿½o suportado. Data: '${0}'", new String[] { date });
      }
    } catch (DateTimeParseException e) {
      throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
    }
  }

  /**
   * Interpreta diversos formatos de data e os converte para {@link Date}.
   * <p>
   * Os formatos suportados sï¿½o:
   * <ul>
   * <li>"yyyy-MM-dd'T'HH:mm:ssXXX" Exemplo: "2024-02-20T15:30:00-07:00" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ssZ" Exemplo: "2024-02-20T15:30:00-0700" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ss" Exemplo: "2024-02-20T15:30:00" (UTC sem timezone)</li>
   * <li>"yyyy-MM-dd" Exemplo: "2024-02-20"</li>
   * <li>"dd/MM/yyyy" Exemplo: "20/02/2024"</li>
   * </ul>
   * <p>
   * Se um timezone for especificado na string, ele serï¿½ utilizado para calcular o timestamp UTC. Caso contrï¿½rio, assume-se o fuso horï¿½rio do sistema.
   *
   * <p>
   * <b>Diferenï¿½a para {@link #parseDate(String, ZoneId)}:</b> Esse mï¿½todo apenas faz o parser da data sem conversï¿½o de timezone.
   * </p>
   *
   * @param date String representando a data.
   * @return {@link Date} correspondente.
   * @throws RFWException Se o formato da data nï¿½o for reconhecido ou se ocorrer um erro de conversï¿½o.
   */
  public static Date parseDate(String date) throws RFWException {
    return parseDate(date, RFW.getZoneId());
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para realizar o parse de uma Date e jï¿½ tratar a exception.
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
   * Os formatos suportados sï¿½o os mesmos do mï¿½todo {@link #parseDate(String)}, porï¿½m, caso a data contenha um timezone, ele serï¿½ convertido para o {@link ZoneId} fornecido.
   * </p>
   *
   * <p>
   * <b>Diferenï¿½a para {@link #parseDate(String)}:</b> Esse mï¿½todo converte o horï¿½rio para o fuso horï¿½rio recebido como argumento.
   * </p>
   *
   * @param date String representando a data.
   * @param zoneID O {@link ZoneId} para o qual a data deve ser convertida.
   * @return {@link Date} correspondente no timezone especificado.
   * @throws RFWException Se o formato da data nï¿½o for reconhecido ou se ocorrer um erro de conversï¿½o.
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
        throw new RFWValidationException("Formato da Data nï¿½o suportado. Data: '${0}'", new String[] { date });
      }
    } catch (DateTimeParseException e) {
      throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
    }
  }

  /**
   * Este mï¿½todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'yyyyMMddHHmmss'.
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
   * @param pattern no Formado especï¿½ficado pela documentaï¿½ï¿½o do mï¿½todo {@link DateTimeFormatter#ofPattern(String)}.
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
   * Os formatos suportados sï¿½o:
   * <ul>
   * <li>"yyyy-MM-dd'T'HH:mm:ssXXX" Exemplo: "2024-02-20T15:30:00-07:00" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ssZ" Exemplo: "2024-02-20T15:30:00-0700" (UTC com timezone)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ss" Exemplo: "2024-02-20T15:30:00" (UTC sem timezone)</li>
   * <li>"yyyy-MM-dd" Exemplo: "2024-02-20"</li>
   * <li>"dd/MM/yyyy" Exemplo: "20/02/2024"</li>
   * </ul>
   * <p>
   * Se um timezone for especificado na string, ele serï¿½ utilizado apenas para conversï¿½o do timestamp UTC, mas o retorno serï¿½ um {@link LocalDate} sem informaï¿½ï¿½es de horï¿½rio.
   *
   * <p>
   * <b>Diferenï¿½a para {@link #parseLocalDate(String, ZoneId)}:</b> Esse mï¿½todo apenas faz o parser da data sem conversï¿½o de timezone.
   * </p>
   *
   * @param date String representando a data.
   * @return {@link LocalDate} correspondente.
   * @throws RFWException Se o formato da data nï¿½o for reconhecido ou se ocorrer um erro de conversï¿½o.
   */
  public static LocalDate parseLocalDate(String date) throws RFWException {
    return parseLocalDate(date, RFW.getZoneId());
  }

  /**
   * Interpreta diversos formatos de data e os converte para {@link LocalDate}, ajustando a data para o timezone especificado antes de descartar a informaï¿½ï¿½o de horï¿½rio.
   * <p>
   * Os formatos suportados sï¿½o os mesmos do mï¿½todo {@link #parseLocalDate(String)}, porï¿½m, caso a data contenha um timezone, ele serï¿½ convertido para o {@link ZoneId} fornecido antes de extrair a data.
   * </p>
   *
   * <p>
   * <b>Diferenï¿½a para {@link #parseLocalDate(String)}:</b> Esse mï¿½todo converte o horï¿½rio para o fuso horï¿½rio recebido como argumento antes de extrair a data.
   * </p>
   *
   * @param date String representando a data.
   * @param zoneID O {@link ZoneId} para o qual a data deve ser convertida antes de extrair a parte da data.
   * @return {@link LocalDate} correspondente sem informaï¿½ï¿½es de horï¿½rio.
   * @throws RFWException Se o formato da data nï¿½o for reconhecido ou se ocorrer um erro de conversï¿½o.
   */
  public static LocalDate parseLocalDate(String date, ZoneId zoneID) throws RFWException {
    if (date == null || date.trim().isEmpty()) {
      return null;
    }

    try {
      if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]")) {
        // yyyy-MM-dd (Apenas Data)
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssXXX (UTC com TimeZone ex: -07:00)
        return OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).atZoneSameInstant(zoneID).toLocalDate();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9][0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ssZ (UTC com TimeZone sem separador ex: -0700)
        return OffsetDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")).atZoneSameInstant(zoneID).toLocalDate();
      } else if (date.matches("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")) {
        // yyyy-MM-dd'T'HH:mm:ss (Sem TimeZone)
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(zoneID).toLocalDate();
      } else if (date.matches("[0-3][0-9]/[0-1][0-9]/[1-2][0-9]{3}")) {
        // dd/MM/yyyy
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
      } else {
        throw new RFWValidationException("Formato da Data nï¿½o suportado. Data: '${0}'", new String[] { date });
      }
    } catch (DateTimeParseException e) {
      throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
    }
  }

  /**
   * Converte um {@link LocalDate} para {@link Date} utilizando a Zona do Sistema {@link RFW#getZoneId()}.<br>
   * ï¿½ considerada a hora zero do dia passaro na conversï¿½o para a Zona.
   *
   * @param date Valor de Entrada a ser convertido
   * @return Valor Convertido
   */
  public static Date parseDate(LocalDate date) {
    return Date.from(date.atStartOfDay().atZone(RFW.getZoneId()).toInstant());
  }

  /**
   * Converte um {@link LocalDate} para {@link Date} utilizando uma Zona personalizada.<br>
   * ï¿½ considerada a hora zero do dia passaro na conversï¿½o para a Zona.
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
   * Converte um {@link Date} em {@link LocalDate}. Utiliza a Zona padrï¿½o do sistema
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
   * @param zone Zone para correta conversï¿½o entre objetos temporais.
   * @return LocalDate conforme a zona, ou nulo caso date == null;
   */
  public static LocalDate parseLocalDate(Date date, ZoneId zone) {
    if (date == null) return null;
    return date.toInstant().atZone(zone).toLocalDate();
  }

  /**
   * Converte um {@link java.sql.Date} em {@link LocalDate}. Utiliza a Zona padrï¿½o do sistema
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
   * @return Objeto com o dia/horï¿½rio convertido para a zona solicitada, ou nulo se receber uma entrada nula.
   * @throws RFWException
   */
  public static LocalDate parseLocalDate(Timestamp stamp) throws RFWException {
    if (stamp == null) return null;
    return stamp.toLocalDateTime().toLocalDate();
  }

  /**
   * Converte um {@link Date} em {@link LocalDateTime}. Utiliza a Zona padrï¿½o do sistema {@link RFWDeprec#getZoneId()}.
   *
   * @param date Data a ser convertida em LocalDateTime
   * @return LocalDateTime conforme a zona, ou nulo se receber o valor nulo como parï¿½metro.
   */
  public static LocalDateTime parseLocalDateTime(Date date) {
    if (date == null) return null;
    return date.toInstant().atZone(RFW.getZoneId()).toLocalDateTime();
  }

  /**
   * Converte um {@link Date} em {@link LocalDateTime}.
   *
   * @param date Data a ser convertida em LocalDateTime
   * @param zone Zone para correta conversï¿½o entre objetos temporais.
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
   * @return Objeto com o dia/horï¿½rio convertido para a zona solicitada, ou nulo se receber uma entrada nula.
   * @throws RFWException
   */
  public static LocalDateTime parseLocalDateTime(Timestamp stamp) throws RFWException {
    if (stamp == null) return null;
    return stamp.toLocalDateTime();
  }

  /**
   * Formata um nï¿½mero decimal para String com o nï¿½mero de casas decimais especificado, usando o Locale informado.
   *
   * @param number Nï¿½mero a ser formatado
   * @param locale Localizaï¿½ï¿½o usada para formataï¿½ï¿½o
   * @param maxDecimals Nï¿½mero mï¿½ximo de casas decimais
   * @return Representaï¿½ï¿½o em String formatada do nï¿½mero
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
   * Converte de forma segura um valor genï¿½rico para {@link Integer}.<br>
   * <p>
   * Regras de conversï¿½o:
   * <ul>
   * <li>Se o valor for {@code null}, retorna {@code null}.</li>
   * <li>Se o valor jï¿½ for {@link Integer}, ï¿½ retornado diretamente.</li>
   * <li>Se o valor for {@link Number}, delega para {@link #parseInteger(Number)}.</li>
   * <li>Se o valor for {@link String}, delega para {@link #parseInteger(String)}.</li>
   * <li>Se o valor for {@link Boolean}, retorna {@code 1} para {@code true} e {@code 0} para {@code false}.</li>
   * </ul>
   * <p>
   * Caso o tipo nï¿½o seja suportado, uma {@link RFWValidationException} ï¿½ lanï¿½ada.<br>
   * Em situaï¿½ï¿½es de falha interna de conversï¿½o (por exemplo, falha em parsers de baixo nï¿½vel), uma {@link RFWCriticalException} poderï¿½ ser propagada pelos mï¿½todos delegados.
   *
   * @param value Valor de entrada a ser convertido.
   * @return Valor convertido em {@link Integer} ou {@code null} caso o valor de entrada seja {@code null} ou {@link String} vazia.
   * @throws RFWException Em caso de erro de validaï¿½ï¿½o ou falha crï¿½tica de conversï¿½o.
   */
  public static Integer parseInteger(Object value) throws RFWException {
    // Trata nulo imediatamente
    if (value == null) {
      return null;
    }

    // Jï¿½ ï¿½ Integer, apenas retorna
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

    // Convenï¿½ï¿½o simples para booleano: true = 1, false = 0
    if (value instanceof Boolean) {
      return ((Boolean) value) ? 1 : 0;
    }

    // Tipo nï¿½o suportado
    throw new RFWValidationException("Tipo '${0}' nï¿½o suportado para conversï¿½o em Integer.", new String[] { value.getClass().getName() });
  }

  /**
   * Converte de forma segura um {@link String} para {@link Integer}.<br>
   * <p>
   * Regras de conversï¿½o:
   * <ul>
   * <li>Se o valor for {@code null} ou vazio (apenas espaï¿½os), retorna {@code null}.</li>
   * <li>O formato aceito ï¿½ opcionalmente sinalizado (+/-) seguido apenas de dï¿½gitos.</li>
   * <li>Se o formato nï¿½o for suportado, lanï¿½a {@link RFWValidationException}.</li>
   * <li>Se ocorrer falha interna no parser (por exemplo, estouro nï¿½o tratado), lanï¿½a {@link RFWCriticalException}.</li>
   * <li>Se o valor estiver fora do intervalo de {@link Integer}, lanï¿½a {@link RFWValidationException}.</li>
   * </ul>
   *
   * @param value Valor textual a ser convertido.
   * @return Valor convertido em {@link Integer} ou {@code null} caso o valor de entrada seja {@code null} ou vazio.
   * @throws RFWException Em caso de erro de validaï¿½ï¿½o ou falha crï¿½tica de conversï¿½o.
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

    // Valida formato aceito: sinal opcional seguido de dï¿½gitos
    if (!trimmed.matches("[+-]?[0-9]+")) {
      throw new RFWValidationException("Formato de nï¿½mero inteiro nï¿½o suportado. Valor: '${0}'", new String[] { value });
    }

    try {
      // Usa Long para detectar facilmente estouro de faixa do int
      long longValue = Long.parseLong(trimmed);

      // Validaï¿½ï¿½o de faixa do tipo Integer
      if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
        throw new RFWValidationException("Valor fora do intervalo suportado para Integer. Valor: '${0}'", new String[] { value });
      }

      return (int) longValue;
    } catch (NumberFormatException e) {
      // Falha interna de conversï¿½o numï¿½rica
      throw new RFWCriticalException("Falha ao converter valor para Integer. Valor '${0}'.", new String[] { value }, e);
    }
  }

  /**
   * Converte de forma segura um {@link Number} para {@link Integer}.<br>
   * <p>
   * Regras de conversï¿½o:
   * <ul>
   * <li>Se o valor for {@code null}, retorna {@code null}.</li>
   * <li>Se o valor jï¿½ for {@link Integer}, ï¿½ retornado diretamente.</li>
   * <li>Para tipos integrais ({@link Long}, {@link Short}, {@link Byte}), ï¿½ feita validaï¿½ï¿½o de faixa.</li>
   * <li>Para {@link BigDecimal}, ï¿½ exigido que o valor seja inteiro (sem casas decimais) e dentro da faixa de {@link Integer}.</li>
   * <li>Para {@link Double} e {@link Float}, ï¿½ exigido que o valor nï¿½o seja NaN/Infinito, seja inteiro (sem parte fracionï¿½ria) e esteja na faixa de {@link Integer}.</li>
   * </ul>
   * <p>
   * Situaï¿½ï¿½es em que o valor nï¿½o puder ser representado como inteiro sem perda de precisï¿½o ou estiver fora da faixa geram {@link RFWValidationException}.
   *
   * @param value Valor numï¿½rico a ser convertido.
   * @return Valor convertido em {@link Integer} ou {@code null} caso o valor de entrada seja {@code null}.
   * @throws RFWException Em caso de erro de validaï¿½ï¿½o ou falha crï¿½tica de conversï¿½o interna.
   */
  public static Integer parseInteger(Number value) throws RFWException {
    // Trata nulo
    if (value == null) {
      return null;
    }

    // Jï¿½ ï¿½ Integer
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
        // Tem parte fracionï¿½ria ou estï¿½ fora da faixa de long
        throw new RFWValidationException("Valor decimal nï¿½o pode ser convertido para Integer sem perda de precisï¿½o. Valor: '${0}'", new String[] { bd.toPlainString() });
      }
    }

    // Double/Float: verifica NaN, infinito, parte fracionï¿½ria e faixa
    if (value instanceof Double || value instanceof Float) {
      double d = value.doubleValue();

      // NaN ou infinito nï¿½o sï¿½o valores vï¿½lidos
      if (Double.isNaN(d) || Double.isInfinite(d)) {
        throw new RFWValidationException("Valor numï¿½rico invï¿½lido para conversï¿½o em Integer. Valor: '${0}'", new String[] { String.valueOf(value) });
      }

      // Verifica faixa de Integer
      if (d < Integer.MIN_VALUE || d > Integer.MAX_VALUE) {
        throw new RFWValidationException("Valor fora do intervalo suportado para Integer. Valor: '${0}'", new String[] { String.valueOf(value) });
      }

      // Exige que seja inteiro (sem casas decimais)
      if (Math.rint(d) != d) {
        throw new RFWValidationException("Valor decimal nï¿½o pode ser convertido para Integer sem perda de precisï¿½o. Valor: '${0}'", new String[] { String.valueOf(value) });
      }

      return (int) d;
    }

    // Qualquer outro subtipo de Number: tenta via long com validaï¿½ï¿½o de faixa
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
   * <li>Se o valor nï¿½o for um nï¿½mero inteiro vï¿½lido, lanï¿½a {@link RFWValidationException}.</li>
   * <li>Se ocorrer falha interna no parser numï¿½rico, lanï¿½a {@link RFWCriticalException}.</li>
   * </ul>
   *
   * @param value Valor textual a ser convertido.
   * @return {@link Long} correspondente ou {@code null}.
   * @throws RFWException Se o valor nï¿½o for vï¿½lido.
   */
  public static Long toLong(String value) throws RFWException {
    if (value == null || value.trim().isEmpty()) return null;
    try {
      return Long.valueOf(value.trim());
    } catch (NumberFormatException e) {
      throw new RFWValidationException("Valor invï¿½lido para conversï¿½o em Long: '${0}'", new String[] { value });
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
   * <li>Se o valor for {@link Double} ou {@link Float}, deve representar nï¿½mero inteiro exato.</li>
   * <li>Outros tipos numï¿½ricos (Integer, Short, Byte) sï¿½o convertidos diretamente.</li>
   * </ul>
   *
   * @param value Valor numï¿½rico a ser convertido.
   * @return {@link Long} correspondente ou {@code null}.
   * @throws RFWException Se o valor for invï¿½lido ou fora da faixa de {@link Long}.
   */
  public static Long toLong(Number value) throws RFWException {
    if (value == null) return null;

    if (value instanceof Long) return (Long) value;
    if (value instanceof Integer || value instanceof Short || value instanceof Byte) return value.longValue();

    if (value instanceof BigDecimal) {
      BigDecimal bd = ((BigDecimal) value).stripTrailingZeros();
      try {
        return bd.longValueExact();
      } catch (ArithmeticException e) {
        throw new RFWValidationException("Valor decimal nï¿½o ï¿½ inteiro ou estï¿½ fora da faixa de Long: '${0}'", new String[] { value.toString() });
      }
    }

    if (value instanceof Double || value instanceof Float) {
      double d = value.doubleValue();
      if (Double.isNaN(d) || Double.isInfinite(d)) throw new RFWValidationException("Valor invï¿½lido (NaN ou Infinito) para Long: '${0}'", new String[] { value.toString() });
      if (d % 1 != 0) throw new RFWValidationException("Valor nï¿½o inteiro para conversï¿½o em Long: '${0}'", new String[] { value.toString() });
      if (d < Long.MIN_VALUE || d > Long.MAX_VALUE) throw new RFWValidationException("Valor fora da faixa de Long: '${0}'", new String[] { value.toString() });
      return (long) d;
    }

    throw new RFWValidationException("Tipo numï¿½rico nï¿½o suportado para conversï¿½o em Long: '${0}'", new String[] { value.getClass().getName() });
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
   * <li>Qualquer outro tipo lanï¿½a {@link RFWValidationException}.</li>
   * </ul>
   *
   * @param value Objeto genï¿½rico a ser convertido.
   * @return {@link Long} correspondente ou {@code null}.
   * @throws RFWException Se o tipo nï¿½o for suportado ou o valor for invï¿½lido.
   */
  public static Long toLong(Object value) throws RFWException {
    if (value == null) return null;

    if (value instanceof Number) return toLong((Number) value);
    if (value instanceof String) return toLong((String) value);
    if (value instanceof Boolean) return ((Boolean) value) ? 1L : 0L;

    throw new RFWValidationException("Tipo nï¿½o suportado para conversï¿½o em Long: '${0}'", new String[] { value.getClass().getName() });
  }

  /**
   * Formata um valor decimal para percentual com 2 casas decimais.
   * <p>
   * O valor recebido deve estar em formato decimal (ex: 1.0 = 100%, 0.5 = 50%, 0.1234 = 12,34%).<br>
   * Caso o valor seja {@code null}, serï¿½ retornado {@code "0%"}.
   * </p>
   *
   * @param value Valor decimal a ser formatado.
   * @return String representando o valor em formato percentual, com 2 casas decimais.
   */
  public static String formatToPercentage(Double value) {
    return formatToPercentage(value, 1);
  }

  /**
   * Formata um valor decimal para percentual com o nï¿½mero de casas decimais especificado.
   * <p>
   * O valor recebido deve estar em formato decimal (ex: 1.0 = 100%, 0.5 = 50%, 0.1234 = 12,34%).<br>
   * Caso o valor seja {@code null}, serï¿½ retornado {@code "0%"}.
   * </p>
   *
   * @param value Valor decimal a ser formatado.
   * @param decimals Quantidade de casas decimais desejadas na parte fracionï¿½ria.
   * @return String representando o valor em formato percentual conforme o nï¿½mero de casas solicitado.
   */
  public static String formatToPercentage(Double value, int decimals) {
    double safeValue = (value == null ? 0.0 : value) * 100.0;

    // Gera o padrï¿½o dinamicamente conforme as casas decimais desejadas
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
   * Este mï¿½todo arredonda <b>para o lado mais prï¿½ximo</b> nï¿½meros decimais (double) com um nï¿½mero mï¿½ximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals nï¿½mero de casas mï¿½ximo
   * @return
   */
  public static Double round(double value, int decimals) {
    double factor = Math.pow(10, decimals);
    double result = Math.round(value * factor) / factor;
    return result;
  }

  /**
   * Este mï¿½todo arredonda <b>para baixo</b> nï¿½meros decimais (double) com um nï¿½mero mï¿½ximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals nï¿½mero de casas mï¿½ximo
   * @return
   */
  public static Double roundFloor(double value, int decimals) {
    double factor = Math.pow(10, decimals);
    double result = Math.floor(value * factor) / factor;
    return result;
  }

  /**
   * Este mï¿½todo arredonda <b>para cima</b> nï¿½meros decimais (double) com um nï¿½mero mï¿½ximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals nï¿½mero de casas mï¿½ximo
   * @return
   */
  public static Double roundCeil(double value, int decimals) {
    double factor = Math.pow(10, decimals);
    double result = Math.ceil(value * factor) / factor;
    return result;
  }

  /**
   * Este mï¿½todo arredonda <b>para o lado mais prï¿½ximo</b> nï¿½meros decimais (float) com um nï¿½mero mï¿½ximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals nï¿½mero de casas mï¿½ximo
   * @return
   */
  public static Float round(float value, int decimals) {
    double factor = Math.pow(10, decimals);
    float result = (float) (Math.round(value * factor) / factor);
    return result;
  }

  /**
   * Este mï¿½todo arredonda <b>para baixo</b> nï¿½meros decimais (float) com um nï¿½mero mï¿½ximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals nï¿½mero de casas mï¿½ximo
   * @return
   */
  public static Float roundFloor(float value, int decimals) {
    double factor = Math.pow(10, decimals);
    float result = (float) (Math.floor(value * factor) / factor);
    return result;
  }

  /**
   * Este mï¿½todo arredonda <b>para cima</b> nï¿½meros decimais (float) com um nï¿½mero mï¿½ximo de casas (decimals).
   *
   * @param value valor decimal a ser arredondado
   * @param decimals nï¿½mero de casas mï¿½ximo
   * @return
   */
  public static Float roundCeil(float value, int decimals) {
    double factor = Math.pow(10, decimals);
    float result = (float) (Math.ceil(value * factor) / factor);
    return result;
  }

  /**
   * Este mï¿½todo formata um {@link LocalDateTime} no padrï¿½o completo "yyyy-MM-dd'T'HH:mm:ssXXX", utilizando o {@link ZoneOffset} informado.
   *
   * @param dateTime Data/hora a ser formatada (nï¿½o nula)
   * @param offset Offset a ser utilizado (ex: {@link ZoneOffset#UTC})
   * @return String com a data/hora formatada
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime dateTime, ZoneOffset offset) {
    return dateTime.atOffset(offset).format(FORMATTER_yyyy_MM_dd_T_HH_mm_ssXXX);
  }

  /**
   * Este mï¿½todo formata um {@link Date} no padrï¿½o completo {@code "yyyy-MM-dd'T'HH:mm:ssXXX"}, utilizando a zona padrï¿½o do sistema {@link RFW#getZoneId()}.
   * <p>
   * Observaï¿½ï¿½o: quando o offset resultante for UTC (deslocamento zero), o {@link DateTimeFormatter} do Java imprimirï¿½ {@code Z} ao final, em vez de {@code +00:00}, conforme a especificaï¿½ï¿½o ISO-8601.
   * </p>
   *
   * @param date Data a ser formatada (pode ser nula)
   * @return String com a data/hora formatada ou {@code null} se {@code date} for nulo.
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(Date date) {
    if (date == null) return null;
    return date.toInstant().atZone(RFW.getZoneId()).format(FORMATTER_yyyy_MM_dd_T_HH_mm_ssXXX);
  }

  /**
   * Este mï¿½todo formata um {@link Date} no padrï¿½o completo {@code "yyyy-MM-dd'T'HH:mm:ssXXX"}, utilizando a {@link ZoneId} informada.
   * <p>
   * Observaï¿½ï¿½o: quando o offset resultante for UTC (deslocamento zero), o {@link DateTimeFormatter} do Java imprimirï¿½ {@code Z} ao final, em vez de {@code +00:00}, conforme a especificaï¿½ï¿½o ISO-8601.
   * </p>
   *
   * @param date Data a ser formatada (pode ser nula)
   * @param zoneId Zona a ser utilizada na conversï¿½o (nï¿½o nula)
   * @return String com a data/hora formatada ou {@code null} se {@code date} for nulo.
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(Date date, ZoneId zoneId) {
    if (date == null) return null;
    return date.toInstant().atZone(zoneId).format(FORMATTER_yyyy_MM_dd_T_HH_mm_ssXXX);
  }

  /**
   * Este mï¿½todo formata um {@link LocalDateTime} no padrï¿½o completo {@code "yyyy-MM-dd'T'HH:mm:ssXXX"}, utilizando a zona padrï¿½o do sistema {@link RFW#getZoneId()}.
   * <p>
   * Observaï¿½ï¿½o: quando o offset resultante for UTC (deslocamento zero), o {@link DateTimeFormatter} do Java imprimirï¿½ {@code Z} ao final, em vez de {@code +00:00}, conforme a especificaï¿½ï¿½o ISO-8601.
   * </p>
   *
   * @param dateTime Data/hora a ser formatada (pode ser nula)
   * @return String com a data/hora formatada ou {@code null} se {@code dateTime} for nulo.
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime dateTime) {
    if (dateTime == null) return null;
    return dateTime.atZone(RFW.getZoneId()).format(FORMATTER_yyyy_MM_dd_T_HH_mm_ssXXX);
  }

  /**
   * Este mï¿½todo formata um {@link LocalDateTime} no padrï¿½o completo {@code "yyyy-MM-dd'T'HH:mm:ssXXX"}, utilizando a {@link ZoneId} informada.
   * <p>
   * Observaï¿½ï¿½o: quando o offset resultante for UTC (deslocamento zero), o {@link DateTimeFormatter} do Java imprimirï¿½ {@code Z} ao final, em vez de {@code +00:00}, conforme a especificaï¿½ï¿½o ISO-8601.
   * </p>
   *
   * @param dateTime Data/hora a ser formatada (pode ser nula)
   * @param zoneId Zona a ser utilizada na conversï¿½o (nï¿½o nula)
   * @return String com a data/hora formatada ou {@code null} se {@code dateTime} for nulo.
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime dateTime, ZoneId zoneId) {
    if (dateTime == null) return null;
    return dateTime.atZone(zoneId).format(FORMATTER_yyyy_MM_dd_T_HH_mm_ssXXX);
  }

  /**
   * Formata uma data usando um padrï¿½o customizado.
   *
   * <p>
   * O parï¿½metro {@code pattern} segue as regras do {@link java.text.SimpleDateFormat}. Abaixo estï¿½o os principais termos que podem ser utilizados no padrï¿½o:
   *
   * <ul>
   * <li><b>y</b> ï¿½ Ano (ex.: yyyy = 2025, yy = 25)</li>
   * <li><b>M</b> ï¿½ Mï¿½s (ex.: MM = 03, MMM = Mar, MMMM = Marï¿½o)</li>
   * <li><b>d</b> ï¿½ Dia do mï¿½s (ex.: dd = 09)</li>
   * <li><b>E</b> ï¿½ Dia da semana (ex.: EEE = Seg, EEEE = Segunda-feira)</li>
   * <li><b>H</b> ï¿½ Hora (0ï¿½23, ex.: HH = 17)</li>
   * <li><b>h</b> ï¿½ Hora (1ï¿½12, ex.: hh = 05)</li>
   * <li><b>m</b> ï¿½ Minutos (ex.: mm = 07)</li>
   * <li><b>s</b> ï¿½ Segundos (ex.: ss = 59)</li>
   * <li><b>S</b> ï¿½ Milissegundos (ex.: SSS = 123)</li>
   * <li><b>a</b> ï¿½ AM/PM (ex.: a = PM)</li>
   * <li><b>z</b> ï¿½ Fuso horï¿½rio (ex.: z = BRT, zzzz = Brasï¿½lia Standard Time)</li>
   * <li><b>Z</b> ï¿½ Offset numï¿½rico do fuso (ex.: -0300)</li>
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
   * @param pattern Padrï¿½o de formataï¿½ï¿½o.
   * @return String formatada ou {@code null} caso a data seja nula.
   * @throws IllegalArgumentException caso o padrï¿½o seja invï¿½lido.
   */
  public static String formatDate(Date date, String pattern) {
    if (date == null) {
      return null;
    }
    if (pattern == null || pattern.trim().isEmpty()) {
      throw new IllegalArgumentException("Pattern nï¿½o pode ser nulo ou vazio");
    }
    return new java.text.SimpleDateFormat(pattern).format(date);
  }

  /**
   * Converte um {@link BigDecimal} em sua representaï¿½ï¿½o sem notaï¿½ï¿½o cientï¿½fica.
   *
   * <p>
   * Retorna {@code null} se o valor informado for {@code null}.
   * </p>
   *
   * @param value valor numï¿½rico a ser convertido
   * @return representaï¿½ï¿½o textual sem notaï¿½ï¿½o cientï¿½fica ou {@code null} se o valor for {@code null}
   */
  public static String toString(BigDecimal value) {
    return value != null ? value.toPlainString() : null;
  }

  /**
   * Converte um {@link Long} em uma {@link String}.
   *
   * <p>
   * Retorna {@code null} caso o valor informado tambï¿½m seja {@code null}.
   * </p>
   *
   * @param value valor long a ser convertido
   * @return representaï¿½ï¿½o textual do valor ou {@code null} se o valor for {@code null}
   */
  public static String toString(Long value) {
    return value != null ? value.toString() : null;
  }

  /**
   * Converte um {@link Integer} em uma {@link String}.
   *
   * <p>
   * Retorna {@code null} caso o valor informado tambï¿½m seja {@code null}.
   * </p>
   *
   * @param value valor inteiro a ser convertido
   * @return representaï¿½ï¿½o textual do valor ou {@code null} se o valor for {@code null}
   */
  public static String toString(Integer value) {
    return value != null ? value.toString() : null;
  }

  /**
   * Converte um {@link Float} em uma {@link String}.
   *
   * <p>
   * Retorna {@code null} caso o valor informado tambï¿½m seja {@code null}.
   * </p>
   *
   * @param value valor float a ser convertido
   * @return representaï¿½ï¿½o textual do valor ou {@code null} se o valor for {@code null}
   */
  public static String toString(Float value) {
    return value != null ? value.toString() : null;
  }

  /**
   * Converte um {@link Double} em uma {@link String}.
   *
   * <p>
   * Retorna {@code null} caso o valor informado tambï¿½m seja {@code null}.
   * </p>
   *
   * @param value valor double a ser convertido
   * @return representaï¿½ï¿½o textual do valor ou {@code null} se o valor for {@code null}
   */
  public static String toString(Double value) {
    return value != null ? value.toString() : null;
  }

  /**
   * Converte de forma segura um {@link String} para {@link BigDecimal}.
   *
   * <p>
   * <b>Regras de conversï¿½o:</b>
   * </p>
   * <ul>
   * <li>Se o valor for {@code null} ou vazio (apenas espaï¿½os), retorna {@code null}.</li>
   * <li>O formato aceito ï¿½:
   * <ul>
   * <li>Sinal opcional (+/-)</li>
   * <li>Parte inteira composta apenas por dï¿½gitos</li>
   * <li>Opcionalmente um ponto decimal seguido apenas de dï¿½gitos</li>
   * </ul>
   * Ex.: {@code "10"}, {@code "-5"}, {@code "12.34"}, {@code "+0.99"}.</li>
   * <li>Nï¿½O aceita formato com vï¿½rgula.</li>
   * <li>Se o formato nï¿½o for suportado, lanï¿½a {@link RFWValidationException}.</li>
   * <li>Se ocorrer falha interna no parser (ex.: valor muito grande ou formato inesperado), lanï¿½a {@link RFWCriticalException}.</li>
   * </ul>
   *
   * @param value Valor textual a ser convertido.
   * @return Instï¿½ncia de {@link BigDecimal} representando o valor ou {@code null} se entrada nula ou vazia.
   * @throws RFWException Em caso de erro de validaï¿½ï¿½o ou falha crï¿½tica de conversï¿½o.
   */
  public static BigDecimal toBigDecimal(String value) throws RFWException {
    if (value == null) {
      return null;
    }

    String trimmed = value.trim();
    if (trimmed.isEmpty()) {
      return null;
    }

    // Regex: sinal opcional, dï¿½gitos obrigatï¿½rios, decimal opcional com dï¿½gitos
    // Aceita: 123, -10, +50, 1.23, -0.50, +12.0001
    if (!trimmed.matches("[+-]?\\d+(\\.\\d+)?")) {
      throw new RFWValidationException("Formato numï¿½rico invï¿½lido para BigDecimal. Valor recebido: '${0}'", new String[] { value });
    }

    try {
      return new BigDecimal(trimmed);
    } catch (Exception e) {
      // BigDecimal pode lanï¿½ar NumberFormatException ou ArithmeticException
      throw new RFWCriticalException("Falha ao converter valor para BigDecimal. Valor '${0}'.", new String[] { value }, e);
    }
  }

}