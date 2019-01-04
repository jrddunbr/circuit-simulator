package com.wrmsr.circuit.io;

public class SquareRailElm
        extends RailElm
{
    public SquareRailElm() { super(WF_SQUARE); }

    public Class getDumpClass() { return RailElm.class; }

    public int getShortcut() { return 0; }
}
