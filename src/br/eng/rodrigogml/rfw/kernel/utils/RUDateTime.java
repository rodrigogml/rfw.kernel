package br.eng.rodrigogml.rfw.kernel.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilit�ria para facilitar manipula��o de datas e hor�rios.<br>
 *
 * @author Rodrigo Leit�o
 * @since 10.0 (22 de jul. de 2023)
 */
public class RUDateTime {

  private RUDateTime() {
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
   * Este m�todo formata um LocalDate para com o patern 'ddMMyyyy'.
   *
   * @param date Objeto LocalDate com a data � formatar.
   * @return String com o Dado Formatado.
   */
  public static String formatToddMMyyyy(LocalDate date) {
    return formatLocalDate(date, "ddMMyyyy");
  }

  /**
   * Este m�todo recebe uma data e for�a a defini��o de milisegundos = 000.
   *
   * @param date Data a ter o hor�rio modificado.
   * @return Novo objeto com a data recebida como parametro, mas com o hor�rio 00:00:00'000
   */
  public static Date setTimeMillisTo000(Date date) {
    final Calendar gc = GregorianCalendar.getInstance();
    gc.setTime(date);
    gc.set(Calendar.MILLISECOND, 000);
    return gc.getTime();
  }

  /**
   * Este m�todo recebe uma data e for�a a defini��o da hora = 00, minuto = 00 e segundos = 00 e milisegundos = 000.
   *
   * @param date Data a ter o hor�rio modificado.
   * @return Novo objeto com a data recebida como parametro, mas com o hor�rio 00:00:00'000
   */
  public static Date setTimeTo000000(Date date) {
    final Calendar gc = GregorianCalendar.getInstance();
    gc.setTime(date);
    gc.set(Calendar.HOUR_OF_DAY, 00);
    gc.set(Calendar.MINUTE, 00);
    gc.set(Calendar.SECOND, 00);
    gc.set(Calendar.MILLISECOND, 000);
    return gc.getTime();
  }

  /**
   * Este m�todo recebe uma data e for�a a defini��o da hora = 00, minuto = 00 e segundos = 00 e nanosegundos = 000.000.000.
   *
   * @param date Data a ter o hor�rio modificado.
   * @return Novo objeto com a data recebida como parametro, mas com o hor�rio 00:00:00'000
   */
  public static LocalDateTime setTimeTo000000(LocalDateTime date) {
    return date.withHour(0).withMinute(0).withSecond(0).withNano(0);
  }

  /**
   * Este m�todo recebe uma data e for�a a defini��o da hora = 23, minuto = 59 e segundos = 59 e milisegundos = 999.
   *
   * @param date Data a ter o hor�rio modificado.
   * @return Novo objeto com a data recebida como parametro, mas com o hor�rio 23:59:59'999
   */
  public static Date setTimeTo235959(Date date) {
    final Calendar gc = GregorianCalendar.getInstance();
    gc.setTime(date);
    gc.set(Calendar.HOUR_OF_DAY, 23);
    gc.set(Calendar.MINUTE, 59);
    gc.set(Calendar.SECOND, 59);
    gc.set(Calendar.MILLISECOND, 999);
    return gc.getTime();
  }

  /**
   * Este m�todo recebe uma data e for�a a defini��o da hora = 23, minuto = 59 e segundos = 59 e nanosegundos = 999.999.999.
   *
   * @param date Data a ter o hor�rio modificado.
   * @return Novo objeto com a data recebida como parametro, mas com o hor�rio 23:59:59'999
   */
  public static LocalDateTime setTimeTo235959(LocalDateTime date) {
    return date.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
  }

  /**
   * Converte um {@link LocalDate} para {@link Date} utilizando a Zona do Sistema {@link RFW#getZoneId()}.<br>
   * � considerada a hora zero do dia passaro na convers�o para a Zona.
   *
   * @param date Valor de Entrada a ser convertido
   * @return Valor Convertido
   */
  public static Date toDate(LocalDate date) {
    return Date.from(date.atStartOfDay().atZone(RFW.getZoneId()).toInstant());
  }

  /**
   * Converte um {@link LocalDate} para {@link Date} utilizando uma Zona personalizada.<br>
   * � considerada a hora zero do dia passaro na convers�o para a Zona.
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
   * Converte um {@link Date} em {@link LocalDate}. Utiliza a Zona padr�o do sistema
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
   * @param zone Zone para correta convers�o entre objetos temporais.
   * @return LocalDate conforme a zona, ou nulo caso date == null;
   */
  public static LocalDate toLocalDate(Date date, ZoneId zone) {
    if (date == null) return null;
    return date.toInstant().atZone(zone).toLocalDate();
  }

  /**
   * Converte um {@link java.sql.Date} em {@link LocalDate}. Utiliza a Zona padr�o do sistema
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
   * @return Objeto com o dia/hor�rio convertido para a zona solicitada, ou nulo se receber uma entrada nula.
   * @throws RFWException
   */
  public static LocalDate toLocalDate(Timestamp stamp) throws RFWException {
    if (stamp == null) return null;
    return stamp.toLocalDateTime().toLocalDate();
  }

  /**
   * Converte um {@link Date} em {@link LocalDateTime}. Utiliza a Zona padr�o do sistema {@link RFWDeprec#getZoneId()}.
   *
   * @param date Data a ser convertida em LocalDateTime
   * @return LocalDateTime conforme a zona, ou nulo se receber o valor nulo como par�metro.
   */
  public static LocalDateTime toLocalDateTime(Date date) {
    if (date == null) return null;
    return date.toInstant().atZone(RFW.getZoneId()).toLocalDateTime();
  }

  /**
   * Converte um {@link Date} em {@link LocalDateTime}.
   *
   * @param date Data a ser convertida em LocalDateTime
   * @param zone Zone para correta convers�o entre objetos temporais.
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
   * @return Objeto com o dia/hor�rio convertido para a zona solicitada, ou nulo se receber uma entrada nula.
   * @throws RFWException
   */
  public static LocalDateTime toLocalDateTime(Timestamp stamp) throws RFWException {
    if (stamp == null) return null;
    return stamp.toLocalDateTime();
  }

  /**
   * Verifica se o dia de uma determinada data � o �ltimo dia do m�s com base no calend�rio Gregoriano.
   *
   * @param date Data base para an�lise
   * @return true caso seja o �ltimo dia do m�s, false caso contr�rio.
   */
  public static boolean isLastDayOfMonth(Date date) {
    return getDayOfMonth(date) == getLastDayOfMonth(date);
  }

  /**
   * Retorna o dia do m�s de uma determinada data com base no calend�rio Gregoriano.
   *
   * @param date Data base extra��o do valor.
   * @return 1 para o primeiro dia do m�s, e assim sucessivamente at� 28, 29, 30 ou 31 para o �ltimo dia do m�s dependendo do m�s corrente.
   */
  public static int getDayOfMonth(Date date) {
    final Calendar c = GregorianCalendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Este m�todo interpreta todos os formatos j� encontrados de datas que podem vir na NFe para o formato do Java. Atualmente os formatos reconhecidos s�o:<br>
   * <li>"yyyy-MM-dd'T'HH:mm:ssXXX", onde XXX � algo como "-07:00" (Padr�o UTC)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ssZ", onde Z � algo como "-0700" (Padr�o UTC)</li>
   * <li>"yyyy-MM-dd'T'HH:mm:ss" (Padr�o UTC Sem TimeZone)</li>
   * <li>"yyyy-MM-dd"</li>
   * <li>"dd/MM/yyyy"</li>
   *
   * @param date Data com os valores recebidos na String. Na aus�ncia de um TimeZone � considerado que o TImeZone
   * @return
   * @throws RFWException
   */
  public static Date parseDate(String date) throws RFWException {
    if (date != null) {
      if (date.matches("[1-2][0-9]{3}\\-[0-1][0-9]\\-[0-3][0-9]")) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7)) - 1;
        int day = Integer.parseInt(date.substring(8, 10));
        GregorianCalendar gc = new GregorianCalendar(year, month, day);
        return gc.getTime();
      } else if (date.matches("[1-2][0-9]{3}\\-[0-1][0-9]\\-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9]:[0-5][0-9]")) {
        try {
          final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
          return df.parse(date);
        } catch (ParseException e) {
          throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
        }
      } else if (date.matches("[1-2][0-9]{3}\\-[0-1][0-9]\\-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9](\\-|\\+)[0-2][0-9][0-5][0-9]")) {
        try {
          final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
          return df.parse(date);
        } catch (ParseException e) {
          throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
        }
      } else if (date.matches("[1-2][0-9]{3}\\-[0-1][0-9]\\-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]")) {
        try {
          final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          return df.parse(date);
        } catch (ParseException e) {
          throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
        }
      } else if (date.matches("[0-3][0-9]/[0-1][0-9]/[1-2][0-9]{3}")) {
        try {
          final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
          return df.parse(date);
        } catch (ParseException e) {
          throw new RFWCriticalException("Falha ao realizar o parser da data. Data '${0}'.", new String[] { date }, e);
        }
      } else {
        throw new RFWValidationException("Formato da Data n�o est� em formato n�o suportado por este m�todo. Data: '${0}'", new String[] { date });
      }
    }
    return null;

  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'yyyyMMddHHmmss'.
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
   * Chama o m�todo {@link #parseDate(String)} e converte para um {@link LocalDateTime} considerando o ZoneID configurado em {@link RFWDeprec#getZoneId()}<br>
   * Verifique os padr�es de entrada suportados na documenta��o do {@link #parseDate(String)}
   *
   * @param date Objeto data a ser convertido, j� com a defini��o de fuso embutida.
   * @return Objeto convertido ou nulo caso receba uma entrada nula.
   * @throws RFWException
   */
  public static LocalDate parseLocalDate(String date) throws RFWException {
    if (date == null) return null;
    return toLocalDateTime(parseDate(date)).toLocalDate();
  }

  /**
   * Realiza o parser da String utilizando o {@link DateTimeFormatter}.<Br>
   *
   * @param date Data no formato String que precisa ser lida.
   * @param pattern no Formado espec�ficado pela documenta��o do m�todo {@link DateTimeFormatter#ofPattern(String)}.
   * @return Objeto Com o a Data.
   * @throws RFWException
   */
  public static LocalDate parseLocalDate(String date, String pattern) throws RFWException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return LocalDate.parse(date, formatter);
  }

  /**
   * Chama o m�todo {@link #parseDate(String)} e converte para um {@link LocalDateTime} considerando o ZoneID configurado em {@link RFWDeprec#getZoneId()}<br>
   * Verifique os padr�es de entrada suportados na documenta��o do {@link #parseDate(String)}
   *
   * @param date Objeto data a ser convertido, j� com a defini��o de fuso embutida.
   * @param zoneID Especifica��o da Zona (fuso hor�rio) para o quam desejamos converter a hora.
   * @return Objeto convertido ou nulo casa date == null;
   * @throws RFWException
   */
  public static LocalDate parseLocalDate(String date, ZoneId zoneID) throws RFWException {
    if (date == null) return null;
    return toLocalDateTime(parseDate(date), zoneID).toLocalDate();
  }

  /**
   * Chama o m�todo {@link #parseDate(String)} e converte para um {@link LocalDateTime} considerando o ZoneID configurado em {@link RFWDeprec#getZoneId()}<br>
   * Verifique os padr�es de entrada suportados na documenta��o do {@link #parseDate(String)}
   *
   * @param date Objeto data a ser convertido, j� com a defini��o de fuso embutida.
   * @return retorna LocalDateTime com o hor�rio retirado da String. Se recebido o valor nulo, retorna nulo.
   * @throws RFWException
   */
  public static LocalDateTime parseLocalDateTime(String date) throws RFWException {
    if (date == null) return null;
    return toLocalDateTime(parseDate(date));
  }

  /**
   * Chama o m�todo {@link #parseDate(String)} e converte para um {@link LocalDateTime} considerando o ZoneID configurado em {@link RFWDeprec#getZoneId()}<br>
   * Verifique os padr�es de entrada suportados na documenta��o do {@link #parseDate(String)}
   *
   * @param date Objeto data a ser convertido, j� com a defini��o de fuso embutida.
   * @param zoneID Especifica��o da Zona (fuso hor�rio) para o quam desejamos converter a hora.
   * @return Objeto convertido ou nulo caso receba date == null.
   * @throws RFWException
   */
  public static LocalDateTime parseLocalDateTime(String date, ZoneId zoneID) throws RFWException {
    if (date == null) return null;
    return toLocalDateTime(parseDate(date), zoneID);
  }

  /**
   * Retorna muda o dia para o primeiro dia do M�s da data passada.<br>
   * ATEN��O: este m�todo n�o altera o tempo. Se desejar colocar o primeiro momento do m�s utilize em conjunto com o m�todo {@link #setTimeTo000000(Date)};
   *
   * @param date Data de refer�ncia
   * @return
   */
  public static Date getFirstDateOfMonth(Date date) {
    final Calendar gc = GregorianCalendar.getInstance();
    gc.setTime(date);
    gc.set(Calendar.DAY_OF_MONTH, 1);
    return gc.getTime();
  }

  /**
   * Retorna muda o dia para o �ltimo dia do M�s da data passada.<br>
   * ATEN��O: este m�todo n�o altera o tempo. Se desejar colocar o �ltimo momento do m�s utilize em conjunto com o m�todo {@link #setsetTimeTo235959(Date)};
   *
   * @param date Data de refer�ncia
   * @return
   */
  public static Date getLastDateOfMonth(Date date) {
    final Calendar gc = GregorianCalendar.getInstance();
    gc.setTime(date);
    gc.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(date));
    return gc.getTime();
  }

