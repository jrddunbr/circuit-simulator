package com.wrmsr.circuit.active;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;

class OpAmpElm
        extends CircuitElm
{
    final int FLAG_SWAP = 1;
    final int FLAG_SMALL = 2;
    final int FLAG_LOWGAIN = 4;
    int opsize, opheight, opwidth, opaddtext;
    double maxOut, minOut, gain, gbw;
    boolean reset;
    double lastvd;

    public OpAmpElm()
    {
        noDiagonal = true;
        maxOut = 15;
        minOut = -15;
        gbw = 1e6;
        setGain();
    }

    public OpAmpElm(int f, StringTokenizer st)
    {
        super(f);
        maxOut = 15;
        minOut = -15;
        // GBW has no effect in this version of the simulator, but we
        // retain it to keep the file format the same
        gbw = 1e6;
        try {
            maxOut = new Double(st.nextToken()).doubleValue();
            minOut = new Double(st.nextToken()).doubleValue();
            gbw = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
        }
        noDiagonal = true;
        setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
        setGain();
    }

    public void setGain()
    {
        // gain of 100000 breaks e-amp-dfdx.txt
        // gain was 1000, but it broke amp-schmitt.txt
        gain = ((flags & FLAG_LOWGAIN) != 0) ? 1000 : 100000;
    }

    public String dump()
    {
        return super.dump() + " " + maxOut + " " + minOut + " " + gbw;
    }

    public boolean nonLinear() { return true; }

    public double getPower() { return volts[2] * current; }

    public void setSize(int s)
    {
        opsize = s;
        opheight = 8 * s;
        opwidth = 13 * s;
        flags = (flags & ~FLAG_SMALL) | ((s == 1) ? FLAG_SMALL : 0);
    }

    public int getPostCount() { return 3; }

    public int getVoltageSourceCount() { return 1; }

    public void getInfo(String arr[])
    {
        arr[0] = "op-amp";
        arr[1] = "V+ = " + getVoltageText(volts[1]);
        arr[2] = "V- = " + getVoltageText(volts[0]);
        // sometimes the voltage goes slightly outside range, to make
        // convergence easier.  so we hide that here.
        double vo = Math.max(Math.min(volts[2], maxOut), minOut);
        arr[3] = "Vout = " + getVoltageText(vo);
        arr[4] = "Iout = " + getCurrentText(getCurrent());
        arr[5] = "range = " + getVoltageText(minOut) + " to " +
                getVoltageText(maxOut);
    }

    public void stamp()
    {
        int vn = sim.nodeList.size() + voltSource;
        sim.stampNonLinear(vn);
        sim.stampMatrix(nodes[2], vn, 1);
    }

    public void doStep()
    {
        double vd = volts[1] - volts[0];
        if (Math.abs(lastvd - vd) > .1) {
            sim.converged = false;
        }
        else if (volts[2] > maxOut + .1 || volts[2] < minOut - .1) {
            sim.converged = false;
        }
        double x = 0;
        int vn = sim.nodeList.size() + voltSource;
        double dx = 0;
        if (vd >= maxOut / gain && (lastvd >= 0 || sim.getrand(4) == 1)) {
            dx = 1e-4;
            x = maxOut - dx * maxOut / gain;
        }
        else if (vd <= minOut / gain && (lastvd <= 0 || sim.getrand(4) == 1)) {
            dx = 1e-4;
            x = minOut - dx * minOut / gain;
        }
        else {
            dx = gain;
        }
        //System.out.println("opamp " + vd + " " + volts[2] + " " + dx + " "  + x + " " + lastvd + " " + sim.converged);

        // newton-raphson
        sim.stampMatrix(vn, nodes[0], dx);
        sim.stampMatrix(vn, nodes[1], -dx);
        sim.stampMatrix(vn, nodes[2], 1);
        sim.stampRightSide(vn, x);

        lastvd = vd;
        /*if (sim.converged)
	      System.out.println((volts[1]-volts[0]) + " " + volts[2] + " " + initvd);*/
    }

    // there is no current path through the op-amp inputs, but there
    // is an indirect path through the output to ground.
    public boolean getConnection(int n1, int n2) { return false; }

    public boolean hasGroundConnection(int n1)
    {
        return (n1 == 2);
    }

    public double getVoltageDiff() { return volts[2] - volts[1]; }

    public int getDumpType() { return 'a'; }
}
