package zyr.dp.command;

import java.util.Iterator;
import java.util.Stack;

public class MacroCommand implements Command {

    Stack commands=new Stack();

    public void execute() {
        Iterator it = commands.iterator();


        while(it.hasNext()){
            Command command=(Command)it.next();

            command.execute();
        }
    }

    public void append(Command command){
        if(command!=this){
            commands.add(command);
        }
    }

    public void clear(){
        commands.clear();
    }

    public void undo(){
        if(!commands.isEmpty()){
            commands.pop();
        }
    }

}