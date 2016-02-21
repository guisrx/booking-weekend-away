package com.selau.booking;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;


public class Solution {
	
    static Integer INFINITE_DISTANCE = Integer.valueOf(Integer.MAX_VALUE);
    static Integer NO_DISTANCE = Integer.valueOf(0);

    public static void main(String[] args) {
    	final Scanner scanner = new Scanner(System.in);
        final int cases = scanner.nextInt();
        
        for (int weekendCase = 1; weekendCase <= cases; weekendCase++) {
        	final int locations = scanner.nextInt();
            final int roads = scanner.nextInt();
            
            final int leastDistanceWith2Hops = calculateLeastDistanceWith2Hops(scanner, locations, roads);
        	
            System.out.println(leastDistanceWith2Hops);
        }
        scanner.close();
    }

	private static int calculateLeastDistanceWith2Hops(final Scanner scanner, final int locations, final int roads) {
		final PriorityQueue<Node> nodes = new PriorityQueue<Node>();
		final Node locationsArray[] = new Location[locations];
		
		for (int locationCount = 0; locationCount < locations; locationCount++) {
			final Location location = new Location(Integer.toString(locationCount +1), INFINITE_DISTANCE);
			
			locationsArray[locationCount] = location;
			nodes.add(location);
		}
		
		final Map<RoadConnection, Integer> distances = new HashMap<RoadConnection, Integer>();
		final Map<Node, Set<Node>> adjacents = new HashMap<Node, Set<Node>>();
		
		for (int road = 0; road < roads; road++) {
			final int source = scanner.nextInt();
			final int target = scanner.nextInt();
			final int distance = scanner.nextInt();
			
			Node sourceNode = locationsArray[source-1];
			Node targetNode = locationsArray[target-1];
			
			if (distance < sourceNode.leastDistance()) {
				nodes.remove(sourceNode);
				sourceNode = new Location(sourceNode.name(), distance);
				locationsArray[source-1] = sourceNode;
				nodes.add(sourceNode);
			}
			
			if (distance < targetNode.leastDistance()) {
				nodes.remove(targetNode);
				targetNode = new Location(targetNode.name(), distance);
				locationsArray[target-1] = targetNode;
				nodes.add(targetNode);
			}
			
		    Set<Node> sourceNodeAdjacents = adjacents.get(sourceNode);
		    if (sourceNodeAdjacents == null) {
		    	sourceNodeAdjacents = new HashSet<Node>();
		    	adjacents.put(sourceNode, sourceNodeAdjacents);
		    }
		    
		    Set<Node> targetNodeAdjacents = adjacents.get(targetNode);
		    if (targetNodeAdjacents == null) {
		    	targetNodeAdjacents = new HashSet<Node>();
		    	adjacents.put(targetNode, targetNodeAdjacents);
		    }

		    sourceNodeAdjacents.add(targetNode);
		    targetNodeAdjacents.add(sourceNode);

		    distances.put(new RoadConnection(sourceNode, targetNode), Integer.valueOf(distance));
		    distances.put(new RoadConnection(targetNode, sourceNode), Integer.valueOf(distance));
		}
		final RoadsNetwork roadsNetwork = new RoadsNetwork(distances, adjacents);
		int leastDistanceWith2Hops = INFINITE_DISTANCE;
		
		while (! nodes.isEmpty()) {
			final Node firstLocation = nodes.poll();
			
			if (firstLocation.leastDistance() > leastDistanceWith2Hops) {
				return leastDistanceWith2Hops;
			}
			
			final int least2HopsCostForLocation = calculateLeast2HopsCost(roadsNetwork, firstLocation, leastDistanceWith2Hops);
			
			if (least2HopsCostForLocation < leastDistanceWith2Hops) {
				leastDistanceWith2Hops = least2HopsCostForLocation;
			}
		}
		return leastDistanceWith2Hops;
	}
    
    private static int calculateLeast2HopsCost(RoadsNetwork roadsNetwork, Node location, int partialLeastDistanceWith2Hops) {
    	int least2HopsCostForLocation = INFINITE_DISTANCE;
    	
    	for (final Node firstNeighbour : roadsNetwork.neighbors(location)) {
    		final int firstNeighbourCost = roadsNetwork.distance(location, firstNeighbour);
    		
    		if (firstNeighbourCost < partialLeastDistanceWith2Hops) {
	    		for (final Node secondNeighbour : roadsNetwork.neighbors(firstNeighbour)) {
	        		final int cost = firstNeighbourCost + roadsNetwork.distance(firstNeighbour, secondNeighbour);
	        		
	        		if ((cost < least2HopsCostForLocation) && (! secondNeighbour.equals(location))) {
	        			least2HopsCostForLocation = cost;
	    			}
	        	}
    		}
    	}
		return least2HopsCostForLocation;
	}

	interface Node extends Comparable<Node> {
    	
        String name();
        
        int leastDistance();
        
    }
    
	static class RoadsNetwork {

        private final Map<RoadConnection, Integer> distances;
        private final Map<Node, Set<Node>> adjacents;

        public RoadsNetwork(
                final Map<RoadConnection, Integer> distances,
                final Map<Node, Set<Node>> adjacentTowns) {

            this.distances = distances;
            this.adjacents = adjacentTowns;
        }

        public Set<Node> neighbors(Node node) {
            final Set<Node> neighbors = this.adjacents.get(node);
            
            if (neighbors == null)
            	return new HashSet<Node>();
			return neighbors;
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
    
    static class RoadConnection {

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
    
    static class Location implements Node {

        private final String name;
        private final int leastDistance;

        public Location(final String name, final int leastDistance) {
            this.name = name;
            this.leastDistance = leastDistance;
        }

        @Override
        public String name() {
            return this.name;
        }
        
        @Override
        public int leastDistance() {
        	return leastDistance;
        }
        
        @Override
        public int compareTo(final Node other) {

            if (this.leastDistance == other.leastDistance())
                return 0;

            if (other.leastDistance() > this.leastDistance)
                return -1;

            return 1;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof Location))
                return false;
            Location other = (Location) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Location [name=" + name + "]";
        }

    }
}
