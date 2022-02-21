package ru.otus.example;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;

public class DateUtils {

  private DateUtils() {
  }

  public static LocalDate getFirstDateOfPeriod(LocalDate date, TemporalField periodType) {
    return date.with(periodType, 1L);
  }

  public static long getDaysBetweenInclusive(LocalDate startDate, LocalDate endDate) {
    return ChronoUnit.DAYS.between(startDate, endDate) + 1;
  }
}
