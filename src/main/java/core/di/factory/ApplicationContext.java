package core.di.factory;

import java.util.Set;

public class ApplicationContext {
	private BeanFactory beanFactory;
	
	public ApplicationContext(Object... basePackage) {
		beanFactory = new BeanFactory();
		ClasspathBeanDefinitionScanner scanner = new ClasspathBeanDefinitionScanner(beanFactory);
		scanner.doScan(basePackage);
		beanFactory.initialize();
	}
	
	public <T> T getBean(Class<T> clazz) {
		return beanFactory.getBean(clazz);
	}
	
	public Set<Class<?>> getBeanClasses() {
		return beanFactory.getBeanClasses();
	}
}
