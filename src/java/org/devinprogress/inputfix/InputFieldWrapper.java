package org.devinprogress.inputfix;

import cpw.mods.fml.client.FMLClientHandler;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;

/**
 * Created by recursiveg on 14-9-11.
 */
public class InputFieldWrapper {
    private static final int TextFieldHeight=25;
    private Canvas canvas = null;
    private JFrame frame = null;
    private JTextField txtField = null;
    private boolean Showed=false;
    private IActionBridge bridge=null;

    public InputFieldWrapper(int Width,int Height){
        canvas =new Canvas();
        frame =new JFrame("Minecraft");
        txtField =new JTextField();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
        frame.add(canvas,BorderLayout.CENTER);
        try {
            Display.setParent(canvas);
        }catch(Exception e){
            e.printStackTrace();
        }
        frame.setPreferredSize(new Dimension(Width, Height));
        frame.pack();

        txtField.setVisible(false);
        txtField.setPreferredSize(new Dimension(Width, TextFieldHeight));
        bindKeys();
        frame.add(txtField, BorderLayout.PAGE_END);

        frame.pack();
        frame.validate();
    }

    public void show(){
        buildBridge();
        if(Showed)return;
        Showed=true;
        frame.setSize(new Dimension(frame.getWidth(), frame.getHeight() + TextFieldHeight));
        txtField.setVisible(true);
        txtField.setCaretPosition(txtField.getText().length());
        //txtField.setText("");
        FMLClientHandler.instance().getClient().setIngameNotInFocus();
        txtField.requestFocus();
        frame.validate();
    }
    public void hide(){
        bridge=null;
        if(!Showed)return;
        Showed=false;
        txtField.setVisible(false);
        txtField.setText("");
        frame.setSize(new Dimension(frame.getWidth(), frame.getHeight() - TextFieldHeight));
        canvas.requestFocus();
        FMLClientHandler.instance().getClient().setIngameFocus();
        frame.validate();
    }
    public void DoActions(IActionBridge.ActionFeedback action, Object obj){
        if(action== IActionBridge.ActionFeedback.Quit){
            hide();
        }else if(action== IActionBridge.ActionFeedback.Nothing){
            return;
        }else if(action== IActionBridge.ActionFeedback.SetText){
            if (obj instanceof String)
                txtField.setText((String)obj);
        }else if(action== IActionBridge.ActionFeedback.Clean){
            txtField.setText("");
        }
    }

    private void bindKeys(){
        InputMap inputmap = txtField.getInputMap();
        ActionMap actionmap = txtField.getActionMap();

        txtField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        txtField.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke tab=KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0);
        Action enterAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onEnter(txtField),null);
            }
        };
        Action escAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onEsc(txtField),null);
            }
        };
        Action tabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onTab(txtField),null);
            }
        };
        inputmap.put(enter, "enter");actionmap.put("enter", enterAction);
        inputmap.put(esc, "esc");actionmap.put("esc", escAction);
        inputmap.put(tab,"tab");actionmap.put("tab",tabAction);
        txtField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(bridge!=null)DoActions(bridge.onChanged(txtField),null);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(bridge!=null)DoActions(bridge.onChanged(txtField),null);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    private void buildBridge(){//TODO
        //bridge=new DebugBridge(Main.currentTextField,Main.currentGuiScreen,this);
        bridge=new CommonBridge(Main.currentTextField,Main.currentGuiScreen,this);
    }
}
