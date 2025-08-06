package com.managersys.event;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailEventListener {

    @RabbitListener(queues = "${rabbitmq.queue.email}")
    public void handleEmailEvent(String message) {
        // In a real application, this would send an email
        // For now, we'll just log the message
        System.out.println("Sending email: " + message);
        
        // Here you would typically use a service like JavaMailSender
        // to send the actual email
        // emailService.sendConfirmationEmail(message);
    }
}
