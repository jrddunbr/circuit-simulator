package com.wrmsr.circuit.active;

import com.wrmsr.circuit.generic.JfetElm;

public class NJfetElm
        extends JfetElm
{
    public NJfetElm() { super(false); }

    public Class getDumpClass() { return JfetElm.class; }
}

