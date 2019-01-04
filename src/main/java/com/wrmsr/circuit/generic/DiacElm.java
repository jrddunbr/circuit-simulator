package com.wrmsr.circuit.generic;
// stub implementation of DiacElm, based on SparkGapElm
// FIXME need to add DiacElm.java to srclist
// FIXME need to uncomment DiacElm line from CirSim.java

import java.util.StringTokenizer;

public class DiacElm
        extends CircuitElm
{
    public double onresistance, offresistance, breakdown, holdcurrent;
    public boolean state;

    public DiacElm()
    {
        // FIXME need to adjust defaults to make sense for diac
        offresistance = 1e9;
        onresistance = 1e3;
        breakdown = 1e3;
        holdcurrent = 0.001;
        state = false;
    }

    public DiacElm(int f, StringTokenizer st)
    {
        super(f);
        onresistance = new Double(st.nextToken()).doubleValue();
        offresistance = new Double(st.nextToken()).doubleValue();
        breakdown = new Double(st.nextToken()).doubleValue();
        holdcurrent = new Double(st.nextToken()).doubleValue();
    }

    public boolean nonLinear() {return true;}

    public int getDumpType() { return 203; }

    public String dump()
    {
        return super.dump() + " " + onresistance + " " + offresistance + " "
                + breakdown + " " + holdcurrent;
    }

    public void calculateCurrent()
    {
        double vd = volts[0] - volts[1];
        if (state) {
            current = vd / onresistance;
        }
        else {
            current = vd / offresistance;
        }
    }

    public void startIteration()
    {
        double vd = volts[0] - volts[1];
        if (Math.abs(current) < holdcurrent) {
            state = false;
        }
        if (Math.abs(vd) > breakdown) {
            state = true;
        }
        //System.out.print(this + " res current set to " + current + "\n");
    }

    public void doStep()
    {
        if (state) {
            sim.stampResistor(nodes[0], nodes[1], onresistance);
        }
        else {
            sim.stampResistor(nodes[0], nodes[1], offresistance);
        }
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
        arr[3] = state ? "on" : "off";
        arr[4] = "Ron = " + getUnitText(onresistance, sim.ohmString);
        arr[5] = "Roff = " + getUnitText(offresistance, sim.ohmString);
        arr[6] = "Vbrkdn = " + getUnitText(breakdown, "V");
        arr[7] = "Ihold = " + getUnitText(holdcurrent, "A");
    }
}

