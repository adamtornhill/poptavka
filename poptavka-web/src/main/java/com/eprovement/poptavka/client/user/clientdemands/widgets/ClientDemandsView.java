package com.eprovement.poptavka.client.user.clientdemands.widgets;

import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid;
import com.eprovement.poptavka.shared.domain.clientdemands.ClientDemandConversationDetail;
import com.eprovement.poptavka.shared.domain.clientdemands.ClientDemandDetail;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ClientDemandsView extends Composite
        implements ClientDemandsPresenter.ClientDemandsLayoutInterface {

    private static ClientDemandsLayoutViewUiBinder uiBinder = GWT.create(ClientDemandsLayoutViewUiBinder.class);

    interface ClientDemandsLayoutViewUiBinder extends UiBinder<Widget, ClientDemandsView> {
    }
    /**************************************************************************/
    /* DemandTable Attributes                                                 */
    /**************************************************************************/
    //table definition
    @UiField(provided = true)
    UniversalAsyncGrid<ClientDemandDetail> demandGrid;
    //table column width constatnts
    private static final int TITLE_COL_WIDTH = 50;
    private static final int PRICE_COL_WIDTH = 30;
    private static final int FINNISH_DATE_COL_WIDTH = 30;
    private static final int VALID_TO_DATE_COL_WIDTH = 30;
    //pager definition
    @UiField(provided = true)
    SimplePager demandPager;
    @UiField(provided = true)
    ListBox demandPageSize;
    /**************************************************************************/
    /* DemandConversationTable Attrinbutes                                         */
    /**************************************************************************/
    //table definition
    @UiField(provided = true)
    UniversalAsyncGrid<ClientDemandConversationDetail> conversationGrid;
    //table columns
    private Header checkHeader;
    private Column<ClientDemandConversationDetail, Boolean> checkColumn;
    private Column<ClientDemandConversationDetail, Boolean> starColumn;
    private Column<ClientDemandConversationDetail, ImageResource> replyColumn;
    private Column<ClientDemandConversationDetail, String> supplierNameColumn;
    private Column<ClientDemandConversationDetail, String> bodyPreviewColumn;
    private Column<ClientDemandConversationDetail, String> dateColumn;
    //table column width constatnts
    private static final int SUPPLIER_NAME_COL_WIDTH = 20;
    private static final int BODY_PREVIEW_COL_WIDTH = 30;
    private static final int DATE_COL_WIDTH = 20;
    //pager definition
    @UiField(provided = true)
    SimplePager conversationPager;
    @UiField(provided = true)
    ListBox conversationPageSize;
    /**************************************************************************/
    /* Attrinbutes                                                            */
    /**************************************************************************/
    //TODO Martin - ako i18n format datumu?
    private DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);
    //detail WrapperPanel
    @UiField
    SimplePanel wrapperPanel;
    @UiField
    Label demandTitlelabel;
    @UiField(provided = true)
    ListBox actions;
    @UiField
    HorizontalPanel demandHeader, conversationHeader;

    /**************************************************************************/
    /* Initialization                                                         */
    /**************************************************************************/
    @Override
    public void createView() {
        //load custom grid cssStyle
        Storage.RSCS.grid().ensureInjected();
        //init pagesize lsit
        demandPageSize = new ListBox();
        demandPageSize.addItem("10");
        demandPageSize.addItem("20");
        demandPageSize.addItem("30");
        demandPageSize.setSelectedIndex(2);

        conversationPageSize = new ListBox();
        conversationPageSize.addItem("10");
        conversationPageSize.addItem("20");
        conversationPageSize.addItem("30");
        conversationPageSize.setSelectedIndex(2);

        actions = new ListBox();
        actions.addItem(Storage.MSGS.action());
        actions.addItem(Storage.MSGS.read());
        actions.addItem(Storage.MSGS.unread());
        actions.addItem(Storage.MSGS.star());
        actions.addItem(Storage.MSGS.unstar());
        actions.setSelectedIndex(0);

        initDemandTable();
        initConversationTable();

        initWidget(uiBinder.createAndBindUi(this));
        setConversationTableVisible(false);
    }

    /**
     * Initialize this example.
     */
    private void initDemandTable() {
        List<String> gridColumns = Arrays.asList(
                new String[]{
                    "status", "title", "price", "finnishDate", "validTo"
                });
        // Create a CellTable.
        demandGrid = new UniversalAsyncGrid<ClientDemandDetail>(gridColumns);
        demandGrid.setWidth("800px");
        demandGrid.setHeight("500px");
//        demandGrid.setLoadingIndicator(new Label("Loading, please wait ..."));
        demandGrid.setRowCount(Integer.valueOf(demandPageSize.getItemText(demandPageSize.getSelectedIndex())), true);
        demandGrid.setPageSize(Integer.valueOf(demandPageSize.getItemText(demandPageSize.getSelectedIndex())));
        // Selection Model - must define different from default which is used in UniversalAsyncGrid
        // Add a selection model so we can select cells.
        final SelectionModel<ClientDemandDetail> selectionModel =
                new SingleSelectionModel<ClientDemandDetail>(ClientDemandDetail.KEY_PROVIDER);
        demandGrid.setSelectionModel(selectionModel);

        // Create a Pager to control the table.
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        demandPager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        demandPager.setDisplay(demandGrid);

        initDemandTableColumns();
    }

    /**
     * Initialize this example.
     */
    private void initConversationTable() {
        List<String> gridColumns = Arrays.asList(new String[]{"supplierName", "body", "date"});
        // Create a CellTable.
        conversationGrid = new UniversalAsyncGrid<ClientDemandConversationDetail>(gridColumns);
        conversationGrid.setWidth("800px");
        conversationGrid.setHeight("500px");

        conversationGrid.setRowCount(Integer.valueOf(
                conversationPageSize.getItemText(conversationPageSize.getSelectedIndex())), true);
        conversationGrid.setPageSize(getConversationPageSize());
        // Selection Model - must define different from default which is used in UniversalAsyncGrid
        // Add a selection model so we can select cells.
        final SelectionModel<ClientDemandConversationDetail> selectionModel =
                new MultiSelectionModel<ClientDemandConversationDetail>(ClientDemandConversationDetail.KEY_PROVIDER);
        conversationGrid.setSelectionModel(
                selectionModel, DefaultSelectionEventManager.<ClientDemandConversationDetail>createCheckboxManager());

        // Create a Pager to control the table.
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        conversationPager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        conversationPager.setDisplay(conversationGrid);

        initConversationTableColumns();
    }

    /**
     * Create all columns to the grid.
     */
    public void initDemandTableColumns() {
        // Demand Status column
        demandGrid.addDemandStatusColumn(Storage.MSGS.status());
        // Demand title column
        demandGrid.addColumn(
                demandGrid.TABLE_CLICKABLE_TEXT_CELL, Storage.MSGS.title(), true, TITLE_COL_WIDTH,
                new UniversalAsyncGrid.GetValue<String>() {
                    @Override
                    public String getValue(Object object) {
                        ClientDemandDetail clientDetail = (ClientDemandDetail) object;
                        return ClientDemandDetail.displayHtml(clientDetail.getTitle(), clientDetail.isRead());
                    }
                });

        // Demand price column
        demandGrid.addColumn(
                demandGrid.TABLE_CLICKABLE_TEXT_CELL, Storage.MSGS.price(), true, PRICE_COL_WIDTH,
                new UniversalAsyncGrid.GetValue<String>() {
                    @Override
                    public String getValue(Object object) {
                        ClientDemandDetail clientDetail = (ClientDemandDetail) object;
                        return ClientDemandDetail.displayHtml(clientDetail.getPrice(), clientDetail.isRead());
                    }
                });

        // Finnish date column
        demandGrid.addColumn(
                demandGrid.TABLE_CLICKABLE_TEXT_CELL, Storage.MSGS.finnishDate(), true, FINNISH_DATE_COL_WIDTH,
                new UniversalAsyncGrid.GetValue<String>() {
                    @Override
                    public String getValue(Object object) {
                        ClientDemandDetail clientDetail = (ClientDemandDetail) object;
                        return ClientDemandDetail.displayHtml(
                                formatter.format(clientDetail.getEndDate()),
                                clientDetail.isRead());
                    }
                });

        // Valid-to date column
        demandGrid.addColumn(
                new TextCell(), Storage.MSGS.validTo(), true, VALID_TO_DATE_COL_WIDTH,
                new UniversalAsyncGrid.GetValue<String>() {
                    @Override
                    public String getValue(Object object) {
                        ClientDemandDetail clientDetail = (ClientDemandDetail) object;
                        return ClientDemandDetail.displayHtml(
                                formatter.format(clientDetail.getValidToDate()),
                                clientDetail.isRead());
                    }
                });
    }

    /**
     * Create all columns to the grid.
     */
    public void initConversationTableColumns() {
        // CheckBox column
        checkHeader = new Header<Boolean>(new CheckboxCell()) {
            @Override
            public Boolean getValue() {
                return false;
            }
        };
        checkColumn = conversationGrid.addCheckboxColumn(checkHeader);
        // Star Column
        starColumn = conversationGrid.addStarColumn();
        // Reply Column
        replyColumn = conversationGrid.addIconColumn(
                Storage.RSCS.images().replyImage(),
                Storage.MSGS.replyExplanationText());
        // Demand title column
        supplierNameColumn = conversationGrid.addColumn(
                conversationGrid.TABLE_CLICKABLE_TEXT_CELL, Storage.MSGS.supplierName(), true, SUPPLIER_NAME_COL_WIDTH,
                new UniversalAsyncGrid.GetValue<String>() {
                    @Override
                    public String getValue(Object object) {
                        ClientDemandConversationDetail detail = (ClientDemandConversationDetail) object;
                        return ClientDemandConversationDetail.displayHtml(detail.getSupplierName(), detail.isRead());
                    }
                });

        // Demand price column
        bodyPreviewColumn = conversationGrid.addColumn(
                conversationGrid.TABLE_CLICKABLE_TEXT_CELL, Storage.MSGS.text(), false, BODY_PREVIEW_COL_WIDTH,
                new UniversalAsyncGrid.GetValue<String>() {
                    @Override
                    public String getValue(Object object) {
                        ClientDemandConversationDetail detail = (ClientDemandConversationDetail) object;
                        StringBuilder str = new StringBuilder();
                        str.append(((ClientDemandConversationDetail) object).getMessageDetail().getBody());
                        str.append("...");
                        return ClientDemandConversationDetail.displayHtml(str.toString(), detail.isRead());
                    }
                });

        // Date column
        dateColumn = conversationGrid.addColumn(
                conversationGrid.TABLE_CLICKABLE_TEXT_CELL, Storage.MSGS.date(), true, DATE_COL_WIDTH,
                new UniversalAsyncGrid.GetValue<String>() {
                    @Override
                    public String getValue(Object object) {
                        ClientDemandConversationDetail detail = (ClientDemandConversationDetail) object;
                        return ClientDemandConversationDetail.displayHtml(
                                formatter.format(detail.getDate()),
                                detail.isRead());
                    }
                });
    }

    /**************************************************************************/
    /* Getters                                                                */
    /**************************************************************************/
    // Columns
    @Override
    public Header getCheckHeader() {
        return checkHeader;
    }

    @Override
    public Column<ClientDemandConversationDetail, Boolean> getCheckColumn() {
        return checkColumn;
    }

    @Override
    public Column<ClientDemandConversationDetail, Boolean> getStarColumn() {
        return starColumn;
    }

    public Column<ClientDemandConversationDetail, ImageResource> getReplyColumn() {
        return replyColumn;
    }

    @Override
    public Column<ClientDemandConversationDetail, String> getSupplierNameColumn() {
        return supplierNameColumn;
    }

    @Override
    public Column<ClientDemandConversationDetail, String> getBodyPreviewColumn() {
        return bodyPreviewColumn;
    }

    @Override
    public Column<ClientDemandConversationDetail, String> getDateColumn() {
        return dateColumn;
    }

    @Override
    public ListBox getActions() {
        return actions;
    }

    // Others
    @Override
    public UniversalAsyncGrid<ClientDemandDetail> getDemandGrid() {
        return demandGrid;
    }

    @Override
    public UniversalAsyncGrid<ClientDemandConversationDetail> getConversationGrid() {
        return conversationGrid;
    }

    //Nemusi byt override nie?
    @Override
    public int getConversationPageSize() {
        return Integer.valueOf(conversationPageSize.getItemText(conversationPageSize.getSelectedIndex()));
    }

    @Override
    public List<Long> getSelectedIdList() {
        List<Long> idList = new ArrayList<Long>();
        Set<ClientDemandConversationDetail> set = getSelectedMessageList();
        Iterator<ClientDemandConversationDetail> it = set.iterator();
        while (it.hasNext()) {
            idList.add(it.next().getUserMessageId());
        }
        return idList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<ClientDemandConversationDetail> getSelectedMessageList() {
        MultiSelectionModel<ClientDemandConversationDetail> model =
                (MultiSelectionModel<ClientDemandConversationDetail>) conversationGrid.getSelectionModel();
        return model.getSelectedSet();
    }

    @Override
    public SimplePanel getWrapperPanel() {
        return wrapperPanel;
    }

    @Override
    public IsWidget getWidgetView() {
        return this;
    }

    /**************************************************************************/
    /* Setters                                                                */
    /**************************************************************************/
    @Override
    public void setConversationTableVisible(boolean visible) {
        demandGrid.setVisible(!visible);
        demandPager.setVisible(!visible);
        demandPageSize.setVisible(!visible);
        demandHeader.setVisible(!visible);

        conversationGrid.setVisible(visible);
        conversationPager.setVisible(visible);
        conversationPageSize.setVisible(visible);
        conversationHeader.setVisible(visible);
    }

    @Override
    public void setDemandTitleLabel(String text) {
        demandTitlelabel.setText(text);
    }
}
