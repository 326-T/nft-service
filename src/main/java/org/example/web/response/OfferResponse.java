package org.example.web.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.example.persistence.entity.Offer;

@Getter
public class OfferResponse {

  private final UUID uuid;
  private final UUID resumeUuid;
  private final UUID companyUuid;
  private final Float price;
  private final String message;
  private final Integer statusId;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public OfferResponse(Offer offer) {
    this.uuid = offer.getUuid();
    this.resumeUuid = offer.getResumeUuid();
    this.companyUuid = offer.getCompanyUuid();
    this.price = offer.getPrice();
    this.message = offer.getMessage();
    this.statusId = offer.getStatusId();
    this.createdAt = offer.getCreatedAt();
    this.updatedAt = offer.getUpdatedAt();
  }
}
