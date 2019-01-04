package com.wrmsr.circuit.io;

import java.util.StringTokenizer;

import com.wrmsr.circuit.ChipElm;

public class LEDMatrixElm
        extends ChipElm
{

    private static final int size = 8;
    private static final double resistance = 100;
    private boolean negateRows = false;
    private boolean negateColumns = false;
    private double colorR = 1.0;
    private double colorG = 0.0;
    private double colorB = 0.0;

    public LEDMatrixElm() {  }

    public LEDMatrixElm(int f, StringTokenizer st)
    {
        super(f, st);
        negateRows = new Boolean(st.nextToken()).booleanValue();
        negateColumns = new Boolean(st.nextToken()).booleanValue();
        colorR = new Double(st.nextToken()).doubleValue();
        colorG = new Double(st.nextToken()).doubleValue();
        colorB = new Double(st.nextToken()).doubleValue();
    }

    public String getChipName() { return "LED Matrix"; }

    public void setupPins()
    {
        sizeX = 8;
        sizeY = 8;
        pins = new Pin[16];
        pins[0] = new Pin(0, SIDE_W, "");
        pins[1] = new Pin(1, SIDE_W, "");
        pins[2] = new Pin(2, SIDE_W, "");
        pins[3] = new Pin(3, SIDE_W, "");
        pins[4] = new Pin(4, SIDE_W, "");
        pins[5] = new Pin(5, SIDE_W, "");
        pins[6] = new Pin(6, SIDE_W, "");
        pins[7] = new Pin(7, SIDE_W, "");

        pins[8] = new Pin(0, SIDE_S, "");
        pins[9] = new Pin(1, SIDE_S, "");
        pins[10] = new Pin(2, SIDE_S, "");
        pins[11] = new Pin(3, SIDE_S, "");
        pins[12] = new Pin(4, SIDE_S, "");
        pins[13] = new Pin(5, SIDE_S, "");
        pins[14] = new Pin(6, SIDE_S, "");
        pins[15] = new Pin(7, SIDE_S, "");
    }

    public int getPostCount() {return 16;}

    public int getVoltageSourceCount() { return 0; }

    public int getDumpType() { return 207;}

    public String dump()
    {
        return super.dump() + " " + negateRows + " " + negateColumns + " " + colorR + " " + colorG + " " + colorB;
    }
}
    
