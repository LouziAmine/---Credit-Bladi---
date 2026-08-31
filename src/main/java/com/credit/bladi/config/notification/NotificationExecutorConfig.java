package com.credit.bladi.config.notification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
public class NotificationExecutorConfig {
    @Bean("notificationTaskExecutor")
    public TaskExecutor notificationTaskExecutor() {
        return new VirtualThreadTaskExecutor("notification-");
    }
}
