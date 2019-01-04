package com.wrmsr.circuit;

import java.util.StringTokenizer;

import com.wrmsr.circuit.io.RailElm;

public class VoltageElm
        extends CircuitElm
{
    public static final int FLAG_COS = 2;
    public static final int WF_DC = 0;
    public static final int WF_AC = 1;
    public static final int WF_SQUARE = 2;
    public static final int WF_TRIANGLE = 3;
    public static final int WF_SAWTOOTH = 4;
    public static final int WF_PULSE = 5;
    public static final int WF_VAR = 6;
    public final int circleSize = 17;
    public int waveform;
    public double frequency, maxVoltage, freqTimeZero, bias,
            phaseShift, dutyCycle;

    public VoltageElm(int wf)
    {
        super();
        waveform = wf;
        maxVoltage = 5;
        frequency = 40;
        dutyCycle = .5;
        reset();
    }

    public VoltageElm(int f, StringTokenizer st)
    {
        super(f);
        maxVoltage = 5;
        frequency = 40;
        waveform = WF_DC;
        dutyCycle = .5;
        try {
            waveform = new Integer(st.nextToken()).intValue();
            frequency = new Double(st.nextToken()).doubleValue();
            maxVoltage = new Double(st.nextToken()).doubleValue();
            bias = new Double(st.nextToken()).doubleValue();
            phaseShift = new Double(st.nextToken()).doubleValue();
            dutyCycle = new Double(st.nextToken()).doubleValue();
        }
        catch (Exception e) {
        }
        if ((flags & FLAG_COS) != 0) {
            flags &= ~FLAG_COS;
            phaseShift = pi / 2;
        }
        reset();
    }

    public int getDumpType() { return 'v'; }
    /*void setCurrent(double c) {
      current = c;
      System.out.print("v current set to " + c + "\n");
      }*/

    public String dump()
    {
        return super.dump() + " " + waveform + " " + frequency + " " +
                maxVoltage + " " + bias + " " + phaseShift + " " +
                dutyCycle;
    }

    public void reset()
    {
        freqTimeZero = 0;
        curcount = 0;
    }

    double triangleFunc(double x)
    {
        if (x < pi) {
            return x * (2 / pi) - 1;
        }
        return 1 - (x - pi) * (2 / pi);
    }

    public void stamp()
    {
        if (waveform == WF_DC) {
            sim.stampVoltageSource(nodes[0], nodes[1], voltSource,
                    getVoltage());
        }
        else {
            sim.stampVoltageSource(nodes[0], nodes[1], voltSource);
        }
    }

    public void doStep()
    {
        if (waveform != WF_DC) {
            sim.updateVoltageSource(nodes[0], nodes[1], voltSource,
                    getVoltage());
        }
    }

    public double getVoltage()
    {
        double w = 2 * pi * (sim.t - freqTimeZero) * frequency + phaseShift;
        switch (waveform) {
            case WF_DC:
                return maxVoltage + bias;
            case WF_AC:
                return Math.sin(w) * maxVoltage + bias;
            case WF_SQUARE:
                return bias + ((w % (2 * pi) > (2 * pi * dutyCycle)) ?
                        -maxVoltage : maxVoltage);
            case WF_TRIANGLE:
                return bias + triangleFunc(w % (2 * pi)) * maxVoltage;
            case WF_SAWTOOTH:
                return bias + (w % (2 * pi)) * (maxVoltage / pi) - maxVoltage;
            case WF_PULSE:
                return ((w % (2 * pi)) < 1) ? maxVoltage + bias : bias;
            default:
                return 0;
        }
    }

    public int getVoltageSourceCount()
    {
        return 1;
    }

    public double getPower() { return -getVoltageDiff() * current; }

    public double getVoltageDiff() { return volts[1] - volts[0]; }

    public void getInfo(String arr[])
    {
        switch (waveform) {
            case WF_DC:
            case WF_VAR:
                arr[0] = "voltage source";
                break;
            case WF_AC:
                arr[0] = "A/C source";
                break;
            case WF_SQUARE:
                arr[0] = "square wave gen";
                break;
            case WF_PULSE:
                arr[0] = "pulse gen";
                break;
            case WF_SAWTOOTH:
                arr[0] = "sawtooth gen";
                break;
            case WF_TRIANGLE:
                arr[0] = "triangle gen";
                break;
        }
        arr[1] = "I = " + getCurrentText(getCurrent());
        arr[2] = ((this instanceof RailElm) ? "V = " : "Vd = ") +
                getVoltageText(getVoltageDiff());
        if (waveform != WF_DC && waveform != WF_VAR) {
            arr[3] = "f = " + getUnitText(frequency, "Hz");
            arr[4] = "Vmax = " + getVoltageText(maxVoltage);
            int i = 5;
            if (bias != 0) {
                arr[i++] = "Voff = " + getVoltageText(bias);
            }
            else if (frequency > 500) {
                arr[i++] = "wavelength = " +
                        getUnitText(2.9979e8 / frequency, "m");
            }
            arr[i++] = "P = " + getUnitText(getPower(), "W");
        }
    }
}
