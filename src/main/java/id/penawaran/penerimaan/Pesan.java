/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.penawaran.penerimaan;

import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author dawud_tan
 */
public class Pesan {

    public String penawaran;
    public BigInteger sisaBagi;
    public Date tanggal;
    public String hashnya;
    public String alamatIP;

    public String getPenawaran() {
        return penawaran;
    }

    public BigInteger getSisaBagi() {
        return sisaBagi;
    }
    
    public Short getJumlahDigit(){
		double faktor = Math.log(2) / Math.log(10);
		short jumlahDigit = (short) (faktor * sisaBagi.bitLength() + 1);
		if (BigInteger.TEN.pow(jumlahDigit - 1).compareTo(sisaBagi) > 0) {
			return (short) (jumlahDigit - 1);
		}
		return jumlahDigit;
	}

    public Date getTanggal() {
        return tanggal;
    }

    public String getHashnya() {
        return hashnya;
    }

    public String getAlamatIP() {
        return alamatIP;
    }
}