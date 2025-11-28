package br.eng.rodrigogml.rfw.kernel.utils;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;
import br.eng.rodrigogml.rfw.kernel.preprocess.PreProcess;

/**
 * Description: Classe utilitária para facilitar manipulação de datas e horários.<br>
 *
 * @author Rodrigo Leitão
 * @since 10.0 (22 de jul. de 2023)
 */
public class RUDateTime {

  private RUDateTime() {
  }

  /**
   * Compara duas datas ignorando a parte de tempo (horas, minutos, segundos e milissegundos).
   *
   * <p>
   * Retorna:
   * <ul>
   * <li>Valor negativo se {@code date1} for anterior a {@code date2}.</li>
   * <li>Zero se ambas as datas forem iguais.</li>
   * <li>Valor positivo se {@code date1} for posterior a {@code date2}.</li>
   * </ul>
   *
   * @param date1 A primeira data a ser comparada (não pode ser nula).
   * @param date2 A segunda data a ser comparada (não pode ser nula).
   * @return Um valor negativo, zero ou positivo conforme {@code date1} for antes, igual ou depois de {@code date2}.
   * @throws NullPointerException Se {@code date1} ou {@code date2} forem nulos.
   */
  public static int compareDateWithoutTime(Date date1, Date date2) {
    LocalDate localDate1 = date1.toInstant().atZone(RFW.getZoneId()).toLocalDate();
    LocalDate localDate2 = date2.toInstant().atZone(RFW.getZoneId()).toLocalDate();
    return localDate1.compareTo(localDate2);
  }

  /**
   * Este método recebe uma data e força a definição de milisegundos = 000.
   *
   * @param date Data a ter o horário modificado.
   * @return Novo objeto com a data recebida como parametro, mas com o horário 00:00:00'000
   */
  public static Date setTimeMillisTo000(Date date) {
    final Calendar gc = GregorianCalendar.getInstance();
    gc.setTime(date);
    gc.set(Calendar.MILLISECOND, 000);
    return gc.getTime();
  }

