package org.devinprogress.yaif.bridges;

import net.minecraft.client.gui.GuiScreenBook;
import org.devinprogress.yaif.InputFieldWrapper;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

public class GuiBookBridge extends CommonBridgeNoField{
    private GuiScreenBook bookScr=null;

    public GuiBookBridge(GuiScreenBook screen, InputFieldWrapper wrapper) {
        super(screen, wrapper);
        bookScr=screen;
    }

    @Override
    public boolean needShow(){
        return bookScr.bookIsUnsigned;
    }
}
