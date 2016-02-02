package com.wrmsr.circuit;

import java.util.Vector;

public class CircuitNode
{
    public Vector<CircuitNodeLink> links;
    int x, y;
    boolean internal;

    CircuitNode() { links = new Vector<CircuitNodeLink>(); }
}
