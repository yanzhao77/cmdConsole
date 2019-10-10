package entity;

import org.fxmisc.richtext.Caret;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConsoleTextAera extends InlineCssTextArea {
    public final ConsolePrinter pStream = new ConsolePrinter();//数据流
    private Interpreter interpreter;//python解析器
    private GroovyShellInter groovyShellInter;//groovy解析器
    private List<String> commandList = new ArrayList<>();//固定命令
    private String command;//命令
    private String ModelType;//界面类型
    private int KEY_PRESSENDNum;//监控按键回滚
    private int cliTextNum;//命令行回滚数（默认20）
    private CircularList<String> cmdList = new CircularList<>();//记录命令行
    private LinkedList<String> newCliList = new LinkedList<>();//暂存命令
    private List<String> fileCommandCileList = new ArrayList<>();//文件操作命令
    private boolean isConsoleListenter = false;//是否开启监听流
    private String ParserName;//解析器名称
    private boolean isSaveFile;//是否开启存储命令
    private boolean isConsoleOutWindowns;//是否开启控制台打印
    private int startEditableLine = 0;//文本框可编辑区域 起始行数
    public String prefix = " >\0\0";//段落起始符
    public String lineSeparator = "...\0";//段落分隔符

    public ConsoleTextAera() {
        super();
        pStream.print(prefix);
        requestFollowCaret();
    }

    public ConsoleTextAera(String text) {
        super(text);
        new ConsoleTextAera();
        pStream.println(this.getText() + "is ready");
    }

    /**
     * 初始化数据
     *
     * @param model
     */
    public void init(String model) {
        ModelType = model;
        registerCaretPosinionListener();
        registerKeyboardEventFilter();
        cliTextNumListener();
        saveInterinit();

    }

    /**
     * 光标位置监听
     */
    public void registerCaretPosinionListener() {
        //如果光标位置位于prefix对象之左，则将其重新定位到原始位置
        caretColumnProperty().addListener((obserColumn, oldColumn, newColumn) -> {
            if (newColumn < prefix.length()) {
                int pos = oldColumn < getParagraphLength(getCurrentParagraph()) ? oldColumn : getParagraphLength(getCurrentParagraph());
                moveTo(getCurrentParagraph(), pos);
            }
        });
        //如果光标位于可编辑行纸上则将textAera设置为不可编辑状态，若位于可编辑行之下则将其设置为可编辑状态
        currentParagraphProperty().addListener((observableRow, oldRow, newRow) -> {
            if (newRow < startEditableLine) {
                setEditable(false);
                setShowCaret(Caret.CaretVisibility.ON);
            } else {
                setEditable(true);
            }
        });
    }


    public void registerKeyboardEventFilter() {

    }

    /**
     * 初始化命令数
     */
    public void cliTextNumListener() {
        if (cliTextNum == 0 || cliTextNum < 0) {
            cliTextNum = 20;
        }
    }

    public void saveInterinit() {
        ParserName = "groovy";
        isSaveFile = false;
        isConsoleOutWindowns = false;

    }

    public void newInter() {
        if (ParserName.equals("jython")) {
            interpreter = new Interpreter(pStream);
        } else if (ParserName.equals("groovy")) {
            groovyShellInter = new GroovyShellInter(pStream);
        }
    }

    class CircularList<E> extends ArrayList {

    }

    class ConsolePrinter extends PrintStream {

        public ConsolePrinter() {
            super(new ByteArrayOutputStream());
        }

        public ConsolePrinter(OutputStream out) {
            super(out);
        }
    }
}
