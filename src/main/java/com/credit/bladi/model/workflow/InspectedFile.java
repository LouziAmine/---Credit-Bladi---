package com.credit.bladi.model.workflow;

import com.credit.bladi.model.enums.ScanStatus;

public record InspectedFile(String originalFilename, byte[] content, String contentType, String checksum,
                            ScanStatus scanStatus) {
}
