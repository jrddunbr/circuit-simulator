package com.wrmsr.circuit;
// stub ThermistorElm based on SparkGapElm
// FIXME need to uncomment ThermistorElm line from CirSim.java
// FIXME need to add ThermistorElm.java to srclist

import java.util.StringTokenizer;

public class ThermistorElm
        extends CircuitElm
{
    double minresistance, maxresistance;
    double resistance;

    public ThermistorElm()
    {
        maxresistance = 1e9;
        minresistance = 1e3;
    }

    public ThermistorElm(int f, StringTokenizer st)
    {
        super(f);
        minresistance = new Double(st.nextToken()).doubleValue();
        maxresistance = new Double(st.nextToken()).doubleValue();
    }

    public boolean nonLinear() {return true;}

    public int getDumpType() { return 192; }

    public String dump()
    {
        return super.dump() + " " + minresistance + " " + maxresistance;
    }

    public void calculateCurrent()
    {
        double vd = volts[0] - volts[1];
        current = vd / resistance;
    }

    public void startIteration()
    {
        double vd = volts[0] - volts[1];
        // FIXME set resistance as appropriate, using slider.getValue()
        resistance = minresistance;
        //System.out.print(this + " res current set to " + current + "\n");
    }

    public void doStep()
    {
        sim.stampResistor(nodes[0], nodes[1], resistance);
    }

    public void stamp()
    {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
    }

    public void getInfo(String arr[])
    {
        // FIXME
        arr[0] = "spark gap";
        getBasicInfo(arr);
        arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
        arr[4] = "Ron = " + getUnitText(minresistance, sim.ohmString);
        arr[5] = "Roff = " + getUnitText(maxresistance, sim.ohmString);
    }
}

