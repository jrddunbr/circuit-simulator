package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;

// contributed by Edward Calver

public class FMElm
        extends CircuitElm
{
    static final int FLAG_COS = 2;
    final int circleSize = 17;
    double carrierfreq, signalfreq, maxVoltage, freqTimeZero, deviation;
    double lasttime = 0;
    double funcx = 0;

    public FMElm()
    {
        deviation = 200;
        maxVoltage = 5;
        carrierfreq = 800;
        signalfreq = 40;
        reset();
    }

    public FMElm(int f, StringTokenizer st)
    {
        super(f);
        carrierfreq = new Double(st.nextToken()).doubleValue();
        signalfreq = new Double(st.nextToken()).doubleValue();
        maxVoltage = new Double(st.nextToken()).doubleValue();
        deviation = new Double(st.nextToken()).doubleValue();
        if ((flags & FLAG_COS) != 0) {
            flags &= ~FLAG_COS;
        }
        reset();
    }

    public int getDumpType() { return 201; }
    /*void setCurrent(double c) {
      current = c;
      System.out.print("v current set to " + c + "\n");
      }*/

    public String dump()
    {
        return super.dump() + " " + carrierfreq + " " + signalfreq + " " + maxVoltage + " " + deviation;
    }

    public void reset()
    {
        freqTimeZero = 0;
        curcount = 0;
    }

    public int getPostCount() { return 1; }

    public void stamp()
    {
        sim.stampVoltageSource(0, nodes[0], voltSource);
    }

    public void doStep()
    {
        sim.updateVoltageSource(0, nodes[0], voltSource, getVoltage());
    }

    public double getVoltage()
    {
        double deltaT = sim.t - lasttime;
        lasttime = sim.t;
        double signalamplitude = Math.sin((2 * pi * (sim.t - freqTimeZero)) * signalfreq);
        funcx += deltaT * (carrierfreq + (signalamplitude * deviation));
        double w = 2 * pi * funcx;
        return Math.sin(w) * maxVoltage;
    }

    public double getVoltageDiff() { return volts[0]; }

    public boolean hasGroundConnection(int n1) { return true; }

    public int getVoltageSourceCount()
    {
        return 1;
    }

    public double getPower() { return -getVoltageDiff() * current; }

    public void getInfo(String arr[])
    {

        arr[0] = "FM Source";
        arr[1] = "I = " + getCurrentText(getCurrent());
        arr[2] = "V = " +
                getVoltageText(getVoltageDiff());
        arr[3] = "cf = " + getUnitText(carrierfreq, "Hz");
        arr[4] = "sf = " + getUnitText(signalfreq, "Hz");
        arr[5] = "dev =" + getUnitText(deviation, "Hz");
        arr[6] = "Vmax = " + getVoltageText(maxVoltage);
    }
}
