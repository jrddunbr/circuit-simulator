package com.wrmsr.circuit.basic;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class WireElm
        extends CircuitElm
{
    public static final int FLAG_SHOWCURRENT = 1;
    public static final int FLAG_SHOWVOLTAGE = 2;

    public WireElm() {}

    public WireElm(int f, StringTokenizer st)
    {
        super(f);
    }

    public void stamp()
    {
        sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
    }

    public boolean mustShowCurrent()
    {
        return (flags & FLAG_SHOWCURRENT) != 0;
    }

    public boolean mustShowVoltage()
    {
        return (flags & FLAG_SHOWVOLTAGE) != 0;
    }

    public int getVoltageSourceCount() { return 1; }

    public void getInfo(String arr[])
    {
        arr[0] = "wire";
        arr[1] = "I = " + getCurrentDText(getCurrent());
        arr[2] = "V = " + getVoltageText(volts[0]);
    }

    public int getDumpType() { return 'w'; }

    public double getPower() { return 0; }

    public double getVoltageDiff() { return volts[0]; }

    public boolean isWire() { return true; }
}
