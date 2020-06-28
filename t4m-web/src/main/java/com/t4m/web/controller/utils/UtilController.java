package com.t4m.web.controller.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * Created by Yuxiang Liao on 2020-06-23 20:35.
 */
@RestController
@RequestMapping("/util")
public class UtilController {

	public static final Logger LOGGER = LoggerFactory.getLogger(UtilController.class);

	@GetMapping("/pkg_conflict")
	public String greeting(@RequestParam(name = "className") String className) {
		try {
			String classLocation = null;
			String error = null;

			classLocation = "" + getClassLocation(Class.forName(className));
			if (error == null) {
				LOGGER.info("The class file of [{}] is [{}]", className, classLocation);
			} else {
				LOGGER.info("There exists no class file for [{}]. \r\nError: {}", className, error);
			}
			return classLocation;
		} catch (Exception e) {
			LOGGER.error("Error happen when searching for [{}]", className, e);
		}
		return "Error happen when searching for [" + className + "], more details are in logs/error.log.";
	}

	public static URL getClassLocation(final Class cls) {
		if (cls == null)
			throw new IllegalArgumentException("null input: cls");
		URL result = null;
		final String clsAsResource = cls.getName().replace('.', '/').concat(".class");
		final ProtectionDomain pd = cls.getProtectionDomain();
		// java.lang.Class contract does not specify if 'pd' can ever be null;
		// it is not the case for Sun's implementations, but guard against null
		// just in case:
		if (pd != null) {
			final CodeSource cs = pd.getCodeSource();
			// 'cs' can be null depending on the classloader behavior:
			if (cs != null)
				result = cs.getLocation();
			if (result != null) {
				// Convert a code source location into a full class file location
				// for some common cases:
				if ("file".equals(result.getProtocol())) {
					try {
						if (result.toExternalForm().endsWith(".jar") || result.toExternalForm().endsWith(".zip"))
							result = new URL("jar:".concat(result.toExternalForm()).concat("!/").concat(clsAsResource));
						else if (new File(result.getFile()).isDirectory())
							result = new URL(result, clsAsResource);
					} catch (MalformedURLException ignore) {
					}
				}
			}
		}
		if (result == null) {
			// Try to find 'cls' definition as a resource; this is not
			// document．d to be legal, but Sun's implementations seem to         //allow this:
			final ClassLoader clsLoader = cls.getClassLoader();
			result = clsLoader != null ? clsLoader.getResource(clsAsResource) : ClassLoader.getSystemResource(
					clsAsResource);
		}
		return result;
	}


}
