package com.doyuyu.server.netty;

public interface Pipeline {
    Pipeline taskReceived();

    Pipeline taskFiltered();

    Pipeline taskExecuted();

    Pipeline afterCompletion();
}
