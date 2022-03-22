package ru.otus.services;

public interface ClientAuthService {
  boolean authenticate(String login, String password);
}
