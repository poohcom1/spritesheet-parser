import com.poohcom1.spritesheetparser.shapes2D.Rect;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class RectTest {
    @Test
    public void Should_Intersect() {
        Rect rect1 = new Rect(186, 78, 189, 80);
        Rect rect2 = new Rect(158, 79, 218, 136);

        Assert.assertTrue(rect1.intersects(rect2));
    }

    @Test
    public void Should_Touch() {
        Rect rect1 = new Rect(0, 0, 1, 1);
        Rect rect2 = new Rect(0, 1, 50, 50);

        Assert.assertTrue(rect1.intersectTouch(rect2));
    }
}
