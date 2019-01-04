package com.wrmsr.circuit.passive;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;
import com.wrmsr.circuit.io.LogicInputElm;

public class SwitchElm
        extends CircuitElm
{
    public boolean momentary;
    // position 0 == closed, position 1 == open
    public int position, posCount;

    public SwitchElm()
    {
        momentary = false;
        position = 0;
        posCount = 2;
    }

    public SwitchElm(boolean mm)
    {
        position = (mm) ? 1 : 0;
        momentary = mm;
        posCount = 2;
    }

    public SwitchElm(int f, StringTokenizer st)
    {
        super(f);
        String str = st.nextToken();
        if (str.compareTo("true") == 0) {
            position = (this instanceof LogicInputElm) ? 0 : 1;
        }
        else if (str.compareTo("false") == 0) {
            position = (this instanceof LogicInputElm) ? 1 : 0;
        }
        else {
            position = new Integer(str).intValue();
        }
        momentary = new Boolean(st.nextToken()).booleanValue();
        posCount = 2;
    }

    public int getDumpType() { return 's'; }

    public String dump()
    {
        return super.dump() + " " + position + " " + momentary;
    }



    public void calculateCurrent()
    {
        if (position == 1) {
            current = 0;
        }
    }

    public void stamp()
    {
        if (position == 0) {
            sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
        }
    }

    public int getVoltageSourceCount()
    {
        return (position == 1) ? 0 : 1;
    }


    public void toggle()
    {
        position++;
        if (position >= posCount) {
            position = 0;
        }
    }

    public void getInfo(String arr[])
    {
        arr[0] = (momentary) ? "push switch (SPST)" : "switch (SPST)";
        if (position == 1) {
            arr[1] = "open";
            arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
        }
        else {
            arr[1] = "closed";
            arr[2] = "V = " + getVoltageText(volts[0]);
            arr[3] = "I = " + getCurrentDText(getCurrent());
        }
    }

    public boolean getConnection(int n1, int n2) { return position == 0; }

    public boolean isWire() { return true; }
}
