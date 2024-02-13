package org.example.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("companies")
public class Company {

  @Id
  private Long id;
  private UUID uuid;
  private String name;
  private String email;
  private String phone;
  private String address;
  private String passwordDigest;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Long version;
}