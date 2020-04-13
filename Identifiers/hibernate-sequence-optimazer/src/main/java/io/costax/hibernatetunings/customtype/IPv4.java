package io.costax.hibernatetunings.customtype;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class IPv4 implements Serializable {

    private final String address;

    public IPv4(final String address) {
        this.address = address;
    }

    public static IPv4 of(String address) {
        return new IPv4(address);
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(address, ((IPv4) o).address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    public InetAddress toInetAddress() {
        try {
            String host = address.replaceAll("\\/.*$", "");
            return Inet4Address.getByName(host);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }
}
