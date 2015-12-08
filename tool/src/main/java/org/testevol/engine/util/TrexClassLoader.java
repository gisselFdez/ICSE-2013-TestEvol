package org.testevol.engine.util;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.testevol.engine.TestRunner;

public class TrexClassLoader extends URLClassLoader {
	private static int times =0;
	
	private HashMap<String, Class<?>> loadedClasses;
	
	public TrexClassLoader( URL[] jarURLs ) {
		super( jarURLs );
		loadedClasses = new HashMap<String, Class<?>>();
	}

	public void addJar( URL url ) throws MalformedURLException {
		addURL( url );
	}

	public Class<?> findOrLoadClass( String className ) {	
		try{
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			URL[] urls = ((URLClassLoader) cl).getURLs();
			/*for (URL url: urls) {
			    System.out.println("loaderClass: "+url.getFile());
			}*/
			
			if(className.contains("$")){
				className = className.substring(0,className.lastIndexOf("$"));
			}
			Class<?> cls = loadedClasses.get( className); //classNAme
			if ( cls == null ) {
				String dots = className.replace("\\", ".");
				cls = cl.loadClass( dots);//className
				loadedClasses.put( className, cls );
			}
			return cls;
		}
		catch(ClassNotFoundException e){
			return null;
		}		
	}

	
	
	/**
	 * Helps to retrive all the class names
	 * @param url
	 * @return
	 */
	public Set<String> getAllClassNames( URL url ) {
		Set<String> classNamesSet = new HashSet<String>();
		
		try {
			//load URL
			addURL(url);		
			JarURLConnection conn = (JarURLConnection) url.openConnection();
			JarFile jarFile = conn.getJarFile();
			
			Enumeration<JarEntry> entries = jarFile.entries();
			while(entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if(entryName.endsWith(".class")) {
					String className = entryName.substring(0, entryName.length()- 6).replace('/', '.');
					classNamesSet.add(className);
				}
			}		
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
		return classNamesSet;
	}
	
	public Collection<String> getAllTestcases( String className ) throws ClassNotFoundException {
		ArrayList<String> testMethods = new ArrayList<String>();
		
		Class<?> cls = findOrLoadClass( className );
		
		for ( Method method : cls.getMethods() ) {
			if (isTestMethod(method)) {
				testMethods.add( Utils.getCanonicalMethodSignature( method ) );
			}
		}
		return testMethods;
	}
	
	public boolean isAnnotationPresent(Annotation[] annotations, String annotationName){
		for(Annotation annotation:annotations){
			if(annotation.annotationType().getName().equals(annotationName)){
				return true;
			}		
		}
		return false;
	}
	
	public boolean isAnnotationPresent(Class clazz, String annotationName){
		return isAnnotationPresent(clazz.getAnnotations(), annotationName);
	}
	
	public boolean isAnnotationPresent(Method method, String annotationName){
		return isAnnotationPresent(method.getAnnotations(), annotationName);
	}
	
	public boolean isIgnoredClass(Class<?> cls) throws ClassNotFoundException {
		return isAnnotationPresent(cls, TestRunner.IgnoreAnnotation);
	}
	
	
	
	public boolean isTestClass( String className ) throws ClassNotFoundException {

		Class<?> cls = findOrLoadClass( className );
		
		if(isTestClass("org.junit.runners.JUnit4", className)){
			return true;
		}
		if(isTestClass("org.junit.internal.runners.JUnit38ClassRunner", className)){
			return true;
		}
		for(Method m:cls.getMethods()){
			if(isTestMethod(m)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isTestClass(String JUnitRunner, String className){
		try {
			Class<?> cls = findOrLoadClass( className );
			Class<?> clsJUnit = findOrLoadClass(JUnitRunner);
			Constructor c = clsJUnit.getConstructor(cls.getClass());
			Object o = c.newInstance(cls);
			int testCount = (Integer) o.getClass().getMethod("testCount").invoke(o);
			return testCount > 0;
		} catch (Exception e) {
			//e.printStackTrace();
			//It is not a test case!
		}
		return false;
	}

	public boolean isTestMethod(Method method) {
		return (method.getName().startsWith("test") || isAnnotationPresent(
				method, TestRunner.TestAnnotation))
				&& !isAnnotationPresent(method, TestRunner.IgnoreAnnotation);
	}

	public Annotation getAnnotation(Method method, String annotationName) {
		
		return getAnnotation(method.getAnnotations(), annotationName);
	}
	
	public Annotation getAnnotation(Class clazz, String annotationName) {
		
		return getAnnotation(clazz.getAnnotations(), annotationName);
	}
	
	public Annotation getAnnotation(Annotation[] annotations, String annotationName){
		for(Annotation annotation:annotations){
			if(annotation.annotationType().getName().equals(annotationName)){
				return annotation;
			}
		}
		return null;
	}
}
