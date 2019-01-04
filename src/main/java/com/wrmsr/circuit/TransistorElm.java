package com.wrmsr.circuit;

import java.util.StringTokenizer;

public class TransistorElm
        extends CircuitElm
{
    public static final double leakage = 1e-13; // 1e-6;
    public static final double vt = .025;
    public static final double vdcoef = 1 / vt;
    public static final double rgain = .5;
    public final int FLAG_FLIP = 1;
    public int pnp;
    public double beta;
    public double fgain;
    public double gmin;
    public double ic, ie, ib, curcount_c, curcount_e, curcount_b;
    public double vcrit;
    public double lastvbc, lastvbe;

    public TransistorElm(boolean pnpflag)
    {
        pnp = (pnpflag) ? -1 : 1;
        beta = 100;
        setup();
    }

    public TransistorElm(int f, StringTokenizer st)
    {
        super(f);
        pnp = new Integer(st.nextToken()).intValue();
        beta = 100;
        try {
            lastvbe = new Double(st.nextToken()).doubleValue();
            lastvbc = new Double(st.nextToken()).doubleValue();
            volts[0] = 0;
            volts[1] = -lastvbe;
            volts[2] = -lastvbc;
            beta = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
        }
        setup();
    }

    public void setup()
    {
        vcrit = vt * Math.log(vt / (Math.sqrt(2) * leakage));
        fgain = beta / (beta + 1);
        noDiagonal = true;
    }

    public boolean nonLinear() { return true; }

    public void reset()
    {
        volts[0] = volts[1] = volts[2] = 0;
        lastvbc = lastvbe = curcount_c = curcount_e = curcount_b = 0;
    }

    public int getDumpType() { return 't'; }

    public String dump()
    {
        return super.dump() + " " + pnp + " " + (volts[0] - volts[1]) + " " +
                (volts[0] - volts[2]) + " " + beta;
    }

    public int getPostCount() { return 3; }

    public double getPower()
    {
        return (volts[0] - volts[2]) * ib + (volts[1] - volts[2]) * ic;
    }

    public double limitStep(double vnew, double vold)
    {
        double arg;
        double oo = vnew;

        if (vnew > vcrit && Math.abs(vnew - vold) > (vt + vt)) {
            if (vold > 0) {
                arg = 1 + (vnew - vold) / vt;
                if (arg > 0) {
                    vnew = vold + vt * Math.log(arg);
                }
                else {
                    vnew = vcrit;
                }
            }
            else {
                vnew = vt * Math.log(vnew / vt);
            }
            sim.converged = false;
            //System.out.println(vnew + " " + oo + " " + vold);
        }
        return (vnew);
    }

    public void stamp()
    {
        sim.stampNonLinear(nodes[0]);
        sim.stampNonLinear(nodes[1]);
        sim.stampNonLinear(nodes[2]);
    }

    public void doStep()
    {
        double vbc = volts[0] - volts[1]; // typically negative
        double vbe = volts[0] - volts[2]; // typically positive
        if (Math.abs(vbc - lastvbc) > .01 || // .01
                Math.abs(vbe - lastvbe) > .01) {
            sim.converged = false;
        }
        gmin = 0;
        if (sim.subIterations > 100) {
            // if we have trouble converging, put a conductance in parallel with all P-N junctions.
            // Gradually increase the conductance value for each iteration.
            gmin = Math.exp(-9 * Math.log(10) * (1 - sim.subIterations / 3000.));
            if (gmin > .1) {
                gmin = .1;
            }
        }
        //System.out.print("T " + vbc + " " + vbe + "\n");
        vbc = pnp * limitStep(pnp * vbc, pnp * lastvbc);
        vbe = pnp * limitStep(pnp * vbe, pnp * lastvbe);
        lastvbc = vbc;
        lastvbe = vbe;
        double pcoef = vdcoef * pnp;
        double expbc = Math.exp(vbc * pcoef);
        /*if (expbc > 1e13 || Double.isInfinite(expbc))
	      expbc = 1e13;*/
        double expbe = Math.exp(vbe * pcoef);
        if (expbe < 1) {
            expbe = 1;
        }
	    /*if (expbe > 1e13 || Double.isInfinite(expbe))
	      expbe = 1e13;*/
        ie = pnp * leakage * (-(expbe - 1) + rgain * (expbc - 1));
        ic = pnp * leakage * (fgain * (expbe - 1) - (expbc - 1));
        ib = -(ie + ic);
        //System.out.println("gain " + ic/ib);
        //System.out.print("T " + vbc + " " + vbe + " " + ie + " " + ic + "\n");
        double gee = -leakage * vdcoef * expbe;
        double gec = rgain * leakage * vdcoef * expbc;
        double gce = -gee * fgain;
        double gcc = -gec * (1 / rgain);

	    /*System.out.print("gee = " + gee + "\n");
	    System.out.print("gec = " + gec + "\n");
	    System.out.print("gce = " + gce + "\n");
	    System.out.print("gcc = " + gcc + "\n");
	    System.out.print("gce+gcc = " + (gce+gcc) + "\n");
	    System.out.print("gee+gec = " + (gee+gec) + "\n");*/

        // stamps from page 302 of Pillage.  Node 0 is the base,
        // node 1 the collector, node 2 the emitter.  Also stamp
        // minimum conductance (gmin) between b,e and b,c
        sim.stampMatrix(nodes[0], nodes[0], -gee - gec - gce - gcc + gmin * 2);
        sim.stampMatrix(nodes[0], nodes[1], gec + gcc - gmin);
        sim.stampMatrix(nodes[0], nodes[2], gee + gce - gmin);
        sim.stampMatrix(nodes[1], nodes[0], gce + gcc - gmin);
        sim.stampMatrix(nodes[1], nodes[1], -gcc + gmin);
        sim.stampMatrix(nodes[1], nodes[2], -gce);
        sim.stampMatrix(nodes[2], nodes[0], gee + gec - gmin);
        sim.stampMatrix(nodes[2], nodes[1], -gec);
        sim.stampMatrix(nodes[2], nodes[2], -gee + gmin);

        // we are solving for v(k+1), not delta v, so we use formula
        // 10.5.13, multiplying J by v(k)
        sim.stampRightSide(nodes[0], -ib - (gec + gcc) * vbc - (gee + gce) * vbe);
        sim.stampRightSide(nodes[1], -ic + gce * vbe + gcc * vbc);
        sim.stampRightSide(nodes[2], -ie + gee * vbe + gec * vbc);
    }

    public void getInfo(String arr[])
    {
        arr[0] = "transistor (" + ((pnp == -1) ? "PNP)" : "NPN)") + " beta=" +
                showFormat.format(beta);
        double vbc = volts[0] - volts[1];
        double vbe = volts[0] - volts[2];
        double vce = volts[1] - volts[2];
        if (vbc * pnp > .2) {
            arr[1] = vbe * pnp > .2 ? "saturation" : "reverse active";
        }
        else {
            arr[1] = vbe * pnp > .2 ? "fwd active" : "cutoff";
        }
        arr[2] = "Ic = " + getCurrentText(ic);
        arr[3] = "Ib = " + getCurrentText(ib);
        arr[4] = "Vbe = " + getVoltageText(vbe);
        arr[5] = "Vbc = " + getVoltageText(vbc);
        arr[6] = "Vce = " + getVoltageText(vce);
    }

    public boolean canViewInScope() { return true; }
}
