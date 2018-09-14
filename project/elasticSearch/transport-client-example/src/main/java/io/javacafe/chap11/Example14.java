package io.javacafe.chap11;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

public class Example14 {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Settings settings = Settings.builder() .put("cluster.name", "javacafe-es").build();

        TransportClient client =
                new PreBuiltTransportClient(settings)
                        .addTransportAddress(new TransportAddress(
                                InetAddress.getByName("127.0.0.1"), 9300));
        String INDEX_NAME = "tweet";
        String TYPE_NAME = "info";
        String FIELD_NAME = "text";
        String QUERY = "만점";

        String AGGREGATION_NAME = "MIN_AGG_NM";
        String AGG_FIELD_NAME = "tweetId";
        MinAggregationBuilder aggregation = AggregationBuilders.min(AGGREGATION_NAME)
                .field(AGG_FIELD_NAME);

        //Aggregation으로 출력할 결과물을 addAggregation메소드에 담아 출력한다.
        SearchResponse response = client.prepareSearch(INDEX_NAME)
                .setTypes(TYPE_NAME)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery(FIELD_NAME, QUERY))
                .addAggregation(aggregation)
                .setSize(0)
                .get();

        //결과 출력
        Min minAgg = response.getAggregations().get(AGGREGATION_NAME);
        double value = minAgg.getValue();


    }
}
