package com.eprovement.poptavka.client.user.widget.detail;

import com.eprovement.poptavka.client.common.category.CategoryCell;
import com.eprovement.poptavka.client.common.locality.LocalityCell;
import com.eprovement.poptavka.client.resources.StyleResource;
import com.eprovement.poptavka.shared.domain.CategoryDetail;
import com.eprovement.poptavka.shared.domain.LocalityDetail;
import com.eprovement.poptavka.shared.domain.demand.FullDemandDetail;
import com.eprovement.poptavka.shared.domain.supplier.FullSupplierDetail;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.LocalizableMessages;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DemandDetailView extends Composite {

    private static DemandDetailViewUiBinder uiBinder = GWT.create(DemandDetailViewUiBinder.class);

    interface DemandDetailViewUiBinder extends UiBinder<Widget, DemandDetailView> {
    }
    @UiField(provided = true) CellList categories, localities;
    @UiField Label demandName, price, endDate, validTo, type, maxNumberOfSuppliers,
    minSupplierRating, excludedSuppliers, description;
    //i18n
    private final StyleResource styleResource = GWT.create(StyleResource.class);
    private LocalizableMessages bundle = (LocalizableMessages) GWT.create(LocalizableMessages.class);
    private NumberFormat currencyFormat = NumberFormat.getFormat(bundle.currencyFormat());
    private DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

    /**************************************************************************/
    /* INITIALIZATON                                                          */
    /**************************************************************************/
    //Constructors
    public DemandDetailView() {
        categories = new CellList<CategoryDetail>(new CategoryCell(CategoryCell.DISPLAY_COUNT_DISABLED));
        localities = new CellList<LocalityDetail>(new LocalityCell(LocalityCell.DISPLAY_COUNT_DISABLED));
        initWidget(uiBinder.createAndBindUi(this));

        setStyle(styleResource.common().textBoxAsLabel());
        StyleResource.INSTANCE.detailViews().ensureInjected();
    }

    /**************************************************************************/
    /* SETTERS                                                                */
    /**************************************************************************/
    public void setDemanDetail(FullDemandDetail demandDetail) {
        GWT.log("detail detail" + demandDetail.toString());
        demandName.setText(demandDetail.getTitle());
        price.setText(currencyFormat.format(demandDetail.getPrice()));
        endDate.setText(dateFormat.format(demandDetail.getEndDate()));
        validTo.setText(dateFormat.format(demandDetail.getValidToDate()));
        type.setText(demandDetail.getDetailType().getValue());
        categories.setRowData(demandDetail.getCategories());
        localities.setRowData(demandDetail.getLocalities());
        maxNumberOfSuppliers.setText(Integer.toString(demandDetail.getMaxOffers()));
        minSupplierRating.setText(Integer.toString(demandDetail.getMinRating()) + "%");
        StringBuilder excludedSuppliersBuildes = new StringBuilder();
        for (FullSupplierDetail supplierDetail : demandDetail.getExcludedSuppliers()) {
            excludedSuppliersBuildes.append(supplierDetail.getCompanyName());
            excludedSuppliersBuildes.append(", ");
        }
        excludedSuppliers.setText(excludedSuppliersBuildes.toString());
        description.setText(demandDetail.getDescription());
//        detail.getElement().getFirstChildElement().getStyle().setDisplay(Display.BLOCK);
    }

    private void setStyle(String cssStyle) {
        demandName.setStyleName(cssStyle);
        price.setStyleName(cssStyle);
        endDate.setStyleName(cssStyle);
        validTo.setStyleName(cssStyle);
        type.setStyleName(cssStyle);
        maxNumberOfSuppliers.setStyleName(cssStyle);
        minSupplierRating.setStyleName(cssStyle);
        excludedSuppliers.setStyleName(cssStyle);
        description.setStyleName(cssStyle);
    }
}
