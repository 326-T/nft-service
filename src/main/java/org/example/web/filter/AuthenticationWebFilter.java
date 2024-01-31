package org.example.web.filter;

import io.netty.util.internal.StringUtil;
import java.util.Arrays;
import java.util.Objects;
import lombok.NonNull;
import org.example.config.AuthConfig;
import org.example.constant.ContextKeys;
import org.example.error.exception.UnauthenticatedException;
import org.example.persistence.entity.Applicant;
import org.example.service.ApplicantService;
import org.example.service.Base64Service;
import org.example.service.JwtService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class AuthenticationWebFilter implements WebFilter {

  private final JwtService jwtService;
  private final ApplicantService applicantService;
  private final Base64Service base64Service;
  private final AuthConfig authConfig;

  public AuthenticationWebFilter(JwtService jwtService, ApplicantService applicantService,
      Base64Service base64Service, AuthConfig authConfig) {
    this.jwtService = jwtService;
    this.applicantService = applicantService;
    this.base64Service = base64Service;
    this.authConfig = authConfig;
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
    if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
      return chain.filter(exchange);
    }
    if (Arrays.stream(authConfig.getNonAuthPaths())
        .anyMatch(
            p -> Objects.equals(p.getMethod(), exchange.getRequest().getMethod().toString()) &&
                Objects.equals(p.getPath(), exchange.getRequest().getPath().toString()))) {
      return chain.filter(exchange);
    }
    HttpCookie cookie = exchange.getRequest().getCookies().getFirst("token");
    if (Objects.isNull(cookie) || StringUtil.isNullOrEmpty(cookie.getValue())) {
      return Mono.error(new UnauthenticatedException("Authorization headerがありません。"));
    }
    return Mono.just(cookie.getValue())
        .map(this::jwtChain)
        .doOnNext(u -> exchange.getAttributes().put(ContextKeys.APPLICANT_KEY, u))
        .then(chain.filter(exchange));
  }

  /**
   * JWT認証の場合
   *
   * @param token JWT認証のトークン
   *
   * @return 認証されたユーザー
   */
  private Mono<Applicant> jwtChain(String token) {
    return Mono.just(token)
        .map(base64Service::decode)
        .map(jwtService::decodeApplicant)
        .flatMap(a -> applicantService.findByEmail(a.getEmail()))
        .switchIfEmpty(Mono.error(new UnauthenticatedException("存在しないユーザです。")));
  }
}
