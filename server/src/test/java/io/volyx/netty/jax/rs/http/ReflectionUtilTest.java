package io.volyx.netty.jax.rs.http;

import io.volyx.netty.jax.rs.http.rest.ReflectionUtil;
import org.junit.Assert;
import org.junit.Test;

public class ReflectionUtilTest {

	@Test
	public void test() {
		Object[][] classes = new Object[][] {
				{byte.class, "1", Byte.class},
				{Byte.class, "1", Byte.class},
				{short.class, "1", Short.class},
				{Short.class, "1", Short.class},
				{int.class, "1", Integer.class},
				{Integer.class, "1", Integer.class},
				{long.class, "1", Long.class},
				{Long.class, "1", Long.class},
				{boolean.class, "true", Boolean.class},
				{Boolean.class, "true", Boolean.class},
		};

		for (Object[] testClazz : classes) {
			final Class result = (Class) testClazz[2];
			Assert.assertEquals(result, ReflectionUtil.castTo((Class) testClazz[0], (String) testClazz[1]).getClass());
		}
	}
}
