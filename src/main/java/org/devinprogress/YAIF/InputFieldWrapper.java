package org.devinprogress.YAIF;

import cpw.mods.fml.client.FMLClientHandler;
import org.devinprogress.YAIF.Bridges.BaseActionBridge;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by recursiveg on 14-9-11.
 */
public class InputFieldWrapper {
    private static final int fontSize=20;

    private static boolean hasInitiated=false;
    //private boolean enabled=true;      //Reserved for Further Use
    private boolean shown =false;
    private BaseActionBridge bridge=null;

    private AWTGLCanvas canvas = null;
    private JFrame frame=null;
    private JTextField textField = null;
    private JPanel panel=null;


    public InputFieldWrapper(int Width,int Height){ //Should be Called only once
        if(hasInitiated)
            throw new RuntimeException("Double Initiation for InputFieldWrapper.");
        hasInitiated=true;

        // Create Instances
        try {
            canvas = new AWTGLCanvas();
        }catch(Exception e){
            e.printStackTrace();
        }
        frame=new JFrame("Minecraft 1.7.10");
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
        textField.setVisible(false);
        //textField.setVisible(true);
        textField.setFont(new Font("Times New Roman",Font.PLAIN, fontSize));

        // Setup Panel
        panel.setLayout(new BorderLayout());
        panel.add(canvas, BorderLayout.CENTER);
        panel.add(textField, BorderLayout.PAGE_END);
        panel.setVisible(true);
        panel.validate();

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
    }

    public void setupBridge(BaseActionBridge bridge){
        if(!bridge.needShow())return;
        if(this.bridge!=null)
            throw new RuntimeException("Loading new bridge without releasing previous one");
        this.bridge=bridge;

        bridge.bindKeys(textField);
        _show();
    }

    public void releaseCurrentBridge(){
        if(bridge!=null) {
            bridge.unlink(textField);
            bridge = null;
        }
    }

    public void closeInputField(){
        YetAnotherInputFix.log("try closing InputField {shown: %s}",shown);
        if(shown) {
            releaseCurrentBridge();
            textField.getActionMap().clear();
            textField.getInputMap().clear();
            textField.setText("");
            _hide();
            //GuiStateManager.getInstance().inputFieldClosed();
        }
    }

    public void bridgeQuit(){
        closeInputField();
        GuiStateManager.getInstance().inputFieldClosed();
    }

    private void _show(){
        if(!shown) {
            shown = true;
            canvas.setPreferredSize(canvas.getSize());
            textField.setVisible(true);
            //panel.validate();
            frame.pack();
            frame.validate();
            FMLClientHandler.instance().getClient().setIngameNotInFocus();
            textField.requestFocus();
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
    /*
    private void bindKeys(){
    //Should be Called Only Once
    //Be careful about textField.setText(). It will trigger here and further trigger the bridges.
        InputMap inputmap = textField.getInputMap();
        ActionMap actionmap = textField.getActionMap();

        textField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        textField.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke tab=KeyStroke.getKeyStroke(KeyEvent.VK_TAB,0);
        KeyStroke up=KeyStroke.getKeyStroke(KeyEvent.VK_UP,0);
        KeyStroke down=KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0);
        KeyStroke backsp=KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0);

        //TODO: to see if the `if` can be removed
        Action enterAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onEnter(textField),null);
            }
        };
        Action escAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onEsc(textField),null);
            }
        };
        Action tabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onTab(textField),null);
            }
        };
        Action upAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onUp(textField),null);
            }
        };
        Action downAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(bridge!=null)DoActions(bridge.onDown(textField),null);
            }
        };
        Action backspAction=new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(bridge!=null)DoActions(bridge.onBackspace(textField),null);
            }
        };
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(bridge!=null&&doTriggerOnChangeEvent)
                    DoActions(bridge.onChange(textField), null);
                doTriggerOnChangeEvent = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(bridge!=null&&doTriggerOnChangeEvent)
                    DoActions(bridge.onChange(textField), null);
                doTriggerOnChangeEvent = true;
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
        inputmap.put(backsp,"backsp");actionmap.put("backsp",backspAction);
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
            frame.setSize(new Dimension(frame.getWidth(), frame.getHeight() + fontSize));
            textField.setVisible(true);
            FMLClientHandler.instance().getClient().setIngameNotInFocus();
            textField.requestFocus();
            frame.validate();
        }
    }

    public void hide(){
        bridge=null;
        if(!shown)return;
        shown =false;
        textField.setVisible(false);
        frame.setSize(new Dimension(frame.getWidth(), frame.getHeight() - fontSize));

        canvas.requestFocusInWindow();
        frame.validate();
        YetAnotherInputFix.needFocus=true;
    }

    public void DoActions(BaseActionBridge.ActionFeedback action, Object obj){
        if(action== BaseActionBridge.ActionFeedback.Quit){
            hide();
        }else if(action== BaseActionBridge.ActionFeedback.Nothing){
            return;
        }else if(action== BaseActionBridge.ActionFeedback.SetText){
            if (obj instanceof String)
                setTextNoEvent((String)obj);
        }else if(action== BaseActionBridge.ActionFeedback.Clean){
            setTextNoEvent("");
        }
    }

    private BaseActionBridge getBridge(){//Remember to add cases here if new Bridges added.
        if(bridge!=null&&bridge.sameAs(YetAnotherInputFix.currentGuiScreen,YetAnotherInputFix.currentTextField))
            return bridge;
        if(YetAnotherInputFix.currentGuiScreen instanceof GuiChat) {
            GuiChatBridge b = new GuiChatBridge(YetAnotherInputFix.currentTextField, (GuiChat) YetAnotherInputFix.currentGuiScreen, this);
            if(b.isCommand())
                return null;
            else
                return b;
        }
        else if(YetAnotherInputFix.currentGuiScreen instanceof GuiEditSign)
            return new EditSignBridge((GuiEditSign)YetAnotherInputFix.currentGuiScreen,this);
        else return null;
        //else return new CommonBridge(YetAnotherInputFix.currentGuiScreen, this);
    }

    public void setTextNoEvent(final String str){
        //doTriggerOnChangeEvent=false;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textField.setText(str);
                textField.setCaretPosition(textField.getText().length());
            }
        });
    }*/
}
