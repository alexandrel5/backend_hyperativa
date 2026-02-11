package com.hyperativa.cards.repository;

import com.hyperativa.cards.entity.ApiLog;
import com.hyperativa.cards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepository extends JpaRepository<ApiLog, User> {
}
