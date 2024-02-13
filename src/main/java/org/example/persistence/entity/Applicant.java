package org.example.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("applicants")
public class Applicant {

  @Id
  private Long id;
  private UUID uuid;
  @Column("first_name")
  private String firstName;
  @Column("last_name")
  private String lastName;
  private String email;
  private String phone;
  private String address;
  @Column("password_digest")
  private String passwordDigest;
  @Column("created_at")
  private LocalDateTime createdAt;
  @Column("updated_at")
  private LocalDateTime updatedAt;
  private Long version;
}