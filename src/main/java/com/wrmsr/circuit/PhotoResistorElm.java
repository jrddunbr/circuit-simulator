package com.wrmsr.circuit;
// stub PhotoResistorElm based on SparkGapElm
// FIXME need to uncomment PhotoResistorElm line from CirSim.java
// FIXME need to add PhotoResistorElm.java to srclist


import java.util.StringTokenizer;

public class PhotoResistorElm
        extends CircuitElm
{
    public double minresistance, maxresistance;
    public double resistance;

    public PhotoResistorElm()
    {
        maxresistance = 1e9;
        minresistance = 1e3;
    }

    public PhotoResistorElm(int f, StringTokenizer st)
    {
        super(f);
        minresistance = new Double(st.nextToken()).doubleValue();
        maxresistance = new Double(st.nextToken()).doubleValue();
    }

    public boolean nonLinear() {return true;}

    public int getDumpType() { return 190; }

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

}

