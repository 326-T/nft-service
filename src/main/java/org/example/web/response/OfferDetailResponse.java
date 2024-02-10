package org.example.web.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.example.persistence.dto.OfferDetailView;

@Getter
public class OfferDetailResponse {

  private final UUID uuid;
  private final UUID resumeUuid;
  private final UUID companyUuid;
  private final String companyName;
  private final Float price;
  private final String message;
  private final Integer statusId;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public OfferDetailResponse(OfferDetailView offer) {
    this.uuid = offer.getUuid();
    this.resumeUuid = offer.getResumeUuid();
    this.companyUuid = offer.getCompanyUuid();
    this.companyName = offer.getCompanyName();
    this.price = offer.getPrice();
    this.message = offer.getMessage();
    this.statusId = offer.getStatusId();
    this.createdAt = offer.getCreatedAt();
    this.updatedAt = offer.getUpdatedAt();
  }
}