  /**
   * Ajusta uma data para ter o horário zerado: 00:00:00.000.
   *
   * @param date Data a ser ajustada. Não pode ser nula.
   * @return Nova instância de {@link Date} com a mesma data informada, mas com o horário definido como 00:00:00.000.
   * @throws RFWException
   */
  public static Date setTimeTo000000(Date date) throws RFWException {
    PreProcess.requiredNonNull(date);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * Este método recebe uma data e força a definição da hora = 00, minuto = 00 e segundos = 00 e nanosegundos = 000.000.000.
   *
   * @param date Data a ter o horário modificado.
   * @return Novo objeto com a data recebida como parametro, mas com o horário 00:00:00'000
   */
  public static LocalDateTime setTimeTo000000(LocalDateTime date) {
    return date.withHour(0).withMinute(0).withSecond(0).withNano(0);
  }

  /**
   * Este método recebe uma data e força a definição da hora = 23, minuto = 59 e segundos = 59 e milisegundos = 999.
   *
   * @param date Data a ter o horário modificado.
   * @return Novo objeto com a data recebida como parametro, mas com o horário 23:59:59'999
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
   * Este método recebe uma data e força a definição da hora = 23, minuto = 59 e segundos = 59 e nanosegundos = 999.999.999.
   *
   * @param date Data a ter o horário modificado.
   * @return Novo objeto com a data recebida como parametro, mas com o horário 23:59:59'999
   */
  public static LocalDateTime setTimeTo235959(LocalDateTime date) {
    return date.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
  }

  /**
   * Verifica se o dia de uma determinada data é o último dia do mês com base no calendário Gregoriano.
   *
   * @param date Data base para análise
   * @return true caso seja o último dia do mês, false caso contrário.
   */
  public static boolean isLastDayOfMonth(Date date) {
    return getDayOfMonth(date) == getLastDayOfMonth(date);
  }

  /**
   * Retorna o dia do mês de uma determinada data com base no calendário Gregoriano.
   *
   * @param date Data base extração do valor.
   * @return 1 para o primeiro dia do mês, e assim sucessivamente até 28, 29, 30 ou 31 para o último dia do mês dependendo do mês corrente.
   */
  public static int getDayOfMonth(Date date) {
    final Calendar c = GregorianCalendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Retorna muda o dia para o primeiro dia do Mês da data passada.<br>
   * ATENÇÃO: este método não altera o tempo. Se desejar colocar o primeiro momento do mês utilize em conjunto com o método {@link #setTimeTo000000(Date)};
   *
   * @param date Data de referência
   * @return
   */
  public static Date getFirstDateOfMonth(Date date) {
    final Calendar gc = GregorianCalendar.getInstance();
    gc.setTime(date);
    gc.set(Calendar.DAY_OF_MONTH, 1);
    return gc.getTime();
  }

  /**
   * Retorna muda o dia para o último dia do Mês da data passada.<br>
   * ATENÇÃO: este método não altera o tempo. Se desejar colocar o último momento do mês utilize em conjunto com o método {@link #setsetTimeTo235959(Date)};
   *
   * @param date Data de referência
   * @return
   */
  public static Date getLastDateOfMonth(Date date) {
    final Calendar gc = GregorianCalendar.getInstance();
    gc.setTime(date);
    gc.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(date));
    return gc.getTime();
  }

  /**
   * Recupera o último dia do mês de uma determinada data, de acordo com o calendário Gregoriano.
   *
   * @param date Data a ser examinada, será analizado o mês e o ano desta data para determinar o último dia do mês.
   * @return 28, 29, 30 ou 31 de acordo com o mês e ano da data passada, indicando o último dia do mês.
   */
  public static int getLastDayOfMonth(Date date) {
    final Calendar c = GregorianCalendar.getInstance();
    c.setTime(date);
    return c.getActualMaximum(Calendar.DAY_OF_MONTH);
  }

  /**
   * Recupera o último dia do mês de uma determinada data, de acordo com o calendário Gregoriano.
   *
   * @param date Data a ser examinada, será analizado o mês e o ano desta data para determinar o último dia do mês.
   * @return 28, 29, 30 ou 31 de acordo com o mês e ano da data passada, indicando o último dia do mês.
   */
  public static int getLastDayOfMonth(LocalDate date) {
    return date.plusMonths(1).withDayOfMonth(1).plusDays(-1).getDayOfMonth();
  }

  /**
   * Verifica se o dia de uma determinada data é o primeiro dia do mês com base no calendário Gregoriano.
   *
   * @param date Data base para análise
   * @return true caso seja o primeiro dia do mês, false caso contrário.
   */
  public static boolean isFirstDayOfMonth(Date date) {
    return getDayOfMonth(date) == 1;
  }

  /**
   * Este método valida se uma data está dentro de um determinado periodo.<br>
   * As datas do periodo são inclusivas, isto é, se date for igual a startPeriod ou endPeriod o método retornará true.
   *
   * @param date Data para averiguação se está dentro do período.
   * @param startPeriod Data de início do período. Se passado nulo considera que o período começou em "menos infinito".
   * @param endPeriod Data de fim do período. Se passado nulo condera que o período nunca termina.
   * @return true caso a data esteja dentro do perioro, false caso contrário.
   */
  public static boolean isInsidePeriod(Date date, Date startPeriod, Date endPeriod) {
    return (startPeriod == null || date.compareTo(startPeriod) >= 0) && (endPeriod == null || date.compareTo(endPeriod) <= 0);
  }

  /**
   * Este método valida se uma data está dentro de um determinado periodo.<br>
   * As datas do periodo são inclusivas, isto é, se date for igual a startPeriod ou endPeriod o método retornará true.<br>
   *
   * @param date Data para averiguação se está dentro do período.
   * @param startPeriod Data de início do período. Se passado nulo considera que o período começou em "menos infinito".
   * @param endPeriod Data de fim do período. Se passado nulo condera que o período nunca termina.
   * @return true caso a data esteja dentro do perioro, false caso contrário.
   */
  public static boolean isInsidePeriod(LocalDate date, LocalDate startPeriod, LocalDate endPeriod) {
    return (startPeriod == null || date.compareTo(startPeriod) >= 0) && (endPeriod == null || date.compareTo(endPeriod) <= 0);
  }

  /**
   * Este método valida se uma data está dentro de um determinado periodo.<br>
   * As datas do periodo são inclusivas, isto é, se date for igual a startPeriod ou endPeriod o método retornará true.
   *
   * @param date Data para averiguação se está dentro do período.
   * @param startPeriod Data de início do período. Se passado nulo considera que o período começou em "menos infinito".
   * @param endPeriod Data de fim do período. Se passado nulo condera que o período nunca termina.
   * @return true caso a data esteja dentro do perioro, false caso contrário.
   */
  public static boolean isInsidePeriod(LocalDateTime date, LocalDateTime startPeriod, LocalDateTime endPeriod) {
    return (startPeriod == null || date.compareTo(startPeriod) >= 0) && (endPeriod == null || date.compareTo(endPeriod) <= 0);
  }

  /**
   * Cria um objeto Date com uma data específica.
   *
   * @param year O ano a ser utilizado na data.
   * @param month O Mês do ano a ser utilizado. 1 para janeiro e 12 para Dezembro.
   * @param dayOfMonth Dia do mês a set utilizado. 1 para dia 1, e assim sucessivamente.
   * @param hour Hora do dia a ser utilizado, variando de 0 à 23.
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
   * Cria um objeto LocalDate com uma data específica.
   *
   * @param year O ano a ser utilizado na data.
   * @param month O Mês do ano a ser utilizado. 1 para janeiro e 12 para Dezembro.
   * @param dayOfMonth Dia do mês a set utilizado. 1 para dia 1, e assim sucessivamente.
   * @return Objeto Data com a data configurada.
   */
  public static LocalDate createLocalDate(int year, int month, int dayOfMonth) {
    return LocalDate.of(year, month, dayOfMonth);
  }

  /**
   * Cria um objeto LocalDateTime com uma data específica.
   *
   * @param year O ano a ser utilizado na data.
   * @param month O Mês do ano a ser utilizado. 1 para janeiro e 12 para Dezembro.
   * @param dayOfMonth Dia do mês a set utilizado. 1 para dia 1, e assim sucessivamente.
   * @param hour Hora do dia a ser utilizado, variando de 0 à 23.
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
   * Calcula quantos minutos se passaram desde o tempo especificado até o momento atual.
   *
   * @param milliseconds O timestamp de referência em milissegundos (epoch time).
   * @return O número de minutos decorridos desde o tempo especificado até agora.
   */
  public static double countMinutesFrom(long milliseconds) {
    return (System.currentTimeMillis() - milliseconds) / 60000.0;
  }

  /**
   * Calcula o número de meses entre duas datas.
   * <p>
   * Mesmo que as datas sejam diferentes, se estiverem dentro do mesmo mês e ano, o valor retornado será zero.
   * </p>
   * <p>
   * Caso a data final esteja em um mês anterior à data inicial, o valor retornado será negativo.
   * </p>
   *
   * @param initialDate Data inicial (não pode ser nula).
   * @param finalDate Data final (não pode ser nula).
   * @return Número de meses completos entre as duas datas.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static long calcDifferenceInMonths(LocalDate initialDate, LocalDate finalDate) {
    return Period.between(initialDate.withDayOfMonth(1), finalDate.withDayOfMonth(1)).toTotalMonths();
  }

  /**
   * Calcula o número de dias completos entre duas datas.
   * <p>
   * O cálculo considera apenas períodos completos de 24 horas.
   * </p>
   *
   * @param initialDate Data inicial (inclusivo, não pode ser nula).
   * @param finalDate Data final (exclusivo, não pode ser nula).
   * @return Total de dias completos entre a data inicial e a final.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static long calcDifferenceInDays(LocalDate initialDate, LocalDate finalDate) {
    return ChronoUnit.DAYS.between(initialDate, finalDate);
  }

  /**
   * Calcula o número de dias completos entre duas datas e horários.
   * <p>
   * O cálculo considera apenas períodos completos de 24 horas.
   * </p>
   *
   * @param initialDate Data e hora inicial (não pode ser nula).
   * @param finalDate Data e hora final (não pode ser nula).
   * @return Total de dias completos entre as datas.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static long calcDifferenceInDays(LocalDateTime initialDate, LocalDateTime finalDate) {
    return ChronoUnit.DAYS.between(initialDate, finalDate);
  }

  /**
   * Calcula a diferença entre duas datas em dias.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em relação à data final.
   * </p>
   *
   * @param initialDate Data inicial (não pode ser nula).
   * @param finalDate Data final (não pode ser nula).
   * @return Diferença entre as datas em dias.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static double calcDifferenceInDays(Date initialDate, Date finalDate) {
    return calcDifferenceInDays(initialDate.getTime(), finalDate.getTime());
  }

  /**
   * Calcula a diferença entre duas datas em dias.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em relação à data final.
   * </p>
   *
   * @param initialDate Timestamp inicial (epoch time).
   * @param finalDate Timestamp final (epoch time).
   * @return Diferença entre as datas em dias.
   */
  public static double calcDifferenceInDays(long initialDate, long finalDate) {
    return (finalDate - initialDate) / 86400000.0; // 1 dia = 86.400.000ms
  }

  /**
   * Calcula a diferença entre duas datas em horas.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em relação à data final.
   * </p>
   *
   * @param initialDate Data inicial (não pode ser nula).
   * @param finalDate Data final (não pode ser nula).
   * @return Diferença entre as datas em horas.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static double calcDifferenceInHours(Date initialDate, Date finalDate) {
    return calcDifferenceInHours(initialDate.getTime(), finalDate.getTime());
  }

  /**
   * Calcula a diferença entre duas datas em horas.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em relação à data final.
   * </p>
   *
   * @param initialDate Timestamp inicial (epoch time).
   * @param finalDate Timestamp final (epoch time).
   * @return Diferença entre as datas em horas.
   */
  public static double calcDifferenceInHours(long initialDate, long finalDate) {
    return (finalDate - initialDate) / 3600000.0; // 1 hora = 3.600.000ms
  }

  /**
   * Calcula a diferença entre duas datas em minutos.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em relação à data final.
   * </p>
   *
   * @param initialDate Data inicial (não pode ser nula).
   * @param finalDate Data final (não pode ser nula).
   * @return Diferença entre as datas em minutos.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static double calcDifferenceInMinutes(Date initialDate, Date finalDate) {
    return calcDifferenceInMinutes(initialDate.getTime(), finalDate.getTime());
  }

  /**
   * Calcula a diferença entre dois timestamps em minutos.
   * <p>
   * Retorna um valor negativo caso o timestamp inicial seja maior que o final.
   * </p>
   *
   * @param initialDate Timestamp inicial (epoch time).
   * @param finalDate Timestamp final (epoch time).
   * @return Diferença entre os timestamps em minutos.
   */
  public static double calcDifferenceInMinutes(long initialDate, long finalDate) {
    return (finalDate - initialDate) / 60000.0; // 1 minuto = 60.000ms
  }

  /**
   * Adiciona ou subtrai um período a uma data.
   * <p>
   * Permite modificar a data base somando ou subtraindo dias, meses, anos, horas, minutos, etc.
   * </p>
   *
   * @param date Data base para a operação (não pode ser nula).
   * @param period Define o período a ser adicionado/subtraído. Valores podem ser encontrados em {@link Calendar}, ex: {@link Calendar#MONTH}.
   * @param amount Quantidade do período a ser somado/subtraído. Valores negativos subtraem da data.
   * @return Nova data com o período ajustado.
   * @throws NullPointerException Se {@code date} for nulo.
   */
  public static Date calcDateAdd(Date date, int period, int amount) {
    Calendar gc = Calendar.getInstance();
    gc.setTime(date);
    gc.add(period, amount);
    return gc.getTime();
  }

  /**
   * Retorna o número do mês de uma determinada data no calendário Gregoriano.
   *
   * @param date Data base para extração do valor (não pode ser nula).
   * @return Número do mês, onde 1 representa Janeiro e 12 representa Dezembro.
   * @throws NullPointerException Se {@code date} for nula.
   */
  public static int getMonth(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.MONTH) + 1; // Soma 1 porque a função do Java retorna de 0 a 11.
  }

  /**
   * Retorna o ano de uma determinada data no calendário Gregoriano.
   *
   * @param date Data base para extração do valor (não pode ser nula).
   * @return O número do ano com 4 dígitos.
   * @throws NullPointerException Se {@code date} for nula.
   */
  public static int getYear(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.YEAR);
  }

