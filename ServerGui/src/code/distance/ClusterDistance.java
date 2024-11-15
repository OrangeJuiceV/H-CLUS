package code.distance;

import code.clustering.Cluster;
import code.data.Data;
import code.data.InvalidSizeException;

/**
 * Interfaccia ClusterDistance
 * contiene metodo per
 * calcolare la distanza tra due cluster
 *
 */
public interface ClusterDistance {
	/**
	 * metodo distance
	 *
	 * @param c1 primo cluster
	 * @param c2 secondo cluster
	 * @param d dataset
	 * @return double
	 */
	double distance(Cluster c1, Cluster c2, Data d) throws InvalidSizeException;
}
