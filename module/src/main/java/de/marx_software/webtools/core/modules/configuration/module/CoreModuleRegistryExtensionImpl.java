package de.marx_software.webtools.core.modules.configuration.module;

import com.thorstenmarx.modules.api.annotation.Extension;
import de.marx_software.webtools.api.configuration.Registry;
import de.marx_software.webtools.api.extensions.core.CoreRegistryExtension;
import java.util.Optional;

/**
 *
 * @author marx
 */
@Extension(CoreRegistryExtension.class)
public class CoreModuleRegistryExtensionImpl extends CoreRegistryExtension {

	@Override
	public String getName() {
		return "CoreModule Configuration";
	}

	@Override
	public Registry getRegistry() {
		return CoreModuleConfigurationModuleLifeCycle.registry;
	}

	@Override
	public void init() {
	}
	
}
