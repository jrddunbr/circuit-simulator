package com.wrmsr.circuit.passive;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;

// 0 = switch
// 1 = switch end 1
// 2 = switch end 2
// ...
// 3n   = coil
// 3n+1 = coil
// 3n+2 = end of coil resistor

public class RelayElm
        extends CircuitElm
{
    final int nSwitch0 = 0;
    final int nSwitch1 = 1;
    final int nSwitch2 = 2;
    final int FLAG_SWAP_COIL = 1;
    double inductance;
    Inductor ind;
    double r_on, r_off, onCurrent;
    double coilCurrent, switchCurrent[], coilCurCount, switchCurCount[];
    double d_position, coilR;
    int i_position;
    int poleCount;
    int openhs;
    int nCoil1, nCoil2, nCoil3;

    public RelayElm()
    {
        ind = new Inductor(sim);
        inductance = .2;
        ind.setup(inductance, 0, Inductor.FLAG_BACK_EULER);
        noDiagonal = true;
        onCurrent = .02;
        r_on = .05;
        r_off = 1e6;
        coilR = 20;
        coilCurrent = coilCurCount = 0;
        poleCount = 1;
        setupPoles();
    }

    public RelayElm(int f, StringTokenizer st)
    {
        super(f);
        poleCount = new Integer(st.nextToken()).intValue();
        inductance = new Double(st.nextToken()).doubleValue();
        coilCurrent = new Double(st.nextToken()).doubleValue();
        r_on = new Double(st.nextToken()).doubleValue();
        r_off = new Double(st.nextToken()).doubleValue();
        onCurrent = new Double(st.nextToken()).doubleValue();
        coilR = new Double(st.nextToken()).doubleValue();
        noDiagonal = true;
        ind = new Inductor(sim);
        ind.setup(inductance, coilCurrent, Inductor.FLAG_BACK_EULER);
        setupPoles();
    }

    public void setupPoles()
    {
        nCoil1 = 3 * poleCount;
        nCoil2 = nCoil1 + 1;
        nCoil3 = nCoil1 + 2;
        if (switchCurrent == null || switchCurrent.length != poleCount) {
            switchCurrent = new double[poleCount];
            switchCurCount = new double[poleCount];
        }
    }

    public int getDumpType() { return 178; }

    public String dump()
    {
        return super.dump() + " " + poleCount + " " +
                inductance + " " + coilCurrent + " " +
                r_on + " " + r_off + " " + onCurrent + " " + coilR;
    }

    public int getPostCount() { return 2 + poleCount * 3; }

    public int getInternalNodeCount() { return 1; }

    public void reset()
    {
        super.reset();
        ind.reset();
        coilCurrent = coilCurCount = 0;
        int i;
        for (i = 0; i != poleCount; i++) {
            switchCurrent[i] = switchCurCount[i] = 0;
        }
    }

    public void stamp()
    {
        // inductor from coil post 1 to internal node
        ind.stamp(nodes[nCoil1], nodes[nCoil3]);
        // resistor from internal node to coil post 2
        sim.stampResistor(nodes[nCoil3], nodes[nCoil2], coilR);

        int i;
        for (i = 0; i != poleCount * 3; i++) {
            sim.stampNonLinear(nodes[nSwitch0 + i]);
        }
    }

    public void startIteration()
    {
        ind.startIteration(volts[nCoil1] - volts[nCoil3]);

        // magic value to balance operate speed with reset speed semi-realistically
        double magic = 1.3;
        double pmult = Math.sqrt(magic + 1);
        double p = coilCurrent * pmult / onCurrent;
        d_position = Math.abs(p * p) - 1.3;
        if (d_position < 0) {
            d_position = 0;
        }
        if (d_position > 1) {
            d_position = 1;
        }
        if (d_position < .1) {
            i_position = 0;
        }
        else if (d_position > .9) {
            i_position = 1;
        }
        else {
            i_position = 2;
        }
        //System.out.println("ind " + this + " " + current + " " + voltdiff);
    }

    // we need this to be able to change the matrix for each step
    public boolean nonLinear() { return true; }

    public void doStep()
    {
        double voltdiff = volts[nCoil1] - volts[nCoil3];
        ind.doStep(voltdiff);
        int p;
        for (p = 0; p != poleCount * 3; p += 3) {
            sim.stampResistor(nodes[nSwitch0 + p], nodes[nSwitch1 + p],
                    i_position == 0 ? r_on : r_off);
            sim.stampResistor(nodes[nSwitch0 + p], nodes[nSwitch2 + p],
                    i_position == 1 ? r_on : r_off);
        }
    }

    public void calculateCurrent()
    {
        double voltdiff = volts[nCoil1] - volts[nCoil3];
        coilCurrent = ind.calculateCurrent(voltdiff);

        // actually this isn't correct, since there is a small amount
        // of current through the switch when off
        int p;
        for (p = 0; p != poleCount; p++) {
            if (i_position == 2) {
                switchCurrent[p] = 0;
            }
            else {
                switchCurrent[p] =
                        (volts[nSwitch0 + p * 3] - volts[nSwitch1 + p * 3 + i_position]) / r_on;
            }
        }
    }

    public void getInfo(String arr[])
    {
        arr[0] = i_position == 0 ? "relay (off)" :
                i_position == 1 ? "relay (on)" : "relay";
        int i;
        int ln = 1;
        for (i = 0; i != poleCount; i++) {
            arr[ln++] = "I" + (i + 1) + " = " + getCurrentDText(switchCurrent[i]);
        }
        arr[ln++] = "coil I = " + getCurrentDText(coilCurrent);
        arr[ln++] = "coil Vd = " +
                getVoltageDText(volts[nCoil1] - volts[nCoil2]);
    }

    public boolean getConnection(int n1, int n2)
    {
        return (n1 / 3 == n2 / 3);
    }
}
    
