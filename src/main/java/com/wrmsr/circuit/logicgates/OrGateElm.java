package com.wrmsr.circuit.logicgates;

import java.util.StringTokenizer;

public class OrGateElm
        extends GateElm
{
    public OrGateElm() { }

    public OrGateElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public String getGateName() { return "OR gate"; }

    public boolean calcFunction()
    {
        int i;
        boolean f = false;
        for (i = 0; i != inputCount; i++) {
            f |= getInput(i);
        }
        return f;
    }

    public int getDumpType() { return 152; }
}
