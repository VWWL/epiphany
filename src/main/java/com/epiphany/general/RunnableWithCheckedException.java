package com.epiphany.general;

@FunctionalInterface
public interface RunnableWithCheckedException {
    void run() throws Exception;
}
