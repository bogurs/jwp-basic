package core.di.factory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import core.annotation.Controller;

public class BeanFactory implements BeanDefinitionRegistry {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstanticateBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    private List<Injector> injectors;
    
    private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();
    
    public BeanFactory() {
    }

    public BeanFactory(Set<Class<?>> preInstanticateBeans) {
        this.preInstanticateBeans = preInstanticateBeans;

        injectors = Arrays.asList(new FieldInjector(this), new SetterInjector(this), new ConstructorInjector(this));
    }

	public Set<Class<?>> getPreInstanticateBeans() {
        return preInstanticateBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }

    public void initialize() {
        for (Class<?> clazz : preInstanticateBeans) {
            if (beans.get(clazz) == null) {
                logger.debug("instantiated Class : {}", clazz);
                inject(clazz);
            }
        }
    }

    private void inject(Class<?> clazz) {
        for (Injector injector : injectors) {
            injector.inject(clazz);
        }
    }

    void registerBean(Class<?> clazz, Object bean) {
        beans.put(clazz, bean);
    }

    public Map<Class<?>, Object> getControllers() {
        Map<Class<?>, Object> controllers = Maps.newHashMap();
        for (Class<?> clazz : preInstanticateBeans) {
            Annotation annotation = clazz.getAnnotation(Controller.class);
            if (annotation != null) {
                controllers.put(clazz, beans.get(clazz));
            }
        }
        return controllers;
    }

    void clear() {
        preInstanticateBeans.clear();
        beans.clear();
    }

	@Override
	public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
		logger.debug("register bean : {}", clazz);
		beanDefinitions.put(clazz, beanDefinition);
	}
}
