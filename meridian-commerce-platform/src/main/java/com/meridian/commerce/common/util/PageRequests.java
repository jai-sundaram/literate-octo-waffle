package com.meridian.commerce.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageRequests {

    public static final int MAX_PAGE_SIZE = 100;

    private PageRequests() {
    }

    /**
     * Builds a Pageable from 1-based page numbers coming from the public API.
     */
    public static Pageable of(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(size, MAX_PAGE_SIZE);
        Sort sort = Sort.by(Sort.Direction.fromString(direction == null ? "ASC" : direction), sortBy);
        return PageRequest.of(page, safeSize, sort);
    }
}
