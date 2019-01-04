package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;

public class GroundElm
        extends CircuitElm
{
    public GroundElm(int xx, int yy) { super(); }

    public GroundElm(int f, StringTokenizer st)
    {
        super(f);
    }

    public int getDumpType() { return 'g'; }

    public int getPostCount() { return 1; }

    public void setCurrent(int x, double c) { current = -c; }

    public void stamp()
    {
        sim.stampVoltageSource(0, nodes[0], voltSource, 0);
    }

    public double getVoltageDiff() { return 0; }

    public int getVoltageSourceCount() { return 1; }

    public void getInfo(String arr[])
    {
        arr[0] = "ground";
        arr[1] = "I = " + getCurrentText(getCurrent());
    }

    public boolean hasGroundConnection(int n1) { return true; }

    public int getShortcut() { return 'g'; }
}
