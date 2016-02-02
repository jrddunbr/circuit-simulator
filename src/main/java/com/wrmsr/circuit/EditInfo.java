package com.wrmsr.circuit;

import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Scrollbar;
import java.awt.TextField;

public class EditInfo
{
    public String name, text;
    public double value, minval, maxval;
    public TextField textf;
    public Choice choice;
    public Checkbox checkbox;
    public boolean newDialog;
    Scrollbar bar;
    boolean forceLargeM;
    boolean dimensionless;

    public EditInfo(String n, double val, double mn, double mx)
    {
        name = n;
        value = val;
        if (mn == 0 && mx == 0 && val > 0) {
            minval = 1e10;
            while (minval > val / 100) {
                minval /= 10.;
            }
            maxval = minval * 1000;
        }
        else {
            minval = mn;
            maxval = mx;
        }
        forceLargeM = name.indexOf("(ohms)") > 0 ||
                name.indexOf("(Hz)") > 0;
        dimensionless = false;
    }

    public EditInfo setDimensionless()
    {
        dimensionless = true;
        return this;
    }
}
    
