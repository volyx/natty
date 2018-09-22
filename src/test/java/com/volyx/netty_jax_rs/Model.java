package com.volyx.netty_jax_rs;

import java.util.Objects;

public class Model {
	public String test;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Model model = (Model) o;
		return Objects.equals(test, model.test);
	}

	@Override
	public int hashCode() {
		return Objects.hash(test);
	}
}
