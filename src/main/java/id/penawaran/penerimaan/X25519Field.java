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
public class X25519Field {

    public static final int SIZE = 10;
    private static final int M24 = 0x00FFFFFF;
    private static final int M25 = 0x01FFFFFF;
    private static final int M26 = 0x03FFFFFF;
    private static final int[] ROOT_NEG_ONE = new int[]{0x020EA0B0, 0x0386C9D2, 0x00478C4E, 0x0035697F, 0x005E8630,
        0x01FBD7A7, 0x0340264F, 0x01F0B2B4, 0x00027E0E, 0x00570649};

    public static void decode(byte[] x, int xOff, int[] z) {
        decode128(x, xOff, z, 0);
        decode128(x, xOff + 16, z, 5);
        z[9] &= M24;
    }

    private static void decode128(byte[] bs, int off, int[] z, int zOff) {
        int t0 = decode32(bs, off + 0);
        int t1 = decode32(bs, off + 4);
        int t2 = decode32(bs, off + 8);
        int t3 = decode32(bs, off + 12);
        z[zOff + 0] = t0 & M26;
        z[zOff + 1] = ((t1 << 6) | (t0 >>> 26)) & M26;
        z[zOff + 2] = ((t2 << 12) | (t1 >>> 20)) & M25;
        z[zOff + 3] = ((t3 << 19) | (t2 >>> 13)) & M26;
        z[zOff + 4] = t3 >>> 7;
    }

    private static int decode32(byte[] bs, int off) {
        int n = bs[off] & 0xFF;
        n |= (bs[++off] & 0xFF) << 8;
        n |= (bs[++off] & 0xFF) << 16;
        n |= bs[++off] << 24;
        return n;
    }

    public static int[] create() {
        return new int[SIZE];
    }

    public static void sqr(int[] x, int[] z) {
        int x0 = x[0];
        int x1 = x[1];
        int x2 = x[2];
        int x3 = x[3];
        int x4 = x[4];
        int u0 = x[5];
        int u1 = x[6];
        int u2 = x[7];
        int u3 = x[8];
        int u4 = x[9];
        int x1_2 = x1 * 2;
        int x2_2 = x2 * 2;
        int x3_2 = x3 * 2;
        int x4_2 = x4 * 2;
        long a0 = (long) x0 * x0;
        long a1 = (long) x0 * x1_2;
        long a2 = (long) x0 * x2_2
                + (long) x1 * x1;
        long a3 = (long) x1_2 * x2_2
                + (long) x0 * x3_2;
        long a4 = (long) x2 * x2_2
                + (long) x0 * x4_2
                + (long) x1 * x3_2;
        long a5 = (long) x1_2 * x4_2
                + (long) x2_2 * x3_2;
        long a6 = (long) x2_2 * x4_2
                + (long) x3 * x3;
        long a7 = (long) x3 * x4_2;
        long a8 = (long) x4 * x4_2;
        int u1_2 = u1 * 2;
        int u2_2 = u2 * 2;
        int u3_2 = u3 * 2;
        int u4_2 = u4 * 2;
        long b0 = (long) u0 * u0;
        long b1 = (long) u0 * u1_2;
        long b2 = (long) u0 * u2_2
                + (long) u1 * u1;
        long b3 = (long) u1_2 * u2_2
                + (long) u0 * u3_2;
        long b4 = (long) u2 * u2_2
                + (long) u0 * u4_2
                + (long) u1 * u3_2;
        long b5 = (long) u1_2 * u4_2
                + (long) u2_2 * u3_2;
        long b6 = (long) u2_2 * u4_2
                + (long) u3 * u3;
        long b7 = (long) u3 * u4_2;
        long b8 = (long) u4 * u4_2;
        a0 -= b5 * 38;
        a1 -= b6 * 38;
        a2 -= b7 * 38;
        a3 -= b8 * 38;
        a5 -= b0;
        a6 -= b1;
        a7 -= b2;
        a8 -= b3;
//        long a9 = -b4;
        x0 += u0;
        x1 += u1;
        x2 += u2;
        x3 += u3;
        x4 += u4;
        x1_2 = x1 * 2;
        x2_2 = x2 * 2;
        x3_2 = x3 * 2;
        x4_2 = x4 * 2;
        long c0 = (long) x0 * x0;
        long c1 = (long) x0 * x1_2;
        long c2 = (long) x0 * x2_2
                + (long) x1 * x1;
        long c3 = (long) x1_2 * x2_2
                + (long) x0 * x3_2;
        long c4 = (long) x2 * x2_2
                + (long) x0 * x4_2
                + (long) x1 * x3_2;
        long c5 = (long) x1_2 * x4_2
                + (long) x2_2 * x3_2;
        long c6 = (long) x2_2 * x4_2
                + (long) x3 * x3;
        long c7 = (long) x3 * x4_2;
        long c8 = (long) x4 * x4_2;
        int z8, z9;
        long t;
        t = a8 + (c3 - a3);
        z8 = (int) t & M26;
        t >>= 26;
//        t       += a9 + (c4 - a4);
        t += (c4 - a4) - b4;
//        z9       = (int)t & M24; t >>= 24;
//        t        = a0 + (t + ((c5 - a5) << 1)) * 19;
        z9 = (int) t & M25;
        t >>= 25;
        t = a0 + (t + c5 - a5) * 38;
        z[0] = (int) t & M26;
        t >>= 26;
        t += a1 + (c6 - a6) * 38;
        z[1] = (int) t & M26;
        t >>= 26;
        t += a2 + (c7 - a7) * 38;
        z[2] = (int) t & M25;
        t >>= 25;
        t += a3 + (c8 - a8) * 38;
        z[3] = (int) t & M26;
        t >>= 26;
//        t       += a4 - a9 * 38;
        t += a4 + b4 * 38;
        z[4] = (int) t & M25;
        t >>= 25;
        t += a5 + (c0 - a0);
        z[5] = (int) t & M26;
        t >>= 26;
        t += a6 + (c1 - a1);
        z[6] = (int) t & M26;
        t >>= 26;
        t += a7 + (c2 - a2);
        z[7] = (int) t & M25;
        t >>= 25;
        t += z8;
        z[8] = (int) t & M26;
        t >>= 26;
        z[9] = z9 + (int) t;
    }

