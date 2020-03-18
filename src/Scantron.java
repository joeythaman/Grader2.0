/**
 *
 */
//package Grader;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

import java.util.*;

import javax.imageio.ImageIO;

/**
 * @author lgathard
 *
 */

public class Scantron {

    public Scantron() {

    }

    static double percent = 0.1;

    static int n = 0;
    static int n2 = 0;
    static BufferedImage newImg;
    static HashSet<Point> border = new HashSet<Point>();
    static Point left1;
    static Point left2;

    static Point right1;
    static Point right2;

    static Point top1;
    static Point top2;

    static Point bot1;
    static Point bot2;

    static int xScan1;
    static int xScan2;
    static int yScan1;
    static int yScan2;

    static int width;
    static int height;

    public static Point2D linearize (Point p1, Point p2, char HorV) {
        if (HorV == 'h') {
            double xDif = p2.x - p1.x;
            double yDif = p2.y - p1.y;
            double slope = yDif/xDif;
            double yCof = p1.y-slope*p1.x;
            return new Point2D.Double(slope,yCof);
        } else {
            double xDif = p2.x - p1.x;
            double yDif = p2.y - p1.y;
            double slope = xDif/yDif;
            double xCof = p1.x-slope*p1.y;
            return new Point2D.Double(slope,xCof);
        }
    }

    public static int linear(Point2D m_and_b, int intercept) {
        return (int)(m_and_b.getX()*intercept+m_and_b.getY());
    }

    public static void drawLine(BufferedImage newImg, Point2D line, char HorV) {
        if (HorV == 'v') {
            for (int i = 0; i < height; i++) {
                int X = linear(line,i);
                if (X > 0 && X < width) {
                    newImg.setRGB(X, i, 11111111);
                }
            }
        } else {
            for (int i = 0; i < width; i++) {
                int Y = linear(line,i);
                if (Y > 0 && Y < height) {
                    newImg.setRGB(i, Y, 11111111);
                }
            }
        }
    }

