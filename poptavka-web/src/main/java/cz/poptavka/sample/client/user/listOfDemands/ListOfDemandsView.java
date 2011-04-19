package cz.poptavka.sample.client.user.listOfDemands;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListOfDemandsView extends Composite implements ListOfDemandsPresenter.ListOfDemandsViewInterface {

    private static ListOfDemandsViewUiBinder uiBinder = GWT.create(ListOfDemandsViewUiBinder.class);
    interface ListOfDemandsViewUiBinder extends UiBinder<Widget, ListOfDemandsView> {
    }

    @UiField
    VerticalPanel header;
    @UiField
    VerticalPanel table;

    @UiField
    Button answerButton;

    @UiField
    Button editButton;

    @UiField
    Button closeButton;

    @UiField
    Button cancelButton;

    @UiField
    ListBox actionSelectButton;

    @UiField
    FlexTable demandsTable;

    public ListOfDemandsView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public Widget getListOfDemandsView() {
        return this;
    }

    @Override
    public void setBody(Widget body) {
        // TODO Auto-generated method stub

    }

}