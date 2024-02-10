package org.example.service;

import java.util.List;
import org.example.constant.ContextKeys;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Service
public class ReactiveContextService {

  public Boolean containsAttributes(ServerWebExchange exchange, ContextKeys... keys) {
    List<ContextKeys> keyList = List.of(keys);
    return keyList.stream().anyMatch(key -> exchange.getAttribute(key.getKey()) != null);
  }

  public <T> T getAttribute(ServerWebExchange exchange, ContextKeys key) {
    return exchange.getAttribute(key.getKey());
  }

  public <T> void setAttribute(ServerWebExchange exchange, ContextKeys key, T object) {
    exchange.getAttributes().put(key.getKey(), object);
  }
}
