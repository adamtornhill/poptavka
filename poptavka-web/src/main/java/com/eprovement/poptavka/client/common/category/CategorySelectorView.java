package com.eprovement.poptavka.client.common.category;

import com.eprovement.poptavka.client.common.category.CategorySelectorPresenter.CategorySelectorInterface;
import com.eprovement.poptavka.client.common.validation.ProvidesValidate;
import com.eprovement.poptavka.resources.StyleResource;
import com.eprovement.poptavka.resources.cellbrowser.CustomCellBrowser;
import com.eprovement.poptavka.shared.domain.CategoryDetail;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.mvp4g.client.view.ReverseViewInterface;

public class CategorySelectorView extends Composite
        implements ProvidesValidate, ReverseViewInterface<CategorySelectorPresenter>, CategorySelectorInterface {

    /**************************************************************************/
    /* UIBINDER                                                               */
    /**************************************************************************/
    private static CategorySelectorUiBinder uiBinder = GWT.create(CategorySelectorUiBinder.class);

    interface CategorySelectorUiBinder extends UiBinder<Widget, CategorySelectorView> {
    }

    /**************************************************************************/
    /* PRESENTER                                                              */
    /**************************************************************************/
    @Override
    public void setPresenter(CategorySelectorPresenter presenter) {
        this.categorySelectorPresenter = presenter;
    }

    @Override
    public CategorySelectorPresenter getPresenter() {
        return categorySelectorPresenter;
    }
    /**************************************************************************/
    /* ATTRIBUTES                                                              */
    /**************************************************************************/
    /** UiBinder attributes. **/
    @UiField HTML loader;
    @UiField SimplePanel cellBrowserHolder;
    @UiField(provided = true) CellList<CategoryDetail> cellList;
    /** Class attributes. **/
    private CategorySelectorPresenter categorySelectorPresenter;
    MultiSelectionModel cellBrowserSelectionModel = new MultiSelectionModel();
    SingleSelectionModel cellListSelectionModel = new SingleSelectionModel();
    ListDataProvider<CategoryDetail> cellListDataProvider = new ListDataProvider<CategoryDetail>();

    /**************************************************************************/
    /* INITIALIZATION                                                         */
    /**************************************************************************/
    @Override
    public void createView() {
        cellList = new CellList<CategoryDetail>(new ItemCell());
        cellList.setSelectionModel(cellListSelectionModel);
        cellListDataProvider.addDataDisplay(cellList);

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void createCellBrowser(int checkboxes, int displayCountsOfWhat) {
        CellBrowser.Resources resource = GWT.create(CustomCellBrowser.class);
        CellBrowser cellBrowser = new CellBrowser(new CategoryTreeViewModel(
                cellBrowserSelectionModel,
                categorySelectorPresenter.getCategoryService(),
                categorySelectorPresenter.getEventBus(),
                checkboxes, displayCountsOfWhat,
                null), null, resource);
        cellBrowser.setAnimationEnabled(true);
        cellBrowserHolder.setWidget(cellBrowser);
    }

    /**************************************************************************/
    /* GETTERS                                                                */
    /**************************************************************************/
    @Override
    public MultiSelectionModel<CategoryDetail> getCellBrowserSelectionModel() {
        return cellBrowserSelectionModel;
    }

    @Override
    public SingleSelectionModel<CategoryDetail> getCellListSelectionModel() {
        return cellListSelectionModel;
    }

    @Override
    public ListDataProvider<CategoryDetail> getCellListDataProvider() {
        return cellListDataProvider;
    }

    @Override
    public boolean isValid() {
        return !cellListDataProvider.getList().isEmpty();
    }

    @Override
    public Widget getWidgetView() {
        return this;
    }
}

/**
 * The Cell used to render a selected Category detail {@link CategoryDetail} from CellBrowser.
 */
class ItemCell extends AbstractCell<CategoryDetail> {

    @Override
    public void render(Context context, CategoryDetail value, SafeHtmlBuilder sb) {
        // Value can be null, so do a null check..
        if (value == null) {
            return;
        }

        // Add category name.
        sb.appendHtmlConstant("<div class=\"selected-Item\">");
        sb.appendEscaped(value.getName());
        // Add image for remove.
        sb.appendHtmlConstant(AbstractImagePrototype.create(
                StyleResource.INSTANCE.images().errorIcon()).getHTML());
        sb.appendHtmlConstant("</div>");
    }
}
