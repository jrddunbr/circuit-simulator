package com.wrmsr.circuit.active;

import com.wrmsr.circuit.generic.MosfetElm;

public class NMosfetElm
        extends MosfetElm
{
    public NMosfetElm() { super(false); }

    public Class getDumpClass() { return MosfetElm.class; }
}
