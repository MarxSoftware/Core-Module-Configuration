
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
import com.thorstenmarx.webtools.core.modules.configuration.store.ClusterDB;
import com.thorstenmarx.webtools.core.modules.configuration.store.H2DB;
import java.io.File;

/**
 *
 * @author marx
 */
public class RegistryImpl implements Registry {
	
	private final File path;
	
	private final H2DB db;
	private ClusterDB cluster_db;
	
	private final Cluster cluster;
	
	public RegistryImpl (final File path) {
		this(path, null);
	}
	public RegistryImpl (final File path, final Cluster cluster) {
		this.path = path;
		this.cluster = cluster;
		db = new H2DB(path);
	}
	
	private boolean isCluster () {
		return cluster != null;
	}
	
	public void open () {
		db.open();
		
		if (isCluster()) {
			cluster_db = new ClusterDB(db, cluster);
		}
	}
	
	@Override
	public void close () {
		db.close();
		
		if (isCluster()) {
			cluster_db.close();
		}
	}
	
	@Override
	public Configuration getConfiguration (final String namespace) {
		if (isCluster()) {
			return new ConfigurationImpl(namespace, cluster_db);
		} else {
			return new ConfigurationImpl(namespace, db);
		}
	}
}