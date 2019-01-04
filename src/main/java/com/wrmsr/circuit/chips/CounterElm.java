package com.wrmsr.circuit.chips;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.ChipElm;

public class CounterElm
        extends ChipElm
{
    final int FLAG_ENABLE = 2;
    boolean invertreset = false;

    public CounterElm() { }

    public CounterElm(int f, StringTokenizer st)
    {
        super(f, st);
        if (st.hasMoreTokens()) {
            invertreset = new Boolean(st.nextToken()).booleanValue();
        }
        else {
            invertreset = true;
        }
        pins[1].bubble = invertreset;
    }

    public String dump()
    {
        return super.dump() + " " + invertreset;
    }

    public boolean needsBits() { return true; }

    public String getChipName() { return "Counter"; }

    public void setupPins()
    {
        sizeX = 2;
        sizeY = bits > 2 ? bits : 2;
        pins = new Pin[getPostCount()];
        pins[0] = new Pin(0, SIDE_W, "");
        pins[0].clock = true;
        pins[1] = new Pin(sizeY - 1, SIDE_W, "R");
        pins[1].bubble = invertreset;
        int i;
        for (i = 0; i != bits; i++) {
            int ii = i + 2;
            pins[ii] = new Pin(i, SIDE_E, "Q" + (bits - i - 1));
            pins[ii].output = pins[ii].state = true;
        }
        if (hasEnable()) {
            pins[bits + 2] = new Pin(sizeY - 2, SIDE_W, "En");
        }
        allocNodes();
    }

    public int getPostCount()
    {
        if (hasEnable()) {
            return bits + 3;
        }
        return bits + 2;
    }

    public boolean hasEnable() { return (flags & FLAG_ENABLE) != 0; }

    public  int getVoltageSourceCount() { return bits; }

    public void execute()
    {
        boolean en = true;
        if (hasEnable()) {
            en = pins[bits + 2].value;
        }
        if (pins[0].value && !lastClock && en) {
            int i;
            for (i = bits - 1; i >= 0; i--) {
                int ii = i + 2;
                if (!pins[ii].value) {
                    pins[ii].value = true;
                    break;
                }
                pins[ii].value = false;
            }
        }
        if (!pins[1].value == invertreset) {
            int i;
            for (i = 0; i != bits; i++) {
                pins[i + 2].value = false;
            }
        }
        lastClock = pins[0].value;
    }

    public int getDumpType() { return 164; }
}
