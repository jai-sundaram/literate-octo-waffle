package com.meridian.commerce.inventory;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    private String code;

    private String name;

    private String state;

    /** Lower number = preferred for fulfilment. */
    private int priority;

    private boolean active = true;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
