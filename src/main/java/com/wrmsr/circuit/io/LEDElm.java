package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.active.DiodeElm;

public class LEDElm
        extends DiodeElm
{
    double colorR, colorG, colorB;

    public LEDElm()
    {
        setup();
        colorR = 1;
        colorG = colorB = 0;
    }

    public LEDElm(int f, StringTokenizer st)
    {
        super(f, st);
        setup();
        colorR = new Double(st.nextToken()).doubleValue();
        colorG = new Double(st.nextToken()).doubleValue();
        colorB = new Double(st.nextToken()).doubleValue();
    }

    public int getDumpType() { return 162; }

    public String dump()
    {
        return super.dump() + " " + colorR + " " + colorG + " " + colorB;
    }

    public void getInfo(String arr[])
    {
        super.getInfo(arr);
        arr[0] = "LED";
    }
}
