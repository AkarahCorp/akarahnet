package akarahnet.util;

import org.bukkit.util.Vector;

import java.util.List;

public class LocalPDTs {
    public static Vector fromList(List<Double> list) {
        return new Vector(list.get(0), list.get(1), list.get(2));
    }

    public static List<Double> fromVector(Vector vector) {
        return List.of(vector.getX(), vector.getY(), vector.getZ());
    }
}
