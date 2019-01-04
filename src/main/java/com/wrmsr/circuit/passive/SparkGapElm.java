package com.wrmsr.circuit.passive;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class SparkGapElm
        extends CircuitElm
{
    double resistance, onresistance, offresistance, breakdown, holdcurrent;
    boolean state;

    public SparkGapElm()
    {
        offresistance = 1e9;
        onresistance = 1e3;
        breakdown = 1e3;
        holdcurrent = 0.001;
        state = false;
    }

    public SparkGapElm(int f, StringTokenizer st)
    {
        super(f);
        onresistance = new Double(st.nextToken()).doubleValue();
        offresistance = new Double(st.nextToken()).doubleValue();
        breakdown = new Double(st.nextToken()).doubleValue();
        holdcurrent = new Double(st.nextToken()).doubleValue();
    }

    public boolean nonLinear() {return true;}

    public int getDumpType() { return 187; }

    public String dump()
    {
        return super.dump() + " " + onresistance + " " + offresistance + " "
                + breakdown + " " + holdcurrent;
    }

    public void calculateCurrent()
    {
        double vd = volts[0] - volts[1];
        current = vd / resistance;
    }

    public void reset()
    {
        super.reset();
        state = false;
    }

    public void startIteration()
    {
        if (Math.abs(current) < holdcurrent) {
            state = false;
        }
        double vd = volts[0] - volts[1];
        if (Math.abs(vd) > breakdown) {
            state = true;
        }
    }

    public void doStep()
    {
        resistance = (state) ? onresistance : offresistance;
        sim.stampResistor(nodes[0], nodes[1], resistance);
    }

    public void stamp()
    {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "spark gap";
        getBasicInfo(arr);
        arr[3] = state ? "on" : "off";
        arr[4] = "Ron = " + getUnitText(onresistance, sim.ohmString);
        arr[5] = "Roff = " + getUnitText(offresistance, sim.ohmString);
        arr[6] = "Vbreakdown = " + getUnitText(breakdown, "V");
    }
}

