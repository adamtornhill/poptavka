package com.eprovement.poptavka.client.common.search;

import com.eprovement.poptavka.client.root.BaseChildEventBus;
import com.eprovement.poptavka.shared.domain.CategoryDetail;
import com.eprovement.poptavka.shared.domain.LocalityDetail;
import com.eprovement.poptavka.shared.search.SearchModuleDataHolder;
import com.google.gwt.user.client.ui.SimplePanel;
import com.mvp4g.client.annotation.Debug;
import com.mvp4g.client.annotation.Debug.LogLevel;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.event.EventBus;
import java.util.List;

@Debug(logLevel = LogLevel.DETAILED)
@Events(startPresenter = SearchModulePresenter.class, module = SearchModule.class)
public interface SearchModuleEventBus extends EventBus, BaseChildEventBus {

    /**************************************************************************/
    /* Navigation events.                                                     */
    /**************************************************************************/
    @Event(handlers = SearchModulePresenter.class)
    void goToSearchModule();

    /**************************************************************************/
    /* Parent events - navigation events                                      */
    /**************************************************************************/
    @Event(forwardToParent = true)
    void goToHomeDemandsModule(SearchModuleDataHolder filter);

    @Event(forwardToParent = true)
    void goToHomeSuppliersModule(SearchModuleDataHolder filter);

    @Event(forwardToParent = true)
    void goToClientDemandsModule(SearchModuleDataHolder filter, int loadWidget);

    @Event(forwardToParent = true)
    void goToSupplierDemandsModule(SearchModuleDataHolder filter, int loadWidget);

    @Event(forwardToParent = true)
    void goToAdminModule(SearchModuleDataHolder filter, int loadWidget);

    @Event(forwardToParent = true)
    void goToMessagesModule(SearchModuleDataHolder filter, int loadWidget);

    /**************************************************************************/
    /* Additional widgets - init events                                       */
    /**************************************************************************/
    @Event(forwardToParent = true)
    void initCategoryWidget(SimplePanel holder, int checkboxes, int displayCountsOfWhat,
            List<CategoryDetail> categoriesToSet, boolean selectionRestriction);

    @Event(forwardToParent = true)
    void initLocalityWidget(SimplePanel embedWidget, int checkboxes, int displayCountsOfWhat,
            List<LocalityDetail> localitiesToSet, boolean selectionRestriction);
}