    public static void mul(int[] x, int[] y, int[] z) {
        int x0 = x[0], y0 = y[0];
        int x1 = x[1], y1 = y[1];
        int x2 = x[2], y2 = y[2];
        int x3 = x[3], y3 = y[3];
        int x4 = x[4], y4 = y[4];
        int u0 = x[5], v0 = y[5];
        int u1 = x[6], v1 = y[6];
        int u2 = x[7], v2 = y[7];
        int u3 = x[8], v3 = y[8];
        int u4 = x[9], v4 = y[9];
        long a0 = (long) x0 * y0;
        long a1 = (long) x0 * y1
                + (long) x1 * y0;
        long a2 = (long) x0 * y2
                + (long) x1 * y1
                + (long) x2 * y0;
        long a3 = (long) x1 * y2
                + (long) x2 * y1;
        a3 <<= 1;
        a3 += (long) x0 * y3
                + (long) x3 * y0;
        long a4 = (long) x2 * y2;
        a4 <<= 1;
        a4 += (long) x0 * y4
                + (long) x1 * y3
                + (long) x3 * y1
                + (long) x4 * y0;
        long a5 = (long) x1 * y4
                + (long) x2 * y3
                + (long) x3 * y2
                + (long) x4 * y1;
        a5 <<= 1;
        long a6 = (long) x2 * y4
                + (long) x4 * y2;
        a6 <<= 1;
        a6 += (long) x3 * y3;
        long a7 = (long) x3 * y4
                + (long) x4 * y3;
        long a8 = (long) x4 * y4;
        a8 <<= 1;
        long b0 = (long) u0 * v0;
        long b1 = (long) u0 * v1
                + (long) u1 * v0;
        long b2 = (long) u0 * v2
                + (long) u1 * v1
                + (long) u2 * v0;
        long b3 = (long) u1 * v2
                + (long) u2 * v1;
        b3 <<= 1;
        b3 += (long) u0 * v3
                + (long) u3 * v0;
        long b4 = (long) u2 * v2;
        b4 <<= 1;
        b4 += (long) u0 * v4
                + (long) u1 * v3
                + (long) u3 * v1
                + (long) u4 * v0;
        long b5 = (long) u1 * v4
                + (long) u2 * v3
                + (long) u3 * v2
                + (long) u4 * v1;
//        b5     <<= 1;
        long b6 = (long) u2 * v4
                + (long) u4 * v2;
        b6 <<= 1;
        b6 += (long) u3 * v3;
        long b7 = (long) u3 * v4
                + (long) u4 * v3;
        long b8 = (long) u4 * v4;
//        b8     <<= 1;
        a0 -= b5 * 76;
        a1 -= b6 * 38;
        a2 -= b7 * 38;
        a3 -= b8 * 76;
        a5 -= b0;
        a6 -= b1;
        a7 -= b2;
        a8 -= b3;
//        long a9 = -b4;
        x0 += u0;
        y0 += v0;
        x1 += u1;
        y1 += v1;
        x2 += u2;
        y2 += v2;
        x3 += u3;
        y3 += v3;
        x4 += u4;
        y4 += v4;
        long c0 = (long) x0 * y0;
        long c1 = (long) x0 * y1
                + (long) x1 * y0;
        long c2 = (long) x0 * y2
                + (long) x1 * y1
                + (long) x2 * y0;
        long c3 = (long) x1 * y2
                + (long) x2 * y1;
        c3 <<= 1;
        c3 += (long) x0 * y3
                + (long) x3 * y0;
        long c4 = (long) x2 * y2;
        c4 <<= 1;
        c4 += (long) x0 * y4
                + (long) x1 * y3
                + (long) x3 * y1
                + (long) x4 * y0;
        long c5 = (long) x1 * y4
                + (long) x2 * y3
                + (long) x3 * y2
                + (long) x4 * y1;
        c5 <<= 1;
        long c6 = (long) x2 * y4
                + (long) x4 * y2;
        c6 <<= 1;
        c6 += (long) x3 * y3;
        long c7 = (long) x3 * y4
                + (long) x4 * y3;
        long c8 = (long) x4 * y4;
        c8 <<= 1;
        int z8, z9;
        long t;
        t = a8 + (c3 - a3);
        z8 = (int) t & M26;
        t >>= 26;
//        t       += a9 + (c4 - a4);
        t += (c4 - a4) - b4;
//        z9       = (int)t & M24; t >>= 24;
//        t        = a0 + (t + ((c5 - a5) << 1)) * 19;
        z9 = (int) t & M25;
        t >>= 25;
        t = a0 + (t + c5 - a5) * 38;
        z[0] = (int) t & M26;
        t >>= 26;
        t += a1 + (c6 - a6) * 38;
        z[1] = (int) t & M26;
        t >>= 26;
        t += a2 + (c7 - a7) * 38;
        z[2] = (int) t & M25;
        t >>= 25;
        t += a3 + (c8 - a8) * 38;
        z[3] = (int) t & M26;
        t >>= 26;
//        t       += a4 - a9 * 38;
        t += a4 + b4 * 38;
        z[4] = (int) t & M25;
        t >>= 25;
        t += a5 + (c0 - a0);
        z[5] = (int) t & M26;
        t >>= 26;
        t += a6 + (c1 - a1);
        z[6] = (int) t & M26;
        t >>= 26;
        t += a7 + (c2 - a2);
        z[7] = (int) t & M25;
        t >>= 25;
        t += z8;
        z[8] = (int) t & M26;
        t >>= 26;
        z[9] = z9 + (int) t;
    }

