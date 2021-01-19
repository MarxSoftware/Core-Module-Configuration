
package de.marx_software.webtools.core.modules.configuration.store;

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
/**
 *
 * @author thmarx
 */
public interface DB {

	boolean add(final String namespace, final String key, final String content);

	void clear(final String namespace);
	
	void clearAll();

	int count(final String namespace);

	/**
	 * Delete an entity and all attributes;
	 *
	 * @param namespace
	 */
	void delete(final String namespace);

	String get(final String namespace, final String key);
	
}
