package io.github.jlmc.types;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class Inet implements Serializable {

    private final String address;

    public Inet(final String address) {
        this.address = address;
    }

    public static Inet of(String address) {
        return new Inet(address);
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(address, Inet.class.cast(o).address);
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
