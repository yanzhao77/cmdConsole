package cn.yz;


import java.awt.Point;

//执行一条命令的类
public class DrawCommand implements Command {
    //构造函数需要具体绘画类的实例和该对象在画布上的位置
    protected Drawable drawable;
    private Point position;
    public DrawCommand(Drawable drawable,Point position) {
        // TODO Auto-generated constructor stub
        this.drawable = drawable;
        this.position = position;
    }
    //执行绘制一个点的方法
    @Override
    public void execute() {
        // TODO Auto-generated method stub
        drawable.draw(position.x,position.y);

    }

}

