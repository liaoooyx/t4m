package com.t4m.conf;

/**
 * Created by Yuxiang Liao on 2020-08-08 09:18.
 */
public class SystemVariableNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1767630439501850343L;

	public SystemVariableNotFoundException(String message) {
		super(message);
	}
}
