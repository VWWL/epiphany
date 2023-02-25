package com.epiphany.general;

public final class RunnableExceptions implements Exceptions {
    private final RunnableWithCheckedException runnable;

    public RunnableExceptions(final RunnableWithCheckedException runnable) {
        this.runnable = runnable;
    }

    public void run() {
        try {
            runnable.run();
        } catch (Exception e) {
            throw Exceptions.wrap(e);
        }
    }

    public void elseThrow(final String message) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(message);
        }
    }
}
