package com.wrmsr.circuit.logicgates;

import java.util.StringTokenizer;

class AndGateElm
        extends GateElm
{
    public AndGateElm() { }

    public AndGateElm(int f,
            StringTokenizer st)
    {
        super(f, st);
    }

    public String getGateName() { return "AND gate"; }

    public boolean calcFunction()
    {
        int i;
        boolean f = true;
        for (i = 0; i != inputCount; i++) {
            f &= getInput(i);
        }
        return f;
    }

    public int getDumpType() { return 150; }

    public int getShortcut() { return '2'; }
}
