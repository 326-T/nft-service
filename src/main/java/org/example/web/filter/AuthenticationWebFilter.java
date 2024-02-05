package org.example.web.filter;

import java.util.Objects;
import lombok.NonNull;
import org.example.constant.ContextKeys;
import org.example.constant.CookieKeys;
import org.example.persistence.entity.Applicant;
import org.example.persistence.entity.Company;
import org.example.service.Base64Service;
import org.example.service.JwtService;
import org.example.service.ReactiveContextService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class AuthenticationWebFilter implements WebFilter {

  private final JwtService jwtService;
  private final Base64Service base64Service;
  private final ReactiveContextService reactiveContextService;

  public AuthenticationWebFilter(JwtService jwtService, Base64Service base64Service,
      ReactiveContextService reactiveContextService) {
    this.jwtService = jwtService;
    this.base64Service = base64Service;
    this.reactiveContextService = reactiveContextService;
  }

  /**
   * 認証を行う
   * OPTIONSメソッドの場合は認証を行わない
   * auth.nonAuthPathsに含まれる場合は認証を行わない
   *
   * @param exchange サーバーとのやり取り
   * @param chain    フィルターチェーン
   *
   * @return 認証されたユーザー
   */
  @Override
  @NonNull
  public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    Mono<Applicant> applicantMono = Mono.justOrEmpty(
            exchange.getRequest().getCookies().getFirst(CookieKeys.APPLICANT_TOKEN))
        .filter(Objects::nonNull)
        .map(HttpCookie::getValue)
        .map(base64Service::decode)
        .map(jwtService::decodeApplicant);
    Mono<Company> companyMono = Mono.justOrEmpty(
            exchange.getRequest().getCookies().getFirst(CookieKeys.COMPANY_TOKEN))
        .filter(Objects::nonNull)
        .map(HttpCookie::getValue)
        .map(base64Service::decode)
        .map(jwtService::decodeCompany);
    return applicantMono
        .doOnNext(a -> reactiveContextService.setAttribute(exchange, ContextKeys.APPLICANT_KEY, a))
        .then(companyMono)
        .doOnNext(c -> reactiveContextService.setAttribute(exchange, ContextKeys.COMPANY_KEY, c))
        .then(chain.filter(exchange));
  }
}
