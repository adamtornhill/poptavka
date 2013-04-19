/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.shared.domain.offer;

import com.eprovement.poptavka.client.user.widget.grid.IUniversalDetail;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;
import java.math.BigDecimal;

import java.util.Date;

/**
 *
 * @author ivlcek
 */
public class ClientOfferedDemandOffersDetail implements IsSerializable, IUniversalDetail {

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private long demandId;
    private long offerId;
    private long threadRootId;
    private long supplierId;
    private long senderId;
    private long userMessageId;
    private boolean isStarred;
    private boolean isRead;
    private String supplierName;
    private BigDecimal price;
    private String demandTitle;
    private Integer rating;
    private Date receivedDate;
    private Date deliveryDate;
    private int messageCount;

    //Keyprovider
    public static final ProvidesKey<IUniversalDetail> KEY_PROVIDER =
            new ProvidesKey<IUniversalDetail>() {
                @Override
                public Object getKey(IUniversalDetail item) {
                    return item == null ? null : item.getOfferId();
                }
            };

    /**************************************************************************/
    /* Constructors                                                           */
    /**************************************************************************/
    public ClientOfferedDemandOffersDetail() {
        //for serialization
    }

    /**************************************************************************/
    /* Getters & Setters                                                      */
    /**************************************************************************/
    /**
     * @return the demandId
     */
    @Override
    public long getDemandId() {
        return demandId;
    }

    /**
     * @param demandId the demandId to set
     */
    public void setDemandId(long demandId) {
        this.demandId = demandId;
    }

    /**
     * @return the offerId
     */
    @Override
    public long getOfferId() {
        return offerId;
    }

    /**
     * @param offerId the offerId to set
     */
    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    /**
     * @return the threadRootId
     */
    @Override
    public long getThreadRootId() {
        return threadRootId;
    }

    /**
     * @param threadRootId the threadRootId to set
     */
    public void setThreadRootId(long threadRootId) {
        this.threadRootId = threadRootId;
    }

    /**
     * @return the supplierId
     */
    @Override
    public long getSupplierId() {
        return supplierId;
    }

    /**
     * @param supplierId the supplierId to set
     */
    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * @return the supplierName
     */
    @Override
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * @param supplierName the supplierName to set
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * @return the price
     */
    @Override
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return the rating
     */
    @Override
    public int getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * @return the receivedDate
     */
    @Override
    public Date getReceivedDate() {
        return receivedDate;
    }

    /**
     * @param receivedDate the receivedDate to set
     */
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    /**
     * @return the deliveryDate
     */
    @Override
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * @param deliveryDate the deliveryDate to set
     */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * @return the messageCount
     */
    @Override
    public int getMessageCount() {
        return messageCount;
    }

    /**
     * @param messageCount the messageCount to set
     */
    @Override
    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    @Override
    public boolean isStarred() {
        return isStarred;
    }

    @Override
    public void setIsStarred(boolean value) {
        this.isStarred = value;
    }

    @Override
    public Date getEndDate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getClientId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getClientName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getMessageId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getSenderId() {
        return this.senderId;
    }

    @Override
    public Date getMessageSent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getUserMessageId() {
        return userMessageId;
    }

    @Override
    public void setUserMessageId(long userMessageId) {
        this.userMessageId = userMessageId;
    }

    @Override
    public Date getValidTo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public void setTitle(String title) {
        demandTitle = title;
    }

    @Override
    public String getTitle() {
        return demandTitle;
    }

    @Override
    public String toString() {
        return "ClientOfferedDemandOffersDetail{" + "demandId=" + demandId + ", offerId=" + offerId
                + ", threadRootId=" + threadRootId + ", supplierId=" + supplierId + ", senderId="
                + senderId + ", isStarred=" + isStarred + ", supplierName="
                + supplierName + ", price=" + price + ", rating=" + rating + ", receivedDate=" + receivedDate
                + ", deliveryDate=" + deliveryDate + ", messageCount=" + messageCount + ", isRead="
                + isRead + '}';
    }

    @Override
    public boolean isRead() {
        return isRead;
    }

    @Override
    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * @param senderId the senderId to set
     */
    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }
}