package com.wrmsr.circuit.active;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class AnalogSwitchElm
        extends CircuitElm
{
    public final int FLAG_INVERT = 1;
    public double resistance, r_on, r_off;
    public boolean open;

    public AnalogSwitchElm()
    {
        r_on = 20;
        r_off = 1e10;
    }

    public AnalogSwitchElm(int f, StringTokenizer st)
    {
        super(f);
        r_on = 20;
        r_off = 1e10;
        try {
            r_on = new Double(st.nextToken()).doubleValue();
            r_off = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
        }
    }

    public String dump()
    {
        return super.dump() + " " + r_on + " " + r_off;
    }

    public int getDumpType() { return 159; }

    public void calculateCurrent()
    {
        current = (volts[0] - volts[1]) / resistance;
    }

    // we need this to be able to change the matrix for each step
    public boolean nonLinear() { return true; }

    public void stamp()
    {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
    }

    public void doStep()
    {
        open = (volts[2] < 2.5);
        if ((flags & FLAG_INVERT) != 0) {
            open = !open;
        }
        resistance = (open) ? r_off : r_on;
        sim.stampResistor(nodes[0], nodes[1], resistance);
    }

    public int getPostCount() { return 3; }

    public void getInfo(String arr[])
    {
        arr[0] = "analog switch";
        arr[1] = open ? "open" : "closed";
        arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
        arr[3] = "I = " + getCurrentDText(getCurrent());
        arr[4] = "Vc = " + getVoltageText(volts[2]);
    }

    // we have to just assume current will flow either way, even though that
    // might cause singular matrix errors
    public boolean getConnection(int n1, int n2)
    {
        if (n1 == 2 || n2 == 2) {
            return false;
        }
        return true;
    }
}

