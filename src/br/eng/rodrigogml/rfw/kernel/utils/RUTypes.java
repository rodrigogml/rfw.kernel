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
 * Description: Classe utilitária utilizada para conversão entre tipos de dados.<br>
 * Os métodos dessa classe são organizados da seguinte forma de acordo com seu prefixo:<br>
 * <li><b>parse</b> - Indica métodos que estraem o tipo a partir de uma String, podendo ou não conter o Locale do usuário para uma correta interpretação dos dados.
 * <li><b>format</b> - Faz o inverso do método 'parse'. Converte o valor de um tipo de dado em uma string formata para leitura do usuário, podendo considerar o Locale ou não.
 * <li><b>to</b> - Métodos que convertem de forma 'segura' (prevendo nulo e outras condições) um tipo em outro tipo compatível.
 * <li><b>gen</b> - Métodos para geração de objetos do Java com conteúdo aleatório.
 *
 * @author Rodrigo Leitão
 * @since (21 de fev. de 2025)
 */
public class RUTypes {

  /**
   * Classe utilitária exclusivamente estática
   */
  private RUTypes() {
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date apenas no horário com o patern '23:59:59', ignorando qualquer que seja a data.
   *
   * @param time
   * @return
   */
  public static String formatTo235959(Date time) {
    return new SimpleDateFormat("HH:mm:ss").format(time);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'yyyyMMddHHmmss'.
   *
   * @param date
   * @return
   */
  public static String formatToyyyyMMddHHmmss(Date date) {
    return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'yyyyMMdd'.
   *
   * @param date
   * @return
   */
  public static String formatToyyyyMMdd(Date date) {
    return new SimpleDateFormat("yyyyMMdd").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'ddMMyyyy'.
   *
   * @param date
   * @return
   */
  public static String formatToddMMyyyy(Date date) {
    return new SimpleDateFormat("ddMMyyyy").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'ddMMyyyyHHmmss'.
   *
   * @param date
   * @return
   */
  public static String formatToddMMyyyyHHmmss(Date date) {
    return new SimpleDateFormat("ddMMyyyyHHmmss").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatTodd_MM_yyyy_HH_mm_ss(Date date) {
    return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatTodd_MM_yyyy_HH_mm_ss(LocalDateTime date) {
    return formatLocalDateTime(date, "dd/MM/yyyy HH:mm:ss");
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'yyyy-MM-dd'T'HH:mm:ssXXX' (Padrão UTC utilizado no XML da NFe).
   *
   * @param date
   * @return
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(Date date) {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'yyyy-MM-dd'T'HH:mm:ssXXX' (Padrão UTC utilizado no XML da NFe).
   *
   * @param date
   * @return
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime date, ZoneId zoneId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    return date.atZone(zoneId).format(formatter);
  }

  /**
   * Este método utiliza o SimpleDateFormat para formar o Date apenas no horário com o patern '23:59:59', ignorando qualquer que seja a data.
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
   * Este método formata um LocalDate para com o patern 'ddMMyyyy'.
   *
   * @param date Objeto LocalDate com a data à formatar.
   * @return String com o Dado Formatado.
   */
  public static String formatToddMMyyyy(LocalDate date) {
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

    return (h > 0 ? String.format("%02d:", h) : "") +
        (h > 0 || m > 0 ? String.format("%02d:", m) : "") +
        String.format("%02d'", s) +
        String.format("%03d\"", ms);
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
        throw new RFWValidationException("Formato da Data não suportado. Data: '${0}'", new String[] { date });
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
        throw new RFWValidationException("Formato da Data não suportado. Data: '${0}'", new String[] { date });
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
   * Este método utiliza o SimpleDateFormat para realizar o parse de uma Date e já tratar a exception.
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
        throw new RFWValidationException("Formato da Data não suportado. Data: '${0}'", new String[] { date });
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
   * @param pattern no Formado específicado pela documentação do método {@link DateTimeFormatter#ofPattern(String)}.
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
   * Se um timezone for especificado na string, ele será utilizado apenas para conversão do timestamp UTC, mas o retorno será um {@link LocalDate} sem informações de horário.
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
   * Interpreta diversos formatos de data e os converte para {@link LocalDate}, ajustando a data para o timezone especificado antes de descartar a informação de horário.
   * <p>
   * Os formatos suportados são os mesmos do método {@link #parseLocalDate(String)}, porém, caso a data contenha um timezone, ele será convertido para o {@link ZoneId} fornecido antes de extrair a data.
   * </p>
   *
   * <p>
   * <b>Diferença para {@link #parseLocalDate(String)}:</b> Esse método converte o horário para o fuso horário recebido como argumento antes de extrair a data.
   * </p>
   *
   * @param date String representando a data.
   * @param zoneID O {@link ZoneId} para o qual a data deve ser convertida antes de extrair a parte da data.
   * @return {@link LocalDate} correspondente sem informações de horário.
   * @throws RFWException Se o formato da data não for reconhecido ou se ocorrer um erro de conversão.
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
        throw new RFWValidationException("Formato da Data não suportado. Data: '${0}'", new String[] { date });
      }
    } catch (DateTimeParseException e) {
      throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
    }
  }

  /**
   * Converte um {@link LocalDate} para {@link Date} utilizando a Zona do Sistema {@link RFW#getZoneId()}.<br>
   * É considerada a hora zero do dia passaro na conversão para a Zona.
   *
   * @param date Valor de Entrada a ser convertido
   * @return Valor Convertido
   */
  public static Date toDate(LocalDate date) {
    return Date.from(date.atStartOfDay().atZone(RFW.getZoneId()).toInstant());
  }

  /**
   * Converte um {@link LocalDate} para {@link Date} utilizando uma Zona personalizada.<br>
   * É considerada a hora zero do dia passaro na conversão para a Zona.
   *
   * @param date Valor de Entrada a ser convertido
   * @param zone Zona a ser utilizada.
   * @return Valor Convertido
   */
  public static Date toDate(LocalDate date, ZoneId zone) {
    return Date.from(date.atStartOfDay().atZone(zone).toInstant());
  }

  /**
   * Converte um {@link LocalDateTime} para {@link Date} utilizando a Zona do Sistema {@link RFW#getZoneId()}.
   *
   * @param dateTime Valor de Entrada a ser convertido
   * @return Valor Convertido ou nulo caso o valor de entrada seja nulo.
   */
  public static Date toDate(LocalDateTime dateTime) {
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
  public static Date toDate(LocalDateTime dateTime, ZoneId zone) {
    return Date.from(dateTime.atZone(zone).toInstant());
  }

  /**
   * Converte um {@link Date} em {@link LocalDate}. Utiliza a Zona padrão do sistema
   *
   * @param date Data a ser convertida em LocalDate
   * @return LocalDate conforme a zona, ou nulo caso a entrada seja nula.
   */
  public static LocalDate toLocalDate(Date date) {
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
  public static LocalDate toLocalDate(Date date, ZoneId zone) {
    if (date == null) return null;
    return date.toInstant().atZone(zone).toLocalDate();
  }

  /**
   * Converte um {@link java.sql.Date} em {@link LocalDate}. Utiliza a Zona padrão do sistema
   *
   * @param date Data a ser convertida em LocalDate
   * @return LocalDate conforme a zona ou null caso a entrada seja nula.
   */
  public static LocalDate toLocalDate(java.sql.Date date) {
    if (date == null) return null;
    return date.toLocalDate();
  }

  /**
   * Converte um {@link LocalDateTime} em {@link LocalDate}.
   *
   * @param date Data a ser convertida em LocalDate
   * @return LocalDate conforme o valor de entrada, ou nulo caso a entrada seja nula.
   */
  public static LocalDate toLocalDate(LocalDateTime date) {
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
  public static LocalDate toLocalDate(Timestamp stamp) throws RFWException {
    if (stamp == null) return null;
    return stamp.toLocalDateTime().toLocalDate();
  }

  /**
   * Converte um {@link Date} em {@link LocalDateTime}. Utiliza a Zona padrão do sistema {@link RFWDeprec#getZoneId()}.
   *
   * @param date Data a ser convertida em LocalDateTime
   * @return LocalDateTime conforme a zona, ou nulo se receber o valor nulo como parâmetro.
   */
  public static LocalDateTime toLocalDateTime(Date date) {
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
  public static LocalDateTime toLocalDateTime(Date date, ZoneId zone) {
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
  public static LocalDateTime toLocalDateTime(Timestamp stamp) throws RFWException {
    if (stamp == null) return null;
    return stamp.toLocalDateTime();
  }

  /**
   * Formata um número decimal para String com o número de casas decimais especificado, usando o Locale informado.
   *
   * @param number Número a ser formatado
   * @param locale Localização usada para formatação
   * @param maxDecimals Número máximo de casas decimais
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
}