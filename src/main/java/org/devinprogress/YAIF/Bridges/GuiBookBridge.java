package org.devinprogress.YAIF.Bridges;

import net.minecraft.client.gui.GuiScreenBook;
import org.devinprogress.YAIF.InputFieldWrapper;

/**
 * Created by recursiveg on 14-11-13.
 */
public class GuiBookBridge extends CommonBridgeNoField{
    private GuiScreenBook bookScr=null;

    public GuiBookBridge(GuiScreenBook screen, InputFieldWrapper wrapper) {
        super(screen, wrapper);
        bookScr=screen;
    }

    @Override
    public boolean needShow(){
        return bookScr.bookIsUnsigned;
        //return true;
    }


}
