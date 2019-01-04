package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.VoltageElm;

public class RailElm
        extends VoltageElm
{
    final int FLAG_CLOCK = 1;

    public RailElm() { super(WF_DC); }

    RailElm(int wf) { super(wf); }

    public RailElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public int getDumpType() { return 'R'; }

    public int getPostCount() { return 1; }


    public double getVoltageDiff() { return volts[0]; }

    public void stamp()
    {
        if (waveform == WF_DC) {
            sim.stampVoltageSource(0, nodes[0], voltSource, getVoltage());
        }
        else {
            sim.stampVoltageSource(0, nodes[0], voltSource);
        }
    }

    public void doStep()
    {
        if (waveform != WF_DC) {
            sim.updateVoltageSource(0, nodes[0], voltSource, getVoltage());
        }
    }

    public boolean hasGroundConnection(int n1) { return true; }
}
