/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.client.common;

import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.shared.domain.ChangeDetail;
import com.eprovement.poptavka.shared.domain.IListDetailObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Martin Slavkovsky
 */
public class ChangeMonitor extends Composite implements HasWidgets, HasHandlers, HasChangeHandlers {

    /**************************************************************************/
    /* UiBinder                                                               */
    /**************************************************************************/
    interface ChangeMonitorUiBinder extends UiBinder<Widget, ChangeMonitor> {
    }
    private static ChangeMonitorUiBinder uiBinder = GWT.create(ChangeMonitorUiBinder.class);
    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    /** UiBinder attributes. **/
    @UiField
    SimplePanel holder;
    @UiField
    Anchor revert;
    /** Class attributes. **/
    private ChangeDetail changeDetail;
    private boolean firstSet = true;

    /**************************************************************************/
    /* Initialization                                                         */
    /**************************************************************************/
    public ChangeMonitor(ChangeDetail changeDetail) {
        initWidget(uiBinder.createAndBindUi(this));
        revert.setVisible(false);
        this.changeDetail = changeDetail;
    }

    /**************************************************************************/
    /* UiHandlers                                                             */
    /**************************************************************************/
    @UiHandler("revert")
    public void revertClickHandler(ClickEvent e) {
        revert();
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), getChangeMonitorWidget());
    }

    /**************************************************************************/
    /* HasWidgets                                                             */
    /**************************************************************************/
    public Widget getChangeMonitorWidget() {
        return this;
    }

    @Override
    public Widget getWidget() {
        return holder.getWidget();
    }

    @Override
    public void setWidget(Widget w) {
        addChangeHandler(w);
        holder.setWidget(w);
    }

    @Override
    public void add(Widget w) {
        addChangeHandler(w);
        holder.add(w);
    }

    @Override
    public void clear() {
        holder.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Widget w) {
        return holder.remove(w);
    }

    /**************************************************************************/
    /* HasHandlers, HasChangeHandler                                          */
    /**************************************************************************/
    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addDomHandler(handler, ChangeEvent.getType());
    }

    /**************************************************************************/
    /* Methods                                                                */
    /**************************************************************************/
    /** Setters. **/
    public void setBothValues(Object value) {
        changeDetail.setOriginalValue(value);
        changeDetail.setValue(value);
        setInputWidgetText(value);
    }

    public void setValue(Object value) {
        changeDetail.setValue(value);
        setInputWidgetText(value);
    }

    public void setChanged(ChangeDetail detail) {
        firstSet = true;
        changeDetail = detail;
        revert.setVisible(true);
        holder.getWidget().setStyleName(Storage.RSCS.common().changed());

        setInputWidgetText(detail.getValue());
    }

    /** Getters. **/
    public Object getValue() {
        return getInputWidgetText();
    }

    public ChangeDetail getChangeDetail() {
        return changeDetail;
    }

    public ChangeDetail getChangeDetailCopy() {
        ChangeDetail detail = new ChangeDetail(changeDetail.getField());
        detail.setOriginalValue(changeDetail.getOriginalValue());
        detail.setValue(changeDetail.getValue());
        return detail;
    }

    public boolean isModified() {
        return !changeDetail.getOriginalValue().equals(getInputWidgetText());
    }

    /** Change operations. **/
    public void commit() {
        revert.setVisible(false);
        changeDetail.revert();
        holder.getWidget().removeStyleName(Storage.RSCS.common().changed());
    }

    public void revert() {
        changeDetail.revert();
        reset();
        setInputWidgetText(changeDetail.getOriginalValue());
        //fire some event to update updatedFields attribute
        //zisti ci zavola changeevent a ci zrobi changesCount --
    }

    public void reset() {
        firstSet = true;
        revert.setVisible(false);
        holder.getWidget().removeStyleName(Storage.RSCS.common().changed());
    }

    /**************************************************************************/
    /* Helper methods                                                         */
    /**************************************************************************/
    private void addChangeHandler(Widget w) {
        if (w instanceof CellList || w instanceof DateBox) {
            w.addHandler(new ValueChangeHandler<IListDetailObject>() {
                @Override
                public void onValueChange(ValueChangeEvent<IListDetailObject> event) {
                    //workarount - how to deny firing event when setting list for first time?
                    if (!firstSet) {
                        revert.setVisible(true);
                        holder.getWidget().setStyleName(Storage.RSCS.common().changed());
                        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), getChangeMonitorWidget());
                    }
                    firstSet = false;
                }
            }, ValueChangeEvent.getType());
        } else {
            w.addDomHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    revert.setVisible(true);
                    holder.getWidget().setStyleName(Storage.RSCS.common().changed());
                }
            }, ChangeEvent.getType());
        }
    }

    private Object getInputWidgetText() {
        if (holder.getWidget() instanceof TextBox) {
            return ((TextBox) holder.getWidget()).getText();
        } else if (holder.getWidget() instanceof TextArea) {
            return ((TextArea) holder.getWidget()).getText();
        } else if (holder.getWidget() instanceof DateBox) {
            return ((DateBox) holder.getWidget()).getValue();
        } else if (holder.getWidget() instanceof ListBox) {
            return //((ListBox) holder.getWidget()).getItemText(
                    ((ListBox) holder.getWidget()).getSelectedIndex();
        } else if (holder.getWidget() instanceof CellList) {
            return changeDetail.getValue();
        } else {
            return null;
        }
    }

    private void setInputWidgetText(Object value) {
        if (holder.getWidget() instanceof TextBox) {
            ((TextBox) holder.getWidget()).setText(String.valueOf(value));
        } else if (holder.getWidget() instanceof TextArea) {
            ((TextArea) holder.getWidget()).setText(String.valueOf(value));
        } else if (holder.getWidget() instanceof DateBox) {
            ((DateBox) holder.getWidget()).setValue((Date) value);
        } else if (holder.getWidget() instanceof ListBox) {
            ((ListBox) holder.getWidget()).setSelectedIndex((Integer) value);
        } else if (holder.getWidget() instanceof CellList) {
            ((CellList) holder.getWidget()).setRowData((List) value);
        }
    }
}
