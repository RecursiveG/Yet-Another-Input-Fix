package org.devinprogress.YAIF.Transformer;

import java.io.IOException;

/**
 * Created by recursiveg on 14-10-22.
 */
public class AccessTransformer extends cpw.mods.fml.common.asm.transformers.AccessTransformer{
    public AccessTransformer() throws IOException {
        super("inputfix_at.cfg");
    }
}
