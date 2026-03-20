package com.voyageai.voyage_ai_backend.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class Trip {
    @Id
    private String id;

    private String userEmail;

    private Object request;

    private String plan;

    private LocalDateTime createdAt;
}
