package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class OutputElm
        extends CircuitElm
{

    public OutputElm() {}

    public OutputElm(int f, StringTokenizer st)
    {
        super(f);
    }

    public int getDumpType() { return 'O'; }

    public int getPostCount() { return 1; }

    public double getVoltageDiff() { return volts[0]; }

    public void getInfo(String arr[])
    {
        arr[0] = "output";
        arr[1] = "V = " + getVoltageText(volts[0]);
    }
}
