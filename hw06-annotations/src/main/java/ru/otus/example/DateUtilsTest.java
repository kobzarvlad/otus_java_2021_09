package ru.otus.example;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import org.assertj.core.api.Assertions;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class DateUtilsTest {

  private long millis;

  @Before
  public void startTimeMeasurement() {
    millis = System.currentTimeMillis();
  }

  @After
  public void stopTimeMeasurement() {
    System.out.printf("Test took %d millis%n", System.currentTimeMillis() - millis);
    millis = 0;
  }

  @Test
  public void shouldReturnDaysBetween() {
    LocalDate startDate = LocalDate.parse("2022-02-01");
    LocalDate endDate = LocalDate.parse("2022-02-20");
    long actual = DateUtils.getDaysBetweenInclusive(startDate, endDate);
    long expected = 20;
    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void shouldReturnFirstDateOfYear() {
    LocalDate localDate = LocalDate.parse("2022-02-14");
    LocalDate actual = DateUtils.getFirstDateOfPeriod(localDate, ChronoField.DAY_OF_YEAR);
    LocalDate expected = LocalDate.parse("2022-01-01");
    Assertions.assertThat(actual).isEqualTo(expected);
  }

  // fail
  @Test
  public void shouldReturnFirstDateOfMonth() {
    LocalDate localDate = LocalDate.parse("2022-02-14");
    LocalDate actual = DateUtils.getFirstDateOfPeriod(localDate, ChronoField.DAY_OF_MONTH);
    LocalDate expected = LocalDate.parse("2022-02-02");
    Assertions.assertThat(actual).isEqualTo(expected);
  }
}
