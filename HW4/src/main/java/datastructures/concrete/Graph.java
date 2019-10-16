package datastructures.concrete;


import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IEdge;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Sorter;
import misc.exceptions.NoPathExistsException;
import misc.exceptions.NoSuchKeyException;


/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    private IDictionary<V, IList<E>> graph;
    private int numEdges;
    private int numVert;
    private IList<V> vertices;
    private IList<E> edges;

    public Graph(IList<V> vertices, IList<E> edges) {
        numEdges = 0;
        numVert = 0;
        this.vertices = vertices;
        this.edges = edges;
        this.graph = createCHDGraph();
    }

    private IDictionary<V, IList<E>> createCHDGraph(){
        IDictionary<V, IList<E>> graph1 = new ChainedHashDictionary<>();
        for (V vertex : vertices) {
            if (vertex ==  null) {
                throw new IllegalArgumentException();
            }
            if (!graph1.containsKey(vertex)) { //only adds vertex to graph if it is not already present
                graph1.put(vertex, new DoubleLinkedList<>());
            }
            numVert++;
        }
        for (E edge : edges) {
            V v1 = edge.getVertex1();
            V v2 = edge.getVertex2();
            double weight = edge.getWeight();
            if (v1 == null || v2 == null || weight < 0 || !graph1.containsKey(v1) || !graph1.containsKey(v2)) {
                throw new IllegalArgumentException();
            }
            IList<E> v1Edges = graph1.get(v1);
            v1Edges.add(edge);
            graph1.put(v1, v1Edges);
            if (v1 != v2) { // doesn't add edge twice if it is a looping edge
                IList<E> v2Edges = graph1.get(v2);
                v2Edges.add(edge);
                graph1.put(v2, v2Edges);
            }
            numEdges++;
        }
        return graph1;
    }
    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return numVert;
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return numEdges;
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        IDisjointSet<V> vDJS = new ArrayDisjointSet<>();
        for (V v : vertices) {
            vDJS.makeSet(v);
        }
        ISet<E> mST = new ChainedHashSet<>();
        IList<E> sorted = Sorter.topKSort(edges.size(), edges);
        for (E e : sorted) {
            if (vDJS.findSet(e.getVertex1()) != vDJS.findSet(e.getVertex2())) {
                mST.add(e);
                vDJS.union(e.getVertex1(), e.getVertex2());
            }
        }
        return mST;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException();
        }
        if (start == end) {
            return new DoubleLinkedList<>();
        }

        IDictionary<V, VNode> vertexMap = new ChainedHashDictionary<>();
        IDictionary<V, E> vertexToEdge = new ChainedHashDictionary<>();
        IList<VNode> fromSource = new DoubleLinkedList<>();
        for (V currVert : vertices) {
            VNode currVertNew;
            if (currVert.equals(start)) {
                currVertNew = new VNode(0.0, start); // Avoid duplicate
            } else {
                currVertNew = new VNode(Double.POSITIVE_INFINITY, currVert);
            }
            fromSource.add(currVertNew);
            vertexMap.put(currVert, currVertNew);
        }
        IPriorityQueue<VNode> bydist = new ArrayHeap<>(fromSource);
        ISet<V> processed = new ChainedHashSet<>();
        while (!bydist.isEmpty()) {
            VNode u = bydist.removeMin();
            if (processed.contains(u.vertex)){
                continue;
            }
            for (E edge : graph.get(u.vertex)){
                V v = edge.getOtherVertex(u.vertex); // edge from u to v
                Double newDist = u.distance + edge.getWeight();
                Double oldDist = vertexMap.get(v).distance;
                if (newDist < oldDist){
                    VNode newv = new VNode(newDist, v);
                    vertexToEdge.put(v, edge);
                    vertexMap.put(v, newv);
                    bydist.insert(newv);
                }
            }
            processed.add(u.vertex);
        }
        IList<E> path = new DoubleLinkedList<>();
        V last = end;
        while (last != start){
            E thisone;
            try{
                thisone = vertexToEdge.get(last);
            } catch (NoSuchKeyException e) {
                throw new NoPathExistsException();
            }
            path.insert(0, thisone);
            last = thisone.getOtherVertex(last);
        }
        return path;
    }

    private class VNode implements Comparable<VNode> {
        public Double distance;
        public V vertex;

        public VNode(Double distance, V vertex) {
            this.distance = distance;
            this.vertex = vertex;
        }

        @Override
        public int compareTo(VNode otherVNode) {
            return this.distance.compareTo(otherVNode.distance);
        }
    }
}

