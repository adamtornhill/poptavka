/*
 * This RPC service serves all methods from MessagesModule
 */
package cz.poptavka.sample.server.service.messages;

import com.googlecode.genericdao.search.Search;
import cz.poptavka.sample.client.main.common.search.SearchModuleDataHolder;
import cz.poptavka.sample.client.main.common.search.dataHolders.FilterItem;
import cz.poptavka.sample.client.service.demand.MessagesRPCService;
import cz.poptavka.sample.domain.message.Message;
import cz.poptavka.sample.domain.message.MessageState;
import cz.poptavka.sample.domain.message.MessageUserRole;
import cz.poptavka.sample.domain.message.MessageUserRoleType;
import cz.poptavka.sample.domain.message.UserMessage;
import cz.poptavka.sample.domain.user.User;
import cz.poptavka.sample.exception.MessageException;
import cz.poptavka.sample.server.service.AutoinjectingRemoteService;
import cz.poptavka.sample.service.GeneralService;
import cz.poptavka.sample.service.message.MessageService;
import cz.poptavka.sample.service.usermessage.UserMessageService;
import cz.poptavka.sample.shared.domain.message.MessageDetail;
import cz.poptavka.sample.shared.domain.message.UserMessageDetail;
import cz.poptavka.sample.shared.exceptions.CommonException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Praso
 */
public class MessagesRPCServiceImpl extends AutoinjectingRemoteService implements MessagesRPCService {

    public static final String INTERNAL_MESSAGE = "Interna sprava";
    /**
     * Generated serialVersionUID.
     */
    private static final long serialVersionUID = -2239531608577928736L;
    private GeneralService generalService;
    private MessageService messageService;
    private UserMessageService userMessageService;

    @Autowired
    public void setUserMessageService(UserMessageService userMessageService) {
        this.userMessageService = userMessageService;
    }

