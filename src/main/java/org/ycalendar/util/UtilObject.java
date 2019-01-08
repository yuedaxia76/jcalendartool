package org.ycalendar.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilObject {

    private static Logger log = LoggerFactory.getLogger(UtilObject.class);

    /**
     * 一个空的class数组
     */
    public static final Class<?>[] emptyClassArray = new Class[0];
    /**
     * 一个空的object数组
     */
    public static final Object[] emptyObjectArray = new Object[0];

    private static void setMethodAccessible(final Method method) {
        try {

            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

        } catch (SecurityException se) {

            log.warn( "Cannot setAccessible on method. Therefore cannot use jvm access bug workaround.", se);
        }
    }

    public static Class<?> getPrimitiveWrapper(Class<?> primitiveType) {
        // does anyone know a better strategy than comparing names?
        if (boolean.class.equals(primitiveType)) {
            return Boolean.class;
        } else if (float.class.equals(primitiveType)) {
            return Float.class;
        } else if (long.class.equals(primitiveType)) {
            return Long.class;
        } else if (int.class.equals(primitiveType)) {
            return Integer.class;
        } else if (short.class.equals(primitiveType)) {
            return Short.class;
        } else if (byte.class.equals(primitiveType)) {
            return Byte.class;
        } else if (double.class.equals(primitiveType)) {
            return Double.class;
        } else if (char.class.equals(primitiveType)) {
            return Character.class;
        } else {

            return null;
        }
    }

    public static final boolean isAssignmentCompatible(Class<?> parameterType, Class<?> parameterization) {
        // try plain assignment
        if (parameterType.isAssignableFrom(parameterization)) {
            return true;
        }

        if (parameterType.isPrimitive()) {
            Class<?> parameterWrapperClazz = getPrimitiveWrapper(parameterType);
            if (parameterWrapperClazz != null) {
                return parameterWrapperClazz.equals(parameterization);
            }
        }

        return false;
    }

    /**
     * <p>
     * 找到符合给定名字并有合适参数的可访问的方法 合适的方法是指参数可以从参数列表中分配. 也就是说按给定名字找到的方法可以使用给定的参数
     * </p>
     *
     * <p>
     * 该方法遍历所有使用该名字的方法，并调用第一符合要求的方法.
     * </p>
     *
     * <p>
     * 该方法被
     * {@link #runMethod(Object object,String methodName,Object [] args,Class[] parameterTypes)}使用.
     *
     * <p>
     * 如果参数是基本数据类型则它的包装类作为匹配类型处理,比如一个<code>Boolean</code>类
     * 匹配<code>boolean</code>类型的参数
     *
     * @param clazz 从该类中找到方法
     * @param methodName 通过该名字找到方法
     * @param parameterTypes 找到方法合适的参数
     */
    private static final Method getMatchingAccessibleMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {

        try {
            Method method = clazz.getMethod(methodName, parameterTypes);

            setMethodAccessible(method);
            return method;

        } catch (NoSuchMethodException e) {
            /* SWALLOW */
        }

        // search through all methods
        final int paramSize = parameterTypes.length;
        Method[] methods = clazz.getMethods();
        for (int i = 0, size = methods.length; i < size; i++) {
            if (methods[i].getName().equals(methodName)) {

                log.trace("Found matching name:{}", methods[i]);

                // compare parameters
                final int methodParamSize = methods[i].getParameterCount();
                if (methodParamSize == paramSize) {
                    Class<?>[] methodsParams = methods[i].getParameterTypes();
                    boolean match = true;
                    for (int n = 0; n < methodParamSize; n++) {
                        if (log.isTraceEnabled()) {
                            log.trace("Param=" + parameterTypes[n].getName());
                            log.trace("Method=" + methodsParams[n].getName());
                        }
                        if (!isAssignmentCompatible(methodsParams[n], parameterTypes[n])) {
                            if (log.isTraceEnabled()) {
                                log.trace(methodsParams[n] + " is not assignable from " + parameterTypes[n]);
                            }
                            match = false;
                            break;
                        }
                    }

                    if (match) {
                        // get accessible version of method
                        Method method = getAccessibleMethod(methods[i]);
                        if (method != null) {
                            if (log.isTraceEnabled()) {
                                log.trace(method + " accessible version of " + methods[i]);
                            }

                            return method;
                        }

                        log.trace("Couldn't find accessible method.");
                    }
                }
            }
        }

        log.info("No match found. for {} name {} ", clazz, methodName);

        return null;
    }

    public static final Method getAccessibleMethod(Method method) {

        if (method == null) {
            return null;
        }

        // If the requested method is not public we cannot call it
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }

        // If the declaring class is public, we are done
        Class<?> clazz = method.getDeclaringClass();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return method;
        }

        // Check the implemented interfaces and subinterfaces
        method = getAccessibleMethodFromInterfaceNest(clazz, method.getName(), method.getParameterTypes());
        setMethodAccessible(method);
        return method;

    }

    /**
     * <p>
     * 返回可使用反射访问的方法，扫描所有接口子接口，找到特定的方法 如果没有找到该方法就返回<code>null</code>.
     * </p>
     *
     * <p>
     * 该方法没有做为私有方法，因为有些类没必要调用更高级别的方法实现.
     * </p>
     *
     * @param clazz 接口要检查的父类
     * @param methodName 要调用的方法的名称
     * @param parameterTypes 参数类型
     */
    private static final Method getAccessibleMethodFromInterfaceNest(Class<?> clazz, String methodName, Class<?> parameterTypes[]) {

        Method method = null;

        // Search up the superclass chain
        for (; clazz != null; clazz = clazz.getSuperclass()) {

            // Check the implemented interfaces of the parent class
            Class<?> interfaces[] = clazz.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {

                // Is this interface public?
                if (!Modifier.isPublic(interfaces[i].getModifiers())) {
                    continue;
                }

                // Does the method exist on this interface?
                try {
                    method = interfaces[i].getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                    ;
                }
                if (method != null) {
                    break;
                }

                // Recursively check our parent interfaces
                method = getAccessibleMethodFromInterfaceNest(interfaces[i], methodName, parameterTypes);
                if (method != null) {
                    break;
                }

            }

        }

        // If we found a method return it
        if (method != null) {
            return (method);
        }

        // We did not find anything
        return (null);

    }

    public static final Object runMethod(Object obj, String methodName, Object[] args, Class<?>[] parameterTypes) {
        assert (methodName != null);
        try {

            if (parameterTypes == null) {
                parameterTypes = emptyClassArray;
            }
            if (args == null) {
                args = emptyObjectArray;
            }
            Method method = getMatchingAccessibleMethod(obj.getClass(), methodName, parameterTypes);
            if (method == null) {
                throw new NoSuchMethodException("No such accessible method: " + methodName + "() on object: " + obj.getClass().getName());
            }

            return method.invoke(obj, args);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反射调用对象方法
     *
     * @param obj 调用对象
     * @param methodName 方法名称
     * @param args 参数，
     * @return
     */
    public static final Object runMethod(final Object obj, final String methodName, final Object[] args) {
        assert (methodName != null);
        try {
            Class<?>[] parameterTypes;
            if (args == null) {
                parameterTypes = emptyClassArray;

                return runMethod(obj, methodName, args, parameterTypes);
            } else {
                parameterTypes = new Class[args.length];

                boolean paramNull = false;
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (args[i] == null) {
                        paramNull = true;
                        break;
                    }
                    parameterTypes[i] = args[i].getClass();
                }
                if (paramNull) {
                    Method m = getMethodByName(obj.getClass(), methodName, args.length);
                    if (m == null) {

                        throw new RuntimeException("No or more than one method: " + methodName + "() on object: " + obj.getClass().getName());
                    } else {
                        return runMethod(m, obj, args);
                    }
                } else {
                    return runMethod(obj, methodName, args, parameterTypes);
                }
            }

        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Object runMethod(final Method method, final Object target, final Object[] args) {
        setMethodAccessible(method);
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    private static Method getMethodByName(final Class<?> clazz, final String methodName, final int paramsSize) {
        Method[] ms = clazz.getMethods();
        Method result = null;
        boolean more = false;
        for (Method m : ms) {
            if (m.getName().equals(methodName) && m.getParameterCount() == paramsSize) {
                if (result == null) {
                    result = m;
                } else {
                    more = true;
                    break;
                }

            }
        }
        if (more) {
            log.warn("obj :{} has more than one method {} " , clazz ,methodName);
            return null;
        }
        return result;
    }

    public static final PropertyDescriptor[] getPropertyDescriptorsNoCache(Class<?> bean) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(bean);
        } catch (IntrospectionException e) {
            return (new PropertyDescriptor[0]);
        }
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[0];
        }
        return descriptors;
    }

    public static final PropertyDescriptor getPropertyDescriptor(final Class<?> bean, final String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        if ((bean == null) || (name == null)) {
            log.warn("bean is null");
            return null;
        }
        PropertyDescriptor descriptors[] = getPropertyDescriptorsNoCache(bean);
        if (descriptors == null) {
            return null;
        }
        for (int i = 0; i < descriptors.length; i++) {
            if (name.equals(descriptors[i].getName())) {
                return descriptors[i];
            }
        }
        return null;

    }

    public static final Method getBeanWriteMethod(Class<?> clazz, String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PropertyDescriptor descriptor = getPropertyDescriptor(clazz, name);
        if (descriptor == null) {
            throw new NoSuchMethodException("class :" + clazz.getName() + "  Unknown property '" + name + "'");
        }
        Method writeMethod = descriptor.getWriteMethod();
        if (writeMethod == null) {
            throw new NoSuchMethodException("Property '" + name + "' has no setter method in " + clazz.getName());
        }

        setMethodAccessible(writeMethod);
        return writeMethod;
    }

    public static final void setBeanValue(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Method writeMethod = getBeanWriteMethod(bean.getClass(), name);
        Object[] values = {value};
        // values[0] = value;
        writeMethod.invoke(bean, values);
    }
}
