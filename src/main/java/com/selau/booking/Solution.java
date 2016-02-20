package com.selau.booking;

import java.io.*;
import java.util.*;


/**
 *  Dijkstra's algorithm implementation using a priority queue based on
 *  https://en.wikipedia.org/wiki/Dijkstra's_algorithm
 *
 *   function DijkstraCalculatorImpl(Graph, source):
 *       dist[source] ← 0                                    // Initialization
 *
 *       create vertex set Q
 *
 *       for each vertex v in Graph:
 *           if v ≠ source
 *               dist[v] ← INFINITY                          // Unknown distance from source to v
 *               prev[v] ← UNDEFINED                         // Predecessor of v
 *
 *           Q.add_with_priority(v, dist[v])
 *
 *       while Q is not empty:                              // The main loop
 *           u ← Q.extract_min()                            // Remove and return best vertex
 *           for each neighbor v of u:                      // only v that is still in Q
 *               alt = dist[u] + length(u, v)
 *               if alt < dist[v]
 *                   dist[v] ← alt
 *                   prev[v] ← u
 *                   Q.decrease_priority(v, alt)
 *
 *       return dist[], prev[]
*/
public class Solution {
	
    static Integer INFINITE_DISTANCE = Integer.valueOf(Integer.MAX_VALUE);
    static Integer NO_DISTANCE = Integer.valueOf(0);

    public static void main(String[] args) {
    	final Scanner scanner = new Scanner(System.in);
        final int cases = scanner.nextInt();
        
        for (int weekendCase = 1; weekendCase <= cases; weekendCase++) {
        	final int locations = scanner.nextInt();
            final int roads = scanner.nextInt();
            
            for (int road = 0; road < roads; road++) {
            	final int source = scanner.nextInt();
            	final int target = scanner.nextInt();
            	final int distance = scanner.nextInt();
            	
            	System.out.println(source + " " + target + " " + distance);
            }
        	
        }
        scanner.close();

        //System.out.println(ways);
    }
    
    public static Map<Node, Integer> calculate(final RoadsNetwork graph, final Node source) {

        if ((graph == null) || (source == null))
            throw new IllegalArgumentException("Invalid null arguments for the algorithm.");

        final Map<Node, Integer> shortestDistances = new HashMap<Node, Integer>();
        final Map<Node, EvaluatedNodeWrapper> evaluatedNodesMap = new HashMap<Node, EvaluatedNodeWrapper>();
        final PriorityQueue<EvaluatedNodeWrapper> priorityQueue = new PriorityQueue<EvaluatedNodeWrapper>();

        shortestDistances.put(source, NO_DISTANCE);

        final EvaluatedNodeWrapper evaluatedSourceNode = new EvaluatedNodeWrapper(source, NO_DISTANCE);
        evaluatedNodesMap.put(source, evaluatedSourceNode);
        priorityQueue.add(evaluatedSourceNode);

        for (final Node node : graph.nodes()) {
            if (! node.equals(source)) {

                final EvaluatedNodeWrapper evaluatedNode = new EvaluatedNodeWrapper(node, INFINITE_DISTANCE);

                evaluatedNodesMap.put(node, evaluatedNode);
                shortestDistances.put(node, INFINITE_DISTANCE);
                priorityQueue.add(evaluatedNode);
            }
        }

        while (! priorityQueue.isEmpty()) {

            final EvaluatedNodeWrapper leastDistanceNode = priorityQueue.poll();

            if (leastDistanceNode.distance() == INFINITE_DISTANCE)
                break;

            final Set<Node> neighbors = graph.neighbors(leastDistanceNode.node());
            if ((neighbors == null) || (neighbors.isEmpty()))
                continue;

            for (final Node neighbor : neighbors) {

                final int currentNeighborDistance =  graph.distance(leastDistanceNode.node(), neighbor);
                final int newNeighborDistance = leastDistanceNode.distance() + currentNeighborDistance;

                final EvaluatedNodeWrapper evaluatedNeighborNode = evaluatedNodesMap.get(neighbor);

                if ((newNeighborDistance < evaluatedNeighborNode.distance())
                        || (evaluatedNeighborNode.distance() == NO_DISTANCE)) {

                    final EvaluatedNodeWrapper newNeighborEvaluation = new EvaluatedNodeWrapper(neighbor, newNeighborDistance);
                    evaluatedNodesMap.put(neighbor, newNeighborEvaluation);

                    final boolean neighborRemoved = priorityQueue.remove(evaluatedNeighborNode);
                    if (neighborRemoved)
                        priorityQueue.add(newNeighborEvaluation);

                    shortestDistances.put(neighbor, Integer.valueOf(newNeighborDistance));
                }
            }
        }
        return shortestDistances;
    }
    
