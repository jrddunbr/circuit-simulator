package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class CurrentElm
        extends CircuitElm
{
    double currentValue;

    public CurrentElm()
    {
        super();
        currentValue = .01;
    }

    public CurrentElm(int f, StringTokenizer st)
    {
        super(f);
        try {
            currentValue = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
            currentValue = .01;
        }
    }

    public String dump()
    {
        return super.dump() + " " + currentValue;
    }

    public int getDumpType() { return 'i'; }

    public void stamp()
    {
        current = currentValue;
        sim.stampCurrentSource(nodes[0], nodes[1], current);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "current source";
        getBasicInfo(arr);
    }

    public double getVoltageDiff()
    {
        return volts[1] - volts[0];
    }
}
