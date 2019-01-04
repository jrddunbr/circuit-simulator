package com.wrmsr.circuit.passive;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class MemristorElm
        extends CircuitElm
{
    public double r_on, r_off, dopeWidth, totalWidth, mobility, resistance;

    public MemristorElm()
    {
        r_on = 100;
        r_off = 160 * r_on;
        dopeWidth = 0;
        totalWidth = 10e-9; // meters
        mobility = 1e-10;   // m^2/sV
        resistance = 100;
    }

    public MemristorElm(int f, StringTokenizer st)
    {
        super(f);
        r_on = new Double(st.nextToken()).doubleValue();
        r_off = new Double(st.nextToken()).doubleValue();
        dopeWidth = new Double(st.nextToken()).doubleValue();
        totalWidth = new Double(st.nextToken()).doubleValue();
        mobility = new Double(st.nextToken()).doubleValue();
        resistance = 100;
    }

    public int getDumpType() { return 'm'; }

    public String dump()
    {
        return super.dump() + " " + r_on + " " + r_off + " " + dopeWidth + " " +
                totalWidth + " " + mobility;
    }

    public boolean nonLinear() { return true; }

    public void calculateCurrent()
    {
        current = (volts[0] - volts[1]) / resistance;
    }

    public void reset()
    {
        dopeWidth = 0;
    }

    public void startIteration()
    {
        double wd = dopeWidth / totalWidth;
        dopeWidth += sim.timeStep * mobility * r_on * current / totalWidth;
        if (dopeWidth < 0) {
            dopeWidth = 0;
        }
        if (dopeWidth > totalWidth) {
            dopeWidth = totalWidth;
        }
        resistance = r_on * wd + r_off * (1 - wd);
    }

    public void stamp()
    {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
    }

    public void doStep()
    {
        sim.stampResistor(nodes[0], nodes[1], resistance);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "memristor";
        getBasicInfo(arr);
        arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
        arr[4] = "P = " + getUnitText(getPower(), "W");
    }

    public double getScopeValue(int x)
    {
        return (x == 2) ? resistance : (x == 1) ? getPower() : getVoltageDiff();
    }

    public String getScopeUnits(int x)
    {
        return (x == 2) ? sim.ohmString : (x == 1) ? "W" : "V";
    }
}

