package ru.otus.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class MeasurementMixin {

  public MeasurementMixin(@JsonProperty("name") String name, @JsonProperty("value") double value) {

  }

}
