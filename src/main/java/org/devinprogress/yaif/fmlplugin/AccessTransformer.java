package org.devinprogress.yaif.fmlplugin;

import java.io.IOException;

public class AccessTransformer extends net.minecraftforge.fml.common.asm.transformers.AccessTransformer {
    public AccessTransformer() throws IOException {
        super("inputfix_at.cfg");
    }
}
