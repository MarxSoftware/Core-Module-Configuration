
package com.thorstenmarx.webtools.configuration;

/*-
 * #%L
 * webtools-configuration
 * %%
 * Copyright (C) 2016 - 2018 Thorsten Marx
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.thorstenmarx.webtools.core.modules.configuration.RegistryImpl;
import com.thorstenmarx.webtools.api.configuration.Configuration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class RegistryTest {

	RegistryImpl registry;
	AtomicInteger counter = new AtomicInteger();

	@BeforeClass
	public void setup() {
		registry = new RegistryImpl(new File("./target/registry-" + System.currentTimeMillis() + "/"));
		registry.open();
	}

	@AfterClass
	public void down() {
		registry.close();
	}

	
	@Test()
	public void testString() {
		Configuration config = registry.getConfiguration("test" + counter.getAndIncrement());
		Configuration config2 = registry.getConfiguration("test" + counter.getAndIncrement());

		Assertions.assertThat(config.set("name", "thorsten")).isTrue();

		Assertions.assertThat(config.getString("name")).isPresent();
		Assertions.assertThat(config.getString("name").get()).isEqualTo("thorsten");

		Assertions.assertThat(config2.getString("name")).isNotPresent();
	}
	@Test()
	public void testBoolean() {
		Configuration config = registry.getConfiguration("test" + counter.getAndIncrement());
		Configuration config2 = registry.getConfiguration("test" + counter.getAndIncrement());

		Assertions.assertThat(config.set("name", Boolean.TRUE)).isTrue();

		Assertions.assertThat(config.getBoolean("name")).isPresent();
		Assertions.assertThat(config.getBoolean("name").get()).isTrue();

		Assertions.assertThat(config2.getString("name")).isNotPresent();
	}

	@Test()
	public void testList() {
		Configuration config = registry.getConfiguration("test" + counter.getAndIncrement());
		Configuration config2 = registry.getConfiguration("test" + counter.getAndIncrement());

		List<String> items = new ArrayList<>();
		items.add("eins");
		items.add("zwei");
		
		Assertions.assertThat(config.set("items", items)).isTrue();

		Assertions.assertThat(config.getList("items", String.class)).isPresent();
		Assertions.assertThat(config.getList("items", String.class).get()).isNotEmpty();
		Assertions.assertThat(config.getList("items", String.class).get()).containsExactlyElementsOf(items);

		Assertions.assertThat(config2.getString("name")).isNotPresent();
	}
}
