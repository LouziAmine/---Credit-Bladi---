package com.credit.bladi.model.client;

import com.credit.bladi.model.vo.Rib;
import java.time.Instant;
import java.util.UUID;

public record EnterpriseModel(Long id, UUID publicId, String name, String sector, String registrationNumber,
        Rib rib, Instant createdAt) {
}
