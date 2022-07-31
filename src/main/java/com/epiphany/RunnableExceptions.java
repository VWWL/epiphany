package com.epiphany;

public class RunnableExceptions implements Exceptions {
    private final RunnableWithCheckedException runnable;

    public RunnableExceptions(RunnableWithCheckedException runnable) {
        this.runnable = runnable;
    }

    public void run() {
        try {
            runnable.run();
        } catch (Exception e) {
            throw Exceptions.wrap(e);
        }
    }

    public void elseThrow(String message) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(message);
        }
    }
}
