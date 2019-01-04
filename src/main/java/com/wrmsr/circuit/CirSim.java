package com.wrmsr.circuit;
// CirSim.java (c) 2010 by Paul Falstad

// For information about the theory behind this, see Electronic Circuit & System Simulation Methods by Pillage

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import com.wrmsr.circuit.basic.*;
import com.wrmsr.circuit.generic.*;
import com.wrmsr.circuit.io.*;
import com.wrmsr.circuit.passive.*;

public class CirSim
{
    public static final double pi = 3.14159265358979323846;
    public static final int infoWidth = 120;
    public static final int HINT_LC = 1;
    public static final int HINT_RC = 2;
    public static final int HINT_3DB_C = 3;
    public static final int HINT_TWINT = 4;
    public static final int HINT_3DB_L = 5;
    public static final int resct = 6;
    public static String muString = "u";
    public static String ohmString = "ohm";
    public boolean useFrame;
    public Random random;
    public boolean analyzeFlag;
    public boolean dumpMatrix;
    public boolean useBufferedImage;
    public double t;
    public int scopeSelected = -1;
    public int hintType = -1, hintItem1, hintItem2;
    public String stopMessage;
    public double timeStep;
    public Vector<CircuitElm> elmList;
    public CircuitElm dragElm, menuElm, mouseElm, stopElm;
    public CircuitElm plotXElm, plotYElm;
    public SwitchElm heldSwitchElm;
    public double circuitMatrix[][], circuitRightSide[],
            origRightSide[], origMatrix[][];
    public RowInfo circuitRowInfo[];
    public int circuitPermute[];
    public boolean circuitNonLinear;
    public int voltageSourceCount;
    public int circuitMatrixSize, circuitMatrixFullSize;
    public boolean circuitNeedsMap;
    public int scopeCount;
    public int scopeColCount[];
    public Class dumpTypes[];
    public String clipboard;
    public int circuitBottom;
    public Vector<String> undoStack, redoStack;
    public Circuit applet;
    public String startCircuit = null;
    public String startLabel = null;
    public String startCircuitText = null;
    public String baseURL = "http://www.falstad.com/circuit/";
    public long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0;
    public int steps = 0;
    public Vector<CircuitNode> nodeList;
    public CircuitElm voltageSources[];
    public boolean converged;
    public int subIterations;

    public CirSim(Circuit a)
    {
        applet = a;
        useFrame = false;
    }

    public int getrand(int x)
    {
        int q = random.nextInt();
        if (q < 0) {
            q = -q;
        }
        return q % x;
    }

    public void init()
    {
        String euroResistor = null;
        String useFrameStr = null;
        boolean printable = false;
        boolean convention = true;

        CircuitElm.initClass(this);

        boolean euro = (euroResistor != null && euroResistor.equalsIgnoreCase("true"));

        dumpTypes = new Class[300];

        /*
        // basic
        "Add Wire", "WireElm"
        "Add Resistor", "ResistorElm"

        // passive components
        mainMenu.add(passMenu);
        passMenu.add(getClassCheckItem("Add Capacitor", "CapacitorElm"));
        passMenu.add(getClassCheckItem("Add Inductor", "InductorElm"));
        passMenu.add(getClassCheckItem("Add Switch", "SwitchElm"));
        passMenu.add(getClassCheckItem("Add Push Switch", "PushSwitchElm"));
        passMenu.add(getClassCheckItem("Add SPDT Switch", "Switch2Elm"));
        passMenu.add(getClassCheckItem("Add Potentiometer", "PotElm"));
        passMenu.add(getClassCheckItem("Add Transformer", "TransformerElm"));
        passMenu.add(getClassCheckItem("Add Tapped Transformer",
                "TappedTransformerElm"));
        passMenu.add(getClassCheckItem("Add Transmission Line", "TransLineElm"));
        passMenu.add(getClassCheckItem("Add Relay", "RelayElm"));
        passMenu.add(getClassCheckItem("Add Memristor", "MemristorElm"));
        passMenu.add(getClassCheckItem("Add Spark Gap", "SparkGapElm"));

        Menu inputMenu = new Menu("Inputs/Outputs");
        mainMenu.add(inputMenu);
        inputMenu.add(getClassCheckItem("Add Ground", "GroundElm"));
        inputMenu.add(getClassCheckItem("Add Voltage Source (2-terminal)", "DCVoltageElm"));
        inputMenu.add(getClassCheckItem("Add A/C Source (2-terminal)", "ACVoltageElm"));
        inputMenu.add(getClassCheckItem("Add Voltage Source (1-terminal)", "RailElm"));
        inputMenu.add(getClassCheckItem("Add A/C Source (1-terminal)", "ACRailElm"));
        inputMenu.add(getClassCheckItem("Add Square Wave (1-terminal)", "SquareRailElm"));
        inputMenu.add(getClassCheckItem("Add Analog Output", "OutputElm"));
        inputMenu.add(getClassCheckItem("Add Logic Input", "LogicInputElm"));
        inputMenu.add(getClassCheckItem("Add Logic Output", "LogicOutputElm"));
        inputMenu.add(getClassCheckItem("Add Clock", "ClockElm"));
        inputMenu.add(getClassCheckItem("Add A/C Sweep", "SweepElm"));
        inputMenu.add(getClassCheckItem("Add Var. Voltage", "VarRailElm"));
        inputMenu.add(getClassCheckItem("Add Antenna", "AntennaElm"));
        inputMenu.add(getClassCheckItem("Add AM source", "AMElm"));
        inputMenu.add(getClassCheckItem("Add FM source", "FMElm"));
        inputMenu.add(getClassCheckItem("Add Current Source", "CurrentElm"));
        inputMenu.add(getClassCheckItem("Add LED", "LEDElm"));
        inputMenu.add(getClassCheckItem("Add Lamp (beta)", "LampElm"));
        inputMenu.add(getClassCheckItem("Add LED Matrix", "LEDMatrixElm"));
        //inputMenu.add(getClassCheckItem("Add Microphone Input", "SignalInElm"));
        //inputMenu.add(getClassCheckItem("Add Speaker Output", "SignalOutElm"));

        Menu activeMenu = new Menu("Active Components");
        mainMenu.add(activeMenu);
        activeMenu.add(getClassCheckItem("Add Diode", "DiodeElm"));
        activeMenu.add(getClassCheckItem("Add Zener Diode", "ZenerElm"));
        activeMenu.add(getClassCheckItem("Add Transistor (bipolar, NPN)",
                "NTransistorElm"));
        activeMenu.add(getClassCheckItem("Add Transistor (bipolar, PNP)",
                "PTransistorElm"));
        activeMenu.add(getClassCheckItem("Add Op Amp (- on top)", "OpAmpElm"));
        activeMenu.add(getClassCheckItem("Add Op Amp (+ on top)",
                "OpAmpSwapElm"));
        activeMenu.add(getClassCheckItem("Add MOSFET (n-channel)",
                "NMosfetElm"));
        activeMenu.add(getClassCheckItem("Add MOSFET (p-channel)",
                "PMosfetElm"));
        activeMenu.add(getClassCheckItem("Add JFET (n-channel)",
                "NJfetElm"));
        activeMenu.add(getClassCheckItem("Add JFET (p-channel)",
                "PJfetElm"));
        activeMenu.add(getClassCheckItem("Add Analog Switch (SPST)", "AnalogSwitchElm"));
        activeMenu.add(getClassCheckItem("Add Analog Switch (SPDT)", "AnalogSwitch2Elm"));
        activeMenu.add(getClassCheckItem("Add Tristate buffer", "TriStateElm"));
        activeMenu.add(getClassCheckItem("Add Schmitt Trigger", "SchmittElm"));
        activeMenu.add(getClassCheckItem("Add Schmitt Trigger (Inverting)", "InvertingSchmittElm"));
        activeMenu.add(getClassCheckItem("Add SCR", "SCRElm"));
        //activeMenu.add(getClassCheckItem("Add Varactor/Varicap", "VaractorElm"));
        activeMenu.add(getClassCheckItem("Add Tunnel Diode", "TunnelDiodeElm"));
        activeMenu.add(getClassCheckItem("Add Triode", "TriodeElm"));
        //activeMenu.add(getClassCheckItem("Add Diac", "DiacElm"));
        //activeMenu.add(getClassCheckItem("Add Triac", "TriacElm"));
        //activeMenu.add(getClassCheckItem("Add Photoresistor", "PhotoResistorElm"));
        //activeMenu.add(getClassCheckItem("Add Thermistor", "ThermistorElm"));
        activeMenu.add(getClassCheckItem("Add CCII+", "CC2Elm"));
        activeMenu.add(getClassCheckItem("Add CCII-", "CC2NegElm"));

        Menu gateMenu = new Menu("Logic Gates");
        mainMenu.add(gateMenu);
        gateMenu.add(getClassCheckItem("Add Inverter", "InverterElm"));
        gateMenu.add(getClassCheckItem("Add NAND Gate", "NandGateElm"));
        gateMenu.add(getClassCheckItem("Add NOR Gate", "NorGateElm"));
        gateMenu.add(getClassCheckItem("Add AND Gate", "AndGateElm"));
        gateMenu.add(getClassCheckItem("Add OR Gate", "OrGateElm"));
        gateMenu.add(getClassCheckItem("Add XOR Gate", "XorGateElm"));

        Menu chipMenu = new Menu("Chips");
        mainMenu.add(chipMenu);
        chipMenu.add(getClassCheckItem("Add D Flip-Flop", "DFlipFlopElm"));
        chipMenu.add(getClassCheckItem("Add JK Flip-Flop", "JKFlipFlopElm"));
        chipMenu.add(getClassCheckItem("Add T Flip-Flop", "TFlipFlopElm"));
        chipMenu.add(getClassCheckItem("Add 7 Segment LED", "SevenSegElm"));
        chipMenu.add(getClassCheckItem("Add 7 Segment Decoder", "SevenSegDecoderElm"));
        chipMenu.add(getClassCheckItem("Add Multiplexer", "MultiplexerElm"));
        chipMenu.add(getClassCheckItem("Add Demultiplexer", "DeMultiplexerElm"));
        chipMenu.add(getClassCheckItem("Add SIPO shift register", "SipoShiftElm"));
        chipMenu.add(getClassCheckItem("Add PISO shift register", "PisoShiftElm"));
        chipMenu.add(getClassCheckItem("Add Phase Comparator", "PhaseCompElm"));
        chipMenu.add(getClassCheckItem("Add Counter", "CounterElm"));
        chipMenu.add(getClassCheckItem("Add Decade Counter", "DecadeElm"));
        chipMenu.add(getClassCheckItem("Add 555 Timer", "TimerElm"));
        chipMenu.add(getClassCheckItem("Add DAC", "DACElm"));
        chipMenu.add(getClassCheckItem("Add ADC", "ADCElm"));
        chipMenu.add(getClassCheckItem("Add Latch", "LatchElm"));
        //chipMenu.add(getClassCheckItem("Add Static RAM", "SRAMElm"));
        chipMenu.add(getClassCheckItem("Add Sequence generator", "SeqGenElm"));
        chipMenu.add(getClassCheckItem("Add VCO", "VCOElm"));
        chipMenu.add(getClassCheckItem("Add Full Adder", "FullAdderElm"));
        chipMenu.add(getClassCheckItem("Add Half Adder", "HalfAdderElm"));
        chipMenu.add(getClassCheckItem("Add Monostable", "MonostableElm"));
        */

        //otherMenu.add(getClassCheckItem("Add Scope Probe", "ProbeElm"));

        elmList = new Vector<CircuitElm>();
//	setupList = new Vector();
        undoStack = new Vector<String>();
        redoStack = new Vector<String>();

        scopeColCount = new int[20];
        scopeCount = 0;

        random = new Random();


        if (startCircuitText != null) {
            readSetup(startCircuitText);
        }
        else if (stopMessage == null && startCircuit != null) {
            //readSetupFile(startCircuit, startLabel);
        }
        else {
            readSetup(null, 0, false);
        }


    }


