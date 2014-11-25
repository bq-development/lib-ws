package com.bqreaders.silkroad.common.util;

import java.lang.annotation.ElementType;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;

public final class ConstraintViolationImplBuilder<T> {

	private String message;
	private String messageTemplate;
	private T rootBean;
	private Class<T> rootBeanClass;
	private Object leafBean;
	private Path propertyPath;
	private Object invalidValue;
	private ElementType elementType;
	private ConstraintDescriptor<?> constraintDescriptor;

	public ConstraintViolation<T> build() {
		return ConstraintViolationImpl.<T> forBeanValidation(message, messageTemplate, rootBeanClass, rootBean,
				leafBean, invalidValue, propertyPath, constraintDescriptor, elementType);
	}

	public ConstraintViolationImplBuilder<T> setConstraintDescriptor(ConstraintDescriptor<?> constraintDescriptor) {
		this.constraintDescriptor = constraintDescriptor;
		return this;
	}

	public ConstraintViolationImplBuilder<T> setElementType(ElementType elementType) {
		this.elementType = elementType;
		return this;
	}

	public ConstraintViolationImplBuilder<T> setInvalidValue(Object invalidValue) {
		this.invalidValue = invalidValue;
		return this;
	}

	public ConstraintViolationImplBuilder<T> setLeafBean(Object leafBean) {
		this.leafBean = leafBean;
		return this;
	}

	public ConstraintViolationImplBuilder<T> setMessage(String message) {
		this.message = message;
		return this;
	}

	public ConstraintViolationImplBuilder<T> setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
		return this;
	}

	public ConstraintViolationImplBuilder<T> setPropertyPath(Path propertyPath) {
		this.propertyPath = propertyPath;
		return this;
	}

	public ConstraintViolationImplBuilder<T> setRootBean(T rootBean) {
		this.rootBean = rootBean;
		return this;
	}

	public ConstraintViolationImplBuilder<T> setRootBeanClass(Class<T> rootBeanClass) {
		this.rootBeanClass = rootBeanClass;
		return this;
	}
}