package com.jml.reconciliation.model.enums;

public enum PermissionLevel {
    NONE(0),
    READ(1),
    USER(2),
    WRITE(3),
    ADMIN(4);

    private final int rank;

    PermissionLevel(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public boolean isHigherThan(PermissionLevel other) {
        if (other == null) return true;
        return this.rank > other.rank;
    }

    public boolean isLowerThan(PermissionLevel other) {
        if (other == null) return false;
        return this.rank < other.rank;
    }
}