    public void register(Class c, CircuitElm elm)
    {
        int t = elm.getDumpType();
        if (t == 0) {
            System.out.println("no dump type: " + c);
            return;
        }

        Class dclass = elm.getDumpClass();

        if (dumpTypes[t] != null && dumpTypes[t] != dclass) {
            System.out.println("dump type conflict: " + c + " " +
                    dumpTypes[t]);
            return;
        }
        dumpTypes[t] = dclass;

        Class sclass = elm.getClass();

    }

    public void updateCircuit()
    {

        if (analyzeFlag) {
            analyzeCircuit();
            analyzeFlag = false;
        }

        try {
            runCircuit();
        }
        catch (Exception e) {
            e.printStackTrace();
            analyzeFlag = true;
            return;
        }

        CircuitElm.powerMult = Math.exp(1 / 4.762 - 7);

        lastFrameTime = lastTime;
    }

    public String getHint()
    {
        CircuitElm c1 = getElm(hintItem1);
        CircuitElm c2 = getElm(hintItem2);
        if (c1 == null || c2 == null) {
            return null;
        }
        if (hintType == HINT_LC) {
            if (!(c1 instanceof InductorElm)) {
                return null;
            }
            if (!(c2 instanceof CapacitorElm)) {
                return null;
            }
            InductorElm ie = (InductorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;
            return "res.f = " + CircuitElm.getUnitText(1 / (2 * pi * Math.sqrt(ie.inductance *
                    ce.capacitance)), "Hz");
        }
        if (hintType == HINT_RC) {
            if (!(c1 instanceof ResistorElm)) {
                return null;
            }
            if (!(c2 instanceof CapacitorElm)) {
                return null;
            }
            ResistorElm re = (ResistorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;
            return "RC = " + CircuitElm.getUnitText(re.resistance * ce.capacitance,
                    "s");
        }
        if (hintType == HINT_3DB_C) {
            if (!(c1 instanceof ResistorElm)) {
                return null;
            }
            if (!(c2 instanceof CapacitorElm)) {
                return null;
            }
            ResistorElm re = (ResistorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;
            return "f.3db = " +
                    CircuitElm.getUnitText(1 / (2 * pi * re.resistance * ce.capacitance), "Hz");
        }
        if (hintType == HINT_3DB_L) {
            if (!(c1 instanceof ResistorElm)) {
                return null;
            }
            if (!(c2 instanceof InductorElm)) {
                return null;
            }
            ResistorElm re = (ResistorElm) c1;
            InductorElm ie = (InductorElm) c2;
            return "f.3db = " +
                    CircuitElm.getUnitText(re.resistance / (2 * pi * ie.inductance), "Hz");
        }
        if (hintType == HINT_TWINT) {
            if (!(c1 instanceof ResistorElm)) {
                return null;
            }
            if (!(c2 instanceof CapacitorElm)) {
                return null;
            }
            ResistorElm re = (ResistorElm) c1;
            CapacitorElm ce = (CapacitorElm) c2;
            return "fc = " +
                    CircuitElm.getUnitText(1 / (2 * pi * re.resistance * ce.capacitance), "Hz");
        }
        return null;
    }

    /*

    public void toggleSwitch(int n)
    {
        (SwitchElm) ce).toggle()
    }

    */


    public CircuitNode getCircuitNode(int n)
    {
        if (n >= nodeList.size()) {
            return null;
        }
        return nodeList.elementAt(n);
    }

    public CircuitElm getElm(int n)
    {
        if (n >= elmList.size()) {
            return null;
        }
        return elmList.elementAt(n);
    }

    public void analyzeCircuit()
    {
        if (elmList.isEmpty()) {
            return;
        }
        stopMessage = null;
        stopElm = null;
        int i, j;
        int vscount = 0;
        nodeList = new Vector<CircuitNode>();
        boolean gotGround = false;
        boolean gotRail = false;
        CircuitElm volt = null;

        //System.out.println("ac1");
        // look for voltage or ground element
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            if (ce instanceof GroundElm) {
                gotGround = true;
                break;
            }
            if (ce instanceof RailElm) {
                gotRail = true;
            }
            if (volt == null && ce instanceof VoltageElm) {
                volt = ce;
            }
        }

        // if no ground, and no rails, then the voltage elm's first terminal
        // is ground
        if (!gotGround && volt != null && !gotRail) {
            CircuitNode cn = new CircuitNode();
            nodeList.addElement(cn);
        }
        else {
            // otherwise allocate extra node for ground
            CircuitNode cn = new CircuitNode();
            cn.x = cn.y = -1;
            nodeList.addElement(cn);
        }
        //System.out.println("ac2");

        // allocate nodes and voltage sources
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            int inodes = ce.getInternalNodeCount();
            int ivs = ce.getVoltageSourceCount();
            int posts = ce.getPostCount();

            // allocate a node for each post and match posts to nodes
            for (j = 0; j != posts; j++) {
                int k;
                for (k = 0; k != nodeList.size(); k++) {
                    CircuitNode cn = getCircuitNode(k);
                }
                if (k == nodeList.size()) {
                    CircuitNode cn = new CircuitNode();
                    CircuitNodeLink cnl = new CircuitNodeLink();
                    cnl.num = j;
                    cnl.elm = ce;
                    cn.links.addElement(cnl);
                    ce.setNode(j, nodeList.size());
                    nodeList.addElement(cn);
                }
                else {
                    CircuitNodeLink cnl = new CircuitNodeLink();
                    cnl.num = j;
                    cnl.elm = ce;
                    getCircuitNode(k).links.addElement(cnl);
                    ce.setNode(j, k);
                    // if it's the ground node, make sure the node voltage is 0,
                    // cause it may not get set later
                    if (k == 0) {
                        ce.setNodeVoltage(j, 0);
                    }
                }
            }
            for (j = 0; j != inodes; j++) {
                CircuitNode cn = new CircuitNode();
                cn.x = cn.y = -1;
                cn.internal = true;
                CircuitNodeLink cnl = new CircuitNodeLink();
                cnl.num = j + posts;
                cnl.elm = ce;
                cn.links.addElement(cnl);
                ce.setNode(cnl.num, nodeList.size());
                nodeList.addElement(cn);
            }
            vscount += ivs;
        }
        voltageSources = new CircuitElm[vscount];
        vscount = 0;
        circuitNonLinear = false;
        //System.out.println("ac3");

        // determine if circuit is nonlinear
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            if (ce.nonLinear()) {
                circuitNonLinear = true;
            }
            int ivs = ce.getVoltageSourceCount();
            for (j = 0; j != ivs; j++) {
                voltageSources[vscount] = ce;
                ce.setVoltageSource(j, vscount++);
            }
        }
        voltageSourceCount = vscount;

        int matrixSize = nodeList.size() - 1 + vscount;
        circuitMatrix = new double[matrixSize][matrixSize];
        circuitRightSide = new double[matrixSize];
        origMatrix = new double[matrixSize][matrixSize];
        origRightSide = new double[matrixSize];
        circuitMatrixSize = circuitMatrixFullSize = matrixSize;
        circuitRowInfo = new RowInfo[matrixSize];
        circuitPermute = new int[matrixSize];
        int vs = 0;
        for (i = 0; i != matrixSize; i++) {
            circuitRowInfo[i] = new RowInfo();
        }
        circuitNeedsMap = false;

        // stamp linear circuit elements
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            ce.stamp();
        }
        //System.out.println("ac4");

