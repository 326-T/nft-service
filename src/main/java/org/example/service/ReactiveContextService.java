package org.example.service;

import org.example.constant.ContextKeys;
import org.example.persistence.entity.Applicant;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Service
public class ReactiveContextService {

  public Applicant getCurrentApplicant(ServerWebExchange exchange) {
    return exchange.getAttribute(ContextKeys.APPLICANT_KEY);
  }
}
