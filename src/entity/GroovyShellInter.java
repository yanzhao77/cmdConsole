package entity;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.PrintStream;

public class GroovyShellInter {
    Binding binding = new Binding();
    GroovyShell shell = new GroovyShell(binding);//解析器
    private PrintStream pstream;
    ConsoleTextAera textAera;
    String clihandNew = "";
    String[] headTitle;

    public GroovyShellInter() {
    }

    public GroovyShellInter(ConsoleTextAera consoleTextAera) {
        this.textAera = consoleTextAera;
        this.pstream = consoleTextAera.pStream;
        pStreamConsoleListtener(pstream);
    }

    public boolean execute(String cmd) {
        if (cmd.contains("\\")) {
            cmd = cmd.replace("\\", "\\\\");
        }
        boolean flag;

        try {
            Object evaluate = shell.evaluate(cmd);
            flag = true;
        } catch (CompilationFailedException e) {
            e.printStackTrace();
            flag = false;
        }
//        if (textAera.isSaveFile()){
//            System.out.println("呆开发");
//        }
        return flag;
    }

    /**
     * 将消息发到流
     *
     * @param printStream
     */
    public void pStreamConsoleListtener(PrintStream printStream) {
        pstream = printStream;
        System.setOut(pstream);
    }


}
