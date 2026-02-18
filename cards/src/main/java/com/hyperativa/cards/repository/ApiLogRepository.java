package com.hyperativa.cards.repository;

import com.hyperativa.cards.entity.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiLogRepository extends JpaRepository<ApiLog, UUID> {
}
