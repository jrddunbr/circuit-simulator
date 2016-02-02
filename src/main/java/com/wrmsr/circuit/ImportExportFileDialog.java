package com.wrmsr.circuit;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

class ImportExportFileDialog
        implements ImportExportDialog
{
    private static String circuitDump;
    private static String directory = ".";
    CirSim cframe;
    Action type;

    ImportExportFileDialog(CirSim f, Action type)
    {
        if (directory.equals(".")) {
            File file = new File("circuits");
            if (file.isDirectory()) {
                directory = "circuits";
            }
        }
        this.type = type;
        cframe = f;
    }

    private static String readFile(String path)
            throws IOException, FileNotFoundException
    {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(new File(path));
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY,
                    0, fc.size());
            return Charset.forName("UTF-8").decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }

    private static void writeFile(String path)
            throws IOException, FileNotFoundException
    {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(path));
            FileChannel fc = stream.getChannel();
            ByteBuffer bb = Charset.forName("UTF-8").encode(circuitDump);
            fc.write(bb);
        }
        finally {
            stream.close();
        }
    }

    public String getDump()
    {
        return circuitDump;
    }

    public void setDump(String dump)
    {
        circuitDump = dump;
    }

    public void execute()
    {
        FileDialog fd = new FileDialog(new Frame(),
                (type == Action.EXPORT) ? "Save File" :
                        "Open File",
                (type == Action.EXPORT) ? FileDialog.SAVE :
                        FileDialog.LOAD);
        fd.setDirectory(directory);
        fd.setVisible(true);
        String file = fd.getFile();
        String dir = fd.getDirectory();
        if (dir != null) {
            directory = dir;
        }
        if (file == null) {
            return;
        }
        System.err.println(dir + File.separator + file);
        if (type == Action.EXPORT) {
            try {
                writeFile(dir + file);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                String dump = readFile(dir + file);
                circuitDump = dump;
                cframe.readSetup(circuitDump);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
