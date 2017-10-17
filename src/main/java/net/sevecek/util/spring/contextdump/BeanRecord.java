package net.sevecek.util.spring.contextdump;

import org.springframework.beans.factory.config.*;

public class BeanRecord {

    private String name;
    private BeanDefinition beanDefinition;
//    private boolean isTopLevelBean;
    private Class<?> definitionClass;
    private Class<?> runtimeClass;
    private Object beanInstance;
    private boolean isDefaultBean;

    public BeanRecord(String beanName, Object bean) {
        name = beanName;
        beanInstance = bean;
        definitionClass = bean.getClass();
    }

    public BeanRecord(String beanName, Object bean, boolean isDefaultBean) {
        this(beanName, bean);
        this.isDefaultBean = isDefaultBean;
    }

    public Object getBeanInstance() {
        return beanInstance;
    }

    public String getName() {
        return name;
    }

    public boolean isAnonymous() {
        return name.matches("(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)#[0-9]+");
    }

    public Class<?> getDefinitionClass() {
        return definitionClass;
    }

    public Class<?> getRuntimeClass() {
        return runtimeClass;
    }

    public void setRuntimeClass(Class<?> runtimeClass) {
        if (this.runtimeClass != null && this.runtimeClass != definitionClass) {
            throw new IllegalStateException("Bean replaced more than once. From " + this.definitionClass.getName() + " to " + this.runtimeClass.getName() + " and now to " + runtimeClass.getName());
        }
        this.runtimeClass = runtimeClass;
    }

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    public void setBeanInstance(Object beanInstance) {
        this.beanInstance = beanInstance;
    }

    public void setBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }
}
