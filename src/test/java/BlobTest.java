import com.poohcom1.spritesheetparser.cv.Blob;
import com.poohcom1.spritesheetparser.cv.BlobSequence;
import com.poohcom1.spritesheetparser.shapes2D.Rect;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlobTest {
    @Test
    public void Should_Add_With_Correct_Dimensions() {
        Blob blob1 = new Blob(0, 0, 15, 15);
        Point point = new Point(17, 7);

        blob1.add(point);
        Blob expected = new Blob(0, 0, 18, 15);

        Assert.assertEquals(blob1, expected);
    }

    @Test
    public void Should_Merge_With_Correct_Dimensions() {
        Blob blob1 = new Blob(0, 0);
        Blob blob2 = new Blob(5, 5);

        Blob merged = new Blob(blob1, blob2);
        Blob expected = new Blob(0, 0, 6, 6);

        Assert.assertEquals(merged, expected);
    }

    @Test
    public void Should_Auto_Merge_Intersecting_Blobs() {
        List<Blob> blobList = new ArrayList<>();
        blobList.add(new Blob(186, 78, 189, 80));
        blobList.add(new Blob(158, 79, 218, 136));

        BlobSequence.autoMergeBlobs(blobList);

        Assert.assertEquals(blobList.size(), 1);
    }

    @Test
    public void Should_Auto_Merge_Touching_Blobs() {
        List<Blob> blobList = new ArrayList<>();
        blobList.add(new Blob(0, 0, 1, 1));
        blobList.add(new Blob(0, 1, 50, 50));

        BlobSequence.autoMergeBlobs(blobList);


        Assert.assertEquals(blobList.size(), 1);
    }

    @Test
    public void Should_Not_Auto_Merge_Distinct_Blobs() {
        List<Blob> blobList = new ArrayList<>();
        blobList.add(new Blob(0, 0, 1, 1));
        blobList.add(new Blob(5, 5, 50, 50));

        BlobSequence.autoMergeBlobs(blobList);

        Assert.assertEquals(blobList.size(), 2);
    }

    @Test
    public void Should_Auto_Merge_Multiple_Touching_Blobs() {
        Blob b1 = new Blob(5, 0, 6, 1);
    }
}
