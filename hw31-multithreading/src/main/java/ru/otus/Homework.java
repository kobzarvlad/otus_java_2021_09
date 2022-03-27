package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Homework {

  private static final Logger log = LoggerFactory.getLogger(Homework.class);

  private int prevThreadNumber = 1;

  public static void main(String[] args) {
    Homework homework = new Homework();
    new Thread(() -> homework.count(0)).start();
    new Thread(() -> homework.count(1)).start();
  }

  private void count(int threadNumber) {
    for (int i = 1; i <= 10; i++) {
      printValue(threadNumber, i);
    }
    for (int i = 9; i >= 1; i--) {
      printValue(threadNumber, i);
    }
  }

  private synchronized void printValue(int threadNumber, int value) {
    while (prevThreadNumber == threadNumber) {
      try {
        wait();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    log.info(String.valueOf(value));
    prevThreadNumber = threadNumber;
    notify();
  }
}
