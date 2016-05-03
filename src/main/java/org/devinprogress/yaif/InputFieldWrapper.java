package org.devinprogress.yaif;

import net.minecraft.util.Util;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.devinprogress.yaif.bridges.BaseActionBridge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

public class InputFieldWrapper {
    private static final int fontSize=20;

    private static boolean hasInitiated=false;
    private boolean shown =false;
    private BaseActionBridge bridge=null;

    private AWTGLCanvas canvas = null;
    private JFrame frame=null;
    private JTextField textField = null;
    private JPanel panel=null;

    public static InputFieldWrapper instance;

    public InputFieldWrapper(int Width,int Height){ //Should be Called only once
        if(hasInitiated)
            throw new RuntimeException("Double Initiation for InputFieldWrapper.");
        hasInitiated=true;
        instance=this;
        // Create Instances
        try {
            canvas = new AWTGLCanvas();
        }catch(Exception e){
            e.printStackTrace();
        }
        frame=new JFrame("Minecraft 1.9");
        textField=new JTextField();
        panel=new JPanel();

        // Setup Canvas
        canvas.setFocusable(true);
        try {
            Display.setParent(canvas);
        }catch(Exception e){
            e.printStackTrace();
        }
        canvas.setPreferredSize(new Dimension(Width, Height));

        // Setup TextField
        textField.setVisible(shown);
        textField.setFont(new Font("Times New Roman",Font.PLAIN, fontSize));
        //textField.enableInputMethods(false);

        // Setup Panel
        panel.setLayout(new BorderLayout());
        panel.add(canvas, BorderLayout.CENTER);
        panel.add(textField, BorderLayout.PAGE_END);
        panel.setVisible(true);
        panel.validate();
        if(Util.getOSType()==Util.EnumOS.OSX) {  //OSX blacks-screen patch
            panel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    //YetAnotherInputFix.log("Panel Resizing...");
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            panel.requestFocusInWindow();
                            canvas.requestFocusInWindow();
                            canvas.requestFocus();
                            YetAnotherInputFix.needCurrent = true;
                        }
                    });
                }
            });
        }

        // Setup frame
        frame.setUndecorated(false);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setContentPane(panel);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                FMLClientHandler.instance().getClient().shutdown();
            }
        });
        frame.pack();
        frame.validate();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //frame.setAutoRequestFocus(true);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void setupBridge(BaseActionBridge bridge){
        if(!bridge.needShow())return;
        if(this.bridge!=null) {
            LogManager.getLogger("YAIF").warn("Loading new bridge without releasing previous one");
            releaseCurrentBridge();
        }
        this.bridge=bridge;
        textField.setText("");
        bridge.bindKeys(textField);

        FMLCommonHandler.instance().bus().register(new switchListener());

        //_show();
    }

    public void releaseCurrentBridge(){
        if(bridge!=null) {
            bridge.unlink(textField);
            bridge = null;
            textField.setText("");
        }
    }

    public void closeInputField(){
        //YetAnotherInputFix.log("try closing InputField {shown: %s}",shown);
        if(shown) {
            releaseCurrentBridge();
            textField.getActionMap().clear();
            textField.getInputMap().clear();
            textField.setText("");
            _hide();
        }
    }

    public void bridgeQuit(){
        closeInputField();
        GuiStateManager.getInstance().inputFieldClosed();
    }

    public void _show(){
        if(!shown) {
            shown = true;
            canvas.setPreferredSize(canvas.getSize());
            textField.setVisible(true);
            FMLClientHandler.instance().getClient().setIngameNotInFocus();
            textField.requestFocus();
            textField.getInputMethodRequests();
            frame.pack();
            frame.validate();
        }else {
            FMLClientHandler.instance().getClient().setIngameNotInFocus();
            textField.requestFocus();
        }
    }

    private void _hide(){
        if(shown){
            shown=false;
            canvas.setPreferredSize(canvas.getSize());
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textField.setVisible(false);
                    canvas.requestFocusInWindow();
                    try {
                        canvas.makeCurrent();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    panel.validate();
                    frame.pack();
                    frame.validate();
                }
            });
        }
    }

    public String getText(){
        return textField.getText();
    }

    public void setText(String text){
        if(text.length()==0&&textField.getText().length()==0)
            bridge.releaseObstacleFlag();
        textField.setText(text);
        textField.setCaretPosition(text.length());
    }

    public void setText(String text,final int caretPos){
        textField.setText(text);
        textField.setCaretPosition(caretPos);
    }

    public int getCaretPosition() {
        return textField.getCaretPosition();
    }
}
