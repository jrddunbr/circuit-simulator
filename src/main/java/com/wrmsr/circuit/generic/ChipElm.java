package com.wrmsr.circuit.generic;

import java.util.StringTokenizer;

import com.wrmsr.circuit.chips.DecadeElm;

public abstract class ChipElm
        extends CircuitElm
{
    public final int FLAG_SMALL = 1;
    public final int FLAG_FLIP_X = 1024;
    public final int FLAG_FLIP_Y = 2048;
    public final int SIDE_N = 0;
    public final int SIDE_S = 1;
    public final int SIDE_W = 2;
    public final int SIDE_E = 3;
    public int csize, cspc, cspc2;
    public int bits;
    public Pin pins[];
    public int sizeX, sizeY;
    public boolean lastClock;

    public ChipElm()
    {
        if (needsBits()) {
            bits = (this instanceof DecadeElm) ? 10 : 4;
        }
        noDiagonal = true;
        setupPins();
    }

    public ChipElm(int f, StringTokenizer st)
    {
        super(f);
        if (needsBits()) {
            bits = new Integer(st.nextToken()).intValue();
        }
        noDiagonal = true;
        setupPins();
        setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
        int i;
        for (i = 0; i != getPostCount(); i++) {
            if (pins[i].state) {
                volts[i] = new Double(st.nextToken()).doubleValue();
                pins[i].value = volts[i] > 2.5;
            }
        }
    }

    public boolean needsBits() { return false; }

    public void setSize(int s)
    {
        csize = s;
        cspc = 8 * s;
        cspc2 = cspc * 2;
        flags &= ~FLAG_SMALL;
        flags |= (s == 1) ? FLAG_SMALL : 0;
    }

    public abstract void setupPins();

    public abstract int getVoltageSourceCount(); // output count

    public void setVoltageSource(int j, int vs)
    {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (p.output && j-- == 0) {
                p.voltSource = vs;
                return;
            }
        }
        System.out.println("setVoltageSource failed for " + this);
    }

    public void stamp()
    {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (p.output) {
                sim.stampVoltageSource(0, nodes[i], p.voltSource);
            }
        }
    }

    public void execute() {}

    public void doStep()
    {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (!p.output) {
                p.value = volts[i] > 2.5;
            }
        }
        execute();
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (p.output) {
                sim.updateVoltageSource(0, nodes[i], p.voltSource,
                        p.value ? 5 : 0);
            }
        }
    }

    public void reset()
    {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            pins[i].value = false;
            pins[i].curcount = 0;
            volts[i] = 0;
        }
        lastClock = false;
    }

    public String dump()
    {
        int t = getDumpType();
        String s = super.dump();
        if (needsBits()) {
            s += " " + bits;
        }
        int i;
        for (i = 0; i != getPostCount(); i++) {
            if (pins[i].state) {
                s += " " + volts[i];
            }
        }
        return s;
    }

    public void getInfo(String arr[])
    {
        arr[0] = getChipName();
        int i, a = 1;
        for (i = 0; i != getPostCount(); i++) {
            Pin p = pins[i];
            if (arr[a] != null) {
                arr[a] += "; ";
            }
            else {
                arr[a] = "";
            }
            String t = p.text;
            if (p.lineOver) {
                t += '\'';
            }
            if (p.clock) {
                t = "Clk";
            }
            arr[a] += t + " = " + getVoltageText(volts[i]);
            if (i % 2 == 1) {
                a++;
            }
        }
    }

    public void setCurrent(int x, double c)
    {
        int i;
        for (i = 0; i != getPostCount(); i++) {
            if (pins[i].output && pins[i].voltSource == x) {
                pins[i].current = c;
            }
        }
    }

    public String getChipName() { return "chip"; }

    public boolean getConnection(int n1, int n2) { return false; }

    public boolean hasGroundConnection(int n1)
    {
        return pins[n1].output;
    }

    public class Pin
    {
        public int pos, side, voltSource, bubbleX, bubbleY;
        public String text;
        public boolean lineOver, bubble, clock, output, value, state;
        public double curcount, current;

        public Pin(int p, int s, String t)
        {
            pos = p;
            side = s;
            text = t;
        }
    }
}

