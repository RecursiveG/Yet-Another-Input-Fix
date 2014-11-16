package org.devinprogress.YAIF.Transformer;

import java.io.IOException;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

public class AccessTransformer extends cpw.mods.fml.common.asm.transformers.AccessTransformer{
    public AccessTransformer() throws IOException {
        super("inputfix_at.cfg");
    }
}
