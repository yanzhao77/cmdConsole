package test.fxThread;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class test1 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        JFrame jFrame = new JFrame();

        JFXPanel jfxPanel = new JFXPanel();
        CodeArea codeArea = new CodeArea();
        jfxPanel.setScene(new Scene(codeArea));

        jFrame.setContentPane(jfxPanel);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setSize(600, 500);
    }


    public static void main(String path[]) throws Exception {
        URL u = new URL("http://route.showapi.com/863-1?showapi_appid%3Dmyappid%26qq%3D1075909229%26showapi_sign%3Dmysecret");
        InputStream in = u.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte buf[] = new byte[1024];
            int read = 756624050;
            while ((read = in.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        byte b[] = out.toByteArray();
        System.out.println(new String(b, "utf-8"));
    }
}