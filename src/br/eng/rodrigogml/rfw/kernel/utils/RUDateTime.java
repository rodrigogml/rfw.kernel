package br.eng.rodrigogml.rfw.kernel.utils;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

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
   * @param date1 A primeira data a ser comparada (n�o pode ser nula).
   * @param date2 A segunda data a ser comparada (n�o pode ser nula).
   * @return Um valor negativo, zero ou positivo conforme {@code date1} for antes, igual ou depois de {@code date2}.
   * @throws NullPointerException Se {@code date1} ou {@code date2} forem nulos.
   */
  public static int compareDateWithoutTime(Date date1, Date date2) {
    LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    return localDate1.compareTo(localDate2);
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
   * Calcula quantos minutos se passaram desde o tempo especificado at� o momento atual.
   *
   * @param milliseconds O timestamp de refer�ncia em milissegundos (epoch time).
   * @return O n�mero de minutos decorridos desde o tempo especificado at� agora.
   */
  public static double countMinutesFrom(long milliseconds) {
    return (System.currentTimeMillis() - milliseconds) / 60000.0;
  }

  /**
   * Calcula o n�mero de meses entre duas datas.
   * <p>
   * Mesmo que as datas sejam diferentes, se estiverem dentro do mesmo m�s e ano, o valor retornado ser� zero.
   * </p>
   * <p>
   * Caso a data final esteja em um m�s anterior � data inicial, o valor retornado ser� negativo.
   * </p>
   *
   * @param initialDate Data inicial (n�o pode ser nula).
   * @param finalDate Data final (n�o pode ser nula).
   * @return N�mero de meses completos entre as duas datas.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static long calcDiferenceInMonths(LocalDate initialDate, LocalDate finalDate) {
    return Period.between(initialDate.withDayOfMonth(1), finalDate.withDayOfMonth(1)).toTotalMonths();
  }

  /**
   * Calcula o n�mero de dias completos entre duas datas.
   * <p>
   * O c�lculo considera apenas per�odos completos de 24 horas.
   * </p>
   *
   * @param initialDate Data inicial (inclusivo, n�o pode ser nula).
   * @param finalDate Data final (exclusivo, n�o pode ser nula).
   * @return Total de dias completos entre a data inicial e a final.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static long calcDiferenceInDays(LocalDate initialDate, LocalDate finalDate) {
    return ChronoUnit.DAYS.between(initialDate, finalDate);
  }

  /**
   * Calcula o n�mero de dias completos entre duas datas e hor�rios.
   * <p>
   * O c�lculo considera apenas per�odos completos de 24 horas.
   * </p>
   *
   * @param initialDate Data e hora inicial (n�o pode ser nula).
   * @param finalDate Data e hora final (n�o pode ser nula).
   * @return Total de dias completos entre as datas.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static long calcDiferenceInDays(LocalDateTime initialDate, LocalDateTime finalDate) {
    return ChronoUnit.DAYS.between(initialDate, finalDate);
  }

  /**
   * Calcula a diferen�a entre duas datas em dias.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em rela��o � data final.
   * </p>
   *
   * @param initialDate Data inicial (n�o pode ser nula).
   * @param finalDate Data final (n�o pode ser nula).
   * @return Diferen�a entre as datas em dias.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static double calcDiferenceInDays(Date initialDate, Date finalDate) {
    return calcDiferenceInDays(initialDate.getTime(), finalDate.getTime());
  }

  /**
   * Calcula a diferen�a entre duas datas em dias.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em rela��o � data final.
   * </p>
   *
   * @param initialDate Timestamp inicial (epoch time).
   * @param finalDate Timestamp final (epoch time).
   * @return Diferen�a entre as datas em dias.
   */
  public static double calcDiferenceInDays(long initialDate, long finalDate) {
    return (finalDate - initialDate) / 86400000.0; // 1 dia = 86.400.000ms
  }

  /**
   * Calcula a diferen�a entre duas datas em horas.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em rela��o � data final.
   * </p>
   *
   * @param initialDate Data inicial (n�o pode ser nula).
   * @param finalDate Data final (n�o pode ser nula).
   * @return Diferen�a entre as datas em horas.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static double calcDiferenceInHours(Date initialDate, Date finalDate) {
    return calcDiferenceInHours(initialDate.getTime(), finalDate.getTime());
  }

  /**
   * Calcula a diferen�a entre duas datas em horas.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em rela��o � data final.
   * </p>
   *
   * @param initialDate Timestamp inicial (epoch time).
   * @param finalDate Timestamp final (epoch time).
   * @return Diferen�a entre as datas em horas.
   */
  public static double calcDiferenceInHours(long initialDate, long finalDate) {
    return (finalDate - initialDate) / 3600000.0; // 1 hora = 3.600.000ms
  }

  /**
   * Calcula a diferen�a entre duas datas em minutos.
   * <p>
   * Retorna um valor negativo caso a data inicial seja futura em rela��o � data final.
   * </p>
   *
   * @param initialDate Data inicial (n�o pode ser nula).
   * @param finalDate Data final (n�o pode ser nula).
   * @return Diferen�a entre as datas em minutos.
   * @throws NullPointerException Se {@code initialDate} ou {@code finalDate} forem nulos.
   */
  public static double calcDiferenceInMinutes(Date initialDate, Date finalDate) {
    return calcDiferenceInMinutes(initialDate.getTime(), finalDate.getTime());
  }

  /**
   * Calcula a diferen�a entre dois timestamps em minutos.
   * <p>
   * Retorna um valor negativo caso o timestamp inicial seja maior que o final.
   * </p>
   *
   * @param initialDate Timestamp inicial (epoch time).
   * @param finalDate Timestamp final (epoch time).
   * @return Diferen�a entre os timestamps em minutos.
   */
  public static double calcDiferenceInMinutes(long initialDate, long finalDate) {
    return (finalDate - initialDate) / 60000.0; // 1 minuto = 60.000ms
  }

  /**
   * Adiciona ou subtrai um per�odo a uma data.
   * <p>
   * Permite modificar a data base somando ou subtraindo dias, meses, anos, horas, minutos, etc.
   * </p>
   *
   * @param date Data base para a opera��o (n�o pode ser nula).
   * @param period Define o per�odo a ser adicionado/subtra�do. Valores podem ser encontrados em {@link Calendar}, ex: {@link Calendar#MONTH}.
   * @param amount Quantidade do per�odo a ser somado/subtra�do. Valores negativos subtraem da data.
   * @return Nova data com o per�odo ajustado.
   * @throws NullPointerException Se {@code date} for nulo.
   */
  public static Date calcDateAdd(Date date, int period, int amount) {
    Calendar gc = Calendar.getInstance();
    gc.setTime(date);
    gc.add(period, amount);
    return gc.getTime();
  }

  /**
   * Retorna o n�mero do m�s de uma determinada data no calend�rio Gregoriano.
   *
   * @param date Data base para extra��o do valor (n�o pode ser nula).
   * @return N�mero do m�s, onde 1 representa Janeiro e 12 representa Dezembro.
   * @throws NullPointerException Se {@code date} for nula.
   */
  public static int getMonth(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.MONTH) + 1; // Soma 1 porque a fun��o do Java retorna de 0 a 11.
  }

  /**
   * Retorna o ano de uma determinada data no calend�rio Gregoriano.
   *
   * @param date Data base para extra��o do valor (n�o pode ser nula).
   * @return O n�mero do ano com 4 d�gitos.
   * @throws NullPointerException Se {@code date} for nula.
   */
  public static int getYear(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.YEAR);
  }

  /**
   * Retorna um {@link LocalDate} com o �ltimo dia do m�s de uma determinada data, de acordo com o calend�rio Gregoriano.
   *
   * @param date Data base para determinar o �ltimo dia do m�s (n�o pode ser nula).
   * @return �ltimo dia do m�s correspondente � data informada.
   * @throws NullPointerException Se {@code date} for nula.
   */
  public static LocalDate getLastLocalDateOfMonth(LocalDateTime date) {
    return getLastLocalDateOfMonth(date.toLocalDate());
  }

  /**
   * Retorna um {@link LocalDate} com o �ltimo dia do m�s de uma determinada data, de acordo com o calend�rio Gregoriano.
   *
   * @param date Data base para determinar o �ltimo dia do m�s (n�o pode ser nula).
   * @return �ltimo dia do m�s correspondente � data informada.
   * @throws NullPointerException Se {@code date} for nula.
   */
  public static LocalDate getLastLocalDateOfMonth(LocalDate date) {
    return date.withDayOfMonth(date.lengthOfMonth());
  }

  /**
   * Retorna o nome do m�s por extenso, de acordo com o {@code Locale} informado.
   *
   * @param locale Locale para tradu��o do nome do m�s (n�o pode ser nulo).
   * @param month M�s do calend�rio gregoriano (1 para Janeiro, 12 para Dezembro).
   * @return Nome completo do m�s no idioma correspondente ao {@code Locale}.
   * @throws IllegalArgumentException Se o m�s estiver fora do intervalo 1-12.
   */
  public static String getMonthName(Locale locale, int month) {
    if (month < 1 || month > 12) throw new IllegalArgumentException("M�s inv�lido: " + month);
    return new DateFormatSymbols(locale).getMonths()[month - 1];
  }

  /**
   * Retorna o nome abreviado (normalmente com 3 letras) do m�s, de acordo com o {@code Locale} informado.
   *
   * @param locale Locale para tradu��o do nome do m�s (n�o pode ser nulo).
   * @param month M�s do calend�rio gregoriano (1 para Janeiro, 12 para Dezembro).
   * @return Nome abreviado do m�s no idioma correspondente ao {@code Locale}.
   * @throws IllegalArgumentException Se o m�s estiver fora do intervalo 1-12.
   */
  public static String getMonthShortName(Locale locale, int month) {
    if (month < 1 || month > 12) throw new IllegalArgumentException("M�s inv�lido: " + month);
    return new DateFormatSymbols(locale).getShortMonths()[month - 1];
  }

  /**
   * Retorna o dia da semana de uma determinada data.
   *
   * @param date Data base para obten��o do dia da semana (n�o pode ser nula).
   * @return Inteiro representando o dia da semana, conforme {@link Calendar}.
   */
  public static int getWeekDay(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(Calendar.DAY_OF_WEEK);
  }

  /**
   * Calcula a sobreposi��o de dias entre dois per�odos de tempo.
   * <p>
   * As datas s�o inclusivas, ou seja, se os per�odos estiverem encostados, a sobreposi��o ser� de 1 dia.
   * </p>
   *
   * @param period1Start In�cio do primeiro per�odo.
   * @param period1End Fim do primeiro per�odo.
   * @param period2Start In�cio do segundo per�odo.
   * @param period2End Fim do segundo per�odo.
   * @return N�mero de dias que os per�odos se sobrep�em.
   * @throws RFWValidationException Se a data final de um per�odo for anterior � sua data inicial.
   */
  public static long calcOverlappingDays(LocalDate period1Start, LocalDate period1End, LocalDate period2Start, LocalDate period2End) throws RFWValidationException {
    if (period1End.isBefore(period1Start)) throw new RFWValidationException("A data de fim do primeiro per�odo � anterior � data de in�cio!");
    if (period2End.isBefore(period2Start)) throw new RFWValidationException("A data de fim do segundo per�odo � anterior � data de in�cio!");

    if (period1End.isBefore(period2Start) || period2End.isBefore(period1Start)) return 0;

    LocalDate start = period1Start.isAfter(period2Start) ? period1Start : period2Start;
    LocalDate end = period1End.isBefore(period2End) ? period1End : period2End;

    return ChronoUnit.DAYS.between(start, end.plusDays(1));
  }

}
