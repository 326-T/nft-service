package org.example.web.filter;

import lombok.NonNull;
import org.example.constant.ContextKeys;
import org.example.error.exception.ForbiddenException;
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
    if (HttpMethod.OPTIONS.equals(method)) {
      return chain.filter(exchange);
    }
    if (HttpMethod.POST.equals(method) && (
        path.startsWith("/api/v1/applicants") || path.startsWith("/api/v1/companies")) ||
        path.startsWith("/api/v1/applicants/login") || path.startsWith("/api/v1/companies/login")) {
      return chain.filter(exchange);
    }
    if (Boolean.TRUE.equals(reactiveContextService
        .containsAttributes(exchange, ContextKeys.APPLICANT_KEY)) &&
        Boolean.TRUE.equals(applicantHasPermission(method, path))) {
      return chain.filter(exchange);
    }
    if (Boolean.TRUE.equals(reactiveContextService
        .containsAttributes(exchange, ContextKeys.COMPANY_KEY)) &&
        Boolean.TRUE.equals(companyHasPermission(method, path))) {
      return chain.filter(exchange);
    }
    return Mono.error(new ForbiddenException("認可されていません。"));
  }

  private Boolean applicantHasPermission(HttpMethod method, String path) {
    if (HttpMethod.GET.equals(method)) {
      return Boolean.TRUE;
    }
    if (path.startsWith("/api/v1/resumes") || path.startsWith("/api/v1/applicants")) {
      return Boolean.TRUE;
    }
    if (path.startsWith("/api/v1/offers/accepted") || path.startsWith("/api/v1/offers/rejected")) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  private Boolean companyHasPermission(HttpMethod method, String path) {
    if (HttpMethod.GET.equals(method)) {
      return Boolean.TRUE;
    }
    if (path.startsWith("/api/v1/companies")) {
      return Boolean.TRUE;
    }
    if (path.startsWith("/api/v1/offers") ||
        (!path.startsWith("/api/v1/offers/accepted"))
        || !path.startsWith("/api/v1/offers/rejected")) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}