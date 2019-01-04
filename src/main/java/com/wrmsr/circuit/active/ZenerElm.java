package com.wrmsr.circuit.active;

import java.util.StringTokenizer;

// Zener code contributed by J. Mike Rollins
// http://www.camotruck.net/rollins/simulator.html
public class ZenerElm
        extends DiodeElm
{
    final int hs = 8;
    final double default_zvoltage = 5.6;

    public ZenerElm()
    {
        zvoltage = default_zvoltage;
        setup();
    }

    public ZenerElm(int f, StringTokenizer st)
    {
        super(f, st);
        zvoltage = new Double(st.nextToken()).doubleValue();
        setup();
    }

    public void setup()
    {
        diode.leakage = 5e-6; // 1N4004 is 5.0 uAmp
        super.setup();
    }

    public int getDumpType() { return 'z'; }

    public String dump()
    {
        return super.dump() + " " + zvoltage;
    }

    public void getInfo(String arr[])
    {
        super.getInfo(arr);
        arr[0] = "Zener diode";
        arr[5] = "Vz = " + getVoltageText(zvoltage);
    }
}
