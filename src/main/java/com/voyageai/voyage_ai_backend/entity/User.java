package com.voyageai.voyage_ai_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @NotBlank
    private String name;

    @Indexed(unique = true)
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private LocalDateTime createdAt;
}

