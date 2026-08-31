package com.credit.bladi.model.reference;

import java.util.UUID;

public record Grade(
        Long id,
        UUID publicId,
        String code,
        String name
) {
}
