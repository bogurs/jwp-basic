package core.di.factory;

import static org.junit.Assert.*;

import javax.sql.DataSource;

import org.junit.Test;

import di.examples.ExampleConfig;

public class AnnotatedBeanDefinitionReaderTest {

	@Test
	public void test() {
		BeanFactory beanFactory = new BeanFactory();
		AnnotatedBeanDefinitionReader abdr = new AnnotatedBeanDefinitionReader(beanFactory);
		abdr.register(ExampleConfig.class);
		beanFactory.initialize();
		
		assertNotNull(beanFactory.getBean(DataSource.class));
	}

}
