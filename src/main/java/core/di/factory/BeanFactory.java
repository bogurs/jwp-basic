package core.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import core.annotation.PostConstruct;

public class BeanFactory implements BeanDefinitionRegistry {
    private static final Logger log = LoggerFactory.getLogger(BeanFactory.class);

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    private Map<Class<?>, BeanDefinition> beanDefinitions = Maps.newHashMap();

    public void initialize() {
        for (Class<?> clazz : getBeanClasses()) {
            getBean(clazz);
        }
    }

    public Set<Class<?>> getBeanClasses() {
        return beanDefinitions.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        Object bean = beans.get(clazz);
        if (bean != null) {
            return (T) bean;
        }
        
        BeanDefinition beanDefinition = beanDefinitions.get(clazz);
        if (beanDefinition != null && beanDefinition instanceof AnnotatedBeanDefinition) {
        	bean = createAnnotatedBean(beanDefinition);
        	beans.put(clazz, bean);
        	return (T) bean;
        }

        Class<?> concreteClass = findConcreteClass(clazz);
        beanDefinition = beanDefinitions.get(concreteClass);
        bean = inject(beanDefinition);
        beans.put(concreteClass, bean);
        initialize(bean, concreteClass);
        return (T) bean;
    }

    private void initialize(Object bean, Class<?> beanClass) {
    	Set<Method> initializeMethods = BeanFactoryUtils.getBeanMethods(beanClass, PostConstruct.class);
    	if (initializeMethods.isEmpty()) {
    		return;
    	}
    	for (Method initializeMethod : initializeMethods) {
    		log.debug("@PostConstruct Initialize Method : {}", initializeMethod);
    		BeanFactoryUtils.invokeMethod(initializeMethod, bean, populateArguments(initializeMethod.getParameterTypes()));
    	}
	}

	private Object createAnnotatedBean(BeanDefinition beanDefinition) {
    	AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) beanDefinition;
        Method method = abd.getMethod();
        Object[] args = populateArguments(method.getParameterTypes());
        return BeanFactoryUtils.invokeMethod(method, getBean(method.getDeclaringClass()), args);
	}

	private Object[] populateArguments(Class<?>[] paramTypes) {
		List<Object> args = Lists.newArrayList();
        for (Class<?> param : paramTypes) {
            Object bean = getBean(param);
            if (bean == null) {
                throw new NullPointerException(param + "에 해당하는 Bean이 존재하지 않습니다.");
            }
            args.add(getBean(param));
        }
        return args.toArray();
	}

	private Class<?> findConcreteClass(Class<?> clazz) {
        Set<Class<?>> beanClasses = getBeanClasses();
        Class<?> concreteClazz = BeanFactoryUtils.findConcreteClass(clazz, beanClasses);
        if (!beanClasses.contains(concreteClazz)) {
            throw new IllegalStateException(clazz + "는 Bean이 아니다.");
        }
        return concreteClazz;
    }

    private Object inject(BeanDefinition beanDefinition) {
        if (beanDefinition.getResolvedInjectMode() == InjectType.INJECT_NO) {
            return BeanUtils.instantiate(beanDefinition.getBeanClass());
        } else if (beanDefinition.getResolvedInjectMode() == InjectType.INJECT_FIELD) {
            return injectFields(beanDefinition);
        } else {
            return injectConstructor(beanDefinition);
        }
    }

    private Object injectConstructor(BeanDefinition beanDefinition) {
        Constructor<?> constructor = beanDefinition.getInjectConstructor();
        List<Object> args = Lists.newArrayList();
        for (Class<?> clazz : constructor.getParameterTypes()) {
            args.add(getBean(clazz));
        }
        return BeanUtils.instantiateClass(constructor, args.toArray());
    }

    private Object injectFields(BeanDefinition beanDefinition) {
        Object bean = BeanUtils.instantiate(beanDefinition.getBeanClass());
        Set<Field> injectFields = beanDefinition.getInjectFields();
        for (Field field : injectFields) {
            injectField(bean, field);
        }
        return bean;
    }

    private void injectField(Object bean, Field field) {
        log.debug("Inject Bean : {}, Field : {}", bean, field);
        try {
            field.setAccessible(true);
            field.set(bean, getBean(field.getType()));
        } catch (IllegalAccessException | IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }

    public void clear() {
        beanDefinitions.clear();
        beans.clear();
    }

    @Override
    public void registerBeanDefinition(Class<?> clazz, BeanDefinition beanDefinition) {
        log.debug("register bean : {}", clazz);
        beanDefinitions.put(clazz, beanDefinition);
    }
}
