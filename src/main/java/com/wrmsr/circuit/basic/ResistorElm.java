package com.wrmsr.circuit.basic;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;

public class ResistorElm
        extends CircuitElm
{
    public double resistance;

    public ResistorElm()
    {
        resistance = 100;
    }

    public ResistorElm(StringTokenizer st)
    {
        resistance = new Double(st.nextToken()).doubleValue();
    }

    public int getDumpType() { return 'r'; }

    public String dump()
    {
        return super.dump() + " " + resistance;
    }

    public void calculateCurrent()
    {
        current = (volts[0] - volts[1]) / resistance;
        //System.out.print(this + " res current set to " + current + "\n");
    }

    public void stamp()
    {
        sim.stampResistor(nodes[0], nodes[1], resistance);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "resistor";
        getBasicInfo(arr);
        arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
        arr[4] = "P = " + getUnitText(getPower(), "W");
    }

}