    interface Node {
    	
        String name();
        
    }
    
    static class EvaluatedNodeWrapper implements Node, Comparable<EvaluatedNodeWrapper> {

        private final Node node;
        private final int distance;

        public EvaluatedNodeWrapper(final Node node, final int distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public String name() {
            return node.name();
        }

        public Node node() {
            return node;
        }

        public int distance() {
            return distance;
        }

        @Override
        public int compareTo(final EvaluatedNodeWrapper other) {

            if (this.node.equals(other.node))
                return 0;

            if (other.distance() > this.distance())
                return -1;

            return 1;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((node == null) ? 0 : node.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final EvaluatedNodeWrapper other = (EvaluatedNodeWrapper) obj;
            if (node == null) {
                if (other.node != null)
                    return false;
            } else if (!node.equals(other.node))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "EvaluatedNodeWrapper [node=" + node + ", distance=" + distance + "]";
        }

    }
    
    class RoadsNetwork {

        private final Map<RoadConnection, Integer> distances;
        private final Map<Node, Set<Node>> adjacents;
        private final Set<Node> nodes;

        public RoadsNetwork(
                final Map<RoadConnection, Integer> distances,
                final Map<Node, Set<Node>> adjacentTowns,
                final Set<Node> towns) {

            this.distances = distances;
            this.adjacents = adjacentTowns;
            this.nodes = towns;
        }

        public Set<Node> nodes() {
            return this.nodes;
        }

        public Set<Node> neighbors(Node node) {
            return this.adjacents.get(node);
        }

        public Integer distance(Node source, Node target) {
            final RoadConnection edge = new RoadConnection(source, target);
            return this.distances.get(edge);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((adjacents == null) ? 0 : adjacents.hashCode());
            result = prime * result + ((distances == null) ? 0 : distances.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof RoadsNetwork))
                return false;
            RoadsNetwork other = (RoadsNetwork) obj;
            if (adjacents == null) {
                if (other.adjacents != null)
                    return false;
            } else if (!adjacents.equals(other.adjacents))
                return false;
            if (distances == null) {
                if (other.distances != null)
                    return false;
            } else if (!distances.equals(other.distances))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "RoadsNetwork [distances=" + distances + ", adjacents=" + adjacents + "]";
        }

    }
    
    class RoadConnection {

        private final Node from;
        private final Node to;

        public RoadConnection(final Node from, final Node to) {
            this.from = from;
            this.to = to;
        }

        public Node from() {
            return from;
        }

        public Node to() {
            return to;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((from == null) ? 0 : from.hashCode());
            result = prime * result + ((to == null) ? 0 : to.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof RoadConnection))
                return false;
            RoadConnection other = (RoadConnection) obj;
            if (from == null) {
                if (other.from != null)
                    return false;
            } else if (!from.equals(other.from))
                return false;
            if (to == null) {
                if (other.to != null)
                    return false;
            } else if (!to.equals(other.to))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "RoadConnection [from=" + from + ", to=" + to + "]";
        }

    }
}
