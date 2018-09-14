package io.javacafe.chap11;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

public class Example12 {
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



        QueryBuilder queryBuilder = QueryBuilders.termQuery(FIELD_NAME, QUERY);
        SearchResponse scrollResp = client.prepareSearch(INDEX_NAME)
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(queryBuilder)
                .setSize(30).get();

        //해당 스크롤을 이용하여 재 검색
        do {

            //데이터 출력
            for (SearchHit hit : scrollResp.getHits().getHits()) {

                String tweetId= hit.getSourceAsMap().get("tweetId").toString();
                String tweetLang = hit.getSourceAsMap().get("tweetLang").toString();
                String text = hit.getSourceAsMap().get("text").toString();
            }

            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
                    .setScroll(new TimeValue(60000)).execute().actionGet();
        } while(scrollResp.getHits().getHits().length != 0);


    }
}