        // determine nodes that are unconnected
        boolean closure[] = new boolean[nodeList.size()];
        boolean tempclosure[] = new boolean[nodeList.size()];
        boolean changed = true;
        closure[0] = true;
        while (changed) {
            changed = false;
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                // loop through all ce's nodes to see if they are connected
                // to other nodes not in closure
                for (j = 0; j < ce.getPostCount(); j++) {
                    if (!closure[ce.getNode(j)]) {
                        if (ce.hasGroundConnection(j)) {
                            closure[ce.getNode(j)] = changed = true;
                        }
                        continue;
                    }
                    int k;
                    for (k = 0; k != ce.getPostCount(); k++) {
                        if (j == k) {
                            continue;
                        }
                        int kn = ce.getNode(k);
                        if (ce.getConnection(j, k) && !closure[kn]) {
                            closure[kn] = true;
                            changed = true;
                        }
                    }
                }
            }
            if (changed) {
                continue;
            }

            // connect unconnected nodes
            for (i = 0; i != nodeList.size(); i++) {
                if (!closure[i] && !getCircuitNode(i).internal) {
                    System.out.println("node " + i + " unconnected");
                    stampResistor(0, i, 1e8);
                    closure[i] = true;
                    changed = true;
                    break;
                }
            }
        }
        //System.out.println("ac5");

        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            // look for inductors with no current path
            if (ce instanceof InductorElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
                        ce.getNode(1));
                // first try findPath with maximum depth of 5, to avoid slowdowns
                if (!fpi.findPath(ce.getNode(0), 5) &&
                        !fpi.findPath(ce.getNode(0))) {
                    System.out.println(ce + " no path");
                    ce.reset();
                }
            }
            // look for current sources with no current path
            if (ce instanceof CurrentElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
                        ce.getNode(1));
                if (!fpi.findPath(ce.getNode(0))) {
                    stop("No path for current source!", ce);
                    return;
                }
            }
            // look for voltage source loops
            if ((ce instanceof VoltageElm && ce.getPostCount() == 2) ||
                    ce instanceof WireElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce,
                        ce.getNode(1));
                if (fpi.findPath(ce.getNode(0))) {
                    stop("Voltage source/wire loop with no resistance!", ce);
                    return;
                }
            }
            // look for shorted caps, or caps w/ voltage but no R
            if (ce instanceof CapacitorElm) {
                FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce,
                        ce.getNode(1));
                if (fpi.findPath(ce.getNode(0))) {
                    System.out.println(ce + " shorted");
                    ce.reset();
                }
                else {
                    fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1));
                    if (fpi.findPath(ce.getNode(0))) {
                        stop("Capacitor loop with no resistance!", ce);
                        return;
                    }
                }
            }
        }
        //System.out.println("ac6");

        // simplify the matrix; this speeds things up quite a bit
        for (i = 0; i != matrixSize; i++) {
            int qm = -1, qp = -1;
            double qv = 0;
            RowInfo re = circuitRowInfo[i];
	    /*System.out.println("row " + i + " " + re.lsChanges + " " + re.rsChanges + " " +
			       re.dropRow);*/
            if (re.lsChanges || re.dropRow || re.rsChanges) {
                continue;
            }
            double rsadd = 0;

            // look for rows that can be removed
            for (j = 0; j != matrixSize; j++) {
                double q = circuitMatrix[i][j];
                if (circuitRowInfo[j].type == RowInfo.ROW_CONST) {
                    // keep a running total of const values that have been
                    // removed already
                    rsadd -= circuitRowInfo[j].value * q;
                    continue;
                }
                if (q == 0) {
                    continue;
                }
                if (qp == -1) {
                    qp = j;
                    qv = q;
                    continue;
                }
                if (qm == -1 && q == -qv) {
                    qm = j;
                    continue;
                }
                break;
            }
            //System.out.println("line " + i + " " + qp + " " + qm + " " + j);
	    /*if (qp != -1 && circuitRowInfo[qp].lsChanges) {
		System.out.println("lschanges");
		continue;
	    }
	    if (qm != -1 && circuitRowInfo[qm].lsChanges) {
		System.out.println("lschanges");
		continue;
		}*/
            if (j == matrixSize) {
                if (qp == -1) {
                    stop("Matrix error", null);
                    return;
                }
                RowInfo elt = circuitRowInfo[qp];
                if (qm == -1) {
                    // we found a row with only one nonzero entry; that value
                    // is a constant
                    int k;
                    for (k = 0; elt.type == RowInfo.ROW_EQUAL && k < 100; k++) {
                        // follow the chain
			/*System.out.println("following equal chain from " +
					   i + " " + qp + " to " + elt.nodeEq);*/
                        qp = elt.nodeEq;
                        elt = circuitRowInfo[qp];
                    }
                    if (elt.type == RowInfo.ROW_EQUAL) {
                        // break equal chains
                        //System.out.println("Break equal chain");
                        elt.type = RowInfo.ROW_NORMAL;
                        continue;
                    }
                    if (elt.type != RowInfo.ROW_NORMAL) {
                        System.out.println("type already " + elt.type + " for " + qp + "!");
                        continue;
                    }
                    elt.type = RowInfo.ROW_CONST;
                    elt.value = (circuitRightSide[i] + rsadd) / qv;
                    circuitRowInfo[i].dropRow = true;
                    //System.out.println(qp + " * " + qv + " = const " + elt.value);
                    i = -1; // start over from scratch
                }
                else if (circuitRightSide[i] + rsadd == 0) {
                    // we found a row with only two nonzero entries, and one
                    // is the negative of the other; the values are equal
                    if (elt.type != RowInfo.ROW_NORMAL) {
                        //System.out.println("swapping");
                        int qq = qm;
                        qm = qp;
                        qp = qq;
                        elt = circuitRowInfo[qp];
                        if (elt.type != RowInfo.ROW_NORMAL) {
                            // we should follow the chain here, but this
                            // hardly ever happens so it's not worth worrying
                            // about
                            System.out.println("swap failed");
                            continue;
                        }
                    }
                    elt.type = RowInfo.ROW_EQUAL;
                    elt.nodeEq = qm;
                    circuitRowInfo[i].dropRow = true;
                    //System.out.println(qp + " = " + qm);
                }
            }
        }
        //System.out.println("ac7");

        // find size of new matrix
        int nn = 0;
        for (i = 0; i != matrixSize; i++) {
            RowInfo elt = circuitRowInfo[i];
            if (elt.type == RowInfo.ROW_NORMAL) {
                elt.mapCol = nn++;
                //System.out.println("col " + i + " maps to " + elt.mapCol);
                continue;
            }
            if (elt.type == RowInfo.ROW_EQUAL) {
                RowInfo e2 = null;
                // resolve chains of equality; 100 max steps to avoid loops
                for (j = 0; j != 100; j++) {
                    e2 = circuitRowInfo[elt.nodeEq];
                    if (e2.type != RowInfo.ROW_EQUAL) {
                        break;
                    }
                    if (i == e2.nodeEq) {
                        break;
                    }
                    elt.nodeEq = e2.nodeEq;
                }
            }
            if (elt.type == RowInfo.ROW_CONST) {
                elt.mapCol = -1;
            }
        }
        for (i = 0; i != matrixSize; i++) {
            RowInfo elt = circuitRowInfo[i];
            if (elt.type == RowInfo.ROW_EQUAL) {
                RowInfo e2 = circuitRowInfo[elt.nodeEq];
                if (e2.type == RowInfo.ROW_CONST) {
                    // if something is equal to a const, it's a const
                    elt.type = e2.type;
                    elt.value = e2.value;
                    elt.mapCol = -1;
                    //System.out.println(i + " = [late]const " + elt.value);
                }
                else {
                    elt.mapCol = e2.mapCol;
                    //System.out.println(i + " maps to: " + e2.mapCol);
                }
            }
        }
        //System.out.println("ac8");

	/*System.out.println("matrixSize = " + matrixSize);

	for (j = 0; j != circuitMatrixSize; j++) {
	    System.out.println(j + ": ");
	    for (i = 0; i != circuitMatrixSize; i++)
		System.out.print(circuitMatrix[j][i] + " ");
	    System.out.print("  " + circuitRightSide[j] + "\n");
	}
	System.out.print("\n");*/

        // make the new, simplified matrix
        int newsize = nn;
        double newmatx[][] = new double[newsize][newsize];
        double newrs[] = new double[newsize];
        int ii = 0;
        for (i = 0; i != matrixSize; i++) {
            RowInfo rri = circuitRowInfo[i];
            if (rri.dropRow) {
                rri.mapRow = -1;
                continue;
            }
            newrs[ii] = circuitRightSide[i];
            rri.mapRow = ii;
            //System.out.println("Row " + i + " maps to " + ii);
            for (j = 0; j != matrixSize; j++) {
                RowInfo ri = circuitRowInfo[j];
                if (ri.type == RowInfo.ROW_CONST) {
                    newrs[ii] -= ri.value * circuitMatrix[i][j];
                }
                else {
                    newmatx[ii][ri.mapCol] += circuitMatrix[i][j];
                }
            }
            ii++;
        }

        circuitMatrix = newmatx;
        circuitRightSide = newrs;
        matrixSize = circuitMatrixSize = newsize;
        for (i = 0; i != matrixSize; i++) {
            origRightSide[i] = circuitRightSide[i];
        }
        for (i = 0; i != matrixSize; i++) {
            for (j = 0; j != matrixSize; j++) {
                origMatrix[i][j] = circuitMatrix[i][j];
            }
        }
        circuitNeedsMap = true;

	/*
	System.out.println("matrixSize = " + matrixSize + " " + circuitNonLinear);
	for (j = 0; j != circuitMatrixSize; j++) {
	    for (i = 0; i != circuitMatrixSize; i++)
		System.out.print(circuitMatrix[j][i] + " ");
	    System.out.print("  " + circuitRightSide[j] + "\n");
	}
	System.out.print("\n");*/

        // if a matrix is linear, we can do the lu_factor here instead of
        // needing to do it every frame
        if (!circuitNonLinear) {
            if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
                stop("Singular matrix!", null);
                return;
            }
        }
    }


    public void stop(String s, CircuitElm ce)
    {
        stopMessage = s;
        circuitMatrix = null;
        stopElm = ce;
        analyzeFlag = false;
    }

    // control voltage source vs with voltage from n1 to n2 (must
    // also call stampVoltageSource())
    public void stampVCVS(int n1, int n2, double coef, int vs)
    {
        int vn = nodeList.size() + vs;
        stampMatrix(vn, n1, coef);
        stampMatrix(vn, n2, -coef);
    }

    // stamp independent voltage source #vs, from n1 to n2, amount v
    public void stampVoltageSource(int n1, int n2, int vs, double v)
    {
        int vn = nodeList.size() + vs;
        stampMatrix(vn, n1, -1);
        stampMatrix(vn, n2, 1);
        stampRightSide(vn, v);
        stampMatrix(n1, vn, 1);
        stampMatrix(n2, vn, -1);
    }

    // use this if the amount of voltage is going to be updated in doStep()
    public void stampVoltageSource(int n1, int n2, int vs)
    {
        int vn = nodeList.size() + vs;
        stampMatrix(vn, n1, -1);
        stampMatrix(vn, n2, 1);
        stampRightSide(vn);
        stampMatrix(n1, vn, 1);
        stampMatrix(n2, vn, -1);
    }

    public void updateVoltageSource(int n1, int n2, int vs, double v)
    {
        int vn = nodeList.size() + vs;
        stampRightSide(vn, v);
    }

    public void stampResistor(int n1, int n2, double r)
    {
        double r0 = 1 / r;
        if (Double.isNaN(r0) || Double.isInfinite(r0)) {
            System.out.print("bad resistance " + r + " " + r0 + "\n");
            int a = 0;
            a /= a;
        }
        stampMatrix(n1, n1, r0);
        stampMatrix(n2, n2, r0);
        stampMatrix(n1, n2, -r0);
        stampMatrix(n2, n1, -r0);
    }

    public void stampConductance(int n1, int n2, double r0)
    {
        stampMatrix(n1, n1, r0);
        stampMatrix(n2, n2, r0);
        stampMatrix(n1, n2, -r0);
        stampMatrix(n2, n1, -r0);
    }

    // current from cn1 to cn2 is equal to voltage from vn1 to 2, divided by g
    public void stampVCCurrentSource(int cn1, int cn2, int vn1, int vn2, double g)
    {
        stampMatrix(cn1, vn1, g);
        stampMatrix(cn2, vn2, g);
        stampMatrix(cn1, vn2, -g);
        stampMatrix(cn2, vn1, -g);
    }

    public void stampCurrentSource(int n1, int n2, double i)
    {
        stampRightSide(n1, -i);
        stampRightSide(n2, i);
    }

    // stamp a current source from n1 to n2 depending on current through vs
    public void stampCCCS(int n1, int n2, int vs, double gain)
    {
        int vn = nodeList.size() + vs;
        stampMatrix(n1, vn, gain);
        stampMatrix(n2, vn, -gain);
    }

    // stamp value x in row i, column j, meaning that a voltage change
    // of dv in node j will increase the current into node i by x dv.
    // (Unless i or j is a voltage source node.)
    public void stampMatrix(int i, int j, double x)
    {
        if (i > 0 && j > 0) {
            if (circuitNeedsMap) {
                i = circuitRowInfo[i - 1].mapRow;
                RowInfo ri = circuitRowInfo[j - 1];
                if (ri.type == RowInfo.ROW_CONST) {
                    //System.out.println("Stamping constant " + i + " " + j + " " + x);
                    circuitRightSide[i] -= x * ri.value;
                    return;
                }
                j = ri.mapCol;
                //System.out.println("stamping " + i + " " + j + " " + x);
            }
            else {
                i--;
                j--;
            }
            circuitMatrix[i][j] += x;
        }
    }

    // stamp value x on the right side of row i, representing an
    // independent current source flowing into node i
    public void stampRightSide(int i, double x)
    {
        if (i > 0) {
            if (circuitNeedsMap) {
                i = circuitRowInfo[i - 1].mapRow;
                //System.out.println("stamping " + i + " " + x);
            }
            else {
                i--;
            }
            circuitRightSide[i] += x;
        }
    }

    // indicate that the value on the right side of row i changes in doStep()
    public void stampRightSide(int i)
    {
        //System.out.println("rschanges true " + (i-1));
        if (i > 0) {
            circuitRowInfo[i - 1].rsChanges = true;
        }
    }

    // indicate that the values on the left side of row i change in doStep()
    public void stampNonLinear(int i)
    {
        if (i > 0) {
            circuitRowInfo[i - 1].lsChanges = true;
        }
    }

    public double getIterCount()
    {
        //if (speedBar.getValue() == 0) {
            return 0;
        //}
        //return (Math.exp((speedBar.getValue()-1)/24.) + .5);
        //return .1 * Math.exp((speedBar.getValue() - 61) / 24.);
    }

    public void runCircuit()
    {
        if (circuitMatrix == null || elmList.size() == 0) {
            circuitMatrix = null;
            return;
        }
        int iter;
        //int maxIter = getIterCount();
        boolean debugprint = dumpMatrix;
        dumpMatrix = false;
        long steprate = (long) (160 * getIterCount());
        long tm = System.currentTimeMillis();
        long lit = lastIterTime;
        if (1000 >= steprate * (tm - lastIterTime)) {
            return;
        }
        for (iter = 1; ; iter++) {
            int i, j, k, subiter;
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                ce.startIteration();
            }
            steps++;
            final int subiterCount = 5000;
            for (subiter = 0; subiter != subiterCount; subiter++) {
                converged = true;
                subIterations = subiter;
                for (i = 0; i != circuitMatrixSize; i++) {
                    circuitRightSide[i] = origRightSide[i];
                }
                if (circuitNonLinear) {
                    for (i = 0; i != circuitMatrixSize; i++) {
                        for (j = 0; j != circuitMatrixSize; j++) {
                            circuitMatrix[i][j] = origMatrix[i][j];
                        }
                    }
                }
                for (i = 0; i != elmList.size(); i++) {
                    CircuitElm ce = getElm(i);
                    ce.doStep();
                }
                if (stopMessage != null) {
                    return;
                }
                boolean printit = debugprint;
                debugprint = false;
                for (j = 0; j != circuitMatrixSize; j++) {
                    for (i = 0; i != circuitMatrixSize; i++) {
                        double x = circuitMatrix[i][j];
                        if (Double.isNaN(x) || Double.isInfinite(x)) {
                            stop("nan/infinite matrix!", null);
                            return;
                        }
                    }
                }
                if (printit) {
                    for (j = 0; j != circuitMatrixSize; j++) {
                        for (i = 0; i != circuitMatrixSize; i++) {
                            System.out.print(circuitMatrix[j][i] + ",");
                        }
                        System.out.print("  " + circuitRightSide[j] + "\n");
                    }
                    System.out.print("\n");
                }
                if (circuitNonLinear) {
                    if (converged && subiter > 0) {
                        break;
                    }
                    if (!lu_factor(circuitMatrix, circuitMatrixSize,
                            circuitPermute)) {
                        stop("Singular matrix!", null);
                        return;
                    }
                }
                lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute,
                        circuitRightSide);

                for (j = 0; j != circuitMatrixFullSize; j++) {
                    RowInfo ri = circuitRowInfo[j];
                    double res = 0;
                    if (ri.type == RowInfo.ROW_CONST) {
                        res = ri.value;
                    }
                    else {
                        res = circuitRightSide[ri.mapCol];
                    }
		    /*System.out.println(j + " " + res + " " +
		      ri.type + " " + ri.mapCol);*/
                    if (Double.isNaN(res)) {
                        converged = false;
                        //debugprint = true;
                        break;
                    }
                    if (j < nodeList.size() - 1) {
                        CircuitNode cn = getCircuitNode(j + 1);
                        for (k = 0; k != cn.links.size(); k++) {
                            CircuitNodeLink cnl = (CircuitNodeLink)
                                    cn.links.elementAt(k);
                            cnl.elm.setNodeVoltage(cnl.num, res);
                        }
                    }
                    else {
                        int ji = j - (nodeList.size() - 1);
                        //System.out.println("setting vsrc " + ji + " to " + res);
                        voltageSources[ji].setCurrent(ji, res);
                    }
                }
                if (!circuitNonLinear) {
                    break;
                }
            }
            if (subiter > 5) {
                System.out.print("converged after " + subiter + " iterations\n");
            }
            if (subiter == subiterCount) {
                stop("Convergence failed!", null);
                break;
            }
            t += timeStep;
            tm = System.currentTimeMillis();
            lit = tm;
            if (iter * 1000 >= steprate * (tm - lastIterTime) ||
                    (tm - lastFrameTime > 500)) {
                break;
            }
        }
        lastIterTime = lit;
        //System.out.println((System.currentTimeMillis()-lastFrameTime)/(double) iter);
    }

    public int min(int a, int b) { return (a < b) ? a : b; }

    public int max(int a, int b) { return (a > b) ? a : b; }

    /*

    public void stackScope(int s)
    {
        if (s == 0) {
            if (scopeCount < 2) {
                return;
            }
            s = 1;
        }
        if (scopes[s].position == scopes[s - 1].position) {
            return;
        }
        scopes[s].position = scopes[s - 1].position;
        for (s++; s < scopeCount; s++) {
            scopes[s].position--;
        }
    }

    public void unstackScope(int s)
    {
        if (s == 0) {
            if (scopeCount < 2) {
                return;
            }
            s = 1;
        }
        if (scopes[s].position != scopes[s - 1].position) {
            return;
        }
        for (; s < scopeCount; s++) {
            scopes[s].position++;
        }
    }

    public void stackAll()
    {
        int i;
        for (i = 0; i != scopeCount; i++) {
            scopes[i].position = 0;
            scopes[i].showMax = scopes[i].showMin = false;
        }
    }

    public void unstackAll()
    {
        int i;
        for (i = 0; i != scopeCount; i++) {
            scopes[i].position = i;
            scopes[i].showMax = true;
        }
    }


    public void doImport()
    {
        if (impDialog == null) {
            impDialog = ImportExportDialogFactory.Create(this,
                    ImportExportDialog.Action.IMPORT);
        }
//	    impDialog = new ImportExportClipboardDialog(this,
//		ImportExportDialog.Action.IMPORT);
        pushUndo();
        impDialog.execute();
    }

    public void doExport(boolean url)
    {
        String dump = dumpCircuit();
        if (url) {
            dump = baseURL + "#" + URLEncoder.encode(dump);
        }
        if (expDialog == null) {
            expDialog = ImportExportDialogFactory.Create(this,
                    ImportExportDialog.Action.EXPORT);
//	    expDialog = new ImportExportClipboardDialog(this,
//		 ImportExportDialog.Action.EXPORT);
        }
        expDialog.setDump(dump);
        expDialog.execute();
    }

    */

    /*

    public String dumpCircuit()
    {
        int i;
        int f = (dotsCheckItem.getState()) ? 1 : 0;
        f |= (smallGridCheckItem.getState()) ? 2 : 0;
        f |= (voltsCheckItem.getState()) ? 0 : 4;
        f |= (powerCheckItem.getState()) ? 8 : 0;
        f |= (showValuesCheckItem.getState()) ? 0 : 16;
        // 32 = linear scale in afilter
        String dump = "$ " + f + " " +
                timeStep + " " + getIterCount() + " " +
                currentBar.getValue() + " " + CircuitElm.voltageRange + " " +
                powerBar.getValue() + "\n";
        for (i = 0; i != elmList.size(); i++) {
            dump += getElm(i).dump() + "\n";
        }
        for (i = 0; i != scopeCount; i++) {
            String d = scopes[i].dump();
            if (d != null) {
                dump += d + "\n";
            }
        }
        if (hintType != -1) {
            dump += "h " + hintType + " " + hintItem1 + " " +
                    hintItem2 + "\n";
        }
        return dump;
    }

    */

    /*

    public ByteArrayOutputStream readUrlData(URL url)
            throws java.io.IOException
    {
        Object o = url.getContent();
        FilterInputStream fis = (FilterInputStream) o;
        ByteArrayOutputStream ba = new ByteArrayOutputStream(fis.available());
        int blen = 1024;
        byte b[] = new byte[blen];
        while (true) {
            int len = fis.read(b);
            if (len <= 0) {
                break;
            }
            ba.write(b, 0, len);
        }
        return ba;
    }

    public URL getCodeBase()
    {
        try {
            if (applet != null) {
                return applet.getCodeBase();
            }
            File f = new File(".");
            return new URL("file:" + f.getCanonicalPath() + "/");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    */

    /*

    public void getSetupList(boolean retry)
    {
        int stackptr = 0;
        try {
            // hausen: if setuplist.txt does not exist in the same
            // directory, try reading from the jar file
            ByteArrayOutputStream ba = null;
            try {
                URL url = new URL(getCodeBase() + "setuplist.txt");
                ba = readUrlData(url);
            }
            catch (Exception e) {
                URL url = getClass().getClassLoader().getResource("setuplist.txt");
                ba = readUrlData(url);
            }
            // /hausen

            byte b[] = ba.toByteArray();
            int len = ba.size();
            int p;
            if (len == 0 || b[0] != '#') {
                // got a redirect, try again
                return;
            }
            for (p = 0; p < len; ) {
                int l;
                for (l = 0; l != len - p; l++) {
                    if (b[l + p] == '\n') {
                        l++;
                        break;
                    }
                }
                String line = new String(b, p, l - 1);
                if (line.charAt(0) == '#') {
                    ;
                }

                else {
                    int i = line.indexOf(' ');
                    if (i > 0) {
                        String title = line.substring(i + 1);
                        boolean first = false;
                        if (line.charAt(0) == '>') {
                            first = true;
                        }
                        String file = line.substring(first ? 1 : 0, i);
                        if (first && startCircuit == null) {
                            startCircuit = file;
                            startLabel = title;
                        }
                    }
                }
                p += l;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            stop("Can't read setuplist.txt!", null);
        }
    }

    */

    public void readSetup(String text)
    {
        readSetup(text, false);
    }

    public void readSetup(String text, boolean retain)
    {
        readSetup(text.getBytes(), text.length(), retain);
    }

    /*

    public void readSetupFile(String str, String title)
    {
        t = 0;
        System.out.println(str);
        try {
            URL url = new URL(getCodeBase() + "circuits/" + str);
            ByteArrayOutputStream ba = readUrlData(url);
            readSetup(ba.toByteArray(), ba.size(), false);
        }
        catch (Exception e1) {
            try {
                URL url = getClass().getClassLoader().getResource("circuits/" + str);
                ByteArrayOutputStream ba = readUrlData(url);
                readSetup(ba.toByteArray(), ba.size(), false);
            }
            catch (Exception e) {
                e.printStackTrace();
                stop("Unable to read " + str + "!", null);
            }
        }
    }

    */

    public void readSetup(byte b[], int len, boolean retain)
    {
        int i;
        if (!retain) {
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                ce.delete();
            }
            elmList.removeAllElements();
            hintType = -1;
            timeStep = 5e-6;
            CircuitElm.voltageRange = 5;
            scopeCount = 0;
        }
        int p;
        for (p = 0; p < len; ) {
            int l;
            int linelen = 0;
            for (l = 0; l != len - p; l++) {
                if (b[l + p] == '\n' || b[l + p] == '\r') {
                    linelen = l++;
                    if (l + p < b.length && b[l + p] == '\n') {
                        l++;
                    }
                    break;
                }
            }
            String line = new String(b, p, linelen);
            StringTokenizer st = new StringTokenizer(line);
            while (st.hasMoreTokens()) {
                String type = st.nextToken();
                int tint = type.charAt(0);
                try {
                    if (tint == 'h') {
                        readHint(st);
                        break;
                    }
                    if (tint == '$') {
                        readOptions(st);
                        break;
                    }
                    if (tint == '%' || tint == '?' || tint == 'B') {
                        // ignore afilter-specific stuff
                        break;
                    }
                    if (tint >= '0' && tint <= '9') {
                        tint = new Integer(type).intValue();
                    }
                    int x1 = new Integer(st.nextToken()).intValue();
                    int y1 = new Integer(st.nextToken()).intValue();
                    int x2 = new Integer(st.nextToken()).intValue();
                    int y2 = new Integer(st.nextToken()).intValue();
                    int f = new Integer(st.nextToken()).intValue();
                    CircuitElm ce = null;
                    Class cls = dumpTypes[tint];
                    if (cls == null) {
                        System.out.println("unrecognized dump type: " + type);
                        break;
                    }
                    // find element class
                    Class carr[] = new Class[6];
                    //carr[0] = getClass();
                    carr[0] = carr[1] = carr[2] = carr[3] = carr[4] =
                            int.class;
                    carr[5] = StringTokenizer.class;
                    Constructor cstr = null;
                    cstr = cls.getConstructor(carr);

                    // invoke constructor with starting coordinates
                    Object oarr[] = new Object[6];
                    //oarr[0] = this;
                    oarr[0] = new Integer(x1);
                    oarr[1] = new Integer(y1);
                    oarr[2] = new Integer(x2);
                    oarr[3] = new Integer(y2);
                    oarr[4] = new Integer(f);
                    oarr[5] = st;
                    ce = (CircuitElm) cstr.newInstance(oarr);
                    elmList.addElement(ce);
                }
                catch (java.lang.reflect.InvocationTargetException ee) {
                    ee.getTargetException().printStackTrace();
                    break;
                }
                catch (Exception ee) {
                    ee.printStackTrace();
                    break;
                }
                break;
            }
            p += l;
        }
    }

    public void readHint(StringTokenizer st)
    {
        hintType = new Integer(st.nextToken()).intValue();
        hintItem1 = new Integer(st.nextToken()).intValue();
        hintItem2 = new Integer(st.nextToken()).intValue();
    }

    public void readOptions(StringTokenizer st)
    {
        int flags = new Integer(st.nextToken()).intValue();
        timeStep = new Double(st.nextToken()).doubleValue();
        double sp = new Double(st.nextToken()).doubleValue();
        int sp2 = (int) (Math.log(10 * sp) * 24 + 61.5);
        CircuitElm.voltageRange = new Double(st.nextToken()).doubleValue();
    }

    /*

    public boolean doSwitch(int x, int y)
    {
        SwitchElm se = (SwitchElm) mouseElm;
        se.toggle();
        if (se.momentary) {
            heldSwitchElm = se;
        }
        needAnalyze();
        return true;
    }

    */

    public int locateElm(CircuitElm elm)
    {
        int i;
        for (i = 0; i != elmList.size(); i++) {
            if (elm == elmList.elementAt(i)) {
                return i;
            }
        }
        return -1;
    }


    public int distanceSq(int x1, int y1, int x2, int y2)
    {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }


    public CircuitElm constructElement(Class c, int x0, int y0)
    {
        // find element class
        Class carr[] = new Class[2];
        //carr[0] = getClass();
        carr[0] = carr[1] = int.class;
        Constructor cstr = null;
        try {
            cstr = c.getConstructor(carr);
        }
        catch (NoSuchMethodException ee) {
            System.out.println("caught NoSuchMethodException " + c);
            return null;
        }
        catch (Exception ee) {
            ee.printStackTrace();
            return null;
        }

        // invoke constructor with starting coordinates
        Object oarr[] = new Object[2];
        oarr[0] = new Integer(x0);
        oarr[1] = new Integer(y0);
        try {
            return (CircuitElm) cstr.newInstance(oarr);
        }
        catch (Exception ee) {
            ee.printStackTrace();
        }
        return null;
    }

    /*

    public void itemStateChanged(ItemEvent e)
    {
        cv.repaint(pause);
        Object mi = e.getItemSelectable();
        if (mi == stoppedCheck) {
            return;
        }
        if (mi == smallGridCheckItem) {
            setGrid();
        }
        if (mi == powerCheckItem) {
            if (powerCheckItem.getState()) {
                voltsCheckItem.setState(false);
            }
            else {
                voltsCheckItem.setState(true);
            }
        }
        if (mi == voltsCheckItem && voltsCheckItem.getState()) {
            powerCheckItem.setState(false);
        }
        enableItems();
        if (menuScope != -1) {
            Scope sc = scopes[menuScope];
            sc.handleMenu(e, mi);
        }
        if (mi instanceof CheckboxMenuItem) {
            MenuItem mmi = (MenuItem) mi;
            int prevMouseMode = mouseMode;
            setMouseMode(MODE_ADD_ELM);
            String s = mmi.getActionCommand();
            if (s.length() > 0) {
                mouseModeStr = s;
            }
            if (s.compareTo("DragAll") == 0) {
                setMouseMode(MODE_DRAG_ALL);
            }
            else if (s.compareTo("DragRow") == 0) {
                setMouseMode(MODE_DRAG_ROW);
            }
            else if (s.compareTo("DragColumn") == 0) {
                setMouseMode(MODE_DRAG_COLUMN);
            }
            else if (s.compareTo("DragSelected") == 0) {
                setMouseMode(MODE_DRAG_SELECTED);
            }
            else if (s.compareTo("DragPost") == 0) {
                setMouseMode(MODE_DRAG_POST);
            }
            else if (s.compareTo("Select") == 0) {
                setMouseMode(MODE_SELECT);
            }
            else if (s.length() > 0) {
                try {
                    addingClass = Class.forName(s);
                }
                catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            else {
                setMouseMode(prevMouseMode);
            }
            tempMouseMode = mouseMode;
        }
    }

*/

