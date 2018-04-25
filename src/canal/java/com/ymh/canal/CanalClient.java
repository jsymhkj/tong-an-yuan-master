package com.ymh.canal;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.fastjson.JSONObject;

public class CanalClient {
    public static void main(String[] args) throws Exception {
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress("127.0.0.1", 11111), "example", "", "");

        connector.connect();
        connector.subscribe(".*\\..*");
        connector.rollback();

        while (true) {
            Message message = connector.getWithoutAck(100);
            long batchId = message.getId();
            if (batchId == -1 || message.getEntries().isEmpty()) {
                System.out.println("sleep");
                Thread.sleep(1000);
                continue;
            }
            printEntries(message.getEntries());
            connector.ack(batchId);
        }
    }

    private static void printEntries(List<Entry> entries) throws Exception {
        for (Entry entry : entries) {

            if (entry.getEntryType() != EntryType.ROWDATA) {
                continue;
            }

            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
            String tableName = entry.getHeader().getTableName();
            for (RowData rowData : rowChange.getRowDatasList()) {
                switch (rowChange.getEventType()) {
                    case INSERT:
                        System.out.println("------->INSERT ");
                        printColumns(tableName,rowData.getAfterColumnsList());
                        redisInsert(tableName,rowData.getAfterColumnsList());
                        break;
                    case UPDATE:
                        System.out.println("------->UPSERT ");
                        System.out.println("-------> before");
                        printColumns(tableName,rowData.getBeforeColumnsList());
                        System.out.println("-------> after");
                        printColumns(tableName,rowData.getAfterColumnsList());
                        redisUpdate(tableName,rowData.getAfterColumnsList());
                        if ("retl_buffer".equals(entry.getHeader().getTableName())) {
                            String newTableName = rowData.getAfterColumns(1).getValue();
                            String pkValue = rowData.getAfterColumns(2).getValue();
                            System.out.println("SELECT * FROM " + newTableName + " WHERE id = " + pkValue);
                        }
                        break;

                    case DELETE:
                        System.out.println("------->DELETE ");
                        printColumns(tableName,rowData.getBeforeColumnsList());
                        redisDelete(tableName,rowData.getBeforeColumnsList());
                        break;

                    default:
                        break;
                }
            }

        }
    }

    private static void printColumns( String tableName, List<Column> columns) {
        String line = columns.stream()
                .map(column -> column.getName() + "=" + column.getValue())
                .collect(Collectors.joining(","));
        System.out.println(tableName);
        System.out.println(line);
    }

    private static void redisInsert( String tableName, List<Column> columns){
        JSONObject json=new JSONObject();
        for (Column column : columns) {
            json.put(column.getName(), column.getValue());
        }
        if(columns.size()>0){
            RedisUtil.hashSet(tableName,columns.get(0).getValue(),json.toJSONString());
            //RedisUtil.stringSet("user:"+ columns.get(0).getValue(),json.toJSONString());
        }
    }

    private static  void redisUpdate( String tableName, List<Column> columns){
        JSONObject json=new JSONObject();
        for (Column column : columns) {
            json.put(column.getName(), column.getValue());
        }
        if(columns.size()>0){
            RedisUtil.hashSet(tableName,columns.get(0).getValue(),json.toJSONString());
            //RedisUtil.stringSet("user:"+ columns.get(0).getValue(),json.toJSONString());
        }
    }

    private static  void redisDelete( String tableName, List<Column> columns){
        JSONObject json=new JSONObject();
        for (Column column : columns) {
            json.put(column.getName(), column.getValue());
        }
        if(columns.size()>0){
            RedisUtil.hashDel(tableName,columns.get(0).getValue());
            //RedisUtil.delKey("user:"+ columns.get(0).getValue());
        }
    }
}