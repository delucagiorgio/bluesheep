package com.betfair.foe;

import com.betfair.foe.api.FoeOperations;
import com.betfair.foe.api.FoeRescriptOperations;
import com.betfair.foe.entities.*;
import com.betfair.foe.enums.MarketProjection;
import com.betfair.foe.enums.MarketSort;
import com.betfair.foe.enums.ResponseFilter;
import com.betfair.foe.exceptions.FOEException;
import com.google.common.collect.Sets;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class FoeRescriptDemo {

    private String applicationKey;
    private String sessionToken;

    private Map<BulkListMarketIndex, ListMarketPricesResult> indexToPricesMap;

    private static FoeOperations foeOperations = FoeRescriptOperations.getInstance();
    private final long FOE_BULK_TIMEOUT;

    public FoeRescriptDemo(String applicationKey, String sessionToken) {
        this.applicationKey = applicationKey;
        this.sessionToken = sessionToken;
        FOE_BULK_TIMEOUT = Long.valueOf(FoeDemo.getProperties().getProperty("FOE_BULK_TIMEOUT"));
    }

    public void specificCallDemo() {
        try {
            // retrieve all event types & find football
            List<EventTypeResult> eventTypeResults = getEventTypes();
            String footballEventId = null;
            for (EventTypeResult result : eventTypeResults) {
                if (result.getEventType().getName().equalsIgnoreCase("football")) {
                    footballEventId = result.getEventType().getId();
                }
            }

            // retrieve all football competition IDs & find the championship
            List<CompetitionResult> competitionResults = getCompetitionsForEventType(footballEventId);
            String championshipCompId = null;
            for (CompetitionResult result : competitionResults) {
                if (result.getCompetition().getName().equalsIgnoreCase("the championship")) {
                    championshipCompId = result.getCompetition().getId();
                }
            }

            // find all events for the competition
            List<EventResult> eventResults = getEventsForCompetition(championshipCompId);
            if (CollectionUtils.isEmpty(eventResults)) {
                System.out.println("Error retrieving event IDs");
                return;
            }

            // retrieve the market catalogue for the first event
            List<MarketCatalogue> marketCatalogues = getMarketCatalogueForEvent(eventResults.get(0).getEvent().getId());

            // get the prices for all of the markets in the catalogue
            List<String> marketIds = marketCatalogues.stream().map(MarketCatalogue::getMarketId).collect(Collectors.toList());
            ListMarketPricesResult marketPricesResult = getMarketPrices(marketIds);
            List<MarketDetails> marketDetailsList = marketPricesResult.getMarketDetails();

            // Output all the market details
            System.out.println("\nEvent Name: " + eventResults.get(0).getEvent().getName());
            System.out.println("Event ID: " + marketDetailsList.get(0).getEventId() + "\n");

            for (MarketDetails marketDetails : marketDetailsList) {
                System.out.println("Market ID: " + marketDetails.getMarketId());
                System.out.println("Market name: " + marketDetails.getMarketName());
                System.out.println("Market type: " + marketDetails.getMarketType());
                System.out.println("\nRunners: ");

                List<RunnerDetails> runnerDetailsList = marketDetails.getRunnerDetails();
                for (int rd = 0; rd < runnerDetailsList.size(); rd++) {
                    RunnerDetails runnerDetails = runnerDetailsList.get(rd);
                    String runnerStr = "r" + String.valueOf(rd) + ") ";
                    runnerStr += "SelectionId=" + runnerDetails.getSelectionId();
                    runnerStr += ", SelectionName=" + runnerDetails.getSelectionName();
                    runnerStr += ", Handicap=" + runnerDetails.getHandicap();
                    runnerStr += ", RunnerStatus=" + runnerDetails.getRunnerStatus();
                    String winOddsStr = (runnerDetails.getWinRunnerOdds() != null) ?
                            String.valueOf(runnerDetails.getWinRunnerOdds().getDecimal()) : "null";
                    runnerStr += ", Odds=" + winOddsStr;
                    System.out.println(runnerStr);
                }
                System.out.println();
            }

        } catch (FOEException e) {
            e.printStackTrace();
        }
    }

    private List<EventTypeResult> getEventTypes() throws FOEException {
        BasicRequestParams params = new BasicRequestParams();
        MarketFilter marketFilter = new MarketFilter();
        params.setMarketFilter(marketFilter);
        params.setLocale(Locale.getDefault().toString());

        return foeOperations.listEventTypes(params, applicationKey, sessionToken);
    }

    private List<CompetitionResult> getCompetitionsForEventType(String eventTypeId) throws FOEException {
        BasicRequestParams params = new BasicRequestParams();
        MarketFilter marketFilter = new MarketFilter();
        marketFilter.setEventTypeIds(Collections.singleton(eventTypeId));
        params.setMarketFilter(marketFilter);
        params.setLocale(Locale.getDefault().toString());

        return foeOperations.listCompetitions(params, applicationKey, sessionToken);
    }

    private List<EventResult> getEventsForCompetition(String competitionId) throws FOEException {
        BasicRequestParams params = new BasicRequestParams();
        MarketFilter marketFilter = new MarketFilter();
        marketFilter.setCompetitionIds(Collections.singleton(competitionId));
        params.setMarketFilter(marketFilter);
        params.setLocale(Locale.getDefault().toString());

        return foeOperations.listEvents(params, applicationKey, sessionToken);
    }

    private List<MarketCatalogue> getMarketCatalogueForEvent(String eventId) throws FOEException {
        ListMarketCatalogueRequestParams params = new ListMarketCatalogueRequestParams();
        MarketFilter filter = new MarketFilter();
        filter.setEventIds(Collections.singleton(eventId));
        params.setMarketFilter(filter);
        params.setLocale(Locale.getDefault().toString());
        params.setMarketProjection(Sets.newHashSet(MarketProjection.MARKET_DESCRIPTION, MarketProjection.RUNNER_DESCRIPTION));
        params.setMaxResults(100);
        params.setSort(MarketSort.FIRST_TO_START);

        return foeOperations.listMarketCatalogue(params, applicationKey, sessionToken);
    }

    private ListMarketPricesResult getMarketPrices(List<String> marketIds) throws FOEException {
        ListMarketPricesRequestParams params = new ListMarketPricesRequestParams();
        params.setMarketIds(marketIds);
        params.setResponseFilter(Sets.newHashSet(ResponseFilter.MARKET_DEFINITION, ResponseFilter.PRICE_DEFINITION));

        return foeOperations.listMarketPrices(params, applicationKey, sessionToken);
    }

    public void bulkLoadingDemo() {

        initialiseBulkMap();
        System.out.println("Bulk market cache initialised!");

        while (true) {
            try {
                System.out.println("Waiting for " + String.valueOf(FOE_BULK_TIMEOUT / 1000) + "s...");
                // each call to bulkListMarketPrices creates a 5 minute timeout for that event/competition index
                // additional calls passing the same index will cause an error
                Thread.sleep(FOE_BULK_TIMEOUT);
                updatePriceInformation();
            } catch (FOEException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialiseBulkMap() {
        indexToPricesMap = new HashMap<>();
        try {
            // retrieve all ( event type / competition ID ) indexes for football, then find champions league ID
            String eventTypeId = "1";
            List<BulkListMarketPricesIndex> bulkMarketPricesIndexes = getBulkMarketPricesIndexes(eventTypeId);

            // initialise bulk cache of all
            for (BulkListMarketPricesIndex index : bulkMarketPricesIndexes) {
                ListMarketPricesResult bulkMarketPrices = getBulkMarketPrices(index.getEventTypeId(), index.getCompetitionId(),
                        Sets.newHashSet(ResponseFilter.MARKET_DEFINITION, ResponseFilter.PRICE_DEFINITION));
                indexToPricesMap.put(new BulkListMarketIndex(index.getEventTypeId(), index.getCompetitionId()), bulkMarketPrices);
            }
        } catch (FOEException e) {
            e.printStackTrace();
        }
    }

    private void updatePriceInformation() throws FOEException {
        System.out.println("Updating price information...");
        // for each index stored within the cache, call the bulk API again (only for price definition)
        // update the cached prices for each of the runners
        for (BulkListMarketIndex index : indexToPricesMap.keySet()) {
            ListMarketPricesResult currentData = indexToPricesMap.get(index);
            ListMarketPricesResult priceUpdates = getBulkMarketPrices(index, Collections.singleton(ResponseFilter.PRICE_DEFINITION));
            List<MarketDetails> currentDetails = currentData.getMarketDetails();
            List<MarketDetails> updatedDetails = priceUpdates.getMarketDetails();
            for (int md = 0; md < currentDetails.size(); md++) {
                List<RunnerDetails> currentRunners = currentDetails.get(md).getRunnerDetails();
                List<RunnerDetails> updatedRunners = updatedDetails.get(md).getRunnerDetails();
                for (int rd = 0; rd < currentRunners.size(); rd++) {
                    currentRunners.get(rd).setEachwayRunnerOdds(updatedRunners.get(rd).getEachwayRunnerOdds());
                    currentRunners.get(rd).setWinRunnerOdds(updatedRunners.get(rd).getWinRunnerOdds());
                }
            }
        }
        System.out.println("Price information updated!");
    }

    private List<BulkListMarketPricesIndex> getBulkMarketPricesIndexes(String eventTypeId) throws FOEException {
        BulkListMarketPricesIndexRequestParams params = new BulkListMarketPricesIndexRequestParams();
        params.setEventTypeIds(Collections.singleton(eventTypeId)); // football
        return foeOperations.bulkListMarketPricesIndex(params, applicationKey, sessionToken);
    }

    private ListMarketPricesResult getBulkMarketPrices(BulkListMarketIndex index, Set<ResponseFilter> responseFilters) throws FOEException {
        return getBulkMarketPrices(index.getEventTypeId(), index.getCompetitionId(), responseFilters);
    }

    private ListMarketPricesResult getBulkMarketPrices(String eventTypeId, String competitionId, Set<ResponseFilter> responseFilters) throws FOEException {
        BulkListMarketPricesRequestParams params = new BulkListMarketPricesRequestParams();
        params.setResponseFilter(responseFilters);
        params.setCompetitionId(competitionId);
        params.setEventTypeId(eventTypeId);
        return foeOperations.bulkListMarketPrices(params, applicationKey, sessionToken);
    }
}
