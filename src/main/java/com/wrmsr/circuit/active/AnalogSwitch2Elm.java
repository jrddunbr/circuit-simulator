package com.wrmsr.circuit.active;

import java.util.StringTokenizer;

public class AnalogSwitch2Elm
        extends AnalogSwitchElm
{
    public final int openhs = 16;

    public AnalogSwitch2Elm()
    {    }

    public AnalogSwitch2Elm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public int getPostCount() { return 4; }

    public int getDumpType() { return 160; }

    public void calculateCurrent()
    {
        if (open) {
            current = (volts[0] - volts[2]) / r_on;
        }
        else {
            current = (volts[0] - volts[1]) / r_on;
        }
    }

    public void stamp()
    {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
        sim.stampNonLinear(nodes[2]);
    }

    public void doStep()
    {
        open = (volts[3] < 2.5);
        if ((flags & FLAG_INVERT) != 0) {
            open = !open;
        }
        if (open) {
            sim.stampResistor(nodes[0], nodes[2], r_on);
            sim.stampResistor(nodes[0], nodes[1], r_off);
        }
        else {
            sim.stampResistor(nodes[0], nodes[1], r_on);
            sim.stampResistor(nodes[0], nodes[2], r_off);
        }
    }

    public boolean getConnection(int n1, int n2)
    {
        if (n1 == 3 || n2 == 3) {
            return false;
        }
        return true;
    }

    public void getInfo(String arr[])
    {
        arr[0] = "analog switch (SPDT)";
        arr[1] = "I = " + getCurrentDText(getCurrent());
    }
}

