package graph.algorithm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AlgorithmExecutor {


	public static ExecuteResult execute(Object algorithm, String methodName, Object...params){

		Long duration = 0L;
		Object result = null;

		Class<?> clazz = algorithm.getClass();
		Method method;

		try {
			long start;
			if (params.length > 0){
				Class<?>[] paramsArray = new Class<?>[params.length];
				for (int i = 0; i < params.length; i++)
					paramsArray[i] = params[i].getClass();

				method = clazz.getDeclaredMethod(methodName, paramsArray);
				start = System.currentTimeMillis();
				if (method.getReturnType().equals(Void.TYPE))
					method.invoke(algorithm, params);
				else
					result = method.invoke(algorithm, params);
			}
			else{
				method = clazz.getDeclaredMethod(methodName);
				start = System.currentTimeMillis();
				if (method.getReturnType().equals(Void.TYPE))
					method.invoke(algorithm, params);
				else
					result = method.invoke(algorithm);
			}

			long end = System.currentTimeMillis();
			duration = end - start;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}


		ExecuteResult executeResult = new ExecuteResult(duration, result);
		return executeResult;

	}


}