    public static void subOne(int[] z) {
        z[0] -= 1;
    }

    public static void addOne(int[] z) {
        z[0] += 1;
    }

    public static void sqr(int[] x, int n, int[] z) {
//        assert n > 0;
        sqr(x, z);
        while (--n > 0) {
            sqr(z, z);
        }
    }

    private static void powPm5d8(int[] x, int[] rx2, int[] rz) {
        // z = x^((p-5)/8) = x^FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFD
        // (250 1s) (1 0s) (1 1s)
        // Addition chain: [1] 2 3 5 10 15 25 50 75 125 [250]
        int[] x2 = rx2;
        sqr(x, x2);
        mul(x, x2, x2);
        int[] x3 = create();
        sqr(x2, x3);
        mul(x, x3, x3);
        int[] x5 = x3;
        sqr(x3, 2, x5);
        mul(x2, x5, x5);
        int[] x10 = create();
        sqr(x5, 5, x10);
        mul(x5, x10, x10);
        int[] x15 = create();
        sqr(x10, 5, x15);
        mul(x5, x15, x15);
        int[] x25 = x5;
        sqr(x15, 10, x25);
        mul(x10, x25, x25);
        int[] x50 = x10;
        sqr(x25, 25, x50);
        mul(x25, x50, x50);
        int[] x75 = x15;
        sqr(x50, 25, x75);
        mul(x25, x75, x75);
        int[] x125 = x25;
        sqr(x75, 50, x125);
        mul(x50, x125, x125);
        int[] x250 = x50;
        sqr(x125, 125, x250);
        mul(x125, x250, x250);
        int[] t = x125;
        sqr(x250, 2, t);
        mul(t, x, rz);
    }

