package br.eng.rodrigogml.rfw.kernel.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.RFW;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUTypesTest {

  @Test
  public void t00_formatTo235959() {
    Date date = new Date(0);
    assertEquals("00:00:00", RUTypes.formatTo235959(date));

    long millis = 23 * 60 * 60 * 1000L + 59 * 60 * 1000L + 59 * 1000L;
    assertEquals("23:59:59", RUTypes.formatTo235959(millis));
  }

  @Test
  public void t01_formatToyyyyMMddHHmmss() {
    Date date = new Date(1700000000000L);
    assertEquals(new SimpleDateFormat("yyyyMMddHHmmss").format(date), RUTypes.formatToyyyyMMddHHmmss(date));
  }

  @Test
  public void t02_formatToyyyyMMdd() {
    Date date = new Date(1700000000000L);
    assertEquals(new SimpleDateFormat("yyyyMMdd").format(date), RUTypes.formatToyyyyMMdd(date));
  }

  @Test
  public void t03_formatToddMMyyyy() {
    Date date = new Date(1700000000000L);
    assertEquals(new SimpleDateFormat("ddMMyyyy").format(date), RUTypes.formatToddMMyyyy(date));
  }

  @Test
  public void t04_formatToddMMyyyyHHmmss() {
    Date date = new Date(1700000000000L);
    assertEquals(new SimpleDateFormat("ddMMyyyyHHmmss").format(date), RUTypes.formatToddMMyyyyHHmmss(date));
  }

  @Test
  public void t05_formatTodd_MM_yyyy_HH_mm_ss() {
    Date date = new Date(1700000000000L);
    String expected = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
    assertEquals(expected, RUTypes.formatTodd_MM_yyyy_HH_mm_ss(date));

    LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    assertEquals(expected, RUTypes.formatTodd_MM_yyyy_HH_mm_ss(ldt));
  }

  @Test
  public void t06_formatLocalDate() {
    LocalDate date = LocalDate.of(2024, 2, 20);
    assertEquals("20-02-2024", RUTypes.formatLocalDate(date, "dd-MM-yyyy"));
  }

  @Test
  public void t07_formatLocalDateTime() {
    LocalDateTime date = LocalDateTime.of(2024, 2, 20, 15, 30, 0);
    assertEquals("20/02/2024 15:30", RUTypes.formatLocalDateTime(date, "dd/MM/yyyy HH:mm"));
  }

  @Test
  public void t08_formatToddMMyyyyLocalDate() {
    LocalDate date = LocalDate.of(2024, 2, 20);
    assertEquals("20022024", RUTypes.formatToddMMyyyy(date));
  }

  @Test
  public void t09_formatMillisToHuman() {
    assertEquals("10'500\"", RUTypes.formatMillisToHuman(10500));
    assertEquals("01:00:10'004\"", RUTypes.formatMillisToHuman(3610004));
  }

  @Test
  public void t10_formatMillis() {
    String pattern = "HH:mm:ss";
    long millis = 3661000;
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    assertEquals(sdf.format(new Date(millis)), RUTypes.formatMillis(pattern, millis));
  }

  @Test
  public void t11_parseLocalDateTime() throws RFWException {
    ZoneId zone = ZoneId.of("UTC");
    LocalDateTime base = LocalDateTime.of(2024, 2, 20, 15, 30, 0);

    assertNull(RUTypes.parseLocalDateTime((String) null));
    assertNull(RUTypes.parseLocalDateTime("   "));

    assertEquals(base, RUTypes.parseLocalDateTime("2024-02-20T15:30:00"));
    assertEquals(base, RUTypes.parseLocalDateTime("2024-02-20"));
    assertEquals(base, RUTypes.parseLocalDateTime("20/02/2024"));

    OffsetDateTime odt = OffsetDateTime.of(base, ZoneOffset.ofHours(-3));
    assertEquals(base, RUTypes.parseLocalDateTime(odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

    LocalDateTime converted = RUTypes.parseLocalDateTime(odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), zone);
    assertEquals(odt.atZoneSameInstant(zone).toLocalDateTime(), converted);

    try {
      RUTypes.parseLocalDateTime("invalid");
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }
  }

  @Test
  public void t12_parseDate() throws RFWException {
    ZoneId zone = RFW.getZoneId();
    String isoDate = "2024-02-20T15:30:00-03:00";
    Date parsed = RUTypes.parseDate(isoDate, zone);
    Date expected = Date.from(OffsetDateTime.parse(isoDate).atZoneSameInstant(zone).toInstant());
    assertEquals(expected, parsed);

    String onlyDate = "2024-02-20";
    Date parsedDefault = RUTypes.parseDate(onlyDate);
    assertEquals(Date.from(LocalDate.parse(onlyDate).atStartOfDay(zone).toInstant()), parsedDefault);

    Date patternParsed = RUTypes.parseDate("dd/MM/yyyy", "21/02/2024");
    assertEquals(LocalDate.of(2024, 2, 21), patternParsed.toInstant().atZone(zone).toLocalDate());

    try {
      RUTypes.parseDate("31-01-2024");
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expectedValidation) {
      // ok
    }
  }

  @Test
  public void t13_parseFromyyyyMMddHHmmss() throws RFWException {
    Date parsed = RUTypes.parseFromyyyyMMddHHmmss("20240220123045");
    String formatted = new SimpleDateFormat("yyyyMMddHHmmss").format(parsed);
    assertEquals("20240220123045", formatted);

    try {
      RUTypes.parseFromyyyyMMddHHmmss("invalid");
      fail("Era esperada RFWCriticalException");
    } catch (RFWCriticalException expected) {
      // ok
    }
  }

  @Test
  public void t14_parseLocalDateWithPattern() throws RFWException {
    LocalDate parsed = RUTypes.parseLocalDate("20-02-2024", "dd-MM-yyyy");
    assertEquals(LocalDate.of(2024, 2, 20), parsed);
  }

  @Test
  public void t15_parseLocalDate() throws RFWException {
    ZoneId zone = ZoneId.of("UTC");
    assertEquals(LocalDate.of(2024, 2, 20), RUTypes.parseLocalDate("2024-02-20"));
    assertEquals(LocalDate.of(2024, 2, 20), RUTypes.parseLocalDate("20/02/2024"));

    String isoWithZone = "2024-02-20T15:30:00-03:00";
    assertEquals(LocalDate.of(2024, 2, 20), RUTypes.parseLocalDate(isoWithZone));
    assertEquals(LocalDate.of(2024, 2, 20), RUTypes.parseLocalDate(isoWithZone, zone));

    try {
      RUTypes.parseLocalDate("2024-13-01");
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }
  }

  @Test
  public void t16_parseDateFromLocalDate() {
    LocalDate date = LocalDate.of(2024, 2, 20);
    Date parsed = RUTypes.parseDate(date);
    assertEquals(date, parsed.toInstant().atZone(RFW.getZoneId()).toLocalDate());

    ZoneId utc = ZoneId.of("UTC");
    assertEquals(date, RUTypes.parseDate(date, utc).toInstant().atZone(utc).toLocalDate());
  }

  @Test
  public void t17_parseDateFromLocalDateTime() {
    LocalDateTime ldt = LocalDateTime.of(2024, 2, 20, 15, 30, 0);
    Date parsed = RUTypes.parseDate(ldt);
    assertEquals(ldt, parsed.toInstant().atZone(RFW.getZoneId()).toLocalDateTime());

    ZoneId utc = ZoneId.of("UTC");
    assertEquals(ldt, RUTypes.parseDate(ldt, utc).toInstant().atZone(utc).toLocalDateTime());
    assertNull(RUTypes.parseDate((LocalDateTime) null));
  }

  @Test
  public void t18_parseLocalDateFromTemporal() throws RFWException {
    Date date = new Date();
    assertEquals(date.toInstant().atZone(RFW.getZoneId()).toLocalDate(), RUTypes.parseLocalDate(date));

    ZoneId utc = ZoneId.of("UTC");
    assertEquals(date.toInstant().atZone(utc).toLocalDate(), RUTypes.parseLocalDate(date, utc));

    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
    assertEquals(sqlDate.toLocalDate(), RUTypes.parseLocalDate(sqlDate));

    LocalDateTime ldt = LocalDateTime.of(2024, 2, 20, 15, 30, 0);
    assertEquals(ldt.toLocalDate(), RUTypes.parseLocalDate(ldt));

    Timestamp ts = Timestamp.valueOf(ldt);
    assertEquals(ldt.toLocalDate(), RUTypes.parseLocalDate(ts));
    assertNull(RUTypes.parseLocalDate((Timestamp) null));
  }

  @Test
  public void t19_parseLocalDateTimeFromTemporal() throws RFWException {
    Date date = new Date();
    assertEquals(date.toInstant().atZone(RFW.getZoneId()).toLocalDateTime(), RUTypes.parseLocalDateTime(date));

    ZoneId utc = ZoneId.of("UTC");
    assertEquals(date.toInstant().atZone(utc).toLocalDateTime(), RUTypes.parseLocalDateTime(date, utc));

    Timestamp ts = Timestamp.valueOf(LocalDateTime.of(2024, 2, 20, 15, 30, 0));
    assertEquals(ts.toLocalDateTime(), RUTypes.parseLocalDateTime(ts));
    assertNull(RUTypes.parseLocalDateTime((Timestamp) null));
  }

  @Test
  public void t20_formatDecimalWithoutTrailingZeros() {
    Locale previous = Locale.getDefault();
    Locale.setDefault(Locale.US);
    try {
      assertEquals("", RUTypes.formatDecimalWithoutTrailingZeros(null, Locale.US, 2));
      assertEquals("10.5", RUTypes.formatDecimalWithoutTrailingZeros(new BigDecimal("10.5000"), Locale.US, 3));
      assertEquals("0.1234", RUTypes.formatDecimalWithoutTrailingZeros(new BigDecimal("0.123400"), Locale.US, 4));
    } finally {
      Locale.setDefault(previous);
    }
  }

  @Test
  public void t21_parseInteger() throws RFWException {
    assertNull(RUTypes.parseInteger((Object) null));
    assertEquals(Integer.valueOf(10), RUTypes.parseInteger(Integer.valueOf(10)));
    assertEquals(Integer.valueOf(5), RUTypes.parseInteger("5"));
    assertEquals(Integer.valueOf(-2), RUTypes.parseInteger(Long.valueOf(-2)));
    assertEquals(Integer.valueOf(1), RUTypes.parseInteger(Boolean.TRUE));
    assertEquals(Integer.valueOf(0), RUTypes.parseInteger(Boolean.FALSE));

    try {
      RUTypes.parseInteger("12.3");
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }

    try {
      RUTypes.parseInteger(Long.valueOf(Long.MAX_VALUE));
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }

    try {
      RUTypes.parseInteger(new Date());
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }
  }

  @Test
  public void t22_parseLong() throws RFWException {
    assertNull(RUTypes.parseLong((Object) null));
    assertEquals(Long.valueOf(10L), RUTypes.parseLong("10"));
    assertEquals(Long.valueOf(-5L), RUTypes.parseLong(Integer.valueOf(-5)));
    assertEquals(Long.valueOf(1L), RUTypes.parseLong(Boolean.TRUE));

    assertEquals(Long.valueOf(20L), RUTypes.parseLong(Long.valueOf(20L)));
    assertEquals(Long.valueOf(30L), RUTypes.parseLong(new BigDecimal("30.00")));

    try {
      RUTypes.parseLong("12.3");
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }

    try {
      RUTypes.parseLong(Double.NaN);
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }

    try {
      RUTypes.parseLong(new Object());
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }
  }

  @Test
  public void t23_formatToPercentage() {
    Locale previous = Locale.getDefault();
    Locale.setDefault(Locale.US);
    try {
      assertEquals("0%", RUTypes.formatToPercentage(null));
      assertEquals("12.3%", RUTypes.formatToPercentage(0.123));
      assertEquals("12.30%", RUTypes.formatToPercentage(0.123, 2));
    } finally {
      Locale.setDefault(previous);
    }
  }

  @Test
  public void t24_roundDouble() {
    assertEquals(Double.valueOf(1.23), RUTypes.round(1.234, 2));
  }

  @Test
  public void t25_roundFloorDouble() {
    assertEquals(Double.valueOf(1.23), RUTypes.roundFloor(1.239, 2));
  }

  @Test
  public void t26_roundCeilDouble() {
    assertEquals(Double.valueOf(1.24), RUTypes.roundCeil(1.231, 2));
  }

  @Test
  public void t27_roundFloat() {
    assertEquals(Float.valueOf(1.23f), RUTypes.round(1.234f, 2));
  }

  @Test
  public void t28_roundFloorFloat() {
    assertEquals(Float.valueOf(1.23f), RUTypes.roundFloor(1.239f, 2));
  }

  @Test
  public void t29_roundCeilFloat() {
    assertEquals(Float.valueOf(1.24f), RUTypes.roundCeil(1.231f, 2));
  }

  @Test
  public void t30_formatToyyyy_MM_dd_T_HH_mm_ssXXX() {
    ZoneId utc = ZoneId.of("UTC");
    ZoneId previous = RFW.getZoneId();
    RFW.initializeZoneID(utc);
    try {
      LocalDateTime ldt = LocalDateTime.of(2024, 2, 20, 15, 30, 0);

      assertEquals("2024-02-20T15:30:00Z", RUTypes.formatToyyyy_MM_dd_T_HH_mm_ssXXX(ldt, ZoneOffset.UTC));

      Date date = Date.from(ldt.atZone(utc).toInstant());
      assertEquals("2024-02-20T15:30:00Z", RUTypes.formatToyyyy_MM_dd_T_HH_mm_ssXXX(date));
      assertEquals("2024-02-20T15:30:00Z", RUTypes.formatToyyyy_MM_dd_T_HH_mm_ssXXX(date, utc));

      assertEquals("2024-02-20T12:30:00-03:00", RUTypes.formatToyyyy_MM_dd_T_HH_mm_ssXXX(ldt, ZoneId.of("America/Sao_Paulo")));
      assertEquals("2024-02-20T15:30:00Z", RUTypes.formatToyyyy_MM_dd_T_HH_mm_ssXXX(ldt));
    } finally {
      RFW.initializeZoneID(previous);
    }
  }

  @Test
  public void t31_formatDate() {
    Date date = new Date(1700000000000L);
    assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(date), RUTypes.formatDate(date, "dd/MM/yyyy"));

    try {
      RUTypes.formatDate(date, " ");
      fail("Era esperado IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      // ok
    }

    assertNull(RUTypes.formatDate(null, "dd/MM/yyyy"));
  }

  @Test
  public void t32_parseStringConversions() {
    assertEquals("10.5", RUTypes.parseString(new BigDecimal("10.5")));
    assertEquals("10", RUTypes.parseString(Long.valueOf(10)));
    assertEquals("5", RUTypes.parseString(Integer.valueOf(5)));
    assertEquals("1.5", RUTypes.parseString(Float.valueOf(1.5f)));
    assertEquals("2.5", RUTypes.parseString(Double.valueOf(2.5)));

    assertNull(RUTypes.parseString((BigDecimal) null));
    assertNull(RUTypes.parseString((Long) null));
    assertNull(RUTypes.parseString((Integer) null));
    assertNull(RUTypes.parseString((Float) null));
    assertNull(RUTypes.parseString((Double) null));
  }

  @Test
  public void t33_parseBigDecimal() throws RFWException {
    assertNull(RUTypes.parseBigDecimal(null));
    assertNull(RUTypes.parseBigDecimal("   "));
    assertEquals(new BigDecimal("10.50"), RUTypes.parseBigDecimal("10.50"));
    assertEquals(new BigDecimal("-5"), RUTypes.parseBigDecimal("-5"));

    try {
      RUTypes.parseBigDecimal("1,5");
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }

    try {
      RUTypes.parseBigDecimal("not a number");
      fail("Era esperada RFWValidationException");
    } catch (RFWValidationException expected) {
      // ok
    }
  }
}
