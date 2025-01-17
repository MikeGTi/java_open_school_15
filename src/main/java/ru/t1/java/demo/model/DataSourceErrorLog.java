package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "data_source_error_log")
public class DataSourceErrorLog extends AbstractPersistable<Long> {

    @Column(name = "stack_trace")
    private String stackTrace;

    @Column(name = "message")
    private String message;

    @Column(name = "method_signature")
    private String methodSignature;

    @Column(name = "created")
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created = LocalDateTime.now();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataSourceErrorLog that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(getStackTrace(), that.getStackTrace()) &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getMethodSignature(), that.getMethodSignature()) &&
                Objects.equals(getCreated(), that.getCreated());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(getStackTrace());
        result = 31 * result + Objects.hashCode(getMessage());
        result = 31 * result + Objects.hashCode(getMethodSignature());
        result = 31 * result + Objects.hashCode(getCreated());
        return result;
    }
}
