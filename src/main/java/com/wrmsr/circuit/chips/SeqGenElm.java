package com.wrmsr.circuit.chips;

import java.util.StringTokenizer;

import com.wrmsr.circuit.ChipElm;

// contributed by Edward Calver

class SeqGenElm
        extends ChipElm
{
    short data = 0;
    byte position = 0;
    boolean oneshot = false;
    double lastchangetime = 0;
    boolean clockstate = false;

    public SeqGenElm() { }

    public SeqGenElm(int f, StringTokenizer st)
    {
        super(f, st);
        data = (short) (new Integer(st.nextToken()).intValue());
        if (st.hasMoreTokens()) {
            oneshot = new Boolean(st.nextToken()).booleanValue();
            position = 8;
        }
    }

    public boolean hasReset() {return false;}

    public  String getChipName() { return "Sequence generator"; }

    public void setupPins()
    {
        sizeX = 2;
        sizeY = 2;
        pins = new Pin[getPostCount()];

        pins[0] = new Pin(0, SIDE_W, "");
        pins[0].clock = true;
        pins[1] = new Pin(1, SIDE_E, "Q");
        pins[1].output = true;
    }

    public int getPostCount()
    {
        return 2;
    }

    public int getVoltageSourceCount() {return 1;}

    public void GetNextBit()
    {
        if (((data >>> position) & 1) != 0) {
            pins[1].value = true;
        }
        else {
            pins[1].value = false;
        }
        position++;
    }

    public void execute()
    {
        if (oneshot) {
            if (sim.t - lastchangetime > 0.005) {
                if (position <= 8) {
                    GetNextBit();
                }
                lastchangetime = sim.t;
            }
        }
        if (pins[0].value && !clockstate) {
            clockstate = true;
            if (oneshot) {
                position = 0;
            }
            else {
                GetNextBit();
                if (position >= 8) {
                    position = 0;
                }
            }
        }
        if (!pins[0].value) {
            clockstate = false;
        }
    }

    public int getDumpType() { return 188; }

    public String dump()
    {
        return super.dump() + " " + data + " " + oneshot;
    }
}
