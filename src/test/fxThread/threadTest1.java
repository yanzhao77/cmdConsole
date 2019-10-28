package test.fxThread;

import javafx.application.Application;
import javafx.application.Platform;
import test.fxThread.FXMLLauncher;

public class threadTest1 {
    public static void main(String[] args) {
        //启动fx线程
        Thread fxThread = new Thread(() -> {
            try {
                Application.launch(FXMLLauncher.class);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });
        fxThread.setDaemon(true);
        fxThread.start();

        //主线程输出
        System.out.println(Thread.currentThread().getName() + "say: hello World");

        //主线程 休眠 1秒 确保fx线程启动成功

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //第一次调用fx线程输出
        Platform.runLater(() -> {
            System.out.println(Thread.currentThread().getName() + "say: Hello javaFX1");
        });


        //第二次调用fx线程输出
        Platform.runLater(() -> {
            System.out.println(Thread.currentThread().getName() + "say: Hello javaFX2");
        });

        //主线程  休眠 1秒 确保线程运行结束

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //中断fxThread程序退出
        fxThread.interrupt();
//        fxThread.stop();
    }
}