  /**
   * Recupera o �ltimo dia do m�s de uma determinada data, de acordo com o calend�rio Gregoriano.
   *
   * @param date Data a ser examinada, ser� analizado o m�s e o ano desta data para determinar o �ltimo dia do m�s.
   * @return 28, 29, 30 ou 31 de acordo com o m�s e ano da data passada, indicando o �ltimo dia do m�s.
   */
  public static int getLastDayOfMonth(Date date) {
    final Calendar c = GregorianCalendar.getInstance();
    c.setTime(date);
    return c.getActualMaximum(Calendar.DAY_OF_MONTH);
  }

  /**
   * Recupera o �ltimo dia do m�s de uma determinada data, de acordo com o calend�rio Gregoriano.
   *
   * @param date Data a ser examinada, ser� analizado o m�s e o ano desta data para determinar o �ltimo dia do m�s.
   * @return 28, 29, 30 ou 31 de acordo com o m�s e ano da data passada, indicando o �ltimo dia do m�s.
   */
  public static int getLastDayOfMonth(LocalDate date) {
    return date.plusMonths(1).withDayOfMonth(1).plusDays(-1).getDayOfMonth();
  }

  /**
   * Verifica se o dia de uma determinada data � o primeiro dia do m�s com base no calend�rio Gregoriano.
   *
   * @param date Data base para an�lise
   * @return true caso seja o primeiro dia do m�s, false caso contr�rio.
   */
  public static boolean isFirstDayOfMonth(Date date) {
    return getDayOfMonth(date) == 1;
  }

