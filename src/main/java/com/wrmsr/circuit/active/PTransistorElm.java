package com.wrmsr.circuit.active;

import com.wrmsr.circuit.TransistorElm;

public class PTransistorElm
        extends TransistorElm
{
    public PTransistorElm() { super( true); }

    public Class getDumpClass() { return TransistorElm.class; }
}
