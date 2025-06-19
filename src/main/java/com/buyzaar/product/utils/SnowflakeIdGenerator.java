package com.buyzaar.product.utils;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class SnowflakeIdGenerator {

    private static final long EPOCH = 1704067200000L; // Custom epoch
    private static final long MACHINE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private final long machineId;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator() {
        this.machineId = generateMachineId(); // Safe fallback mechanism
    }

    private long generateMachineId() {
        // Simple fallback to hash of hostname or JVM instance
        try {
            String hostname = java.net.InetAddress.getLocalHost().getHostName();
            return Math.abs(hostname.hashCode()) % (MAX_MACHINE_ID + 1);
        } catch (Exception e) {
            return new Random().nextInt((int) MAX_MACHINE_ID); // fallback random machine ID
        }
    }

    public synchronized long nextId() {
        long timestamp = currentTimestamp();

        if (timestamp < lastTimestamp) {
            timestamp = waitUntilNextMillis(lastTimestamp);
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = waitUntilNextMillis(timestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << (MACHINE_ID_BITS + SEQUENCE_BITS))
                | (machineId << SEQUENCE_BITS)
                | sequence;
    }

    private long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = currentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp();
        }
        return timestamp;
    }

    private long currentTimestamp() {
        return System.currentTimeMillis();
    }
}
