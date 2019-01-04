package com.wrmsr.circuit.active;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

// contributed by Edward Calver

public class TriStateElm
        extends CircuitElm
{
    public double resistance, r_on, r_off;
    public boolean open;

    public TriStateElm()
    {
        r_on = 0.1;
        r_off = 1e10;
    }

    public TriStateElm(int f,
            StringTokenizer st)
    {
        super(f);
        r_on = 0.1;
        r_off = 1e10;
        try {
            r_on = new Double(st.nextToken()).doubleValue();
            r_off = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
        }
    }

    public String dump()
    {
        return super.dump() + " " + r_on + " " + r_off;
    }

    public int getDumpType() { return 180; }

    public void calculateCurrent()
    {
        current = (volts[0] - volts[1]) / resistance;
    }

    // we need this to be able to change the matrix for each step
    public boolean nonLinear() { return true; }

    public void stamp()
    {
        sim.stampVoltageSource(0, nodes[3], voltSource);
        sim.stampNonLinear(nodes[3]);
        sim.stampNonLinear(nodes[1]);
    }

    public void doStep()
    {
        open = (volts[2] < 2.5);
        resistance = (open) ? r_off : r_on;
        sim.stampResistor(nodes[3], nodes[1], resistance);
        sim.updateVoltageSource(0, nodes[3], voltSource, volts[0] > 2.5 ? 5 : 0);
    }

    public int getPostCount() { return 4; }

    public int getVoltageSourceCount() { return 1; }

    public void getInfo(String arr[])
    {
        arr[0] = "tri-state buffer";
        arr[1] = open ? "open" : "closed";
        arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
        arr[3] = "I = " + getCurrentDText(getCurrent());
        arr[4] = "Vc = " + getVoltageText(volts[2]);
    }
    // we have to just assume current will flow either way, even though that
    // might cause singular matrix errors

//     0---3----------1
//            /
//           2

    public boolean getConnection(int n1, int n2)
    {
        if ((n1 == 1 && n2 == 3) || (n1 == 3 && n2 == 1)) {
            return true;
        }
        return false;
    }
}

