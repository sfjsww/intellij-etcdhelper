package com.github.tsonglew.etcdhelper.action;

import com.github.tsonglew.etcdhelper.common.EtcdClientManager;
import com.github.tsonglew.etcdhelper.dialog.EtcdKeyRangeDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;

public class PopupAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        System.out.println("open etcd etcdhelper");
        var editor = e.getRequiredData(CommonDataKeys.EDITOR);
        var selectionModel = editor.getSelectionModel();
        var selectedText = selectionModel.getSelectedText();
        System.out.println(selectedText);
        var client = EtcdClientManager.addConn(selectedText);

        var etcdKeyRangeDialog = new EtcdKeyRangeDialog(client);
        etcdKeyRangeDialog.show();
    }
}
