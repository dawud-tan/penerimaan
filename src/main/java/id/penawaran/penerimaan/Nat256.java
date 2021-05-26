/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.penawaran.penerimaan;

/**
 *
 * @author dawud_tan
 */
public class Nat256 {

    public static boolean gte(int[] x, int[] y) {
        for (int i = 7; i >= 0; --i) {
            int x_i = x[i] ^ Integer.MIN_VALUE;
            int y_i = y[i] ^ Integer.MIN_VALUE;
            if (x_i < y_i) {
                return false;
            }
            if (x_i > y_i) {
                return true;
            }
        }
        return true;
    }
}
