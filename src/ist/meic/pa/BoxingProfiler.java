package ist.meic.pa;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.reflect.*;

public class BoxingProfiler {

	public static void main(String[] args) throws NotFoundException, CannotCompileException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{

		if (args.length < 1) {
			System.err.println("Usage: java ist.meic.pa.BoxingProfiler <class to profile> [args]");
			System.exit(1);
		} else {

			ClassPool pool = ClassPool.getDefault();
			CtClass ctClass = pool.get(args[0]);
			CtMethod[] methods = ctClass.getDeclaredMethods();
			System.out.println("Got " + methods.length + " methods.");

			for (CtMethod cm : methods) {
				cm.instrument(new ExprEditor() {
					public void edit(MethodCall m) throws CannotCompileException {
						String s = cm.getLongName();
						//System.out.println("Methodname: " + m.getMethodName());
						//System.out.println("Classname: " + m.getClassName());
						/*if (m.getClassName().equals("java.lang.Integer") && m.getMethodName().equals("valueOf")) {
						
							m.replace("{System.out.println(\"before\");" + "$_ = $proceed($$);"
									+ "System.out.println(\"after\");}");
						}*/
						
						if (m.getMethodName().equals("valueOf") &&
								(m.getClassName().equals("java.lang.Byte") ||
								m.getClassName().equals("java.lang.Short") ||
								m.getClassName().equals("java.lang.Integer") ||
								m.getClassName().equals("java.lang.Long") ||
								m.getClassName().equals("java.lang.Float") ||
								m.getClassName().equals("java.lang.Double") ||
								m.getClassName().equals("java.lang.Character") ||
								m.getClassName().equals("java.lang.Boolean"))) {
						
							sendBoxMessage(cm, m);
			
						}
						
						else if (m.getMethodName().equals("byteValue") &&
								m.getMethodName().equals("shortValue") &&
								m.getMethodName().equals("intValue") &&
								m.getMethodName().equals("longValue") &&
								m.getMethodName().equals("floatValue") &&
								m.getMethodName().equals("doubleValue") &&
								m.getMethodName().equals("charValue") &&
								m.getMethodName().equals("booleanValue")){
							
							sendUnboxMessage(cm, m);
						}
								
					}
				});
			}

			Class<?> rtClass = ctClass.toClass();
			Method main = rtClass.getMethod("main", args.getClass());
			String[] restArgs = new String[args.length - 1];
			System.arraycopy(args, 1, restArgs, 0, restArgs.length);
			System.err.println("Transfer control");
			main.invoke(null, new Object[] { restArgs });
		}
	}
	
	static void sendUnboxMessage(CtMethod cm, MethodCall m){
		
		System.err.println(cm.getLongName() + " unboxed a " + m.getClassName());
		
	}
	
	static void sendBoxMessage(CtMethod cm, MethodCall m){
		
		System.err.println(cm.getLongName() + " boxed a " + m.getClassName());
		
	}

	/*static void profiler(CtClass ctClass, CtMethod ctMethod) throws CannotCompileException {
		String name = ctMethod.getName();
		ctMethod.setName(name + "$original");
		ctMethod = CtNewMethod.copy(ctMethod, name, ctClass, null);
		ctMethod.setBody("{System.err.println(\"Entrei no método\"); return " + name + "$original($$);}");
		ctClass.addMethod(ctMethod);
	}*/
}
