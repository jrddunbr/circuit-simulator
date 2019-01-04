package com.wrmsr.circuit.active;

import java.util.StringTokenizer;

// contributed by Edward Calver

public class SchmittElm
        extends InvertingSchmittElm
{
    public SchmittElm()
    {

    }

    public SchmittElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public int getDumpType() { return 182; }

    public void doStep()
    {
        double v0 = volts[1];
        double out;
        if (state) {//Output is high
            if (volts[0] > upperTrigger)//Input voltage high enough to set output high
            {
                state = false;
                out = 5;
            }
            else {
                out = 0;
            }
        }
        else {//Output is low
            if (volts[0] < lowerTrigger)//Input voltage low enough to set output low
            {
                state = true;
                out = 0;
            }
            else {
                out = 5;
            }
        }

        double maxStep = slewRate * sim.timeStep * 1e9;
        out = Math.max(Math.min(v0 + maxStep, out), v0 - maxStep);
        sim.updateVoltageSource(0, nodes[1], voltSource, out);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "Schmitt";
    }
}
