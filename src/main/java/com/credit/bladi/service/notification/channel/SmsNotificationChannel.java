package com.credit.bladi.service.notification.channel;

import static com.credit.bladi.common.PiiMasker.maskPhone;

import com.credit.bladi.model.notification.RenderedNotification;
import com.credit.bladi.service.notification.NotificationChannel;
import com.credit.bladi.service.notification.sms.ISmsGateway;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationChannel implements NotificationChannel {
    private static final Logger LOGGER = LogManager.getLogger(SmsNotificationChannel.class);

    private final ISmsGateway smsGateway;

    private final boolean enabled;

    public SmsNotificationChannel(ISmsGateway smsGateway,
            @Value("${notification.sms.enabled:false}") boolean enabled) {
        this.smsGateway = smsGateway;
        this.enabled = enabled;
    }

    @Override
    public void send(RenderedNotification message) {
        if (message.recipientPhone() == null || message.recipientPhone().isBlank()) {
            LOGGER.debug("SMS_NOTIFICATION_SKIPPED recipient={} reason=no-phone-on-file",
                message.recipientUsername());
            return;
        }
        String smsText = message.title() + ": " + message.body();
        if (!enabled) {
            LOGGER.info("SMS_NOTIFICATION_WOULD_SEND to={} length={}", maskPhone(message.recipientPhone()),
                smsText.length());
            return;
        }
        smsGateway.send(message.recipientPhone(), smsText);
    }
}
