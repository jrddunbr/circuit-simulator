package com.wrmsr.circuit;

import java.util.StringTokenizer;

public class MosfetElm
        extends CircuitElm
{
    public final int hs = 16;
    public int pnp;
    public int FLAG_PNP = 1;
    public int FLAG_SHOWVT = 2;
    public int FLAG_DIGITAL = 4;
    public double vt;
    public int pcircler;
    public double lastv1, lastv2;
    public double ids;
    public int mode = 0;
    public double gm = 0;

    public MosfetElm(boolean pnpflag)
    {
        pnp = (pnpflag) ? -1 : 1;
        flags = (pnpflag) ? FLAG_PNP : 0;
        noDiagonal = true;
        vt = getDefaultThreshold();
    }

    public MosfetElm(int f, StringTokenizer st)
    {
        super(f);
        pnp = ((f & FLAG_PNP) != 0) ? -1 : 1;
        noDiagonal = true;
        vt = getDefaultThreshold();
        try {
            vt = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
        }
    }

    public double getDefaultThreshold() { return 1.5; }

    public double getBeta() { return .02; }

    public boolean nonLinear() { return true; }

    public boolean drawDigital() { return (flags & FLAG_DIGITAL) != 0; }

    public void reset()
    {
        lastv1 = lastv2 = volts[0] = volts[1] = volts[2] = curcount = 0;
    }

    public String dump()
    {
        return super.dump() + " " + vt;
    }

    public int getDumpType() { return 'f'; }


    public double getCurrent() { return ids; }

    public double getPower() { return ids * (volts[2] - volts[1]); }

    public int getPostCount() { return 3; }

    public void stamp()
    {
        sim.stampNonLinear(nodes[1]);
        sim.stampNonLinear(nodes[2]);
    }

    public void doStep()
    {
        double vs[] = new double[3];
        vs[0] = volts[0];
        vs[1] = volts[1];
        vs[2] = volts[2];
        if (vs[1] > lastv1 + .5) {
            vs[1] = lastv1 + .5;
        }
        if (vs[1] < lastv1 - .5) {
            vs[1] = lastv1 - .5;
        }
        if (vs[2] > lastv2 + .5) {
            vs[2] = lastv2 + .5;
        }
        if (vs[2] < lastv2 - .5) {
            vs[2] = lastv2 - .5;
        }
        int source = 1;
        int drain = 2;
        if (pnp * vs[1] > pnp * vs[2]) {
            source = 2;
            drain = 1;
        }
        int gate = 0;
        double vgs = vs[gate] - vs[source];
        double vds = vs[drain] - vs[source];
        if (Math.abs(lastv1 - vs[1]) > .01 ||
                Math.abs(lastv2 - vs[2]) > .01) {
            sim.converged = false;
        }
        lastv1 = vs[1];
        lastv2 = vs[2];
        double realvgs = vgs;
        double realvds = vds;
        vgs *= pnp;
        vds *= pnp;
        ids = 0;
        gm = 0;
        double Gds = 0;
        double beta = getBeta();
        if (vgs > .5 && this instanceof JfetElm) {
            sim.stop("JFET is reverse biased!", this);
            return;
        }
        if (vgs < vt) {
            // should be all zero, but that causes a singular matrix,
            // so instead we treat it as a large resistor
            Gds = 1e-8;
            ids = vds * Gds;
            mode = 0;
        }
        else if (vds < vgs - vt) {
            // linear
            ids = beta * ((vgs - vt) * vds - vds * vds * .5);
            gm = beta * vds;
            Gds = beta * (vgs - vds - vt);
            mode = 1;
        }
        else {
            // saturation; Gds = 0
            gm = beta * (vgs - vt);
            // use very small Gds to avoid nonconvergence
            Gds = 1e-8;
            ids = .5 * beta * (vgs - vt) * (vgs - vt) + (vds - (vgs - vt)) * Gds;
            mode = 2;
        }
        double rs = -pnp * ids + Gds * realvds + gm * realvgs;
        //System.out.println("M " + vds + " " + vgs + " " + ids + " " + gm + " "+ Gds + " " + volts[0] + " " + volts[1] + " " + volts[2] + " " + source + " " + rs + " " + this);
        sim.stampMatrix(nodes[drain], nodes[drain], Gds);
        sim.stampMatrix(nodes[drain], nodes[source], -Gds - gm);
        sim.stampMatrix(nodes[drain], nodes[gate], gm);

        sim.stampMatrix(nodes[source], nodes[drain], -Gds);
        sim.stampMatrix(nodes[source], nodes[source], Gds + gm);
        sim.stampMatrix(nodes[source], nodes[gate], -gm);

        sim.stampRightSide(nodes[drain], rs);
        sim.stampRightSide(nodes[source], -rs);
        if (source == 2 && pnp == 1 ||
                source == 1 && pnp == -1) {
            ids = -ids;
        }
    }

    public void getFetInfo(String arr[], String n)
    {
        arr[0] = ((pnp == -1) ? "p-" : "n-") + n;
        arr[0] += " (Vt = " + getVoltageText(pnp * vt) + ")";
        arr[1] = ((pnp == 1) ? "Ids = " : "Isd = ") + getCurrentText(ids);
        arr[2] = "Vgs = " + getVoltageText(volts[0] - volts[pnp == -1 ? 2 : 1]);
        arr[3] = ((pnp == 1) ? "Vds = " : "Vsd = ") + getVoltageText(volts[2] - volts[1]);
        arr[4] = (mode == 0) ? "off" :
                (mode == 1) ? "linear" : "saturation";
        arr[5] = "gm = " + getUnitText(gm, "A/V");
    }

    public void getInfo(String arr[])
    {
        getFetInfo(arr, "MOSFET");
    }

    public boolean canViewInScope() { return true; }

    public double getVoltageDiff() { return volts[2] - volts[1]; }

    public boolean getConnection(int n1, int n2)
    {
        return !(n1 == 0 || n2 == 0);
    }
}
