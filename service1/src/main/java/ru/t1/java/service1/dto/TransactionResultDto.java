package ru.t1.java.service1.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.service1.model.enums.TransactionStatus;

import java.io.Serializable;
import java.util.UUID;

/**
 * Представляет DTO (Data Transfer Object) для сообщения, содержащего результат транзакции.
 * Этот класс используется для передачи результатов транзакции между различными слоями приложения.
 *
 * @author mboychook
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResultDto implements Serializable {
    /**
     * Уникальный идентификатор транзакции.
     */
    private UUID transactionUuid;

    /**
     * Статус транзакции.
     */
    private TransactionStatus transactionStatus;

    /**
     * Уникальный идентификатор счета, связанного с транзакцией.
     */
    private UUID accountUuid;
}