/*

    void doDelete()
    {
        int i;
        pushUndo();
        setMenuSelection();
        boolean hasDeleted = false;

        for (i = elmList.size() - 1; i >= 0; i--) {
            CircuitElm ce = getElm(i);
            if (ce.isSelected()) {
                ce.delete();
                elmList.removeElementAt(i);
                hasDeleted = true;
            }
        }

        if (!hasDeleted) {
            for (i = elmList.size() - 1; i >= 0; i--) {
                CircuitElm ce = getElm(i);
                if (ce == mouseElm) {
                    ce.delete();
                    elmList.removeElementAt(i);
                    hasDeleted = true;
                    mouseElm = null;
                    break;
                }
            }
        }

        if (hasDeleted) {
            needAnalyze();
        }
    }

    void doCopy()
    {
        int i;
        clipboard = "";
        setMenuSelection();
        for (i = elmList.size() - 1; i >= 0; i--) {
            CircuitElm ce = getElm(i);
            if (ce.isSelected()) {
                clipboard += ce.dump() + "\n";
            }
        }
        enablePaste();
    }

    void enablePaste()
    {
        pasteItem.setEnabled(clipboard.length() > 0);
    }

    void doPaste()
    {
        pushUndo();
        clearSelection();
        int i;
        Rectangle oldbb = null;
        for (i = 0; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            Rectangle bb = ce.getBoundingBox();
            if (oldbb != null) {
                oldbb = oldbb.union(bb);
            }
            else {
                oldbb = bb;
            }
        }
        int oldsz = elmList.size();
        readSetup(clipboard, true);

        // select new items
        Rectangle newbb = null;
        for (i = oldsz; i != elmList.size(); i++) {
            CircuitElm ce = getElm(i);
            ce.setSelected(true);
            Rectangle bb = ce.getBoundingBox();
            if (newbb != null) {
                newbb = newbb.union(bb);
            }
            else {
                newbb = bb;
            }
        }
        if (oldbb != null && newbb != null && oldbb.intersects(newbb)) {
            // find a place for new items
            int dx = 0, dy = 0;
            int spacew = circuitArea.width - oldbb.width - newbb.width;
            int spaceh = circuitArea.height - oldbb.height - newbb.height;
            if (spacew > spaceh) {
                dx = snapGrid(oldbb.x + oldbb.width - newbb.x + gridSize);
            }
            else {
                dy = snapGrid(oldbb.y + oldbb.height - newbb.y + gridSize);
            }
            for (i = oldsz; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                ce.move(dx, dy);
            }
            // center circuit
            handleResize();
        }
        needAnalyze();
    }

*/

    // factors a matrix into upper and lower triangular matrices by
    // gaussian elimination.  On entry, a[0..n-1][0..n-1] is the
    // matrix to be factored.  ipvt[] returns an integer vector of pivot
    // indices, used in the lu_solve() routine.
    public boolean lu_factor(double a[][], int n, int ipvt[])
    {
        double scaleFactors[];
        int i, j, k;

        scaleFactors = new double[n];

        // divide each row by its largest element, keeping track of the
        // scaling factors
        for (i = 0; i != n; i++) {
            double largest = 0;
            for (j = 0; j != n; j++) {
                double x = Math.abs(a[i][j]);
                if (x > largest) {
                    largest = x;
                }
            }
            // if all zeros, it's a singular matrix
            if (largest == 0) {
                return false;
            }
            scaleFactors[i] = 1.0 / largest;
        }

        // use Crout's method; loop through the columns
        for (j = 0; j != n; j++) {

            // calculate upper triangular elements for this column
            for (i = 0; i != j; i++) {
                double q = a[i][j];
                for (k = 0; k != i; k++) {
                    q -= a[i][k] * a[k][j];
                }
                a[i][j] = q;
            }

            // calculate lower triangular elements for this column
            double largest = 0;
            int largestRow = -1;
            for (i = j; i != n; i++) {
                double q = a[i][j];
                for (k = 0; k != j; k++) {
                    q -= a[i][k] * a[k][j];
                }
                a[i][j] = q;
                double x = Math.abs(q);
                if (x >= largest) {
                    largest = x;
                    largestRow = i;
                }
            }

            // pivoting
            if (j != largestRow) {
                double x;
                for (k = 0; k != n; k++) {
                    x = a[largestRow][k];
                    a[largestRow][k] = a[j][k];
                    a[j][k] = x;
                }
                scaleFactors[largestRow] = scaleFactors[j];
            }

            // keep track of row interchanges
            ipvt[j] = largestRow;

            // avoid zeros
            if (a[j][j] == 0.0) {
                System.out.println("avoided zero");
                a[j][j] = 1e-18;
            }

            if (j != n - 1) {
                double mult = 1.0 / a[j][j];
                for (i = j + 1; i != n; i++) {
                    a[i][j] *= mult;
                }
            }
        }
        return true;
    }

    // Solves the set of n linear equations using a LU factorization
    // previously performed by lu_factor.  On input, b[0..n-1] is the right
    // hand side of the equations, and on output, contains the solution.
    public void lu_solve(double a[][], int n, int ipvt[], double b[])
    {
        int i;

        // find first nonzero b element
        for (i = 0; i != n; i++) {
            int row = ipvt[i];

            double swap = b[row];
            b[row] = b[i];
            b[i] = swap;
            if (swap != 0) {
                break;
            }
        }

        int bi = i++;
        for (; i < n; i++) {
            int row = ipvt[i];
            int j;
            double tot = b[row];

            b[row] = b[i];
            // forward substitution using the lower triangular matrix
            for (j = bi; j < i; j++) {
                tot -= a[i][j] * b[j];
            }
            b[i] = tot;
        }
        for (i = n - 1; i >= 0; i--) {
            double tot = b[i];

            // back-substitution using the upper triangular matrix
            int j;
            for (j = i + 1; j != n; j++) {
                tot -= a[i][j] * b[j];
            }
            b[i] = tot / a[i][i];
        }
    }

    public  class FindPathInfo
    {
        static final int INDUCT = 1;
        static final int VOLTAGE = 2;
        static final int SHORT = 3;
        static final int CAP_V = 4;
        boolean used[];
        int dest;
        CircuitElm firstElm;
        int type;

        public FindPathInfo(int t, CircuitElm e, int d)
        {
            dest = d;
            type = t;
            firstElm = e;
            used = new boolean[nodeList.size()];
        }

        public boolean findPath(int n1) { return findPath(n1, -1); }

        public boolean findPath(int n1, int depth)
        {
            if (n1 == dest) {
                return true;
            }
            if (depth-- == 0) {
                return false;
            }
            if (used[n1]) {
                //System.out.println("used " + n1);
                return false;
            }
            used[n1] = true;
            int i;
            for (i = 0; i != elmList.size(); i++) {
                CircuitElm ce = getElm(i);
                if (ce == firstElm) {
                    continue;
                }
                if (type == INDUCT) {
                    if (ce instanceof CurrentElm) {
                        continue;
                    }
                }
                if (type == VOLTAGE) {
                    if (!(ce.isWire() || ce instanceof VoltageElm)) {
                        continue;
                    }
                }
                if (type == SHORT && !ce.isWire()) {
                    continue;
                }
                if (type == CAP_V) {
                    if (!(ce.isWire() || ce instanceof CapacitorElm ||
                            ce instanceof VoltageElm)) {
                        continue;
                    }
                }
                if (n1 == 0) {
                    // look for posts which have a ground connection;
                    // our path can go through ground
                    int j;
                    for (j = 0; j != ce.getPostCount(); j++) {
                        if (ce.hasGroundConnection(j) &&
                                findPath(ce.getNode(j), depth)) {
                            used[n1] = false;
                            return true;
                        }
                    }
                }
                int j;
                for (j = 0; j != ce.getPostCount(); j++) {
                    //System.out.println(ce + " " + ce.getNode(j));
                    if (ce.getNode(j) == n1) {
                        break;
                    }
                }
                if (j == ce.getPostCount()) {
                    continue;
                }
                if (ce.hasGroundConnection(j) && findPath(0, depth)) {
                    //System.out.println(ce + " has ground");
                    used[n1] = false;
                    return true;
                }
                if (type == INDUCT && ce instanceof InductorElm) {
                    double c = ce.getCurrent();
                    if (j == 0) {
                        c = -c;
                    }
                    //System.out.println("matching " + c + " to " + firstElm.getCurrent());
                    //System.out.println(ce + " " + firstElm);
                    if (Math.abs(c - firstElm.getCurrent()) > 1e-10) {
                        continue;
                    }
                }
                int k;
                for (k = 0; k != ce.getPostCount(); k++) {
                    if (j == k) {
                        continue;
                    }
                    //System.out.println(ce + " " + ce.getNode(j) + "-" + ce.getNode(k));
                    if (ce.getConnection(j, k) && findPath(ce.getNode(k), depth)) {
                        //System.out.println("got findpath " + n1);
                        used[n1] = false;
                        return true;
                    }
                    //System.out.println("back on findpath " + n1);
                }
            }
            used[n1] = false;
            //System.out.println(n1 + " failed");
            return false;
        }
    }
}
