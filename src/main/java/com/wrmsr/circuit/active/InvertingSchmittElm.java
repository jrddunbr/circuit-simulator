package com.wrmsr.circuit.active;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;

// contributed by Edward Calver

public class InvertingSchmittElm
        extends CircuitElm
{
    double slewRate; // V/ns
    double lowerTrigger;
    double upperTrigger;
    boolean state;
    double dlt;
    double dut;

    public InvertingSchmittElm()
    {
        noDiagonal = true;
        slewRate = .5;
        state = false;
        lowerTrigger = 1.66;
        upperTrigger = 3.33;
    }

    public InvertingSchmittElm(int f, StringTokenizer st)
    {
        super(f);
        noDiagonal = true;
        try {
            slewRate = new Double(st.nextToken()).doubleValue();
            lowerTrigger = new Double(st.nextToken()).doubleValue();
            upperTrigger = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
            slewRate = .5;
            lowerTrigger = 1.66;
            upperTrigger = 3.33;
        }
    }

    public String dump()
    {
        return super.dump() + " " + slewRate + " " + lowerTrigger + " " + upperTrigger;
    }

    public int getDumpType() { return 183; }//Trying to find unused type

    public int getVoltageSourceCount() { return 1; }

    public void stamp()
    {
        sim.stampVoltageSource(0, nodes[1], voltSource);
    }

    public void doStep()
    {
        double v0 = volts[1];
        double out;
        if (state) {//Output is high
            if (volts[0] > upperTrigger)//Input voltage high enough to set output low
            {
                state = false;
                out = 0;
            }
            else {
                out = 5;
            }
        }
        else {//Output is low
            if (volts[0] < lowerTrigger)//Input voltage low enough to set output high
            {
                state = true;
                out = 5;
            }
            else {
                out = 0;
            }
        }

        double maxStep = slewRate * sim.timeStep * 1e9;
        out = Math.max(Math.min(v0 + maxStep, out), v0 - maxStep);
        sim.updateVoltageSource(0, nodes[1], voltSource, out);
    }

    public double getVoltageDiff() { return volts[0]; }

    public void getInfo(String arr[])
    {
        arr[0] = "InvertingSchmitt";
        arr[1] = "Vi = " + getVoltageText(volts[0]);
        arr[2] = "Vo = " + getVoltageText(volts[1]);
    }

    // there is no current path through the InvertingSchmitt input, but there
    // is an indirect path through the output to ground.
    public boolean getConnection(int n1, int n2) { return false; }

    public boolean hasGroundConnection(int n1)
    {
        return (n1 == 1);
    }
}
