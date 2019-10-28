package entity;

import org.python.core.Py;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.PrintStream;
import java.util.Properties;

public class Interpreter {
    private ConsoleTextAera consoleTextAera;
    private PrintStream pstream;
    public String name;
    public PythonInterpreter interpreter;

    public Interpreter() {

    }

    public Interpreter(ConsoleTextAera consoleTextAera) {
        this.consoleTextAera = consoleTextAera;
        this.pstream = consoleTextAera.pStream;
        init();
    }

    public void init() {
        Properties properties = new Properties();
        properties.put("Python.console.encoding", "utf8");
        properties.put("python.home", System.getProperty("user.dir") + "/jython.jar");
        properties.put("python.security.respectJavaAccessibility", "false");
        properties.put("python.import.site", "false");
        Properties properties1 = System.getProperties();
        PythonInterpreter.initialize(properties1, properties, new String[0]);
        PySystemState sys = Py.getSystemState();
        sys.path.add(System.getProperty("user.dir") + "/jython=lib.jar");
        sys.setdefaultencoding("utf8");
        this.interpreter = new PythonInterpreter(null, sys);
        interpreter.setErr(pstream);
        interpreter.setOut(pstream);
    }

    /**
     * 执行命令
     *
     * @param cmd
     * @return
     */
    public boolean execute(String cmd) {

        System.out.println(cmd);
        return true;
    }
}

