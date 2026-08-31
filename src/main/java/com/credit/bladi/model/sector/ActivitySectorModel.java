package com.credit.bladi.model.sector;

import java.time.Instant;
import java.util.UUID;

public record ActivitySectorModel(Long id, UUID publicId, String name, Instant createdAt) {
}
