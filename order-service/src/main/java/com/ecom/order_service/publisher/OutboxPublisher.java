package com.ecom.order_service.publisher;

import com.ecom.order_service.entity.OutboxEvent;
import com.ecom.order_service.repository.OutBoxEventRepository;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPublisher {

    private final OutBoxEventRepository outBoxEventRepository;
    private final JmsTemplate jmsTemplate;


    public OutboxPublisher(OutBoxEventRepository outBoxEventRepository, JmsTemplate jmsTemplate) {
        this.outBoxEventRepository = outBoxEventRepository;
        this.jmsTemplate = jmsTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {
        List<OutboxEvent> events
                = outBoxEventRepository.findByStatus("NEW");

        for (OutboxEvent event : events) {
            try {

                jmsTemplate.convertAndSend(
                        "order.created",
                        event.getPayload()
                );

                event.setStatus("PUBLISHED");
                outBoxEventRepository.save(event);

            } catch (Exception e) {
                System.err.println(
                        "Failed to publish event: "
                                + event.getEventId()
                );
            }
        }
    }
}
