package com.wrmsr.circuit.otherelements;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class ProbeElm
        extends CircuitElm
{
    static final int FLAG_SHOWVOLTAGE = 1;

    public ProbeElm() {}

    public ProbeElm(int f, StringTokenizer st)
    {
        super(f);
    }

    public int getDumpType() { return 'p'; }

    public boolean mustShowVoltage()
    {
        return (flags & FLAG_SHOWVOLTAGE) != 0;
    }

    public void getInfo(String arr[])
    {
        arr[0] = "scope probe";
        arr[1] = "Vd = " + getVoltageText(getVoltageDiff());
    }

    public boolean getConnection(int n1, int n2) { return false; }
}

