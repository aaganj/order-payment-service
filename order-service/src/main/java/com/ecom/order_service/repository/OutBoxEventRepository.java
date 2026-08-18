package com.ecom.order_service.repository;

import com.ecom.order_service.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutBoxEventRepository extends JpaRepository<OutboxEvent,Long> {

    List<OutboxEvent> findByStatus(String status);
}
