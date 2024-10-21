package com.milesight.iab.eventbus.subscribe;

/**
 * @author leon
 */
public interface Copyable<T> {

    void copy(T source, T target);

}