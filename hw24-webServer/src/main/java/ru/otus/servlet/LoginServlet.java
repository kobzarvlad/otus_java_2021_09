package ru.otus.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import jakarta.servlet.http.HttpSession;
import ru.otus.services.ClientAuthService;
import ru.otus.services.TemplateProcessor;

import java.io.IOException;
import java.util.Collections;

public class LoginServlet extends HttpServlet {

    private static final String PARAM_LOGIN = "login";
    private static final String PARAM_PASSWORD = "password";
    private static final int MAX_INACTIVE_INTERVAL = 600;
    private static final String LOGIN_PAGE_TEMPLATE = "login.html";


    private final TemplateProcessor templateProcessor;
    private final ClientAuthService clientAuthService;

    public LoginServlet(TemplateProcessor templateProcessor, ClientAuthService clientAuthService) {
        this.clientAuthService = clientAuthService;
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage(LOGIN_PAGE_TEMPLATE, Collections.emptyMap()));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String name = request.getParameter(PARAM_LOGIN);
        String password = request.getParameter(PARAM_PASSWORD);

        if (clientAuthService.authenticate(name, password)) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
            response.sendRedirect("/clients");
        } else {
            response.sendError(SC_UNAUTHORIZED, "Incorrect login data");
        }

    }

}
