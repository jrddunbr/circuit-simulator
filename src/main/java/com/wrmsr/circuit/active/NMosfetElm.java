package com.wrmsr.circuit.active;

import com.wrmsr.circuit.MosfetElm;

public class NMosfetElm
        extends MosfetElm
{
    public NMosfetElm() { super(false); }

    public Class getDumpClass() { return MosfetElm.class; }
}
