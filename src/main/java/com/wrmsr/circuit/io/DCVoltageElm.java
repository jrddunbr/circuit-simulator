package com.wrmsr.circuit.io;

import com.wrmsr.circuit.generic.VoltageElm;

public class DCVoltageElm
        extends VoltageElm
{
    public DCVoltageElm() { super(WF_DC); }

    public Class getDumpClass() { return VoltageElm.class; }

    public int getShortcut() { return 'v'; }
}
