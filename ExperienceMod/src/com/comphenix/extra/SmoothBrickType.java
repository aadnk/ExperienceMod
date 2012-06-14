package com.comphenix.extra;

/**
* Represents the four different types of Smooth Brick
*/
public enum SmoothBrickType {
    NORMAL(0x0),
    MOSSY(0x1),
    CRACKED(0x2),
    CIRCLE(0x3);

    private final byte data;

    private SmoothBrickType(final int data) {
        this.data = (byte) data;
    }

    /**
	* Gets the associated data value representing this type of smooth brick
	*
	* @return A byte containing the data value of this smooth brick type
	*/
    public byte getData() {
        return data;
    }
}
