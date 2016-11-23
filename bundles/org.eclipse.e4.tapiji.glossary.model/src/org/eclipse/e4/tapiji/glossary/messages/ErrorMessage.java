package org.eclipse.e4.tapiji.glossary.messages;


public final class ErrorMessage {

    private final String title;
    private final String message;

    public ErrorMessage(final String title, final String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
