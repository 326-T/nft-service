package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth")
@Data
public class AuthConfig {

  private PathAndMethod[] nonAuthPaths;

  @Data
  public static class PathAndMethod {
    private String path;
    private String method;
  }
}