    public static void drawSquare(BufferedImage newImg, Point pixel, int radius, int color) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                newImg.setRGB(pixel.x+i, pixel.y+j, color);
            }
        }
    }

    public static Point percentLine(Point p1, Point p2, double percent) {
        return new Point((int)((p2.x-p1.x)*percent+p1.x), (int)((p2.y-p1.y)*percent+p1.y));
    }

    public static Point2D fractionalize(Point p1, Point p2, Point p3) {
        return new Point2D.Double(((double)(p3.x-p1.x))/((double)(p2.x-p1.x)), ((double)(p3.y-p1.y))/((double)(p2.y-p1.y)));
    }

    public static Point intersect(Point2D y, Point2D x) {
        double m1 = y.getX();
        double b1 = y.getY();
        double m2 = x.getX();
        double b2 = x.getY();

        int yPoint = (int)((m1*b2+b1)/(1-m1*m2));
        int xPoint = linear(x,yPoint);

        return new Point(xPoint,yPoint);
    }

    public static Point[] intersections(Point2D y1, Point2D y2, Point2D x1, Point2D x2) {
        Point[] intersectPoints = new Point[4];
        intersectPoints[0] = intersect(y1,x1);
        intersectPoints[1] = intersect(y1,x2);
        intersectPoints[2] = intersect(y2,x1);
        intersectPoints[3] = intersect(y2,x2);
        return intersectPoints;
    }



    public static HashSet<Point> border(int x, int y) {
        LinkedList<Point> queue = new LinkedList<Point>();
        int p = newImg.getRGB(x, y);
        int a = (p>>24) & 0xff;
        int r = (p>>16) & 0xff;
        int g = (p>>8) & 0xff;
        int b = p & 0xff;
        if (!border.contains(new Point(x,y)) && (r > 50 | g > 50 | b > 50) && (x) >= 0 && (x) < width && (y) >= 0 && (y) < height) {
            queue.add(new Point(x,y));
            border.add(new Point(x,y));
        }
        while (!queue.isEmpty()) {
            Point l = queue.poll();
            x = l.x;
            y = l.y;


            if (y == xScan1) {
                if (x > width/2) {
                    if (right1 == null || x < right1.getX()) {
                        right1 = new Point(x,y);
                    }
                } else {
                    if (left1 == null || x > left1.getX()) {
                        left1 = new Point(x,y);
                    }
                }
            }

            if (y == xScan2) {
                if (x > width/2) {
                    if (right2 == null || x < right2.getX()) {
                        right2 = new Point(x,y);
                    }
                } else {
                    if (left2 == null || x > left2.getX()) {
                        left2 = new Point(x,y);
                    }
                }
            }

            if (x == yScan1) {
                if (y > height/2) {
                    if (bot1 == null || y < bot1.getY()) {
                        bot1 = new Point(x,y);
                    }
                } else {
                    if (top1 == null || y > top1.getX()) {
                        top1 = new Point(x,y);
                    }
                }
            }

            if (x == yScan2) {
                if (y > height/2) {
                    if (bot2 == null || y < bot2.getY()) {
                        bot2 = new Point(x,y);
                    }
                } else {
                    if (top2 == null || y > top2.getX()) {
                        top2 = new Point(x,y);
                    }
                }
            }

            newImg.setRGB(x, y, 16711680);
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    p = newImg.getRGB(x+i, y+j);
                    a = (p>>24) & 0xff;
                    r = (p>>16) & 0xff;
                    g = (p>>8) & 0xff;
                    b = p & 0xff;
                    if (!border.contains(new Point(x+i,y+j)) && !(i == 0 && j == 0)  && (r > 50 | g > 50 | b > 50) && (x+i) >= 0 && (x+i) < width && (y+j) >= 0 && (y+j) < height) {
                        queue.add(new Point(x+i,y+j));
                        border.add(new Point(x+i,y+j));
                    }
                }
            }
            //}
        }
        return border;
    }

    public static int choiceExamine(BufferedImage img, BufferedImage newImg, int x, Point2D answersLine) {
        int count = 0;
        for (int i = -40; i <= 40; i++) {
            for (int j = -40; j <= 40; j++) {
                int newX = i + x;
                int newY = linear(answersLine, x + i) + j;

                if ((newX) >= 0 && (newX) < width && (newY) >= 0 && (newY) < height) {
                    int pp = img.getRGB(newX, newY);
                    int aa = (pp >> 24) & 0xff;
                    int rr = (pp >> 16) & 0xff;
                    int gg = (pp >> 8) & 0xff;
                    int bb = pp & 0xff;

                    if (gg + rr + bb < 650) {
                        count++;
                        newImg.setRGB(newX, newY, 0xffa5ff);
                    } else {
                        newImg.setRGB(newX, newY, 0x00008b);
                    }
                }
            }
        }

        return count;
    }

    private static void border2(int x, int y, HashSet<Point> co) {
        int p = newImg.getRGB(x, y);
        int a = (p>>24) & 0xff;
        int r = (p>>16) & 0xff;
        int g = (p>>8) & 0xff;
        int b = p & 0xff;
        n2 += 1;
        if ((r > 50 | g > 50 | b > 50) && x >= 0 && x < width && y >= 0 && y < height) {
            co.add(new Point(x,y));
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    if (!co.contains(new Point(x+i,y+j)) && !(i == 0 && j == 0)) {
                        try {
                            //border(x+i,y+j,co);
                        } catch (java.lang.StackOverflowError e) {

                        }
                    }
                }
            }
            n += 1;
            newImg.setRGB(x, y, 16711680);
        }
    }

    public static void scan(String imageName) {


        BufferedImage img = null;
        System.out.println(imageName);
        try {
            img = ImageIO.read(new File(imageName));
            System.out.println("good");
        } catch (IOException e) {
            System.out.println("bad");
            System.out.println(imageName);
        }

        width = img.getWidth();
        height = img.getHeight();

        xScan1 = (int) (height*0.4);
        xScan2 = (int) (height*0.6);

        yScan1 = (int) (width*0.4);
        yScan2 = (int) (width*0.6);

        newImg = new BufferedImage(img.getWidth(),height,BufferedImage.TYPE_INT_RGB);
        int[][][] array = new int[height][width][3];
        System.out.println("height: "+height);
        System.out.println("width: "+width);
        for (int i = 0;i < height;i++) {
            for (int j = 0;j < width;j++) {
                int p = img.getRGB(j,i);

                int a = (p>>24) & 0xff;
                int r = (p>>16) & 0xff;
                int g = (p>>8) & 0xff;
                int b = p & 0xff;

                array[i][j] = new int[] {r,g,b};
            }
        }

        for (int i = 0;i < height;i++) {
            for (int j = 0;j < width;j++) {
                int count = 0;
                int[] newColors = new int[] {0,0,0};

                for (int m = -1;m < 2;m++) {
                    for (int n = -1;n < 2;n++) {
                        if (j+m >= 0 && j+m < width && i+n >= 0 && i+n < height && !(n == 0 && m == 0)) {
                            count += 1;
                            newColors[0] -= array[i+n][j+m][0];
                            newColors[1] -= array[i+n][j+m][1];
                            newColors[2] -= array[i+n][j+m][2];
                        }
                    }
                }

                newColors[0] += count*array[i][j][0];
                newColors[1] += count*array[i][j][1];
                newColors[2] += count*array[i][j][2];

                newColors[0] = Math.min(255,Math.max(0,newColors[0]));
                newColors[1] = Math.min(255,Math.max(0,newColors[1]));
                newColors[2] = Math.min(255,Math.max(0,newColors[2]));

                int p = 0;
                if (false && newColors[0] > 100 | newColors[1] > 100 | newColors[2] > 100) {
                    p = 255<<16;
                } else {
                    p = (0<<24) | (newColors[0]<<16) | (newColors[1]<<8) | newColors[2];
                }

                newImg.setRGB(j, i, p);
            }
        }

        HashSet<Point> cod = new HashSet<Point>();
        for (int i = 140;i <= 180; i++) {
            border(1000,i);
        }
        for (int i = 245;i <= 285; i++) {
            border(1000,height-i);
        }


        Point2D left = linearize(left1,left2,'v');

        Point2D right = linearize(right1,right2,'v');

        Point2D top = linearize(top1,top2,'h');

        Point2D bot = linearize(bot1,bot2,'h');

        System.out.println(top);

        System.out.println("left: "+left);
        System.out.println("right: "+right);
        System.out.println("top: "+top);
        System.out.println("bot: "+bot);

        Point[] allIntersections = intersections(top,bot,left,right);

        System.out.println("\nINTERSECTIONS:");
        Point secondQuad = allIntersections[0];
        Point firstQuad = allIntersections[1];
        Point thirdQuad = allIntersections[2];
        Point fourthQuad = allIntersections[3];

        drawLine(newImg,left,'v');
        drawLine(newImg,right,'v');
        drawLine(newImg,top,'h');
        drawLine(newImg,bot,'h');

        drawSquare(newImg,allIntersections[0],5,16776960);
        drawSquare(newImg,allIntersections[1],5,16776960);
        drawSquare(newImg,allIntersections[2],5,16776960);
        drawSquare(newImg,allIntersections[3],5,16776960);

        drawSquare(newImg,percentLine(secondQuad,firstQuad,percent),5,0xFF33D4);
        drawSquare(newImg,percentLine(thirdQuad,fourthQuad,percent),5,0x33FFF6);

        System.out.println("weird line: "+linearize(percentLine(secondQuad,firstQuad,percent),percentLine(thirdQuad,fourthQuad,percent),'v'));

        Point scanlinePoint1 = percentLine(secondQuad,firstQuad,percent);
        Point scanlinePoint2 = percentLine(thirdQuad,fourthQuad,percent);
        Point2D scanLine = linearize(scanlinePoint1,scanlinePoint2,'v');

        int lastUsed = scanlinePoint2.y;
        int next = 0;

        int question = 25;

        ArrayList<ArrayList<Character>> answers = new ArrayList<ArrayList<Character>>();;

        for (int j = scanlinePoint2.y; j > scanlinePoint1.y; j--) {
            int X = linear(scanLine,j);
            if (X > 0 && X < width) {
                int p = newImg.getRGB(X, j);
                int a = (p>>24) & 0xfff;
                int r = (p>>16) & 0xff;
                int g = (p>>8) & 0xff;
                int b = p & 0xff;
                if (r+g+b > 100) {
                    if ((lastUsed - j) > 50) {
                        if (next == 0) {
                            next = j;
                            drawSquare(newImg, new Point(X, j + 10), 10, 0xFF0000);
                        } else {
                            int i = j;
                            if (next > 0) {
                                if (next - j < 20) {
                                    continue;
                                }
                                i = (next + j) / 2;
                                next = -1;
                            }

                            System.out.println("Question: "+question);
                            System.out.println("Position: "+i);

                            drawSquare(newImg, new Point(X, i + 10), 10, 0x0000FF);
                            Point2D fract = fractionalize(scanlinePoint1, scanlinePoint2, new Point(X, i));
                            double fractY = fract.getY() + 0.002;

                            Point leftPoint = percentLine(secondQuad, thirdQuad, fractY);
                            Point rightPoint = percentLine(firstQuad, fourthQuad, fractY);

                            Point2D answersLine = linearize(leftPoint, rightPoint, 'h');

                            Point[] FivePoints = new Point[5];

                            ArrayList<Character> currAnswers = new ArrayList<Character>();

                            for (int answerNum = 0; answerNum < 5; answerNum++)
                            {
                                double pos = Arrays.asList(0.225, 0.303, 0.38, 0.458, 0.535).get(answerNum);

                                Point answer = percentLine(leftPoint, rightPoint, pos);

                                int count = choiceExamine(img, newImg, answer.x, answersLine);

                                if (count > 1700) {
                                    currAnswers.add(Arrays.asList('A', 'B', 'C', 'D', 'E').get(answerNum));
                                    drawSquare(newImg, new Point(answer.x, linear(answersLine,answer.x)), 10, 0x00FF00);
                                }

                                System.out.println("pos: " + pos + ", count: " + count);
                            }

                            answers.add(currAnswers);

                            System.out.println("");

                            lastUsed = i;

                            question--;

                            if (question == 0) {
                                break;
                            }
                        }

                    } else {
                        newImg.setRGB(X,j,0xFF0000);
                    }
                } else {
                    newImg.setRGB(X, j, 0x00FF00);
                }
            }
        }

        try {
            ImageIO.write(newImg, "bmp", new File("new_"+imageName));
            System.out.println("good");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("bad");
        }

        System.out.println(answers);
    }

}
