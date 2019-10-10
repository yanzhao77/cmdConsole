package cn.yz;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

//绘制画布和绘制点的类
public class DrawCanvas extends Canvas implements Drawable {
    private Color color = Color.red;
    private int radius = 6;
    private MacroCommand history;
    //构造函数绘制画布
    public DrawCanvas(int width,int height,MacroCommand history) {
        // TODO Auto-generated constructor stub
        setSize(width,height);
        setBackground(Color.white);
        this.history = history;
    }
    //该方法绘制一个点，包括创建画笔实例，设置画笔颜色，以及最终的画圆
    @Override
    public void draw(int x, int y) {
        // TODO Auto-generated method stub
        Graphics graphics = getGraphics();
        graphics.setColor(color);
        graphics.fillOval(x-radius, y-radius, radius*2, radius*2);
    }
    //重新执行命令栈中的命令
    public void paint(Graphics g){
        history.execute();
    }

}

