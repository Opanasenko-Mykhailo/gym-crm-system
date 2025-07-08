package com.gcs.app.facade.constant;

public final class ValidationPatterns {
    public static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\]{};':\"\\\\|,.<>/?]).{8,}$";

    private ValidationPatterns() {
    }
}
