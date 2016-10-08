package zhuboss.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类处理Util
 * 
 * @author KevinZhu
 *
 */

public class ClassUtil {

	protected static Logger log = LoggerFactory.getLogger(ClassUtil.class);

	/**
	 * 获得class所有的属性
	 * 
	 * @param classObj
	 * @author KevinZhu
	 * @return
	 */
	public static Field[] getClassAttributes(Class classObj) {
		Field[] selfFields = classObj.getDeclaredFields();
		Field[] parentFields = null;
		Class superClass = classObj.getSuperclass();
		if (superClass != null) {
			parentFields = superClass.getDeclaredFields();
		}
		List fieldList = new ArrayList();
		for (int i = 0; selfFields != null && i < selfFields.length; i++) {
			fieldList.add(selfFields[i]);
		}
		for (int i = 0; parentFields != null && i < parentFields.length; i++) {
			fieldList.add(parentFields[i]);
		}

		return (Field[]) fieldList.toArray(new Field[] {});
	}

	/**
	 * 判断所有属性值是否为null，为null则附值为""
	 * 只处理了String 和 BigDecimal
	 * @param object
	 *            : object
	 * @author KevinZhu
	 * @return
	 */
	public static void converAllNullValueField(Object object) {
		try {
			Field[] fs = getClassAttributes(object.getClass());
			for (int i = 0; i < fs.length; i++) {
				Field field = fs[i];
				if (field == null) {
					continue;
				}
				// System.out.println("Name:"+field.getName());
				if (field.getName().equals("serialVersionUID"))
					continue;
				String type = field
						.getType()
						.toString()
						.substring(
								field.getType().toString()
										.lastIndexOf(".") + 1,
								field.getType().toString().length());
				if ("String".equals(type)) {
					try {
						if (checkFieldValueIsNull(object, field)) {
//							System.out.println("Name:" + field.getName()
//									+ " is null");
							setFieldValueWithSetter(object, field, "");
						} else {
//							System.out.println("Name:" + field.getName()
//									+ " is not null");
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}else if("BigDecimal".equals(type)){
					try {
						if (checkFieldValueIsNull(object, field)) {
//							System.out.println("Name:" + field.getName()
//									+ " is null");
							setFieldValueWithSetter(object, field, new BigDecimal(0));
						} else {
//							System.out.println("Name:" + field.getName()
//									+ " is not null");
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				

			}
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

//	public static void main(String[] args) {
//		BalanceBean bean = new BalanceBean();
//		try {
//			converAllNullValueField(bean);
//			converAllNullValueField(bean);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}

	/**
	 * 判断某一属性值为null
	 * 
	 * @param object
	 *            : object
	 * @param field
	 *            : field
	 * @author KevinZhu
	 * @return
	 */
	public static boolean checkFieldValueIsNull(Object object, Field field) {
		if (field == null) {
			return false;
		}
		if (ClassUtil.getFieldValueWithGetter(object, field) == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get 值
	 * 
	 * @param obj
	 * @param field
	 * @author KevinZhu
	 * @return
	 */
	public static Object getFieldValueWithGetter(Object obj, Field field) {
		try {
			String fieldName = field.getName();
			String getterMethod = "get"
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1, fieldName.length());

			Method method = getMethod(obj.getClass(), getterMethod, null);
			if (method != null) {
				return method.invoke(obj, null);
			} else {
				log.warn(fieldName + " has no getter method : " + getterMethod
						+ "! ");
				return null;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	/**
	 * Set 值
	 * 
	 * @param obj
	 * @param field
	 * @author KevinZhu
	 * @return
	 */
	public static void setFieldValueWithSetter(Object obj, Field field,
			Object value) {
		try {
			String fieldName = field.getName();
			String setterMethod = "set"
					+ fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1, fieldName.length());

			Method method = getMethod(obj.getClass(), setterMethod,
					new Class[] { value.getClass() });
			if (method != null) {
				method.invoke(obj, new Object[] { value });
			} else {
				// log.warn(fieldName+" has no setter method : "+setterMethod+"! ");
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 获得get方法
	 * 
	 * @param classObj
	 * @param methodName
	 * @param paramTypes
	 * @author KevinZhu
	 * @return
	 */
	public static Method getMethod(Class classObj, String methodName,
			Class[] paramTypes) {
		try {
			return classObj.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
			// log.warn(e.toString());
		}
		return null;
	}
}
