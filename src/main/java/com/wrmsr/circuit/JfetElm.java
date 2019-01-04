package com.wrmsr.circuit;

import java.util.StringTokenizer;

public class JfetElm
        extends MosfetElm
{

    public JfetElm(boolean pnpflag)
    {
        super(pnpflag);
        noDiagonal = true;
    }

    public JfetElm(int f, StringTokenizer st)
    {
        super(f, st);
        noDiagonal = true;
    }

    public int getDumpType() { return 'j'; }

    // these values are taken from Hayes+Horowitz p155
    public double getDefaultThreshold() { return -4; }

    public double getBeta() { return .00125; }

    public void getInfo(String arr[])
    {
        getFetInfo(arr, "JFET");
    }
}
