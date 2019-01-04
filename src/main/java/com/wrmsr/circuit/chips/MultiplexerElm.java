package com.wrmsr.circuit.chips;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.ChipElm;

// contributed by Edward Calver

public class MultiplexerElm
        extends ChipElm
{
    public MultiplexerElm() { }

    public MultiplexerElm(int f,
            StringTokenizer st)
    {
        super(f, st);
    }

    public boolean hasReset() {return false;}

    public String getChipName() { return "Multiplexer"; }

    public void setupPins()
    {
        sizeX = 3;
        sizeY = 5;
        pins = new Pin[getPostCount()];

        pins[0] = new Pin(0, SIDE_W, "I0");
        pins[1] = new Pin(1, SIDE_W, "I1");
        pins[2] = new Pin(2, SIDE_W, "I2");
        pins[3] = new Pin(3, SIDE_W, "I3");

        pins[4] = new Pin(1, SIDE_S, "S0");
        pins[5] = new Pin(2, SIDE_S, "S1");

        pins[6] = new Pin(0, SIDE_E, "Q");
        pins[6].output = true;
    }

    public int getPostCount()
    {
        return 7;
    }

    public int getVoltageSourceCount() {return 1;}

    public void execute()
    {
        int selectedvalue = 0;
        if (pins[4].value) {
            selectedvalue++;
        }
        if (pins[5].value) {
            selectedvalue += 2;
        }
        pins[6].value = pins[selectedvalue].value;
    }

    public int getDumpType() { return 184; }
}
