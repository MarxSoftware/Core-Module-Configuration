
package com.thorstenmarx.webtools.core.modules.configuration;

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

import com.thorstenmarx.webtools.api.cluster.Cluster;
import com.thorstenmarx.webtools.api.configuration.Configuration;
import com.thorstenmarx.webtools.api.configuration.Registry;
import com.thorstenmarx.webtools.core.modules.configuration.store.H2DB;
import java.io.File;

/**
 *
 * @author marx
 */
public class RegistryImpl implements Registry {
	
	private final File path;
	
	private final H2DB db;
	
	public RegistryImpl (final File path) {
		this.path = path;
		db = new H2DB(path);
	}
	
	
	public void open () {
		db.open();	
	}
	
	@Override
	public void close () {
		db.close();
	}
	
	@Override
	public Configuration getConfiguration (final String namespace) {
		return new ConfigurationImpl(namespace, db);
	}
}
