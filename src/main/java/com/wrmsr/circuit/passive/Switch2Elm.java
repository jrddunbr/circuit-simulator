package com.wrmsr.circuit.passive;

import java.util.StringTokenizer;

public class Switch2Elm
        extends SwitchElm
{
    static final int FLAG_CENTER_OFF = 1;
    final int openhs = 16;
    int link;

    public Switch2Elm()
    {
        super(false);
        noDiagonal = true;
    }

    public Switch2Elm(boolean mm)
    {
        super(mm);
        noDiagonal = true;
    }

    public Switch2Elm(int f, StringTokenizer st)
    {
        super(f, st);
        link = new Integer(st.nextToken()).intValue();
        noDiagonal = true;
    }

    public int getDumpType() { return 'S'; }

    public String dump()
    {
        return super.dump() + " " + link;
    }

    public int getPostCount() { return 3; }

    public void calculateCurrent()
    {
        if (position == 2) {
            current = 0;
        }
    }

    public void stamp()
    {
        if (position == 2) // in center?
        {
            return;
        }
        sim.stampVoltageSource(nodes[0], nodes[position + 1], voltSource, 0);
    }

    public int getVoltageSourceCount()
    {
        return (position == 2) ? 0 : 1;
    }

    public void toggle()
    {
        super.toggle();
        if (link != 0) {
            int i;
            for (i = 0; i != sim.elmList.size(); i++) {
                Object o = sim.elmList.elementAt(i);
                if (o instanceof Switch2Elm) {
                    Switch2Elm s2 = (Switch2Elm) o;
                    if (s2.link == link) {
                        s2.position = position;
                    }
                }
            }
        }
    }

    public boolean getConnection(int n1, int n2)
    {
        if (position == 2) {
            return false;
        }
        //TODO fix this. I have no idea what it does, but this function did not seem to be able to go anywhere in the old code.
        return true;
        //return comparePair(n1, n2, 0, 1 + position);
    }

    public void getInfo(String arr[])
    {
        arr[0] = (link == 0) ? "switch (SPDT)" : "switch (DPDT)";
        arr[1] = "I = " + getCurrentDText(getCurrent());
    }

    boolean hasCenterOff() { return (flags & FLAG_CENTER_OFF) != 0; }
}