  /**
   * Retorna um {@link LocalDate} com o último dia do mês de uma determinada data, de acordo com o calendário Gregoriano.
   *
   * @param date Data base para determinar o último dia do mês (não pode ser nula).
   * @return Último dia do mês correspondente à data informada.
   * @throws NullPointerException Se {@code date} for nula.
   */
  public static LocalDate getLastLocalDateOfMonth(LocalDateTime date) {
    return getLastLocalDateOfMonth(date.toLocalDate());
  }

  /**
   * Retorna um {@link LocalDate} com o último dia do mês de uma determinada data, de acordo com o calendário Gregoriano.
   *
   * @param date Data base para determinar o último dia do mês (não pode ser nula).
   * @return Último dia do mês correspondente à data informada.
   * @throws NullPointerException Se {@code date} for nula.
   */
  public static LocalDate getLastLocalDateOfMonth(LocalDate date) {
    return date.withDayOfMonth(date.lengthOfMonth());
  }

  /**
   * Retorna o nome do mês por extenso, de acordo com o {@code Locale} informado.
   *
   * @param locale Locale para tradução do nome do mês (não pode ser nulo).
   * @param month Mês do calendário gregoriano (1 para Janeiro, 12 para Dezembro).
   * @return Nome completo do mês no idioma correspondente ao {@code Locale}.
   * @throws IllegalArgumentException Se o mês estiver fora do intervalo 1-12.
   */
  public static String getMonthName(Locale locale, int month) {
    if (month < 1 || month > 12) throw new IllegalArgumentException("Mês inválido: " + month);
    return new DateFormatSymbols(locale).getMonths()[month - 1];
  }

