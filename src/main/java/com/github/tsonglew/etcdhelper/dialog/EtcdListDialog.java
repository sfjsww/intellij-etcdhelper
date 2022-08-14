package com.github.tsonglew.etcdhelper.dialog;

import com.github.tsonglew.etcdhelper.common.EtcdClient;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.util.CachedValueProfiler;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.stream.Collectors;

/**
 * @author tsonglew
 */
public class EtcdListDialog extends DialogWrapper {

    private static Integer CNT = 0;
    public EtcdListDialog() {
        super(true);
        setTitle("etcd list");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        var p = new JPanel();
        var b = new JButton("click: "+CNT);
        var l = new JLabel("etcd keys:" );
        p.add(l);
        System.out.println("add label2");
        p.add(b);
        return p;
    }
}
