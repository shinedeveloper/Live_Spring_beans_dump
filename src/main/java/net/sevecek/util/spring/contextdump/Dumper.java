package net.sevecek.util.spring.contextdump;

import java.io.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;

public class Dumper implements BeanPostProcessor, BeanFactoryAware {

    private BeanRecordRegistry beanRegistry = new BeanRecordRegistry();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        beanRegistry.add(beanName, bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        beanRegistry.initializing(beanName, bean);
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanRegistry.setBeanFactory((ConfigurableListableBeanFactory) beanFactory);
    }

    public void dumpBeans(PrintWriter out) {
        beanRegistry.dumpBeans(out);
    }

    public void dumpBeans(PrintStream out) {
        beanRegistry.dumpBeans(new PrintWriter(new OutputStreamWriter(out), true));
    }
}
