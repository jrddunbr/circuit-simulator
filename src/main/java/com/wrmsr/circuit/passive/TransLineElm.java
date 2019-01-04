package com.wrmsr.circuit.passive;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;

public class TransLineElm
        extends CircuitElm
{
    double delay, imped;
    double voltageL[], voltageR[];
    int lenSteps, ptr, width;
    int voltSource1, voltSource2;
    double current1, current2, curCount1, curCount2;

    public TransLineElm()
    {
        delay = 1000 * sim.timeStep;
        imped = 75;
        noDiagonal = true;
        reset();
    }

    public TransLineElm(int f, StringTokenizer st)
    {
        super(f);
        delay = new Double(st.nextToken()).doubleValue();
        imped = new Double(st.nextToken()).doubleValue();
        width = new Integer(st.nextToken()).intValue();
        // next slot is for resistance (losses), which is not implemented
        st.nextToken();
        noDiagonal = true;
        reset();
    }

    public int getDumpType() { return 171; }

    public int getPostCount() { return 4; }

    public int getInternalNodeCount() { return 2; }

    public String dump()
    {
        return super.dump() + " " + delay + " " + imped + " " + width + " " + 0.;
    }

    public void reset()
    {
        if (sim.timeStep == 0) {
            return;
        }
        lenSteps = (int) (delay / sim.timeStep);
        System.out.println(lenSteps + " steps");
        if (lenSteps > 100000) {
            voltageL = voltageR = null;
        }
        else {
            voltageL = new double[lenSteps];
            voltageR = new double[lenSteps];
        }
        ptr = 0;
        super.reset();
    }

    public void setVoltageSource(int n, int v)
    {
        if (n == 0) {
            voltSource1 = v;
        }
        else {
            voltSource2 = v;
        }
    }

    public void setCurrent(int v, double c)
    {
        if (v == voltSource1) {
            current1 = c;
        }
        else {
            current2 = c;
        }
    }

    public void stamp()
    {
        sim.stampVoltageSource(nodes[4], nodes[0], voltSource1);
        sim.stampVoltageSource(nodes[5], nodes[1], voltSource2);
        sim.stampResistor(nodes[2], nodes[4], imped);
        sim.stampResistor(nodes[3], nodes[5], imped);
    }

    public void startIteration()
    {
        // calculate voltages, currents sent over wire
        if (voltageL == null) {
            sim.stop("Transmission line delay too large!", this);
            return;
        }
        voltageL[ptr] = volts[2] - volts[0] + volts[2] - volts[4];
        voltageR[ptr] = volts[3] - volts[1] + volts[3] - volts[5];
        //System.out.println(volts[2] + " " + volts[0] + " " + (volts[2]-volts[0]) + " " + (imped*current1) + " " + voltageL[ptr]);
    /*System.out.println("sending fwd  " + currentL[ptr] + " " + current1);
	  System.out.println("sending back " + currentR[ptr] + " " + current2);*/
        //System.out.println("sending back " + voltageR[ptr]);
        ptr = (ptr + 1) % lenSteps;
    }

    public void doStep()
    {
        if (voltageL == null) {
            sim.stop("Transmission line delay too large!", this);
            return;
        }
        sim.updateVoltageSource(nodes[4], nodes[0], voltSource1, -voltageR[ptr]);
        sim.updateVoltageSource(nodes[5], nodes[1], voltSource2, -voltageL[ptr]);
        if (Math.abs(volts[0]) > 1e-5 || Math.abs(volts[1]) > 1e-5) {
            sim.stop("Need to ground transmission line!", this);
            return;
        }
    }

    //double getVoltageDiff() { return volts[0]; }
    public int getVoltageSourceCount() { return 2; }

    public boolean hasGroundConnection(int n1) { return false; }

    public boolean getConnection(int n1, int n2)
    {
        return false;
	/*if (comparePair(n1, n2, 0, 1))
	  return true;
	  if (comparePair(n1, n2, 2, 3))
	  return true;
	  return false;*/
    }

    public void getInfo(String arr[])
    {
        arr[0] = "transmission line";
        arr[1] = getUnitText(imped, sim.ohmString);
        arr[2] = "length = " + getUnitText(2.9979e8 * delay, "m");
        arr[3] = "delay = " + getUnitText(delay, "s");
    }
}

