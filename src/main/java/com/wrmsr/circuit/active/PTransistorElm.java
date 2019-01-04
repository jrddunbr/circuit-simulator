package com.wrmsr.circuit.active;

import com.wrmsr.circuit.generic.TransistorElm;

public class PTransistorElm
        extends TransistorElm
{
    public PTransistorElm() { super( true); }

    public Class getDumpClass() { return TransistorElm.class; }
}
