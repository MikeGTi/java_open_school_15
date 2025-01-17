package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.ErrorLogService;
import ru.t1.java.demo.util.DatasourceErrorLogMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ErrorLogServiceImpl implements ErrorLogService {

    private final DataSourceErrorLogRepository repository;
    private final KafkaTemplate<String, DataSourceErrorLogDto> kafkaTemplate;

    @Value("${t1.kafka.topic.data-source-errors}")
    public String topic;

    @Value("${t1.kafka.topic.header.data-source-errors-header}")
    private String header;

    @Override
    @Transactional
    public void saveErrorInfo(Exception e, String methodSignature) {
        DataSourceErrorLog error = getDataSourceErrorLog(e, methodSignature);
        repository.saveAndFlush(error);
    }

    /*@Override
    public DataSourceErrorLog saveDataSourceErrorLog(DataSourceErrorLog dataSourceErrorLog) {
        //log.info("Saving datasource error log: starts");
        DataSourceErrorLog savedLog = repository.save(dataSourceErrorLog);
        //log.info("Saving datasource error log: starts, ID: {}", savedLog.getId());
        return savedLog;
    }*/

    @Override
    public void sendError(Exception e, String methodSignature) {
        DataSourceErrorLogDto errorLogDto = DatasourceErrorLogMapper.toDto(getDataSourceErrorLog(e, methodSignature));
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("error_type",
                header.getBytes(StandardCharsets.UTF_8)));
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, DataSourceErrorLogDto> record = new ProducerRecord<>(topic,
                1,
                key,
                errorLogDto,
                headers);
        kafkaTemplate.send(record);
    }

    private DataSourceErrorLog getDataSourceErrorLog(Exception e, String methodSignature) {
        return DataSourceErrorLog.builder()
                .stackTrace(Arrays.toString(e.getStackTrace()))
                .message(e.getMessage())
                .methodSignature(methodSignature)
                .created(LocalDateTime.now())
                .build();
    }
}