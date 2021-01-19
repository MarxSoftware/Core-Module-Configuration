package de.marx_software.webtools.core.modules.configuration;

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

import de.marx_software.webtools.api.configuration.Configuration;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import de.marx_software.webtools.core.modules.configuration.store.DB;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author marx
 */
public class ConfigurationImpl implements Configuration {
	
	private final String namespace;
	
	private Gson gson = new Gson();
	private final DB db;
	
	protected ConfigurationImpl (final String namespace, final DB db) {
		this.namespace = namespace;
		this.db = db;
	}
	
	private <T> Optional<T> get (final String key, final Class<T> type) {
		final String content = db.get(namespace, key);
		if (Strings.isNullOrEmpty(content)) {
			return Optional.empty();
		}
		T value = gson.fromJson(content, type);
		
		return Optional.ofNullable(value);
	}
	private boolean internal_set (final String key, final Object value) {
		final String content = gson.toJson(value);
		return db.add(namespace, key, content);
	}
	
	@Override
	public Optional<Boolean> getBoolean (final String key) {
		return get(key, Boolean.class);
	}
	@Override
	public Optional<String> getString (final String key) {
		return get(key, String.class);
	}
	@Override
	public Optional<Integer> getInt (final String key) {
		return get(key, Integer.class);
	}
	@Override
	public Optional<Float> getFloat (final String key) {
		return get(key, Float.class);
	}
	@Override
	public Optional<Double> getDouble (final String key) {
		return get(key, Double.class);
	}
	@Override
	public Optional<Short> getShort (final String key) {
		return get(key, Short.class);
	}
	@Override
	public <T> Optional<List<T>> getList (final String key, final Class<T> type) {
		Optional<List> value = get(key, List.class);
		if (!value.isPresent()) {
			return Optional.empty();
		}
		return Optional.ofNullable(new ArrayList<>(value.get()));
	}
	
	@Override
	public boolean set (final String key, final List<?> value) {
		return internal_set(key, value);
	}
	@Override
	public boolean set (final String key, final Boolean value) {
		return internal_set(key, value);
	}
	@Override
	public boolean set (final String key, final String value) {
		return internal_set(key, value);
	}
	@Override
	public boolean set (final String key, final Integer value) {
		return internal_set(key, value);
	}
	@Override
	public boolean set (final String key, final Float value) {
		return internal_set(key, value);
	}
	@Override
	public boolean set (final String key, final Double value) {
		return internal_set(key, value);
	}
	@Override
	public boolean set (final String key, final Short value) {
		return internal_set(key, value);
	}
}