    public static void sub(int[] x, int[] y, int[] z) {
        for (int i = 0; i < SIZE; ++i) {
            z[i] = x[i] - y[i];
        }
    }

    private static void reduce(int[] z, int x) {
        int t = z[9], z9 = t & M24;
        t = (t >> 24) + x;
        long cc = t * 19;
        cc += z[0];
        z[0] = (int) cc & M26;
        cc >>= 26;
        cc += z[1];
        z[1] = (int) cc & M26;
        cc >>= 26;
        cc += z[2];
        z[2] = (int) cc & M25;
        cc >>= 25;
        cc += z[3];
        z[3] = (int) cc & M26;
        cc >>= 26;
        cc += z[4];
        z[4] = (int) cc & M25;
        cc >>= 25;
        cc += z[5];
        z[5] = (int) cc & M26;
        cc >>= 26;
        cc += z[6];
        z[6] = (int) cc & M26;
        cc >>= 26;
        cc += z[7];
        z[7] = (int) cc & M25;
        cc >>= 25;
        cc += z[8];
        z[8] = (int) cc & M26;
        cc >>= 26;
        z[9] = z9 + (int) cc;
    }

    public static void normalize(int[] z) {
        int x = ((z[9] >>> 23) & 1);
        reduce(z, x);
        reduce(z, -x);
//        assert z[9] >>> 24 == 0;
    }

    public static int isZero(int[] x) {
        int d = 0;
        for (int i = 0; i < SIZE; ++i) {
            d |= x[i];
        }
        d = (d >>> 1) | (d & 1);
        return (d - 1) >> 31;
    }

    public static boolean isZeroVar(int[] x) {
        return 0 != isZero(x);
    }

    public static void copy(int[] x, int xOff, int[] z, int zOff) {
        for (int i = 0; i < SIZE; ++i) {
            z[zOff + i] = x[xOff + i];
        }
    }

    public static void add(int[] x, int[] y, int[] z) {
        for (int i = 0; i < SIZE; ++i) {
            z[i] = x[i] + y[i];
        }
    }

    public static boolean sqrtRatioVar(int[] u, int[] v, int[] z) {
        int[] uv3 = create();
        int[] uv7 = create();
        mul(u, v, uv3);
        sqr(v, uv7);
        mul(uv3, uv7, uv3);
        sqr(uv7, uv7);
        mul(uv7, uv3, uv7);
        int[] t = create();
        int[] x = create();
        powPm5d8(uv7, t, x);
        mul(x, uv3, x);
        int[] vx2 = create();
        sqr(x, vx2);
        mul(vx2, v, vx2);
        sub(vx2, u, t);
        normalize(t);
        if (isZeroVar(t)) {
            copy(x, 0, z, 0);
            return true;
        }
        add(vx2, u, t);
        normalize(t);
        if (isZeroVar(t)) {
            mul(x, ROOT_NEG_ONE, z);
            return true;
        }
        return false;
    }

    public static void negate(int[] x, int[] z) {
        for (int i = 0; i < SIZE; ++i) {
            z[i] = -x[i];
        }
    }

}
