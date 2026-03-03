package dev.celestial.silly.loaders;

import net.fabricmc.loader.api.Version;

public interface ISillyLoader {
    public boolean isModLoaded(String mod_id);

    public String getModVersion(String mod_id);
}
