package com.example.resqfood;

public class NotificationParams {
    private String title;
    private String message;

    // Private constructor to prevent direct instantiation
    private NotificationParams(Builder builder) {
        this.title = builder.title;
        this.message = builder.message;
    }

    // Builder class for constructing NotificationParams objects
    public static class Builder {
        private String title;
        private String message;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public NotificationParams build() {
            return new NotificationParams(this);
        }
    }
}
