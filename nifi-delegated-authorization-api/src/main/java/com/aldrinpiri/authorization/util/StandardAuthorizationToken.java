package com.aldrinpiri.authorization.util;

/**
 * Created by apiri on 23Feb2016.
 */
public class StandardAuthorizationToken implements AuthorizationToken {

    final private String tokenValue;

    public StandardAuthorizationToken(final String tokenValue) {
        this.tokenValue = tokenValue;
    }

    @Override
    public String getValue() {
        return tokenValue;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StandardAuthorizationToken that = (StandardAuthorizationToken) o;

        return tokenValue != null ? tokenValue.equals(that.tokenValue) : that.tokenValue == null;

    }

    @Override
    public int hashCode() {
        return tokenValue != null ? tokenValue.hashCode() : 0;
    }
}
