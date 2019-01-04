package com.wrmsr.circuit.chips;

import java.util.StringTokenizer;

import com.wrmsr.circuit.ChipElm;

public class MonostableElm
        extends ChipElm
{

    //Used to detect rising edge
    private boolean prevInputValue = false;
    private boolean retriggerable = false;
    private boolean triggered = false;
    private double lastRisingEdge = 0;
    private double delay = 0.01;

    public MonostableElm() { }

    public MonostableElm(int f, StringTokenizer st)
    {
        super(f, st);
        retriggerable = new Boolean(st.nextToken()).booleanValue();
        delay = new Double(st.nextToken()).doubleValue();
    }

    public String getChipName() { return "Monostable"; }

    public void setupPins()
    {
        sizeX = 2;
        sizeY = 2;
        pins = new Pin[getPostCount()];
        pins[0] = new Pin(0, SIDE_W, "");
        pins[0].clock = true;
        pins[1] = new Pin(0, SIDE_E, "Q");
        pins[1].output = true;
        pins[2] = new Pin(1, SIDE_E, "Q");
        pins[2].output = true;
        pins[2].lineOver = true;
    }

    public int getPostCount()
    {
        return 3;
    }

    public int getVoltageSourceCount() { return 2; }

    public void execute()
    {

        if (pins[0].value && prevInputValue != pins[0].value && (retriggerable || !triggered)) {
            lastRisingEdge = sim.t;
            pins[1].value = true;
            pins[2].value = false;
            triggered = true;
        }

        if (triggered && sim.t > lastRisingEdge + delay) {
            pins[1].value = false;
            pins[2].value = true;
            triggered = false;
        }
        prevInputValue = pins[0].value;
    }

    public String dump()
    {
        return super.dump() + " " + retriggerable + " " + delay;
    }

    public int getDumpType() { return 194; }
}
