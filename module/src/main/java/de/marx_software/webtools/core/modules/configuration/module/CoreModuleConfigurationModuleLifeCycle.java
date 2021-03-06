/**
 * webTools-contentengine
 * Copyright (C) 2016  Thorsten Marx (kontakt@thorstenmarx.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.marx_software.webtools.core.modules.configuration.module;

import com.thorstenmarx.modules.api.ModuleLifeCycleExtension;
import com.thorstenmarx.modules.api.annotation.Extension;
import de.marx_software.webtools.api.CoreModuleContext;
import de.marx_software.webtools.api.configuration.Registry;
import de.marx_software.webtools.core.modules.configuration.RegistryImpl;

/**
 *
 * @author marx
 */
@Extension(ModuleLifeCycleExtension.class)
public class CoreModuleConfigurationModuleLifeCycle extends ModuleLifeCycleExtension {

	public static RegistryImpl registry;

	public CoreModuleContext getCoreContext() {
		return (CoreModuleContext) getContext();
	}

	@Override
	public void activate() {
		if (registry == null) {
			registry = new RegistryImpl(configuration.getDataDir());
			registry.open();
			getContext().serviceRegistry().register(Registry.class, registry);
		}
	}

	@Override
	public void deactivate() {
		getContext().serviceRegistry().unregister(Registry.class, registry);
		registry.close();
		registry = null;
	}

	@Override
	public void init() {

	}

}
