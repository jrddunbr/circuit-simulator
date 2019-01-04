package com.wrmsr.circuit.logicgates;

import java.util.StringTokenizer;

public class NorGateElm
        extends OrGateElm
{
    public NorGateElm() {}

    public NorGateElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public String getGateName() { return "NOR gate"; }

    public boolean isInverting() { return true; }

    public int getDumpType() { return 153; }

    public int getShortcut() { return '#'; }
}
