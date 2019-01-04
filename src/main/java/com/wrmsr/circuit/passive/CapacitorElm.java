package com.wrmsr.circuit.passive;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class CapacitorElm
        extends CircuitElm
{
    public static final int FLAG_BACK_EULER = 2;
    public double capacitance;
    public double compResistance, voltdiff;
    public double curSourceValue;

    public CapacitorElm()
    {
        capacitance = 1e-5;
    }

    public CapacitorElm(int f, StringTokenizer st)
    {
        super(f);
        capacitance = new Double(st.nextToken()).doubleValue();
        voltdiff = new Double(st.nextToken()).doubleValue();
    }

    boolean isTrapezoidal() { return (flags & FLAG_BACK_EULER) == 0; }

    public void setNodeVoltage(int n, double c)
    {
        super.setNodeVoltage(n, c);
        voltdiff = volts[0] - volts[1];
    }

    public void reset()
    {
        current = curcount = 0;
        // put small charge on caps when reset to start oscillators
        voltdiff = 1e-3;
    }

    public int getDumpType() { return 'c'; }

    public String dump()
    {
        return super.dump() + " " + capacitance + " " + voltdiff;
    }



    public void stamp()
    {
        // capacitor companion model using trapezoidal approximation
        // (Norton equivalent) consists of a current source in
        // parallel with a resistor.  Trapezoidal is more accurate
        // than backward euler but can cause oscillatory behavior
        // if RC is small relative to the timestep.
        if (isTrapezoidal()) {
            compResistance = sim.timeStep / (2 * capacitance);
        }
        else {
            compResistance = sim.timeStep / capacitance;
        }
        sim.stampResistor(nodes[0], nodes[1], compResistance);
        sim.stampRightSide(nodes[0]);
        sim.stampRightSide(nodes[1]);
    }

    public void startIteration()
    {
        if (isTrapezoidal()) {
            curSourceValue = -voltdiff / compResistance - current;
        }
        else {
            curSourceValue = -voltdiff / compResistance;
        }
        //System.out.println("cap " + compResistance + " " + curSourceValue + " " + current + " " + voltdiff);
    }

    public void calculateCurrent()
    {
        double voltdiff = volts[0] - volts[1];
        // we check compResistance because this might get called
        // before stamp(), which sets compResistance, causing
        // infinite current
        if (compResistance > 0) {
            current = voltdiff / compResistance + curSourceValue;
        }
    }

    public  void doStep()
    {
        sim.stampCurrentSource(nodes[0], nodes[1], curSourceValue);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "capacitor";
        getBasicInfo(arr);
        arr[3] = "C = " + getUnitText(capacitance, "F");
        arr[4] = "P = " + getUnitText(getPower(), "W");
        //double v = getVoltageDiff();
        //arr[4] = "U = " + getUnitText(.5*capacitance*v*v, "J");
    }
}
