package com.doyuyu.server;

public interface Pipeline {
    Pipeline taskReceived();

    Pipeline taskFiltered();

    Pipeline taskExecuted();

    Pipeline afterCompletion();
}
