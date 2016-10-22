package com.mymobkit.util.txn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.objectify.TxnType;

/**
 * Annotation which works with the TransactInterceptor to provide EJB-style behavior for methods.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Transact {
	TxnType value();
}