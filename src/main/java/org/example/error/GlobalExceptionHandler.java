package org.example.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.error.exception.PasswordAuthenticationException;
import org.example.error.exception.ForbiddenException;
import org.example.error.response.ErrorResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @NonNull
  @Override
  public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
    if (ex instanceof ForbiddenException) {
      return setResponse(exchange, HttpStatus.UNAUTHORIZED,
          ErrorResponse.builder()
              .status(HttpStatus.UNAUTHORIZED.value())
              .summary("クライアント側の認証切れ")
              .detail(ex.toString())
              .message("JWTが有効ではありません。")
              .build());
    }

    if (ex instanceof PasswordAuthenticationException) {
      return setResponse(exchange, HttpStatus.UNAUTHORIZED,
          ErrorResponse.builder()
              .status(HttpStatus.UNAUTHORIZED.value())
              .summary("emailまたはpasswordが間違っている")
              .detail(ex.toString())
              .message("emailまたはpasswordが間違っています。")
              .build());
    }

    log.error("""
        予期せぬエラーが発生しました。
        %s
        """.formatted(ex.getMessage()));
    return setResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .summary("ハンドリングしていない例外が発生")
            .detail(ex.toString())
            .message("予期せぬエラーが発生しました。")
            .build());
  }

  private Mono<Void> setResponse(ServerWebExchange exchange, HttpStatusCode status, Object body) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(status);
    response.getHeaders().setAccessControlAllowOrigin("*");
    response.getHeaders().setAccessControlAllowHeaders(List.of("*"));
    response.getHeaders().setAccessControlAllowMethods(List.of(
        HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.OPTIONS));
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    Flux<DataBuffer> buffer = Mono.just(body)
        .flatMap(this::writeValueAsBytes)
        .map(b -> response.bufferFactory().wrap(b))
        .flux();
    return response.writeWith(buffer);
  }

  private Mono<byte[]> writeValueAsBytes(Object object) {
    try {
      return Mono.just(objectMapper.writeValueAsBytes(object));
    } catch (JsonProcessingException e) {
      return Mono.error(e);
    }
  }
}
