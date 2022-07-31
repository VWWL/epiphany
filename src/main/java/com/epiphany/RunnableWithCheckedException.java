package com.epiphany;

@FunctionalInterface
public interface RunnableWithCheckedException {
    void run() throws Exception;
}
