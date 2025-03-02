package com.notification.service.builder;

import java.time.LocalDateTime;

public interface Auditable {
    String getCreatedBy();
    LocalDateTime getCreatedAt();
    String getLastModifiedBy();
    LocalDateTime getLastModifiedAt();
}