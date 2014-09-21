package org.devinprogress.YAIF;

import com.sun.istack.internal.Nullable;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiEditSign;
import org.devinprogress.YAIF.Bridges.EditSignBridge;
import org.devinprogress.YAIF.Bridges.GuiChatBridge;
import org.devinprogress.YAIF.Bridges.IActionBridge;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;

/**
 * Created by recursiveg on 14-9-11.
 */
public class InputFieldWrapper {
    private static final int TextFieldHeight=25;

    private static boolean hasInitiated=false;
    private boolean enabled=true;      //Reserved for Further Use
    private boolean shown =false;
    private boolean doTriggerOnChangeEvent=true;
    private IActionBridge bridge=null;

    private AWTGLCanvas canvas = null;
    private final JFrame frame=new JFrame("Minecraft");
    private JTextField txtField = null;

    public InputFieldWrapper(int Width,int Height){ //Should be Called only once
        if(hasInitiated){
            YetAnotherInputFix.logger.severe("Double Initiation for InputFieldWrapper.");
            return;
        }
        hasInitiated=true;

        try {
            canvas = new AWTGLCanvas();
        }catch(Exception e){
            e.printStackTrace();
        }
        canvas.setFocusable(true);
        txtField =new JTextField();

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                FMLClientHandler.instance().getClient().shutdown();
            }
        });
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

    private void bindKeys(){
    //Should be Called Only Once
    //Be careful about txtField.setText(). It will trigger here and further trigger the bridges.
        InputMap inputmap = txtField.getInputMap();
        ActionMap actionmap = txtField.getActionMap();

        txtField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        txtField.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke tab=KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0);
        KeyStroke up=KeyStroke.getKeyStroke(KeyEvent.VK_UP,0);
        KeyStroke down=KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0);

        //TODO: to see if the `if` can be removed
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
        Action upAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onUp(txtField),null);
            }
        };
        Action downAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onDown(txtField),null);
            }
        };
        txtField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(bridge!=null)if(doTriggerOnChangeEvent)DoActions(bridge.onChange(txtField),null);else doTriggerOnChangeEvent=true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(bridge!=null)if(doTriggerOnChangeEvent)DoActions(bridge.onChange(txtField),null);else doTriggerOnChangeEvent=true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        inputmap.put(enter, "enter");actionmap.put("enter", enterAction);
        inputmap.put(esc, "esc");actionmap.put("esc", escAction);
        inputmap.put(tab,"tab");actionmap.put("tab", tabAction);
        inputmap.put(up,"up");actionmap.put("up", upAction);
        inputmap.put(down,"down");actionmap.put("down", downAction);
    }

    public void setEnabled(boolean flag){  //Reserved for further use
        if(flag)
            enabled=true;
        else{
            enabled=false;
            hide();
        }
    }

    public void show(){//called when GuiTextField: New/Re-click/change
        if(!enabled)return;
        bridge=getBridge();
        if((!shown)&&bridge!=null) {
            shown = true;
            frame.setSize(new Dimension(frame.getWidth(), frame.getHeight() + TextFieldHeight));
            txtField.setVisible(true);
            FMLClientHandler.instance().getClient().setIngameNotInFocus();
            txtField.requestFocus();
            frame.validate();
        }
    }

    //TODO: Fix Bugs about focus
    public void hide(){
        bridge=null;
        if(!shown)return;
        shown =false;
        txtField.setVisible(false);
        frame.setSize(new Dimension(frame.getWidth(), frame.getHeight() - TextFieldHeight));

        canvas.requestFocusInWindow();
        frame.validate();
        FMLClientHandler.instance().getClient().setIngameFocus();
    }

    public void DoActions(IActionBridge.ActionFeedback action, Object obj){
        if(action== IActionBridge.ActionFeedback.Quit){
            hide();
        }else if(action== IActionBridge.ActionFeedback.Nothing){
            return;
        }else if(action== IActionBridge.ActionFeedback.SetText){
            if (obj instanceof String)
                setTextNoEvent((String)obj);
        }else if(action== IActionBridge.ActionFeedback.Clean){
            setTextNoEvent("");
        }
    }

    @Nullable
    private IActionBridge getBridge(){//Remember to add cases here if new Bridges added.
        if(bridge!=null&&bridge.sameAs(YetAnotherInputFix.currentGuiScreen,YetAnotherInputFix.currentTextField))
            return bridge;
        if(YetAnotherInputFix.currentGuiScreen instanceof GuiChat)
            return new GuiChatBridge(YetAnotherInputFix.currentTextField, (GuiChat)YetAnotherInputFix.currentGuiScreen,this);
        else if(YetAnotherInputFix.currentGuiScreen instanceof GuiEditSign)
            return new EditSignBridge((GuiEditSign)YetAnotherInputFix.currentGuiScreen,this);
        else
            return null;
        //else bridge=new CommonBridge(YetAnotherInputFix.currentTextField, this);
    }

    public void setTextNoEvent(String str){
        doTriggerOnChangeEvent=false;
        txtField.setText(str);
        txtField.setCaretPosition(txtField.getText().length());
    }
}
