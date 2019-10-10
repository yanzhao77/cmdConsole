package zyr.dp.command;

import java.awt.Point;


public class DrawCommand implements Command {

    private Drawable drawable;
    private Point position;
    public  DrawCommand(Drawable drawable,Point position){
        this.drawable=drawable;
        this.position=position;
    }

    public void execute() {
        drawable.draw(position.x, position.y);
    }

}