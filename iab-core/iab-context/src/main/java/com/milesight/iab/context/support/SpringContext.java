package com.milesight.iab.context.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;

import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 * @author leon
 */
@Slf4j
public class SpringContext implements BeanFactoryPostProcessor {

    public static ConfigurableListableBeanFactory beanFactory;

    @Override
    public  void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringContext.beanFactory = beanFactory;
    }
    public static ConfigurableListableBeanFactory getFactory()  {
        return beanFactory;
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean replaceBean(String beanName, T beanObject) throws BeansException {
        //反射获取Factory中的singletonObjects 将该名称下的bean进行替换
        try {
            Field singletonObjects = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
            singletonObjects.setAccessible(true);
            Map<String, Object> map = (Map<String, Object>) singletonObjects.get(beanFactory);
            if(!map.containsKey(beanName)){
                throw new NoSuchBeanDefinitionException(beanName) ;
            }
            map.put(beanName, beanObject);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("bean replace exception:" , e);
            throw new BeanCreationException(e.getMessage());
        }
        return true;
    }
    /**
     * @param name
     * @throws org.springframework.beans.BeansException
     *
     */
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz){
        Map<String, T> beansOfType = beanFactory.getBeansOfType(clazz);
        return beansOfType;
    }

    /**
     * @param clz
     * @return
     * @throws org.springframework.beans.BeansException
     *
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        @SuppressWarnings("unchecked")
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    /**
     * Returns true if the BeanFactory contains a bean definition matching the given name
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * Determines whether the bean definition registered with the given name is a singleton or a prototype. If the bean definition corresponding to the given name is not found, an exception (NoSuchBeanDefinitionException) will be thrown
     *
     * @param name
     * @return boolean
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     *
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    /**
     * @param name
     * @return Class
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     *
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * If the given bean name has aliases in the bean definition, return those aliases
     * @param name
     * @return
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     *
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

}
