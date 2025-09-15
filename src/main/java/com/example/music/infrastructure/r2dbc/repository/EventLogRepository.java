package com.example.music.infrastructure.r2dbc.repository;

import com.example.music.domain.entity.EventLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLogRepository extends ReactiveCrudRepository<EventLog, String> {

}
