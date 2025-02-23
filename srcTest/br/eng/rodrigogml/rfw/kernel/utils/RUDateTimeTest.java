package br.eng.rodrigogml.rfw.kernel.utils;

import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDateAdd;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDifferenceInDays;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDifferenceInHours;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDifferenceInMinutes;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcDifferenceInMonths;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.calcOverlappingDays;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.compareDateWithoutTime;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.countMinutesFrom;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.dateAdd;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.dateDifferenceInDays;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.dateDifferenceInHours;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.dateDifferenceInMinutes;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getLastLocalDateOfMonth;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getMonth;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getMonthName;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getMonthShortName;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getNameByDate;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getWeekDay;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.getYear;
import static br.eng.rodrigogml.rfw.kernel.utils.RUDateTime.setTimeTo000000;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe de Teste dos m�todos da {@link RUDateTime}.<br>
 *
 * @author Rodrigo Leit�o
 * @since (21 de fev. de 2025)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUDateTimeTest {

  /**
   * Testa o m�todo {@code compareDateWithoutTime}, garantindo que apenas a data (dia, m�s e ano) seja considerada.
   */
  @Test
  public void t00_compareDateWithoutTime() {
    Calendar cal = Calendar.getInstance();

    // Criando date1: 2024-02-20 10:30:45
    cal.set(2024, Calendar.FEBRUARY, 20, 10, 30, 45);
    Date date1 = cal.getTime();

    // Criando date2: 2024-02-20 23:59:59 (mesmo dia, mas hor�rio diferente)
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
   * Testa o m�todo {@code countMinutesFrom}, garantindo que o c�lculo do tempo decorrido esteja correto.
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
   * Testa o m�todo {@code calcDifferenceInMonths}, garantindo que a contagem de meses esteja correta.
   */
  @Test
  public void t00_calcDifferenceInMonths() {
    assertEquals("Mesmo m�s deve retornar 0.", 0, calcDifferenceInMonths(LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 28)));
    assertEquals("Dois meses de diferen�a.", 2, calcDifferenceInMonths(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 1)));
    assertEquals("Ano novo deve contar corretamente.", -3, calcDifferenceInMonths(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 3, 1)));
  }

  /**
   * Testa o m�todo {@code calcDifferenceInDays} para {@code LocalDate}.
   */
  @Test
  public void t00_calcDifferenceInDays_LocalDate() {
    assertEquals("Mesma data deve retornar 0.", 0, calcDifferenceInDays(LocalDate.of(2024, 2, 20), LocalDate.of(2024, 2, 20)));
    assertEquals("Um dia de diferen�a.", 1, calcDifferenceInDays(LocalDate.of(2024, 2, 20), LocalDate.of(2024, 2, 21)));
    assertEquals("Diferen�a de cinco dias.", 5, calcDifferenceInDays(LocalDate.of(2024, 2, 20), LocalDate.of(2024, 2, 25)));
  }

  /**
   * Testa o m�todo {@code calcDifferenceInDays} para {@code LocalDateTime}.
   */
  @Test
  public void t00_calcDifferenceInDays_LocalDateTime() {
    assertEquals("Menos de 24h n�o deve contar como um dia.", 0, calcDifferenceInDays(LocalDateTime.of(2024, 2, 20, 10, 0), LocalDateTime.of(2024, 2, 21, 9, 59)));
    assertEquals("Exatamente 24h deve contar um dia.", 1, calcDifferenceInDays(LocalDateTime.of(2024, 2, 20, 10, 0), LocalDateTime.of(2024, 2, 21, 10, 0)));
  }

  /**
   * Testa o m�todo {@code calcDifferenceInDays} para {@code Date}.
   */
  @Test
  public void t00_calcDifferenceInDays_Date() {
    long now = System.currentTimeMillis();
    assertEquals("Mesma data deve retornar 0.", 0.0, calcDifferenceInDays(new Date(now), new Date(now)), 0.1);
    assertEquals("Um dia de diferen�a.", 1.0, calcDifferenceInDays(new Date(now - 86400000), new Date(now)), 0.1);
  }

  /**
   * Testa o m�todo {@code calcDifferenceInHours} para {@code Date}.
   */
  @Test
  public void t00_calcDifferenceInHours_Date() {
    long now = System.currentTimeMillis();
    assertEquals("Sem diferen�a de horas deve retornar 0.", 0.0, calcDifferenceInHours(new Date(now), new Date(now)), 0.1);
    assertEquals("Uma hora de diferen�a.", 1.0, calcDifferenceInHours(new Date(now - 3600000), new Date(now)), 0.1);
  }

  /**
   * Testa o m�todo {@code calcDifferenceInMinutes}, garantindo que a diferen�a em minutos seja calculada corretamente.
   */
  @Test
  public void t00_calcDifferenceInMinutes() {
    long now = System.currentTimeMillis();
    assertEquals("Sem diferen�a deve retornar 0.", 0.0, calcDifferenceInMinutes(new Date(now), new Date(now)), 0.1);
    assertEquals("Dez minutos de diferen�a.", 10.0, calcDifferenceInMinutes(new Date(now - 600000), new Date(now)), 0.1);
  }

  /**
   * Testa o m�todo {@code calcDateAdd}, garantindo que a soma e subtra��o de per�odos seja correta.
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
   * Testa o m�todo {@code getMonth}, garantindo que o m�s seja extra�do corretamente.
   */
  @Test
  public void t00_getMonth() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.FEBRUARY, 20); // 20/02/2024
    assertEquals("Fevereiro deve ser representado como 2.", 2, getMonth(cal.getTime()));
  }

  /**
   * Testa o m�todo {@code getYear}, garantindo que o ano seja extra�do corretamente.
   */
  @Test
  public void t00_getYear() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.FEBRUARY, 20); // 20/02/2024
    assertEquals("O ano deve ser extra�do corretamente.", 2024, getYear(cal.getTime()));
  }

  /**
   * Testa o m�todo {@code getLastLocalDateOfMonth} para {@code LocalDateTime} e {@code LocalDate}.
   */
  @Test
  public void t00_getLastLocalDateOfMonth() {
    assertEquals("�ltimo dia de fevereiro 2024 deve ser 29.", LocalDate.of(2024, 2, 29), getLastLocalDateOfMonth(LocalDateTime.of(2024, 2, 15, 12, 0)));
    assertEquals("�ltimo dia de abril 2024 deve ser 30.", LocalDate.of(2024, 4, 30), getLastLocalDateOfMonth(LocalDate.of(2024, 4, 10)));
  }

  /**
   * Testa o m�todo {@code getMonthName}, garantindo que o nome do m�s seja retornado corretamente.
   */
  @Test
  public void t00_getMonthName() {
    assertEquals("Janeiro deve ser 'January' em ingl�s.", "January", getMonthName(Locale.ENGLISH, 1));
    assertEquals("Janeiro deve ser 'Janeiro' em portugu�s.", "Janeiro", getMonthName(new Locale("pt", "BR"), 1));
  }

  /**
   * Testa o m�todo {@code getMonthShortName}, garantindo que o nome curto do m�s seja retornado corretamente.
   */
  @Test
  public void t00_getMonthShortName() {
    assertEquals("Janeiro deve ser 'Jan' em ingl�s.", "Jan", getMonthShortName(Locale.ENGLISH, 1));
    assertEquals("Janeiro deve ser 'jan' em portugu�s.", "jan", getMonthShortName(new Locale("pt", "BR"), 1));
  }

  /**
   * Testa o m�todo {@code getWeekDay}, garantindo que o dia da semana seja retornado corretamente.
   */
  @Test
  public void t00_getWeekDay() {
    Calendar cal = Calendar.getInstance();
    cal.set(2024, Calendar.FEBRUARY, 19); // Segunda-feira
    assertEquals("19/02/2024 deve ser uma segunda-feira.", Calendar.MONDAY, getWeekDay(cal.getTime()));
    cal.set(2024, Calendar.FEBRUARY, 20); // Ter�a-feira
    assertEquals("20/02/2024 deve ser uma ter�a-feira.", Calendar.TUESDAY, getWeekDay(cal.getTime()));
  }

  /**
   * Testa o m�todo {@code calcOverlappingDays}, garantindo que a sobreposi��o de per�odos seja correta.
   */
  @Test
  public void t00_calcOverlappingDays() throws RFWValidationException {
    assertEquals("Per�odos id�nticos devem ter sobreposi��o total.", 10, calcOverlappingDays(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10)));
    assertEquals("Per�odos sem sobreposi��o devem retornar 0.", 0, calcOverlappingDays(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 11), LocalDate.of(2024, 2, 20)));
    assertEquals("Sobreposi��o parcial deve ser calculada corretamente.", 5, calcOverlappingDays(LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 6), LocalDate.of(2024, 2, 15)));
  }

  @Test
  public void t00_dateAdd() {
    Date baseDate = new GregorianCalendar(2025, Calendar.FEBRUARY, 22).getTime();

    // Testa adi��o de um dia
    Date expectedDate = new GregorianCalendar(2025, Calendar.FEBRUARY, 23).getTime();
    assertEquals(expectedDate, dateAdd(baseDate, Calendar.DAY_OF_MONTH, 1));

    // Testa subtra��o de um m�s
    expectedDate = new GregorianCalendar(2025, Calendar.JANUARY, 22).getTime();
    assertEquals(expectedDate, dateAdd(baseDate, Calendar.MONTH, -1));

    // Testa adi��o de 10 minutos
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(baseDate);
    calendar.add(Calendar.MINUTE, 10);
    assertEquals(calendar.getTime(), dateAdd(baseDate, Calendar.MINUTE, 10));

    // Testa exce��o com data nula
    try {
      dateAdd(null, Calendar.DAY_OF_MONTH, 1);
      fail("Esperava IllegalArgumentException para data nula.");
    } catch (IllegalArgumentException e) {
      assertEquals("A data base n�o pode ser nula.", e.getMessage());
    }
  }

  /**
   * Testa o m�todo timeTo000000 para garantir que ele ajusta corretamente a hora de uma data.
   *
   * @throws RFWException
   */
  @Test
  public void t00_timeTo000000_ValidDate() throws RFWException {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2024, Calendar.FEBRUARY, 23, 15, 30, 45);
    calendar.set(Calendar.MILLISECOND, 500);
    Date inputDate = calendar.getTime();

    Date adjustedDate = setTimeTo000000(inputDate);

    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    assertEquals(calendar.getTime(), adjustedDate);
  }

  /**
   * Testa a diferen�a de dias entre duas datas.
   */
  @Test
  public void t00_dateDifferenceInDays_validDates() {
    Calendar cal = Calendar.getInstance();

    cal.set(2024, Calendar.FEBRUARY, 1);
    Date startDate = cal.getTime();

    cal.set(2024, Calendar.FEBRUARY, 10);
    Date endDate = cal.getTime();

    assertEquals(9.0, dateDifferenceInDays(startDate, endDate), 0.001);
  }

  /**
   * Testa a diferen�a de dias quando a data inicial � posterior � final.
   */
  @Test
  public void t01_dateDifferenceInDays_negativeDifference() {
    Calendar cal = Calendar.getInstance();

    cal.set(2024, Calendar.MARCH, 10);
    Date startDate = cal.getTime();

    cal.set(2024, Calendar.MARCH, 1);
    Date endDate = cal.getTime();

    assertEquals(-9.0, dateDifferenceInDays(startDate, endDate), 0.001);
  }

  /**
   * Testa a diferen�a de dias entre duas datas id�nticas.
   */
  @Test
  public void t02_dateDifferenceInDays_sameDate() {
    Calendar cal = Calendar.getInstance();

    cal.set(2024, Calendar.JANUARY, 1);
    Date date = cal.getTime();

    assertEquals(0.0, dateDifferenceInDays(date, date), 0.001);
  }

  /**
   * Testa os m�todos de c�lculo de diferen�a de tempo em horas e minutos. Valida resultados positivos, negativos e casos com datas id�nticas.
   */
  @Test
  public void t00_dateDifferenceInHours() {
    Date date1 = new Date(1700000000000L); // Timestamp fixo
    Date date2 = new Date(1700003600000L); // +1 hora depois

    // Testa diferen�a positiva
    assertEquals(1.0, dateDifferenceInHours(date1, date2), 0.0001);
    assertEquals(1.0, dateDifferenceInHours(date1.getTime(), date2.getTime()), 0.0001);

    // Testa diferen�a negativa
    assertEquals(-1.0, dateDifferenceInHours(date2, date1), 0.0001);
    assertEquals(-1.0, dateDifferenceInHours(date2.getTime(), date1.getTime()), 0.0001);

    // Testa quando as datas s�o iguais
    assertEquals(0.0, dateDifferenceInHours(date1, date1), 0.0001);
  }

  @Test
  public void t00_dateDifferenceInMinutes() {
    Date date1 = new Date(1700000000000L); // Timestamp fixo
    Date date2 = new Date(1700000600000L); // +10 minutos depois

    // Testa diferen�a positiva
    assertEquals(10.0, dateDifferenceInMinutes(date1, date2), 0.0001);
    assertEquals(10.0, dateDifferenceInMinutes(date1.getTime(), date2.getTime()), 0.0001);

    // Testa diferen�a negativa
    assertEquals(-10.0, dateDifferenceInMinutes(date2, date1), 0.0001);
    assertEquals(-10.0, dateDifferenceInMinutes(date2.getTime(), date1.getTime()), 0.0001);

    // Testa quando as datas s�o iguais
    assertEquals(0.0, dateDifferenceInMinutes(date1, date1), 0.0001);
  }

  /**
   * Teste unit�rio para o m�todo {@link #getNameByDate(Date, Date)}.
   * <ul>
   * <li>Verifica a gera��o correta do sufixo para per�odos mensais completos.</li>
   * <li>Verifica a gera��o correta do sufixo para intervalos de datas distintos.</li>
   * <li>Testa se datas iguais geram um �nico valor no formato "ddMMyyyy".</li>
   * <li>Testa a exce��o ao passar datas nulas.</li>
   * </ul>
   */
  @Test
  public void t00_getNameByDate() {
    Locale.setDefault(Locale.forLanguageTag("pt-BR"));

    Calendar cal = Calendar.getInstance();

    // Caso: M�s completo (01/08/2017 a 31/08/2017) -> "Agosto-2017"
    cal.set(2017, Calendar.AUGUST, 1);
    Date startFullMonth = cal.getTime();
    cal.set(2017, Calendar.AUGUST, 31);
    Date endFullMonth = cal.getTime();

    assertEquals("Agosto-2017", getNameByDate(startFullMonth, endFullMonth));

    // Caso: Datas distintas (10/05/2021 a 25/05/2021) -> "10052021_25052021"
    cal.set(2021, Calendar.MAY, 10);
    Date startRange = cal.getTime();
    cal.set(2021, Calendar.MAY, 25);
    Date endRange = cal.getTime();

    assertEquals("10052021_25052021", getNameByDate(startRange, endRange));

    // Caso: Mesma data (15/02/2023) -> "15022023"
    cal.set(2023, Calendar.FEBRUARY, 15);
    Date sameDate = cal.getTime();

    assertEquals("15022023", getNameByDate(sameDate, sameDate));

    // Caso: Exce��o para datas nulas
    try {
      getNameByDate(null, endRange);
      fail("Esperado IllegalArgumentException para startDate nulo.");
    } catch (IllegalArgumentException e) {
      assertEquals("As datas n�o podem ser nulas.", e.getMessage());
    }

    try {
      getNameByDate(startRange, null);
      fail("Esperado IllegalArgumentException para endDate nulo.");
    } catch (IllegalArgumentException e) {
      assertEquals("As datas n�o podem ser nulas.", e.getMessage());
    }
  }

}