  /**
   * Este m�todo valida se uma data est� dentro de um determinado periodo.<br>
   * As datas do periodo s�o inclusivas, isto �, se date for igual a startPeriod ou endPeriod o m�todo retornar� true.
   *
   * @param date Data para averigua��o se est� dentro do per�odo.
   * @param startPeriod Data de in�cio do per�odo. Se passado nulo considera que o per�odo come�ou em "menos infinito".
   * @param endPeriod Data de fim do per�odo. Se passado nulo condera que o per�odo nunca termina.
   * @return true caso a data esteja dentro do perioro, false caso contr�rio.
   */
  public static boolean isInsidePeriod(Date date, Date startPeriod, Date endPeriod) {
    return (startPeriod == null || date.compareTo(startPeriod) >= 0) && (endPeriod == null || date.compareTo(endPeriod) <= 0);
  }

  /**
   * Este m�todo valida se uma data est� dentro de um determinado periodo.<br>
   * As datas do periodo s�o inclusivas, isto �, se date for igual a startPeriod ou endPeriod o m�todo retornar� true.<br>
   *
   * @param date Data para averigua��o se est� dentro do per�odo.
   * @param startPeriod Data de in�cio do per�odo. Se passado nulo considera que o per�odo come�ou em "menos infinito".
   * @param endPeriod Data de fim do per�odo. Se passado nulo condera que o per�odo nunca termina.
   * @return true caso a data esteja dentro do perioro, false caso contr�rio.
   */
  public static boolean isInsidePeriod(LocalDate date, LocalDate startPeriod, LocalDate endPeriod) {
    return (startPeriod == null || date.compareTo(startPeriod) >= 0) && (endPeriod == null || date.compareTo(endPeriod) <= 0);
  }

