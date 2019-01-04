package com.wrmsr.circuit.active;

public class OpAmpSwapElm
        extends OpAmpElm
{
    public OpAmpSwapElm()
    {
        flags |= FLAG_SWAP;
    }

    public Class getDumpClass() { return OpAmpElm.class; }
}
