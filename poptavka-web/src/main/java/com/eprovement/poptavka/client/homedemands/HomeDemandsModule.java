/*
 * HomeDemandsModule has the HomeDemandsEventBus associated that servers all
 * events for this mvp4g module. HomeDemandsModule represents an exclusive
 * fragment. An exclusive fragment contains code that is needed only once its
 * split point is activated.
 *
 * Specification:
 * Wireframe: http://www.webgres.cz/axure/ -> VR Vypis poptaviek
 */
package com.eprovement.poptavka.client.homedemands;

import com.mvp4g.client.Mvp4gModule;
import com.mvp4g.client.annotation.module.HistoryName;

/**
 *
 * @author praso
 */
@HistoryName("homeDemands")
public interface HomeDemandsModule extends Mvp4gModule {

}
