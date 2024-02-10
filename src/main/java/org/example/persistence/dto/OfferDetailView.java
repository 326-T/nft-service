package org.example.persistence.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table("offer_detail_view")
public class OfferDetailView {

  private Long id;
  private UUID uuid;
  @Column("resume_uuid")
  private UUID resumeUuid;
  @Column("company_uuid")
  private UUID companyUuid;
  @Column("company_name")
  private String companyName;
  private Float price;
  private String message;
  @Column("status_id")
  private Integer statusId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
