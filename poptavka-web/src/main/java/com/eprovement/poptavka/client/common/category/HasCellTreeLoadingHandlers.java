/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.client.common.category;

import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;

/**
 *
 * @author jackie177
 */
public interface HasCellTreeLoadingHandlers {

    LoadingStateChangeEvent.Handler getCategoryLoadingHandler();
}
