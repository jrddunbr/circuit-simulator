package com.wrmsr.circuit.generic;

import com.wrmsr.circuit.CirSim;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class CircuitElm
{
    public static final double pi = 3.14159265358979323846;
    public static NumberFormat showFormat, shortFormat, noCommaFormat;
    public static double voltageRange = 5;
    public static double currentMult, powerMult;
    public static CirSim sim;
    public int x, y, x2, y2, flags, nodes[], voltSource;
    public double volts[];
    public double current, curcount;
    public boolean noDiagonal;

    public CircuitElm()
    {
        flags = getDefaultFlags();
        allocNodes();
    }

    public CircuitElm(int f)
    {
        flags = f;
        allocNodes();
    }

    public static void initClass(CirSim s)
    {
        sim = s;
        showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits(2);
        shortFormat = DecimalFormat.getInstance();
        shortFormat.setMaximumFractionDigits(1);
        noCommaFormat = DecimalFormat.getInstance();
        noCommaFormat.setMaximumFractionDigits(10);
        noCommaFormat.setGroupingUsed(false);
    }




    public static String getVoltageDText(double v)
    {
        return getUnitText(Math.abs(v), "V");
    }

    public static String getVoltageText(double v)
    {
        return getUnitText(v, "V");
    }

    public static String getUnitText(double v, String u)
    {
        double va = Math.abs(v);
        if (va < 1e-14) {
            return "0 " + u;
        }
        if (va < 1e-9) {
            return showFormat.format(v * 1e12) + " p" + u;
        }
        if (va < 1e-6) {
            return showFormat.format(v * 1e9) + " n" + u;
        }
        if (va < 1e-3) {
            return showFormat.format(v * 1e6) + " " + CirSim.muString + u;
        }
        if (va < 1) {
            return showFormat.format(v * 1e3) + " m" + u;
        }
        if (va < 1e3) {
            return showFormat.format(v) + " " + u;
        }
        if (va < 1e6) {
            return showFormat.format(v * 1e-3) + " k" + u;
        }
        if (va < 1e9) {
            return showFormat.format(v * 1e-6) + " M" + u;
        }
        return showFormat.format(v * 1e-9) + " G" + u;
    }

    public static String getShortUnitText(double v, String u)
    {
        double va = Math.abs(v);
        if (va < 1e-13) {
            return null;
        }
        if (va < 1e-9) {
            return shortFormat.format(v * 1e12) + "p" + u;
        }
        if (va < 1e-6) {
            return shortFormat.format(v * 1e9) + "n" + u;
        }
        if (va < 1e-3) {
            return shortFormat.format(v * 1e6) + CirSim.muString + u;
        }
        if (va < 1) {
            return shortFormat.format(v * 1e3) + "m" + u;
        }
        if (va < 1e3) {
            return shortFormat.format(v) + u;
        }
        if (va < 1e6) {
            return shortFormat.format(v * 1e-3) + "k" + u;
        }
        if (va < 1e9) {
            return shortFormat.format(v * 1e-6) + "M" + u;
        }
        return shortFormat.format(v * 1e-9) + "G" + u;
    }

    public static String getCurrentText(double i)
    {
        return getUnitText(i, "A");
    }

    public static String getCurrentDText(double i)
    {
        return getUnitText(Math.abs(i), "A");
    }

    public static int abs(int x) { return x < 0 ? -x : x; }

    public static int sign(int x) { return (x < 0) ? -1 : (x == 0) ? 0 : 1; }

    public static int min(int a, int b) { return (a < b) ? a : b; }

    public static int max(int a, int b) { return (a > b) ? a : b; }


    public int getDumpType() { return 0; }

    public Class getDumpClass() { return getClass(); }

    public int getDefaultFlags() { return 0; }

    public void allocNodes()
    {
        nodes = new int[getPostCount() + getInternalNodeCount()];
        volts = new double[getPostCount() + getInternalNodeCount()];
    }

    public String dump()
    {
        int t = getDumpType();
        return (t < 127 ? ((char) t) + " " : t + " ") + x + " " + y + " " +
                x2 + " " + y2 + " " + flags;
    }

    public void reset()
    {
        int i;
        for (i = 0; i != getPostCount() + getInternalNodeCount(); i++) {
            volts[i] = 0;
        }
        curcount = 0;
    }


    public void setCurrent(int x, double c) { current = c; }

    public double getCurrent() { return current; }

    public void doStep() {}

    public void delete() {}

    public void startIteration() {}

    public double getPostVoltage(int x) { return volts[x]; }

    public void setNodeVoltage(int n, double c)
    {
        volts[n] = c;
        calculateCurrent();
    }

    public void calculateCurrent() {}

    public void stamp() {}

    public int getVoltageSourceCount() { return 0; }

    public int getInternalNodeCount() { return 0; }

    public void setNode(int p, int n) { nodes[p] = n; }

    public void setVoltageSource(int n, int v) { voltSource = v; }

    public int getVoltageSource() { return voltSource; }

    public double getVoltageDiff()
    {
        return volts[0] - volts[1];
    }

    public boolean nonLinear() { return false; }

    public int getPostCount() { return 2; }

    public int getNode(int n) { return nodes[n]; }


    public int getBasicInfo(String arr[])
    {
        arr[1] = "I = " + getCurrentDText(getCurrent());
        arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
        return 3;
    }


    public double getPower() { return getVoltageDiff() * current; }

    public double getScopeValue(int x)
    {
        return (x == 1) ? getPower() : getVoltageDiff();
    }

    public String getScopeUnits(int x)
    {
        return (x == 1) ? "W" : "V";
    }

    public boolean getConnection(int n1, int n2) { return true; }

    public boolean hasGroundConnection(int n1) { return false; }

    public boolean isWire() { return false; }
}
