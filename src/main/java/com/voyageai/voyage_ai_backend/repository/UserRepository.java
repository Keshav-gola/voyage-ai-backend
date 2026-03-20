package com.voyageai.voyage_ai_backend.repository;

import com.voyageai.voyage_ai_backend.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
