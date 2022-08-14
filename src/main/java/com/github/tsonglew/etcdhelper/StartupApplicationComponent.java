package com.github.tsonglew.etcdhelper;

import com.github.tsonglew.etcdhelper.common.EtcdClient;
import com.github.tsonglew.etcdhelper.dialog.EtcdListDialog;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.xml.util.XmlApplicationComponent;

/**
 * @author tsonglew
 */
public class StartupApplicationComponent implements ApplicationComponent {
    @Override
    public void initComponent() {
        System.out.println("init component");
        var d =new EtcdListDialog();
        d.show();
    }
}
