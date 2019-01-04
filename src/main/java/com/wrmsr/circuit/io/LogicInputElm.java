package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.passive.SwitchElm;

public class LogicInputElm
		extends SwitchElm
{
    public final int FLAG_TERNARY = 1;
    public final int FLAG_NUMERIC = 2;
    public double hiV, loV;

    public LogicInputElm()
    {
        super(false);
        hiV = 5;
        loV = 0;
    }

    public LogicInputElm(int f, StringTokenizer st)
    {
        super(f, st);
        try {
            hiV = new Double(st.nextToken()).doubleValue();
            loV = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
            hiV = 5;
            loV = 0;
        }
		if (isTernary()) {
			posCount = 3;
		}
    }

    public boolean isTernary() { return (flags & FLAG_TERNARY) != 0; }

    public boolean isNumeric() { return (flags & (FLAG_TERNARY | FLAG_NUMERIC)) != 0; }

    public int getDumpType() { return 'L'; }

    public String dump()
    {
        return super.dump() + " " + hiV + " " + loV;
    }

    public int getPostCount() { return 1; }



    public void setCurrent(int vs, double c) { current = -c; }

    public void stamp()
    {
        double v = (position == 0) ? loV : hiV;
		if (isTernary()) {
			v = position * 2.5;
		}
        sim.stampVoltageSource(0, nodes[0], voltSource, v);
    }

    public int getVoltageSourceCount() { return 1; }

    public double getVoltageDiff() { return volts[0]; }

    public  void getInfo(String arr[])
    {
        arr[0] = "logic input";
        arr[1] = (position == 0) ? "low" : "high";
		if (isNumeric()) {
			arr[1] = "" + position;
		}
        arr[1] += " (" + getVoltageText(volts[0]) + ")";
        arr[2] = "I = " + getCurrentText(getCurrent());
    }

    public boolean hasGroundConnection(int n1) { return true; }
}
