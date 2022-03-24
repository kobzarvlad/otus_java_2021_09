package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.dao.ClientDao;
import ru.otus.services.TemplateProcessor;

public class ClientsServlet extends HttpServlet {

  private final ClientDao clientDao;
  private final TemplateProcessor templateProcessor;

  public ClientsServlet(ClientDao clientDao, TemplateProcessor templateProcessor) {
    this.clientDao = clientDao;
    this.templateProcessor = templateProcessor;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
    List<Client> clients = clientDao.findAll();

    response.setContentType("text/html");
    String page = templateProcessor.getPage("clients.html", Map.of("clients", clients));
    response.getWriter().println(page);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String name = req.getParameter("name");
    String login = req.getParameter("login");
    String password = req.getParameter("password");
    String address = req.getParameter("street");
    String phoneNumber1 = req.getParameter("phoneNumber1");
    String phoneNumber2 = req.getParameter("phoneNumber2");

    if (name.isBlank() || login.isBlank() || password.isBlank()) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Name, login and password can't be blank");
      return;
    }

    List<String> phoneNumbers = List.of(phoneNumber1, phoneNumber2);
    List<Phone> phones = phoneNumbers.stream().filter(number -> !number.isBlank()).map(Phone::new).toList();

    clientDao.saveClient(new Client(
        null,
        name,
        login,
        password,
        address.isBlank() ? null : new Address(address),
        phones.isEmpty() ? null : phones
    ));

    resp.sendRedirect("/clients");
  }
}


