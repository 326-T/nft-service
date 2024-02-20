package org.example.web.request;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.constant.OfferStatus;
import org.example.persistence.entity.Offer;

@Data
@NoArgsConstructor
public class OfferRequest {

  private UUID resumeUuid;
  private Float price;
  private String message;

  public Offer exportEntity() {
    return Offer.builder()
        .uuid(UUID.randomUUID())
        .resumeUuid(resumeUuid)
        .price(price)
        .message(message)
        .statusId(OfferStatus.PENDING.getId())
        .build();
  }
}
