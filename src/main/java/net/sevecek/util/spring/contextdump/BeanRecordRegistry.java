package net.sevecek.util.spring.contextdump;

import java.io.*;
import java.util.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;

public class BeanRecordRegistry {

    private Map<String, BeanRecord> beansToDump = new LinkedHashMap<>();
    private ConfigurableListableBeanFactory beanFactory;

    public void setBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void add(String beanName, Object bean) {
        BeanRecord beanRecord = new BeanRecord(beanName, bean);
        add(beanRecord);
    }

    private void addNonstandard(String beanName, Object bean) {
        BeanRecord beanRecord = new BeanRecord(beanName, bean, true);
        add(beanRecord);
    }

    private void add(BeanRecord beanRecord) {
        if (beanRecord.getName() == null || beanRecord.getName().isEmpty()) {
            throw new IllegalArgumentException("Empty bean name for bean " + beanRecord.getBeanInstance().getClass().getName() + ": " + beanRecord.getBeanInstance());
        }
        if (beansToDump.containsKey(beanRecord.getName())) {
            return;
//            throw new IllegalArgumentException("Bean name already present " + beanRecord.getName());
        }
        for (BeanRecord beanRecord2 : beansToDump.values()) {
            if (beanRecord2.getBeanInstance() == beanRecord.getBeanInstance()) {
                throw new IllegalArgumentException("Bean instance already present in a different BeanRecord " + beanRecord.getName() + " and " + beanRecord2.getName());
            }
        }
        beansToDump.put(beanRecord.getName(), beanRecord);
    }

    public void initializing(String beanName, Object bean) {
        BeanRecord beanRecord = beansToDump.get(beanName);
        if (beanRecord == null) {
            throw new IllegalArgumentException("Bean name not present " + beanName);
        }
        beanRecord.setRuntimeClass(bean.getClass());
        beanRecord.setBeanInstance(bean);
    }

    private void addNotPostProcessedTopLevelBeans() {
        Map<String, Object> beans = beanFactory.getBeansOfType(Object.class);
        for (Map.Entry<String, Object> beanEntry : beans.entrySet()) {
            BeanRecord existingBeanRecord = beansToDump.get(beanEntry.getKey());
            if (existingBeanRecord != null) {
                if (existingBeanRecord.getBeanInstance() != beanEntry.getValue()) {
                    throw new IllegalStateException("Two bean entries with different instances " + existingBeanRecord.getBeanInstance() + " vs. " + beanEntry.getValue());
                }
            } else {
                // Bean was not received by the dumping BeanPostProcessor
                addNonstandard(beanEntry.getKey(), beanEntry.getValue());
            }
        }
    }

    private void scanForBeanDefinitions() {
        for (Map.Entry<String, BeanRecord> stringBeanRecordEntry : beansToDump.entrySet()) {
            String name = stringBeanRecordEntry.getKey();
            try {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
                BeanRecord beanRecord = stringBeanRecordEntry.getValue();
                beanRecord.setBeanDefinition(beanDefinition);
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
    }

    private void scanTopLevelBeanDefinitionsForInnerBeans() {
        // TODO
    }

    public void dumpBeans(PrintWriter out) {
        addNotPostProcessedTopLevelBeans();
        scanForBeanDefinitions();
        scanTopLevelBeanDefinitionsForInnerBeans();

        for (BeanRecord beanRecord : beansToDump.values()) {
            out.println(dumpBeanRecord(beanRecord));
        }
    }

    public String dumpBeanRecord(BeanRecord beanRecord) {
        String text = "\n<bean";
        if (!beanRecord.isAnonymous()) {
            // Not an anonymous bean definition
            text += " id=\"" + beanRecord.getName() + "\"\n     ";
        }
        text += " class=\"" + beanRecord.getDefinitionClass().getName() + "\"";
        if (beanRecord.getDefinitionClass() != beanRecord.getRuntimeClass() && beanRecord.getRuntimeClass() != null) {
            text += "\n      runtimeClass=\"" + beanRecord.getRuntimeClass().getName() + "\"";
        }
        text += "\n      instance=\"" + System.identityHashCode(beanRecord.getBeanInstance()) + "\"";
        text += "/>";

        BeanDefinition beanDefinition = beanRecord.getBeanDefinition();
        text += "\n      -> Definition: " + beanDefinition;
        return text;
    }
}
