/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.client.user.supplierdemands;

import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid.IEventBusData;
import com.eprovement.poptavka.domain.enums.OrderType;
import com.eprovement.poptavka.shared.search.SearchModuleDataHolder;
import com.mvp4g.client.annotation.Debug;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.annotation.Forward;
import com.mvp4g.client.annotation.Start;
import com.mvp4g.client.event.EventBus;
import java.util.Map;

@Debug(logLevel = Debug.LogLevel.DETAILED)
@Events(startPresenter = SupplierDemandsPresenter.class, module = SupplierDemandsModule.class)
public interface SupplierDemandsEventBus extends EventBus, IEventBusData {

    /**
     * First event to be handled.
     */
    @Start
    @Event(handlers = SupplierDemandsPresenter.class)
    void start();

    /**
     * Forward event is called only if it is configured here. If there is nothing to carry out
     * in this method we should remove forward event to save the number of method invocations.
     */
    @Forward
    @Event(handlers = SupplierDemandsPresenter.class)
    void forward();

    /**************************************************************************/
    /* Navigation events.                                                     */
    /**************************************************************************/
    /**
     * The only entry point to this module. This module is not asynchronous.
     *
     * @param filter - defines data holder to be displayed in advanced search bar
     */
    @Event(handlers = SupplierDemandsPresenter.class, historyConverter = SupplierDemandsHistoryConverter.class)
    String goToSupplierDemandsModule(SearchModuleDataHolder filterm, int loadWidget);

    /**************************************************************************/
    /* Parent events */
    /**************************************************************************/
    /**************************************************************************/
    /* Business events handled by Presenters. */
    /**************************************************************************/
    /**************************************************************************/
    /* Business events handled by Handlers. */
    /**************************************************************************/

    /**************************************************************************/
    /* Overriden methods of IEventBusData interface. */
    /* Should be called only from UniversalAsyncGrid. */
    /**************************************************************************/
    @Override
    @Event(handlers = SupplierDemandsHandler.class)
    void getDataCount(UniversalAsyncGrid grid, SearchModuleDataHolder detail);

    @Override
    @Event(handlers = SupplierDemandsHandler.class)
    void getData(int start, int count, SearchModuleDataHolder detail, Map<String, OrderType> orderColumns);
}