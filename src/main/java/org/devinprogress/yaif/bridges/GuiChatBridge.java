package org.devinprogress.yaif.bridges;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.devinprogress.yaif.InputFieldWrapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

// Author: Recursive G
// Source released under GPLv2
// Full document under resources/LICENSE

public class GuiChatBridge extends BaseActionBridge {
    private GuiChat screen=null;
    private GuiTextField txt=null;
    private InputFieldWrapper wrapper=null;

    public GuiChatBridge(GuiTextField textField,GuiChat screen,InputFieldWrapper wrapper){
        this.screen=screen;
        txt=textField;
        this.wrapper=wrapper;
    }

    @Override
    public boolean needShow(){
        return !screen.defaultInputFieldText.equals("/");
    }

    @Override
    public void bindKeys(JTextField tf){
        super.bindKeys(tf);

        bindKey(tf, KeyEvent.VK_ENTER,"enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txt.setText(wrapper.getText());
                try {
                    screen.keyTyped('\n', 28);
                }catch(Exception ex){ex.printStackTrace();}
                wrapper.bridgeQuit();
            }
        });

        bindKey(tf, KeyEvent.VK_ESCAPE, "esc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //YetAnotherInputFix.log("GuiChatBridge ESC Pressed");
                wrapper.bridgeQuit();
            }
        });

        bindKey(tf,KeyEvent.VK_UP,"up",new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatch(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            screen.keyTyped(' ', 200);
                        }catch(Exception ex){ex.printStackTrace();}
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                textChangedByBridge=true;
                                wrapper.setText(txt.getText());
                            }
                        });
                    }
                });
            }
        });

        bindKey(tf,KeyEvent.VK_DOWN,"down",new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatch(new Runnable() {
                    @Override
                    public void run() {
                        try{
                        screen.keyTyped(' ', 208);
                        }catch(Exception ex){ex.printStackTrace();}
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                textChangedByBridge=true;
                                wrapper.setText(txt.getText());
                            }
                        });
                    }
                });
            }
        });

        bindKey(tf, KeyEvent.VK_TAB, "tab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int cursorPos=wrapper.getCaretPosition();
                dispatch(new Runnable() {
                    @Override
                    public void run() {
                        txt.setCursorPosition(cursorPos);
                        try{
                        screen.keyTyped('\t',15);
                        }catch(Exception ex){ex.printStackTrace();}
                        final String str=txt.getText();
                        final int pos=txt.getCursorPosition();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                textChangedByBridge=true;
                                wrapper.setText(str,pos);
                            }
                        });
                    }
                });
            }
        });

        setListenDocumentEvent(tf);
    }

    @Override
    protected void textUpdated(){
        final String str=wrapper.getText();
        if(str.length()>100){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textChangedByBridge=true;
                    wrapper.setText(str.substring(0,100));
                }
            });
            this.txt.setText(str.substring(0,100));
        }else {
            this.txt.setText(str);
        }
    }

    @Override
    public void onTabComplete(GuiChat screen){
        if(screen!=this.screen)
            throw new RuntimeException("WTF onTabComplete Received but screen not match?!");
        final String str=txt.getText();
        final int pos=txt.getCursorPosition();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textChangedByBridge=true;
                wrapper.setText(str,pos);
            }
        });
    }
}
