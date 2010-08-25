package java5;

import java.awt.*;
import javax.swing.*;

/**
* Draws a <i>bullet</i> icon like the one shown on the picture below:
* <p><img src="doc-files/bullet.gif">
* @author Filigris Works
* @see javax.swing.Icon
* @prj:type demo
* @todo Support different bullet shapes
*/
@Author("Filigris Works")
@Reviews({
  @Review(grade=Review.Grade.EXCELLENT,
          reviewer="The Boss",
          comment="Well done. Great job, guys!")
})
public class BulletIcon implements Icon
{
  private Color myColor = null;

  public BulletIcon()
  {
  }

  /**
   * This allows to specify the color for the bullet.
   * If <code>null</code> (default), the foreground color 
   * of the component is used where the icon is drawn.
   */
  public void setColor (Color color)
  {
    myColor = color;
  }

  /**
   * The icon width.
   * @return 6 (pixels)
   */
  @Reviews({
    @Review(grade=Review.Grade.UNSATISFACTORY,
            reviewer="Tester A",
            comment="Too few code!")
  })
  public int getIconWidth()
  {
    return 6;
  }

  /**
   * The icon height.
   * @return 6 (pixels)
   */
  public int getIconHeight()
  {
    return 6;
  }

  /**
   * Here's where the actual drawing occurs. Producing a bullet shape
   * is very simple. Just two intersecting rectangles and that's all:
   * <pre>
   *     int w = getIconWidth();
   *     int h = getIconHeight();
   * 
   *     g.fillRect (x+1, y, w-2, h);
   *     g.fillRect (x, y+1, w, h-2);
   * </pre>
   */
  @Reviews({
    @Review(grade=Review.Grade.UNSATISFACTORY,
            reviewer="Tester A",
            comment="Too simple bullet shape!"),
    @Review(grade=Review.Grade.SATISFACTORY,
            reviewer="Tester B",
            comment="I tested. It works.")
  })
  public void paintIcon (Component c, Graphics g, int x, int y)
  {
    g.setColor (myColor != null ? myColor : c.getForeground());

    int w = getIconWidth();
    int h = getIconHeight();

    g.fillRect (x+1, y, w-2, h);
    g.fillRect (x, y+1, w, h-2);
  }
}
