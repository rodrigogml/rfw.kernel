package br.eng.rodrigogml.rfw.kernel.utils;

import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDateAdd;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDiferenceInDays;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDiferenceInHours;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDiferenceInMinutes;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDiferenceInMonths;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcOverlappingDays;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.compareDateWithoutTime;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.countMinutesFrom;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getLastLocalDateOfMonth;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getMonth;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getMonthName;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getMonthShortName;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getWeekDay;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getYear;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe de Teste dos métodos da {@link RUDateTime}.<br>
 *
 * @author Rodrigo Leitão
 * @since (21 de fev. de 2025)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUDateTimeTest {

  /**
   * Testa o método {@code compareDateWithoutTime}, garantindo que apenas a data (dia, mês e ano) seja considerada.
   */
  @Test
  public void t00_compareDateWithoutTime() {
    Calendar cal = Calendar.getInstance();

    // Criando date1: 2024-02-20 10:30:45
    cal.set(2024, Calendar.FEBRUARY, 20, 10, 30, 45);
    Date date1 = cal.getTime();

    // Criando date2: 2024-02-20 23:59:59 (mesmo dia, mas horário diferente)
    cal.set(2024, Calendar.FEBRUARY, 20, 23, 59, 59);
    Date date2 = cal.getTime();

    // Criando date3: 2024-02-21 00:00:00 (dia seguinte)
    cal.set(2024, Calendar.FEBRUARY, 21, 0, 0, 0);
    Date date3 = cal.getTime();

    assertEquals("Deve retornar 0 para a mesma data sem considerar o tempo.", 0, compareDateWithoutTime(date1, date2));
    assertTrue("Deve retornar negativo quando date1 for antes de date3.", compareDateWithoutTime(date1, date3) < 0);
    assertTrue("Deve retornar positivo quando date3 for depois de date1.", compareDateWithoutTime(date3, date1) > 0);
  }

  /**
   * Testa o método {@code countMinutesFrom}, garantindo que o cálculo do tempo decorrido esteja correto.
   */
  @Test
  public void t00_countMinutesFrom() {
    long now = System.currentTimeMillis();
    long fiveMinutesAgo = now - (5 * 60000);
    long tenMinutesAgo = now - (10 * 60000);

    assertEquals("Deve retornar aproximadamente 5 minutos.", 5.0, countMinutesFrom(fiveMinutesAgo), 0.1);
    assertEquals("Deve retornar aproximadamente 10 minutos.", 10.0, countMinutesFrom(tenMinutesAgo), 0.1);
    assertEquals("Deve retornar aproximadamente 0 minutos para timestamp atual.", 0.0, countMinutesFrom(now), 0.1);
  }

  /**
   * Testa o método {@code calcDiferenceInMonths}, garantindo que a contagem de meses esteja correta.
   */
  @Test
  public void t00_calcDiferenceInMonths() {
    assertEquals("Mesmo mês deve retornar 0.", 0, calcDiferenceInMonths(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 28)));
    assertEquals("Dois meses de diferença.", 2, calcDiferenceInMonths(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 1)));
    assertEquals("Ano novo deve contar corretamente.", -3, calcDiferenceInMonths(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 3, 1)));
  }

  /**
   * Testa o método {@code calcDiferenceInDays} para {@code LocalDate}.
   */
  @Test
  public void t00_calcDiferenceInDays_LocalDate() {
    assertEquals("Mesma data deve retornar 0.", 0, calcDiferenceInDays(LocalDate.of(2024, 2, 20), LocalDate.of(2024, 2, 20)));
    assertEquals("Um dia de diferença.", 1, calcDiferenceInDays(LocalDate.of(2024, 2, 20), LocalDate.of(2024, 2, 21)));
    assertEquals("Diferença de cinco dias.", 5, calcDiferenceInDays(LocalDate.of(2024, 2, 20), LocalDate.of(2024, 2, 25)));
  }

  /**
   * Testa o método {@code calcDiferenceInDays} para {@code LocalDateTime}.
   */
  @Test
  public void t00_calcDiferenceInDays_LocalDateTime() {
    assertEquals("Menos de 24h não deve contar como um dia.", 0, calcDiferenceInDays(LocalDateTime.of(2024, 2, 20, 10, 0), LocalDateTime.of(2024, 2, 21, 9, 59)));
    assertEquals("Exatamente 24h deve contar um dia.", 1, calcDiferenceInDays(LocalDateTime.of(2024, 2, 20, 10, 0), LocalDateTime.of(2024, 2, 21, 10, 0)));
  }

  /**
   * Testa o método {@code calcDiferenceInDays} para {@code Date}.
   */
  @Test
  public void t00_calcDiferenceInDays_Date() {
    long now = System.currentTimeMillis();
    assertEquals("Mesma data deve retornar 0.", 0.0, calcDiferenceInDays(new Date(now), new Date(now)), 0.1);
    assertEquals("Um dia de diferença.", 1.0, calcDiferenceInDays(new Date(now - 86400000), new Date(now)), 0.1);
  }

  /**
   * Testa o método {@code calcDiferenceInHours} para {@code Date}.
   */
  @Test
  public void t00_calcDiferenceInHours_Date() {
    long now = System.currentTimeMillis();
    assertEquals("Sem diferença de horas deve retornar 0.", 0.0, calcDiferenceInHours(new Date(now), new Date(now)), 0.1);
    assertEquals("Uma hora de diferença.", 1.0, calcDiferenceInHours(new Date(now - 3600000), new Date(now)), 0.1);
  }

  /**
   * Testa o método {@code calcDiferenceInMinutes}, garantindo que a diferença em minutos seja calculada corretamente.
   */
  @Test
  public void t00_calcDiferenceInMinutes() {
    long now = System.currentTimeMillis();
    assertEquals("Sem diferença deve retornar 0.", 0.0, calcDiferenceInMinutes(new Date(now), new Date(now)), 0.1);
    assertEquals("Dez minutos de diferença.", 10.0, calcDiferenceInMinutes(new Date(now - 600000), new Date(now)), 0.1);
  }

  /**
   * Testa o método {@code calcDateAdd}, garantindo que a soma e subtração de períodos seja correta.
   */
  @Test
  public void t00_calcDateAdd() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.FEBRUARY, 20); // 20/02/2024
    Date baseDate = cal.getTime();

    Date addedDays = calcDateAdd(baseDate, Calendar.DAY_OF_MONTH, 5);
    cal.setTime(addedDays);
    assertEquals("Adicionar 5 dias deve resultar em 25/02/2024.", 25, cal.get(Calendar.DAY_OF_MONTH));

    Date subtractedMonths = calcDateAdd(baseDate, Calendar.MONTH, -2);
    cal.setTime(subtractedMonths);
    assertEquals("Subtrair 2 meses deve resultar em Dezembro de 2023.", Calendar.DECEMBER, cal.get(Calendar.MONTH));
  }

  /**
   * Testa o método {@code getMonth}, garantindo que o mês seja extraído corretamente.
   */
  @Test
  public void t00_getMonth() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.FEBRUARY, 20); // 20/02/2024
    assertEquals("Fevereiro deve ser representado como 2.", 2, getMonth(cal.getTime()));
  }

  /**
   * Testa o método {@code getYear}, garantindo que o ano seja extraído corretamente.
   */
  @Test
  public void t00_getYear() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.FEBRUARY, 20); // 20/02/2024
    assertEquals("O ano deve ser extraído corretamente.", 2024, getYear(cal.getTime()));
  }

  /**
   * Testa o método {@code getLastLocalDateOfMonth} para {@code LocalDateTime} e {@code LocalDate}.
   */
  @Test
  public void t00_getLastLocalDateOfMonth() {
    assertEquals("Último dia de fevereiro 2024 deve ser 29.", LocalDate.of(2024, 2, 29), getLastLocalDateOfMonth(LocalDateTime.of(2024, 2, 15, 12, 0)));
    assertEquals("Último dia de abril 2024 deve ser 30.", LocalDate.of(2024, 4, 30), getLastLocalDateOfMonth(LocalDate.of(2024, 4, 10)));
  }

  /**
   * Testa o método {@code getMonthName}, garantindo que o nome do mês seja retornado corretamente.
   */
  @Test
  public void t00_getMonthName() {
    assertEquals("Janeiro deve ser 'January' em inglês.", "January", getMonthName(Locale.ENGLISH, 1));
    assertEquals("Janeiro deve ser 'Janeiro' em português.", "Janeiro", getMonthName(new Locale("pt", "BR"), 1));
  }

  /**
   * Testa o método {@code getMonthShortName}, garantindo que o nome curto do mês seja retornado corretamente.
   */
  @Test
  public void t00_getMonthShortName() {
    assertEquals("Janeiro deve ser 'Jan' em inglês.", "Jan", getMonthShortName(Locale.ENGLISH, 1));
    assertEquals("Janeiro deve ser 'jan' em português.", "jan", getMonthShortName(new Locale("pt", "BR"), 1));
  }

  /**
   * Testa o método {@code getWeekDay}, garantindo que o dia da semana seja retornado corretamente.
   */
  @Test
  public void t00_getWeekDay() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.FEBRUARY, 19); // Segunda-feira
    assertEquals("19/02/2024 deve ser uma segunda-feira.", Calendar.MONDAY, getWeekDay(cal.getTime()));
    cal.set(2024, Calendar.FEBRUARY, 20); // Terça-feira
    assertEquals("20/02/2024 deve ser uma terça-feira.", Calendar.TUESDAY, getWeekDay(cal.getTime()));
  }

  /**
   * Testa o método {@code calcOverlappingDays}, garantindo que a sobreposição de períodos seja correta.
   */
  @Test
  public void t00_calcOverlappingDays() throws RFWValidationException {
    assertEquals("Períodos idênticos devem ter sobreposição total.", 10, calcOverlappingDays(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10)));
    assertEquals("Períodos sem sobreposição devem retornar 0.", 0, calcOverlappingDays(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 20)));
    assertEquals("Sobreposição parcial deve ser calculada corretamente.", 5, calcOverlappingDays(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 6), LocalDate.of(2024, 2, 15)));
  }

}
