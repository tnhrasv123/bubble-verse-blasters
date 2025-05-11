
package com.bubblegum.models;

import java.util.ArrayList;
import java.util.List;

public class Backpack {
    
    private String type;
    private int capacity;
    private final List<Bubble> bubbles;
    
    public Backpack(String type, int capacity) {
        this.type = type;
        this.capacity = capacity;
        this.bubbles = new ArrayList<>();
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public int getUsed() {
        return bubbles.size();
    }
    
    public boolean isFull() {
        return bubbles.size() >= capacity;
    }
    
    public boolean addBubble(Bubble bubble) {
        if (bubbles.size() < capacity) {
            bubbles.add(bubble);
            return true;
        }
        return false;
    }
    
    public List<Bubble> getBubbles() {
        return new ArrayList<>(bubbles);
    }
    
    public void clearBubbles() {
        bubbles.clear();
    }
    
    public long calculateTotalValue() {
        long totalValue = 0;
        for (Bubble bubble : bubbles) {
            totalValue += bubble.calculateValue();
        }
        return totalValue;
    }
}
