package com.wrmsr.circuit.passive;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class InductorElm
        extends CircuitElm
{
    public Inductor ind;
    public double inductance;

    public InductorElm()
    {
        ind = new Inductor(sim);
        inductance = 1;
        ind.setup(inductance, current, flags);
    }

    public InductorElm(int f,
            StringTokenizer st)
    {
        super(f);
        ind = new Inductor(sim);
        inductance = new Double(st.nextToken()).doubleValue();
        current = new Double(st.nextToken()).doubleValue();
        ind.setup(inductance, current, flags);
    }

    public int getDumpType() { return 'l'; }

    public String dump()
    {
        return super.dump() + " " + inductance + " " + current;
    }

    public void reset()
    {
        current = volts[0] = volts[1] = curcount = 0;
        ind.reset();
    }

    public void stamp() { ind.stamp(nodes[0], nodes[1]); }

    public void startIteration()
    {
        ind.startIteration(volts[0] - volts[1]);
    }

    public boolean nonLinear() { return ind.nonLinear(); }

    public void calculateCurrent()
    {
        double voltdiff = volts[0] - volts[1];
        current = ind.calculateCurrent(voltdiff);
    }

    public void doStep()
    {
        double voltdiff = volts[0] - volts[1];
        ind.doStep(voltdiff);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "inductor";
        getBasicInfo(arr);
        arr[3] = "L = " + getUnitText(inductance, "H");
        arr[4] = "P = " + getUnitText(getPower(), "W");
    }
}
