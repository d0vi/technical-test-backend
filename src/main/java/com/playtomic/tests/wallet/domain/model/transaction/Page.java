package com.playtomic.tests.wallet.domain.model.transaction;

import java.util.List;

public record Page<T>(
    List<T> content,
    int currentPage,
    int totalPages,
    long totalElements,
    boolean hasNext,
    boolean hasPrevious) {}
