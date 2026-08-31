package com.credit.bladi.service.notification.sms;

public interface ISmsGateway {
    void send(String phoneNumber, String text);
}
