package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class LogicOutputElm
        extends CircuitElm
{
    final int FLAG_TERNARY = 1;
    final int FLAG_NUMERIC = 2;
    final int FLAG_PULLDOWN = 4;
    double threshold;
    String value;

    public LogicOutputElm()
    {
        threshold = 2.5;
    }

    public LogicOutputElm(int f, StringTokenizer st)
    {
        super(f);
        try {
            threshold = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
            threshold = 2.5;
        }
    }

    public String dump()
    {
        return super.dump() + " " + threshold;
    }

    public int getDumpType() { return 'M'; }

    public int getPostCount() { return 1; }

    public boolean isTernary() { return (flags & FLAG_TERNARY) != 0; }

    public boolean isNumeric() { return (flags & (FLAG_TERNARY | FLAG_NUMERIC)) != 0; }

    public boolean needsPullDown() { return (flags & FLAG_PULLDOWN) != 0; }

    public void stamp()
    {
        if (needsPullDown()) {
            sim.stampResistor(nodes[0], 0, 1e6);
        }
    }

    public double getVoltageDiff() { return volts[0]; }

    public void getInfo(String arr[])
    {
        arr[0] = "logic output";
        arr[1] = (volts[0] < threshold) ? "low" : "high";
        if (isNumeric()) {
            arr[1] = value;
        }
        arr[2] = "V = " + getVoltageText(volts[0]);
    }
}
