package com.javeriana.twitter.communitydetection.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import com.javeriana.twitter.communitydetection.dto.graph.TwitterEdge;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterVertex;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class GraphUtils {
  public static final String COMMUNITY_PATH = "communities/";

  public static byte[] getImageBytesFromGraph(Graph<TwitterVertex, TwitterEdge> graph)
      throws IOException {

    BufferedImage image = createBufferedImageFromGraph(graph);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "png", baos);
    baos.flush();
    byte[] imageBytes = baos.toByteArray();
    baos.close();
    return imageBytes;
  }

  private static BufferedImage createBufferedImageFromGraph(
      Graph<TwitterVertex, TwitterEdge> graph) {
    Layout<TwitterVertex, TwitterEdge> layout = new CircleLayout<>(graph);
    layout.setSize(new Dimension(1024, 768));
    BasicVisualizationServer<TwitterVertex, TwitterEdge> vv =
        new BasicVisualizationServer<>(layout);
    vv.setPreferredSize(new Dimension(1000, 700));

    VisualizationImageServer<TwitterVertex, TwitterEdge> vis =
        new VisualizationImageServer<>(vv.getGraphLayout(), vv.getGraphLayout().getSize());

    // Configure the VisualizationImageServer the same way
    // you did your VisualizationViewer. In my case e.g.

    vis.setBackground(Color.WHITE);
    vis.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    vis.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

    // Create the buffered image
    return (BufferedImage) vis.getImage(
        new Point2D.Double(vv.getGraphLayout().getSize().getWidth() / 2,
            vv.getGraphLayout().getSize().getHeight() / 2),
        new Dimension(vv.getGraphLayout().getSize()));
  }

  public static void createCommunityImagesFromGraphList(
      List<Graph<TwitterVertex, TwitterEdge>> graphs) throws IOException {
    createCommunityImagesFromGraphList(graphs, "community");
  }

  public static void createCommunityImagesFromGraphList(
      List<Graph<TwitterVertex, TwitterEdge>> graphs, String filePrefix) throws IOException {
    File directory = new File(COMMUNITY_PATH);

    if (!directory.exists()) {
      directory.mkdir();
    } else {
      directory.delete();
    }
    int i = 0;
    for (Graph<TwitterVertex, TwitterEdge> graph : graphs) {
      String fileName = new StringBuilder(filePrefix).append(i).toString();
      BufferedImage image = createBufferedImageFromGraph(graph);
      File outputFile =
          new File(new StringBuilder(COMMUNITY_PATH).append(fileName).append(".png").toString());
      ImageIO.write(image, "png", outputFile);
      ++i;
    }
  }

  private GraphUtils() {
    super();
  }
}
