package io.costa.hibernatetunings.customtype;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;

public final class MacAddr implements Serializable {

    private static final String REGEX = "^([0-9A-Fa-f]{2}[//.:-]){5}([0-9A-Fa-f]{2})$";

    private final String address;

    private MacAddr(final String address) {
        validate(address);
        this.address = formatAddress(address);
    }

    public static MacAddr of(final String address) {
        return new MacAddr(address);
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MacAddr a = (MacAddr) o;
        return this.address.equalsIgnoreCase(a.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public String toString() {
        return '[' + address + ']';
    }

    private void validate(final String address) {
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("MacAddress should not be null");
        }
        if (!isValid(address)) {
            throw new IllegalArgumentException(String.format("'%s' is not a valid MacAddress", address));
        }
    }

    private String formatAddress(String address) {
        Objects.requireNonNull(address);

        char[] chars = address.replaceAll("[^a-zA-Z0-9\\s+]", "").toLowerCase().toCharArray();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i > 0 && i % 2 == 0) {
                sb.append(":");
            }
            sb.append(chars[i]);
        }

        return sb.toString();
    }

    private boolean isValid(String mac) {
        Objects.requireNonNull(mac);
        try {
            return mac.matches(REGEX);
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
