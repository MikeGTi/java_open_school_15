package ru.t1.java.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.base.model.DataSourceErrorLog;


public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLog, Long> {
}