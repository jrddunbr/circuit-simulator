package com.wrmsr.circuit;

class PTransistorElm
        extends TransistorElm
{
    public PTransistorElm(int xx, int yy) { super(xx, yy, true); }

    Class getDumpClass() { return TransistorElm.class; }
}
