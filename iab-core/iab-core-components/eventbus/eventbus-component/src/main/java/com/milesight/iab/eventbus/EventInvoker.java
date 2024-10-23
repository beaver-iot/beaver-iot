package com.milesight.iab.eventbus;

import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.eventbus.api.IdentityKey;

import java.lang.reflect.InvocationTargetException;

/**
 * @author leon
 */
public interface EventInvoker<T extends Event<? extends IdentityKey>> {

    Object invoke(T event, String[] matchMultiKeys) throws InvocationTargetException, IllegalAccessException ;

}
