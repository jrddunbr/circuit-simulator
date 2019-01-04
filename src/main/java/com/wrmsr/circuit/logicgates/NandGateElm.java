package com.wrmsr.circuit.logicgates;

import java.util.StringTokenizer;

public class NandGateElm
        extends AndGateElm
{
    public NandGateElm() {}

    public NandGateElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public boolean isInverting() { return true; }

    public String getGateName() { return "NAND gate"; }

    public int getDumpType() { return 151; }

    public int getShortcut() { return '@'; }
}
