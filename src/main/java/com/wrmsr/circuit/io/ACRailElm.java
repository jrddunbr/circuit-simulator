package com.wrmsr.circuit.io;

public class ACRailElm
        extends RailElm
{
    public ACRailElm() { super(WF_DC); }

    public Class getDumpClass() { return RailElm.class; }

    public int getShortcut() { return 0; }
}
