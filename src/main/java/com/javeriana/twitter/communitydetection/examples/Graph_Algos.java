package com.javeriana.twitter.communitydetection.examples;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class Graph_Algos {
  static int edgeCount_Directed = 0;

  class MyNode {
    String id;

    public MyNode(String id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return "V" + this.id;
    }
  }

  class MyLink {
    double weight;
    int id;

    public MyLink(double weight) {
      this.id = edgeCount_Directed++;
      this.weight = weight;
    }

    @Override
    public String toString() {
      return "E" + this.id;
    }
  }

  public void Community_Detection_Using_BC(LinkedList<String> Distinct_nodes,
      LinkedList<String> source_vertex, LinkedList<String> target_vertex,
      LinkedList<Double> Edge_Weight) {
    // CREATING weighted directed graph
    Graph<MyNode, MyLink> g = new DirectedSparseGraph<>();
    // create node objects
    Hashtable<String, MyNode> Graph_Nodes = new Hashtable<>();
    LinkedList<MyNode> Source_Node = new LinkedList<>();
    LinkedList<MyNode> Target_Node = new LinkedList<>();
    // create graph nodes
    for (int i = 0; i < Distinct_nodes.size(); i++) {
      String node_name = Distinct_nodes.get(i);
      MyNode data = new MyNode(node_name);
      Graph_Nodes.put(node_name, data);
    }
    // Now convert all source and target nodes into objects
    for (int t = 0; t < source_vertex.size(); t++) {
      Source_Node.add(Graph_Nodes.get(source_vertex.get(t)));
      Target_Node.add(Graph_Nodes.get(target_vertex.get(t)));
    }
    // Now add nodes and edges to the graph
    for (int i = 0; i < Edge_Weight.size(); i++) {
      g.addEdge(new MyLink(Edge_Weight.get(i)), Source_Node.get(i), Target_Node.get(i),
          EdgeType.DIRECTED);
    }
    // Printing the graph
    System.out.println("Graph: " + g);

    // Now call the graph algorithms
    edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer<MyNode, MyLink> EBC1 =
        new EdgeBetweennessClusterer<>(1);
    Set<Set<MyNode>> sets = EBC1.apply(g);
    Iterator<Set<MyNode>> keys = sets.iterator();
    while (keys.hasNext()) {
      System.out.println(keys.next());
    }

  }// end of public void Calculate_BetweenNess_Centrality(LinkedList<String> Distinct_nodes,
   // LinkedList<String> source_vertex, LinkedList<String> target_vertex, LinkedList<Double>
   // Edge_Weight, String SourceV, String TargetV)

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    // let the nodes of graph are: {A, B, C, D, E, F, G}
    // Directed edges are: {AB=0.7, BC=0.9, CD=0.57, DB=1.0, CA=1.3, AD=0.3, DF=0.2, DE=0.8, EG=0.4,
    // FE=0.6, GF=0.2}

    Graph_Algos GA1 = new Graph_Algos();

    LinkedList<String> Distinct_Vertex = new LinkedList<>();
    LinkedList<String> Source_Vertex = new LinkedList<>();
    LinkedList<String> Target_Vertex = new LinkedList<>();
    LinkedList<Double> Edge_Weight = new LinkedList<>();

    // add the distinct vertexes
    Distinct_Vertex.add("A");
    Distinct_Vertex.add("B");
    Distinct_Vertex.add("C");
    Distinct_Vertex.add("D");
    Distinct_Vertex.add("E");
    Distinct_Vertex.add("F");
    Distinct_Vertex.add("G");

    Source_Vertex.add("A");
    Target_Vertex.add("B");
    Edge_Weight.add(0.7);
    Source_Vertex.add("B");
    Target_Vertex.add("C");
    Edge_Weight.add(0.9);
    Source_Vertex.add("C");
    Target_Vertex.add("D");
    Edge_Weight.add(0.57);
    Source_Vertex.add("D");
    Target_Vertex.add("B");
    Edge_Weight.add(1.0);
    Source_Vertex.add("C");
    Target_Vertex.add("A");
    Edge_Weight.add(1.3);
    Source_Vertex.add("A");
    Target_Vertex.add("D");
    Edge_Weight.add(0.3);
    Source_Vertex.add("D");
    Target_Vertex.add("F");
    Edge_Weight.add(0.2);
    // Source_Vertex.add("D"); Target_Vertex.add("E"); Edge_Weight.add(0.8);
    Source_Vertex.add("E");
    Target_Vertex.add("G");
    Edge_Weight.add(0.4);
    Source_Vertex.add("F");
    Target_Vertex.add("E");
    Edge_Weight.add(0.6);
    Source_Vertex.add("G");
    Target_Vertex.add("F");
    Edge_Weight.add(0.2);

    System.out.println("Community Detection ");
    GA1.Community_Detection_Using_BC(Distinct_Vertex, Source_Vertex, Target_Vertex, Edge_Weight);
  }

}
