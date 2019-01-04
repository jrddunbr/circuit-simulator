package com.wrmsr.circuit.logicgates;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class InverterElm
        extends CircuitElm
{
    double slewRate; // V/ns

    public InverterElm()
    {
        noDiagonal = true;
        slewRate = .5;
    }

    public InverterElm(int f, StringTokenizer st)
    {
        super(f);
        noDiagonal = true;
        try {
            slewRate = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
            slewRate = .5;
        }
    }

    public String dump()
    {
        return super.dump() + " " + slewRate;
    }

    public int getDumpType() { return 'I'; }

    public int getVoltageSourceCount() { return 1; }

    public void stamp()
    {
        sim.stampVoltageSource(0, nodes[1], voltSource);
    }

    public void doStep()
    {
        double v0 = volts[1];
        double out = volts[0] > 2.5 ? 0 : 5;
        double maxStep = slewRate * sim.timeStep * 1e9;
        out = Math.max(Math.min(v0 + maxStep, out), v0 - maxStep);
        sim.updateVoltageSource(0, nodes[1], voltSource, out);
    }

    public double getVoltageDiff() { return volts[0]; }

    public void getInfo(String arr[])
    {
        arr[0] = "inverter";
        arr[1] = "Vi = " + getVoltageText(volts[0]);
        arr[2] = "Vo = " + getVoltageText(volts[1]);
    }

    // there is no current path through the inverter input, but there
    // is an indirect path through the output to ground.
    public  boolean getConnection(int n1, int n2) { return false; }

    public boolean hasGroundConnection(int n1)
    {
        return (n1 == 1);
    }

    public int getShortcut() { return '1'; }
}
