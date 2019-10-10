package cn.yz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Main extends JFrame implements ActionListener, MouseMotionListener, WindowListener {
    //创建多命令链对象
    private MacroCommand history = new MacroCommand();
    //初始化画布
    private DrawCanvas canvas = new DrawCanvas(400, 400, history);
    //创建按钮对象
    private JButton clearButton = new JButton("clear");
    public Main(String string){
        super(string);
        //为空间添加事件监听
        this.addWindowListener(this);
        canvas.addMouseMotionListener(this);
        clearButton.addActionListener(this);

        //设置布局
        Box buttonnBox = new Box(BoxLayout.X_AXIS);
        buttonnBox.add(clearButton);
        Box mainBox = new Box(BoxLayout.Y_AXIS);
        mainBox.add(buttonnBox);
        mainBox.add(canvas);
        getContentPane().add(mainBox);

        pack();
        show();
    }
    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowClosing(WindowEvent e) {
        // TODO Auto-generated method stub
        System.exit(0);

    }

    @Override
    public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }
    //鼠标拖拽事件发生时，创建一条命令，将该命令加入栈中，执行命令
    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        Command cmd = new DrawCommand(canvas,e.getPoint());
        history.append(cmd);
        cmd.execute();


    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        //如果按下清除键，则清除历史栈中的命令，重新绘制画布
        if(e.getSource() == clearButton){
            history.clear();
            canvas.repaint();
        }


    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new Main("Command Pattern Sample");

    }

}