  /**
   * Este m�todo valida se uma data est� dentro de um determinado periodo.<br>
   * As datas do periodo s�o inclusivas, isto �, se date for igual a startPeriod ou endPeriod o m�todo retornar� true.
   *
   * @param date Data para averigua��o se est� dentro do per�odo.
   * @param startPeriod Data de in�cio do per�odo. Se passado nulo considera que o per�odo come�ou em "menos infinito".
   * @param endPeriod Data de fim do per�odo. Se passado nulo condera que o per�odo nunca termina.
   * @return true caso a data esteja dentro do perioro, false caso contr�rio.
   */
  public static boolean isInsidePeriod(LocalDateTime date, LocalDateTime startPeriod, LocalDateTime endPeriod) {
    return (startPeriod == null || date.compareTo(startPeriod) >= 0) && (endPeriod == null || date.compareTo(endPeriod) <= 0);
  }

  /**
   * Cria um objeto Date com uma data espec�fica.
   *
   * @param year O ano a ser utilizado na data.
   * @param month O M�s do ano a ser utilizado. 1 para janeiro e 12 para Dezembro.
   * @param dayOfMonth Dia do m�s a set utilizado. 1 para dia 1, e assim sucessivamente.
   * @param hour Hora do dia a ser utilizado, variando de 0 � 23.
   * @param minute Minuto a ser utilizado na data.
   * @param second Segundos a ser utilizado na data.
   * @param milliseconds Milesegundos a ser utilizado na data.
   * @return Objeto Data com a data configurada.
   */
  public static Date createDate(int year, int month, int dayOfMonth, int hour, int minute, int second, int milliseconds) {
    final Calendar c = GregorianCalendar.getInstance();
    c.set(year, month - 1, dayOfMonth, hour, minute, second);
    c.set(Calendar.MILLISECOND, milliseconds);
    return c.getTime();
  }

