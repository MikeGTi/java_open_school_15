package ru.t1.java.base.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.base.dto.MetricDto;
import ru.t1.java.base.service.MetricService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class MetricServiceImpl implements MetricService<MetricDto> {

    private final KafkaTemplate<String, MetricDto> kafkaTemplate;

    @Value("${t1.kafka.producer.topic.methods-metric}")
    public String topic;

    @Value("${t1.kafka.producer.topic.header.methods-metric-header}")
    private String header;

    @Override
    public void sendMetric(MetricDto metricDto) {
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader(header, header.getBytes(StandardCharsets.UTF_8)));
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, MetricDto> record = new ProducerRecord<>(topic,
                                                                    1,
                                                                            key,
                                                                            metricDto,
                                                                            headers);

        try {
            kafkaTemplate.send(record);
            log.info("Successfully sent metric log to Kafka: {}", metricDto);
        } catch (Exception ex) {
            log.error("Failed to send metric to Kafka", ex);
        } finally {
            kafkaTemplate.flush();
        }
    }

}