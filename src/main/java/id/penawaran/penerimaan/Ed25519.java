/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.penawaran.penerimaan;

import org.bouncycastle.util.Arrays;

/**
 *
 * @author dawud_tan
 */
public class Ed25519 {

    private static final int POINT_BYTES = 32;
    public static final int PUBLIC_KEY_SIZE = POINT_BYTES;
    private static final int[] C_d = new int[]{0x035978A3, 0x02D37284, 0x018AB75E, 0x026A0A0E, 0x0000E014, 0x0379E898,
        0x01D01E5D, 0x01E738CC, 0x03715B7F, 0x00A406D9};
    private static final int[] P = new int[]{0xFFFFFFED, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0x7FFFFFFF};

    public static class PointAffine {

        public int[] x = X25519Field.create();
        public int[] y = X25519Field.create();
    }

    private static int decode32(byte[] bs, int off) {
        int n = bs[off] & 0xFF;
        n |= (bs[++off] & 0xFF) << 8;
        n |= (bs[++off] & 0xFF) << 16;
        n |= bs[++off] << 24;
        return n;
    }

    private static void decode32(byte[] bs, int bsOff, int[] n, int nOff, int nLen) {
        for (int i = 0; i < nLen; ++i) {
            n[nOff + i] = decode32(bs, bsOff + i * 4);
        }
    }

    private static boolean checkPointVar(byte[] p) {
        int[] t = new int[8];
        decode32(p, 0, t, 0, 8);
        t[7] &= 0x7FFFFFFF;
        return !Nat256.gte(t, P);
    }

    public static boolean decodePointVar(byte[] p, int pOff, boolean negate, PointAffine r) {
        byte[] py = Arrays.copyOfRange(p, pOff, pOff + POINT_BYTES);
        if (!checkPointVar(py)) {
            return false;
        }
        int x_0 = (py[POINT_BYTES - 1] & 0x80) >>> 7;
        py[POINT_BYTES - 1] &= 0x7F;
        X25519Field.decode(py, 0, r.y);
        int[] u = X25519Field.create();
        int[] v = X25519Field.create();
        X25519Field.sqr(r.y, u);
        X25519Field.mul(C_d, u, v);
        X25519Field.subOne(u);
        X25519Field.addOne(v);
        if (!X25519Field.sqrtRatioVar(u, v, r.x)) {
            return false;
        }
        X25519Field.normalize(r.x);
        if (x_0 == 1 && X25519Field.isZeroVar(r.x)) {
            return false;
        }
        if (negate ^ (x_0 != (r.x[0] & 1))) {
            X25519Field.negate(r.x, r.x);
        }
        return true;
    }
}
