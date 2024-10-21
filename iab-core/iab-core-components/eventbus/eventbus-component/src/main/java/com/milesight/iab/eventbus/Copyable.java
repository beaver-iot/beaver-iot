package com.milesight.iab.eventbus;

/**
 * @author leon
 */
public interface Copyable<T> {

    void copy(T source, T target);

}