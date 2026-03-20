package com.voyageai.voyage_ai_backend.dto;

import lombok.Data;

@Data
public class TravelPlanRequest {

    private String source;
    private String destination;
    private String budget;
    private String travelMode;
    private String startDate;
    private String endDate;
    private int travellers;
}
