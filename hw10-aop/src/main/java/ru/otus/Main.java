package ru.otus;

public class Main {
  public static void main(String[] args) {
    MyClassInterface proxy = (MyClassInterface) ProxyCreator.create(new MyClassImpl());
    proxy.calculate();
    proxy.calculate(1);
    proxy.calculate(1, 2);
  }
}
