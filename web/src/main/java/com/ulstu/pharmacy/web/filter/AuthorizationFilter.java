package com.ulstu.pharmacy.web.filter;

import lombok.NoArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;

@NoArgsConstructor
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = { "*.xhtml" })
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest reqt = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = reqt.getSession(false);

        String reqURI = reqt.getRequestURI();
        if (reqURI.contains("/login.xhtml")
                || (Objects.nonNull(session) && Objects.nonNull(session.getAttribute("isAuthorized")))) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect(reqt.getContextPath() + "/login.xhtml");
        }
    }
}
