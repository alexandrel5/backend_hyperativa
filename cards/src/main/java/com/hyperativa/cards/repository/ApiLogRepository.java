package com.hyperativa.cards.repository;

import com.hyperativa.cards.entity.ApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepository extends JpaRepository<ApiLogEntity, Integer> {
}
