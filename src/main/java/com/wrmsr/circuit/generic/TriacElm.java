package com.wrmsr.circuit.generic;
// stub implementation of TriacElm, based on SCRElm
// FIXME need to add TriacElm to srclist
// FIXME need to uncomment TriacElm line from CirSim.java

import java.util.StringTokenizer;

// Silicon-Controlled Rectifier
// 3 nodes, 1 internal node
// 0 = anode, 1 = cathode, 2 = gate
// 0, 3 = variable resistor
// 3, 2 = diode
// 2, 1 = 50 ohm resistor

public class TriacElm
        extends CircuitElm
{
    public final int anode = 0;
    public final int cnode = 1;
    public final int gnode = 2;
    public final int inode = 3;
    public final int hs = 8;
    public Diode diode;
    public double ia, ic, ig, curcount_a, curcount_c, curcount_g;
    public double lastvac, lastvag;
    public double cresistance, triggerI, holdingI;
    public double aresistance;

    public TriacElm()
    {
        setDefaults();
        setup();
    }

    public TriacElm(int f, StringTokenizer st)
    {
        super(f);
        setDefaults();
        try {
            lastvac = new Double(st.nextToken()).doubleValue();
            lastvag = new Double(st.nextToken()).doubleValue();
            volts[anode] = 0;
            volts[cnode] = -lastvac;
            volts[gnode] = -lastvag;
            triggerI = new Double(st.nextToken()).doubleValue();
            holdingI = new Double(st.nextToken()).doubleValue();
            cresistance = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
        }
        setup();
    }

    public void setDefaults()
    {
        cresistance = 50;
        holdingI = .0082;
        triggerI = .01;
    }

    public void setup()
    {
        diode = new Diode(sim);
        diode.setup(.8, 0);
    }

    public boolean nonLinear() { return true; }

    public void reset()
    {
        volts[anode] = volts[cnode] = volts[gnode] = 0;
        diode.reset();
        lastvag = lastvac = curcount_a = curcount_c = curcount_g = 0;
    }

    public int getDumpType() { return 206; }

    public String dump()
    {
        return super.dump() + " " + (volts[anode] - volts[cnode]) + " " +
                (volts[anode] - volts[gnode]) + " " + triggerI + " " + holdingI + " " +
                cresistance;
    }

    public int getPostCount() { return 3; }

    public int getInternalNodeCount() { return 1; }

    public double getPower()
    {
        return (volts[anode] - volts[gnode]) * ia + (volts[cnode] - volts[gnode]) * ic;
    }

    public void stamp()
    {
        sim.stampNonLinear(nodes[anode]);
        sim.stampNonLinear(nodes[cnode]);
        sim.stampNonLinear(nodes[gnode]);
        sim.stampNonLinear(nodes[inode]);
        sim.stampResistor(nodes[gnode], nodes[cnode], cresistance);
        diode.stamp(nodes[inode], nodes[gnode]);
    }

    public void doStep()
    {
        double vac = volts[anode] - volts[cnode]; // typically negative
        double vag = volts[anode] - volts[gnode]; // typically positive
        if (Math.abs(vac - lastvac) > .01 ||
                Math.abs(vag - lastvag) > .01) {
            sim.converged = false;
        }
        lastvac = vac;
        lastvag = vag;
        diode.doStep(volts[inode] - volts[gnode]);
        double icmult = 1 / triggerI;
        double iamult = 1 / holdingI - icmult;
        //System.out.println(icmult + " " + iamult);
        aresistance = (-icmult * ic + ia * iamult > 1) ? .0105 : 10e5;
        //System.out.println(vac + " " + vag + " " + sim.converged + " " + ic + " " + ia + " " + aresistance + " " + volts[inode] + " " + volts[gnode] + " " + volts[anode]);
        sim.stampResistor(nodes[anode], nodes[inode], aresistance);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "SCR";
        double vac = volts[anode] - volts[cnode];
        double vag = volts[anode] - volts[gnode];
        double vgc = volts[gnode] - volts[cnode];
        arr[1] = "Ia = " + getCurrentText(ia);
        arr[2] = "Ig = " + getCurrentText(ig);
        arr[3] = "Vac = " + getVoltageText(vac);
        arr[4] = "Vag = " + getVoltageText(vag);
        arr[5] = "Vgc = " + getVoltageText(vgc);
    }

    public void calculateCurrent() {
        ic = (volts[cnode] - volts[gnode]) / cresistance;
        ia = (volts[anode] - volts[inode]) / aresistance;
        ig = -ic - ia;
    }
}

