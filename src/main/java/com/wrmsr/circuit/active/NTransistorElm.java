package com.wrmsr.circuit.active;

import com.wrmsr.circuit.TransistorElm;

class NTransistorElm
        extends TransistorElm
{
    public NTransistorElm() { super( false); }

    public Class getDumpClass() { return TransistorElm.class; }
}
