package com.wrmsr.circuit.logicgates;

import java.util.StringTokenizer;

public class XorGateElm
        extends OrGateElm
{
    public XorGateElm() {  }

    public XorGateElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public String getGateName() { return "XOR gate"; }

    public boolean calcFunction()
    {
        int i;
        boolean f = false;
        for (i = 0; i != inputCount; i++) {
            f ^= getInput(i);
        }
        return f;
    }

    public int getDumpType() { return 154; }

    public int getShortcut() { return '4'; }
}
