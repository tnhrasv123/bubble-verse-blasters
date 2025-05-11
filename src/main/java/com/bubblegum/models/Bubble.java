
package com.bubblegum.models;

public class Bubble {
    
    private double size;
    private final String gumType;
    
    public Bubble(double size, String gumType) {
        this.size = size;
        this.gumType = gumType;
    }
    
    public double getSize() {
        return size;
    }
    
    public void setSize(double size) {
        this.size = size;
    }
    
    public String getGumType() {
        return gumType;
    }
    
    public long calculateValue() {
        // Base calculation, will be modified by the BubbleManager
        return (long) (size * 5);
    }
}
