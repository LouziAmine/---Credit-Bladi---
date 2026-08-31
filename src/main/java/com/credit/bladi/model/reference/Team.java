package com.credit.bladi.model.reference;

import java.util.UUID;

public record Team(
        Long id,
        UUID publicId,
        String code,
        String name
) {
}
