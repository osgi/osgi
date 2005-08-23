package com.nokia.test.midletsample;

import java.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.osgi.service.log.LogService;

public class TestMidlet extends MIDlet
{

    public TestMidlet()
    {
        fileName = null;
        storedString = null;
        paused = false;
        myStaticFieldChecker = 0L;
    }

    public void startApp()
        throws MIDletStateChangeException
    {
        logService.log(4, "Checking whether the log service is working.");
        staticFieldChecker = myStaticFieldChecker = System.currentTimeMillis();
        if(paused)
        {
            writeResult("RESUME");
        } else
        {
            fileName = getAppProperty("TestResult");
            writeResult("START");
        }
    }

    public void destroyApp(boolean immediate)
        throws MIDletStateChangeException
    {
        if(myStaticFieldChecker == staticFieldChecker)
        {
            writeResult("STOP");
        } else
        {
            writeResult("ERROR");
            System.err.println("Static field sharing in multiple midlet instances!");
        }
    }

    public void pauseApp()
    {
        paused = true;
        writeResult("PAUSE");
    }

    private void writeResult(String result)
    {
        try
        {
            if(fileName == null)
                return;
            File file = new File(fileName);
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(result.getBytes());
            stream.close();
        }
        catch(IOException ioexception) { }
    }

    String fileName;
    String storedString;
    boolean paused;
    static long staticFieldChecker = 0L;
    private LogService logService;
    private long myStaticFieldChecker;

}
