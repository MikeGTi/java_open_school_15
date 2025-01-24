package ru.t1.java.base.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.base.dto.DataSourceErrorLogDto;
import ru.t1.java.base.model.DataSourceErrorLog;
import ru.t1.java.base.repository.DataSourceErrorLogRepository;
import ru.t1.java.base.service.ErrorLogService;
import ru.t1.java.base.util.DatasourceErrorLogMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorLogServiceImpl implements ErrorLogService {

    private final KafkaTemplate<String, DataSourceErrorLogDto> kafkaTemplate;
    private final DataSourceErrorLogRepository repository;

    @Value("${t1.kafka.producer.topic.data-source-errors}")
    public String topic;

    @Value("${t1.kafka.producer.topic.header.data-source-errors-header}")
    private String header;

    @Override
    public void sendError(Exception e, String methodSignature) {
        DataSourceErrorLogDto errorLogDto = DatasourceErrorLogMapper.toDto(getDataSourceErrorLog(e, methodSignature));
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader(header, header.getBytes(StandardCharsets.UTF_8)));
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, DataSourceErrorLogDto> record = new ProducerRecord<>(topic,
                                                                                1,
                                                                                        key,
                                                                                        errorLogDto,
                                                                                        headers);

        try {
            kafkaTemplate.send(record);
            log.info("Successfully sent error log to Kafka: {}", errorLogDto);
        } catch (Exception ex) {
            log.error("Failed to send message to Kafka", ex);
        } finally {
            kafkaTemplate.flush();
        }

    }

    @Override
    public void saveError(Exception e, String methodSignature) {
        log.info("Saving datasource error log: starts");
        DataSourceErrorLog savedError = repository.save(getDataSourceErrorLog(e, methodSignature));
        log.info("Saving datasource error log: ends, ID: {}", savedError.getId());
    }

    private DataSourceErrorLog getDataSourceErrorLog(Exception e, String methodSignature) {
        return DataSourceErrorLog.builder()
                .message(e.getMessage())
                .stackTrace(Arrays.toString(e.getStackTrace()))
                .methodSignature(methodSignature)
                .created(LocalDateTime.now())
                .build();
    }
}