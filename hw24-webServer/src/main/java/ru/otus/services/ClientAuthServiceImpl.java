package ru.otus.services;

import ru.otus.dao.ClientDao;

public class ClientAuthServiceImpl implements ClientAuthService {

  private final ClientDao clientDao;

  public ClientAuthServiceImpl(ClientDao clientDao) {
    this.clientDao = clientDao;
  }

  @Override
  public boolean authenticate(String login, String password) {
    return clientDao.getByLogin(login)
        .map(client -> client.getPassword().equals(password))
        .orElse(false);
  }
}
