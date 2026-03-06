package com.yubzhou.statemachine.statistics.service;

import java.util.Map;

/**
 * Service interface for order statistics.
 */
public interface OrderStatisticsService {

    /** Return a map of state name -> order count. */
    Map<String, Long> countByState();

    /** Return the total number of non-deleted orders. */
    long totalCount();
}
