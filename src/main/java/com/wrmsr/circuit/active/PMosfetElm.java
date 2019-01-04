package com.wrmsr.circuit.active;

import com.wrmsr.circuit.generic.MosfetElm;

public class PMosfetElm
        extends MosfetElm
{
    public PMosfetElm() { super(true); }

    public Class getDumpClass() { return MosfetElm.class; }
}
