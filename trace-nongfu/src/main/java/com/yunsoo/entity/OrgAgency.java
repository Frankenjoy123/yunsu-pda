package com.yunsoo.entity;

/**
 * Created by frank on 2016/6/20.
 */
public class OrgAgency {
    private String id;
    private String name;
    private int inbound_count;
    private int outbound_count;

    public int getInbound_count() {
        return inbound_count;
    }

    public void setInbound_count(int inbound_count) {
        this.inbound_count = inbound_count;
    }

    public int getOutbound_count() {
        return outbound_count;
    }

    public void setOutbound_count(int outbound_count) {
        this.outbound_count = outbound_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
