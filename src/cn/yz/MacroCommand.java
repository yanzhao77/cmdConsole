package cn.yz;

import java.util.Iterator;
import java.util.Stack;

//实现Command接口，组织多条命令
public class MacroCommand implements Command {
    private Stack<Command> commands = new Stack();
    //执行压入命令栈中的每条命令，通过迭代器调用执行方法
    @Override
    public void execute() {
        // TODO Auto-generated method stub
        Iterator iterator = commands.iterator();
        while(iterator.hasNext()){
            ((Command)iterator.next()).execute();
        }


    }
    //如果有了新的命令，将新的命令压入栈中
    public void append(Command cmd){
        if(cmd!=this){
            commands.push(cmd);
        }
    }
    //如果撤回命令，则将命令对象pop出栈
    public void undo(){
        if(!commands.empty()){
            commands.pop();
        }
    }
    //栈清空
    public void clear(){
        commands.clear();
    }

}

