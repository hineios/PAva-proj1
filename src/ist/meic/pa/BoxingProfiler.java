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
			System.out.println("Got " + methods.length + " mothods.");

			for (CtMethod cm : methods) {
				cm.instrument(new ExprEditor() {
					public void edit(MethodCall m) throws CannotCompileException {
						System.out.println(m.getMethodName());
						if (m.getClassName().equals("java.lang.Integer") && m.getMethodName().equals("valueOf")) {
							m.replace("{System.out.println(\"before\");" + "$_ = $proceed($$);"
									+ "System.out.println(\"after\");}");
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

	/*static void profiler(CtClass ctClass, CtMethod ctMethod) throws CannotCompileException {
		String name = ctMethod.getName();
		ctMethod.setName(name + "$original");
		ctMethod = CtNewMethod.copy(ctMethod, name, ctClass, null);
		ctMethod.setBody("{System.err.println(\"Entrei no método\"); return " + name + "$original($$);}");
		ctClass.addMethod(ctMethod);
	}*/
}
