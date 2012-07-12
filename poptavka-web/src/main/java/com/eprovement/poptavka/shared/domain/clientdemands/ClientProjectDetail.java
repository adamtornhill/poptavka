package com.eprovement.poptavka.shared.domain.clientdemands;

import com.eprovement.poptavka.domain.enums.DemandStatus;
import com.eprovement.poptavka.domain.enums.OfferStateType;
import com.eprovement.poptavka.shared.domain.message.TableDisplay;
import com.google.gwt.view.client.ProvidesKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents full detail of demand. Serves for creating new demand or for call of detail, that supports editing.
 *
 * @author Beho
 */
public class ClientProjectDetail implements Serializable, TableDisplay {

    /**
     * Generated serialVersionUID.
     */
    private static final long serialVersionUID = -530982467233195456L;
    private long demandId;
    private long messageId;
    private long userMessageId;
    private DemandStatus demandStatus;
    private String demandTitle; //title
    private BigDecimal price = null;
    private Date endDate;
    private Date validToDate;
    private boolean read = false;
    private boolean starred = false;
    private int messageCount = -1;
    private int unreadSubmessages = -1;

    public static final ProvidesKey<ClientProjectDetail> KEY_PROVIDER =
            new ProvidesKey<ClientProjectDetail>() {

                @Override
                public Object getKey(ClientProjectDetail item) {
                    return item == null ? null : item.getDemandId();
                }
            };


    //---------------------------- GETTERS AND SETTERS --------------------
    public long getDemandId() {
        return demandId;
    }

    public void setDemandId(long demandId) {
        this.demandId = demandId;
    }

    public String getDemandTitle() {
        return demandTitle;
    }

    public void setDemandTitle(String demandTitle) {
        this.demandTitle = demandTitle;
    }

    public long getUserMessageId() {
        return userMessageId;
    }

    public void setUserMessageId(long userMessageId) {
        this.userMessageId = userMessageId;
    }

    public DemandStatus getDemandStatus() {
        return demandStatus;
    }

    public void setDemandStatus(DemandStatus demandStatus) {
        this.demandStatus = demandStatus;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(Date validToDate) {
        this.validToDate = validToDate;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public long getMessageId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    @Override
//    public String getTitle() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
    public Date getCreated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPrice() {
        return price == null ? "N/A" : price.toString();
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getUnreadSubmessages() {
        return unreadSubmessages;
    }

    public void setUnreadSubmessages(int unreadSubmessages) {
        this.unreadSubmessages = unreadSubmessages;
    }

    public String getFormattedMessageCount() {
        return "(" + getMessageCount() + "/" + getUnreadSubmessages() + ")";
    }

    public String getSender() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getRating() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Date getExpireDate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Date getReceivedDate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Date getAcceptedDate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTitle() {
        return demandTitle;
    }

    public void setTitle(String title) {
        demandTitle = title;
    }

    public OfferStateType getOfferState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Display string as HTML. We suppose calling of this method always come from trusted (programmed) source.
     * User CANNOT call this nethod due to security issues.
     * @param trustedHtml
     * @return string in html tags
     */
    public static String displayHtml(String trustedHtml, boolean isRead) {
        if (isRead) {
            return trustedHtml;
        } else {
            return "<strong>" + trustedHtml + "</strong>";
        }
    }
}
