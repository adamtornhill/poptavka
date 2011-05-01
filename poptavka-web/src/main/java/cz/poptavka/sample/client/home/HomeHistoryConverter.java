package cz.poptavka.sample.client.home;

import java.util.logging.Logger;

import com.mvp4g.client.annotation.History;
import com.mvp4g.client.annotation.History.HistoryConverterType;
import com.mvp4g.client.history.HistoryConverter;

@History(type = HistoryConverterType.NONE, name = "home")
public class HomeHistoryConverter implements HistoryConverter<HomeEventBus> {

    private static final Logger LOGGER = Logger.getLogger(HomeHistoryConverter.class.getName());

    @Override
    public void convertFromToken(String historyName, String param, HomeEventBus eventBus) {
        eventBus.setHistoryStoredForNextOne(false);
        eventBus.displayMenu();

        if (historyName.equals("atHome")) {
            eventBus.atHome();
        }
        if (historyName.equals("atCreateDemand")) {
            eventBus.atCreateDemand(true);
        }
        if (historyName.equals("atDemands")) {
            eventBus.atDemands();
        }
    }

    @Override
    public boolean isCrawlable() {
        // TODO Auto-generated method stub
        return true;
    }

}
