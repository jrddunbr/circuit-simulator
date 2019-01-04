package com.wrmsr.circuit.logicgates;

import com.wrmsr.circuit.CircuitElm;

import java.util.StringTokenizer;

public abstract class GateElm
        extends CircuitElm
{
    public final int FLAG_SMALL = 1;
    public int inputCount = 2;
    public boolean lastOutput;
    public int gsize, gwidth, gwidth2, gheight, hs2;
    public int ww;

    public GateElm()
    {
        noDiagonal = true;
        inputCount = 2;
    }

    public GateElm(int f, StringTokenizer st)
    {
        super(f);
        inputCount = new Integer(st.nextToken()).intValue();
        lastOutput = new Double(st.nextToken()).doubleValue() > 2.5;
        noDiagonal = true;
        setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
    }

    public boolean isInverting() { return false; }

    public void setSize(int s)
    {
        gsize = s;
        gwidth = 7 * s;
        gwidth2 = 14 * s;
        gheight = 8 * s;
        flags = (s == 1) ? FLAG_SMALL : 0;
    }

    public String dump()
    {
        return super.dump() + " " + inputCount + " " + volts[inputCount];
    }

    public int getPostCount() { return inputCount + 1; }

    public int getVoltageSourceCount() { return 1; }

    public abstract String getGateName();

    public void getInfo(String arr[])
    {
        arr[0] = getGateName();
        arr[1] = "Vout = " + getVoltageText(volts[inputCount]);
        arr[2] = "Iout = " + getCurrentText(getCurrent());
    }

    public void stamp()
    {
        sim.stampVoltageSource(0, nodes[inputCount], voltSource);
    }

    public boolean getInput(int x)
    {
        return volts[x] > 2.5;
    }

    public abstract boolean calcFunction();

    public void doStep()
    {
        int i;
        boolean f = calcFunction();
        if (isInverting()) {
            f = !f;
        }
        lastOutput = f;
        double res = f ? 5 : 0;
        sim.updateVoltageSource(0, nodes[inputCount], voltSource, res);
    }

    // there is no current path through the gate inputs, but there
    // is an indirect path through the output to ground.
    public boolean getConnection(int n1, int n2) { return false; }

    public boolean hasGroundConnection(int n1)
    {
        return (n1 == inputCount);
    }
}

