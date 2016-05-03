package org.devinprogress.yaif;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class switchListener {
    private static switchListener instance = null;

    public static switchListener getInstance() {
        if (instance == null) instance = new switchListener();
        return instance;
    }


    private static Field lwjglKeyBuffer;

    static {
        try {
            lwjglKeyBuffer = Keyboard.class.getDeclaredField("keyDownBuffer");
            lwjglKeyBuffer.setAccessible(true);
        } catch (Exception ex) {
            lwjglKeyBuffer = null;
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public void clientTickEvent(TickEvent.ClientTickEvent event) {
        /*YetAnotherInputFix.log.info(String.format("Ticking event #%d, Side=%s Phase=%s Type=%s",round,event.side.name(),event.phase.name(),event.type.name()));
        if(++round==10) {
            round=0;
            //FMLCommonHandler.instance().bus().unregister(this);
        }*/
        if (!hasKeyDown()) {
            InputFieldWrapper.instance._show();
            FMLCommonHandler.instance().bus().unregister(this);
        }
    }

    private boolean hasKeyDown() {
        try {
            ByteBuffer tmp = (ByteBuffer) (lwjglKeyBuffer.get(null));
            for (int i = 0; i < tmp.remaining(); i++)
                if (tmp.get(i) != 0) return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
