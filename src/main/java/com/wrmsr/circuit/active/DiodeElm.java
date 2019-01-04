package com.wrmsr.circuit.active;

import java.util.StringTokenizer;

import com.wrmsr.circuit.generic.CircuitElm;
import com.wrmsr.circuit.generic.Diode;

public class DiodeElm
        extends CircuitElm
{
    static final int FLAG_FWDROP = 1;
    final double defaultdrop = .805904783;
    final int hs = 8;
    Diode diode;
    double fwdrop, zvoltage;

    public DiodeElm()
    {
        diode = new Diode(sim);
        fwdrop = defaultdrop;
        zvoltage = 0;
        setup();
    }

    public DiodeElm(int f, StringTokenizer st)
    {
        super(f);
        diode = new Diode(sim);
        fwdrop = defaultdrop;
        zvoltage = 0;
        if ((f & FLAG_FWDROP) > 0) {
            try {
                fwdrop = new Double(st.nextToken()).doubleValue();
            }
            catch (Exception e) {
            }
        }
        setup();
    }

    public boolean nonLinear() { return true; }

    public void setup()
    {
        diode.setup(fwdrop, zvoltage);
    }

    public int getDumpType() { return 'd'; }

    public String dump()
    {
        flags |= FLAG_FWDROP;
        return super.dump() + " " + fwdrop;
    }

    public void reset()
    {
        diode.reset();
        volts[0] = volts[1] = curcount = 0;
    }

    public void stamp() { diode.stamp(nodes[0], nodes[1]); }

    public void doStep()
    {
        diode.doStep(volts[0] - volts[1]);
    }

    public void calculateCurrent()
    {
        current = diode.calculateCurrent(volts[0] - volts[1]);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "diode";
        arr[1] = "I = " + getCurrentText(getCurrent());
        arr[2] = "Vd = " + getVoltageText(getVoltageDiff());
        arr[3] = "P = " + getUnitText(getPower(), "W");
        arr[4] = "Vf = " + getVoltageText(fwdrop);
    }
}
