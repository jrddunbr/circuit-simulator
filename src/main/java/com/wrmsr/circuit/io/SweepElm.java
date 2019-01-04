package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;

public class SweepElm
        extends CircuitElm
{
    final int FLAG_LOG = 1;
    final int FLAG_BIDIR = 2;
    final int circleSize = 17;
    double maxV, maxF, minF, sweepTime, frequency;
    double fadd, fmul, freqTime, savedTimeStep;
    int dir = 1;
    double v;

    public SweepElm()
    {
        minF = 20;
        maxF = 4000;
        maxV = 5;
        sweepTime = .1;
        flags = FLAG_BIDIR;
        reset();
    }

    public SweepElm(int f, StringTokenizer st)
    {
        super(f);
        minF = new Double(st.nextToken()).doubleValue();
        maxF = new Double(st.nextToken()).doubleValue();
        maxV = new Double(st.nextToken()).doubleValue();
        sweepTime = new Double(st.nextToken()).doubleValue();
        reset();
    }

    public int getDumpType() { return 170; }

    public int getPostCount() { return 1; }

    public String dump()
    {
        return super.dump() + " " + minF + " " + maxF + " " + maxV + " " +
                sweepTime;
    }

    public void stamp()
    {
        sim.stampVoltageSource(0, nodes[0], voltSource);
    }

    public void setParams()
    {
        if (frequency < minF || frequency > maxF) {
            frequency = minF;
            freqTime = 0;
            dir = 1;
        }
        if ((flags & FLAG_LOG) == 0) {
            fadd = dir * sim.timeStep * (maxF - minF) / sweepTime;
            fmul = 1;
        }
        else {
            fadd = 0;
            fmul = Math.pow(maxF / minF, dir * sim.timeStep / sweepTime);
        }
        savedTimeStep = sim.timeStep;
    }

    public  void reset()
    {
        frequency = minF;
        freqTime = 0;
        dir = 1;
        setParams();
    }

    public void startIteration()
    {
        // has timestep been changed?
        if (sim.timeStep != savedTimeStep) {
            setParams();
        }
        v = Math.sin(freqTime) * maxV;
        freqTime += frequency * 2 * pi * sim.timeStep;
        frequency = frequency * fmul + fadd;
        if (frequency >= maxF && dir == 1) {
            if ((flags & FLAG_BIDIR) != 0) {
                fadd = -fadd;
                fmul = 1 / fmul;
                dir = -1;
            }
            else {
                frequency = minF;
            }
        }
        if (frequency <= minF && dir == -1) {
            fadd = -fadd;
            fmul = 1 / fmul;
            dir = 1;
        }
    }

    public void doStep()
    {
        sim.updateVoltageSource(0, nodes[0], voltSource, v);
    }

    public double getVoltageDiff() { return volts[0]; }

    public int getVoltageSourceCount() { return 1; }

    public boolean hasGroundConnection(int n1) { return true; }

    public void getInfo(String arr[])
    {
        arr[0] = "sweep " + (((flags & FLAG_LOG) == 0) ? "(linear)" : "(log)");
        arr[1] = "I = " + getCurrentDText(getCurrent());
        arr[2] = "V = " + getVoltageText(volts[0]);
        arr[3] = "f = " + getUnitText(frequency, "Hz");
        arr[4] = "range = " + getUnitText(minF, "Hz") + " .. " +
                getUnitText(maxF, "Hz");
        arr[5] = "time = " + getUnitText(sweepTime, "s");
    }
}
    
