package entity;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.Caret;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
        newInter();
    }

    /**
     * 光标位置监听
     */
    public void registerCaretPosinionListener() {

        //如果光标位于prefix对象之左，则将其位置重新定位至其原始位置
        caretColumnProperty().addListener((obserColumn, oldColumn, newColumn) -> {
            if (newColumn < prefix.length()) {
                int pos = oldColumn < getParagraphLength(getCurrentParagraph()) ? oldColumn : getParagraphLength(getCurrentParagraph());
                moveTo(getCurrentParagraph(), pos);
            }
        });

        //如果光标位于可编辑之上则将textAera设置为不可编辑状态，若位于可编辑行之下则将其设置为可编辑状态
        currentParagraphProperty().addListener((obserRow, oldRow, newRow) -> {
            if (newRow < startEditableLine) {
                setEditable(false);
                setShowCaret(Caret.CaretVisibility.ON);//开启光标显示
            } else {
                setEditable(true);
            }
        });
    }


    /**
     * 键盘按键事件
     */
    public void registerKeyboardEventFilter() {
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            //BACK_SPACE键
            if (event.getCode() == KeyCode.BACK_SPACE) {
                doBACK_SPACE(event);
            }
            //Enter键
            else if (event.getCode() == KeyCode.ENTER) {
                doENTER(event);
            }
            //Ctrl+a组合键屏蔽
            else if (event.isControlDown() && event.getCode() == KeyCode.A) {
                event.consume();
            }
            //Ctrl+z组合键屏蔽
            else if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                event.consume();
            }

            //Alt+up组合键
            else if (event.isAltDown() && event.getCode() == KeyCode.UP) {
                doCommandRecordEvent("up");
            } else if (event.isAltDown() && event.getCode() == KeyCode.DOWN) {
                doCommandRecordEvent("down");
            } else if (event.getCode() == KeyCode.F5) {
                moveTo(getLength());
            }

            requestFollowCaret();
            requestFocus();

        });
    }

    /**
     * 回车事件
     *
     * @param event
     */
    private void doENTER(KeyEvent event) {
        //cmd字符串，剔除内部的prefix和lineSeparator，
        String commandText = getCommand();
        //当前文本
        String currentLineText = getParagraph(getCurrentParagraph()).getText();
        if (currentLineText.equals(prefix)) {
            pStream.println();
        }
        //多行cmd直接执行
        else if ((!commandText.endsWith(":")) & (!thisACommand().contains(lineSeparator)) & (consoleInputCommandList(thisACommand()).length > 1)) {
            String[] CommandList = consoleInputCommandList(thisACommand());
            for (String thisListCommand : CommandList) {
                thisListCommand = thisListCommand.replace(prefix, "");
                thisListCommand = thisListCommand.replace(" ", "");
                thisListCommand = thisListCommand.replace("\t", "");
                command = thisListCommand;
                doCommand(thisListCommand);
                ListListener(commandText);
            }
            pStream.println();
        }
        //如果当前行文本以prefix作为开始
        else if (currentLineText.startsWith(prefix)) {
            //当前文本不以“：”作为结束
            //普通单行cmd输入事件
            if (!currentLineText.endsWith(":") & !commandText.endsWith("{")) {
                pStream.println();
                doCommand(commandText);
                ListListener(commandText);
            }
            //多行cmd首行输入事件，后续行缩进一个制表符
            else {
                newLineWithIndented(1);
            }
        } else if (currentLineText.startsWith(lineSeparator)) {
            if (!commandText.endsWith(":") & !commandText.endsWith("{")) {
                //多行cmd结束事件，后续不缩进
                if (getParagraph(getCurrentParagraph()).getText().replace(lineSeparator, "").trim().isEmpty()) {
                    pStream.println();
                    doCommand(commandText);
                    ListListener(commandText);
                }
                //多行cmd内部文本行，后续行缩进与当前行保持一致
                else {
                    newLineWithIndented(numOfStartWithCharSequence(currentLineText.substring(lineSeparator.length()), "\t"));
                }
            }
            /**
             * 多行cmd内部嵌套多行cmd，后续行缩进在当前行缩进的基础上+1
             * 通常用于函数中嵌套循环或判断，一斤循环内部嵌套循环或判断，
             * 例：
             * def func(*1):
             *      for i in 1:
             *          print(i)
             */
            else {
                newLineWithIndented(numOfStartWithCharSequence(currentLineText.substring(lineSeparator.length()), "\t") + 1);
            }
        }
        event.consume();
    }

    /**
     * 退格事件
     *
     * @param event
     */
    private void doBACK_SPACE(KeyEvent event) {
        //获取光标当前行文本
        String text = getParagraph(getCurrentParagraph()).getText();
        //若当前行以prefix对象作为结尾，或者以“\n”作为结尾，则屏蔽该删除事件
        if (text.endsWith(prefix) || text.endsWith("\n")) {
            event.consume();
        }
        //当前行以prefix对象为起始，且光标位置在prefix对象的结尾处
        else if (text.startsWith(prefix) && getCaretColumn() == prefix.length()) {
            event.consume();
        }
        //当前行以lineSeparator对象 作为起始，且光标在lineSeparator对象结尾处
        else if (text.startsWith(lineSeparator) && getCaretColumn() == lineSeparator.length()) {
            if (isEditable()) {
                deleteText(getCurrentParagraph() - 1, getParagraphLength(getCurrentParagraph() - 1), getCurrentParagraph(), getCaretColumn());
            }
            event.consume();
        }
    }


    /**
     * 命令回滚
     *
     * @param upOrDown
     */
    public void doCommandRecordEvent(String upOrDown) {
        if (upOrDown.equals("up")) {
            cmdList.doIndexUp();
        } else if (upOrDown.equals("down")) {
            cmdList.doIndexDown();
        }
        String command;
        if ((command = cmdList.get()) == null) {
            return;
        }
        deleteText(startEditableLine, 0, getParagraphs().size() - 1, getParagraphLength(getParagraphs().size() - 1));
        String[] temp = command.split("\n");
        for (int i = 0; i < temp.length; i++) {
            if (i == 0) {
                appendText(prefix);
                appendText(temp[i]);
            } else {
                appendText("\n");
                appendText(lineSeparator);
                appendText(temp[i]);
            }
        }
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
            interpreter = new Interpreter(this);
        } else if (ParserName.equals("groovy")) {
            groovyShellInter = new GroovyShellInter(this);
        }
    }


    /**
     * 获取cmd命令字符串，剔除内部 的prefix和lineSeparator，同时适用于单行与多行cmd命令
     *
     * @return
     */
    public String getCommand() {
        String text = getText();
        return text.substring(text.lastIndexOf(prefix) + prefix.length()).replace(lineSeparator, "");
    }

    /**
     * 执行命令
     *
     * @param command
     * @return
     */
    public Boolean doCommand(String command) {
        boolean ret = true;
        if (ParserName == null) {
            System.out.println("parserName is null");
        } else if (ParserName.equals("jython")) {
            if (interpreter != null) {
                if (ret = interpreter.execute(command)) {
                    scriptPrinterrecord(command);
                }
            } else {
                System.out.println("jython is null");
            }
        } else if (ParserName.equals("groovy")) {
            if (groovyShellInter != null) {
                if (ret = groovyShellInter.execute(command)) {
                    scriptPrinterrecord(command);
                }
            } else {
                System.out.println("groovy is null");
            }
        }
        cmdList.add(command);
        cmdList.resrtIndex();
        return ret;
    }

    /**
     * 执行成功记录脚本
     *
     * @param cmd
     */
    public void scriptPrinterrecord(String cmd) {

    }

    /**
     * 多行缩进一个制表符
     *
     * @param indentLevel
     */
    public void newLineWithIndented(int indentLevel) {
        appendText("\n");
        appendText(lineSeparator);
        for (int i = 0; i < indentLevel; i++) {
            appendText("\t");
        }
    }

    class CircularList<E> extends ArrayList {
        int index = size();
        boolean hasPressed = false;

        public E get() {
            if (index >= 0 && index < size()) {
                return (E) super.get(index);
            } else {
                return null;
            }
        }

        void doIndexUp() {
            if (hasPressed) {
                if (--index < 0) {
                    index = size() - 1;
                }
            } else {
                hasPressed = true;
            }
        }

        void doIndexDown() {
            if (hasPressed) {
                if (++index == size()) {
                    index = 0;
                }
            } else {
                hasPressed = true;
            }
        }

        void resrtIndex() {
            index = size() - 1;
            hasPressed = false;
        }
    }


    /**
     * 输出流
     */
    class ConsolePrinter extends PrintStream {

        public ConsolePrinter() {
            super(new ByteArrayOutputStream());
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            print(new String(buf));
        }

        @Override
        public void println() {
            appendText("\n");
            appendText(prefix);
            moveTo(getLength());
            startEditableLine = getCurrentParagraph();
            requestFollowCaret();
            requestFocus();
        }

        @Override
        public void println(String x) {
            for (String s : x.split("\n")) {
                appendText("\n");
                appendText(prefix);
                appendText(s);
                appendText("\n");
                appendText(prefix);
            }
            moveTo(getLength());
            startEditableLine = getCurrentParagraph();
            requestFollowCaret();
            requestFocus();
        }

        @Override
        public void print(String s) {
            s = s.replace("\r\n", "\n");
            boolean endWithNewLine;
            endWithNewLine = s.endsWith("\n");
            String[] temp = s.split("\n");
            for (int i = 0; i < temp.length; i++) {
                if (i == temp.length - 1) {
                    appendText(temp[i]);
                    break;
                } else {
                    appendText(temp[i]);
                    appendText("\n");
                    appendText(prefix);
                }
            }
            if (endWithNewLine) {
                println();
            }
            moveTo(getLength());
            requestFollowCaret();
            requestFocus();
        }
    }

    /**
     * 获取字符串，以某个指定字符开头的所重复的次数
     * numOfStartWithCharSequence("ababab123","ab"),返回的结果为4
     *
     * @param str
     * @param charSequence
     * @return
     */
    public int numOfStartWithCharSequence(String str, String charSequence) {
        int num = 0;
        while (str.startsWith(charSequence)) {
            str = str.substring(charSequence.length());
        }
        return num;
    }

    /**
     * 输出流
     */
    public class Console extends OutputStream {

        public Console() {
            super();
        }

        public void println(String str) {
            Platform.runLater(() -> {
                appendText(str + "\n");
            });
        }

        @Override
        public void write(int b) throws IOException {
            Platform.runLater(() -> {
                appendText(String.valueOf((char) b));
            });
        }
    }

    /**
     * 获取当前一行的命令
     */
    public String thisACommand() {
        String command = getText(getText().lastIndexOf(prefix), getCaretPosition());
        return command;
    }

    /**
     * 获取批量执行的命令
     *
     * @param str
     * @return
     */
    public String[] consoleInputCommandList(String str) {
        String[] strings = str.split("\n");
        return strings;
    }

    /**
     * 实现循环数组
     *
     * @param command
     */
    public void ListListener(String command) {
        this.command = command;
        if (newCliList.size() + 1 > cliTextNum) {
            newCliList.clear();
        }
        if (cmdList.size() >= cliTextNum) {
            newCliList.add(command);
            cmdList.remove(newCliList.lastIndexOf(command));
            cmdList.add(newCliList.lastIndexOf(command), String.valueOf(newCliList.getLast()));
        } else {
            cmdList.add(command);
        }
        List<String> newCmdList = ListDuplicateRemoval(cmdList);
        cmdList.clear();
        cmdList.addAll(newCmdList);
        if (isConsoleListenter) {
            saveCommandText();
        }
    }

    /**
     * 写入公共的脚本文件中
     */
    private void saveCommandText() {
        ConsoleTextAera.CircularList<String> cmdList = getCmdList();
        String commandText = (String) cmdList.get(cmdList.size() - 1);
        pStream.println(commandText);
    }

    /**
     * stream 去重
     *
     * @param cmdList
     * @return
     */
    private List<String> ListDuplicateRemoval(List<String> cmdList) {
        cmdList = cmdList.stream().distinct().collect(Collectors.toList());
        return cmdList;
    }

    public int getCliTextNum() {
        return cliTextNum;
    }

    public void setCliTextNum(int cliTextNum) {
        if (cliTextNum > 37) {
            this.cliTextNum = 37;
        } else {
            this.cliTextNum = cliTextNum;
        }
    }

    public ConsolePrinter getpStream() {
        return pStream;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public GroovyShellInter getGroovyShellInter() {
        return groovyShellInter;
    }

    public void setGroovyShellInter(GroovyShellInter groovyShellInter) {
        this.groovyShellInter = groovyShellInter;
    }

    public List<String> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
    }


    public String getModelType() {
        return ModelType;
    }

    public void setModelType(String modelType) {
        ModelType = modelType;
    }

    public int getKEY_PRESSENDNum() {
        return KEY_PRESSENDNum;
    }

    public void setKEY_PRESSENDNum(int KEY_PRESSENDNum) {
        this.KEY_PRESSENDNum = KEY_PRESSENDNum;
    }

    public CircularList<String> getCmdList() {
        return cmdList;
    }

    public void setCmdList(CircularList<String> cmdList) {
        this.cmdList = cmdList;
    }

    public LinkedList<String> getNewCliList() {
        return newCliList;
    }

    public void setNewCliList(LinkedList<String> newCliList) {
        this.newCliList = newCliList;
    }

    public List<String> getFileCommandCileList() {
        return fileCommandCileList;
    }

    public void setFileCommandCileList(List<String> fileCommandCileList) {
        this.fileCommandCileList = fileCommandCileList;
    }

    public boolean isConsoleListenter() {
        return isConsoleListenter;
    }

    public void setConsoleListenter(boolean consoleListenter) {
        isConsoleListenter = consoleListenter;
    }

    public String getParserName() {
        return ParserName;
    }

    public void setParserName(String parserName) {
        ParserName = parserName;
    }

    public boolean isSaveFile() {
        return isSaveFile;
    }

    public void setSaveFile(boolean saveFile) {
        isSaveFile = saveFile;
    }

    public boolean isConsoleOutWindowns() {
        return isConsoleOutWindowns;
    }

    public void setConsoleOutWindowns(boolean consoleOutWindowns) {
        isConsoleOutWindowns = consoleOutWindowns;
    }

    public int getStartEditableLine() {
        return startEditableLine;
    }

    public void setStartEditableLine(int startEditableLine) {
        this.startEditableLine = startEditableLine;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
}

