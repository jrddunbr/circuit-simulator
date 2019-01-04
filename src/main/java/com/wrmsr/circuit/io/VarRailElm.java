package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

public class VarRailElm
        extends RailElm
{
    public VarRailElm()
    {
        super(WF_VAR);
        frequency = maxVoltage;
    }

    public VarRailElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public String dump()
    {
        return super.dump();
    }

    public int getDumpType() { return 172; }

    public double getVoltage()
    {
        frequency = 1 * (maxVoltage - bias) / 100. + bias;
        return frequency;
    }
}
