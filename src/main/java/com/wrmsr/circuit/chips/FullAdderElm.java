package com.wrmsr.circuit.chips;

import java.util.StringTokenizer;

import com.wrmsr.circuit.ChipElm;

public class FullAdderElm
        extends ChipElm
{
    public FullAdderElm() { }

    public FullAdderElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public boolean hasReset() {return false;}

    public String getChipName() { return "Full Adder"; }

    public void setupPins()
    {
        sizeX = 2;
        sizeY = 3;
        pins = new Pin[getPostCount()];

        pins[0] = new Pin(2, SIDE_E, "S");
        pins[0].output = true;
        pins[1] = new Pin(0, SIDE_E, "C");
        pins[1].output = true;
        pins[2] = new Pin(0, SIDE_W, "A");
        pins[3] = new Pin(1, SIDE_W, "B");
        pins[4] = new Pin(2, SIDE_W, "Cin");
    }

    public int getPostCount()
    {
        return 5;
    }

    public int getVoltageSourceCount() {return 2;}

    public void execute()
    {
        pins[0].value = (pins[2].value ^ pins[3].value) ^ pins[4].value;
        pins[1].value = (pins[2].value && pins[3].value) || (pins[2].value && pins[4].value) ||
                (pins[3].value && pins[4].value);
    }

    public int getDumpType() { return 196; }
}
