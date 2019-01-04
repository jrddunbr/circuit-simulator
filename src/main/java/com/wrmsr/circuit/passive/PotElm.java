package com.wrmsr.circuit.passive;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

public class PotElm
        extends CircuitElm
{
    double position, maxResistance, resistance1, resistance2;
    double current1, current2, current3;
    double curcount1, curcount2, curcount3;
    String sliderText;
    int bodyLen;

    public PotElm()
    {
        setup();
        maxResistance = 1000;
        position = .5;
        sliderText = "Resistance";
    }

    public PotElm(int f,  StringTokenizer st)
    {
        super(f);
        maxResistance = new Double(st.nextToken()).doubleValue();
        position = new Double(st.nextToken()).doubleValue();
        sliderText = st.nextToken();
        while (st.hasMoreTokens()) {
            sliderText += ' ' + st.nextToken();
        }
    }

    public void setup()
    {
    }

    public int getPostCount() { return 3; }

    public int getDumpType() { return 174; }

    public String dump()
    {
        return super.dump() + " " + maxResistance + " " +
                position + " " + sliderText;
    }

    public void calculateCurrent()
    {
        current1 = (volts[0] - volts[2]) / resistance1;
        current2 = (volts[1] - volts[2]) / resistance2;
        current3 = -current1 - current2;
    }

    public void stamp()
    {
        resistance1 = maxResistance * position;
        resistance2 = maxResistance * (1 - position);
        sim.stampResistor(nodes[0], nodes[2], resistance1);
        sim.stampResistor(nodes[2], nodes[1], resistance2);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "potentiometer";
        arr[1] = "Vd = " + getVoltageDText(getVoltageDiff());
        arr[2] = "R1 = " + getUnitText(resistance1, sim.ohmString);
        arr[3] = "R2 = " + getUnitText(resistance2, sim.ohmString);
        arr[4] = "I1 = " + getCurrentDText(current1);
        arr[5] = "I2 = " + getCurrentDText(current2);
    }
}

