package cz.poptavka.sample.client.user.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.view.BaseCycleView;

import cz.poptavka.sample.client.resources.StyleResource;
import cz.poptavka.sample.client.user.messages.SimpleMessageWindow.MessageDisplayType;
import cz.poptavka.sample.shared.domain.message.MessageDetail;
import cz.poptavka.sample.shared.domain.offer.FullOfferDetail;

// TODO extend SimpleMessageWindow, if possible.
public class OfferWindow extends BaseCycleView implements OfferWindowPresenter.ActionMessageInterface {

    private static final int DATE_POS = 3;

    private static UserActionMessageViewUiBinder uiBinder = GWT
            .create(UserActionMessageViewUiBinder.class);

    interface UserActionMessageViewUiBinder extends
            UiBinder<Widget, OfferWindow> {
    }

    private static final StyleResource CSS = GWT.create(StyleResource.class);

    @UiField Element header;
    @UiField Element body;
    @UiField Element headerTable;
    @UiField Element messagePreview;

    @UiField Anchor acceptButton;
    @UiField Anchor replyButton;
    @UiField Anchor deleteButton;

    @UiField SimplePanel responseHolder;

    private boolean collapsed = false;

    private FullOfferDetail offerDetail;

    @Override
    public void createView() {
        initWidget(uiBinder.createAndBindUi(this));
    }


    public void setContent(MessageDetail message, boolean collapsed) {
        setMessage(message);

        // set collapsed state
        this.collapsed = collapsed;
        toggleCollapsed();
        setMessageStyle(MessageDisplayType.BOTH);
    }

    /**
     * Constructs object of user message with custom parameter. Used for FIRST or LAST message
     * in the conversation. If there is only one message, it's style should be BOTH
     *
     * @param message message to fill the view
     * @param collapsed define if this newly created message should be collapsed
     * @param style if true, message is set as first, if false message is set as last!
     */

    public void setMessage(MessageDetail message) {
        NodeList<Element> tableColumns = headerTable.getElementsByTagName("td");
        // author
        tableColumns.getItem(0).setInnerText(message.getSubject());

        // message
        messagePreview.setInnerText(message.getBody());

        // date
        tableColumns.getItem(DATE_POS).setInnerText(message.getSent().toString());

        // message body
        // the first child is our content place
        body.getElementsByTagName("div").getItem(0).setInnerHTML(message.getBody());
    }


    public void setMessageStyle(MessageDisplayType type) {
        switch (type) {
            case FIRST:
                header.getParentElement().addClassName(CSS.message().messageFirst());
                break;
            case BOTH:
                header.getParentElement().addClassName(CSS.message().messageFirst());
            case LAST:
                header.getParentElement().addClassName(CSS.message().messageLast());
                break;
            case NORMAL:
                header.getParentElement().removeClassName(CSS.message().messageLast());
                break;
            default:
                break;
        }
    }

    public void toggleCollapsed() {
        if (collapsed) {
            body.getStyle().setDisplay(Display.NONE);
        } else {
            body.getStyle().setDisplay(Display.BLOCK);
        }
        collapsed = !collapsed;
    }

    public FullOfferDetail getFullOfferDetail() {
        return offerDetail;
    }

    public void setFullOfferDetail(FullOfferDetail fullOfferDetail) {
        this.offerDetail = offerDetail;
        setMessage(offerDetail.getMessageDetail());
        setMessageStyle(MessageDisplayType.BOTH);
    }

    /**********************************************************************************/
    /**                       Widget internal behavior handling.                       */
    @Override
    public void onLoad() {
        com.google.gwt.user.client.Element castedElement = castElement(header);
        DOM.sinkEvents(castedElement, Event.ONCLICK);
        DOM.setEventListener(castedElement, new MessageToggleHangler());
    }

    @Override
    public void onUnload() {
        super.onUnload();
        DOM.setEventListener(castElement(header), null);
    }

    private com.google.gwt.user.client.Element castElement(Element elem) {
        return (com.google.gwt.user.client.Element) elem;
    }

    private class MessageToggleHangler implements EventListener {
        @Override
        public void onBrowserEvent(Event event) {
            event.preventDefault();
            event.stopPropagation();
            if (event.getTypeInt() == Event.ONCLICK) {
                toggleCollapsed();
            }
        }
    }

    @Override
    public Widget getWidgetView() {
        return this;
    }


    @Override
    public Anchor getAcceptButton() {
        return acceptButton;
    }


    @Override
    public Anchor getReplyButton() {
        return replyButton;
    }


    @Override
    public Anchor getDeclineButton() {
        return deleteButton;
    }


    @Override
    public SimplePanel getResponseHolder() {
        return responseHolder;
    }

}
