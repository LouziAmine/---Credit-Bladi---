package com.credit.bladi.controller.notification.mapper;

import com.credit.bladi.common.CreditPage;
import com.credit.bladi.common.CreditPageMapper;
import com.credit.bladi.controller.dto.notification.response.NotificationResponseDTO;
import com.credit.bladi.model.notification.NotificationModel;

public final class NotificationMapper {
    private NotificationMapper() {
    }

    public static NotificationResponseDTO toResponseDTO(NotificationModel model) {
        return new NotificationResponseDTO(
            model.publicId(),
            model.type().name(),
            model.title(),
            model.body(),
            model.link(),
            model.read(),
            model.createdAt());
    }

    public static CreditPage<NotificationResponseDTO> toCreditPage(CreditPage<NotificationModel> source) {
        return CreditPageMapper.map(source, NotificationMapper::toResponseDTO);
    }
}
