package org.devinprogress.yaif.bridges;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

public class DoNothingBridge extends BaseActionBridge {
    @Override
    public boolean needShow() {
        return false;
    }
}
