//package com.poohcom1.spritesheetparser.util.cv;
//
//import com.poohcom1.spritesheetparser.util.Shapes2D.Rect;
//
//public class OrderedBlob extends Blob implements Comparable<OrderedBlob> {
//    private int row = 0;
//    private int column = 0;
//
//
//    public void setRowColumn(int row, int column) {
//        this.row = row; this.column = column;
//    }
//
//    public int getRow() {
//        return row;
//    }
//
//    public int getColumn() {
//        return column;
//    }
//
//
//    @Override
//    public int compareTo(OrderedBlob o) {
//        return compareTo(o, BlobUtil.LEFT_TO_RIGHT, BlobUtil.TOP_TO_BOTTOM);
//    }
//
//    /*
//        LEFT_TO_RIGHT = 0;
//        TOP_TO_BOTTOM = 1;
//        RIGHT_TO_LEFT = 2;
//        BOTTOM_TO_TOP = 3;
//     */
//    public int compareTo(OrderedBlob other, int primaryOrder, int secondaryOrder) {
//        if (primaryOrder == secondaryOrder) throw new IllegalArgumentException("Primary and secondary order cannot be the same.");
//        if (primaryOrder > 3 || secondaryOrder > 3) throw new IllegalArgumentException("Illegal blob order constant.");
//
//        final int[] origins = {this.x, this.y, other.x, other.y};
//
//        // Primary/Secondary here refers to the axis that takes precedence in ordering
//        int aPrimaryOrigin = origins[primaryOrder];
//        int bPrimaryOrigin = origins[(primaryOrder + 2) % 4];
//        int aSecondaryOrigin = origins[secondaryOrder];
//        int bSecondaryOrigin = origins[(secondaryOrder + 2) % 4];
//
//        if (overlapsOrder(other, primaryOrder)) {
//            return aPrimaryOrigin - bPrimaryOrigin;
//        } else {
//            return aSecondaryOrigin - bSecondaryOrigin;
//        }
//    }
//
//    // Check if object
//    public boolean overlapsOrder(Blob other, int primaryOrder) {
//        final int[] axes = {Rect.VERTICAL_AXIS, Rect.HORIZONTAL_AXIS};
//        int subAxis = axes[(primaryOrder) % 2];
//
//        return overlapsDirection(other, subAxis);
//    }
//}