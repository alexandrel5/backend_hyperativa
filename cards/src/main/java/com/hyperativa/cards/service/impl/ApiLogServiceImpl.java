package com.hyperativa.cards.service.impl;

import com.hyperativa.cards.entity.ApiLogEntity;
import com.hyperativa.cards.repository.ApiLogRepository;
import com.hyperativa.cards.service.IApiLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApiLogServiceImpl implements IApiLogService {

    private static final Logger log = LoggerFactory.getLogger(ApiLogServiceImpl.class);


    private final ApiLogRepository repository;

    public ApiLogServiceImpl(ApiLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveLog(ApiLogEntity apiLogEntity) {
        try {
            repository.save(apiLogEntity);
        } catch (Exception e) {
            log.error("Audit log save failed → {}", apiLogEntity, e);
        }
    }
}
