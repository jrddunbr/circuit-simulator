package com.wrmsr.circuit.chips;

import java.util.StringTokenizer;

import com.wrmsr.circuit.ChipElm;

public class DFlipFlopElm
        extends ChipElm
{
    final int FLAG_RESET = 2;
    final int FLAG_SET = 4;

    public DFlipFlopElm() {}

    public DFlipFlopElm(int f, StringTokenizer st)
    {
        super(f, st);
        pins[2].value = !pins[1].value;
    }

    public boolean hasReset() { return (flags & FLAG_RESET) != 0 || hasSet(); }

    public boolean hasSet() { return (flags & FLAG_SET) != 0; }

    public String getChipName() { return "D flip-flop"; }

    public void setupPins()
    {
        sizeX = 2;
        sizeY = 3;
        pins = new Pin[getPostCount()];
        pins[0] = new Pin(0, SIDE_W, "D");
        pins[1] = new Pin(0, SIDE_E, "Q");
        pins[1].output = pins[1].state = true;
        pins[2] = new Pin(hasSet() ? 1 : 2, SIDE_E, "Q");
        pins[2].output = true;
        pins[2].lineOver = true;
        pins[3] = new Pin(1, SIDE_W, "");
        pins[3].clock = true;
        if (!hasSet()) {
            if (hasReset()) {
                pins[4] = new Pin(2, SIDE_W, "R");
            }
        }
        else {
            pins[5] = new Pin(2, SIDE_W, "S");
            pins[4] = new Pin(2, SIDE_E, "R");
        }
    }

    public int getPostCount()
    {
        return 4 + (hasReset() ? 1 : 0) + (hasSet() ? 1 : 0);
    }

    public int getVoltageSourceCount() { return 2; }

    public void reset()
    {
        super.reset();
        volts[2] = 5;
        pins[2].value = true;
    }

    public void execute()
    {
        if (pins[3].value && !lastClock) {
            pins[1].value = pins[0].value;
            pins[2].value = !pins[0].value;
        }
        if (hasSet() && pins[5].value) {
            pins[1].value = true;
            pins[2].value = false;
        }
        if (hasReset() && pins[4].value) {
            pins[1].value = false;
            pins[2].value = true;
        }
        lastClock = pins[3].value;
    }

    public int getDumpType() { return 155; }
}
