package org.example.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.example.constant.OfferStatus;
import org.example.persistence.entity.Offer;

@Getter
public class OfferResponse {

  private final UUID uuid;
  private final UUID resumeUuid;
  private final UUID companyUuid;
  private final Float price;
  private final String message;
  private final Integer statusId;
  private final String status;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime updatedAt;

  public OfferResponse(Offer offer) {
    this.uuid = offer.getUuid();
    this.resumeUuid = offer.getResumeUuid();
    this.companyUuid = offer.getCompanyUuid();
    this.price = offer.getPrice();
    this.message = offer.getMessage();
    this.statusId = offer.getStatusId();
    this.status = OfferStatus.valueOf(offer.getStatusId()).getName();
    this.createdAt = offer.getCreatedAt();
    this.updatedAt = offer.getUpdatedAt();
  }
}
