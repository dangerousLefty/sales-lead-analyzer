package com.hamza.smartleadgenerator.message;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
        @NotBlank(message = "content is required")
        String content
) {}
