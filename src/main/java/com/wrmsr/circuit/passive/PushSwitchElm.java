package com.wrmsr.circuit.passive;

public class PushSwitchElm
        extends SwitchElm
{
    public PushSwitchElm() { super(true); }

    public Class getDumpClass() { return SwitchElm.class; }

    public int getShortcut() { return 0; }
}
