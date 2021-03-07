package connect4board;

import javafx.scene.paint.Color;

public enum Connect4Piece {

       RED(+1, Color.RED), YELLOW(-1, Color.YELLOW), EMPTY(0, Color.WHITE);

       private final int fieldValue;
       private final Color color;

       Connect4Piece(int fieldValue, Color color) {
           this.fieldValue = fieldValue;
           this.color = color;
       }

       public int getFieldValue() {
           return fieldValue;
       }

       public Color getColor() {
           return color;
       }

       static Connect4Piece ofFieldValue(int f) {
           switch (f) {
           case -1:
               return YELLOW;
           case +1:
               return RED;
           default:
               return EMPTY;
           }
       }

}
