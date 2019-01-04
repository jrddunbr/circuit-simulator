package com.wrmsr.circuit.active;

import java.util.StringTokenizer;

import com.wrmsr.circuit.CircuitElm;

class TunnelDiodeElm
        extends CircuitElm
{
    static final double pvp = .1;
    static final double pip = 4.7e-3;
    static final double pvv = .37;
    static final double pvt = .026;
    static final double pvpp = .525;
    static final double piv = 370e-6;
    final int hs = 8;
    double lastvoltdiff;

    public TunnelDiodeElm()
    {
        setup();
    }

    public TunnelDiodeElm(int f,
            StringTokenizer st)
    {
        super(f);
        setup();
    }

    public boolean nonLinear() { return true; }

    public void setup()
    {
    }

    public int getDumpType() { return 175; }

    public void reset()
    {
        lastvoltdiff = volts[0] = volts[1] = curcount = 0;
    }

    public double limitStep(double vnew, double vold)
    {
        // Prevent voltage changes of more than 1V when iterating.  Wow, I thought it would be
        // much harder than this to prevent convergence problems.
        if (vnew > vold + 1) {
            return vold + 1;
        }
        if (vnew < vold - 1) {
            return vold - 1;
        }
        return vnew;
    }

    public void stamp()
    {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
    }

    public void doStep()
    {
        double voltdiff = volts[0] - volts[1];
        if (Math.abs(voltdiff - lastvoltdiff) > .01) {
            sim.converged = false;
        }
        //System.out.println(voltdiff + " " + lastvoltdiff + " " + Math.abs(voltdiff-lastvoltdiff));
        voltdiff = limitStep(voltdiff, lastvoltdiff);
        lastvoltdiff = voltdiff;

        double i = pip * Math.exp(-pvpp / pvt) * (Math.exp(voltdiff / pvt) - 1) +
                pip * (voltdiff / pvp) * Math.exp(1 - voltdiff / pvp) +
                piv * Math.exp(voltdiff - pvv);

        double geq = pip * Math.exp(-pvpp / pvt) * Math.exp(voltdiff / pvt) / pvt +
                pip * Math.exp(1 - voltdiff / pvp) / pvp
                - Math.exp(1 - voltdiff / pvp) * pip * voltdiff / (pvp * pvp) +
                Math.exp(voltdiff - pvv) * piv;
        double nc = i - geq * voltdiff;
        sim.stampConductance(nodes[0], nodes[1], geq);
        sim.stampCurrentSource(nodes[0], nodes[1], nc);
    }

    public void calculateCurrent()
    {
        double voltdiff = volts[0] - volts[1];
        current = pip * Math.exp(-pvpp / pvt) * (Math.exp(voltdiff / pvt) - 1) +
                pip * (voltdiff / pvp) * Math.exp(1 - voltdiff / pvp) +
                piv * Math.exp(voltdiff - pvv);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "tunnel diode";
        arr[1] = "I = " + getCurrentText(getCurrent());
        arr[2] = "Vd = " + getVoltageText(getVoltageDiff());
        arr[3] = "P = " + getUnitText(getPower(), "W");
    }
}
