package org.devinprogress.YAIF.Transformer;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.devinprogress.YAIF.YetAnotherInputFix;

import java.util.Map;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class Loader implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] {ASMTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        YetAnotherInputFix.ObfuscatedEnv=(Boolean)data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return AccessTransformer.class.getName();
    }
}
