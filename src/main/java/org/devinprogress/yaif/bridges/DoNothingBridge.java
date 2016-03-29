package org.devinprogress.yaif.bridges;

/**
 * Created by recursiveg on 14-12-17.
 */
public class DoNothingBridge extends BaseActionBridge {
    @Override
    public boolean needShow(){
        return false;
    }
}
