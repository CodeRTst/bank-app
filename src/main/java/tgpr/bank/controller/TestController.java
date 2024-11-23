package tgpr.bank.controller;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import tgpr.bank.model.Test;
import tgpr.bank.view.TestView;
import tgpr.framework.Controller;

import java.util.List;

public class TestController extends Controller {
    private final TestView view = new TestView(this);

    @Override
    public Window getView() {
        return view;
    }

    public List<Test> getData() {
        return Test.getAll();
    }

    public void displayTest(Test test) {
        showMessage(test.getName(), "Test Detail", MessageDialogButton.OK);
    }
}
