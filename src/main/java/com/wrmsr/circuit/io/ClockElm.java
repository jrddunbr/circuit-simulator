package com.wrmsr.circuit.io;

public class ClockElm
        extends RailElm
{
    public ClockElm()
    {
        super(WF_SQUARE);
        maxVoltage = 2.5;
        bias = 2.5;
        frequency = 100;
        flags |= FLAG_CLOCK;
    }

    public Class getDumpClass() { return RailElm.class; }
}
