package nz;

import javafx.scene.text.Text;

/** The Objectives class manages objectives in the game. */
public class Objectives {
  private static Text[] objArr = new Text[4];
  private static boolean[] objBol = new boolean[4];

  /**
   * Sets the specified objective's text.
   *
   * @param newObj1 The Text object representing the new objective.
   * @param num The index of the objective to set (0 to 3).
   */
  public static void setObj(Text newObj1, int num) {
    objArr[num] = newObj1;
  }

  /**
   * Marks the specified objective as completed and updates the display.
   *
   * @param num The index of the completed objective (0 to 3).
   */

  // This method is called when the player completes an objective.

  public static void updateObj(int num) {
    objBol[num] = true;
    // The objective is crossed out when it is completed.
    objArr[num].setStrikethrough(true);
    if (objBol[0] && objBol[1] && objBol[2] && objBol[3]) {
      objArr[1].setStrikethrough(false);
      objArr[0].setX(7);
      // The player is told to exit the room when all objectives are completed.
      objArr[1].setText("Objectives complete exit the room!");
      objArr[0].setText("");
      objArr[2].setText("");
      objArr[3].setText("");
    }
  }
}
