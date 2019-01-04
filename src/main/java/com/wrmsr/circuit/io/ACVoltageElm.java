package com.wrmsr.circuit.io;

import com.wrmsr.circuit.generic.VoltageElm;

public class ACVoltageElm
        extends VoltageElm
{
    public ACVoltageElm() { super(WF_AC); }

    public Class getDumpClass() { return VoltageElm.class; }
}
