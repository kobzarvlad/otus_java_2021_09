package ru.otus;

import com.google.common.collect.Lists;
import java.util.List;

public class HelloOtus {

  public static void main(String[] args) {
    List<Integer> list = List.of(1, 2, 3);
    System.out.printf("Original list: %s%n", list);
    System.out.printf("Reversed list: %s%n", Lists.reverse(list));
  }
}
