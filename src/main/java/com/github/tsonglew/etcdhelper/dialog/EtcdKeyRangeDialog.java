package com.github.tsonglew.etcdhelper.dialog;

import com.github.tsonglew.etcdhelper.common.EtcdClient;
import com.github.tsonglew.etcdhelper.common.EtcdClientManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import com.intellij.xml.util.XmlTagRuleProviderBase;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * @author tsonglew
 */
public class EtcdKeyRangeDialog extends DialogWrapper {

    private EditorTextField key;
    private EditorTextField val;
    private final EtcdClient client;

    public EtcdKeyRangeDialog(EtcdClient client) {
        super(true);
        this.client = client;
        setTitle("get by key prefix: " + Arrays.toString(client.endpoints));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        var panel = new JPanel(new BorderLayout());
        key = new EditorTextField("key");
        key.setPreferredWidth(800);
        val = new EditorTextField("value");
        val.setPreferredWidth(800);
        panel.add(key, BorderLayout.NORTH);
        panel.add(val, BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected JComponent createSouthPanel() {
        var panel = new JPanel();
        var button = new JButton("enter");
        button.addActionListener(e -> {
            var k = key.getText();
            System.out.println("get key: " + k);
            val.setText(client.getByPrefix(k, 0).get(0).getValue().toString());
        });
        panel.add(button);
        return panel;
    }
}
