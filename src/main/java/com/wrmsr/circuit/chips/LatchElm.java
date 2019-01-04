package com.wrmsr.circuit.chips;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.ChipElm;

public class LatchElm
        extends ChipElm
{
    int loadPin;
    boolean lastLoad = false;

    public LatchElm() { }

    public LatchElm(int f, StringTokenizer st)
    {
        super(f, st);
    }

    public String getChipName() { return "Latch"; }

    public boolean needsBits() { return true; }

    public void setupPins()
    {
        sizeX = 2;
        sizeY = bits + 1;
        pins = new Pin[getPostCount()];
        int i;
        for (i = 0; i != bits; i++) {
            pins[i] = new Pin(bits - 1 - i, SIDE_W, "I" + i);
        }
        for (i = 0; i != bits; i++) {
            pins[i + bits] = new Pin(bits - 1 - i, SIDE_E, "O");
            pins[i + bits].output = true;
        }
        pins[loadPin = bits * 2] = new Pin(bits, SIDE_W, "Ld");
        allocNodes();
    }

    public void execute()
    {
        int i;
        if (pins[loadPin].value && !lastLoad) {
            for (i = 0; i != bits; i++) {
                pins[i + bits].value = pins[i].value;
            }
        }
        lastLoad = pins[loadPin].value;
    }

    public int getVoltageSourceCount() { return bits; }

    public int getPostCount() { return bits * 2 + 1; }

    public int getDumpType() { return 168; }
}
    
