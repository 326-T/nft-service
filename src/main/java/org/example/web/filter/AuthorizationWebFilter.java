package org.example.web.filter;

import lombok.NonNull;
import org.example.constant.ContextKeys;
import org.example.error.exception.ForbiddenException;
import org.example.persistence.entity.Applicant;
import org.example.persistence.entity.Company;
import org.example.service.ReactiveContextService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(2)
@Component
public class AuthorizationWebFilter implements WebFilter {

  private final ReactiveContextService reactiveContextService;

  public AuthorizationWebFilter(ReactiveContextService reactiveContextService) {
    this.reactiveContextService = reactiveContextService;
  }

  @Override
  @NonNull
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    HttpMethod method = exchange.getRequest().getMethod();
    String path = exchange.getRequest().getPath().value();
    System.out.println("path: " + path);
    System.out.println("method: " + method);
    Applicant applicant = reactiveContextService.getAttribute(exchange, ContextKeys.APPLICANT_KEY);
    System.out.println("applicant: " + applicant);
    Company company = reactiveContextService.getAttribute(exchange, ContextKeys.COMPANY_KEY);
    System.out.println("company: " + company);
    if (HttpMethod.OPTIONS.equals(method)) {
      return chain.filter(exchange);
    }
    if (HttpMethod.POST.equals(method) && (
        path.startsWith("/api/v1/applicants") || path.startsWith("/api/v1/companies")) ||
        path.startsWith("/api/v1/applicants/login") || path.startsWith("/api/v1/companies/login")) {
      return chain.filter(exchange);
    }
    // 認証されていてGETメソッドの場合は全てのAPIを許可する
    if (Boolean.TRUE.equals(reactiveContextService.containsAttributes(
        exchange, ContextKeys.APPLICANT_KEY, ContextKeys.COMPANY_KEY)) &&
        HttpMethod.GET.equals(method)) {
      return chain.filter(exchange);
    }

    if (Boolean.TRUE.equals(
        reactiveContextService.containsAttributes(exchange, ContextKeys.APPLICANT_KEY)) &&
        (path.startsWith("/api/v1/resumes") || path.startsWith("/api/v1/applicants"))) {
      return chain.filter(exchange);
    }

    if (Boolean.TRUE.equals(
        reactiveContextService.containsAttributes(exchange, ContextKeys.COMPANY_KEY)) &&
        path.startsWith("/api/v1/companies")) {
      return chain.filter(exchange);
    }

    return Mono.error(new ForbiddenException("認可されていません。"));
  }
}