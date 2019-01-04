package com.wrmsr.circuit.chips;

import java.util.StringTokenizer;

import com.wrmsr.circuit.ChipElm;

class SevenSegElm
        extends ChipElm
{

    public SevenSegElm() {}

    public SevenSegElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public String getChipName() { return "7-segment driver/display"; }

    public void setupPins()
    {
        sizeX = 4;
        sizeY = 4;
        pins = new Pin[7];
        pins[0] = new Pin(0, SIDE_W, "a");
        pins[1] = new Pin(1, SIDE_W, "b");
        pins[2] = new Pin(2, SIDE_W, "c");
        pins[3] = new Pin(3, SIDE_W, "d");
        pins[4] = new Pin(1, SIDE_S, "e");
        pins[5] = new Pin(2, SIDE_S, "f");
        pins[6] = new Pin(3, SIDE_S, "g");
    }

    public int getPostCount() { return 7; }

    public int getVoltageSourceCount() { return 0; }

    public int getDumpType() { return 157; }
}