  /**
   * Cria um objeto LocalDate com uma data espec�fica.
   *
   * @param year O ano a ser utilizado na data.
   * @param month O M�s do ano a ser utilizado. 1 para janeiro e 12 para Dezembro.
   * @param dayOfMonth Dia do m�s a set utilizado. 1 para dia 1, e assim sucessivamente.
   * @return Objeto Data com a data configurada.
   */
  public static LocalDate createLocalDate(int year, int month, int dayOfMonth) {
    return LocalDate.of(year, month, dayOfMonth);
  }

  /**
   * Cria um objeto LocalDateTime com uma data espec�fica.
   *
   * @param year O ano a ser utilizado na data.
   * @param month O M�s do ano a ser utilizado. 1 para janeiro e 12 para Dezembro.
   * @param dayOfMonth Dia do m�s a set utilizado. 1 para dia 1, e assim sucessivamente.
   * @param hour Hora do dia a ser utilizado, variando de 0 � 23.
   * @param minute Minuto a ser utilizado na data.
   * @param second Segundos a ser utilizado na data.
   * @param milliseconds Milesegundos a ser utilizado na data.
   * @param nanoOfSecond Nanos segundos.
   * @return Objeto Data com a data configurada.
   */
  public static LocalDateTime createLocalDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second, int milliseconds, int nanoOfSecond) {
    return LocalDateTime.of(year, month, dayOfMonth, hour, minute, milliseconds, nanoOfSecond);
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date apenas no hor�rio com o patern '23:59:59', ignorando qualquer que seja a data.
   *
   * @param time
   * @return
   */
  public static String formatTo235959(Date time) {
    return new SimpleDateFormat("HH:mm:ss").format(time);
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'yyyyMMddHHmmss'.
   *
   * @param date
   * @return
   */
  public static String formatToyyyyMMddHHmmss(Date date) {
    return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'yyyyMMdd'.
   *
   * @param date
   * @return
   */
  public static String formatToyyyyMMdd(Date date) {
    return new SimpleDateFormat("yyyyMMdd").format(date);
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'ddMMyyyy'.
   *
   * @param date
   * @return
   */
  public static String formatToddMMyyyy(Date date) {
    return new SimpleDateFormat("ddMMyyyy").format(date);
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date em um TimeStamp com o patern 'ddMMyyyyHHmmss'.
   *
   * @param date
   * @return
   */
  public static String formatToddMMyyyyHHmmss(Date date) {
    return new SimpleDateFormat("ddMMyyyyHHmmss").format(date);
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatTodd_MM_yyyy_HH_mm_ss(Date date) {
    return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'dd/MM/yyyy HH:mm:ss'.
   *
   * @param date
   * @return
   */
  public static String formatTodd_MM_yyyy_HH_mm_ss(LocalDateTime date) {
    return formatLocalDateTime(date, "dd/MM/yyyy HH:mm:ss");
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'yyyy-MM-dd'T'HH:mm:ssXXX' (Padr�o UTC utilizado no XML da NFe).
   *
   * @param date
   * @return
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(Date date) {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date);
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date em um formato completo com o patern 'yyyy-MM-dd'T'HH:mm:ssXXX' (Padr�o UTC utilizado no XML da NFe).
   *
   * @param date
   * @return
   */
  public static String formatToyyyy_MM_dd_T_HH_mm_ssXXX(LocalDateTime date, ZoneId zoneId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    return date.atZone(zoneId).format(formatter);
  }

  /**
   * Este m�todo utiliza o SimpleDateFormat para formar o Date apenas no hor�rio com o patern '23:59:59', ignorando qualquer que seja a data.
   *
   * @param dLastConsult
   * @return
   */
  public static String formatTo235959(long timemillis) {
    return formatTo235959(new Date(timemillis));
  }
}
