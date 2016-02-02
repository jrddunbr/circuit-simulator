package com.wrmsr.circuit;

import java.util.Vector;

public class CircuitNode
{
    int x, y;
    public Vector<CircuitNodeLink> links;
    boolean internal;

    CircuitNode() { links = new Vector<CircuitNodeLink>(); }
}
