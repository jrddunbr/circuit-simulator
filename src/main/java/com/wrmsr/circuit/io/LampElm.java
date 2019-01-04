package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;

public class LampElm
        extends CircuitElm
{
    final double roomTemp = 300;
    double resistance;
    double temp, nom_pow, nom_v, warmTime, coolTime;

    public LampElm()
    {
        temp = roomTemp;
        nom_pow = 100;
        nom_v = 120;
        warmTime = .4;
        coolTime = .4;
    }

    public LampElm(int f, StringTokenizer st)
    {
        super(f);
        temp = new Double(st.nextToken()).doubleValue();
        nom_pow = new Double(st.nextToken()).doubleValue();
        nom_v = new Double(st.nextToken()).doubleValue();
        warmTime = new Double(st.nextToken()).doubleValue();
        coolTime = new Double(st.nextToken()).doubleValue();
    }

    public String dump()
    {
        return super.dump() + " " + temp + " " + nom_pow + " " + nom_v +
                " " + warmTime + " " + coolTime;
    }

    public int getDumpType() { return 181; }

    public void reset()
    {
        super.reset();
        temp = roomTemp;
    }

    public void calculateCurrent()
    {
        current = (volts[0] - volts[1]) / resistance;
        //System.out.print(this + " res current set to " + current + "\n");
    }

    public void stamp()
    {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
    }

    public boolean nonLinear() { return true; }

    public void startIteration()
    {
        // based on http://www.intusoft.com/nlpdf/nl11.pdf
        double nom_r = nom_v * nom_v / nom_pow;
        // this formula doesn't work for values over 5390
        double tp = (temp > 5390) ? 5390 : temp;
        resistance = nom_r * (1.26104 -
                4.90662 * Math.sqrt(17.1839 / tp - 0.00318794) -
                7.8569 / (tp - 187.56));
        double cap = 1.57e-4 * nom_pow;
        double capw = cap * warmTime / .4;
        double capc = cap * coolTime / .4;
        //System.out.println(nom_r + " " + (resistance/nom_r));
        temp += getPower() * sim.timeStep / capw;
        double cr = 2600 / nom_pow;
        temp -= sim.timeStep * (temp - roomTemp) / (capc * cr);
        //System.out.println(capw + " " + capc + " " + temp + " " +resistance);
    }

    public void doStep()
    {
        sim.stampResistor(nodes[0], nodes[1], resistance);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "lamp";
        getBasicInfo(arr);
        arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
        arr[4] = "P = " + getUnitText(getPower(), "W");
        arr[5] = "T = " + ((int) temp) + " K";
    }
}