  /**
   * Retorna o nome abreviado (normalmente com 3 letras) do mês, de acordo com o {@code Locale} informado.
   *
   * @param locale Locale para tradução do nome do mês (não pode ser nulo).
   * @param month Mês do calendário gregoriano (1 para Janeiro, 12 para Dezembro).
   * @return Nome abreviado do mês no idioma correspondente ao {@code Locale}.
   * @throws IllegalArgumentException Se o mês estiver fora do intervalo 1-12.
   */
  public static String getMonthShortName(Locale locale, int month) {
    if (month < 1 || month > 12) throw new IllegalArgumentException("Mês inválido: " + month);
    return new DateFormatSymbols(locale).getShortMonths()[month - 1];
  }

  /**
   * Retorna o dia da semana de uma determinada data.
   *
   * @param date Data base para obtenção do dia da semana (não pode ser nula).
   * @return Inteiro representando o dia da semana, conforme {@link Calendar}.
   */
  public static int getWeekDay(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * Calcula a sobreposição de dias entre dois períodos de tempo.
   * <p>
   * As datas são inclusivas, ou seja, se os períodos estiverem encostados, a sobreposição será de 1 dia.
   * </p>
   *
   * @param period1Start Início do primeiro período.
   * @param period1End Fim do primeiro período.
   * @param period2Start Início do segundo período.
   * @param period2End Fim do segundo período.
   * @return Número de dias que os períodos se sobrepõem.
   * @throws RFWValidationException Se a data final de um período for anterior à sua data inicial.
   */
  public static long calcOverlappingDays(LocalDate period1Start, LocalDate period1End, LocalDate period2Start, LocalDate period2End) throws RFWValidationException {
    if (period1End.isBefore(period1Start)) throw new RFWValidationException("A data de fim do primeiro período é anterior à data de início!");
    if (period2End.isBefore(period2Start)) throw new RFWValidationException("A data de fim do segundo período é anterior à data de início!");

    if (period1End.isBefore(period2Start) || period2End.isBefore(period1Start)) return 0;

    LocalDate start = period1Start.isAfter(period2Start) ? period1Start : period2Start;
    LocalDate end = period1End.isBefore(period2End) ? period1End : period2End;

    return ChronoUnit.DAYS.between(start, end.plusDays(1));
  }

  /**
   * Adiciona ou subtrai um período específico a uma data fornecida.
   *
   * @param baseDate Data base para a operação.
   * @param period Tipo do período a ser adicionado (Calendar.DAY_OF_MONTH, Calendar.MONTH, etc.).
   * @param amount Quantidade do período a ser somado ou subtraído (valores negativos subtraem).
   * @return Nova data resultante da operação.
   * @throws IllegalArgumentException Se a data fornecida for nula.
   */
  public static Date dateAdd(Date baseDate, int period, int amount) {
    if (baseDate == null) {
      throw new IllegalArgumentException("A data base não pode ser nula.");
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(baseDate);
    calendar.add(period, amount);
    return calendar.getTime();
  }

  /**
   * Calcula a diferença entre duas datas em dias. Retorna um valor negativo se a data inicial for posterior à data final.
   *
   * @param initialDate Data inicial
   * @param finalDate Data final
   * @return Diferença de dias entre as datas (pode ser negativa se initialDate > finalDate)
   */
  public static double dateDifferenceInDays(Date initialDate, Date finalDate) {
    return dateDifferenceInDays(initialDate.getTime(), finalDate.getTime());
  }

  /**
   * Calcula a diferença entre dois timestamps em dias. Retorna um valor negativo se o timestamp inicial for posterior ao final.
   *
   * @param initialTimestamp Timestamp inicial em milissegundos
   * @param finalTimestamp Timestamp final em milissegundos
   * @return Diferença de dias entre os timestamps (pode ser negativa se initialTimestamp > finalTimestamp)
   */
  public static double dateDifferenceInDays(long initialTimestamp, long finalTimestamp) {
    return (finalTimestamp - initialTimestamp) / 86_400_000.0; // 1 dia = 86.400.000ms
  }

  /**
   * Calcula a diferença entre duas datas em horas. Retorna um valor negativo caso a data inicial seja posterior à data final. Essa referência negativa pode ser útil para validar a ordem cronológica dos eventos.
   *
   * @param initialDate Data inicial.
   * @param finalDate Data final.
   * @return Diferença entre as datas em horas.
   */
  public static double dateDifferenceInHours(Date initialDate, Date finalDate) {
    return dateDifferenceInHours(initialDate.getTime(), finalDate.getTime());
  }

  /**
   * Calcula a diferença entre duas datas em horas a partir dos timestamps em milissegundos. Retorna um valor negativo caso a data inicial seja posterior à data final. Essa referência negativa pode ser útil para validar a ordem cronológica dos eventos.
   *
   * @param initialTimestamp Timestamp da data inicial em milissegundos.
   * @param finalTimestamp Timestamp da data final em milissegundos.
   * @return Diferença entre os timestamps em horas.
   */
  public static double dateDifferenceInHours(long initialTimestamp, long finalTimestamp) {
    return (finalTimestamp - initialTimestamp) / 3_600_000d; // 1 hora = 3.600.000 ms
  }

  /**
   * Calcula a diferença entre duas datas em minutos. Retorna um valor negativo caso a data inicial seja posterior à data final. Essa referência negativa pode ser útil para validar a ordem cronológica dos eventos.
   *
   * @param initialDate Data inicial.
   * @param finalDate Data final.
   * @return Diferença entre as datas em minutos.
   */
  public static double dateDifferenceInMinutes(Date initialDate, Date finalDate) {
    return dateDifferenceInMinutes(initialDate.getTime(), finalDate.getTime());
  }

  /**
   * Calcula a diferença entre duas datas em minutos a partir dos timestamps em milissegundos. Retorna um valor negativo caso a data inicial seja posterior à data final. Essa referência negativa pode ser útil para validar a ordem cronológica dos eventos.
   *
   * @param initialTimestamp Timestamp da data inicial em milissegundos.
   * @param finalTimestamp Timestamp da data final em milissegundos.
   * @return Diferença entre os timestamps em minutos.
   */
  public static double dateDifferenceInMinutes(long initialTimestamp, long finalTimestamp) {
    return (finalTimestamp - initialTimestamp) / 60_000d; // 1 minuto = 60.000 ms
  }

  /**
   * Gera o sufixo do nome do arquivo baseado no período de datas fornecido.<br>
   * <ul>
   * <li>Se o período corresponde a um mês completo: "Mês-Ano" (exemplo: "Agosto-2017")</li>
   * <li>Caso contrário: "ddMMyyyy_ddMMyyyy" ou "ddMMyyyy" se a data de início e fim forem iguais</li>
   * </ul>
   * <Br>
   * <Br>
   * Faz o mesmo que o método {@link #getNameByDate(Date, Date, Locale)}, passando o valor de {@link RFW#getLocale()}.
   *
   * @param startDate Data de início do período.
   * @param endDate Data de fim do período.
   * @return Sufixo gerado de acordo com as regras descritas.
   */
  public static String getNameByDate(Date startDate, Date endDate) {
    return getNameByDate(startDate, endDate, RFW.getLocale());
  }

  /**
   * Gera o sufixo do nome do arquivo baseado no período de datas fornecido.<br>
   * <ul>
   * <li>Se o período corresponde a um mês completo: "Mês-Ano" (exemplo: "Agosto-2017")</li>
   * <li>Caso contrário: "ddMMyyyy_ddMMyyyy" ou "ddMMyyyy" se a data de início e fim forem iguais</li>
   * </ul>
   *
   * @param startDate Data de início do período.
   * @param endDate Data de fim do período.
   * @return Sufixo gerado de acordo com as regras descritas.
   */
  public static String getNameByDate(Date startDate, Date endDate, Locale locale) {
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("As datas não podem ser nulas.");
    }

    if (RUDateTime.isFirstDayOfMonth(startDate) &&
        RUDateTime.isLastDayOfMonth(endDate) &&
        RUDateTime.getMonth(startDate) == RUDateTime.getMonth(endDate) &&
        RUDateTime.getYear(startDate) == RUDateTime.getYear(endDate)) {

      return RUDateTime.getMonthName(locale, RUDateTime.getMonth(startDate)) + "-" + RUDateTime.getYear(startDate);
    }

      String startFormatted = RUTypes.formatDateDayMonthYear(startDate);
      String endFormatted = RUTypes.formatDateDayMonthYear(endDate);

    return startFormatted.equals(endFormatted) ? startFormatted : startFormatted + '_' + endFormatted;
  }

}