    @Autowired
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Message sent by user to another user without any link to demand or offer.
     * @param messageDetailImpl
     * @return message
     */
    @Override
    public MessageDetail sendInternalMessage(MessageDetail messageDetailImpl) throws CommonException {
        try {
            Message m = messageService.newReply(this.messageService.getById(
                    messageDetailImpl.getThreadRootId()),
                    this.generalService.find(User.class, messageDetailImpl.getSenderId()));
            m.setBody(messageDetailImpl.getBody());
            m.setSubject(INTERNAL_MESSAGE);
            // TODO set the id correctly, check it
            MessageDetail messageDetailFromDB = MessageDetail.createMessageDetail(this.messageService.create(m));
            return messageDetailFromDB;
        } catch (MessageException ex) {
            Logger.getLogger(MessagesRPCServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void deleteMessages(List<Long> messagesIds) throws CommonException {
        Search searchMsgs = new Search(Message.class);
        searchMsgs.addFilterIn("id", messagesIds);
        List<Message> msgs = generalService.search(searchMsgs);
        for (Message msg : msgs) {
            Message subRoot = messageService.getById(msg.getId());
            List<Message> conversation = messageService.getAllDescendants(subRoot);
            msg.setMessageState(MessageState.DELETED); //musi byt? neobsahuje to ten getALlDescendatns
            for (Message msg1 : conversation) {
                msg1.setMessageState(MessageState.DELETED);
            }
            generalService.merge(msg);
        }
    }

    @Override
    public List<UserMessageDetail> getInboxMessages(Long recipientId, SearchModuleDataHolder searchDataHolder)
        throws CommonException {
        return this.getMessages(recipientId, searchDataHolder, Arrays.asList(
                MessageUserRoleType.TO, MessageUserRoleType.CC, MessageUserRoleType.BCC));
    }

    @Override
    public List<UserMessageDetail> getSentMessages(Long senderId, SearchModuleDataHolder searchDataHolder)
        throws CommonException {
        User sender = generalService.find(User.class, senderId);

        /****/// ziskaj vsetky spravy poslane danym uzivatelom
        Search messageSearch = new Search(Message.class);
        messageSearch.addFilterEqual("sender", sender);
        //ak treba, filtruj spravy poslane danym uzivatelom
        if (searchDataHolder != null) {
            for (FilterItem item : searchDataHolder.getFilters()) {
                this.filter(messageSearch, "", item);
            }
        }

        /****/
        Map<Long, Message> senderMessages = new HashMap<Long, Message>();
        /****/
        List<Message> senderMessagesTmp = new ArrayList<Message>();
        /****/
        senderMessagesTmp.addAll(generalService.search(messageSearch));
        for (Message msg : senderMessagesTmp) {
            if (!senderMessages.containsKey(msg.getThreadRoot().getId())) {
                senderMessages.put(msg.getThreadRoot().getId(), msg);
            }
        }

        /****///Ziskaj vsetkych prijemcov danych sprav
        List<MessageUserRole> recipients = new ArrayList<MessageUserRole>();
        Search messageUserRoleSearch = new Search(MessageUserRole.class);
        messageUserRoleSearch.addFilterIn("message", generalService.search(messageSearch));
        messageUserRoleSearch.addFilterIn("type", MessageUserRoleType.TO);
        //ak treba, filtruj prijemcov danych sprav
        if (searchDataHolder != null) {
            for (FilterItem item : searchDataHolder.getFilters()) {
                if (item.getItem().equals("email")) {
                    messageUserRoleSearch.addFilterIn("user", generalService.search(
                            this.filter(new Search(User.class), "", item)));
                } else {
                    this.filter(messageSearch, "", item);
                }
            }
        }
        /****/
        recipients.addAll(generalService.search(messageUserRoleSearch));

        todoDeleteOrRefactor();


        //Stacilo by mi aj to zhora, ale musim ziskat este UserMessage, aby som vedel, isRead, isStarred, ...

        /**///Ziskaj UserMessage (read/unread , starred/unstarred)
        List<UserMessage> inboxMessages = new ArrayList<UserMessage>();
        Search userMessagesSearch = new Search(UserMessage.class);
//        for (Message msg : rootMessages) {

        userMessagesSearch.addFilterEqual("user", sender);
        userMessagesSearch.addFilterIn("message", senderMessages.values());
        /**/ inboxMessages.addAll(generalService.search(userMessagesSearch));
//        }

        //Create details
        List<UserMessageDetail> inboxMessagesDetail = new ArrayList<UserMessageDetail>();
//        for (MessageUserRole)
        for (UserMessage userMessage : inboxMessages) {
//            rootMessages.contains(userMessage.getMessage());
//            senderMessages.containsValue(userMessage.getMessage());
//            userMessage.getMessage().equals(this);
            for (MessageUserRole mur : recipients) {
                if (mur.getMessage().equals(userMessage.getMessage())) {
                    try {
                        userMessage.getMessage().setSender(mur.getMessage().getSender());
                    } catch (MessageException ex) {
                        Logger.getLogger(MessagesRPCServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            inboxMessagesDetail.add(UserMessageDetail.createUserMessageDetail(userMessage));
        }

        return inboxMessagesDetail;
//        return this.getMessages(senderId, searchDataHolder, Arrays.asList(MessageUserRoleType.SENDER));
    }

    /**
     * Get all conversations of thread.
     * @param threadRootId
     * @param subRootId
     * @return conversation list
     */
    @Override
    public ArrayList<MessageDetail> getConversationMessages(long threadRootId,
            long subRootId) throws CommonException {
//        Message root = messageService.getById(threadRootId);
        Message subRoot = messageService.getById(subRootId);
        List<Message> conversation = messageService.getAllDescendants(subRoot);

        ArrayList<MessageDetail> result = new ArrayList<MessageDetail>();
        // add root and subRoot message
//        result.add(MessageDetail.createMessageDetail(root));
        result.add(MessageDetail.createMessageDetail(subRoot));
        for (Message m : conversation) {
            result.add(MessageDetail.createMessageDetail(m));
        }
        return result;
    }

    /**
     * COMMON.
     * Change 'read' status of sent messages to chosen value
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setMessageReadStatus(List<Long> userMessageIds,
            boolean isRead) throws CommonException {
        for (Long userMessageId : userMessageIds) {
            UserMessage userMessage = this.generalService.find(UserMessage.class, userMessageId);
            userMessage.setRead(isRead);
            this.userMessageService.update(userMessage);
        }
    }

    /**
     * COMMON.
     * Change 'star' status of sent messages to chosen value
     */
    @Override
    public void setMessageStarStatus(List<Long> userMessageIds,
            boolean isStarred) throws CommonException {
        for (Long userMessageId : userMessageIds) {
            UserMessage userMessage = this.generalService.find(UserMessage.class, userMessageId);
            userMessage.setStarred(isStarred);
            this.userMessageService.update(userMessage);
        }
    }

    @Override
    public List<UserMessageDetail> getDeletedMessages(Long userId, SearchModuleDataHolder searchDataHolder)
        throws CommonException {
        Search messageSearch = new Search(Message.class);
        messageSearch.addFilterEqual("messageState", MessageState.DELETED);
        if (searchDataHolder != null) {
            for (FilterItem item : searchDataHolder.getFilters()) {
                if (item.getItem().equals("email")) {
                    messageSearch.addFilterIn("sender", generalService.search(
                            this.filter(new Search(User.class), "", item)));
                } else {
                    this.filter(messageSearch, "", item);
                }
            }
        }

        Search userMessagesSearch = new Search(UserMessage.class);
        userMessagesSearch.addFilterEqual("user", generalService.find(User.class, userId));
        if (searchDataHolder != null) {
            userMessagesSearch.addFilterIn("message", generalService.search(messageSearch));
        }
        List<UserMessage> userMessages = generalService.search(userMessagesSearch);
        Map<Long, UserMessage> rootDeletedMessages = new TreeMap<Long, UserMessage>();

        for (UserMessage userMessage : userMessages) {
            if (userMessage.getMessage().getParent() == null) {
                if (!rootDeletedMessages.containsKey(userMessage.getMessage().getThreadRoot().getId())) {
                    rootDeletedMessages.put(userMessage.getMessage().getThreadRoot().getId(), userMessage);
                }
            }
        }
        List<UserMessageDetail> deletedMessagesDetail = new ArrayList<UserMessageDetail>();

        for (UserMessage userMessage : rootDeletedMessages.values()) {
            deletedMessagesDetail.add(UserMessageDetail.createUserMessageDetail(userMessage));
        }

        return deletedMessagesDetail;
    }

    // TODO - check this method
    private void todoDeleteOrRefactor() {
        //        Search recipientMessagesSearch = new Search(MessageUserRole.class);
//        recipientMessagesSearch.addFilterEqual("user", sender);
//        recipientMessagesSearch.addFilterIn("type", roles);
//        if (searchDataHolder != null) {
//            recipientMessagesSearch.addFilterIn("message", generalService.search(messageSearch));
//        }
        //ziskaj prvotne spravy na vypis v tabulke
//        List<Message> rootMessages = new ArrayList<Message>();
//        List<Long> threadIds = new ArrayList<Long>();
//        if (recipients.isEmpty()) {
//            for (Message msg : senderMessages) {
//                if (!threadIds.contains(msg.getThreadRoot().getId())) {
//                    threadIds.add(msg.getThreadRoot().getId());
//                    Search rootMsgSearch = new Search(Message.class);
//                    rootMsgSearch.addFilterEqual("id", msg.getThreadRoot().getId());
//                    rootMsgSearch.addFilterNull("parent");
//                    rootMessages.addAll(generalService.search(messageSearch));
//                }
//            }
//        } else {
//            for (MessageUserRole mur : recipients) {
//                if (!threadIds.contains(mur.getMessage().getThreadRoot().getId())) {
//                    threadIds.add(mur.getMessage().getThreadRoot().getId());
//                    Search rootMsgSearch = new Search(Message.class);
//                    rootMsgSearch.addFilterEqual("id", mur.getMessage().getThreadRoot().getId());
//                    rootMsgSearch.addFilterNull("parent");
//                    rootMessages.addAll(generalService.search(messageSearch));
//                }
//            }
//        }
//        Search firstBornRecipientMessagesSearch = new Search(Message.class);
//        List<Message> firstBornRecipientMessages = new ArrayList<Message>();
//        for (MessageUserRole mur : recipientMessages) {
//            firstBornRecipientMessagesSearch.addFilterEqual("id", mur.getMessage().getId());
//            firstBornRecipientMessages = generalService.search(firstBornRecipientMessagesSearch);
//        }
//        Map<Long, Message> rootRecipientMessages = new TreeMap<Long, Message>();
//        for (MessageUserRole mur : senderMessages) {
//            if (mur.getMessage().getParent() == null) {
//                // nemusi kontorlovat, ved thread_id s parent_id = null je vzdy len jeden
////                if (!rootRecipientMessages.containsKey(mur.getMessage().getThreadRoot().getId())) {
//                rootRecipientMessages.put(mur.getMessage().getThreadRoot().getId(), mur.getMessage());
//            }
//        }
    }

    private List<UserMessageDetail> getMessages(Long recipientId, SearchModuleDataHolder searchDataHolder,
            List<MessageUserRoleType> roles) {
        User recipient = generalService.find(User.class, recipientId);

        Search messageSearch = null;
        if (searchDataHolder != null) {
            messageSearch = new Search(Message.class);
            for (FilterItem item : searchDataHolder.getFilters()) {
                if (item.getItem().equals("email")) {
                    messageSearch.addFilterIn("sender", generalService.search(
                            this.filter(new Search(User.class), "", item)));
                } else {
                    this.filter(messageSearch, "", item);
                }
            }

            //Ziskaj vsetky spravy daneho uzivatela, kt bol oznaceny ako adresat alebo odosielatel
            List<MessageUserRole> recipientMessages = new ArrayList<MessageUserRole>();
            Search recipientMessagesSearch = new Search(MessageUserRole.class);
            recipientMessagesSearch.addFilterEqual("user", recipient);
            recipientMessagesSearch.addFilterIn("type", roles);
            if (searchDataHolder != null) {
                recipientMessagesSearch.addFilterIn("message", generalService.search(messageSearch));
            }
            recipientMessages.addAll(generalService.search(recipientMessagesSearch));


//        Search firstBornRecipientMessagesSearch = new Search(Message.class);
//        List<Message> firstBornRecipientMessages = new ArrayList<Message>();
//        for (MessageUserRole mur : recipientMessages) {
//            firstBornRecipientMessagesSearch.addFilterEqual("id", mur.getMessage().getId());
//            firstBornRecipientMessages = generalService.search(firstBornRecipientMessagesSearch);
//        }

            Map<Long, Message> rootRecipientMessages = new TreeMap<Long, Message>();
            for (MessageUserRole mur : recipientMessages) {
                if (mur.getMessage().getParent() == null) {
                    // nemusi kontorlovat, ved thread_id s parent_id = null je vzdy len jeden
//                if (!rootRecipientMessages.containsKey(mur.getMessage().getThreadRoot().getId())) {
                    rootRecipientMessages.put(mur.getMessage().getThreadRoot().getId(), mur.getMessage());
                }
            }
            //Stacilo by mi aj to zhora, ale musim ziskat este UserMessage, aby som vedel, isRead, isStarred, ...
            List<UserMessage> inboxMessages = new ArrayList<UserMessage>();
            for (Message msg : rootRecipientMessages.values()) {
                Search userMessagesSearch = new Search(UserMessage.class);
                userMessagesSearch.addFilterEqual("user", recipient);
                userMessagesSearch.addFilterEqual("message", msg);
                inboxMessages.addAll(generalService.search(userMessagesSearch));
            }
            List<UserMessageDetail> inboxMessagesDetail = new ArrayList<UserMessageDetail>();
            for (UserMessage userMessage : inboxMessages) {
                inboxMessagesDetail.add(UserMessageDetail.createUserMessageDetail(userMessage));
            }
        }
        return null;
    }

    private Search filter(Search search, String prefix, FilterItem item) {
        prefix += ".";
        switch (item.getOperation()) {
            case FilterItem.OPERATION_EQUALS:
                search.addFilterEqual(prefix + item.getItem(), item.getValue());
                break;
            case FilterItem.OPERATION_LIKE:
                search.addFilterLike(prefix + item.getItem(), "%" + item.getValue().toString() + "%");
                break;
            case FilterItem.OPERATION_IN:
                search.addFilterIn(prefix + item.getItem(), item.getValue());
                break;
            case FilterItem.OPERATION_FROM:
                search.addFilterGreaterOrEqual(prefix + item.getItem(), item.getValue());
                break;
            case FilterItem.OPERATION_TO:
                search.addFilterLessOrEqual(prefix + item.getItem(), item.getValue());
                break;
            default:
                break;
        }
        return search;
    }
}
