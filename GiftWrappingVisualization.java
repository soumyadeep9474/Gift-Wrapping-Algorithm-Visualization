import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GiftWrappingVisualization extends JPanel implements ActionListener {
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int POINT_RADIUS = 4;
    private static final int DELAY = 1000;

    private List<Point> points;
    private List<Point> convexHull;
    private int currentIndex = 0;
    private JButton generateButton;

    public GiftWrappingVisualization() {
        generateButton = new JButton("Generate new Graph");
        generateButton.addActionListener(this);
        add(generateButton);

        generatePoints();

        Timer timer = new Timer(DELAY, this);
        timer.start();
    }

    private void generatePoints() {
        points = new ArrayList<>();

        // Generate random points
        for (int i = 0; i < 50; i++) {
            int x = (int) (Math.random() * FRAME_WIDTH);
            int y = (int) (Math.random() * FRAME_HEIGHT);
            points.add(new Point(x, y));
        }
    }

    /*
     * This function only show the ouput result instead of showing the steps of point joining
     */
    // private void giftWrapping() {
    //     int n = points.size();

    //     // Find the leftmost point
    //     int leftmostIndex = 0;
    //     for (int i = 1; i < n; i++) {
    //         if (points.get(i).x < points.get(leftmostIndex).x) {
    //             leftmostIndex = i;
    //         }
    //     }

    //     int p = leftmostIndex;
    //     int q;

    //     convexHull = new ArrayList<>();
    //     do {
    //         convexHull.add(points.get(p));

    //         q = (p + 1) % n;
    //         for (int i = 0; i < n; i++) {
    //             // Check if point i is on the left side of the segment (p, q)
    //             if (orientation(points.get(p), points.get(i), points.get(q)) < 0) {
    //                 q = i;
    //             }
    //         }

    //         p = q;
    //     } while (p != leftmostIndex);
    // }

    //This function delay the process of joining the points so that it can be clearly visible
    private void giftWrapping() {
        int n = points.size();

        // Find the leftmost point
        int leftmostIndex = 0;
        for (int i = 1; i < n; i++) {
            if (points.get(i).x < points.get(leftmostIndex).x) {
                leftmostIndex = i;
            }
        }

        int p = leftmostIndex;
        int q;

        convexHull = new ArrayList<>();
        convexHull.add(points.get(p));

        // Draw the initial point
        Graphics g = getGraphics();
        g.setColor(Color.RED);
        int x1 = (int) (points.get(p).getX() * getWidth() / FRAME_WIDTH);
        int y1 = (int) (points.get(p).getY() * getHeight() / FRAME_HEIGHT);
        g.fillOval(x1 - POINT_RADIUS, y1 - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);

        try {
            do {
                // Draw the current point
                g.setColor(Color.BLACK);
                int x2 = (int) (points.get(p).getX() * getWidth() / FRAME_WIDTH);
                int y2 = (int) (points.get(p).getY() * getHeight() / FRAME_HEIGHT);
                g.fillOval(x2 - POINT_RADIUS, y2 - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);

                // Find the next point
                q = (p + 1) % n;
                for (int i = 0; i < n; i++) {
                    if (orientation(points.get(p), points.get(i), points.get(q)) < 0) {
                        q = i;
                    }
                }

                // Draw the line connecting the two points
                g.setColor(Color.RED);
                int x3 = (int) (points.get(q).getX() * getWidth() / FRAME_WIDTH);
                int y3 = (int) (points.get(q).getY() * getHeight() / FRAME_HEIGHT);
                g.drawLine(x2, y2, x3, y3);

                Thread.sleep(DELAY);

                p = q;
            } while (p != leftmostIndex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double orientation(Point p, Point q, Point r) {
        return (q.getY() - p.getY()) * (r.getX() - q.getX()) - (q.getX() - p.getX()) * (r.getY() - q.getY());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        double scaleX = (double) getWidth() / FRAME_WIDTH;
        double scaleY = (double) getHeight() / FRAME_HEIGHT;

        // Draw points
        for (Point point : points) {
            int x = (int) (point.getX() * scaleX);
            int y = (int) (point.getY() * scaleY);
            g.fillOval(x - POINT_RADIUS, y - POINT_RADIUS, 2 * POINT_RADIUS, 2 * POINT_RADIUS);
        }

        // Draw convex hull
        if (convexHull != null) {
            for (int i = 0; i < convexHull.size() - 1; i++) {
                Point current = convexHull.get(i);
                Point next = convexHull.get(i + 1);
                int x1 = (int) (current.getX() * scaleX);
                int y1 = (int) (current.getY() * scaleY);
                int x2 = (int) (next.getX() * scaleX);
                int y2 = (int) (next.getY() * scaleY);
                g.drawLine(x1, y1, x2, y2);
            }
            // Draw last line connecting last and first point
            Point last = convexHull.get(convexHull.size() - 1);
            Point first = convexHull.get(0);
            int x1 = (int) (last.getX() * scaleX);
            int y1 = (int) (last.getY() * scaleY);
            int x2 = (int) (first.getX() * scaleX);
            int y2 = (int) (first.getY() * scaleY);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) {
            generatePoints();
            currentIndex = 0;
        } else {
            if (currentIndex < points.size()) {
                giftWrapping();
                currentIndex++;
            }
        }
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gift Wrapping Algorithm Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        GiftWrappingVisualization panel = new GiftWrappingVisualization();
        panel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        frame.getContentPane().add(panel);

        frame.setVisible(true);
    }
}
