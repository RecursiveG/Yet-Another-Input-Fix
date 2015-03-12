package org.devinprogress.YAIF.fmlplugin;

import java.io.IOException;

/**
 * Created by recursiveg on 15-2-24.
 */
public class AccessTransformer extends net.minecraftforge.fml.common.asm.transformers.AccessTransformer {
    public AccessTransformer() throws IOException{
        super("inputfix_at.cfg");
    }
}
