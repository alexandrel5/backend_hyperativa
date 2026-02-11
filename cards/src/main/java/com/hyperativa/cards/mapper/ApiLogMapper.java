package com.hyperativa.cards.mapper;

import com.hyperativa.cards.dto.ApiLogDto;
import com.hyperativa.cards.entity.ApiLog;

public class ApiLogMapper {
    public static ApiLogDto mapToApiLogDto(ApiLog apiLog, ApiLogDto apiLogDto){
        apiLogDto.setUser(apiLog.getUser());
        apiLogDto.setAction(apiLog.getAction());
        apiLogDto.setRequestData(apiLog.getRequestData());
        apiLogDto.setResponseData(apiLog.getResponseData());
        return apiLogDto;
    }

    public static ApiLog mapToApiLog(ApiLogDto apiLogDto, ApiLog apiLog){
        apiLog.setUser(apiLogDto.getUser());
        apiLog.setAction(apiLogDto.getAction());
        apiLog.setRequestData(apiLogDto.getRequestData());
        apiLog.setResponseData(apiLogDto.getResponseData());
        return apiLog;
    }

}
