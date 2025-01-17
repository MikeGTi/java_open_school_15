package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.DataSourceErrorLog;

@Component
public class DatasourceErrorLogMapper {

    public static DataSourceErrorLog toEntity(DataSourceErrorLogDto dto) {
        return DataSourceErrorLog.builder()
                .stackTrace(dto.getStackTrace())
                .message(dto.getMessage())
                .methodSignature(dto.getMethodSignature())
                .created(dto.getCreated())
                .build();
    }

    public static DataSourceErrorLogDto toDto(DataSourceErrorLog entity) {
        return DataSourceErrorLogDto.builder()
                .stackTrace(entity.getStackTrace())
                .message(entity.getMessage())
                .methodSignature(entity.getMethodSignature())
                .created(entity.getCreated())
                .build();
    }
}
